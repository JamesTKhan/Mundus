// Brings the normal from [0, 1] to [-1, 1]
vec3 unpackNormal(vec3 normal)
{
    return normalize(normal * 2.0 - 1.0);
}