/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "compat.glsl"
#include "light.glsl"

// Just stops the static analysis from complaining
#ifndef MED
#define MED
#endif

#define PI 3.1415926535897932384626433832795

const MED vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const MED vec4 COLOR_WHITE = vec4(1,1,1, 1.0);
const MED vec4 COLOR_DARK = vec4(0.05,0.05,0.05, 1.0);
const MED vec4 COLOR_BRIGHT = vec4(0.8,0.8,0.8, 1.0);
const MED vec4 COLOR_BRUSH = vec4(0.4,0.4,0.4, 0.4);

// splat textures
uniform sampler2D u_baseTexture;

#ifdef baseNormalFlag
uniform sampler2D u_texture_base_normal;
#endif

#ifdef splatFlag
    varying vec2 v_splatPosition;
    uniform sampler2D u_texture_splat;

    #ifdef splatRFlag
    uniform sampler2D u_texture_r;
    #endif
    #ifdef splatGFlag
    uniform sampler2D u_texture_g;
    #endif
    #ifdef splatBFlag
    uniform sampler2D u_texture_b;
    #endif
    #ifdef splatAFlag
    uniform sampler2D u_texture_a;
    #endif

    #ifdef splatRNormalFlag
    uniform sampler2D u_texture_r_normal;
    #endif
    #ifdef splatGNormalFlag
    uniform sampler2D u_texture_g_normal;
    #endif
    #ifdef splatBNormalFlag
    uniform sampler2D u_texture_b_normal;
    #endif
    #ifdef splatANormalFlag
    uniform sampler2D u_texture_a_normal;
    #endif

#endif // splatFlag

#ifdef fogFlag
uniform vec3 u_fogEquation;
uniform MED vec4 u_fogColor;
#endif

// mouse picking
#ifdef PICKER
uniform vec3 u_pickerPos;
uniform float u_pickerRadius;
uniform int u_pickerActive;
varying vec3 v_pos;
#endif

// light
varying mat3 v_TBN;

varying vec2 v_texCoord0;
varying float v_clipDistance;

// Brings the normal from [0, 1] to [-1, 1]
vec3 unpackNormal(vec3 normal)
{
    return normalize(normal * 2.0 - 1.0);
}

void main(void) {
    if ( v_clipDistance < 0.0 )
        discard;

    vec3 normal;

    gl_FragColor = texture2D(u_baseTexture, v_texCoord0);

    #ifdef baseNormalFlag
        normal = unpackNormal(texture2D(u_texture_base_normal, v_texCoord0).rgb);
    #endif

    // Mix splat textures
    #ifdef splatFlag
    vec4 splat = texture2D(u_texture_splat, v_splatPosition);
        #ifdef splatRFlag
            vec4 colorR = texture2D(u_texture_r, v_texCoord0);
            gl_FragColor = mix(gl_FragColor, mix(gl_FragColor, colorR, splat.r), colorR.a);
        #endif
        #ifdef splatGFlag
            vec4 colorG = texture2D(u_texture_g, v_texCoord0);
            gl_FragColor = mix(gl_FragColor, mix(gl_FragColor, colorG, splat.g), colorG.a);
        #endif
        #ifdef splatBFlag
            vec4 colorB = texture2D(u_texture_b, v_texCoord0);
            gl_FragColor = mix(gl_FragColor, mix(gl_FragColor, colorB, splat.b), colorB.a);
    #endif
        #ifdef splatAFlag
            vec4 colorA = texture2D(u_texture_a, v_texCoord0);
            gl_FragColor = mix(gl_FragColor, mix(gl_FragColor, colorA, splat.a), colorA.a);
    #endif

        #ifdef normalTextureFlag
            vec3 splatNormal = vec3(0.0);
            // Splat normals
            #ifdef splatRNormalFlag
                splatNormal += unpackNormal(texture2D(u_texture_r_normal, v_texCoord0).rgb) * splat.r;
            #endif
            #ifdef splatGNormalFlag
                splatNormal += unpackNormal(texture2D(u_texture_g_normal, v_texCoord0).rgb) * splat.g;
            #endif
            #ifdef splatBNormalFlag
                splatNormal += unpackNormal(texture2D(u_texture_b_normal, v_texCoord0).rgb) * splat.b;
            #endif
            #ifdef splatANormalFlag
                splatNormal += unpackNormal(texture2D(u_texture_a_normal, v_texCoord0).rgb) * splat.a;
            #endif

            // The base normal should only be visible when the sum of the splat weights is less than 1.0
            float normalBlendFactor = (1.0 - splat.r - splat.g - splat.b - splat.a);
            normal = normalize((normal * normalBlendFactor) + splatNormal);
        #endif

    #endif

    #ifdef normalTextureFlag
        // Apply TBN matrix to tangent space normal to get world space normal
        normal = normalize(v_TBN * normal);
    #else
        normal = normalize(v_TBN[2].xyz);
    #endif

    // =================================================================
    //                          Lighting
    // =================================================================
    vec4 totalLight = CalcDirectionalLight(normal);

    for (int i = 0 ; i < numPointLights ; i++) {
        if (i >= u_activeNumPointLights){break;}
        totalLight += CalcPointLight(u_pointLights[i], normal);
    }

    for (int i = 0; i < numSpotLights; i++) {
        if (i >= u_activeNumSpotLights){break;}
        totalLight += CalcSpotLight(u_spotLights[i], normal);
    }

    gl_FragColor *= totalLight;
    // =================================================================
    //                          /Lighting
    // =================================================================

    #ifdef fogFlag
    // fog
    vec3 surfaceToCamera = u_cameraPosition.xyz - v_worldPos;
    float eyeDistance = length(surfaceToCamera);

    float fog = (eyeDistance - u_fogEquation.x) / (u_fogEquation.y - u_fogEquation.x);
    fog = clamp(fog, 0.0, 1.0);
    fog = pow(fog, u_fogEquation.z);

    gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, fog * u_fogColor.a);
    #endif

    #ifdef PICKER
    if(u_pickerActive == 1) {
        float dist = distance(u_pickerPos, v_pos);
        if(dist <= u_pickerRadius) {
            float gradient = (u_pickerRadius - dist + 0.01) / u_pickerRadius;
            gradient = 1.0 - clamp(cos(gradient * PI), 0.0, 1.0);
            gl_FragColor += COLOR_BRUSH * gradient;
        }
    }
    #endif

}
