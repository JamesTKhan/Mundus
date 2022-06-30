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
uniform sampler2D u_texture_base;
uniform sampler2D u_texture_r;
uniform sampler2D u_texture_g;
uniform sampler2D u_texture_b;
uniform sampler2D u_texture_a;
uniform sampler2D u_texture_splat;
uniform int u_texture_has_splatmap;
uniform int u_texture_has_diffuse;

// splat normal texture
uniform int u_texture_has_normals;
uniform int u_texture_has_normal_base;
uniform int u_texture_has_normal_r;
uniform int u_texture_has_normal_g;
uniform int u_texture_has_normal_b;
uniform int u_texture_has_normal_a;
uniform sampler2D u_texture_base_normal;
uniform sampler2D u_texture_r_normal;
uniform sampler2D u_texture_g_normal;
uniform sampler2D u_texture_b_normal;
uniform sampler2D u_texture_a_normal;

// mouse picking
#ifdef PICKER
uniform vec3 u_pickerPos;
uniform float u_pickerRadius;
uniform int u_pickerActive;
varying vec3 v_pos;
#endif

uniform MED vec4 u_fogColor;

// light
varying vec3 v_normal;
varying mat3 v_TBN;

varying MED vec2 v_texCoord0;
varying float v_fog;

varying vec2 splatPosition;
varying float v_clipDistance;

void main(void) {
    if ( v_clipDistance < 0.0 )
        discard;

    vec3 normal;

    // blend textures
    if(u_texture_has_diffuse == 1) {
        gl_FragColor = texture2D(u_texture_base, v_texCoord0);

        if (u_texture_has_normal_base == 1) {
            normal = texture2D(u_texture_base_normal, v_texCoord0).rgb;
        }
    }
    if(u_texture_has_splatmap == 1) {
        vec4 splat = texture2D(u_texture_splat, splatPosition);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_r, v_texCoord0), splat.r);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_g, v_texCoord0), splat.g);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_b, v_texCoord0), splat.b);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_a, v_texCoord0), splat.a);

        // Mix in splat map normals
        if (u_texture_has_normals == 1) {

            if (u_texture_has_normal_r == 1) {
                normal = mix(normal, texture2D(u_texture_r_normal, v_texCoord0).rgb, splat.r);
            }
            if (u_texture_has_normal_g == 1) {
                normal = mix(normal, texture2D(u_texture_g_normal, v_texCoord0).rgb, splat.g);
            }
            if (u_texture_has_normal_b == 1) {
                normal = mix(normal, texture2D(u_texture_b_normal, v_texCoord0).rgb, splat.b);
            }
            if (u_texture_has_normal_a == 1) {
                normal = mix(normal, texture2D(u_texture_a_normal, v_texCoord0).rgb, splat.a);
            }

        }
    }

    if (u_texture_has_normals == 1) {
        normal = normalize(v_TBN * ((2.0 * normal - 1.0)));
    } else {
        normal = normalize(v_TBN[2].xyz);
    }

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


    // fog
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);

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
