#ifdef shadowMapFlag
uniform float u_shadowBias;
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;

#ifdef varianceShadowMapFlag // Based on GPU Gems 3 : Chapter 8.

float linstep(float min, float max, float v)
{
    return clamp((v - min) / (max - min), 0, 1);
}

float decode(vec2 v) {
    return dot(v, vec2(1.0, 1.0/256.0));
}

float reduceLightBleeding(float p_max, float Amount) {
    // Remove the [0, Amount] tail and linearly rescale (Amount, 1].
    return linstep(Amount, 1.0, p_max);
}

// Computes Chebyshev's Inequality
// Returns an upper bound given the first two moments and mean
float chebyshevUpperBound(sampler2D tex, vec3 uv, float mean, float minVariance) {
    // We retrive the two moments previously stored (depth and depth*depth)
    #ifdef GLSL3
    vec2 moments = texture2D(tex, uv.xy).rg;
    #else
    float moment1 = decode(texture2D(tex, uv.xy).rg);
    float moment2 = decode(texture2D(tex, uv.xy).ba);
    vec2 moments = vec2(moment1, moment2);
    #endif

    // Standard shadow map comparison
    float p = float(mean <= moments.x);

    // Compute variance
    float variance = moments.y - (moments.x * moments.x);
    variance = max(variance, minVariance);

    // Compute probabilistic upper bound
    float d = mean - moments.x;
    float p_max = variance / (variance + d * d);

    return max(p, p_max);
}

#endif // varianceShadowMapFlag

#ifdef numCSM

uniform sampler2D u_csmSamplers[numCSM];
uniform vec2 u_csmPCFClip[numCSM];
varying vec3 v_csmUVs[numCSM];

float getCSMShadowness(sampler2D sampler, vec3 uv, vec2 offset){
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(uv.z, dot(texture2D(sampler, uv.xy + offset), bitShifts) + u_shadowBias); // (1.0/255.0)
}

float getCSMShadow(sampler2D sampler, vec3 uv, float pcf){
    #ifdef varianceShadowMapFlag
    float depth = uv.z;
    float shadow = chebyshevUpperBound(sampler, uv, depth, 0.00002);
    shadow = reduceLightBleeding(shadow, 0.2);
    return shadow;
    #else
    return (
    getCSMShadowness(sampler, uv, vec2(pcf, pcf)) +
    getCSMShadowness(sampler, uv, vec2(-pcf, pcf)) +
    getCSMShadowness(sampler, uv, vec2(pcf, -pcf)) +
    getCSMShadowness(sampler, uv, vec2(-pcf, -pcf))) * 0.25;
    #endif
}
float getShadow()
{
    for(int i=0 ; i<numCSM ; i++){
        vec2 pcfClip = u_csmPCFClip[i];
        float pcf = pcfClip.x;
        float clip = pcfClip.y;
        vec3 uv = v_csmUVs[i];
        if(uv.x >= clip && uv.x <= 1.0 - clip &&
        uv.y >= clip && uv.y <= 1.0 - clip &&
        uv.z >= 0.0 && uv.z <= 1.0){
            return getCSMShadow(u_csmSamplers[i], uv, pcf);
        }
    }
    // default map
    return getCSMShadow(u_shadowTexture, v_shadowMapUv, u_shadowPCFOffset);
}

#else

float getShadowness(vec2 offset)
{
    #ifdef GLSL3
    // Using a float texture with depth in the red channel
    float depthFromShadowMap = texture2D(u_shadowTexture, v_shadowMapUv.xy + offset).r;
    return step(v_shadowMapUv.z, depthFromShadowMap + u_shadowBias);
    #else
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts) + u_shadowBias);// (1.0/255.0)
    #endif
}

float getShadow()
{
    #ifdef varianceShadowMapFlag
    float depth = v_shadowMapUv.z;
    float shadow = chebyshevUpperBound(u_shadowTexture, v_shadowMapUv, depth, 0.00002);
    shadow = reduceLightBleeding(shadow, 0.2);
    return shadow;
    #else
    return (//getShadowness(vec2(0,0)) +
    getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
    #endif
}

#endif

#endif//shadowMapFlag
