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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.MundusAttribute;

public class WaterColorAttribute extends MundusAttribute {
	public final static String DiffuseAlias = "diffuseColor";
	public final static long Diffuse = register(DiffuseAlias);

	protected static long Mask = Diffuse;

	public final static boolean is (final long mask) {
		return (mask & Mask) != 0;
	}


	public final static WaterColorAttribute createDiffuse (final Color color) {
		return new WaterColorAttribute(Diffuse, color);
	}

	public final static WaterColorAttribute createDiffuse (float r, float g, float b, float a) {
		return new WaterColorAttribute(Diffuse, r, g, b, a);
	}

	public final Color color = new Color();

	public WaterColorAttribute(final long type) {
		super(type);
		if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
	}

	public WaterColorAttribute(final long type, final Color color) {
		this(type);
		if (color != null) this.color.set(color);
	}

	public WaterColorAttribute(final long type, float r, float g, float b, float a) {
		this(type);
		this.color.set(r, g, b, a);
	}

	public WaterColorAttribute(final WaterColorAttribute copyFrom) {
		this(copyFrom.type, copyFrom.color);
	}

	@Override
	public MundusAttribute copy () {
		return new WaterColorAttribute(this);
	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 953 * result + color.toIntBits();
		return result; 
	}
	
	@Override
	public int compareTo (MundusAttribute o) {
		if (type != o.type) return (int)(type - o.type);
		return ((WaterColorAttribute)o).color.toIntBits() - color.toIntBits();
	}
}
