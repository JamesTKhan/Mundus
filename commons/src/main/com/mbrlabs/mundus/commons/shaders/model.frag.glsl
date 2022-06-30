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

const MED vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const MED vec4 AMBIENT = vec4(0.05,0.05,0.05,0.05);

varying MED vec2    v_texCoord0;
varying vec3    v_vectorToLight;
varying vec3    v_surfaceNormal;
varying float   v_fog;
varying vec3 v_normal;

// diffuse material
uniform sampler2D   u_diffuseTexture;
uniform sampler2D   u_normalTexture;
uniform int         u_diffuseUseTexture;
uniform int         u_useNormalMap;

// enviroment
uniform MED vec4 u_fogColor;

varying float v_clipDistance;
varying mat3 v_TBN;

void main(void) {
    if ( v_clipDistance < 0.0 )
        discard;

    if(u_diffuseUseTexture == 1) {
        gl_FragColor = texture2D(u_diffuseTexture, v_texCoord0);
        //    if(gl_FragColor.a < 0.5) {
        //        discard;
        //    }
    } else {
        gl_FragColor = vec4(u_material.DiffuseColor, 1.0);
    }

    vec3 normal;

    if (u_useNormalMap == 1) {
        normal = texture2D(u_normalTexture, v_texCoord0);
        normal = normalize(v_TBN * ((2.0 * normal - 1.0)));
    } else {
        normal = v_normal;
    }

    vec4 totalLight = CalcDirectionalLight(normal);

    for (int i = 0 ; i < numPointLights ; i++) {
        if (i >= u_activeNumPointLights){break;}
        totalLight += CalcPointLight(u_pointLights[i], normal);
    }

    for (int i = 0 ; i < numSpotLights; i++) {
        if (i >= u_activeNumSpotLights){break;}
        totalLight += CalcSpotLight(u_spotLights[i], normal);
    }

    gl_FragColor = max(gl_FragColor, AMBIENT); // TODO make ambient color a unifrom
    gl_FragColor *= totalLight;
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);
}
