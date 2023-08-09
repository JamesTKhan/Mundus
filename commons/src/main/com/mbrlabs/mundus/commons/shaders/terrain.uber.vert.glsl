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

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
attribute vec4 a_tangent;

// Default Uniforms
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_cameraPosition;
uniform mat3 u_normalMatrix;

varying vec2 v_texCoord0;
varying vec3 v_worldPos;
varying mat3 v_TBN;

#ifdef PICKER
varying vec3 v_pos;
#endif

#ifdef splatFlag
varying vec2 v_splatPosition;
uniform vec2 u_terrainSize;
#endif

// clipping plane
varying float v_clipDistance;
uniform vec4 u_clipPlane;

uniform mat4 u_shadowMapProjViewTrans;
varying vec3 v_shadowMapUv;

void main(void) {
    // position
    vec4 worldPos = u_worldTrans * vec4(a_position, 1.0);
    gl_Position = u_projViewTrans * worldPos;

    vec4 spos = u_shadowMapProjViewTrans * worldPos;
    v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
    v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998);

    // Logic for Tangent/Bi-tangent/Normal from gdx-gltf
    vec3 tangent = a_tangent.xyz;
    vec3 normalW = normalize(vec3(u_normalMatrix * a_normal.xyz));
    vec3 tangentW = normalize(vec3(u_worldTrans * vec4(tangent, 0.0)));
    vec3 bitangentW = cross(normalW, tangentW) * a_tangent.w;
    v_TBN = mat3(tangentW, bitangentW, normalW);

    // clipping plane
    v_clipDistance = dot(worldPos, u_clipPlane);

    // texture stuff
    v_texCoord0 = a_texCoord0;

    #ifdef splatFlag
    v_splatPosition = vec2(a_position.x / u_terrainSize.x, a_position.z / u_terrainSize);
    #endif

    v_worldPos = worldPos.xyz;

    #ifdef PICKER
    v_pos = worldPos.xyz;
    #endif

}
