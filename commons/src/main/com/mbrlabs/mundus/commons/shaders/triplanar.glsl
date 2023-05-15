uniform vec2 u_uvScale;
const float scaleAdjust = 0.0008;

vec3 triplanarNormal(sampler2D normalTex, vec3 worldPos, vec3 triblend)
{
    vec2 uvX = worldPos.zy * u_uvScale * scaleAdjust;
    vec2 uvY = worldPos.xz * u_uvScale * scaleAdjust;
    vec2 uvZ = worldPos.xy * u_uvScale * scaleAdjust;

    // project+fetch
    vec3 x = unpackNormal(texture2D(normalTex, uvX));
    vec3 y = unpackNormal(texture2D(normalTex, uvY));
    vec3 z = unpackNormal(texture2D(normalTex, uvZ));
    vec3 nrm = x * triblend.x + y * triblend.y + z * triblend.z;

    return nrm;
}

vec4 triplanar(sampler2D texture, vec3 worldPos, vec3 triblend)
{
    vec2 uvX = worldPos.zy * u_uvScale * scaleAdjust;
    vec2 uvY = worldPos.xz * u_uvScale * scaleAdjust;
    vec2 uvZ = worldPos.xy * u_uvScale * scaleAdjust;

    // project+fetch
    vec4 x = texture2D(texture, uvX);
    vec4 y = texture2D(texture, uvY);
    vec4 z = texture2D(texture, uvZ);
    vec4 col = x * triblend.x + y * triblend.y + z * triblend.z;

    return col;
}