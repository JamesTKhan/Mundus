#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
varying vec2 v_waterTexCoords;
varying vec4 v_clipSpace;
varying vec3 v_toCameraVector;
varying float v_fog;
varying vec2 v_diffuseUV;

uniform vec3 u_color;
uniform sampler2D u_texture;
uniform sampler2D u_refractionTexture;
uniform sampler2D u_refractionDepthTexture;
uniform sampler2D u_dudvTexture;
uniform sampler2D u_normalMapTexture;
uniform sampler2D u_foamTexture;
uniform float u_waveStrength;
uniform float u_moveFactor;
uniform float u_shineDamper;
uniform float u_reflectivity;
uniform float u_foamScale;
uniform float u_foamEdgeBias;
uniform float u_foamEdgeDistance;
uniform float u_foamFallOffDistance;
uniform float u_foamScrollSpeed;
uniform vec3 u_cameraPosition;
uniform float u_camNearPlane;
uniform float u_camFarPlane;
uniform vec4 u_fogColor;

const vec4 COLOR_TURQUOISE = vec4(0,0.5,0.686, 0.2);

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

void main() {

    // Normalized device coordinates
    vec2 ndc = (v_clipSpace.xy/v_clipSpace.w)/2.0 + 0.5;
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, 1.0-ndc.y);

    float near = u_camNearPlane;
    float far = u_camFarPlane;
    float depth = DecodeFloatRGBA(texture2D(u_refractionDepthTexture, refractTexCoords));
    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
    float waterDepth = floorDistance - waterDistance;

    // Dudv distortion
    vec2 distortedTexCoords = texture2D(u_dudvTexture, vec2(v_waterTexCoords.x + u_moveFactor, v_waterTexCoords.y)).rg*0.1;
    distortedTexCoords = v_waterTexCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+u_moveFactor);
    vec2 totalDistortion = (texture2D(u_dudvTexture, distortedTexCoords).rg * 2.0 - 1.0) * u_waveStrength * clamp(waterDepth/20.0, 0.0, 1.0);

    float minTexCoord = 0.005;
    float maxTexCoord = 1.0 - minTexCoord;

    refractTexCoords = refractTexCoords + totalDistortion;
    reflectTexCoords = reflectTexCoords + totalDistortion;

    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, 0.001, 0.999);

    // Sample textures with distortion
    vec4 reflectColor = texture2D(u_texture, reflectTexCoords);
    vec4 refractColor = texture2D(u_refractionTexture, refractTexCoords);

    // Normal map
    vec4 normalMapColor = texture2D(u_normalMapTexture, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0);
    normal = normalize(normal);

    // Fresnel Effect
    vec3 viewVector = normalize(v_toCameraVector);
    float refractiveFactor = dot(viewVector, normal);
    vec4 color =  mix(reflectColor, refractColor, refractiveFactor);

    // Mix some color in
    color = mix(color, COLOR_TURQUOISE, 0.2);

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
    if (edge.r < 0.99) {
        // Fade foam out after a distance, otherwise we get ugly 1 pixel lines
        float distanceToCam = length(v_worldPos - u_cameraPosition);
        float foamVisibleFactor = clamp(1.0 - distanceToCam / 1000.0, 0.0, 1.0);

        // Subtract mask value from foam gradient, then add the foam value to the final pixel color
        color.rgb += clamp(edge - vec3(mask), 0.0, 1.0) * foamVisibleFactor;
    }

    // Apply directional light
    color *= CalcDirectionalLight(normal);

    // Calculate specular hightlights for directional light
    vec3 specularHighlights = calcSpecularHighlights(gDirectionalLight.Base, gDirectionalLight.Direction, normal, viewVector, waterDepth);

    // Calculate specular and lighting for point lights
    for (int i = 0 ; i < gNumPointLights ; i++) {
        vec4 lightColor = vec4(gPointLights[i].Base.Color, 1.0) * gPointLights[i].Base.DiffuseIntensity;

        vec3 lightDirection = v_worldPos - gPointLights[i].LocalPos;
        float dist = length(lightDirection);
        lightDirection = normalize(lightDirection);

        float attenuation =  gPointLights[i].Atten.Constant +
        gPointLights[i].Atten.Linear * dist +
        gPointLights[i].Atten.Exp * dist * dist;

        float specularDistanceFactor = length(u_cameraPosition - gPointLights[i].LocalPos);

        // Limit distance of point lights specular highlights over water by 500 units
        specularDistanceFactor = clamp(1.0 - specularDistanceFactor / 500.0, 0.0, 1.0);

        // We want specular to adjust based on attenuation, but not to the same degree otherwise we lose too much
        float specularAttenuationFactor = 0.1f;

        // Add point light contribution to specular highlights
        specularHighlights += (calcSpecularHighlights(gPointLights[i].Base, lightDirection, normal, viewVector, waterDepth) * specularDistanceFactor) / (attenuation * specularAttenuationFactor);

        // Apply point light colors to overall color
        color += lightColor / attenuation;
    }

    // Apply final specular values
    color += vec4(specularHighlights, 0.0);

    // Fog
    color = mix(color, u_fogColor, v_fog);

    gl_FragColor = color;
    //gl_FragColor = vec4(waterDepth/50.0);
    //gl_FragColor.a = clamp(waterDepth/5.0, 0.0, 1.0);
}
