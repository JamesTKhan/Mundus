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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;
import com.mbrlabs.mundus.commons.MundusAttribute;

public class WaterFloatAttribute extends MundusAttribute {
	public static final String TilingAlias = "tiling";
	public static final long Tiling = register(TilingAlias);

	public static final String WaveStrengthAlias = "waveStrength";
	public static final long WaveStrength = register(WaveStrengthAlias);

	public static final String WaveSpeedAlias = "waveSpeed";
	public static final long WaveSpeed = register(WaveSpeedAlias);

	public static final String ReflectivityAlias = "reflectivity";
	public static final long Reflectivity = register(ReflectivityAlias);

	public static final String ShineDamperAlias = "shineDamper";
	public static final long ShineDamper = register(ShineDamperAlias);

	public static final String FoamPatternScaleAlias = "foamPatternScale";
	public static final long FoamPatternScale = register(FoamPatternScaleAlias);

	public static final String FoamEdgeBiasAlias = "foamEdgeBias";
	public static final long FoamEdgeBias = register(FoamEdgeBiasAlias);

	public static final String FoamScrollSpeedAlias = "foamScrollSpeed";
	public static final long FoamScrollSpeed = register(FoamScrollSpeedAlias);

	public static final String FoamFallOffDistanceAlias = "foamFallOffDistance";
	public static final long FoamFallOffDistance = register(FoamFallOffDistanceAlias);

	public static final String FoamEdgeDistanceAlias = "foamEdgeDistance";
	public static final long FoamEdgeDistance = register(FoamEdgeDistanceAlias);

	public static final String FoamUVOffsetAlias = "foamUVOffset";
	public static final long FoamUVOffset = register(FoamUVOffsetAlias);

	public static final String MoveFactorAlias = "moveFactor";
	public static final long MoveFactor = register(MoveFactorAlias);

	public static final String MaxVisibleDepthAlias = "maxVisibleDepth";
	public static final long MaxVisibleDepth = register(MaxVisibleDepthAlias);

	public float value;

	public WaterFloatAttribute(long type) {
		super(type);
	}

	public WaterFloatAttribute(long type, float value) {
		super(type);
		this.value = value;
	}

	@Override
	public MundusAttribute copy () {
		return new WaterFloatAttribute(type, value);
	}

	@Override
	public int hashCode () {
		int result = super.hashCode();
		result = 977 * result + NumberUtils.floatToRawIntBits(value);
		return result; 
	}
	
	@Override
	public int compareTo (MundusAttribute o) {
		if (type != o.type) return (int)(type - o.type);
		final float v = ((WaterFloatAttribute)o).value;
		return MathUtils.isEqual(value, v) ? 0 : value < v ? -1 : 1;
	}
}
