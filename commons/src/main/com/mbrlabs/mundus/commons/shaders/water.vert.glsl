attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;
uniform vec3 u_cameraPosition;
uniform float u_tiling;
uniform vec3 u_lightPositon;

varying vec2 v_texCoord0;
varying vec4 v_clipSpace;
varying vec3 v_toCameraVector;
varying vec3 v_fromLightVector;

void main() {
    vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);

    v_clipSpace = u_projViewMatrix * worldPos;
    gl_Position = v_clipSpace;

    v_texCoord0 = vec2(a_position.x/2.0 + 0.5, a_position.z/2.0 + 0.5) * u_tiling;

    v_toCameraVector = u_cameraPosition - worldPos.xyz;
    v_fromLightVector = worldPos.xyz - u_lightPositon; // from light to water
}
