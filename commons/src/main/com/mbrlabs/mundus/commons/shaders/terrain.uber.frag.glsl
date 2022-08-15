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

#ifdef GL_ES
#define LOW lowp
#define MED mediump
#define HIGH highp
precision highp float;
#else
#define MED
#define LOW
#define HIGH
#endif

#define PI 3.1415926535897932384626433832795

const MED vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const MED vec4 COLOR_WHITE = vec4(1,1,1, 1.0);
const MED vec4 COLOR_DARK = vec4(0.05,0.05,0.05, 1.0);
const MED vec4 COLOR_BRIGHT = vec4(0.8,0.8,0.8, 1.0);
const MED vec4 COLOR_BRUSH = vec4(0.4,0.4,0.4, 0.4);

// splat textures
uniform sampler2D u_baseTexture;

#ifdef splatFlag

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

    #ifdef normalTextureFlag
    uniform sampler2D u_texture_base_normal;
    uniform sampler2D u_texture_r_normal;
    uniform sampler2D u_texture_g_normal;
    uniform sampler2D u_texture_b_normal;
    uniform sampler2D u_texture_a_normal;
    #endif

#endif // splatFlag

uniform vec3 u_fogEquation;

// mouse picking
#ifdef PICKER
uniform vec3 u_pickerPos;
uniform float u_pickerRadius;
uniform int u_pickerActive;
varying vec3 v_pos;
#endif

uniform MED vec4 u_fogColor;

// light
varying mat3 v_TBN;

varying MED vec2 v_texCoord0;

varying vec2 splatPosition;
varying float v_clipDistance;

void main(void) {
    if ( v_clipDistance < 0.0 )
        discard;

    vec3 normal;

    gl_FragColor = texture2D(u_baseTexture, v_texCoord0);
    #ifdef normalTextureFlag
        normal = texture2D(u_texture_base_normal, v_texCoord0).rgb;
    #endif

    // Mix splat textures
    #ifdef splatFlag
    vec4 splat = texture2D(u_texture_splat, splatPosition);
        #ifdef splatRFlag
            gl_FragColor = mix(gl_FragColor, texture2D(u_texture_r, v_texCoord0), splat.r);
        #endif
        #ifdef splatGFlag
            gl_FragColor = mix(gl_FragColor, texture2D(u_texture_g, v_texCoord0), splat.g);
        #endif
        #ifdef splatBFlag
            gl_FragColor = mix(gl_FragColor, texture2D(u_texture_b, v_texCoord0), splat.b);
        #endif
        #ifdef splatAFlag
            gl_FragColor = mix(gl_FragColor, texture2D(u_texture_a, v_texCoord0), splat.a);
        #endif

        #ifdef normalTextureFlag
            // Splat normals
            #ifdef splatRNormalFlag
                normal = mix(normal, texture2D(u_texture_r_normal, v_texCoord0).rgb, splat.r);
            #endif
            #ifdef splatGNormalFlag
                normal = mix(normal, texture2D(u_texture_g_normal, v_texCoord0).rgb, splat.g);
            #endif
            #ifdef splatBNormalFlag
                normal = mix(normal, texture2D(u_texture_b_normal, v_texCoord0).rgb, splat.b);
            #endif
            #ifdef splatANormalFlag
                normal = mix(normal, texture2D(u_texture_a_normal, v_texCoord0).rgb, splat.a);
            #endif

        #endif

    #endif

    #ifdef normalTextureFlag
        normal = normalize(v_TBN * ((2.0 * normal - 1.0)));
    #else
        normal = normalize(v_TBN[2].xyz);
    #endif


    //    if (u_texture_has_normal_base == 1) {
//        normal = texture2D(u_baseTexture_normal, v_texCoord0).rgb;
//    }
//    if(u_texture_has_splatmap == 1) {
//        vec4 splat = texture2D(u_texture_splat, splatPosition);
//        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_r, v_texCoord0), splat.r);
//        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_g, v_texCoord0), splat.g);
//        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_b, v_texCoord0), splat.b);
//        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_a, v_texCoord0), splat.a);
//
//        // Mix in splat map normals
//        if (u_texture_has_normals == 1) {
//
//            if (u_texture_has_normal_r == 1) {
//                normal = mix(normal, texture2D(u_texture_r_normal, v_texCoord0).rgb, splat.r);
//            }
//            if (u_texture_has_normal_g == 1) {
//                normal = mix(normal, texture2D(u_texture_g_normal, v_texCoord0).rgb, splat.g);
//            }
//            if (u_texture_has_normal_b == 1) {
//                normal = mix(normal, texture2D(u_texture_b_normal, v_texCoord0).rgb, splat.b);
//            }
//            if (u_texture_has_normal_a == 1) {
//                normal = mix(normal, texture2D(u_texture_a_normal, v_texCoord0).rgb, splat.a);
//            }
//
//        }
//    }

//    if (u_texture_has_normals == 1) {
//        normal = normalize(v_TBN * ((2.0 * normal - 1.0)));
//    } else {
//        normal = normalize(v_TBN[2].xyz);
//    }

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

//    // fog
//    if (u_fogEquation.z > 0.0) {
//        vec3 surfaceToCamera = u_cameraPosition.xyz - v_worldPos;
//        float eyeDistance = length(surfaceToCamera);
//
//        float fog = (eyeDistance - u_fogEquation.x) / (u_fogEquation.y - u_fogEquation.x);
//        fog = clamp(fog, 0.0, 1.0);
//        fog = pow(fog, u_fogEquation.z);
//
//        gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, fog * u_fogColor.a);
//    }

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
