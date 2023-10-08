#include "compat.glsl"

varying float v_clipDistance;
varying vec4 v_position;

#ifndef GLSL3
vec2 encode(float v) {
    const vec2 offset = vec2(1.0/256.0, 0.0);
    vec2 encoded = fract( v * vec2(1.0, 256.0) );
    return encoded - (encoded.yy * offset);
}
#endif

void main() {
    float depth = v_position.z / v_position.w;
    depth = depth * 0.5 + 0.5;

    float moment1 = depth;
    float moment2 = depth * depth;

    // Ajust the variance distribution to include the whole pixel if requested
//    float dx = dFdx(depth);
//    float dy = dFdy(depth);
//    moment2 += 0.25 * (dx * dx + dy * dy);

    #ifdef GLSL3
    gl_FragColor = vec4(moment1, moment2, 0.0, 1.0);
    #else
    vec2 moment1Encoded = encode(moment1);
    vec2 moment2Encoded = encode(moment2);
    gl_FragColor = vec4(moment1Encoded, moment2Encoded);
    #endif
}