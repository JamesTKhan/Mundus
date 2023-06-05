#include "compat.glsl"
#include "light.glsl"

// Just stops the static analysis from complaining
#ifndef MED
#define MED
#endif

varying MED vec2 v_texCoord0;
varying vec2 v_waterTexCoords;
varying vec4 v_clipSpace;
varying vec3 v_toCameraVector;
varying vec2 v_diffuseUV;

#ifdef reflectionFlag
uniform sampler2D u_reflectionTexture;
#endif

#ifdef refractionFlag
uniform sampler2D u_refractionTexture;
uniform float u_maxVisibleDepth;
#endif

uniform sampler2D u_refractionDepthTexture;
uniform sampler2D u_dudvTexture;
uniform sampler2D u_normalMapTexture;
uniform sampler2D u_foamTexture;
uniform vec4 u_color;
uniform MED float u_waveStrength;
uniform MED float u_moveFactor;
uniform MED float u_shineDamper;
uniform MED float u_reflectivity;
uniform MED float u_foamScale;
uniform MED float u_foamEdgeBias;
uniform MED float u_foamEdgeDistance;
uniform MED float u_foamFallOffDistance;
uniform MED float u_foamScrollSpeed;
uniform vec2 u_cameraNearFar;

#ifdef fogFlag
uniform vec3 u_fogEquation;
uniform MED vec4 u_fogColor;
#endif

// From gdx-gltf library https://github.com/mgsx-dev/gdx-gltf
vec4 SRGBtoLINEAR(vec4 srgbIn)
{
    #ifdef MANUAL_SRGB
    #ifdef SRGB_FAST_APPROXIMATION
    vec3 linOut = pow(srgbIn.xyz,vec3(2.2));
    #else //SRGB_FAST_APPROXIMATION
    vec3 bLess = step(vec3(0.04045),srgbIn.xyz);
    vec3 linOut = mix( srgbIn.xyz/vec3(12.92), pow((srgbIn.xyz+vec3(0.055))/vec3(1.055),vec3(2.4)), bLess );
    #endif //SRGB_FAST_APPROXIMATION
    return vec4(linOut,srgbIn.w);;
    #else //MANUAL_SRGB
    return srgbIn;
    #endif //MANUAL_SRGB
}

//https://aras-p.info/blog/2009/07/30/encoding-floats-to-rgba-the-final/
float DecodeFloatRGBA( vec4 rgba ) {
    return dot( rgba, vec4(1.0, 1.0/255.0, 1.0/65025.0, 1.0/16581375.0) );
}

vec3 calcSpecularHighlights(BaseLight baseLight, vec3 direction, vec3 normal, vec3 viewVector, float waterDepth) {
    vec3 reflectedLight = reflect(normalize(direction), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, u_shineDamper);
    vec3 specularHighlights = vec3(baseLight.Color) * baseLight.DiffuseIntensity * specular * u_reflectivity * clamp(waterDepth/5.0, 0.0, 1.0);

    return specularHighlights;
}

float normalizeRange(float value, float minValue, float maxValue) {
    float weight = max(minValue, value);
    weight = min(maxValue, weight);
    weight -= minValue;
    weight /= maxValue - minValue; // Normalizes to 0.0-1.0 range
    return weight;
}

void main() {

    // Normalized device coordinates
    vec2 ndc = (v_clipSpace.xy/v_clipSpace.w)/2.0 + 0.5;
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);

    float near = u_cameraNearFar.x;
    float far = u_cameraNearFar.y;
    float depth = DecodeFloatRGBA(texture2D(u_refractionDepthTexture, refractTexCoords));
    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
    float waterDepth = floorDistance - waterDistance;

    // When nothing is under(behind) the water, we get some weird values, so ensure depth is atleast above 0.0
    waterDepth = max(waterDepth, 0.0);

    // Dudv distortion
    vec2 distortedTexCoords = texture2D(u_dudvTexture, vec2(v_waterTexCoords.x + u_moveFactor, v_waterTexCoords.y)).rg*0.1;
    distortedTexCoords = v_waterTexCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+u_moveFactor);

    // Soften distortions near edges
    float soften = clamp(waterDepth/80.0, 0.0, 1.0);
    vec2 totalDistortion = (texture2D(u_dudvTexture, distortedTexCoords).rg * 2.0 - 1.0) * u_waveStrength * soften;

    float minTexCoord = 0.005;
    float maxTexCoord = 1.0 - minTexCoord;

    // Normal map
    vec4 normalMapColor = texture2D(u_normalMapTexture, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0);
    normal = normalize(normal);

    // Sample textures with distortion
    #ifdef reflectionFlag
        vec2 reflectTexCoords = vec2(ndc.x, 1.0-ndc.y);
        reflectTexCoords = reflectTexCoords + totalDistortion;
        reflectTexCoords.x = clamp(reflectTexCoords.x, minTexCoord, maxTexCoord);
        reflectTexCoords.y = clamp(reflectTexCoords.y, minTexCoord, maxTexCoord);

        vec4 reflectColor = SRGBtoLINEAR(texture2D(u_reflectionTexture, reflectTexCoords));
    #endif

    #ifdef refractionFlag
        refractTexCoords = refractTexCoords + totalDistortion;
        refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

        vec4 refractColor;

        // Blend amount for color vs refraction texture
        if (waterDepth == 0.0) {
            // Color in the refraction when depth = 0 which happens if nothing is underneath the water
            // (like corners of water)
            refractColor = u_color;
        } else {
            refractColor = SRGBtoLINEAR(texture2D(u_refractionTexture, refractTexCoords));
            float refractionBlend = normalizeRange(waterDepth, 0.0, u_maxVisibleDepth);
            refractColor = mix(refractColor, u_color, refractionBlend);
        }
    #endif

    vec3 viewVector = normalize(v_toCameraVector);

    #ifdef reflectionFlag
        #ifdef refractionFlag
            // If we have both Reflection and Reflection, blend based on fresnel effect
            float refractiveFactor = dot(viewVector, normal);
            vec4 color =  mix(reflectColor, refractColor, refractiveFactor);
        #else
            // No Refraction but we have reflection
            vec4 color = reflectColor;
        #endif
    #else
        #ifdef refractionFlag
            // No Reflection but we have refraction
            vec4 color = refractColor;
        #else
            // We have neither reflection or refraction
            vec4 color = u_color;
        #endif
    #endif

    // Mix some color in
    color.rgb = mix(color.rgb, u_color.rgb, u_color.a);

    // Water Foam implemented from http://fire-face.com/personal/water/
    float edgePatternScroll = u_moveFactor * u_foamScrollSpeed;
    vec4 edgeFalloffColor = vec4(0.8,0.8,0.8,0.6);

    vec2 scaledUV = v_diffuseUV * u_foamScale;

    // Calculate linear falloff value
    float falloff = 1.0 - (waterDepth / u_foamFallOffDistance) + u_foamEdgeBias;

    vec2 coords = v_texCoord0 + totalDistortion;

    // Sample the mask
    float channelA = texture2D(u_foamTexture, scaledUV - vec2(edgePatternScroll, cos(coords.x))).r;
    float channelB = texture2D(u_foamTexture, scaledUV * 0.5 + vec2(sin(coords.y), edgePatternScroll)).b;

    // Modify it to our liking
    float mask = (channelA + channelB) * 0.95;
    mask = pow(mask, 2.0);
    mask = clamp(mask, 0.0, 1.0);

    // Is this pixel in the leading edge?
    if(waterDepth < u_foamFallOffDistance * u_foamEdgeDistance)
    {
        // Modulate the surface alpha and the mask strength
        float leading = waterDepth / (u_foamFallOffDistance * u_foamEdgeDistance);
        color.a *= leading;
        mask *= leading;
    }

    // Color the foam, blend based on alpha
    vec3 edge = edgeFalloffColor.rgb * falloff * edgeFalloffColor.a;

    // This is a workaround fix to resolve an issue when using packed depth that causes white borders on water
    // so if the red channel is full 1.0 its probably pure white (border) so we ignore it.
    if (edge.r < 0.99 && waterDepth > 0.0) {
        // Fade foam out after a distance, otherwise we get ugly 1 pixel lines
        float distanceToCam = length(v_worldPos - u_cameraPosition.xyz);
        float foamVisibleFactor = clamp(1.0 - distanceToCam / 500.0, 0.0, 1.0);

        // Subtract mask value from foam gradient, then add the foam value to the final pixel color
        color.rgb += clamp(edge - vec3(mask), 0.0, 1.0) * foamVisibleFactor;
    }

    // Get directional light
    vec4 totalLight = CalcDirectionalLight(normal);

    // Calculate specular hightlights for directional light
    vec3 specularHighlights = calcSpecularHighlights(u_directionalLight.Base, u_directionalLight.Direction, normal, viewVector, waterDepth);

    // Calculate specular and lighting for point lights, logic modified from gdx-gltf to closer match PBR Shaders
    for (int i = 0 ; i < numPointLights ; i++) {
        if (i >= u_activeNumPointLights){break;}

        // Light distance
        vec3 d = v_worldPos - u_pointLights[i].LocalPos;
        float dist2 = dot(d,d);
        float attenuation = 1.0 + dist2;

        // Apply point light colors to overall color
        vec4 lightColor = vec4((u_pointLights[i].Base.Color, 1.0) * u_pointLights[i].Base.DiffuseIntensity) / attenuation;

        // Unlike PBR shaders there is no PBR light contribution so we manually dampen with * 0.2
        totalLight += 0.2 * lightColor;

        // Point light Specular highlights
        float specularDistanceFactor = length(u_cameraPosition.xyz - u_pointLights[i].LocalPos);

        // Limit distance of point lights specular highlights over water by 500 units
        specularDistanceFactor = clamp(1.0 - specularDistanceFactor / 500.0, 0.0, 1.0);

        // We want specular to adjust based on attenuation, but not to the same degree otherwise we lose too much
        float specularAttenuationFactor = 0.2;

        // Add point light contribution to specular highlights
       specularHighlights += (calcSpecularHighlights(u_pointLights[i].Base, normalize(d), normal, viewVector, waterDepth) * specularDistanceFactor) / (attenuation * specularAttenuationFactor);
    }

    for (int i = 0; i < numSpotLights; i++) {
        if (i >= u_activeNumSpotLights){break;}

        // Light distance
        vec3 d = v_worldPos - u_spotLights[i].Base.LocalPos;
        float dist2 = dot(d,d);
        float attenuation = 1.0 + dist2;
        d*= inversesqrt(dist2);

        // light direction
        vec3 l = normalize(u_spotLights[i].Direction);  // Vector from surface point to light

        // from https://github.com/KhronosGroup/glTF/blob/master/extensions/2.0/Khronos/KHR_lights_punctual/README.md#inner-and-outer-cone-angles
        float lightAngleOffset = u_spotLights[i].Cutoff;
        float lightAngleScale = u_spotLights[i].Exponent;

        float cd = dot(l, d);
        float angularAttenuation = clamp(cd * lightAngleScale + lightAngleOffset, 0.0, 1.0);
        angularAttenuation *= angularAttenuation;

        vec4 lightColor = vec4(u_spotLights[i].Base.Base.Color, 1.0) * u_spotLights[i].Base.Base.DiffuseIntensity;
        totalLight += lightColor * (angularAttenuation / attenuation);
    }

    // Apply all lighting
    color *= totalLight;

    // Apply final specular values
    color += vec4(specularHighlights, 0.0);

    #ifdef GAMMA_CORRECTION
    color.rgb = pow(color.rgb,vec3(1.0/GAMMA_CORRECTION));
    #endif

    // Fog
    #ifdef fogFlag
    float fog = (waterDistance - u_fogEquation.x) / (u_fogEquation.y - u_fogEquation.x);
    fog = clamp(fog, 0.0, 1.0);
    fog = pow(fog, u_fogEquation.z);

    color.rgb  = mix(color.rgb, u_fogColor.rgb, fog * u_fogColor.a);
    #endif

    gl_FragColor = color;
    //gl_FragColor = vec4(waterDepth/50.0);
    //gl_FragColor.a = clamp(waterDepth/5.0, 0.0, 1.0);
}