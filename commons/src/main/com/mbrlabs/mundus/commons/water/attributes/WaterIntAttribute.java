/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.mbrlabs.mundus.commons.water.attributes;

import com.mbrlabs.mundus.commons.MundusAttribute;

public class WaterIntAttribute extends MundusAttribute {
	public static final String CullFaceAlias = "cullfaceWater";
	public static final long CullFace = register(CullFaceAlias);

	/** create a cull face attribute to be used in a material
	 * @param value cull face value, possible values are GL_FRONT_AND_BACK, GL_BACK, GL_FRONT, or -1 to inherit default
	 * @return an attribute */
	public static WaterIntAttribute createCullFace (int value) {
		return new WaterIntAttribute(CullFace, value);
	}

	public int value;

	public WaterIntAttribute(long type) {
		super(type);
	}

	public WaterIntAttribute(long type, int value) {
		super(type);
		this.value = value;
	}

	@Override
	public MundusAttribute copy () {
		return new WaterIntAttribute(type, value);
	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 983 * result + value;
		return result; 
	}
	
	@Override
	public int compareTo (MundusAttribute o) {
		if (type != o.type) return (int)(type - o.type);
		return value - ((WaterIntAttribute)o).value;
	}
}
