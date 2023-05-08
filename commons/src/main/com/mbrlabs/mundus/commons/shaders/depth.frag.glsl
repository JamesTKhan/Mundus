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

varying float v_clipDistance;

/*
 * Encodes a floating point number in [0..1) range into several channels of 8 bit/channel render texture for precision
 *
 * https://aras-p.info/blog/2009/07/30/encoding-floats-to-rgba-the-final/
*/
vec4 EncodeFloatRGBA( float v ) {
    vec4 enc = vec4(1.0, 255.0, 65025.0, 16581375.0) * v;
    enc = fract(enc);
    enc -= enc.yzww * vec4(1.0/255.0,1.0/255.0,1.0/255.0,0.0);
    return enc;
}

void main() {
    if ( v_clipDistance < 0.0 )
        discard;

    // Encode the depth into RGBA values, for later decoding in other shaders, to improve precision
    gl_FragColor = EncodeFloatRGBA(gl_FragCoord.z);
}