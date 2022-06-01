struct BaseLight
{
    vec3 Color;
    vec3 AmbientColor;
    float AmbientIntensity;
    float DiffuseIntensity;
};

struct DirectionalLight
{
    BaseLight Base;
    vec3 Direction;
};

struct Attenuation
{
    float Constant;
    float Linear;
    float Exp;
};

struct PointLight
{
    BaseLight Base;
    vec3 LocalPos;
    Attenuation Atten;
};

struct Material
{
    vec3 AmbientColor;
    vec3 DiffuseColor;
    vec3 SpecularColor;
};

const int MAX_POINT_LIGHTS = 4;
varying vec3 Normal0;
varying vec3 v_worldPos;
uniform DirectionalLight gDirectionalLight;
uniform int gNumPointLights;
uniform PointLight gPointLights[MAX_POINT_LIGHTS];
uniform Material gMaterial;
uniform vec3 u_camPos;

vec4 CalcLightInternal(BaseLight Light, vec3 LightDirection, vec3 Normal)
{
    vec4 AmbientColor = vec4(Light.AmbientColor, 1.0) * Light.AmbientIntensity; /* vec4(gMaterial.AmbientColor, 1.0); */

    float DiffuseFactor = dot(Normal, -LightDirection);

    vec4 DiffuseColor = vec4(0, 0, 0, 0);
    vec4 SpecularColor = vec4(0, 0, 0, 0);

    if (DiffuseFactor > 0.0) {
        DiffuseColor = vec4(Light.Color, 1.0) *
        Light.DiffuseIntensity * /* vec4(gMaterial.DiffuseColor, 1.0) * */ DiffuseFactor;

        vec3 PixelToCamera = normalize(u_camPos - v_worldPos);
        vec3 LightReflect = normalize(reflect(LightDirection, Normal));
        float SpecularFactor = dot(PixelToCamera, LightReflect);
        //        if (SpecularFactor > 0.0) {
        //            float SpecularExponent = texture2D(gSamplerSpecularExponent, TexCoord0).r * 255.0;
        //            SpecularFactor = pow(SpecularFactor, SpecularExponent);
        //            SpecularColor = vec4(Light.Color, 1.0) *
        //            Light.DiffuseIntensity * // using the diffuse intensity for diffuse/specular
        //            vec4(gMaterial.SpecularColor, 1.0) *
        //            SpecularFactor;
        //        }
    }

    return (AmbientColor + DiffuseColor + SpecularColor);
}

vec4 CalcPointLight(int Index, vec3 Normal)
{
    vec3 LightDirection = v_worldPos - gPointLights[Index].LocalPos;
    float dist = length(LightDirection);
    LightDirection = normalize(LightDirection);

    vec4 Color = CalcLightInternal(gPointLights[Index].Base, LightDirection, Normal);
    float attenuation =  gPointLights[Index].Atten.Constant +
    gPointLights[Index].Atten.Linear * dist +
    gPointLights[Index].Atten.Exp * dist * dist;

    return Color / attenuation;
}

vec4 CalcDirectionalLight(vec3 Normal)
{
    return CalcLightInternal(gDirectionalLight.Base, gDirectionalLight.Direction, Normal);
}
