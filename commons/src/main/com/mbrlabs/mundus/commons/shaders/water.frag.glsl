#ifdef GL_ES
precision mediump float;
#endif

in vec2 v_texCoord0;
in vec4 v_clipSpace;
in vec3 v_toCameraVector;
in vec3 v_fromLightVector;

uniform vec3 u_color;
uniform float u_alpha;
uniform sampler2D u_texture;
uniform sampler2D u_refractionTexture;
uniform sampler2D u_dudvTexture;
uniform sampler2D u_normalMapTexture;
uniform float u_waveStrength;
uniform vec3 u_lightColor;
uniform float u_moveFactor;

const vec4 COLOR_TURQUOISE = vec4(0,0.5,0.686, 0.2);
const float shineDamper = 20.0;
const float reflectivity = 0.6;

void main() {

    // Normalized device coordinates
    vec2 ndc = (v_clipSpace.xy/v_clipSpace.w)/2.0 + 0.5;
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, 1.0-ndc.y);

    // Dudv distortion
    vec2 distortedTexCoords = texture(u_dudvTexture, vec2(v_texCoord0.x + u_moveFactor, v_texCoord0.y)).rg*0.1;
    distortedTexCoords = v_texCoord0 + vec2(distortedTexCoords.x, distortedTexCoords.y+u_moveFactor);
    vec2 totalDistortion = (texture(u_dudvTexture, distortedTexCoords).rg * 2.0 - 1.0) * u_waveStrength * clamp(5/20.0,0.0,1.0);

    float minTexCoord = 0.005;
    float maxTexCoord = 1.0 - minTexCoord;

    refractTexCoords = refractTexCoords + totalDistortion;
    reflectTexCoords = reflectTexCoords + totalDistortion;

    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, 0.001, 0.999);

    // Sample textures with distortion
    vec4 reflectColor = texture(u_texture, reflectTexCoords);
    vec4 refractColor = texture(u_refractionTexture, refractTexCoords);

    // Normal map
    vec4 normalMapColor = texture(u_normalMapTexture, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3, normalMapColor.g * 2.0 - 1.0);
    normal = normalize(normal);

    // Fresnel Effect
    vec3 viewVector = normalize(v_toCameraVector);
    float refractiveFactor = dot(viewVector, vec3(0.0,1.0,0.0));

    // Calculate specular hightlights
    vec3 reflectedLight = reflect(normalize(v_fromLightVector), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shineDamper);
    vec3 specularHighlights = u_lightColor * specular * reflectivity;

    vec4 color =  mix(reflectColor, refractColor, refractiveFactor);
    color = mix(color, COLOR_TURQUOISE, 0.2f) + vec4(specularHighlights, 0.0);

    gl_FragColor = color;
}
