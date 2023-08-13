// This file was originally from https://github.com/Auburns/FastNoise_Java as:
// FastNoise.java
//
// MIT License
//
// Copyright(c) 2017 Jordan Peck
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
// The developer's email is jorzixdan.me2@gzixmail.com (for great email, take
// off every 'zix'.)
//

package com.mbrlabs.mundus.editor.terrain;

/**
 * A wide range of noise functions that can all be called from one configurable object. Originally from Jordan Peck's
 * FastNoise library, the implementation here is meant to be fast without sacrificing quality. Usage requires a Noise
 * object, which can be {@link #instance} in simple cases and otherwise should be constructed with whatever adjustment
 * is needed. You can choose Perlin (also called "classic Perlin") noise, Simplex (Ken Perlin's later creation, which is
 * a reasonable default), Cubic (a slower sort of very smooth noise that doesn't have many predictable patterns), Value
 * (simple noise with lots of artifacts), Cellular (a kind of Voronoi-based noise), or plain White noise (a "TV static"
 * pattern). Most of these have fractal variants that allow layering multiple frequencies of noise. After construction
 * you can set how a fractal variant is layered using {@link #setFractalType(int)}, with {@link #FBM} as the normal mode
 * and {@link #RIDGED_MULTI} as a not-uncommon way of altering the form noise takes. This supports 2D, 3D, and 4D fully,
 * with partial support for 6D (which can be used for tiling 3D maps).
 */
public class Noise {
    /**
     * Simple, very fast but very low-quality noise that forms a grid of squares, with their values blending at shared
     * edges somewhat.
     * <br>
     * <a href="https://i.imgur.com/egjotwb.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    public static final int VALUE = 0,
    /**
     * Simple, very fast but very low-quality noise that forms a grid of squares, with their values blending at shared
     * edges somewhat; this version can use {@link #setFractalType(int)}, {@link #setFractalOctaves(int)}, and more, but
     * none of these really disguise the grid it uses.
     * <br>
     * <a href="https://i.imgur.com/egjotwb.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    VALUE_FRACTAL = 1,
    /**
     * Also called Gradient Noise or Classic Perlin noise, this is fast and mid-to-low-quality in 2D, but slows down
     * significantly in higher dimensions while mostly improving in quality. This may have a noticeable grid at 90
     * degree angles (and a little at 45 degree angles).
     * <br>
     * <a href="https://i.imgur.com/MO7hwSI.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    PERLIN = 2,
    /**
     * Also called Gradient Noise or Classic Perlin noise, this is fast and mid-to-low-quality in 2D, but slows down
     * significantly in higher dimensions while mostly improving in quality. This may have a noticeable grid at 90
     * degree angles (and a little at 45 degree angles). This version can use {@link #setFractalType(int)},
     * {@link #setFractalOctaves(int)}, and more.
     * <br>
     * <a href="https://i.imgur.com/MO7hwSI.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    PERLIN_FRACTAL = 3,
    /**
     * Also called Improved Perlin noise, this is always fast but tends to have better quality in lower dimensions. This
     * may have a noticeable grid at 60 degree angles, made of regular triangles in 2D.
     * <br>
     * <a href="https://i.imgur.com/wg3kq5A.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    SIMPLEX = 4,
    /**
     * Also called Improved Perlin noise, this is always fast but tends to have better quality in lower dimensions. This
     * may have a noticeable grid at 60 degree angles, made of regular triangles in 2D. This version can use
     * {@link #setFractalType(int)}, {@link #setFractalOctaves(int)}, and more; it is the default noise type if none is
     * specified.
     * <br>
     * <a href="https://i.imgur.com/wg3kq5A.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    SIMPLEX_FRACTAL = 5,
    /**
     * Creates a Voronoi diagram of 2D or 3D space and fills cells based on the {@link #setCellularReturnType(int)},
     * {@link #setCellularDistanceFunction(int)}, and possibly the {@link #setCellularNoiseLookup(Noise)}. This is
     * more of an advanced usage, but can yield useful results when oddly-shaped areas should have similar values.
     * <br>
     * <a href="https://i.imgur.com/ScRves7.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    CELLULAR = 6,
    /**
     * Purely chaotic, non-continuous random noise per position; looks like static on a TV screen.
     * <br>
     * <a href="https://i.imgur.com/vBtISSx.jpg">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    WHITE_NOISE = 7,
    /**
     * A simple kind of noise that gets a random float for each vertex of a square or cube, and interpolates between all
     * of them to get a smoothly changing value (using cubic interpolation, also called {@link #HERMITE}, of course).
     * <br>
     * <a href="https://i.imgur.com/foV90pn.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    CUBIC = 8,
    /**
     * A simple kind of noise that gets a random float for each vertex of a square or cube, and interpolates between all
     * of them to get a smoothly changing value (using cubic interpolation, also called {@link #HERMITE}, of course).
     * This version can use {@link #setFractalType(int)}, {@link #setFractalOctaves(int)}, and more.
     * <br>
     * <a href="https://i.imgur.com/foV90pn.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    CUBIC_FRACTAL = 9,
    /**
     * A novel kind of noise that works in n-dimensions by averaging n+1 value noise calls, all of them rotated around
     * each other, and with all of the value noise calls after the first adding in the last call's result to part of the
     * position. This yields rather high-quality noise (especially when comparing one octave of FOAM to one octave of
     * {@link #PERLIN} or {@link #SIMPLEX}), but is somewhat slow.
     * <br>
     * <a href="https://i.imgur.com/4ZC9h5t.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    FOAM = 10,
    /**
     * A novel kind of noise that works in n-dimensions by averaging n+1 value noise calls, all of them rotated around
     * each other, and with all of the value noise calls after the first adding in the last call's result to part of the
     * position. This yields rather high-quality noise (especially when comparing one octave of FOAM to one octave of
     * {@link #PERLIN} or {@link #SIMPLEX}), but is somewhat slow. This version can use {@link #setFractalType(int)},
     * {@link #setFractalOctaves(int)}, and more, and usually doesn't need as many octaves as PERLIN or SIMPLEX to
     * attain comparable quality.
     * <br>
     * <a href="https://i.imgur.com/4ZC9h5t.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    FOAM_FRACTAL = 11,
    /**
     * A simple combination of {@link #SIMPLEX} and {@link #VALUE} noise, averaging a call to each and then distorting
     * the result's distribution so it isn't as centrally-biased. The result is somewhere between {@link #FOAM} and
     * {@link #SIMPLEX}, and has less angular bias than Simplex or Value. This gets its name from how it mixes two
     * different geometric honeycombs (a triangular one for 2D Simplex noise and a square one for 2D Value noise).
     * <br>
     * <a href="https://i.imgur.com/bMEPiBA.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    HONEY = 12,
    /**
     * A simple combination of {@link #SIMPLEX_FRACTAL} and {@link #VALUE_FRACTAL} noise, averaging a call to each and
     * then distorting the result's distribution so it isn't as centrally-biased. The result is somewhere between
     * {@link #FOAM_FRACTAL} and {@link #SIMPLEX_FRACTAL}, and has less angular bias than Simplex or Value. This gets
     * its name from how it mixes two different geometric honeycombs (a triangular one for 2D Simplex noise and a square
     * one for 2D Value noise). This version can use {@link #setFractalType(int)}, {@link #setFractalOctaves(int)}, and
     * more, and usually doesn't need as many octaves as PERLIN or SIMPLEX to attain comparable quality, though it
     * drastically improves with just two octaves.
     * <br>
     * <a href="https://i.imgur.com/bMEPiBA.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    HONEY_FRACTAL = 13,
    /**
     * A kind of noise that allows extra configuration via {@link #setMutation(float)}, producing small changes when the
     * mutation value is similar, or large changes if it is very different. This contrasts with changes to the seed,
     * which almost always cause large changes for any difference in seed. The implementation here is the same as
     * {@link #FOAM} with one more dimension, which is filled by the mutation value.
     * <br>
     * <a href="https://i.imgur.com/4ZC9h5t.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    MUTANT = 14,
    /**
     * A kind of noise that allows extra configuration via {@link #setMutation(float)}, producing small changes when the
     * mutation value is similar, or large changes if it is very different. This contrasts with changes to the seed,
     * which almost always cause large changes for any difference in seed. The implementation here is the same as
     * {@link #FOAM_FRACTAL} with one more dimension, which is filled by the mutation value.
     * <br>
     * <a href="https://i.imgur.com/4ZC9h5t.png">Noise sample at left, FFT at right.</a>
     * <br>
     * This is meant to be used with {@link #setNoiseType(int)}.
     */
    MUTANT_FRACTAL = 15;

    /**
     * Simple linear interpolation. May result in artificial-looking noise.
     * Meant to be used with {@link #setInterpolation(int)}.
     */
    public static final int LINEAR = 0;
    /**
     * Cubic interpolation via Hermite spline, more commonly known as "smoothstep".
     * Can be very natural-looking, but can also have problems in higher dimensions
     * (including 3D when used with normals) with seams appearing.
     * Meant to be used with {@link #setInterpolation(int)}.
     */
    public static final int HERMITE = 1;
    /**
     * Quintic interpolation, sometimes known as "smootherstep".
     * This has somewhat steeper transitions than {@link #HERMITE}, but doesn't
     * have any issues with seams.
     * Meant to be used with {@link #setInterpolation(int)}.
     */
    public static final int QUINTIC = 2;

    /**
     * "Standard" layered octaves of noise, where each octave has a different frequency and weight.
     * Tends to look cloudy with more octaves, and generally like a natural process.
     * <br>
     * Meant to be used with {@link #setFractalType(int)}.
     */
    public static final int FBM = 0;
    /**
     * A less common way to layer octaves of noise, where most results are biased toward higher values,
     * but "valleys" show up filled with much lower values.
     * This probably has some good uses in 3D or higher noise, but it isn't used too frequently.
     * <br>
     * Meant to be used with {@link #setFractalType(int)}.
     */
    public static final int BILLOW = 1;
    /**
     * A way to layer octaves of noise so most values are biased toward low values but "ridges" of high
     * values run across the noise. This can be a good way of highlighting the least-natural aspects of
     * some kinds of noise; {@link #PERLIN_FRACTAL} has mostly ridges along 45-degree angles,
     * {@link #SIMPLEX_FRACTAL} has many ridges along a triangular grid, and so on. {@link #FOAM_FRACTAL}
     * and {@link #HONEY_FRACTAL} do well with this mode, though, and look something like lightning or
     * bubbling fluids, respectively. Using FOAM or HONEY will have this look natural, but PERLIN in
     * particular will look unnatural if the grid is visible.
     * <br>
     * Meant to be used with {@link #setFractalType(int)}.
     */
    public static final int RIDGED_MULTI = 2;

    /**
     * Measures distances "as the crow flies."
     * All points at an equal distance from the origin form a circle.
     * Used only with {@link #CELLULAR} noise.
     * Meant to be used with {@link #setCellularDistanceFunction(int)}.
     */
    public static final int EUCLIDEAN = 0;
    /**
     * Measures distances on a grid, as if allowing only orthogonal movement (with no diagonals).
     * All points at an equal distance from the origin form a diamond shape.
     * Used only with {@link #CELLULAR} noise.
     * Meant to be used with {@link #setCellularDistanceFunction(int)}.
     */
    public static final int MANHATTAN = 1;
    /**
     * Measures distances with an approximation of Euclidean distance that's not 100% accurate.
     * All points at an equal distance from the origin form a rough octagon.
     * Used only with {@link #CELLULAR} noise.
     * Meant to be used with {@link #setCellularDistanceFunction(int)}.
     */
    public static final int NATURAL = 2;

    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int CELL_VALUE = 0;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}. Note that this does not allow configuring an extra
     * Noise value to use for lookup (anymore); it always uses 3 octaves of {@link #SIMPLEX_FRACTAL} with {@link #FBM}.
     */
    public static final int NOISE_LOOKUP = 1;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE = 2;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2 = 3;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_ADD = 4;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_SUB = 5;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_MUL = 6;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_DIV = 7;

    /**
     * @see #getSeed()
     */
    protected int seed;

    /**
     * @see #getFrequency()
     */
    protected float frequency = 0.03125f;

    /**
     * @see #getInterpolation()
     */
    protected int interpolation = HERMITE;

    /**
     * @see #getNoiseType()
     */
    protected int noiseType = SIMPLEX_FRACTAL;

    /**
     * @see #getFractalOctaves()
     */
    protected int octaves = 1;

    /**
     * @see #getFractalLacunarity()
     */
    protected float lacunarity = 2f;
    /**
     * @see #getFractalGain()
     */
    protected float gain = 0.5f;
    /**
     * @see #getFractalType()
     */
    protected int fractalType = FBM;

    private float fractalBounding;

    /**
     * @see #getCellularDistanceFunction()
     */
    protected int cellularDistanceFunction = EUCLIDEAN;

    /**
     * @see #getCellularReturnType()
     */
    protected int cellularReturnType = CELL_VALUE;

    /**
     * @see #getGradientPerturbAmp()
     */
    protected float gradientPerturbAmp = 1f / 0.45f;

    /**
     * @see #getFoamSharpness()
     */
    protected float foamSharpness = 1f;

    /**
     * @see #getMutation()
     */
    protected float mutation = 0f;
    
    /**
     * A publicly available Noise object with seed 1337, frequency 1.0f/32.0f, 1 octave of Simplex noise using
     * SIMPLEX_FRACTAL noiseType, 2f lacunarity and 0.5f gain. It's encouraged to use methods that temporarily configure
     * this variable, like {@link #getNoiseWithSeed(float, float, int)} rather than changing its settings and using a
     * method that needs that lasting configuration, like {@link #getConfiguredNoise(float, float)}.
     */
    public static final Noise instance = new Noise();

    /**
     * A constructor that takes no parameters, and uses all default settings with a seed of 1337. An example call to
     * this would be {@code new Noise()}, which makes noise with the seed 1337, a default frequency of 1.0f/32.0f, 1
     * octave of Simplex noise (since this doesn't specify octave count, it always uses 1 even for the
     * SIMPLEX_FRACTAL noiseType this uses, but you can call {@link #setFractalOctaves(int)} later to benefit from the
     * fractal noiseType), and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
     */
    public Noise() {
        this(1337);
    }

    /**
     * A constructor that takes only a parameter for the Noise's seed, which should produce different results for
     * any different seeds. An example call to this would be {@code new Noise(1337)}, which makes noise with the
     * seed 1337, a default frequency of 1.0f/32.0f, 1 octave of Simplex noise (since this doesn't specify octave count,
     * it always uses 1 even for the SIMPLEX_FRACTAL noiseType this uses, but you can call
     * {@link #setFractalOctaves(int)} later to benefit from the fractal noiseType), and normal lacunarity and gain
     * (when unspecified, they are 2f and 0.5f).
     * @param seed the int seed for the noise, which should significantly affect the produced noise
     */
    public Noise(int seed) {
        this.seed = seed;
        calculateFractalBounding();
    }
    /**
     * A constructor that takes two parameters to specify the Noise from the start. An example call to this
     * would be {@code new Noise(1337, 0.02f)}, which makes noise with the seed 1337, a lower
     * frequency, 1 octave of Simplex noise (since this doesn't specify octave count, it always uses 1 even for the
     * SIMPLEX_FRACTAL noiseType this uses, but you can call {@link #setFractalOctaves(int)} later to benefit from the
     * fractal noiseType), and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
     * @param seed the int seed for the noise, which should significantly affect the produced noise
     * @param frequency the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
     */
    public Noise(int seed, float frequency)
    {
        this(seed, frequency, SIMPLEX_FRACTAL, 1, 2f, 0.5f);
    }
    /**
     * A constructor that takes a few parameters to specify the Noise from the start. An example call to this
     * would be {@code new Noise(1337, 0.02f, Noise.SIMPLEX)}, which makes noise with the seed 1337, a lower
     * frequency, 1 octave of Simplex noise (since this doesn't specify octave count, it always uses 1 even for
     * noiseTypes like SIMPLEX_FRACTAL, but using a fractal noiseType can make sense if you call
     * {@link #setFractalOctaves(int)} later), and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
     * @param seed the int seed for the noise, which should significantly affect the produced noise
     * @param frequency the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
     * @param noiseType the noiseType, which should be a constant from this class (see {@link #setNoiseType(int)})
     */
    public Noise(int seed, float frequency, int noiseType)
    {
        this(seed, frequency, noiseType, 1, 2f, 0.5f);
    }

    /**
     * A constructor that takes several parameters to specify the Noise from the start. An example call to this
     * would be {@code new Noise(1337, 0.02f, Noise.SIMPLEX_FRACTAL, 4)}, which makes noise with the seed 1337, a lower
     * frequency, 4 octaves of Simplex noise, and normal lacunarity and gain (when unspecified, they are 2f and 0.5f).
     * @param seed the int seed for the noise, which should significantly affect the produced noise
     * @param frequency the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
     * @param noiseType the noiseType, which should be a constant from this class (see {@link #setNoiseType(int)})
     * @param octaves how many octaves of noise to use when the noiseType is one of the _FRACTAL types
     */
    public Noise(int seed, float frequency, int noiseType, int octaves)
    {
        this(seed, frequency, noiseType, octaves, 2f, 0.5f);
    }

    /**
     * A constructor that takes a lot of parameters to specify the Noise from the start. An example call to this
     * would be {@code new Noise(1337, 0.02f, Noise.SIMPLEX_FRACTAL, 4, 0.5f, 2f)}, which makes noise with a
     * lower frequency, 4 octaves of Simplex noise, and the "inverse" effect on how those octaves work (which makes
     * the extra added octaves be more significant to the final result and also have a lower frequency, while normally
     * added octaves have a higher frequency and tend to have a minor effect on the large-scale shape of the noise).
     * @param seed the int seed for the noise, which should significantly affect the produced noise
     * @param frequency the multiplier for all dimensions, which is usually fairly small (1.0f/32.0f is the default)
     * @param noiseType the noiseType, which should be a constant from this class (see {@link #setNoiseType(int)})
     * @param octaves how many octaves of noise to use when the noiseType is one of the _FRACTAL types
     * @param lacunarity typically 2.0, or 0.5 to change how extra octaves work (inverse mode)
     * @param gain typically 0.5, or 2.0 to change how extra octaves work (inverse mode)
     */
    public Noise(int seed, float frequency, int noiseType, int octaves, float lacunarity, float gain)
    {
        this.seed = seed;
        this.frequency = Math.max(0.0001f, frequency);
        this.noiseType = noiseType;
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.gain = gain;
        calculateFractalBounding();
    }

    /**
     * Copy constructor; copies all non-temporary fields from  {@code other} into this. Everything this copies is a
     * primitive value.
     * @param other another Noise, which must not be null
     */
    public Noise(final Noise other) {
        this(other.seed, other.frequency, other.noiseType, other.octaves, other.lacunarity, other.gain);
        this.fractalType = other.fractalType;
        this.interpolation = other.interpolation;
        this.gradientPerturbAmp = other.gradientPerturbAmp;
        this.cellularReturnType = other.cellularReturnType;
        this.cellularDistanceFunction = other.cellularDistanceFunction;
        this.foamSharpness = other.foamSharpness;
        this.mutation = other.mutation;
    }

    protected static float dotf(final float[] g, final float x, final float y) {
        return g[0] * x + g[1] * y;
    }

    /**
     * @return Returns the seed used by this object
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Sets the seed used for all noise types, as a long.
     * If this is not called, defaults to 1337L.
     * @param seed a seed as a long
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * Sets the frequency for all noise types. If this is not called, it defaults to 0.03125f (or 1f/32f).
     * @param frequency the frequency for all noise types, as a positive non-zero float
     */
    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets the frequency for all noise types. The default is 0.03125f, or 1f/32f.
     * @return the frequency for all noise types, which should be a positive non-zero float
     */
    public float getFrequency()
    {
        return frequency;
    }

    /**
     * Changes the interpolation method used to smooth between noise values, using one of the following constants from
     * this class (lowest to highest quality): {@link #LINEAR} (0), {@link #HERMITE} (1), or {@link #QUINTIC} (2). If
     * this is not called, it defaults to HERMITE. This is used in Value, Perlin, and Position Perturbing, and because
     * it is used in Value, that makes it also apply to Foam, Honey, and Mutant.
     * @param interpolation an int (0, 1, or 2) corresponding to a constant from this class for an interpolation type
     */
    public void setInterpolation(int interpolation) {
        this.interpolation = Math.min(Math.max(interpolation, 0), 2);
    }

    /**
     * Gets the constant corresponding to the interpolation method used to smooth between noise values. This is always
     * one of the constants {@link #LINEAR} (0), {@link #HERMITE} (1), or {@link #QUINTIC} (2). If this is not called,
     * it defaults to HERMITE. This is used in Value, Perlin, and Position Perturbing, and because it is used in Value,
     * that makes it also apply to Foam, Honey, and Mutant.
     * @return an int (0, 1, or 2) corresponding to a constant from this class for an interpolation type
     */
    public int getInterpolation() {
        return interpolation;
    }

    /**
     * Sets the default type of noise returned by {@link #getConfiguredNoise(float, float)}, using one of the following
     * constants in this class:
     * {@link #VALUE} (0), {@link #VALUE_FRACTAL} (1), {@link #PERLIN} (2), {@link #PERLIN_FRACTAL} (3),
     * {@link #SIMPLEX} (4), {@link #SIMPLEX_FRACTAL} (5), {@link #CELLULAR} (6), {@link #WHITE_NOISE} (7),
     * {@link #CUBIC} (8), {@link #CUBIC_FRACTAL} (9), {@link #FOAM} (10), {@link #FOAM_FRACTAL} (11), {@link #HONEY}
     * (12), {@link #HONEY_FRACTAL} (13), {@link #MUTANT} (14), or {@link #MUTANT_FRACTAL} (15).
     * If this isn't called, getConfiguredNoise() will default to SIMPLEX_FRACTAL. Note that if you have a fractal noise
     * type, you can get the corresponding non-fractal noise type by subtracting 1 from the constant this returns. The
     * reverse is not always true, because Cellular and White Noise have no fractal version.
     * @param noiseType an int from 0 to 15 corresponding to a constant from this class for a noise type
     */
    public void setNoiseType(int noiseType) {
        this.noiseType = noiseType;
    }

    /**
     * Gets the default type of noise returned by {@link #getConfiguredNoise(float, float)}, using one of the following
     * constants in this class:
     * {@link #VALUE} (0), {@link #VALUE_FRACTAL} (1), {@link #PERLIN} (2), {@link #PERLIN_FRACTAL} (3),
     * {@link #SIMPLEX} (4), {@link #SIMPLEX_FRACTAL} (5), {@link #CELLULAR} (6), {@link #WHITE_NOISE} (7),
     * {@link #CUBIC} (8), {@link #CUBIC_FRACTAL} (9), {@link #FOAM} (10), {@link #FOAM_FRACTAL} (11), {@link #HONEY}
     * (12), {@link #HONEY_FRACTAL} (13), {@link #MUTANT} (14), or {@link #MUTANT_FRACTAL} (15).
     * The default is SIMPLEX_FRACTAL. Note that if you have a fractal noise type, you can get the corresponding
     * non-fractal noise type by subtracting 1 from the constant this returns. The reverse is not always true, because
     * Cellular and White Noise have no fractal version.
     * @return the noise type as a code, from 0 to 15 inclusive
     */
    public int getNoiseType()
    {
        return noiseType;
    }

    /**
     * Sets the octave count for all fractal noise types.
     * If this isn't called, it will default to 3.
     * @param octaves the number of octaves to use for fractal noise types, as a positive non-zero int
     */
    public void setFractalOctaves(int octaves) {
        this.octaves = octaves;
        calculateFractalBounding();
    }

    /**
     * Gets the octave count for all fractal noise types. The default is 3.
     * @return the number of octaves to use for fractal noise types, as a positive non-zero int
     */
    public int getFractalOctaves()
    {
        return octaves;
    }

    /**
     * Sets the octave lacunarity for all fractal noise types.
     * Lacunarity is a multiplicative change to frequency between octaves. If this isn't called, it defaults to 2.
     * @param lacunarity a non-0 float that will be used for the lacunarity of fractal noise types; commonly 2.0 or 0.5
     */
    public void setFractalLacunarity(float lacunarity) {
        this.lacunarity = lacunarity;
    }

    /**
     * Gets the octave lacunarity for all fractal noise types.
     * Lacunarity is a multiplicative change to frequency between octaves. If this wasn't changed, it defaults to 2.
     * @return a float that will be used for the lacunarity of fractal noise types; commonly 2.0 or 0.5
     */
    public float getFractalLacunarity() {
        return lacunarity;
    }

    /**
     * Sets the octave gain for all fractal noise types.
     * If this isn't called, it defaults to 0.5.
     * @param gain the gain between octaves, as a float
     */
    public void setFractalGain(float gain) {
        this.gain = gain;
        calculateFractalBounding();
    }

    /**
     * Sets the octave gain for all fractal noise types.
     * This is typically related to {@link #getFractalLacunarity()}, with gain falling as lacunarity rises.
     * If this wasn't changed, it defaults to 0.5.
     * @return the gain between octaves, as a float
     */
    public float getFractalGain() {
        return gain;
    }

    /**
     * Sets the method for combining octaves in all fractal noise types, allowing an int argument corresponding to one
     * of the following constants from this class: {@link #FBM} (0), {@link #BILLOW} (1), or {@link #RIDGED_MULTI} (2).
     * If this hasn't been called, it will use FBM.
     * @param fractalType an int (0, 1, or 2) that corresponds to a constant like {@link #FBM} or {@link #RIDGED_MULTI}
     */
    public void setFractalType(int fractalType) {
        this.fractalType = fractalType;
    }

    /**
     * Gets the method for combining octaves in all fractal noise types, allowing an int argument corresponding to one
     * of the following constants from this class: {@link #FBM} (0), {@link #BILLOW} (1), or {@link #RIDGED_MULTI} (2).
     * The default is FBM.
     * @return the fractal type as a code; 0, 1, or 2
     */
    public int getFractalType()
    {
        return fractalType;
    }
    /**
     * Sets the distance function used in cellular noise calculations, allowing an int argument corresponding to one of
     * the following constants from this class: {@link #EUCLIDEAN} (0), {@link #MANHATTAN} (1), or {@link #NATURAL} (2).
     * If this hasn't been called, it will use EUCLIDEAN.
     * @param cellularDistanceFunction an int that can be 0, 1, or 2, corresponding to a constant from this class
     */
    public void setCellularDistanceFunction(int cellularDistanceFunction) {
        this.cellularDistanceFunction = cellularDistanceFunction;
    }

    /**
     * Gets the distance function used in cellular noise calculations, as an int constant from this class:
     * {@link #EUCLIDEAN} (0), {@link #MANHATTAN} (1), or {@link #NATURAL} (2). If this wasn't changed, it will use
     * EUCLIDEAN.
     * @return an int that can be 0, 1, or 2, corresponding to a constant from this class
     */
    public int getCellularDistanceFunction() {
        return cellularDistanceFunction;
    }

    /**
     * Sets the return type from cellular noise calculations, allowing an int argument corresponding to one of the
     * following constants from this class: {@link #CELL_VALUE} (0), {@link #NOISE_LOOKUP} (1), {@link #DISTANCE} (2),
     * {@link #DISTANCE_2} (3), {@link #DISTANCE_2_ADD} (4), {@link #DISTANCE_2_SUB} (5), {@link #DISTANCE_2_MUL} (6),
     * or {@link #DISTANCE_2_DIV} (7). If this isn't called, it will use CELL_VALUE.
     * @param cellularReturnType a constant from this class (see above JavaDoc)
     */
    public void setCellularReturnType(int cellularReturnType) {
        this.cellularReturnType = cellularReturnType;
    }

    /**
     * Gets the return type from cellular noise calculations, corresponding to a constant from this class:
     * {@link #CELL_VALUE} (0), {@link #NOISE_LOOKUP} (1), {@link #DISTANCE} (2), {@link #DISTANCE_2} (3),
     * {@link #DISTANCE_2_ADD} (4), {@link #DISTANCE_2_SUB} (5), {@link #DISTANCE_2_MUL} (6), or
     * {@link #DISTANCE_2_DIV} (7). If this wasn't changed, it will use CELL_VALUE.
     * @return a constant from this class representing a type of cellular noise calculation
     */
    public int getCellularReturnType() {
        return cellularReturnType;
    }

    /**
     * A no-op method that is here for compatibility with earlier versions.
     * @param noise ignored
     */
    public void setCellularNoiseLookup(Noise noise) {
    }

    /**
     * Sets the maximum perturb distance from original location when using {@link #gradientPerturb2(float[])},
     * {@link #gradientPerturb3(float[])}, {@link #gradientPerturbFractal2(float[])}, or
     * {@link #gradientPerturbFractal3(float[])}; the default is 1.0.
     * @param gradientPerturbAmp the maximum perturb distance from the original location when using relevant methods
     */
    public void setGradientPerturbAmp(float gradientPerturbAmp) {
        this.gradientPerturbAmp = gradientPerturbAmp / 0.45f;
    }

    /**
     * Gets the maximum perturb distance from original location when using {@link #gradientPerturb2(float[])},
     * {@link #gradientPerturb3(float[])}, {@link #gradientPerturbFractal2(float[])}, or
     * {@link #gradientPerturbFractal3(float[])}; the default is 1.0.
     * @return the maximum perturb distance from the original location when using relevant methods
     */
    public float getGradientPerturbAmp(){
        return gradientPerturbAmp * 0.45f;
    }

    /**
     * Gets the "sharpness" for the {@link #FOAM}, {@link #FOAM_FRACTAL}, {@link #MUTANT}, and {@link #MUTANT_FRACTAL}
     * noise types, which is usually around 0.25f to 2.0f, and defaults to 1.0f. High values produce extreme results
     * more often, and low values produce mid-range values more often.
     * @return the current "sharpness" {@link #FOAM} and {@link #FOAM_FRACTAL} noise types
     */
    public float getFoamSharpness() {
        return foamSharpness;
    }

    /**
     * Only used with {@link #FOAM}, {@link #FOAM_FRACTAL}, {@link #MUTANT}, and {@link #MUTANT_FRACTAL} noise types,
     * this affects how often the noise will produce very high and very low results (more often with high values of
     * foamSharpness), as opposed to mid-range (more often with low values of foamSharpness).
     * <br>
     * This defaults to 1.0f if not set.
     * @param foamSharpness higher results (above 1) tend to produce extremes, lower results (below 1) produce mid-range
     */
    public void setFoamSharpness(float foamSharpness) {
        this.foamSharpness = foamSharpness;
    }

    /**
     * Gets the mutation value used by {@link #MUTANT} and {@link #MUTANT_FRACTAL} noise types, which allows making
     * small changes to the result when the mutation values are slightly different.
     * @return the current mutation value, which can be any finite float
     */
    public float getMutation() {
        return mutation;
    }

    /**
     * Sets the mutation value used by {@link #MUTANT} and {@link #MUTANT_FRACTAL} noise types, which can be any finite
     * float. Small changes to the mutation value cause small changes in the result, unlike changes to the seed.
     * @param mutation the mutation value to use, which can be any finite float
     */
    public void setMutation(float mutation) {
        this.mutation = mutation;
    }

    public double getNoise(double x, double y) {
        return getConfiguredNoise((float)x, (float)y);
    }

    public double getNoiseWithSeed(double x, double y, long seed) {
        int s = this.seed;
        this.seed = (int) (seed ^ seed >>> 32);
        double r = getConfiguredNoise((float)x, (float)y);
        this.seed = s;
        return r;
    }

    public double getNoise(double x, double y, double z) {
        return getConfiguredNoise((float)x, (float)y, (float)z);
    }

    public double getNoiseWithSeed(double x, double y, double z, long seed) {
        int s = this.seed;
        this.seed = (int) (seed ^ seed >>> 32);
        double r = getConfiguredNoise((float)x, (float)y, (float)z);
        this.seed = s;
        return r;
    }

    public double getNoise(double x, double y, double z, double w) {
        return getConfiguredNoise((float)x, (float)y, (float)z, (float)w);
    }

    public double getNoiseWithSeed(double x, double y, double z, double w, long seed) {
        int s = this.seed;
        this.seed = (int) (seed ^ seed >>> 32);
        double r = getConfiguredNoise((float)x, (float)y, (float)z, (float)w);
        this.seed = s;
        return r;
    }

    public double getNoise(double x, double y, double z, double w, double u) {
        return getConfiguredNoise((float)x, (float)y, (float)z, (float)w, (float)u);
    }

    public double getNoiseWithSeed(double x, double y, double z, double w, double u, long seed) {
        int s = this.seed;
        this.seed = (int) (seed ^ seed >>> 32);
        double r = getConfiguredNoise((float)x, (float)y, (float)z, (float)w, (float)u);
        this.seed = s;
        return r;
    }

    public double getNoise(double x, double y, double z, double w, double u, double v) {
        return getConfiguredNoise((float)x, (float)y, (float)z, (float)w, (float)u, (float)v);
    }

    public double getNoiseWithSeed(double x, double y, double z, double w, double u, double v, long seed) {
        int s = this.seed;
        this.seed = (int) (seed ^ seed >>> 32);
        double r = getConfiguredNoise((float)x, (float)y, (float)z, (float)w, (float)u, (float)v);
        this.seed = s;
        return r;
    }
    
    public float getNoiseWithSeed(float x, float y, int seed) {
        final int s = this.seed;
        this.seed = seed;
        float r = getConfiguredNoise(x, y);
        this.seed = s;
        return r;
    }
    public float getNoiseWithSeed(float x, float y, float z, int seed) {
        final int s = this.seed;
        this.seed = seed;
        float r = getConfiguredNoise(x, y, z);
        this.seed = s;
        return r;
    }
    public float getNoiseWithSeed(float x, float y, float z, float w, int seed) {
        final int s = this.seed;
        this.seed = seed;
        float r = getConfiguredNoise(x, y, z, w);
        this.seed = s;
        return r;
    }

    public float getNoiseWithSeed(float x, float y, float z, float w, float u, int seed) {
        final int s = this.seed;
        this.seed = seed;
        float r = getConfiguredNoise(x, y, z, w, u);
        this.seed = s;
        return r;
    }

    public float getNoiseWithSeed(float x, float y, float z, float w, float u, float v, int seed) {
        final int s = this.seed;
        this.seed = seed;
        float r = getConfiguredNoise(x, y, z, w, u, v);
        this.seed = s;
        return r;
    }

    protected static int fastFloor(float f) {
        return (f >= 0 ? (int) f : (int) f - 1);
    }

    protected static int fastRound(float f) {
        return (f >= 0) ? (int) (f + 0.5f) : (int) (f - 0.5f);
    }


    private static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    protected static float hermiteInterpolator(float t) {
        return t * t * (3 - 2 * t);
    }

    protected static float quinticInterpolator(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    protected static float cubicLerp(float a, float b, float c, float d, float t) {
        float p = (d - c) - (a - b);
        return t * (t * t * p + t * ((a - b) - p) + (c - a)) + b;
    }

    private void calculateFractalBounding() {
        float amp = gain;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        fractalBounding = 1 / ampFractal;
    }

    private float valCoord2D(int seed, int x, int y) {
        return (hashAll(x, y, seed) >> 7) * 0x1.0p-24f;
    }

    private float valCoord3D(int seed, int x, int y, int z) {
        return (hashAll(x, y, z, seed) >> 7) * 0x1.0p-24f;
    }

    private float valCoord4D(int seed, int x, int y, int z, int w) {
        return (hashAll(x, y, z, w, seed) >> 7) * 0x1.0p-24f;
    }

    private float valCoord5D(int seed, int x, int y, int z, int w, int u) {
        return (hashAll(x, y, z, w, u, seed) >> 7) * 0x1.0p-24f;
    }

    private float valCoord6D(int seed, int x, int y, int z, int w, int u, int v) {
        return (hashAll(x, y, z, w, u, v, seed) >> 7) * 0x1.0p-24f;
    }

    protected float gradCoord2D(int seed, int x, int y, float xd, float yd) {
        final int hash = hash256(x, y, seed) << 1;
        return xd * GRAD_2D[hash] + yd * GRAD_2D[hash + 1];
    }

    protected float gradCoord3D(int seed, int x, int y, int z, float xd, float yd, float zd) {
        final int hash = hash32(x, y, z, seed) << 2;
        return xd * GRAD_3D[hash] + yd * GRAD_3D[hash+1] + zd * GRAD_3D[hash+2];
    }

    protected float gradCoord4D(int seed, int x, int y, int z, int w, float xd, float yd, float zd, float wd) {
        final int hash = hash256(x, y, z, w, seed) & 0xFC;
        return xd * GRAD_4D[hash] + yd * GRAD_4D[hash + 1] + zd * GRAD_4D[hash + 2] + wd * GRAD_4D[hash + 3];
    }

    protected float gradCoord5D(int seed, int x, int y, int z, int w, int u,
        float xd, float yd, float zd, float wd, float ud) {
        final int hash = hash256(x, y, z, w, u, seed) << 3;
        return xd * GRAD_5D[hash] + yd * GRAD_5D[hash+1] + zd * GRAD_5D[hash+2] + wd * GRAD_5D[hash+3] + ud * GRAD_5D[hash+4];
    }

    protected float gradCoord6D(int seed, int x, int y, int z, int w, int u, int v,
        float xd, float yd, float zd, float wd, float ud, float vd) {
        final int hash = hash256(x, y, z, w, u, v, seed) << 3;
        return xd * GRAD_6D[hash] + yd * GRAD_6D[hash+1] + zd * GRAD_6D[hash+2] +
                wd * GRAD_6D[hash+3] + ud * GRAD_6D[hash+4] + vd * GRAD_6D[hash+5];
    }

    /**
     * After being configured with the setters in this class, such as {@link #setNoiseType(int)},
     * {@link #setFrequency(float)}, {@link #setFractalOctaves(int)}, and {@link #setFractalType(int)}, among others,
     * you can call this method to get the particular variety of noise you specified, in 2D.
     * @param x x position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param y y position, as a float; the range this should have depends on {@link #getFrequency()}
     * @return noise as a float from -1f to 1f
     */
    public float getConfiguredNoise(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (noiseType) {
            case VALUE:
                return singleValue(seed, x, y);
            case VALUE_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleValueFractalBillow(x, y);
                    case RIDGED_MULTI:
                        return singleValueFractalRidgedMulti(x, y);
                    default:
                        return singleValueFractalFBM(x, y);
                }
            case FOAM:
                return singleFoam(seed, x, y);
            case FOAM_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y);
                    default:
                        return singleFoamFractalFBM(x, y);
                }
            case MUTANT:
                return singleFoam(seed, x, y, mutation);
            case MUTANT_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, mutation);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, mutation);
                    default:
                        return singleFoamFractalFBM(x, y, mutation);
                }
            case HONEY:
                return singleHoney(seed, x, y);
            case HONEY_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleHoneyFractalBillow(x, y);
                    case RIDGED_MULTI:
                        return singleHoneyFractalRidgedMulti(x, y);
                    default:
                        return singleHoneyFractalFBM(x, y);
                }
            case PERLIN:
                return singlePerlin(seed, x, y);
            case PERLIN_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singlePerlinFractalBillow(x, y);
                    case RIDGED_MULTI:
                        return singlePerlinFractalRidgedMulti(x, y);
                    default:
                        return singlePerlinFractalFBM(x, y);
                }
            case SIMPLEX_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleSimplexFractalBillow(x, y);
                    case RIDGED_MULTI:
                        return singleSimplexFractalRidgedMulti(x, y);
                    default:
                        return singleSimplexFractalFBM(x, y);
                }
            case CELLULAR:
                switch (cellularReturnType) {
                    case CELL_VALUE:
                    case NOISE_LOOKUP:
                    case DISTANCE:
                        return singleCellular(x, y);
                    default:
                        return singleCellular2Edge(x, y);
                }
            case WHITE_NOISE:
                return getWhiteNoise(x, y);
            case CUBIC:
                return singleCubic(seed, x, y);
            case CUBIC_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleCubicFractalBillow(x, y);
                    case RIDGED_MULTI:
                        return singleCubicFractalRidgedMulti(x, y);
                    default:
                        return singleCubicFractalFBM(x, y);
                }
            default:
                return singleSimplex(seed, x, y);
        }
    }

    /**
     * After being configured with the setters in this class, such as {@link #setNoiseType(int)},
     * {@link #setFrequency(float)}, {@link #setFractalOctaves(int)}, and {@link #setFractalType(int)}, among others,
     * you can call this method to get the particular variety of noise you specified, in 3D.
     * @param x x position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param y y position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param z z position, as a float; the range this should have depends on {@link #getFrequency()}
     * @return noise as a float from -1f to 1f
     */
    public float getConfiguredNoise(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (noiseType) {
            case VALUE:
                return singleValue(seed, x, y, z);
            case VALUE_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleValueFractalBillow(x, y, z);
                    case RIDGED_MULTI:
                        return singleValueFractalRidgedMulti(x, y, z);
                    default:
                        return singleValueFractalFBM(x, y, z);
                }
            case FOAM:
                return singleFoam(seed, x, y, z);
            case FOAM_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z);
                    default:
                        return singleFoamFractalFBM(x, y, z);
                }
            case MUTANT:
                return singleFoam(seed, x, y, z, mutation);
            case MUTANT_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z, mutation);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z, mutation);
                    default:
                        return singleFoamFractalFBM(x, y, z, mutation);
                }
            case HONEY:
                return singleHoney(seed, x, y, z);
            case HONEY_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleHoneyFractalBillow(x, y, z);
                    case RIDGED_MULTI:
                        return singleHoneyFractalRidgedMulti(x, y, z);
                    default:
                        return singleHoneyFractalFBM(x, y, z);
                }
            case PERLIN:
                return singlePerlin(seed, x, y, z);
            case PERLIN_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singlePerlinFractalBillow(x, y, z);
                    case RIDGED_MULTI:
                        return singlePerlinFractalRidgedMulti(x, y, z);
                    default:
                        return singlePerlinFractalFBM(x, y, z);
                }
            case SIMPLEX_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleSimplexFractalBillow(x, y, z);
                    case RIDGED_MULTI:
                        return singleSimplexFractalRidgedMulti(x, y, z);
                    default:
                        return singleSimplexFractalFBM(x, y, z);
                }
            case CELLULAR:
                switch (cellularReturnType) {
                    case CELL_VALUE:
                    case NOISE_LOOKUP:
                    case DISTANCE:
                        return singleCellular(x, y, z);
                    default:
                        return singleCellular2Edge(x, y, z);
                }
            case WHITE_NOISE:
                return getWhiteNoise(x, y, z);
            case CUBIC:
                return singleCubic(seed, x, y, z);
            case CUBIC_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleCubicFractalBillow(x, y, z);
                    case RIDGED_MULTI:
                        return singleCubicFractalRidgedMulti(x, y, z);
                    default:
                        return singleCubicFractalFBM(x, y, z);
                }
            default:
                return singleSimplex(seed, x, y, z);
        }
    }
    /**
     * After being configured with the setters in this class, such as {@link #setNoiseType(int)},
     * {@link #setFrequency(float)}, {@link #setFractalOctaves(int)}, and {@link #setFractalType(int)}, among others,
     * you can call this method to get the particular variety of noise you specified, in 4D.
     * @param x x position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param y y position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param z z position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param w w position, as a float; the range this should have depends on {@link #getFrequency()}
     * @return noise as a float from -1f to 1f
     */
    public float getConfiguredNoise(float x, float y, float z, float w) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;

        switch (noiseType) {
            case VALUE:
                return singleValue(seed, x, y, z, w);
            case VALUE_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleValueFractalBillow(x, y, z, w);
                    case RIDGED_MULTI:
                        return singleValueFractalRidgedMulti(x, y, z, w);
                    default:
                        return singleValueFractalFBM(x, y, z, w);
                }
            case FOAM:
                return singleFoam(seed, x, y, z, w);
            case FOAM_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z, w);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z, w);
                    default:
                        return singleFoamFractalFBM(x, y, z, w);
                }
            case MUTANT:
                return singleFoam(seed, x, y, z, w, mutation);
            case MUTANT_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z, w, mutation);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z, w, mutation);
                    default:
                        return singleFoamFractalFBM(x, y, z, w, mutation);
                }
            case HONEY:
                return singleHoney(seed, x, y, z, w);
            case HONEY_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleHoneyFractalBillow(x, y, z, w);
                    case RIDGED_MULTI:
                        return singleHoneyFractalRidgedMulti(x, y, z, w);
                    default:
                        return singleHoneyFractalFBM(x, y, z, w);
                }
            case PERLIN:
                return singlePerlin(seed, x, y, z, w);
            case PERLIN_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singlePerlinFractalBillow(x, y, z, w);
                    case RIDGED_MULTI:
                        return singlePerlinFractalRidgedMulti(x, y, z, w);
                    default:
                        return singlePerlinFractalFBM(x, y, z, w);
                }
            case SIMPLEX_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleSimplexFractalBillow(x, y, z, w);
                    case RIDGED_MULTI:
                        return singleSimplexFractalRidgedMulti(x, y, z, w);
                    default:
                        return singleSimplexFractalFBM(x, y, z, w);
                }
            case WHITE_NOISE:
                return getWhiteNoise(x, y, z, w);
            case CUBIC:
                return singleCubic(seed, x, y, z, w);
            case CUBIC_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleCubicFractalBillow(x, y, z, w);
                    case RIDGED_MULTI:
                        return singleCubicFractalRidgedMulti(x, y, z, w);
                    default:
                        return singleCubicFractalFBM(x, y, z, w);
                }

            default:
                return singleSimplex(seed, x, y, z, w);
        }
    }

    /**
     * After being configured with the setters in this class, such as {@link #setNoiseType(int)},
     * {@link #setFrequency(float)}, {@link #setFractalOctaves(int)}, and {@link #setFractalType(int)}, among others,
     * you can call this method to get the particular variety of noise you specified, in 5D.
     * @param x x position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param y y position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param z z position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param w w position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param u u position, as a float; the range this should have depends on {@link #getFrequency()}
     * @return noise as a float from -1f to 1f
     */
    public float getConfiguredNoise(float x, float y, float z, float w, float u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;

        switch (noiseType) {
            case VALUE:
                return singleValue(seed, x, y, z, w, u);
            case VALUE_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleValueFractalBillow(x, y, z, w, u);
                    case RIDGED_MULTI:
                        return singleValueFractalRidgedMulti(x, y, z, w, u);
                    default:
                        return singleValueFractalFBM(x, y, z, w, u);
                }
            case FOAM:
                return singleFoam(seed, x, y, z, w, u);
            case FOAM_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z, w, u);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z, w, u);
                    default:
                        return singleFoamFractalFBM(x, y, z, w, u);
                }
            case MUTANT:
                return singleFoam(seed, x, y, z, w, u, mutation);
            case MUTANT_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z, w, u, mutation);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z, w, u, mutation);
                    default:
                        return singleFoamFractalFBM(x, y, z, w, u, mutation);
                }
            case HONEY:
                return singleHoney(seed, x, y, z, w, u);
            case HONEY_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleHoneyFractalBillow(x, y, z, w, u);
                    case RIDGED_MULTI:
                        return singleHoneyFractalRidgedMulti(x, y, z, w, u);
                    default:
                        return singleHoneyFractalFBM(x, y, z, w, u);
                }
            case PERLIN:
                return singlePerlin(seed, x, y, z, w, u);
            case PERLIN_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singlePerlinFractalBillow(x, y, z, w, u);
                    case RIDGED_MULTI:
                        return singlePerlinFractalRidgedMulti(x, y, z, w, u);
                    default:
                        return singlePerlinFractalFBM(x, y, z, w, u);
                }
            case SIMPLEX_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleSimplexFractalBillow(x, y, z, w, u);
                    case RIDGED_MULTI:
                        return singleSimplexFractalRidgedMulti(x, y, z, w, u);
                    default:
                        return singleSimplexFractalFBM(x, y, z, w, u);
                }
            case WHITE_NOISE:
                return getWhiteNoise(x, y, z, w, u);
            default:
                return singleSimplex(seed, x, y, z, w, u);
        }
    }

    /**
     * After being configured with the setters in this class, such as {@link #setNoiseType(int)},
     * {@link #setFrequency(float)}, {@link #setFractalOctaves(int)}, and {@link #setFractalType(int)}, among others,
     * you can call this method to get the particular variety of noise you specified, in 6D.
     * @param x x position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param y y position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param z z position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param w w position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param u u position, as a float; the range this should have depends on {@link #getFrequency()}
     * @param v v position, as a float; the range this should have depends on {@link #getFrequency()}
     * @return noise as a float from -1f to 1f
     */
    public float getConfiguredNoise(float x, float y, float z, float w, float u, float v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        switch (noiseType) {
            case VALUE:
                return singleValue(seed, x, y, z, w, u, v);
            case VALUE_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleValueFractalBillow(x, y, z, w, u, v);
                    case RIDGED_MULTI:
                        return singleValueFractalRidgedMulti(x, y, z, w, u, v);
                    default:
                        return singleValueFractalFBM(x, y, z, w, u, v);
                }
            case FOAM:
                return singleFoam(seed, x, y, z, w, u, v);
            case FOAM_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z, w, u, v);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z, w, u, v);
                    default:
                        return singleFoamFractalFBM(x, y, z, w, u, v);
                }
            case MUTANT:
                return singleFoam(seed, x, y, z, w, u, v, mutation);
            case MUTANT_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleFoamFractalBillow(x, y, z, w, u, v, mutation);
                    case RIDGED_MULTI:
                        return singleFoamFractalRidgedMulti(x, y, z, w, u, v, mutation);
                    default:
                        return singleFoamFractalFBM(x, y, z, w, u, v, mutation);
                }
            case HONEY:
                return singleHoney(seed, x, y, z, w, u, v);
            case HONEY_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleHoneyFractalBillow(x, y, z, w, u, v);
                    case RIDGED_MULTI:
                        return singleHoneyFractalRidgedMulti(x, y, z, w, u, v);
                    default:
                        return singleHoneyFractalFBM(x, y, z, w, u, v);
                }
            case PERLIN:
                return singlePerlin(seed, x, y, z, w, u, v);
            case PERLIN_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singlePerlinFractalBillow(x, y, z, w, u, v);
                    case RIDGED_MULTI:
                        return singlePerlinFractalRidgedMulti(x, y, z, w, u, v);
                    default:
                        return singlePerlinFractalFBM(x, y, z, w, u, v);
                }
            case SIMPLEX_FRACTAL:
                switch (fractalType) {
                    case BILLOW:
                        return singleSimplexFractalBillow(x, y, z, w, u, v);
                    case RIDGED_MULTI:
                        return singleSimplexFractalRidgedMulti(x, y, z, w, u, v);
                    default:
                        return singleSimplexFractalFBM(x, y, z, w, u, v);
                }
            case WHITE_NOISE:
                return getWhiteNoise(x, y, z, w, u, v);
            default:
                return singleSimplex(seed, x, y, z, w, u, v);
        }
    }

    // White Noise

    /**
     * Gets the bit representation of a float with {@link Float#floatToIntBits(float)} and mixes its
     * typically-more-varied high bits with its low bits, returning an int. NOTE: if you target GWT,
     * this method will be unnecessarily slow because of GWT's poor implementation of floatToIntBits.
     * If you use libGDX and want to use the white noise methods here, you should extend this class
     * and override this method like so:
     * <pre><code>
     * public int floatToIntMixed(final float f) {
     *     final int i = com.badlogic.gdx.utils.NumberUtils.floatToIntBits(f);
     *     return i ^ i >>> 16;
     * }
     * </code></pre>
     * @param f can be any float except for NaN, though this will technically work on NaN
     * @return a slightly-mixed version of the bits that make up {@code f}, as an int
     */
    public int floatToIntMixed(final float f) {
        final int i = Float.floatToIntBits(f);
        return i ^ i >>> 16;
    }

    public float getWhiteNoise(float x, float y) {
        int xi = floatToIntMixed(x);
        int yi = floatToIntMixed(y);

        return valCoord2D(seed, xi, yi);
    }

    public float getWhiteNoise(float x, float y, float z) {
        int xi = floatToIntMixed(x);
        int yi = floatToIntMixed(y);
        int zi = floatToIntMixed(z);

        return valCoord3D(seed, xi, yi, zi);
    }

    public float getWhiteNoise(float x, float y, float z, float w) {
        int xi = floatToIntMixed(x);
        int yi = floatToIntMixed(y);
        int zi = floatToIntMixed(z);
        int wi = floatToIntMixed(w);

        return valCoord4D(seed, xi, yi, zi, wi);
    }

    public float getWhiteNoise(float x, float y, float z, float w, float u) {
        int xi = floatToIntMixed(x);
        int yi = floatToIntMixed(y);
        int zi = floatToIntMixed(z);
        int wi = floatToIntMixed(w);
        int ui = floatToIntMixed(u);

        return valCoord5D(seed, xi, yi, zi, wi, ui);
    }

    public float getWhiteNoise(float x, float y, float z, float w, float u, float v) {
        int xi = floatToIntMixed(x);
        int yi = floatToIntMixed(y);
        int zi = floatToIntMixed(z);
        int wi = floatToIntMixed(w);
        int ui = floatToIntMixed(u);
        int vi = floatToIntMixed(v);

        return valCoord6D(seed, xi, yi, zi, wi, ui, vi);
    }

    // Value Noise
    //x should be premultiplied by 0xD1B55
    //y should be premultiplied by 0xABC99
    private static int hashPart1024(final int x, final int y, int s) {
        s += x ^ y;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }
    //x should be premultiplied by 0xDB4F1
    //y should be premultiplied by 0xBBE05
    //z should be premultiplied by 0xA0F2F
    private static int hashPart1024(final int x, final int y, final int z, int s) {
        s += x ^ y ^ z;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }
    //x should be premultiplied by 0xE19B1
    //y should be premultiplied by 0xC6D1D
    //z should be premultiplied by 0xAF36D
    //w should be premultiplied by 0x9A695
    private static int hashPart1024(final int x, final int y, final int z, final int w, int s) {
        s += x ^ y ^ z ^ w;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }

    //x should be premultiplied by 0xE60E3
    //y should be premultiplied by 0xCEBD7
    //z should be premultiplied by 0xB9C9B
    //w should be premultiplied by 0xA6F57
    //u should be premultiplied by 0x9609D
    private static int hashPart1024(final int x, final int y, final int z, final int w, final int u, int s) {
        s += x ^ y ^ z ^ w ^ u;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }

    //x should be premultiplied by 0xE95E1
    //y should be premultiplied by 0xD4BC7
    //z should be premultiplied by 0xC1EDB
    //w should be premultiplied by 0xB0C8B
    //u should be premultiplied by 0xA127B
    //v should be premultiplied by 0x92E85
    private static int hashPart1024(final int x, final int y, final int z, final int w, final int u, final int v, int s) {
        s += x ^ y ^ z ^ w ^ u ^ v;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }

    //x should be premultiplied by 0xEBEDF 0xD96EB 0xC862B 0xB8ACD 0xAA323 0x9CDA5 0x908E3
    //y should be premultiplied by 0xD96EB
    //z should be premultiplied by 0xC862B
    //w should be premultiplied by 0xB8ACD
    //u should be premultiplied by 0xAA323
    //v should be premultiplied by 0x9CDA5
    //m should be premultiplied by 0x908E3
    private static int hashPart1024(final int x, final int y, final int z, final int w, final int u, final int v, final int m, int s) {
        s += x ^ y ^ z ^ w ^ u ^ v ^ m;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }


    public float getValueFractal(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (fractalType) {
            case FBM:
                return singleValueFractalFBM(x, y);
            case BILLOW:
                return singleValueFractalBillow(x, y);
            case RIDGED_MULTI:
                return singleValueFractalRidgedMulti(x, y);
            default:
                return 0;
        }
    }

    private float singleValueFractalFBM(float x, float y) {
        int seed = this.seed;
        float sum = singleValue(seed, x, y);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += singleValue(++seed, x, y) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalBillow(float x, float y) {
        int seed = this.seed;
        float sum = Math.abs(singleValue(seed, x, y)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
            sum += (Math.abs(singleValue(++seed, x, y)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalRidgedMulti(float x, float y) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleValue(seed + i, x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getValue(float x, float y) {
        return singleValue(seed, x * frequency, y * frequency);
    }


    public float singleValue (int seed, float x, float y) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        switch (interpolation) {
            case HERMITE:
                x = hermiteInterpolator(x);
                y = hermiteInterpolator(y);
                break;
            case QUINTIC:
                x = quinticInterpolator(x);
                y = quinticInterpolator(y);
                break;
        }
        xFloor *= 0xD1B55;
        yFloor *= 0xABC99;
        return ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, seed) + x * hashPart1024(xFloor + 0xD1B55, yFloor, seed))
            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xABC99, seed) + x * hashPart1024(xFloor + 0xD1B55, yFloor + 0xABC99, seed)))
            * 0x1p-9f;
    }

    /**
     * Produces noise from 0 to 1, instead of the normal -1 to 1.
     * @param seed
     * @param x
     * @param y
     * @return noise from 0 to 1.
     */
    protected float valueNoise (int seed, float x, float y) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        x *= x * (3 - 2 * x);
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        y *= y * (3 - 2 * y);
        xFloor *= 0xD1B55;
        yFloor *= 0xABC99;
        return ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, seed) + x * hashPart1024(xFloor + 0xD1B55, yFloor, seed))
            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xABC99, seed) + x * hashPart1024(xFloor + 0xD1B55, yFloor + 0xABC99, seed)))
            * 0x1p-10f + 0.5f;
    }
    public float getValueFractal(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (fractalType) {
            case BILLOW:
                return singleValueFractalBillow(x, y, z);
            case RIDGED_MULTI:
                return singleValueFractalRidgedMulti(x, y, z);
            default:
                return singleValueFractalFBM(x, y, z);
        }
    }

    private float singleValueFractalFBM(float x, float y, float z) {
        int seed = this.seed;
        float sum = singleValue(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += singleValue(++seed, x, y, z) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalBillow(float x, float y, float z) {
        int seed = this.seed;
        float sum = Math.abs(singleValue(seed, x, y, z)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleValue(++seed, x, y, z)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalRidgedMulti(float x, float y, float z) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleValue(seed + i, x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getValue(float x, float y, float z) {
        return singleValue(seed, x * frequency, y * frequency, z * frequency);
    }

    public float singleValue(int seed, float x, float y, float z) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        switch (interpolation) {
            case HERMITE:
                x = hermiteInterpolator(x);
                y = hermiteInterpolator(y);
                z = hermiteInterpolator(z);
                break;
            case QUINTIC:
                x = quinticInterpolator(x);
                y = quinticInterpolator(y);
                z = quinticInterpolator(z);
                break;
        }
        //0xDB4F1, 0xBBE05, 0xA0F2F
        xFloor *= 0xDB4F1;
        yFloor *= 0xBBE05;
        zFloor *= 0xA0F2F;
        return ((1 - z) *
            ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor, zFloor, seed))
                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xBBE05, zFloor, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor + 0xBBE05, zFloor, seed)))
            + z *
            ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xA0F2F, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor, zFloor + 0xA0F2F, seed))
                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xBBE05, zFloor + 0xA0F2F, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor + 0xBBE05, zFloor + 0xA0F2F, seed)))
        ) * 0x1p-9f;
    }
    /**
     * Produces noise from 0 to 1, instead of the normal -1 to 1.
     * @param seed
     * @param x
     * @param y
     * @param z
     * @return noise from 0 to 1.
     */
    protected float valueNoise(int seed, float x, float y, float z)
    {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        x *= x * (3 - 2 * x);
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        y *= y * (3 - 2 * y);
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        z *= z * (3 - 2 * z);
        //0xDB4F1, 0xBBE05, 0xA0F2F
        xFloor *= 0xDB4F1;
        yFloor *= 0xBBE05;
        zFloor *= 0xA0F2F;
        return ((1 - z) *
            ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor, zFloor, seed))
                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xBBE05, zFloor, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor + 0xBBE05, zFloor, seed)))
            + z *
            ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xA0F2F, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor, zFloor + 0xA0F2F, seed))
                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xBBE05, zFloor + 0xA0F2F, seed) + x * hashPart1024(xFloor + 0xDB4F1, yFloor + 0xBBE05, zFloor + 0xA0F2F, seed)))
        ) * 0x1p-10f + 0.5f;
    }
    public float getValueFractal(float x, float y, float z, float w) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;

        switch (fractalType) {
            case BILLOW:
                return singleValueFractalBillow(x, y, z, w);
            case RIDGED_MULTI:
                return singleValueFractalRidgedMulti(x, y, z, w);
            default:
                return singleValueFractalFBM(x, y, z, w);
        }
    }

    private float singleValueFractalFBM(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = singleValue(seed, x, y, z, w);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += singleValue(++seed, x, y, z, w) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalBillow(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = Math.abs(singleValue(seed, x, y, z, w)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleValue(++seed, x, y, z, w)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalRidgedMulti(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleValue(seed + i, x, y, z, w));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getValue(float x, float y, float z, float w) {
        return singleValue(seed, x * frequency, y * frequency, z * frequency, w * frequency);
    }

    public float singleValue(int seed, float x, float y, float z, float w) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        int wFloor = w >= 0 ? (int) w : (int) w - 1;
        w -= wFloor;
        switch (interpolation) {
            case HERMITE:
                x = hermiteInterpolator(x);
                y = hermiteInterpolator(y);
                z = hermiteInterpolator(z);
                w = hermiteInterpolator(w);
                break;
            case QUINTIC:
                x = quinticInterpolator(x);
                y = quinticInterpolator(y);
                z = quinticInterpolator(z);
                w = quinticInterpolator(w);
                break;
        }
        //0xE19B1, 0xC6D1D, 0xAF36D, 0x9A695
        xFloor *= 0xE19B1;
        yFloor *= 0xC6D1D;
        zFloor *= 0xAF36D;
        wFloor *= 0x9A695;
        return ((1 - w) *
            ((1 - z) *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor, wFloor, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor, wFloor, seed)))
                + z *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xAF36D, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor + 0xAF36D, wFloor, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor, seed))))
            + (w *
            ((1 - z) *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor, wFloor + 0x9A695, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor, wFloor + 0x9A695, seed)))
                + z *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xAF36D, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor + 0xAF36D, wFloor + 0x9A695, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor + 0x9A695, seed)))
            ))) * 0x1p-9f;
    }
    protected float valueNoise(int seed, float x, float y, float z, float w)
    {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        x *= x * (3 - 2 * x);
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        y *= y * (3 - 2 * y);
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        z *= z * (3 - 2 * z);
        int wFloor = w >= 0 ? (int) w : (int) w - 1;
        w -= wFloor;
        w *= w * (3 - 2 * w);
        //0xE19B1, 0xC6D1D, 0xAF36D, 0x9A695
        xFloor *= 0xE19B1;
        yFloor *= 0xC6D1D;
        zFloor *= 0xAF36D;
        wFloor *= 0x9A695;
        return ((1 - w) *
            ((1 - z) *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor, wFloor, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor, wFloor, seed)))
                + z *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xAF36D, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor + 0xAF36D, wFloor, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor, seed))))
            + (w *
            ((1 - z) *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor, wFloor + 0x9A695, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor, wFloor + 0x9A695, seed)))
                + z *
                ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xAF36D, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor, zFloor + 0xAF36D, wFloor + 0x9A695, seed))
                    + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor + 0x9A695, seed) + x * hashPart1024(xFloor + 0xE19B1, yFloor + 0xC6D1D, zFloor + 0xAF36D, wFloor + 0x9A695, seed)))
            ))) * 0x1p-10f + 0.5f;
    }

    public float getValueFractal(float x, float y, float z, float w, float u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;

        switch (fractalType) {
            case BILLOW:
                return singleValueFractalBillow(x, y, z, w, u);
            case RIDGED_MULTI:
                return singleValueFractalRidgedMulti(x, y, z, w, u);
            default:
                return singleValueFractalFBM(x, y, z, w, u);
        }
    }
    private float singleValueFractalFBM(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = singleValue(seed, x, y, z, w, u);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += singleValue(++seed, x, y, z, w, u) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalBillow(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = Math.abs(singleValue(seed, x, y, z, w, u)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleValue(++seed, x, y, z, w, u)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalRidgedMulti(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleValue(seed + i, x, y, z, w, u));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getValue(float x, float y, float z, float w, float u) {
        return singleValue(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency);
    }

    public float singleValue(int seed, float x, float y, float z, float w, float u) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        int wFloor = w >= 0 ? (int) w : (int) w - 1;
        w -= wFloor;
        int uFloor = u >= 0 ? (int) u : (int) u - 1;
        u -= uFloor;
        switch (interpolation) {
            case HERMITE:
                x = hermiteInterpolator(x);
                y = hermiteInterpolator(y);
                z = hermiteInterpolator(z);
                w = hermiteInterpolator(w);
                u = hermiteInterpolator(u);
                break;
            case QUINTIC:
                x = quinticInterpolator(x);
                y = quinticInterpolator(y);
                z = quinticInterpolator(z);
                w = quinticInterpolator(w);
                u = quinticInterpolator(u);
                break;
        }
        //0xE60E3, 0xCEBD7, 0xB9C9B, 0xA6F57, 0x9609D, 0x86D51
        xFloor *= 0xE60E3;
        yFloor *= 0xCEBD7;
        zFloor *= 0xB9C9B;
        wFloor *= 0xA6F57;
        uFloor *= 0x9609D;
        return ((1 - u) *
            ((1 - w) *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor, uFloor, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor, seed))))
                + (w *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor + 0xA6F57, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed)))
                )))
            + (u *
            ((1 - w) *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor, uFloor + 0x9609D, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed))))
                + (w *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed)))
                ))))
        ) * 0x1p-9f;
    }

    protected float valueNoise(int seed, float x, float y, float z, float w, float u) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        x *= x * (3 - 2 * x);
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        y *= y * (3 - 2 * y);
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        z *= z * (3 - 2 * z);
        int wFloor = w >= 0 ? (int) w : (int) w - 1;
        w -= wFloor;
        w *= w * (3 - 2 * w);
        int uFloor = u >= 0 ? (int) u : (int) u - 1;
        u -= uFloor;
        u *= u * (3 - 2 * u);
        //0xE60E3, 0xCEBD7, 0xB9C9B, 0xA6F57, 0x9609D, 0x86D51
        xFloor *= 0xE60E3;
        yFloor *= 0xCEBD7;
        zFloor *= 0xB9C9B;
        wFloor *= 0xA6F57;
        uFloor *= 0x9609D;
        return ((1 - u) *
            ((1 - w) *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor, uFloor, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor, seed))))
                + (w *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor + 0xA6F57, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor, seed)))
                )))
            + (u *
            ((1 - w) *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor, uFloor + 0x9609D, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor, uFloor + 0x9609D, seed))))
                + (w *
                ((1 - z) *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor, wFloor + 0xA6F57, uFloor + 0x9609D, seed)))
                    + z *
                    ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed))
                        + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed) + x * hashPart1024(xFloor + 0xE60E3, yFloor + 0xCEBD7, zFloor + 0xB9C9B, wFloor + 0xA6F57, uFloor + 0x9609D, seed)))
                ))))
        ) * 0x1p-10f + 0.5f;
    }

    public float getValueFractal(float x, float y, float z, float w, float u, float v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        switch (fractalType) {
            case BILLOW:
                return singleValueFractalBillow(x, y, z, w, u, v);
            case RIDGED_MULTI:
                return singleValueFractalRidgedMulti(x, y, z, w, u, v);
            default:
                return singleValueFractalFBM(x, y, z, w, u, v);
        }
    }
    private float singleValueFractalFBM(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = singleValue(seed, x, y, z, w, u, v);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += singleValue(++seed, x, y, z, w, u, v) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalBillow(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = Math.abs(singleValue(seed, x, y, z, w, u, v)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleValue(++seed, x, y, z, w, u, v)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleValueFractalRidgedMulti(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleValue(seed + i, x, y, z, w, u, v));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getValue(float x, float y, float z, float w, float u, float v) {
        return singleValue(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency, v * frequency);
    }

    public float singleValue(int seed, float x, float y, float z, float w, float u, float v) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        int wFloor = w >= 0 ? (int) w : (int) w - 1;
        w -= wFloor;
        int uFloor = u >= 0 ? (int) u : (int) u - 1;
        u -= uFloor;
        int vFloor = v >= 0 ? (int) v : (int) v - 1;
        v -= vFloor;
        switch (interpolation) {
            case HERMITE:
                x = hermiteInterpolator(x);
                y = hermiteInterpolator(y);
                z = hermiteInterpolator(z);
                w = hermiteInterpolator(w);
                u = hermiteInterpolator(u);
                v = hermiteInterpolator(v);
                break;
            case QUINTIC:
                x = quinticInterpolator(x);
                y = quinticInterpolator(y);
                z = quinticInterpolator(z);
                w = quinticInterpolator(w);
                u = quinticInterpolator(u);
                v = quinticInterpolator(v);
                break;
        }
        //0xE95E1, 0xD4BC7, 0xC1EDB, 0xB0C8B, 0xA1279, 0x92E85
        xFloor *= 0xE95E1;
        yFloor *= 0xD4BC7;
        zFloor *= 0xC1EDB;
        wFloor *= 0xB0C8B;
        uFloor *= 0xA127B;
        vFloor *= 0x92E85;
        return ((1 - v) *
            ((1 - u) *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed)))
                    )))
                + (u *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed)))
                    )))))
            + (v *
            ((1 - u) *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed)))
                    )))
                + (u *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed)))
                    ))))))
        ) * 0x1p-9f;
    }

    protected float valueNoise(int seed, float x, float y, float z, float w, float u, float v) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        x *= x * (3 - 2 * x);
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        y *= y * (3 - 2 * y);
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        z *= z * (3 - 2 * z);
        int wFloor = w >= 0 ? (int) w : (int) w - 1;
        w -= wFloor;
        w *= w * (3 - 2 * w);
        int uFloor = u >= 0 ? (int) u : (int) u - 1;
        u -= uFloor;
        u *= u * (3 - 2 * u);
        int vFloor = v >= 0 ? (int) v : (int) v - 1;
        v -= vFloor;
        v *= v * (3 - 2 * v);
        //0xE95E1, 0xD4BC7, 0xC1EDB, 0xB0C8B, 0xA1279, 0x92E85
        xFloor *= 0xE95E1;
        yFloor *= 0xD4BC7;
        zFloor *= 0xC1EDB;
        wFloor *= 0xB0C8B;
        uFloor *= 0xA127B;
        vFloor *= 0x92E85;
        return ((1 - v) *
            ((1 - u) *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor, seed)))
                    )))
                + (u *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor, seed)))
                    )))))
            + (v *
            ((1 - u) *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor, vFloor + 0x92E85, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor, vFloor + 0x92E85, seed)))
                    )))
                + (u *
                ((1 - w) *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor, uFloor + 0xA127B, vFloor + 0x92E85, seed))))
                    + (w *
                    ((1 - z) *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed)))
                        + z *
                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed))
                            + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed) + x * hashPart1024(xFloor + 0xE95E1, yFloor + 0xD4BC7, zFloor + 0xC1EDB, wFloor + 0xB0C8B, uFloor + 0xA127B, vFloor + 0x92E85, seed)))
                    ))))))
        ) * 0x1p-10f + 0.5f;
    }

    protected float valueNoise(int seed, float x, float y, float z, float w, float u, float v, float m) {
        int xFloor = x >= 0 ? (int) x : (int) x - 1;
        x -= xFloor;
        x *= x * (3 - 2 * x);
        int yFloor = y >= 0 ? (int) y : (int) y - 1;
        y -= yFloor;
        y *= y * (3 - 2 * y);
        int zFloor = z >= 0 ? (int) z : (int) z - 1;
        z -= zFloor;
        z *= z * (3 - 2 * z);
        int wFloor = w >= 0 ? (int) w : (int) w - 1;
        w -= wFloor;
        w *= w * (3 - 2 * w);
        int uFloor = u >= 0 ? (int) u : (int) u - 1;
        u -= uFloor;
        u *= u * (3 - 2 * u);
        int vFloor = v >= 0 ? (int) v : (int) v - 1;
        v -= vFloor;
        v *= v * (3 - 2 * v);
        int mFloor = m >= 0 ? (int) m : (int) m - 1;
        m -= mFloor;
        m *= m * (3 - 2 * m);
        xFloor *= 0xEBEDF;
        yFloor *= 0xD96EB;
        zFloor *= 0xC862B;
        wFloor *= 0xB8ACD;
        uFloor *= 0xAA323;
        vFloor *= 0x9CDA5;
        mFloor *= 0x908E3;
        return
                ((1 - m) *
                        ((1 - v) *
                                ((1 - u) *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor, seed)))
                                                )))
                                        + (u *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor, seed)))
                                                )))))
                                + (v *
                                ((1 - u) *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor, seed)))
                                                )))
                                        + (u *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor, seed)))
                                                )))))))
                        + (m *
                        ((1 - v) *
                                ((1 - u) *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor, mFloor + 0x908E3, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor, mFloor + 0x908E3, seed)))
                                                )))
                                        + (u *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor, mFloor + 0x908E3, seed)))
                                                )))))
                                + (v *
                                ((1 - u) *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor, vFloor + 0x9CDA5, mFloor + 0x908E3, seed)))
                                                )))
                                        + (u *
                                        ((1 - w) *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))))
                                                + (w *
                                                ((1 - z) *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed)))
                                                        + z *
                                                        ((1 - y) * ((1 - x) * hashPart1024(xFloor, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed))
                                                                + y * ((1 - x) * hashPart1024(xFloor, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed) + x * hashPart1024(xFloor + 0xEBEDF, yFloor + 0xD96EB, zFloor + 0xC862B, wFloor + 0xB8ACD, uFloor + 0xAA323, vFloor + 0x9CDA5, mFloor + 0x908E3, seed)))
                                                ))))))))
                ) * 0x1p-10f + 0.5f;
    }

    // Foam Noise

    public float getFoam(float x, float y) {
        return singleFoam(seed, x * frequency, y * frequency);
    }

    public float singleFoam(int seed, float x, float y) {
        final float p0 = x;
        final float p1 = x * -0.5f + y * 0.8660254037844386f;
        final float p2 = x * -0.5f + y * -0.8660254037844387f;

        float xin = p2;
        float yin = p0;
        final float a = valueNoise(seed, xin, yin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p1;
        yin = p2;
        final float b = valueNoise(seed, xin + a, yin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        final float c = valueNoise(seed, xin + b, yin);
        final float result = (a + b + c) * F3f;
        final float sharp = foamSharpness * 2.2f;
        final float diff = 0.5f - result;
        final int sign = Float.floatToIntBits(diff) >> 31, one = sign | 1;
        return (((result + sign)) / (Float.MIN_VALUE - sign + (result + sharp * diff) * one) - sign - sign) - 1f;
    }

    public float getFoamFractal(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (fractalType) {
            case FBM:
                return singleFoamFractalFBM(x, y);
            case BILLOW:
                return singleFoamFractalBillow(x, y);
            case RIDGED_MULTI:
                return singleFoamFractalRidgedMulti(x, y);
            default:
                return 0;
        }
    }

    private float singleFoamFractalFBM(float x, float y) {
        int seed = this.seed;
        float sum = singleFoam(seed, x, y);
        float amp = 1, t;

        for (int i = 1; i < octaves; i++) {
            t = x;
            x = y * lacunarity;
            y = t * lacunarity;

            amp *= gain;
            sum += singleFoam(seed + i, x, y) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalBillow(float x, float y) {
        int seed = this.seed;
        float sum = Math.abs(singleFoam(seed, x, y)) * 2 - 1;
        float amp = 1, t;

        for (int i = 1; i < octaves; i++) {
            t = x;
            x = y * lacunarity;
            y = t * lacunarity;

            amp *= gain;
            sum += (Math.abs(singleFoam(++seed, x, y)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalRidgedMulti(float x, float y) {
        int seed = this.seed;
        float t;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleFoam(seed + i, x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            t = x;
            x = y * lacunarity;
            y = t * lacunarity;
        }
        return sum * 2f / correction - 1f;
    }


    public float getFoamFractal(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (fractalType) {
            case FBM:
                return singleFoamFractalFBM(x, y, z);
            case BILLOW:
                return singleFoamFractalBillow(x, y, z);
            case RIDGED_MULTI:
                return singleFoamFractalRidgedMulti(x, y, z);
            default:
                return 0;
        }
    }

    private float singleFoamFractalFBM(float x, float y, float z) {
        int seed = this.seed;
        float sum = singleFoam(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += singleFoam(++seed, x, y, z) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalBillow(float x, float y, float z) {
        int seed = this.seed;
        float sum = Math.abs(singleFoam(seed, x, y, z)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleFoam(++seed, x, y, z)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalRidgedMulti(float x, float y, float z) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleFoam(seed + i, x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getFoam(float x, float y, float z) {
        return singleFoam(seed, x * frequency, y * frequency, z * frequency);
    }

    public float singleFoam(int seed, float x, float y, float z){
        final float p0 = x;
        final float p1 = x * -0.3333333333333333f + y * 0.9428090415820634f;
        final float p2 = x * -0.3333333333333333f + y * -0.4714045207910317f + z * 0.816496580927726f;
        final float p3 = x * -0.3333333333333333f + y * -0.4714045207910317f + z * -0.816496580927726f;

        float xin = p3;
        float yin = p2;
        float zin = p0;
        final float a = valueNoise(seed, xin, yin, zin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p3;
        final float b = valueNoise(seed, xin + a, yin, zin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p1;
        yin = p2;
        zin = p3;
        final float c = valueNoise(seed, xin + b, yin, zin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p2;
        final float d = valueNoise(seed, xin + c, yin, zin);

        final float result = (a + b + c + d) * 0.25f;
        final float sharp = foamSharpness * 3.3f;
        final float diff = 0.5f - result;
        final int sign = Float.floatToIntBits(diff) >> 31, one = sign | 1;
        return (((result + sign)) / (Float.MIN_VALUE - sign + (result + sharp * diff) * one) - sign - sign) - 1f;

    }


    private float singleFoamFractalFBM(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = singleFoam(seed, x, y, z, w);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += singleFoam(++seed, x, y, z, w) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalBillow(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = Math.abs(singleFoam(seed, x, y, z, w)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleFoam(++seed, x, y, z, w)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalRidgedMulti(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleFoam(seed + i, x, y,  z, w));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getFoam(float x, float y, float z, float w) {
        return singleFoam(seed, x * frequency, y * frequency, z * frequency, w * frequency);
    }

    public float singleFoam(int seed, float x, float y, float z, float w) {
        final float p0 = x;
        final float p1 = x * -0.25f + y *  0.9682458365518543f;
        final float p2 = x * -0.25f + y * -0.3227486121839514f + z *  0.91287092917527690f;
        final float p3 = x * -0.25f + y * -0.3227486121839514f + z * -0.45643546458763834f + w *  0.7905694150420949f;
        final float p4 = x * -0.25f + y * -0.3227486121839514f + z * -0.45643546458763834f + w * -0.7905694150420947f;

        float xin = p1;
        float yin = p2;
        float zin = p3;
        float win = p4;
        final float a = valueNoise(seed, xin, yin, zin, win);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p2;
        zin = p3;
        win = p4;
        final float b = valueNoise(seed, xin + a, yin, zin, win);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p3;
        win = p4;
        final float c = valueNoise(seed, xin + b, yin, zin, win);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p2;
        win = p4;
        final float d = valueNoise(seed, xin + c, yin, zin, win);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p2;
        win = p3;
        final float e = valueNoise(seed, xin + d, yin, zin, win);

        final float result = (a + b + c + d + e) * 0.2f;
        final float sharp = foamSharpness * 4.4f;
        final float diff = 0.5f - result;
        final int sign = Float.floatToIntBits(diff) >> 31, one = sign | 1;
        return (((result + sign)) / (Float.MIN_VALUE - sign + (result + sharp * diff) * one) - sign - sign) - 1f;
    }
    public float getFoamFractal(float x, float y, float z, float w, float u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;

        switch (fractalType) {
            case FBM:
                return singleFoamFractalFBM(x, y, z, w, u);
            case BILLOW:
                return singleFoamFractalBillow(x, y, z, w, u);
            case RIDGED_MULTI:
                return singleFoamFractalRidgedMulti(x, y, z, w, u);
            default:
                return 0;
        }
    }

    private float singleFoamFractalFBM(float x, float y, float z, float w, float u) {
        final int seed = this.seed;
        float sum = singleFoam(seed, x, y, z, w, u);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += singleFoam(seed + i, x, y, z, w, u) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalBillow(float x, float y, float z, float w, float u) {
        final int seed = this.seed;
        float sum = Math.abs(singleFoam(seed, x, y, z, w, u)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleFoam(seed + i, x, y, z, w, u)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalRidgedMulti(float x, float y, float z, float w, float u) {
        final int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleFoam(seed + i, x, y, z, w, u));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getFoam(float x, float y, float z, float w, float u) {
        return singleFoam(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency);
    }

    public float singleFoam(int seed, float x, float y, float z, float w, float u) {
        final float p0 = x *  0.8157559148337911f + y *  0.5797766823136037f;
        final float p1 = x * -0.7314923478726791f + y *  0.6832997137249108f;
        final float p2 = x * -0.0208603044412437f + y * -0.3155296974329846f + z * 0.9486832980505138f;
        final float p3 = x * -0.0208603044412437f + y * -0.3155296974329846f + z * -0.316227766016838f + w *   0.8944271909999159f;
        final float p4 = x * -0.0208603044412437f + y * -0.3155296974329846f + z * -0.316227766016838f + w * -0.44721359549995804f + u *  0.7745966692414833f;
        final float p5 = x * -0.0208603044412437f + y * -0.3155296974329846f + z * -0.316227766016838f + w * -0.44721359549995804f + u * -0.7745966692414836f;

        float xin = p1;
        float yin = p2;
        float zin = p3;
        float win = p4;
        float uin = p5;
        final float a = valueNoise(seed, xin, yin, zin, win, uin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p2;
        zin = p3;
        win = p4;
        uin = p5;
        final float b = valueNoise(seed, xin + a, yin, zin, win, uin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p3;
        win = p4;
        uin = p5;
        final float c = valueNoise(seed, xin + b, yin, zin, win, uin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p2;
        win = p4;
        uin = p5;
        final float d = valueNoise(seed, xin + c, yin, zin, win, uin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p2;
        win = p3;
        uin = p5;
        final float e = valueNoise(seed, xin + d, yin, zin, win, uin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p1;
        zin = p2;
        win = p3;
        uin = p4;
        final float f = valueNoise(seed, xin + e, yin, zin, win, uin);

        final float result = (a + b + c + d + e + f) * 0.16666666666666666f;
        final float sharp = foamSharpness * 5.5f;
        final float diff = 0.5f - result;
        final int sign = Float.floatToIntBits(diff) >> 31, one = sign | 1;
        return (((result + sign)) / (Float.MIN_VALUE - sign + (result + sharp * diff) * one) - sign - sign) - 1f;

    }

    public float getFoamFractal(float x, float y, float z, float w, float u, float v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        switch (fractalType) {
            case FBM:
                return singleFoamFractalFBM(x, y, z, w, u, v);
            case BILLOW:
                return singleFoamFractalBillow(x, y, z, w, u, v);
            case RIDGED_MULTI:
                return singleFoamFractalRidgedMulti(x, y, z, w, u, v);
            default:
                return 0;
        }
    }

    private float singleFoamFractalFBM(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = singleFoam(seed, x, y, z, w, u, v);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += singleFoam(++seed, x, y, z, w, u, v) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalBillow(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = Math.abs(singleFoam(seed, x, y, z, w, u, v)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleFoam(++seed, x, y, z, w, u, v)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalRidgedMulti(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleFoam(seed + i, x, y, z, w, u, v));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getFoam(float x, float y, float z, float w, float u, float v) {
        return singleFoam(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency, v * frequency);
    }

    public float singleFoam(int seed, float x, float y, float z, float w, float u, float v) {
        final float p0 = x;
        final float p1 = x * -0.16666666666666666f + y *  0.98601329718326940f;
        final float p2 = x * -0.16666666666666666f + y * -0.19720265943665383f + z *  0.96609178307929590f;
        final float p3 = x * -0.16666666666666666f + y * -0.19720265943665383f + z * -0.24152294576982394f + w *  0.93541434669348530f;
        final float p4 = x * -0.16666666666666666f + y * -0.19720265943665383f + z * -0.24152294576982394f + w * -0.31180478223116176f + u *  0.8819171036881969f;
        final float p5 = x * -0.16666666666666666f + y * -0.19720265943665383f + z * -0.24152294576982394f + w * -0.31180478223116176f + u * -0.4409585518440984f + v *  0.7637626158259734f;
        final float p6 = x * -0.16666666666666666f + y * -0.19720265943665383f + z * -0.24152294576982394f + w * -0.31180478223116176f + u * -0.4409585518440984f + v * -0.7637626158259732f;
        float xin = p0;
        float yin = p5;
        float zin = p3;
        float win = p6;
        float uin = p1;
        float vin = p4;
        final float a = valueNoise(seed, xin, yin, zin, win, uin, vin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p2;
        yin = p6;
        zin = p0;
        win = p4;
        uin = p5;
        vin = p3;
        final float b = valueNoise(seed, xin + a, yin, zin, win, uin, vin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p1;
        yin = p2;
        zin = p3;
        win = p4;
        uin = p6;
        vin = p5;
        final float c = valueNoise(seed, xin + b, yin, zin, win, uin, vin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p6;
        yin = p0;
        zin = p2;
        win = p5;
        uin = p4;
        vin = p1;
        final float d = valueNoise(seed, xin + c, yin, zin, win, uin, vin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p2;
        yin = p1;
        zin = p5;
        win = p0;
        uin = p3;
        vin = p6;
        final float e = valueNoise(seed, xin + d, yin, zin, win, uin, vin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p0;
        yin = p4;
        zin = p6;
        win = p3;
        uin = p1;
        vin = p2;
        final float f = valueNoise(seed, xin + e, yin, zin, win, uin, vin);
        seed += 0x9E3779BD;
        seed ^= seed >>> 14;
        xin = p5;
        yin = p1;
        zin = p2;
        win = p3;
        uin = p4;
        vin = p0;
        final float g = valueNoise(seed, xin + f, yin, zin, win, uin, vin);
        final float result = (a + b + c + d + e + f + g) * 0.14285714285714285f;
        final float sharp = foamSharpness * 6.6f;
        final float diff = 0.5f - result;
        final int sign = Float.floatToIntBits(diff) >> 31, one = sign | 1;
        return (((result + sign)) / (Float.MIN_VALUE - sign + (result + sharp * diff) * one) - sign - sign) - 1f;
    }


    private float singleFoamFractalFBM(float x, float y, float z, float w, float u, float v, float m) {
        int seed = this.seed;
        float sum = singleFoam(seed, x, y, z, w, u, v, m);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
            m *= lacunarity;

            amp *= gain;
            sum += singleFoam(++seed, x, y, z, w, u, v, m) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalBillow(float x, float y, float z, float w, float u, float v, float m) {
        int seed = this.seed;
        float sum = Math.abs(singleFoam(seed, x, y, z, w, u, v, m)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
            m *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleFoam(++seed, x, y, z, w, u, v, m)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleFoamFractalRidgedMulti(float x, float y, float z, float w, float u, float v, float m) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleFoam(seed + i, x, y, z, w, u, v, m));
            correction += (exp *= 0.5f);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
            m *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float singleFoam(int seed, float x, float y, float z, float w, float u, float v, float m) {
        final float p0 = x;
        final float p1 = x * -0.14285714285714285f + y * +0.9897433186107870f;
        final float p2 = x * -0.14285714285714285f + y * -0.1649572197684645f + z * +0.97590007294853320f;
        final float p3 = x * -0.14285714285714285f + y * -0.1649572197684645f + z * -0.19518001458970663f + w * +0.95618288746751490f;
        final float p4 = x * -0.14285714285714285f + y * -0.1649572197684645f + z * -0.19518001458970663f + w * -0.23904572186687872f + u * +0.92582009977255150f;
        final float p5 = x * -0.14285714285714285f + y * -0.1649572197684645f + z * -0.19518001458970663f + w * -0.23904572186687872f + u * -0.30860669992418377f + v * +0.8728715609439696f;
        final float p6 = x * -0.14285714285714285f + y * -0.1649572197684645f + z * -0.19518001458970663f + w * -0.23904572186687872f + u * -0.30860669992418377f + v * -0.4364357804719847f + m * +0.7559289460184545f;
        final float p7 = x * -0.14285714285714285f + y * -0.1649572197684645f + z * -0.19518001458970663f + w * -0.23904572186687872f + u * -0.30860669992418377f + v * -0.4364357804719847f + m * -0.7559289460184544f;
        float xin = p0;
        float yin = p6;
        float zin = p3;
        float win = p7;
        float uin = p1;
        float vin = p4;
        float min = p5;
        final float a = valueNoise(seed, xin, yin, zin, win, uin, vin, min);
        seed += 0x9E377;
        xin = p2;
        yin = p3;
        zin = p0;
        win = p4;
        uin = p6;
        vin = p5;
        min = p7;
        final float b = valueNoise(seed, xin + a, yin, zin, win, uin, vin, min);
        seed += 0x9E377;
        xin = p1;
        yin = p2;
        zin = p4;
        win = p3;
        uin = p5;
        vin = p7;
        min = p6;
        final float c = valueNoise(seed, xin + b, yin, zin, win, uin, vin, min);
        seed += 0x9E377;
        xin = p7;
        yin = p0;
        zin = p2;
        win = p5;
        uin = p4;
        vin = p6;
        min = p1;
        final float d = valueNoise(seed, xin + c, yin, zin, win, uin, vin, min);
        seed += 0x9E377;
        xin = p3;
        yin = p1;
        zin = p5;
        win = p6;
        uin = p7;
        vin = p0;
        min = p2;
        final float e = valueNoise(seed, xin + d, yin, zin, win, uin, vin, min);
        seed += 0x9E377;
        xin = p4;
        yin = p7;
        zin = p6;
        win = p2;
        uin = p0;
        vin = p1;
        min = p3;
        final float f = valueNoise(seed, xin + e, yin, zin, win, uin, vin, min);
        seed += 0x9E377;
        xin = p5;
        yin = p4;
        zin = p7;
        win = p1;
        uin = p2;
        vin = p3;
        min = p0;
        final float g = valueNoise(seed, xin + f, yin, zin, win, uin, vin, min);
        seed += 0x9E377;
        xin = p6;
        yin = p5;
        zin = p1;
        win = p0;
        uin = p3;
        vin = p2;
        min = p4;
        final float h = valueNoise(seed, xin + g, yin, zin, win, uin, vin, min);
        final float result = (a + b + c + d + e + f + g + h) * 0.125f;
        final float sharp = foamSharpness * 7.7f;
        final float diff = 0.5f - result;
        final int sign = Float.floatToIntBits(diff) >> 31, one = sign | 1;
        return (((result + sign)) / (Float.MIN_VALUE - sign + (result + sharp * diff) * one) - sign - sign) - 1f;
    }


    // (Classic) Perlin Noise, AKA Gradient Noise

    public float getPerlinFractal(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (fractalType) {
            case FBM:
                return singlePerlinFractalFBM(x, y);
            case BILLOW:
                return singlePerlinFractalBillow(x, y);
            case RIDGED_MULTI:
                return singlePerlinFractalRidgedMulti(x, y);
            default:
                return 0;
        }
    }

    private float singlePerlinFractalFBM(float x, float y) {
        int seed = this.seed;
        float sum = singlePerlin(seed, x, y);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += singlePerlin(++seed, x, y) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalBillow(float x, float y) {
        int seed = this.seed;
        float sum = Math.abs(singlePerlin(seed, x, y)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singlePerlin(++seed, x, y)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalRidgedMulti(float x, float y) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singlePerlin(seed + i, x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getPerlin(float x, float y) {
        return singlePerlin(seed, x * frequency, y * frequency);
    }

    public float singlePerlin(int seed, float x, float y) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        float xs, ys;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                break;
        }

        float xd0 = x - x0;
        float yd0 = y - y0;
        float xd1 = xd0 - 1;
        float yd1 = yd0 - 1;

        float xf0 = lerp(gradCoord2D(seed, x0, y0, xd0, yd0), gradCoord2D(seed, x1, y0, xd1, yd0), xs);
        float xf1 = lerp(gradCoord2D(seed, x0, y1, xd0, yd1), gradCoord2D(seed, x1, y1, xd1, yd1), xs);

        return lerp(xf0, xf1, ys);
    }

    public float getPerlinFractal(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (fractalType) {
            case FBM:
                return singlePerlinFractalFBM(x, y, z);
            case BILLOW:
                return singlePerlinFractalBillow(x, y, z);
            case RIDGED_MULTI:
                return singlePerlinFractalRidgedMulti(x, y, z);
            default:
                return 0;
        }
    }

    private float singlePerlinFractalFBM(float x, float y, float z) {
        int seed = this.seed;
        float sum = singlePerlin(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += singlePerlin(++seed, x, y, z) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalBillow(float x, float y, float z) {
        int seed = this.seed;
        float sum = Math.abs(singlePerlin(seed, x, y, z)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singlePerlin(++seed, x, y, z)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalRidgedMulti(float x, float y, float z) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singlePerlin(seed + i, x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getPerlin(float x, float y, float z) {
        return singlePerlin(seed, x * frequency, y * frequency, z * frequency);
    }

    public float singlePerlin(int seed, float x, float y, float z) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;

        float xs, ys, zs;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                zs = z - z0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                zs = hermiteInterpolator(z - z0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                zs = quinticInterpolator(z - z0);
                break;
        }

        final float xd0 = x - x0;
        final float yd0 = y - y0;
        final float zd0 = z - z0;
        final float xd1 = xd0 - 1;
        final float yd1 = yd0 - 1;
        final float zd1 = zd0 - 1;

        final float xf00 = lerp(gradCoord3D(seed, x0, y0, z0, xd0, yd0, zd0), gradCoord3D(seed, x1, y0, z0, xd1, yd0, zd0), xs);
        final float xf10 = lerp(gradCoord3D(seed, x0, y1, z0, xd0, yd1, zd0), gradCoord3D(seed, x1, y1, z0, xd1, yd1, zd0), xs);
        final float xf01 = lerp(gradCoord3D(seed, x0, y0, z1, xd0, yd0, zd1), gradCoord3D(seed, x1, y0, z1, xd1, yd0, zd1), xs);
        final float xf11 = lerp(gradCoord3D(seed, x0, y1, z1, xd0, yd1, zd1), gradCoord3D(seed, x1, y1, z1, xd1, yd1, zd1), xs);

        final float yf0 = lerp(xf00, xf10, ys);
        final float yf1 = lerp(xf01, xf11, ys);

        return lerp(yf0, yf1, zs);
    }
    public float getPerlin(float x, float y, float z, float w) {
        return singlePerlin(seed, x * frequency, y * frequency, z * frequency, w * frequency);
    }

    public float singlePerlin(int seed, float x, float y, float z, float w) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);
        int w0 = fastFloor(w);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;
        int w1 = w0 + 1;

        float xs, ys, zs, ws;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                zs = z - z0;
                ws = w - w0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                zs = hermiteInterpolator(z - z0);
                ws = hermiteInterpolator(w - w0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                zs = quinticInterpolator(z - z0);
                ws = quinticInterpolator(w - w0);
                break;
        }

        final float xd0 = x - x0;
        final float yd0 = y - y0;
        final float zd0 = z - z0;
        final float wd0 = w - w0;
        final float xd1 = xd0 - 1;
        final float yd1 = yd0 - 1;
        final float zd1 = zd0 - 1;
        final float wd1 = wd0 - 1;

        final float xf000 = lerp(gradCoord4D(seed, x0, y0, z0, w0, xd0, yd0, zd0, wd0), gradCoord4D(seed, x1, y0, z0, w0, xd1, yd0, zd0, wd0), xs);
        final float xf100 = lerp(gradCoord4D(seed, x0, y1, z0, w0, xd0, yd1, zd0, wd0), gradCoord4D(seed, x1, y1, z0, w0, xd1, yd1, zd0, wd0), xs);
        final float xf010 = lerp(gradCoord4D(seed, x0, y0, z1, w0, xd0, yd0, zd1, wd0), gradCoord4D(seed, x1, y0, z1, w0, xd1, yd0, zd1, wd0), xs);
        final float xf110 = lerp(gradCoord4D(seed, x0, y1, z1, w0, xd0, yd1, zd1, wd0), gradCoord4D(seed, x1, y1, z1, w0, xd1, yd1, zd1, wd0), xs);
        final float xf001 = lerp(gradCoord4D(seed, x0, y0, z0, w1, xd0, yd0, zd0, wd1), gradCoord4D(seed, x1, y0, z0, w1, xd1, yd0, zd0, wd1), xs);
        final float xf101 = lerp(gradCoord4D(seed, x0, y1, z0, w1, xd0, yd1, zd0, wd1), gradCoord4D(seed, x1, y1, z0, w1, xd1, yd1, zd0, wd1), xs);
        final float xf011 = lerp(gradCoord4D(seed, x0, y0, z1, w1, xd0, yd0, zd1, wd1), gradCoord4D(seed, x1, y0, z1, w1, xd1, yd0, zd1, wd1), xs);
        final float xf111 = lerp(gradCoord4D(seed, x0, y1, z1, w1, xd0, yd1, zd1, wd1), gradCoord4D(seed, x1, y1, z1, w1, xd1, yd1, zd1, wd1), xs);

        final float yf00 = lerp(xf000, xf100, ys);
        final float yf10 = lerp(xf010, xf110, ys);
        final float yf01 = lerp(xf001, xf101, ys);
        final float yf11 = lerp(xf011, xf111, ys);

        final float zf0 = lerp(yf00, yf10, zs);
        final float zf1 = lerp(yf01, yf11, zs);
        return lerp(zf0, zf1, ws) * 0.55f;
    }
    private float singlePerlinFractalFBM(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = singlePerlin(seed, x, y, z, w);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += singlePerlin(++seed, x, y, z, w) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalBillow(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = Math.abs(singlePerlin(seed, x, y, z, w)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singlePerlin(++seed, x, y, z, w)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalRidgedMulti(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singlePerlin(seed + i, x, y,  z, w));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getPerlin(float x, float y, float z, float w, float u) {
        return singlePerlin(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency);
    }
    
    public float singlePerlin(int seed, float x, float y, float z, float w, float u) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);
        int w0 = fastFloor(w);
        int u0 = fastFloor(u);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;
        int w1 = w0 + 1;
        int u1 = u0 + 1;

        float xs, ys, zs, ws, us;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                zs = z - z0;
                ws = w - w0;
                us = u - u0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                zs = hermiteInterpolator(z - z0);
                ws = hermiteInterpolator(w - w0);
                us = hermiteInterpolator(u - u0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                zs = quinticInterpolator(z - z0);
                ws = quinticInterpolator(w - w0);
                us = quinticInterpolator(u - u0);
                break;
        }

        final float xd0 = x - x0;
        final float yd0 = y - y0;
        final float zd0 = z - z0;
        final float wd0 = w - w0;
        final float ud0 = u - u0;
        final float xd1 = xd0 - 1;
        final float yd1 = yd0 - 1;
        final float zd1 = zd0 - 1;
        final float wd1 = wd0 - 1;
        final float ud1 = ud0 - 1;

        final float xf0000 = lerp(gradCoord5D(seed, x0, y0, z0, w0, u0, xd0, yd0, zd0, wd0, ud0), gradCoord5D(seed, x1, y0, z0, w0, u0, xd1, yd0, zd0, wd0, ud0), xs);
        final float xf1000 = lerp(gradCoord5D(seed, x0, y1, z0, w0, u0, xd0, yd1, zd0, wd0, ud0), gradCoord5D(seed, x1, y1, z0, w0, u0, xd1, yd1, zd0, wd0, ud0), xs);
        final float xf0100 = lerp(gradCoord5D(seed, x0, y0, z1, w0, u0, xd0, yd0, zd1, wd0, ud0), gradCoord5D(seed, x1, y0, z1, w0, u0, xd1, yd0, zd1, wd0, ud0), xs);
        final float xf1100 = lerp(gradCoord5D(seed, x0, y1, z1, w0, u0, xd0, yd1, zd1, wd0, ud0), gradCoord5D(seed, x1, y1, z1, w0, u0, xd1, yd1, zd1, wd0, ud0), xs);
        final float xf0010 = lerp(gradCoord5D(seed, x0, y0, z0, w1, u0, xd0, yd0, zd0, wd1, ud0), gradCoord5D(seed, x1, y0, z0, w1, u0, xd1, yd0, zd0, wd1, ud0), xs);
        final float xf1010 = lerp(gradCoord5D(seed, x0, y1, z0, w1, u0, xd0, yd1, zd0, wd1, ud0), gradCoord5D(seed, x1, y1, z0, w1, u0, xd1, yd1, zd0, wd1, ud0), xs);
        final float xf0110 = lerp(gradCoord5D(seed, x0, y0, z1, w1, u0, xd0, yd0, zd1, wd1, ud0), gradCoord5D(seed, x1, y0, z1, w1, u0, xd1, yd0, zd1, wd1, ud0), xs);
        final float xf1110 = lerp(gradCoord5D(seed, x0, y1, z1, w1, u0, xd0, yd1, zd1, wd1, ud0), gradCoord5D(seed, x1, y1, z1, w1, u0, xd1, yd1, zd1, wd1, ud0), xs);
        final float xf0001 = lerp(gradCoord5D(seed, x0, y0, z0, w0, u1, xd0, yd0, zd0, wd0, ud1), gradCoord5D(seed, x1, y0, z0, w0, u1, xd1, yd0, zd0, wd0, ud1), xs);
        final float xf1001 = lerp(gradCoord5D(seed, x0, y1, z0, w0, u1, xd0, yd1, zd0, wd0, ud1), gradCoord5D(seed, x1, y1, z0, w0, u1, xd1, yd1, zd0, wd0, ud1), xs);
        final float xf0101 = lerp(gradCoord5D(seed, x0, y0, z1, w0, u1, xd0, yd0, zd1, wd0, ud1), gradCoord5D(seed, x1, y0, z1, w0, u1, xd1, yd0, zd1, wd0, ud1), xs);
        final float xf1101 = lerp(gradCoord5D(seed, x0, y1, z1, w0, u1, xd0, yd1, zd1, wd0, ud1), gradCoord5D(seed, x1, y1, z1, w0, u1, xd1, yd1, zd1, wd0, ud1), xs);
        final float xf0011 = lerp(gradCoord5D(seed, x0, y0, z0, w1, u1, xd0, yd0, zd0, wd1, ud1), gradCoord5D(seed, x1, y0, z0, w1, u1, xd1, yd0, zd0, wd1, ud1), xs);
        final float xf1011 = lerp(gradCoord5D(seed, x0, y1, z0, w1, u1, xd0, yd1, zd0, wd1, ud1), gradCoord5D(seed, x1, y1, z0, w1, u1, xd1, yd1, zd0, wd1, ud1), xs);
        final float xf0111 = lerp(gradCoord5D(seed, x0, y0, z1, w1, u1, xd0, yd0, zd1, wd1, ud1), gradCoord5D(seed, x1, y0, z1, w1, u1, xd1, yd0, zd1, wd1, ud1), xs);
        final float xf1111 = lerp(gradCoord5D(seed, x0, y1, z1, w1, u1, xd0, yd1, zd1, wd1, ud1), gradCoord5D(seed, x1, y1, z1, w1, u1, xd1, yd1, zd1, wd1, ud1), xs);

        final float yf000 = lerp(xf0000, xf1000, ys);
        final float yf100 = lerp(xf0100, xf1100, ys);
        final float yf010 = lerp(xf0010, xf1010, ys);
        final float yf110 = lerp(xf0110, xf1110, ys);
        final float yf001 = lerp(xf0001, xf1001, ys);
        final float yf101 = lerp(xf0101, xf1101, ys);
        final float yf011 = lerp(xf0011, xf1011, ys);
        final float yf111 = lerp(xf0111, xf1111, ys);

        final float zf00 = lerp(yf000, yf100, zs);
        final float zf10 = lerp(yf010, yf110, zs);
        final float zf01 = lerp(yf001, yf101, zs);
        final float zf11 = lerp(yf011, yf111, zs);

        final float wf0 = lerp(zf00, zf10, ws);
        final float wf1 = lerp(zf01, zf11, ws);

        return lerp(wf0, wf1, us) * 0.7777777f;
    }
    private float singlePerlinFractalFBM(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = singlePerlin(seed, x, y, z, w, u);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += singlePerlin(seed + i, x, y, z, w, u) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalBillow(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = Math.abs(singlePerlin(seed, x, y, z, w, u)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singlePerlin(seed + i, x, y, z, w, u)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalRidgedMulti(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singlePerlin(seed + i, x, y, z, w, u));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }









    public float getPerlin(float x, float y, float z, float w, float u, float v) {
        return singlePerlin(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency, v * frequency);
    }

    public float singlePerlin(int seed, float x, float y, float z, float w, float u, float v) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);
        int w0 = fastFloor(w);
        int u0 = fastFloor(u);
        int v0 = fastFloor(v);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;
        int w1 = w0 + 1;
        int u1 = u0 + 1;
        int v1 = v0 + 1;

        float xs, ys, zs, ws, us, vs;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                zs = z - z0;
                ws = w - w0;
                us = u - u0;
                vs = v - v0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                zs = hermiteInterpolator(z - z0);
                ws = hermiteInterpolator(w - w0);
                us = hermiteInterpolator(u - u0);
                vs = hermiteInterpolator(v - v0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                zs = quinticInterpolator(z - z0);
                ws = quinticInterpolator(w - w0);
                us = quinticInterpolator(u - u0);
                vs = quinticInterpolator(v - v0);
                break;
        }

        final float xd0 = x - x0;
        final float yd0 = y - y0;
        final float zd0 = z - z0;
        final float wd0 = w - w0;
        final float ud0 = u - u0;
        final float vd0 = v - v0;
        final float xd1 = xd0 - 1;
        final float yd1 = yd0 - 1;
        final float zd1 = zd0 - 1;
        final float wd1 = wd0 - 1;
        final float ud1 = ud0 - 1;
        final float vd1 = vd0 - 1;

        final float xf00000 = lerp(gradCoord6D(seed, x0, y0, z0, w0, u0, v0, xd0, yd0, zd0, wd0, ud0, vd0), gradCoord6D(seed, x1, y0, z0, w0, u0, v0, xd1, yd0, zd0, wd0, ud0, vd0), xs);
        final float xf10000 = lerp(gradCoord6D(seed, x0, y1, z0, w0, u0, v0, xd0, yd1, zd0, wd0, ud0, vd0), gradCoord6D(seed, x1, y1, z0, w0, u0, v0, xd1, yd1, zd0, wd0, ud0, vd0), xs);
        final float xf01000 = lerp(gradCoord6D(seed, x0, y0, z1, w0, u0, v0, xd0, yd0, zd1, wd0, ud0, vd0), gradCoord6D(seed, x1, y0, z1, w0, u0, v0, xd1, yd0, zd1, wd0, ud0, vd0), xs);
        final float xf11000 = lerp(gradCoord6D(seed, x0, y1, z1, w0, u0, v0, xd0, yd1, zd1, wd0, ud0, vd0), gradCoord6D(seed, x1, y1, z1, w0, u0, v0, xd1, yd1, zd1, wd0, ud0, vd0), xs);
        final float xf00100 = lerp(gradCoord6D(seed, x0, y0, z0, w1, u0, v0, xd0, yd0, zd0, wd1, ud0, vd0), gradCoord6D(seed, x1, y0, z0, w1, u0, v0, xd1, yd0, zd0, wd1, ud0, vd0), xs);
        final float xf10100 = lerp(gradCoord6D(seed, x0, y1, z0, w1, u0, v0, xd0, yd1, zd0, wd1, ud0, vd0), gradCoord6D(seed, x1, y1, z0, w1, u0, v0, xd1, yd1, zd0, wd1, ud0, vd0), xs);
        final float xf01100 = lerp(gradCoord6D(seed, x0, y0, z1, w1, u0, v0, xd0, yd0, zd1, wd1, ud0, vd0), gradCoord6D(seed, x1, y0, z1, w1, u0, v0, xd1, yd0, zd1, wd1, ud0, vd0), xs);
        final float xf11100 = lerp(gradCoord6D(seed, x0, y1, z1, w1, u0, v0, xd0, yd1, zd1, wd1, ud0, vd0), gradCoord6D(seed, x1, y1, z1, w1, u0, v0, xd1, yd1, zd1, wd1, ud0, vd0), xs);

        final float xf00010 = lerp(gradCoord6D(seed, x0, y0, z0, w0, u1, v0, xd0, yd0, zd0, wd0, ud1, vd0), gradCoord6D(seed, x1, y0, z0, w0, u1, v0, xd1, yd0, zd0, wd0, ud1, vd0), xs);
        final float xf10010 = lerp(gradCoord6D(seed, x0, y1, z0, w0, u1, v0, xd0, yd1, zd0, wd0, ud1, vd0), gradCoord6D(seed, x1, y1, z0, w0, u1, v0, xd1, yd1, zd0, wd0, ud1, vd0), xs);
        final float xf01010 = lerp(gradCoord6D(seed, x0, y0, z1, w0, u1, v0, xd0, yd0, zd1, wd0, ud1, vd0), gradCoord6D(seed, x1, y0, z1, w0, u1, v0, xd1, yd0, zd1, wd0, ud1, vd0), xs);
        final float xf11010 = lerp(gradCoord6D(seed, x0, y1, z1, w0, u1, v0, xd0, yd1, zd1, wd0, ud1, vd0), gradCoord6D(seed, x1, y1, z1, w0, u1, v0, xd1, yd1, zd1, wd0, ud1, vd0), xs);
        final float xf00110 = lerp(gradCoord6D(seed, x0, y0, z0, w1, u1, v0, xd0, yd0, zd0, wd1, ud1, vd0), gradCoord6D(seed, x1, y0, z0, w1, u1, v0, xd1, yd0, zd0, wd1, ud1, vd0), xs);
        final float xf10110 = lerp(gradCoord6D(seed, x0, y1, z0, w1, u1, v0, xd0, yd1, zd0, wd1, ud1, vd0), gradCoord6D(seed, x1, y1, z0, w1, u1, v0, xd1, yd1, zd0, wd1, ud1, vd0), xs);
        final float xf01110 = lerp(gradCoord6D(seed, x0, y0, z1, w1, u1, v0, xd0, yd0, zd1, wd1, ud1, vd0), gradCoord6D(seed, x1, y0, z1, w1, u1, v0, xd1, yd0, zd1, wd1, ud1, vd0), xs);
        final float xf11110 = lerp(gradCoord6D(seed, x0, y1, z1, w1, u1, v0, xd0, yd1, zd1, wd1, ud1, vd0), gradCoord6D(seed, x1, y1, z1, w1, u1, v0, xd1, yd1, zd1, wd1, ud1, vd0), xs);

        final float xf00001 = lerp(gradCoord6D(seed, x0, y0, z0, w0, u0, v1, xd0, yd0, zd0, wd0, ud0, vd1), gradCoord6D(seed, x1, y0, z0, w0, u0, v1, xd1, yd0, zd0, wd0, ud0, vd1), xs);
        final float xf10001 = lerp(gradCoord6D(seed, x0, y1, z0, w0, u0, v1, xd0, yd1, zd0, wd0, ud0, vd1), gradCoord6D(seed, x1, y1, z0, w0, u0, v1, xd1, yd1, zd0, wd0, ud0, vd1), xs);
        final float xf01001 = lerp(gradCoord6D(seed, x0, y0, z1, w0, u0, v1, xd0, yd0, zd1, wd0, ud0, vd1), gradCoord6D(seed, x1, y0, z1, w0, u0, v1, xd1, yd0, zd1, wd0, ud0, vd1), xs);
        final float xf11001 = lerp(gradCoord6D(seed, x0, y1, z1, w0, u0, v1, xd0, yd1, zd1, wd0, ud0, vd1), gradCoord6D(seed, x1, y1, z1, w0, u0, v1, xd1, yd1, zd1, wd0, ud0, vd1), xs);
        final float xf00101 = lerp(gradCoord6D(seed, x0, y0, z0, w1, u0, v1, xd0, yd0, zd0, wd1, ud0, vd1), gradCoord6D(seed, x1, y0, z0, w1, u0, v1, xd1, yd0, zd0, wd1, ud0, vd1), xs);
        final float xf10101 = lerp(gradCoord6D(seed, x0, y1, z0, w1, u0, v1, xd0, yd1, zd0, wd1, ud0, vd1), gradCoord6D(seed, x1, y1, z0, w1, u0, v1, xd1, yd1, zd0, wd1, ud0, vd1), xs);
        final float xf01101 = lerp(gradCoord6D(seed, x0, y0, z1, w1, u0, v1, xd0, yd0, zd1, wd1, ud0, vd1), gradCoord6D(seed, x1, y0, z1, w1, u0, v1, xd1, yd0, zd1, wd1, ud0, vd1), xs);
        final float xf11101 = lerp(gradCoord6D(seed, x0, y1, z1, w1, u0, v1, xd0, yd1, zd1, wd1, ud0, vd1), gradCoord6D(seed, x1, y1, z1, w1, u0, v1, xd1, yd1, zd1, wd1, ud0, vd1), xs);

        final float xf00011 = lerp(gradCoord6D(seed, x0, y0, z0, w0, u1, v1, xd0, yd0, zd0, wd0, ud1, vd1), gradCoord6D(seed, x1, y0, z0, w0, u1, v1, xd1, yd0, zd0, wd0, ud1, vd1), xs);
        final float xf10011 = lerp(gradCoord6D(seed, x0, y1, z0, w0, u1, v1, xd0, yd1, zd0, wd0, ud1, vd1), gradCoord6D(seed, x1, y1, z0, w0, u1, v1, xd1, yd1, zd0, wd0, ud1, vd1), xs);
        final float xf01011 = lerp(gradCoord6D(seed, x0, y0, z1, w0, u1, v1, xd0, yd0, zd1, wd0, ud1, vd1), gradCoord6D(seed, x1, y0, z1, w0, u1, v1, xd1, yd0, zd1, wd0, ud1, vd1), xs);
        final float xf11011 = lerp(gradCoord6D(seed, x0, y1, z1, w0, u1, v1, xd0, yd1, zd1, wd0, ud1, vd1), gradCoord6D(seed, x1, y1, z1, w0, u1, v1, xd1, yd1, zd1, wd0, ud1, vd1), xs);
        final float xf00111 = lerp(gradCoord6D(seed, x0, y0, z0, w1, u1, v1, xd0, yd0, zd0, wd1, ud1, vd1), gradCoord6D(seed, x1, y0, z0, w1, u1, v1, xd1, yd0, zd0, wd1, ud1, vd1), xs);
        final float xf10111 = lerp(gradCoord6D(seed, x0, y1, z0, w1, u1, v1, xd0, yd1, zd0, wd1, ud1, vd1), gradCoord6D(seed, x1, y1, z0, w1, u1, v1, xd1, yd1, zd0, wd1, ud1, vd1), xs);
        final float xf01111 = lerp(gradCoord6D(seed, x0, y0, z1, w1, u1, v1, xd0, yd0, zd1, wd1, ud1, vd1), gradCoord6D(seed, x1, y0, z1, w1, u1, v1, xd1, yd0, zd1, wd1, ud1, vd1), xs);
        final float xf11111 = lerp(gradCoord6D(seed, x0, y1, z1, w1, u1, v1, xd0, yd1, zd1, wd1, ud1, vd1), gradCoord6D(seed, x1, y1, z1, w1, u1, v1, xd1, yd1, zd1, wd1, ud1, vd1), xs);

        final float yf0000 = lerp(xf00000, xf10000, ys);
        final float yf1000 = lerp(xf01000, xf11000, ys);
        final float yf0100 = lerp(xf00100, xf10100, ys);
        final float yf1100 = lerp(xf01100, xf11100, ys);

        final float yf0010 = lerp(xf00010, xf10010, ys);
        final float yf1010 = lerp(xf01010, xf11010, ys);
        final float yf0110 = lerp(xf00110, xf10110, ys);
        final float yf1110 = lerp(xf01110, xf11110, ys);

        final float yf0001 = lerp(xf00001, xf10001, ys);
        final float yf1001 = lerp(xf01001, xf11001, ys);
        final float yf0101 = lerp(xf00101, xf10101, ys);
        final float yf1101 = lerp(xf01101, xf11101, ys);

        final float yf0011 = lerp(xf00011, xf10011, ys);
        final float yf1011 = lerp(xf01011, xf11011, ys);
        final float yf0111 = lerp(xf00111, xf10111, ys);
        final float yf1111 = lerp(xf01111, xf11111, ys);

        final float zf000 = lerp(yf0000, yf1000, zs);
        final float zf100 = lerp(yf0100, yf1100, zs);

        final float zf010 = lerp(yf0010, yf1010, zs);
        final float zf110 = lerp(yf0110, yf1110, zs);

        final float zf001 = lerp(yf0001, yf1001, zs);
        final float zf101 = lerp(yf0101, yf1101, zs);

        final float zf011 = lerp(yf0011, yf1011, zs);
        final float zf111 = lerp(yf0111, yf1111, zs);

        final float wf00 = lerp(zf000, zf100, ws);
        final float wf10 = lerp(zf010, zf110, ws);
        final float wf01 = lerp(zf001, zf101, ws);
        final float wf11 = lerp(zf011, zf111, ws);

        final float uf0 = lerp(wf00, wf10, us);
        final float uf1 = lerp(wf01, wf11, us);

        return lerp(uf0, uf1, vs) * 1.61f;
    }
    private float singlePerlinFractalFBM(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = singlePerlin(seed, x, y, z, w, u, v);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += singlePerlin(++seed, x, y, z, w, u, v) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalBillow(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = Math.abs(singlePerlin(seed, x, y, z, w, u, v)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singlePerlin(++seed, x, y, z, w, u, v)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singlePerlinFractalRidgedMulti(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singlePerlin(seed + i, x, y, z, w, u, v));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    // Simplex Noise
    public float getSimplexFractal(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (fractalType) {
            case FBM:
                return singleSimplexFractalFBM(x, y);
            case BILLOW:
                return singleSimplexFractalBillow(x, y);
            case RIDGED_MULTI:
                return singleSimplexFractalRidgedMulti(x, y);
            default:
                return 0;
        }
    }

    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and default frequency (0.03125), lacunarity
     * (2) and gain (0.5) in 2D.
     * @param x
     * @param y
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float layered2D(float x, float y, int seed, int octaves)
    {
        x *= 0.03125f;
        y *= 0.03125f;

        float sum = 1 - Math.abs(singleSimplex(seed, x, y));
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;

            amp *= 0.5f;
            sum -= (1 - Math.abs(singleSimplex(seed + i, x, y))) * amp;
        }
        amp = gain;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        return sum / ampFractal;
    }
    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and default frequency (0.03125), lacunarity
     * (2) and gain (0.5) in 2D.
     * @param x
     * @param y
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float layered2D(float x, float y, int seed, int octaves, float frequency)
    {
        x *= frequency;
        y *= frequency;

        float sum = singleSimplex(seed, x, y);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;

            amp *= 0.5f;
            sum += singleSimplex(seed + i, x, y) * amp;
        }
        amp = gain;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        return sum / ampFractal;
    }
    /**
     * Generates layered simplex noise with the given amount of octaves and specified lacunarity (the amount of
     * frequency change between octaves) and gain (0.5) in D.
     * @param x
     * @param y
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float layered2D(float x, float y, int seed, int octaves, float frequency, float lacunarity)
    {
        x *= frequency;
        y *= frequency;

        float sum = singleSimplex(seed, x, y);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= 0.5f;
            sum += singleSimplex(seed + i, x, y) * amp;
        }
        amp = gain;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        return sum / ampFractal;
    }

    /**
     * Generates layered simplex noise with the given amount of octaves and specified lacunarity (the amount of
     * frequency change between octaves) and gain (loosely, how much to emphasize lower-frequency octaves) in 2D.
     * @param x
     * @param y
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float layered2D(float x, float y, int seed, int octaves, float frequency, float lacunarity, float gain)
    {
        x *= frequency;
        y *= frequency;

        float sum = singleSimplex(seed, x, y);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += singleSimplex(seed + i, x, y) * amp;
        }
        amp = gain;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        return sum / ampFractal;
    }

    private float singleSimplexFractalFBM(float x, float y) {
        int seed = this.seed;
        float sum = singleSimplex(seed, x, y);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += singleSimplex(seed + i, x, y) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleSimplexFractalBillow(float x, float y) {
        int seed = this.seed;
        float sum = Math.abs(singleSimplex(seed, x, y)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleSimplex(++seed, x, y)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and default frequency (0.03125), lacunarity
     * (2) and gain (0.5).
     * @param x
     * @param y
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float ridged2D(float x, float y, int seed, int octaves)
    {
        return ridged2D(x, y, seed, octaves, 0.03125f, 2f);
    }
    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and default frequency (0.03125), lacunarity
     * (2) and gain (0.5).
     * @param x
     * @param y
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float ridged2D(float x, float y, int seed, int octaves, float frequency)
    {
        return ridged2D(x, y, seed, octaves, frequency, 2f);
    }
    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and specified lacunarity (the amount of
     * frequency change between octaves); gain is not used.
     * @param x
     * @param y
     * @param seed any int
     * @param octaves how many "layers of detail" to generate; at least 1, but note this slows down with many octaves
     * @param frequency often about {@code 1f / 32f}, but generally adjusted for the use case
     * @param lacunarity when {@code octaves} is 2 or more, this affects the change between layers
     * @return noise as a float between -1f and 1f
     */
    public float ridged2D(float x, float y, int seed, int octaves, float frequency, float lacunarity)
    {
        x *= frequency;
        y *= frequency;

        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleSimplex(seed + i, x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    private float singleSimplexFractalRidgedMulti(float x, float y) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleSimplex(seed + i, x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getSimplex(float x, float y) {
        return singleSimplex(seed, x * frequency, y * frequency);
    }

    public float singleSimplex(int seed, float x, float y) {
        float t = (x + y) * F2f;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);

        t = (i + j) * G2f;
        float X0 = i - t;
        float Y0 = j - t;

        float x0 = x - X0;
        float y0 = y - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        float x1 = x0 - i1 + G2f;
        float y1 = y0 - j1 + G2f;
        float x2 = x0 - 1 + H2f;
        float y2 = y0 - 1 + H2f;

        float n = 0f;

        t = 0.5f - x0 * x0 - y0 * y0;
        if (t >= 0) {
            t *= t;
            n += t * t * gradCoord2D(seed, i, j, x0, y0);
        }

        t = 0.5f - x1 * x1 - y1 * y1;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord2D(seed, i + i1, j + j1, x1, y1);
        }

        t = 0.5f - x2 * x2 - y2 * y2;
        if (t > 0)  {
            t *= t;
            n += t * t * gradCoord2D(seed, i + 1, j + 1, x2, y2);
        }
        return n * 99.20689070704672f; // this is 99.83685446303647 / 1.00635 ; the first number was found by kdotjpg
    }

    public float getSimplexFractal(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (fractalType) {
            case FBM:
                return singleSimplexFractalFBM(x, y, z);
            case BILLOW:
                return singleSimplexFractalBillow(x, y, z);
            case RIDGED_MULTI:
                return singleSimplexFractalRidgedMulti(x, y, z);
            default:
                return 0;
        }
    }

    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and default frequency (0.03125), lacunarity
     * (2) and gain (0.5) in 3D.
     * @param x
     * @param y
     * @param z
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float layered3D(float x, float y, float z, int seed, int octaves)
    {
        x *= 0.03125f;
        y *= 0.03125f;
        z *= 0.03125f;

        float sum = 1 - Math.abs(singleSimplex(seed, x, y, z));
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;

            amp *= 0.5f;
            sum -= (1 - Math.abs(singleSimplex(seed + i, x, y, z))) * amp;
        }
        amp = 0.5f;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= 0.5f;
        }
        return sum / ampFractal;
    }
    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and default frequency (0.03125), lacunarity
     * (2) and gain (0.5) in 3D.
     * @param x
     * @param y
     * @param z
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float layered3D(float x, float y, float z, int seed, int octaves, float frequency)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        float sum = singleSimplex(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;

            amp *= 0.5f;
            sum += singleSimplex(seed + i, x, y, z) * amp;
        }
        amp = 0.5f;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= 0.5f;
        }
        return sum / ampFractal;
    }
    /**
     * Generates layered simplex noise with the given amount of octaves and specified lacunarity (the amount of
     * frequency change between octaves) and gain (0.5) in 3D.
     * @param x
     * @param y
     * @param z
     * @param seed
     * @param octaves
     * @param frequency
     * @param lacunarity
     * @return noise as a float between -1f and 1f
     */
    public float layered3D(float x, float y, float z, int seed, int octaves, float frequency, float lacunarity)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        float sum = singleSimplex(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= 0.5f;
            sum += singleSimplex(seed + i, x, y, z) * amp;
        }
        amp = 0.5f;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= 0.5f;
        }
        return sum / ampFractal;
    }

    /**
     * Generates layered simplex noise with the given amount of octaves and specified lacunarity (the amount of
     * frequency change between octaves) and gain (loosely, how much to emphasize lower-frequency octaves) in 3D.
     * @param x
     * @param y
     * @param z
     * @param seed
     * @param octaves
     * @param frequency
     * @param lacunarity
     * @param gain
     * @return noise as a float between -1f and 1f
     */
    public float layered3D(float x, float y, float z, int seed, int octaves, float frequency, float lacunarity, float gain)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        float sum = singleSimplex(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += singleSimplex(seed + i, x, y, z) * amp;
        }
        amp = gain;
        float ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        return sum / ampFractal;
    }

    private float singleSimplexFractalFBM(float x, float y, float z) {
        int seed = this.seed;
        float sum = singleSimplex(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += singleSimplex(seed + i, x, y, z) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleSimplexFractalBillow(float x, float y, float z) {
        int seed = this.seed;
        float sum = Math.abs(singleSimplex(seed, x, y, z)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleSimplex(seed + i, x, y, z)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and default frequency (0.03125), lacunarity
     * (2) and gain (0.5).
     * @param x
     * @param y
     * @param z
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float ridged3D(float x, float y, float z, int seed, int octaves)
    {
        return ridged3D(x, y, z, seed, octaves, 0.03125f, 2f);
    }
    /**
     * Generates ridged-multi simplex noise with the given amount of octaves, specified frequency, and the default
     * lacunarity (2) and gain (0.5).
     * @param x
     * @param y
     * @param z
     * @param seed
     * @param octaves
     * @return noise as a float between -1f and 1f
     */
    public float ridged3D(float x, float y, float z, int seed, int octaves, float frequency)
    {
        return ridged3D(x, y, z, seed, octaves, frequency, 2f);
    }
    /**
     * Generates ridged-multi simplex noise with the given amount of octaves and specified lacunarity (the amount of
     * frequency change between octaves); gain is not used.
     * @param x
     * @param y
     * @param z
     * @param seed any int
     * @param octaves how many "layers of detail" to generate; at least 1, but note this slows down with many octaves
     * @param frequency often about {@code 1f / 32f}, but generally adjusted for the use case
     * @param lacunarity when {@code octaves} is 2 or more, this affects the change between layers
     * @return noise as a float between -1f and 1f
     */
    public float ridged3D(float x, float y, float z, int seed, int octaves, float frequency, float lacunarity)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleSimplex(seed + i, x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    private float singleSimplexFractalRidgedMulti(float x, float y, float z) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleSimplex(seed + i, x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getSimplex(float x, float y, float z) {
        return singleSimplex(seed, x * frequency, y * frequency, z * frequency);
    }

    public float singleSimplex(int seed, float x, float y, float z) {
        float t = (x + y + z) * F3f;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);

        t = (i + j + k) * G3f;
        float x0 = x - (i - t);
        float y0 = y - (j - t);
        float z0 = z - (k - t);

        int i1, j1, k1;
        int i2, j2, k2;

        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else // x0 < z0
            {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else // x0 < y0
        {
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else // x0 >= z0
            {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }

        float x1 = x0 - i1 + G3f;
        float y1 = y0 - j1 + G3f;
        float z1 = z0 - k1 + G3f;
        float x2 = x0 - i2 + F3f;
        float y2 = y0 - j2 + F3f;
        float z2 = z0 - k2 + F3f;
        float x3 = x0 - 0.5f;
        float y3 = y0 - 0.5f;
        float z3 = z0 - 0.5f;

        float n = 0;

        t = 0.6f - x0 * x0 - y0 * y0 - z0 * z0;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord3D(seed, i, j, k, x0, y0, z0);
        }

        t = 0.6f - x1 * x1 - y1 * y1 - z1 * z1;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord3D(seed, i + i1, j + j1, k + k1, x1, y1, z1);
        }

        t = 0.6f - x2 * x2 - y2 * y2 - z2 * z2;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord3D(seed, i + i2, j + j2, k + k2, x2, y2, z2);
        }

        t = 0.6f - x3 * x3 - y3 * y3 - z3 * z3;
        if (t > 0)  {
            t *= t;
            n += t * t * gradCoord3D(seed, i + 1, j + 1, k + 1, x3, y3, z3);
        }
        return 31.5f * n;
    }

    public float getSimplex(float x, float y, float z, float w) {
        return singleSimplex(seed, x * frequency, y * frequency, z * frequency, w * frequency);
    }

    public float singleSimplex(int seed, float x, float y, float z, float w) {
        float skew = (x + y + z + w) * F4f;
        int i = fastFloor(x + skew);
        int j = fastFloor(y + skew);
        int k = fastFloor(z + skew);
        int l = fastFloor(w + skew);
        float unskew = (i + j + k + l) * G4f;
        float X0 = i - unskew;
        float Y0 = j - unskew;
        float Z0 = k - unskew;
        float W0 = l - unskew;
        float x0 = x - X0;
        float y0 = y - Y0;
        float z0 = z - Z0;
        float w0 = w - W0;

        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;

        if (x0 > y0) rankx++; else ranky++;
        if (x0 > z0) rankx++; else rankz++;
        if (x0 > w0) rankx++; else rankw++;

        if (y0 > z0) ranky++; else rankz++;
        if (y0 > w0) ranky++; else rankw++;

        if (z0 > w0) rankz++; else rankw++;

        int i1 = 2 - rankx >>> 31;
        int j1 = 2 - ranky >>> 31;
        int k1 = 2 - rankz >>> 31;
        int l1 = 2 - rankw >>> 31;

        int i2 = 1 - rankx >>> 31;
        int j2 = 1 - ranky >>> 31;
        int k2 = 1 - rankz >>> 31;
        int l2 = 1 - rankw >>> 31;

        int i3 = -rankx >>> 31;
        int j3 = -ranky >>> 31;
        int k3 = -rankz >>> 31;
        int l3 = -rankw >>> 31;

        float x1 = x0 - i1 + G4f;
        float y1 = y0 - j1 + G4f;
        float z1 = z0 - k1 + G4f;
        float w1 = w0 - l1 + G4f;

        float x2 = x0 - i2 + 2 * G4f;
        float y2 = y0 - j2 + 2 * G4f;
        float z2 = z0 - k2 + 2 * G4f;
        float w2 = w0 - l2 + 2 * G4f;

        float x3 = x0 - i3 + 3 * G4f;
        float y3 = y0 - j3 + 3 * G4f;
        float z3 = z0 - k3 + 3 * G4f;
        float w3 = w0 - l3 + 3 * G4f;

        float x4 = x0 - 1 + 4 * G4f;
        float y4 = y0 - 1 + 4 * G4f;
        float z4 = z0 - 1 + 4 * G4f;
        float w4 = w0 - 1 + 4 * G4f;

        float t0 = LIMIT4 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        if(t0 > 0) {
            t0 *= t0;
            t0 *= t0 * gradCoord4D(seed, i, j, k, l, x0, y0, z0, w0);
        }
        else t0 = 0;
        float t1 = LIMIT4 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        if (t1 > 0) {
            t1 *= t1;
            t1 *= t1 * gradCoord4D(seed, i + i1, j + j1, k + k1, l + l1, x1, y1, z1, w1);
        }
        else t1 = 0;
        float t2 = LIMIT4 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        if (t2 > 0) {
            t2 *= t2;
            t2 *= t2 * gradCoord4D(seed, i + i2, j + j2, k + k2, l + l2, x2, y2, z2, w2);
        }
        else t2 = 0;
        float t3 = LIMIT4 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        if (t3 > 0) {
            t3 *= t3;
            t3 *= t3 * gradCoord4D(seed, i + i3, j + j3, k + k3, l + l3, x3, y3, z3, w3);
        }
        else t3 = 0;
        float t4 = LIMIT4 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        if (t4 > 0) {
            t4 *= t4;
            t4 *= t4 * gradCoord4D(seed, i + 1, j + 1, k + 1, l + 1, x4, y4, z4, w4);
        }
        else t4 = 0;

        return (t0 + t1 + t2 + t3 + t4) * 14.7279f;
    }

    // Simplex Noise
    public float getSimplexFractal(float x, float y, float z, float w) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;

        switch (fractalType) {
            case FBM:
                return singleSimplexFractalFBM(x, y, z, w);
            case BILLOW:
                return singleSimplexFractalBillow(x, y, z, w);
            case RIDGED_MULTI:
                return singleSimplexFractalRidgedMulti(x, y, z, w);
            default:
                return 0;
        }
    }

    private float singleSimplexFractalFBM(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = singleSimplex(seed, x, y, z, w);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += singleSimplex(seed + i, x, y, z, w) * amp;
        }

        return sum * fractalBounding;
    }
    private float singleSimplexFractalRidgedMulti(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleSimplex(seed + i, x, y, z, w));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    private float singleSimplexFractalBillow(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = Math.abs(singleSimplex(seed, x, y, z, w)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleSimplex(seed + i, x, y, z, w)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    // 5D Simplex

    public float singleSimplex(int seed, float x, float y, float z, float w, float u) {
        float n0, n1, n2, n3, n4, n5;
        float t = (x + y + z + w + u) * F5f;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);
        int l = fastFloor(w + t);
        int h = fastFloor(u + t);
        t = (i + j + k + l + h) * G5f;
        float X0 = i - t;
        float Y0 = j - t;
        float Z0 = k - t;
        float W0 = l - t;
        float U0 = h - t;
        float x0 = x - X0;
        float y0 = y - Y0;
        float z0 = z - Z0;
        float w0 = w - W0;
        float u0 = u - U0;

        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        int ranku = 0;

        if (x0 > y0) rankx++; else ranky++;
        if (x0 > z0) rankx++; else rankz++;
        if (x0 > w0) rankx++; else rankw++;
        if (x0 > u0) rankx++; else ranku++;

        if (y0 > z0) ranky++; else rankz++;
        if (y0 > w0) ranky++; else rankw++;
        if (y0 > u0) ranky++; else ranku++;

        if (z0 > w0) rankz++; else rankw++;
        if (z0 > u0) rankz++; else ranku++;

        if (w0 > u0) rankw++; else ranku++;

        int i1 = 3 - rankx >>> 31;
        int j1 = 3 - ranky >>> 31;
        int k1 = 3 - rankz >>> 31;
        int l1 = 3 - rankw >>> 31;
        int h1 = 3 - ranku >>> 31;

        int i2 = 2 - rankx >>> 31;
        int j2 = 2 - ranky >>> 31;
        int k2 = 2 - rankz >>> 31;
        int l2 = 2 - rankw >>> 31;
        int h2 = 2 - ranku >>> 31;

        int i3 = 1 - rankx >>> 31;
        int j3 = 1 - ranky >>> 31;
        int k3 = 1 - rankz >>> 31;
        int l3 = 1 - rankw >>> 31;
        int h3 = 1 - ranku >>> 31;

        int i4 = -rankx >>> 31;
        int j4 = -ranky >>> 31;
        int k4 = -rankz >>> 31;
        int l4 = -rankw >>> 31;
        int h4 = -ranku >>> 31;

        float x1 = x0 - i1 + G5f;
        float y1 = y0 - j1 + G5f;
        float z1 = z0 - k1 + G5f;
        float w1 = w0 - l1 + G5f;
        float u1 = u0 - h1 + G5f;

        float x2 = x0 - i2 + 2 * G5f;
        float y2 = y0 - j2 + 2 * G5f;
        float z2 = z0 - k2 + 2 * G5f;
        float w2 = w0 - l2 + 2 * G5f;
        float u2 = u0 - h2 + 2 * G5f;

        float x3 = x0 - i3 + 3 * G5f;
        float y3 = y0 - j3 + 3 * G5f;
        float z3 = z0 - k3 + 3 * G5f;
        float w3 = w0 - l3 + 3 * G5f;
        float u3 = u0 - h3 + 3 * G5f;

        float x4 = x0 - i4 + 4 * G5f;
        float y4 = y0 - j4 + 4 * G5f;
        float z4 = z0 - k4 + 4 * G5f;
        float w4 = w0 - l4 + 4 * G5f;
        float u4 = u0 - h4 + 4 * G5f;

        float x5 = x0 - 1 + 5 * G5f;
        float y5 = y0 - 1 + 5 * G5f;
        float z5 = z0 - 1 + 5 * G5f;
        float w5 = w0 - 1 + 5 * G5f;
        float u5 = u0 - 1 + 5 * G5f;

        t = LIMIT5 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0 - u0 * u0;
        if (t < 0) n0 = 0;
        else
        {
            t *= t;
            n0 = t * t * gradCoord5D(seed, i, j, k, l, h, x0, y0, z0, w0, u0);
        }

        t = LIMIT5 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1 - u1 * u1;
        if (t < 0) n1 = 0;
        else
        {
            t *= t;
            n1 = t * t * gradCoord5D(seed, i + i1, j + j1, k + k1, l + l1, h + h1, x1, y1, z1, w1, u1);
        }

        t = LIMIT5 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2 - u2 * u2;
        if (t < 0) n2 = 0;
        else
        {
            t *= t;
            n2 = t * t * gradCoord5D(seed, i + i2, j + j2, k + k2, l + l2, h + h2, x2, y2, z2, w2, u2);
        }

        t = LIMIT5 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3 - u3 * u3;
        if (t < 0) n3 = 0;
        else
        {
            t *= t;
            n3 = t * t * gradCoord5D(seed, i + i3, j + j3, k + k3, l + l3, h + h3, x3, y3, z3, w3, u3);
        }

        t = LIMIT5 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4 - u4 * u4;
        if (t < 0) n4 = 0;
        else
        {
            t *= t;
            n4 = t * t * gradCoord5D(seed, i + i4, j + j4, k + k4, l + l4, h + h4, x4, y4, z4, w4, u4);
        }

        t = LIMIT5 - x5 * x5 - y5 * y5 - z5 * z5 - w5 * w5 - u5 * u5;
        if (t < 0) n5 = 0;
        else
        {
            t *= t;
            n5 = t * t * gradCoord5D(seed, i + 1, j + 1, k + 1, l + 1, h + 1, x5, y5, z5, w5, u5);
        }

        return  (n0 + n1 + n2 + n3 + n4 + n5) * 10f;
    }

    public float getSimplex(float x, float y, float z, float w, float u) {
        return singleSimplex(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency);
    }
    // Simplex Noise
    public float getSimplexFractal(float x, float y, float z, float w, float u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;

        switch (fractalType) {
            case FBM:
                return singleSimplexFractalFBM(x, y, z, w, u);
            case BILLOW:
                return singleSimplexFractalBillow(x, y, z, w, u);
            case RIDGED_MULTI:
                return singleSimplexFractalRidgedMulti(x, y, z, w, u);
            default:
                return 0;
        }
    }

    private float singleSimplexFractalFBM(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = singleSimplex(seed, x, y, z, w, u);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += singleSimplex(seed + i, x, y, z, w, u) * amp;
        }

        return sum * fractalBounding;
    }
    private float singleSimplexFractalRidgedMulti(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleSimplex(seed + i, x, y, z, w, u));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    private float singleSimplexFractalBillow(float x, float y, float z, float w, float u) {
        int seed = this.seed;
        float sum = Math.abs(singleSimplex(seed, x, y, z, w, u)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleSimplex(seed + i, x, y, z, w, u)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }


    // 6D Simplex

    public float singleSimplex(int seed, float x, float y, float z, float w, float u, float v) {
        float n0, n1, n2, n3, n4, n5, n6;
        float t = (x + y + z + w + u + v) * F6f;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);
        int l = fastFloor(w + t);
        int h = fastFloor(u + t);
        int g = fastFloor(v + t);
        t = (i + j + k + l + h + g) * G6f;
        float X0 = i - t;
        float Y0 = j - t;
        float Z0 = k - t;
        float W0 = l - t;
        float U0 = h - t;
        float V0 = g - t;
        float x0 = x - X0;
        float y0 = y - Y0;
        float z0 = z - Z0;
        float w0 = w - W0;
        float u0 = u - U0;
        float v0 = v - V0;

        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        int ranku = 0;
        int rankv = 0;

        if (x0 > y0) rankx++; else ranky++;
        if (x0 > z0) rankx++; else rankz++;
        if (x0 > w0) rankx++; else rankw++;
        if (x0 > u0) rankx++; else ranku++;
        if (x0 > v0) rankx++; else rankv++;

        if (y0 > z0) ranky++; else rankz++;
        if (y0 > w0) ranky++; else rankw++;
        if (y0 > u0) ranky++; else ranku++;
        if (y0 > v0) ranky++; else rankv++;

        if (z0 > w0) rankz++; else rankw++;
        if (z0 > u0) rankz++; else ranku++;
        if (z0 > v0) rankz++; else rankv++;

        if (w0 > u0) rankw++; else ranku++;
        if (w0 > v0) rankw++; else rankv++;

        if (u0 > v0) ranku++; else rankv++;

        int i1 = 4 - rankx >>> 31;
        int j1 = 4 - ranky >>> 31;
        int k1 = 4 - rankz >>> 31;
        int l1 = 4 - rankw >>> 31;
        int h1 = 4 - ranku >>> 31;
        int g1 = 4 - rankv >>> 31;

        int i2 = 3 - rankx >>> 31;
        int j2 = 3 - ranky >>> 31;
        int k2 = 3 - rankz >>> 31;
        int l2 = 3 - rankw >>> 31;
        int h2 = 3 - ranku >>> 31;
        int g2 = 3 - rankv >>> 31;

        int i3 = 2 - rankx >>> 31;
        int j3 = 2 - ranky >>> 31;
        int k3 = 2 - rankz >>> 31;
        int l3 = 2 - rankw >>> 31;
        int h3 = 2 - ranku >>> 31;
        int g3 = 2 - rankv >>> 31;

        int i4 = 1 - rankx >>> 31;
        int j4 = 1 - ranky >>> 31;
        int k4 = 1 - rankz >>> 31;
        int l4 = 1 - rankw >>> 31;
        int h4 = 1 - ranku >>> 31;
        int g4 = 1 - rankv >>> 31;

        int i5 = -rankx >>> 31;
        int j5 = -ranky >>> 31;
        int k5 = -rankz >>> 31;
        int l5 = -rankw >>> 31;
        int h5 = -ranku >>> 31;
        int g5 = -rankv >>> 31;

        float x1 = x0 - i1 + G6f;
        float y1 = y0 - j1 + G6f;
        float z1 = z0 - k1 + G6f;
        float w1 = w0 - l1 + G6f;
        float u1 = u0 - h1 + G6f;
        float v1 = v0 - g1 + G6f;

        float x2 = x0 - i2 + 2 * G6f;
        float y2 = y0 - j2 + 2 * G6f;
        float z2 = z0 - k2 + 2 * G6f;
        float w2 = w0 - l2 + 2 * G6f;
        float u2 = u0 - h2 + 2 * G6f;
        float v2 = v0 - g2 + 2 * G6f;

        float x3 = x0 - i3 + 3 * G6f;
        float y3 = y0 - j3 + 3 * G6f;
        float z3 = z0 - k3 + 3 * G6f;
        float w3 = w0 - l3 + 3 * G6f;
        float u3 = u0 - h3 + 3 * G6f;
        float v3 = v0 - g3 + 3 * G6f;

        float x4 = x0 - i4 + 4 * G6f;
        float y4 = y0 - j4 + 4 * G6f;
        float z4 = z0 - k4 + 4 * G6f;
        float w4 = w0 - l4 + 4 * G6f;
        float u4 = u0 - h4 + 4 * G6f;
        float v4 = v0 - g4 + 4 * G6f;

        float x5 = x0 - i5 + 5 * G6f;
        float y5 = y0 - j5 + 5 * G6f;
        float z5 = z0 - k5 + 5 * G6f;
        float w5 = w0 - l5 + 5 * G6f;
        float u5 = u0 - h5 + 5 * G6f;
        float v5 = v0 - g5 + 5 * G6f;

        float x6 = x0 - 1 + 6 * G6f;
        float y6 = y0 - 1 + 6 * G6f;
        float z6 = z0 - 1 + 6 * G6f;
        float w6 = w0 - 1 + 6 * G6f;
        float u6 = u0 - 1 + 6 * G6f;
        float v6 = v0 - 1 + 6 * G6f;

        n0 = LIMIT6 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0 - u0 * u0 - v0 * v0;
        if (n0 <= 0f) n0 = 0f;
        else
        {
            n0 *= n0;
            n0 *= n0 * gradCoord6D(seed, i, j, k, l, h, g, x0, y0, z0, w0, u0, v0);
        }

        n1 = LIMIT6 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1 - u1 * u1 - v1 * v1;
        if (n1 <= 0f) n1 = 0f;
        else
        {
            n1 *= n1;
            n1 *= n1 * gradCoord6D(seed, i + i1, j + j1, k + k1, l + l1, h + h1, g + g1, x1, y1, z1, w1, u1, v1);
        }

        n2 = LIMIT6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2 - u2 * u2 - v2 * v2;
        if (n2 <= 0f) n2 = 0f;
        else
        {
            n2 *= n2;
            n2 *= n2 * gradCoord6D(seed, i + i2, j + j2, k + k2, l + l2, h + h2, g + g2, x2, y2, z2, w2, u2, v2);
        }

        n3 = LIMIT6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3 - u3 * u3 - v3 * v3;
        if (n3 <= 0f) n3 = 0f;
        else
        {
            n3 *= n3;
            n3 *= n3 * gradCoord6D(seed, i + i3, j + j3, k + k3, l + l3, h + h3, g + g3, x3, y3, z3, w3, u3, v3);
        }

        n4 = LIMIT6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4 - u4 * u4 - v4 * v4;
        if (n4 <= 0f) n4 = 0f;
        else
        {
            n4 *= n4;
            n4 *= n4 * gradCoord6D(seed, i + i4, j + j4, k + k4, l + l4, h + h4, g + g4, x4, y4, z4, w4, u4, v4);
        }

        n5 = LIMIT6 - x5 * x5 - y5 * y5 - z5 * z5 - w5 * w5 - u5 * u5 - v5 * v5;
        if (n5 <= 0f) n5 = 0f;
        else
        {
            n5 *= n5;
            n5 *= n5 * gradCoord6D(seed, i + i5, j + j5, k + k5, l + l5, h + h5, g + g5, x5, y5, z5, w5, u5, v5);
        }

        n6 = LIMIT6 - x6 * x6 - y6 * y6 - z6 * z6 - w6 * w6 - u6 * u6 - v6 * v6;
        if (n6 <= 0f) n6 = 0f;
        else
        {
            n6 *= n6;
            n6 *= n6 * gradCoord6D(seed, i + 1, j + 1, k + 1, l + 1, h + 1, g + 1, x6, y6, z6, w6, u6, v6);
        }

        return (n0 + n1 + n2 + n3 + n4 + n5 + n6) * 7.499f;
    }

    public float getSimplex(float x, float y, float z, float w, float u, float v) {
        return singleSimplex(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency, v * frequency);
    }
    // Simplex Noise
    public float getSimplexFractal(float x, float y, float z, float w, float u, float v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        switch (fractalType) {
            case FBM:
                return singleSimplexFractalFBM(x, y, z, w, u, v);
            case BILLOW:
                return singleSimplexFractalBillow(x, y, z, w, u, v);
            case RIDGED_MULTI:
                return singleSimplexFractalRidgedMulti(x, y, z, w, u, v);
            default:
                return 0;
        }
    }

    private float singleSimplexFractalFBM(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = singleSimplex(seed, x, y, z, w, u, v);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += singleSimplex(seed + i, x, y, z, w, u, v) * amp;
        }

        return sum * fractalBounding;
    }
    private float singleSimplexFractalRidgedMulti(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleSimplex(seed + i, x, y, z, w, u, v));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    private float singleSimplexFractalBillow(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = Math.abs(singleSimplex(seed, x, y, z, w, u, v)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleSimplex(seed + i, x, y, z, w, u, v)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    // Cubic Noise
    public float getCubicFractal(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (fractalType) {
            case FBM:
                return singleCubicFractalFBM(x, y);
            case BILLOW:
                return singleCubicFractalBillow(x, y);
            case RIDGED_MULTI:
                return singleCubicFractalRidgedMulti(x, y);
            default:
                return 0;
        }
    }

    private float singleCubicFractalFBM(float x, float y) {
        int seed = this.seed;
        float sum = singleCubic(seed, x, y);
        float amp = 1;
        int i = 0;

        while (++i < octaves) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += singleCubic(++seed, x, y) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleCubicFractalBillow(float x, float y) {
        int seed = this.seed;
        float sum = Math.abs(singleCubic(seed, x, y)) * 2 - 1;
        float amp = 1;
        int i = 0;

        while (++i < octaves) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleCubic(++seed, x, y)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleCubicFractalRidgedMulti(float x, float y) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleCubic(seed + i, x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getCubic(float x, float y) {
        x *= frequency;
        y *= frequency;

        return singleCubic(0, x, y);
    }

    private final static float CUBIC_2D_BOUNDING = 1 / 2.25f;

    private float singleCubic(int seed, float x, float y) {
        int x1 = fastFloor(x);
        int y1 = fastFloor(y);

        int x0 = x1 - 1;
        int y0 = y1 - 1;
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        int x3 = x1 + 2;
        int y3 = y1 + 2;

        float xs = x - (float) x1;
        float ys = y - (float) y1;

        return cubicLerp(
                cubicLerp(valCoord2D(seed, x0, y0), valCoord2D(seed, x1, y0), valCoord2D(seed, x2, y0), valCoord2D(seed, x3, y0),
                        xs),
                cubicLerp(valCoord2D(seed, x0, y1), valCoord2D(seed, x1, y1), valCoord2D(seed, x2, y1), valCoord2D(seed, x3, y1),
                        xs),
                cubicLerp(valCoord2D(seed, x0, y2), valCoord2D(seed, x1, y2), valCoord2D(seed, x2, y2), valCoord2D(seed, x3, y2),
                        xs),
                cubicLerp(valCoord2D(seed, x0, y3), valCoord2D(seed, x1, y3), valCoord2D(seed, x2, y3), valCoord2D(seed, x3, y3),
                        xs),
                ys) * CUBIC_2D_BOUNDING;
    }

    public float getCubicFractal(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (fractalType) {
            case FBM:
                return singleCubicFractalFBM(x, y, z);
            case BILLOW:
                return singleCubicFractalBillow(x, y, z);
            case RIDGED_MULTI:
                return singleCubicFractalRidgedMulti(x, y, z);
            default:
                return 0;
        }
    }

    private float singleCubicFractalFBM(float x, float y, float z) {
        int seed = this.seed;
        float sum = singleCubic(seed, x, y, z);
        float amp = 1;
        int i = 0;

        while (++i < octaves) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += singleCubic(++seed, x, y, z) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleCubicFractalBillow(float x, float y, float z) {
        int seed = this.seed;
        float sum = Math.abs(singleCubic(seed, x, y, z)) * 2 - 1;
        float amp = 1;
        int i = 0;

        while (++i < octaves) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleCubic(++seed, x, y, z)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleCubicFractalRidgedMulti(float x, float y, float z) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleCubic(seed + i, x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getCubic(float x, float y, float z) {
        return singleCubic(seed, x * frequency, y * frequency, z * frequency);
    }

    private final static float CUBIC_3D_BOUNDING = 1 / (float) (1.5 * 1.5 * 1.5);

    private float singleCubic(int seed, float x, float y, float z) {
        int x1 = fastFloor(x);
        int y1 = fastFloor(y);
        int z1 = fastFloor(z);

        int x0 = x1 - 1;
        int y0 = y1 - 1;
        int z0 = z1 - 1;
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        int z2 = z1 + 1;
        int x3 = x1 + 2;
        int y3 = y1 + 2;
        int z3 = z1 + 2;

        float xs = x - (float) x1;
        float ys = y - (float) y1;
        float zs = z - (float) z1;

        return cubicLerp(
            cubicLerp(
                cubicLerp(valCoord3D(seed, x0, y0, z0), valCoord3D(seed, x1, y0, z0), valCoord3D(seed, x2, y0, z0), valCoord3D(seed, x3, y0, z0), xs),
                cubicLerp(valCoord3D(seed, x0, y1, z0), valCoord3D(seed, x1, y1, z0), valCoord3D(seed, x2, y1, z0), valCoord3D(seed, x3, y1, z0), xs),
                cubicLerp(valCoord3D(seed, x0, y2, z0), valCoord3D(seed, x1, y2, z0), valCoord3D(seed, x2, y2, z0), valCoord3D(seed, x3, y2, z0), xs),
                cubicLerp(valCoord3D(seed, x0, y3, z0), valCoord3D(seed, x1, y3, z0), valCoord3D(seed, x2, y3, z0), valCoord3D(seed, x3, y3, z0), xs),
                ys),
            cubicLerp(
                cubicLerp(valCoord3D(seed, x0, y0, z1), valCoord3D(seed, x1, y0, z1), valCoord3D(seed, x2, y0, z1), valCoord3D(seed, x3, y0, z1), xs),
                cubicLerp(valCoord3D(seed, x0, y1, z1), valCoord3D(seed, x1, y1, z1), valCoord3D(seed, x2, y1, z1), valCoord3D(seed, x3, y1, z1), xs),
                cubicLerp(valCoord3D(seed, x0, y2, z1), valCoord3D(seed, x1, y2, z1), valCoord3D(seed, x2, y2, z1), valCoord3D(seed, x3, y2, z1), xs),
                cubicLerp(valCoord3D(seed, x0, y3, z1), valCoord3D(seed, x1, y3, z1), valCoord3D(seed, x2, y3, z1), valCoord3D(seed, x3, y3, z1), xs),
                ys),
            cubicLerp(
                cubicLerp(valCoord3D(seed, x0, y0, z2), valCoord3D(seed, x1, y0, z2), valCoord3D(seed, x2, y0, z2), valCoord3D(seed, x3, y0, z2), xs),
                cubicLerp(valCoord3D(seed, x0, y1, z2), valCoord3D(seed, x1, y1, z2), valCoord3D(seed, x2, y1, z2), valCoord3D(seed, x3, y1, z2), xs),
                cubicLerp(valCoord3D(seed, x0, y2, z2), valCoord3D(seed, x1, y2, z2), valCoord3D(seed, x2, y2, z2), valCoord3D(seed, x3, y2, z2), xs),
                cubicLerp(valCoord3D(seed, x0, y3, z2), valCoord3D(seed, x1, y3, z2), valCoord3D(seed, x2, y3, z2), valCoord3D(seed, x3, y3, z2), xs),
                ys),
            cubicLerp(
                cubicLerp(valCoord3D(seed, x0, y0, z3), valCoord3D(seed, x1, y0, z3), valCoord3D(seed, x2, y0, z3), valCoord3D(seed, x3, y0, z3), xs),
                cubicLerp(valCoord3D(seed, x0, y1, z3), valCoord3D(seed, x1, y1, z3), valCoord3D(seed, x2, y1, z3), valCoord3D(seed, x3, y1, z3), xs),
                cubicLerp(valCoord3D(seed, x0, y2, z3), valCoord3D(seed, x1, y2, z3), valCoord3D(seed, x2, y2, z3), valCoord3D(seed, x3, y2, z3), xs),
                cubicLerp(valCoord3D(seed, x0, y3, z3), valCoord3D(seed, x1, y3, z3), valCoord3D(seed, x2, y3, z3), valCoord3D(seed, x3, y3, z3), xs),
                ys),
            zs) * CUBIC_3D_BOUNDING;
    }

    public float getCubicFractal(float x, float y, float z, float w) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;

        switch (fractalType) {
            case FBM:
                return singleCubicFractalFBM(x, y, z, w);
            case BILLOW:
                return singleCubicFractalBillow(x, y, z, w);
            case RIDGED_MULTI:
                return singleCubicFractalRidgedMulti(x, y, z, w);
            default:
                return 0;
        }
    }

    private float singleCubicFractalFBM(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = singleCubic(seed, x, y, z, w);
        float amp = 1;
        int i = 0;

        while (++i < octaves) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += singleCubic(++seed, x, y, z, w) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleCubicFractalBillow(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = Math.abs(singleCubic(seed, x, y, z, w)) * 2 - 1;
        float amp = 1;
        int i = 0;

        while (++i < octaves) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleCubic(++seed, x, y, z, w)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleCubicFractalRidgedMulti(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleCubic(seed + i, x, y, z, w));
            correction += (exp *= 0.5f);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getCubic(float x, float y, float z, float w) {
        return singleCubic(seed, x * frequency, y * frequency, z * frequency, w * frequency);
    }

    private final static float CUBIC_4D_BOUNDING = 1f / (1.5f * 1.5f);

    private float singleCubic(int seed, float x, float y, float z, float w) {
        int x1 = fastFloor(x);
        int y1 = fastFloor(y);
        int z1 = fastFloor(z);
        int w1 = fastFloor(w);

        int x0 = x1 - 1;
        int y0 = y1 - 1;
        int z0 = z1 - 1;
        int w0 = w1 - 1;
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        int z2 = z1 + 1;
        int w2 = w1 + 1;
        int x3 = x1 + 2;
        int y3 = y1 + 2;
        int z3 = z1 + 2;
        int w3 = w1 + 2;

        float xs = x - (float) x1;
        float ys = y - (float) y1;
        float zs = z - (float) z1;
        float ws = w - (float) w1;

        return cubicLerp(
                cubicLerp(
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z0, w0), valCoord4D(seed, x1, y0, z0, w0), valCoord4D(seed, x2, y0, z0, w0), valCoord4D(seed, x3, y0, z0, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z0, w0), valCoord4D(seed, x1, y1, z0, w0), valCoord4D(seed, x2, y1, z0, w0), valCoord4D(seed, x3, y1, z0, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z0, w0), valCoord4D(seed, x1, y2, z0, w0), valCoord4D(seed, x2, y2, z0, w0), valCoord4D(seed, x3, y2, z0, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z0, w0), valCoord4D(seed, x1, y3, z0, w0), valCoord4D(seed, x2, y3, z0, w0), valCoord4D(seed, x3, y3, z0, w0), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z1, w0), valCoord4D(seed, x1, y0, z1, w0), valCoord4D(seed, x2, y0, z1, w0), valCoord4D(seed, x3, y0, z1, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z1, w0), valCoord4D(seed, x1, y1, z1, w0), valCoord4D(seed, x2, y1, z1, w0), valCoord4D(seed, x3, y1, z1, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z1, w0), valCoord4D(seed, x1, y2, z1, w0), valCoord4D(seed, x2, y2, z1, w0), valCoord4D(seed, x3, y2, z1, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z1, w0), valCoord4D(seed, x1, y3, z1, w0), valCoord4D(seed, x2, y3, z1, w0), valCoord4D(seed, x3, y3, z1, w0), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z2, w0), valCoord4D(seed, x1, y0, z2, w0), valCoord4D(seed, x2, y0, z2, w0), valCoord4D(seed, x3, y0, z2, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z2, w0), valCoord4D(seed, x1, y1, z2, w0), valCoord4D(seed, x2, y1, z2, w0), valCoord4D(seed, x3, y1, z2, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z2, w0), valCoord4D(seed, x1, y2, z2, w0), valCoord4D(seed, x2, y2, z2, w0), valCoord4D(seed, x3, y2, z2, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z2, w0), valCoord4D(seed, x1, y3, z2, w0), valCoord4D(seed, x2, y3, z2, w0), valCoord4D(seed, x3, y3, z2, w0), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z3, w0), valCoord4D(seed, x1, y0, z3, w0), valCoord4D(seed, x2, y0, z3, w0), valCoord4D(seed, x3, y0, z3, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z3, w0), valCoord4D(seed, x1, y1, z3, w0), valCoord4D(seed, x2, y1, z3, w0), valCoord4D(seed, x3, y1, z3, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z3, w0), valCoord4D(seed, x1, y2, z3, w0), valCoord4D(seed, x2, y2, z3, w0), valCoord4D(seed, x3, y2, z3, w0), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z3, w0), valCoord4D(seed, x1, y3, z3, w0), valCoord4D(seed, x2, y3, z3, w0), valCoord4D(seed, x3, y3, z3, w0), xs),
                                ys),
                        zs),
                cubicLerp(
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z0, w1), valCoord4D(seed, x1, y0, z0, w1), valCoord4D(seed, x2, y0, z0, w1), valCoord4D(seed, x3, y0, z0, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z0, w1), valCoord4D(seed, x1, y1, z0, w1), valCoord4D(seed, x2, y1, z0, w1), valCoord4D(seed, x3, y1, z0, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z0, w1), valCoord4D(seed, x1, y2, z0, w1), valCoord4D(seed, x2, y2, z0, w1), valCoord4D(seed, x3, y2, z0, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z0, w1), valCoord4D(seed, x1, y3, z0, w1), valCoord4D(seed, x2, y3, z0, w1), valCoord4D(seed, x3, y3, z0, w1), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z1, w1), valCoord4D(seed, x1, y0, z1, w1), valCoord4D(seed, x2, y0, z1, w1), valCoord4D(seed, x3, y0, z1, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z1, w1), valCoord4D(seed, x1, y1, z1, w1), valCoord4D(seed, x2, y1, z1, w1), valCoord4D(seed, x3, y1, z1, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z1, w1), valCoord4D(seed, x1, y2, z1, w1), valCoord4D(seed, x2, y2, z1, w1), valCoord4D(seed, x3, y2, z1, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z1, w1), valCoord4D(seed, x1, y3, z1, w1), valCoord4D(seed, x2, y3, z1, w1), valCoord4D(seed, x3, y3, z1, w1), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z2, w1), valCoord4D(seed, x1, y0, z2, w1), valCoord4D(seed, x2, y0, z2, w1), valCoord4D(seed, x3, y0, z2, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z2, w1), valCoord4D(seed, x1, y1, z2, w1), valCoord4D(seed, x2, y1, z2, w1), valCoord4D(seed, x3, y1, z2, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z2, w1), valCoord4D(seed, x1, y2, z2, w1), valCoord4D(seed, x2, y2, z2, w1), valCoord4D(seed, x3, y2, z2, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z2, w1), valCoord4D(seed, x1, y3, z2, w1), valCoord4D(seed, x2, y3, z2, w1), valCoord4D(seed, x3, y3, z2, w1), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z3, w1), valCoord4D(seed, x1, y0, z3, w1), valCoord4D(seed, x2, y0, z3, w1), valCoord4D(seed, x3, y0, z3, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z3, w1), valCoord4D(seed, x1, y1, z3, w1), valCoord4D(seed, x2, y1, z3, w1), valCoord4D(seed, x3, y1, z3, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z3, w1), valCoord4D(seed, x1, y2, z3, w1), valCoord4D(seed, x2, y2, z3, w1), valCoord4D(seed, x3, y2, z3, w1), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z3, w1), valCoord4D(seed, x1, y3, z3, w1), valCoord4D(seed, x2, y3, z3, w1), valCoord4D(seed, x3, y3, z3, w1), xs),
                                ys),
                        zs),
                cubicLerp(
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z0, w2), valCoord4D(seed, x1, y0, z0, w2), valCoord4D(seed, x2, y0, z0, w2), valCoord4D(seed, x3, y0, z0, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z0, w2), valCoord4D(seed, x1, y1, z0, w2), valCoord4D(seed, x2, y1, z0, w2), valCoord4D(seed, x3, y1, z0, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z0, w2), valCoord4D(seed, x1, y2, z0, w2), valCoord4D(seed, x2, y2, z0, w2), valCoord4D(seed, x3, y2, z0, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z0, w2), valCoord4D(seed, x1, y3, z0, w2), valCoord4D(seed, x2, y3, z0, w2), valCoord4D(seed, x3, y3, z0, w2), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z1, w2), valCoord4D(seed, x1, y0, z1, w2), valCoord4D(seed, x2, y0, z1, w2), valCoord4D(seed, x3, y0, z1, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z1, w2), valCoord4D(seed, x1, y1, z1, w2), valCoord4D(seed, x2, y1, z1, w2), valCoord4D(seed, x3, y1, z1, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z1, w2), valCoord4D(seed, x1, y2, z1, w2), valCoord4D(seed, x2, y2, z1, w2), valCoord4D(seed, x3, y2, z1, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z1, w2), valCoord4D(seed, x1, y3, z1, w2), valCoord4D(seed, x2, y3, z1, w2), valCoord4D(seed, x3, y3, z1, w2), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z2, w2), valCoord4D(seed, x1, y0, z2, w2), valCoord4D(seed, x2, y0, z2, w2), valCoord4D(seed, x3, y0, z2, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z2, w2), valCoord4D(seed, x1, y1, z2, w2), valCoord4D(seed, x2, y1, z2, w2), valCoord4D(seed, x3, y1, z2, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z2, w2), valCoord4D(seed, x1, y2, z2, w2), valCoord4D(seed, x2, y2, z2, w2), valCoord4D(seed, x3, y2, z2, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z2, w2), valCoord4D(seed, x1, y3, z2, w2), valCoord4D(seed, x2, y3, z2, w2), valCoord4D(seed, x3, y3, z2, w2), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z3, w2), valCoord4D(seed, x1, y0, z3, w2), valCoord4D(seed, x2, y0, z3, w2), valCoord4D(seed, x3, y0, z3, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z3, w2), valCoord4D(seed, x1, y1, z3, w2), valCoord4D(seed, x2, y1, z3, w2), valCoord4D(seed, x3, y1, z3, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z3, w2), valCoord4D(seed, x1, y2, z3, w2), valCoord4D(seed, x2, y2, z3, w2), valCoord4D(seed, x3, y2, z3, w2), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z3, w2), valCoord4D(seed, x1, y3, z3, w2), valCoord4D(seed, x2, y3, z3, w2), valCoord4D(seed, x3, y3, z3, w2), xs),
                                ys),
                        zs),
                cubicLerp(
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z0, w3), valCoord4D(seed, x1, y0, z0, w3), valCoord4D(seed, x2, y0, z0, w3), valCoord4D(seed, x3, y0, z0, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z0, w3), valCoord4D(seed, x1, y1, z0, w3), valCoord4D(seed, x2, y1, z0, w3), valCoord4D(seed, x3, y1, z0, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z0, w3), valCoord4D(seed, x1, y2, z0, w3), valCoord4D(seed, x2, y2, z0, w3), valCoord4D(seed, x3, y2, z0, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z0, w3), valCoord4D(seed, x1, y3, z0, w3), valCoord4D(seed, x2, y3, z0, w3), valCoord4D(seed, x3, y3, z0, w3), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z1, w3), valCoord4D(seed, x1, y0, z1, w3), valCoord4D(seed, x2, y0, z1, w3), valCoord4D(seed, x3, y0, z1, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z1, w3), valCoord4D(seed, x1, y1, z1, w3), valCoord4D(seed, x2, y1, z1, w3), valCoord4D(seed, x3, y1, z1, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z1, w3), valCoord4D(seed, x1, y2, z1, w3), valCoord4D(seed, x2, y2, z1, w3), valCoord4D(seed, x3, y2, z1, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z1, w3), valCoord4D(seed, x1, y3, z1, w3), valCoord4D(seed, x2, y3, z1, w3), valCoord4D(seed, x3, y3, z1, w3), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z2, w3), valCoord4D(seed, x1, y0, z2, w3), valCoord4D(seed, x2, y0, z2, w3), valCoord4D(seed, x3, y0, z2, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z2, w3), valCoord4D(seed, x1, y1, z2, w3), valCoord4D(seed, x2, y1, z2, w3), valCoord4D(seed, x3, y1, z2, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z2, w3), valCoord4D(seed, x1, y2, z2, w3), valCoord4D(seed, x2, y2, z2, w3), valCoord4D(seed, x3, y2, z2, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z2, w3), valCoord4D(seed, x1, y3, z2, w3), valCoord4D(seed, x2, y3, z2, w3), valCoord4D(seed, x3, y3, z2, w3), xs),
                                ys),
                        cubicLerp(
                                cubicLerp(valCoord4D(seed, x0, y0, z3, w3), valCoord4D(seed, x1, y0, z3, w3), valCoord4D(seed, x2, y0, z3, w3), valCoord4D(seed, x3, y0, z3, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y1, z3, w3), valCoord4D(seed, x1, y1, z3, w3), valCoord4D(seed, x2, y1, z3, w3), valCoord4D(seed, x3, y1, z3, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y2, z3, w3), valCoord4D(seed, x1, y2, z3, w3), valCoord4D(seed, x2, y2, z3, w3), valCoord4D(seed, x3, y2, z3, w3), xs),
                                cubicLerp(valCoord4D(seed, x0, y3, z3, w3), valCoord4D(seed, x1, y3, z3, w3), valCoord4D(seed, x2, y3, z3, w3), valCoord4D(seed, x3, y3, z3, w3), xs),
                                ys),
                        zs),
                ws) * CUBIC_4D_BOUNDING;
    }
    
    // Cellular Noise
    public float getCellular(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (cellularReturnType) {
            case CELL_VALUE:
            case NOISE_LOOKUP:
            case DISTANCE:
                return singleCellular(x, y, z);
            default:
                return singleCellular2Edge(x, y, z);
        }
    }

    private float singleCellular(float x, float y, float z) {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        float distance = 999999;
        int xc = 0, yc = 0, zc = 0;

        switch (cellularDistanceFunction) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            float vecX = xi - x + vec.x;
                            float vecY = yi - y + vec.y;
                            float vecZ = zi - z + vec.z;

                            float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;

                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            float vecX = xi - x + vec.x;
                            float vecY = yi - y + vec.y;
                            float vecZ = zi - z + vec.z;

                            float newDistance = Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ);

                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            float vecX = xi - x + vec.x;
                            float vecY = yi - y + vec.y;
                            float vecZ = zi - z + vec.z;

                            float newDistance = (Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ);

                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
        }

        switch (cellularReturnType) {
            case CELL_VALUE:
                return valCoord3D(0, xc, yc, zc);

            case NOISE_LOOKUP:
                Float3 vec = CELL_3D[hash256(xc, yc, zc, seed)];
                return layered3D(xc + vec.x, yc + vec.y, zc + vec.z, 123, 3);

            case DISTANCE:
                return distance - 1;
            default:
                return 0;
        }
    }

    private float singleCellular2Edge(float x, float y, float z) {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        float distance = 999999;
        float distance2 = 999999;

        switch (cellularDistanceFunction) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            float vecX = xi - x + vec.x;
                            float vecY = yi - y + vec.y;
                            float vecZ = zi - z + vec.z;

                            float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;

                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            distance = Math.min(distance, newDistance);
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            float vecX = xi - x + vec.x;
                            float vecY = yi - y + vec.y;
                            float vecZ = zi - z + vec.z;

                            float newDistance = Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ);

                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            distance = Math.min(distance, newDistance);
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            float vecX = xi - x + vec.x;
                            float vecY = yi - y + vec.y;
                            float vecZ = zi - z + vec.z;

                            float newDistance = (Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ);

                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            distance = Math.min(distance, newDistance);
                        }
                    }
                }
                break;
            default:
                break;
        }

        switch (cellularReturnType) {
            case DISTANCE_2:
                return distance2 - 1;
            case DISTANCE_2_ADD:
                return distance2 + distance - 1;
            case DISTANCE_2_SUB:
                return distance2 - distance - 1;
            case DISTANCE_2_MUL:
                return distance2 * distance - 1;
            case DISTANCE_2_DIV:
                return distance / distance2 - 1;
            default:
                return 0;
        }
    }

    public float getCellular(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (cellularReturnType) {
            case CELL_VALUE:
            case NOISE_LOOKUP:
            case DISTANCE:
                return singleCellular(x, y);
            default:
                return singleCellular2Edge(x, y);
        }
    }

    private float singleCellular(float x, float y) {
        int xr = fastRound(x);
        int yr = fastRound(y);

        float distance = 999999;
        int xc = 0, yc = 0;

        switch (cellularDistanceFunction) {
            default:
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash256(xi, yi, seed)];

                        float vecX = xi - x + vec.x;
                        float vecY = yi - y + vec.y;

                        float newDistance = vecX * vecX + vecY * vecY;

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash256(xi, yi, seed)];

                        float vecX = xi - x + vec.x;
                        float vecY = yi - y + vec.y;

                        float newDistance = (Math.abs(vecX) + Math.abs(vecY));

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash256(xi, yi, seed)];

                        float vecX = xi - x + vec.x;
                        float vecY = yi - y + vec.y;

                        float newDistance = (Math.abs(vecX) + Math.abs(vecY)) + (vecX * vecX + vecY * vecY);

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
        }

        switch (cellularReturnType) {
            case CELL_VALUE:
                return valCoord2D(0, xc, yc);

            case NOISE_LOOKUP:
                Float2 vec = CELL_2D[hash256(xc, yc, seed)];
                return layered2D(xc + vec.x, yc + vec.y, 123, 3);

            case DISTANCE:
                return distance - 1;
            default:
                return 0;
        }
    }

    private float singleCellular2Edge(float x, float y) {
        int xr = fastRound(x);
        int yr = fastRound(y);

        float distance = 999999;
        float distance2 = 999999;

        switch (cellularDistanceFunction) {
            default:
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash256(xi, yi, seed)];

                        float vecX = xi - x + vec.x;
                        float vecY = yi - y + vec.y;

                        float newDistance = vecX * vecX + vecY * vecY;

                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        distance = Math.min(distance, newDistance);
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash256(xi, yi, seed)];

                        float vecX = xi - x + vec.x;
                        float vecY = yi - y + vec.y;

                        float newDistance = Math.abs(vecX) + Math.abs(vecY);

                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        distance = Math.min(distance, newDistance);
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash256(xi, yi, seed)];

                        float vecX = xi - x + vec.x;
                        float vecY = yi - y + vec.y;

                        float newDistance = (Math.abs(vecX) + Math.abs(vecY)) + (vecX * vecX + vecY * vecY);

                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        distance = Math.min(distance, newDistance);
                    }
                }
                break;
        }

        switch (cellularReturnType) {
            case DISTANCE_2:
                return distance2 - 1;
            case DISTANCE_2_ADD:
                return distance2 + distance - 1;
            case DISTANCE_2_SUB:
                return distance2 - distance - 1;
            case DISTANCE_2_MUL:
                return distance2 * distance - 1;
            case DISTANCE_2_DIV:
                return distance / distance2 - 1;
            default:
                return 0;
        }
    }

    public void gradientPerturb3(float[] v3) {
        singleGradientPerturb3(seed, gradientPerturbAmp, frequency, v3);
    }

    public void gradientPerturbFractal3(float[] v3) {
        int seed = this.seed;
        float amp = gradientPerturbAmp * fractalBounding;
        float freq = frequency;

        singleGradientPerturb3(seed, amp, frequency, v3);

        for (int i = 1; i < octaves; i++) {
            freq *= lacunarity;
            amp *= gain;
            singleGradientPerturb3(++seed, amp, freq, v3);
        }
    }

    private void singleGradientPerturb3(int seed, float perturbAmp, float frequency, float[] v3) {
        float xf = v3[0] * frequency;
        float yf = v3[1] * frequency;
        float zf = v3[2] * frequency;

        int x0 = fastFloor(xf);
        int y0 = fastFloor(yf);
        int z0 = fastFloor(zf);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;

        float xs, ys, zs;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = xf - x0;
                ys = yf - y0;
                zs = zf - z0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(xf - x0);
                ys = hermiteInterpolator(yf - y0);
                zs = hermiteInterpolator(zf - z0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(xf - x0);
                ys = quinticInterpolator(yf - y0);
                zs = quinticInterpolator(zf - z0);
                break;
        }

        Float3 vec0 = CELL_3D[hash256(x0, y0, z0, seed)];
        Float3 vec1 = CELL_3D[hash256(x1, y0, z0, seed)];

        float lx0x = lerp(vec0.x, vec1.x, xs);
        float ly0x = lerp(vec0.y, vec1.y, xs);
        float lz0x = lerp(vec0.z, vec1.z, xs);

        vec0 = CELL_3D[hash256(x0, y1, z0, seed)];
        vec1 = CELL_3D[hash256(x1, y1, z0, seed)];

        float lx1x = lerp(vec0.x, vec1.x, xs);
        float ly1x = lerp(vec0.y, vec1.y, xs);
        float lz1x = lerp(vec0.z, vec1.z, xs);

        float lx0y = lerp(lx0x, lx1x, ys);
        float ly0y = lerp(ly0x, ly1x, ys);
        float lz0y = lerp(lz0x, lz1x, ys);

        vec0 = CELL_3D[hash256(x0, y0, z1, seed)];
        vec1 = CELL_3D[hash256(x1, y0, z1, seed)];

        lx0x = lerp(vec0.x, vec1.x, xs);
        ly0x = lerp(vec0.y, vec1.y, xs);
        lz0x = lerp(vec0.z, vec1.z, xs);

        vec0 = CELL_3D[hash256(x0, y1, z1, seed)];
        vec1 = CELL_3D[hash256(x1, y1, z1, seed)];

        lx1x = lerp(vec0.x, vec1.x, xs);
        ly1x = lerp(vec0.y, vec1.y, xs);
        lz1x = lerp(vec0.z, vec1.z, xs);

        v3[0] += lerp(lx0y, lerp(lx0x, lx1x, ys), zs) * perturbAmp;
        v3[1] += lerp(ly0y, lerp(ly0x, ly1x, ys), zs) * perturbAmp;
        v3[2] += lerp(lz0y, lerp(lz0x, lz1x, ys), zs) * perturbAmp;
    }

    public void gradientPerturb2(float[] v2) {
        singleGradientPerturb2(seed, gradientPerturbAmp, frequency, v2);
    }

    public void gradientPerturbFractal2(float[] v2) {
        int seed = this.seed;
        float amp = gradientPerturbAmp * fractalBounding;
        float freq = frequency;

        singleGradientPerturb2(seed, amp, frequency, v2);

        for (int i = 1; i < octaves; i++) {
            freq *= lacunarity;
            amp *= gain;
            singleGradientPerturb2(++seed, amp, freq, v2);
        }
    }

    private void singleGradientPerturb2(int seed, float perturbAmp, float frequency, float[] v2) {
        float xf = v2[0] * frequency;
        float yf = v2[1] * frequency;

        int x0 = fastFloor(xf);
        int y0 = fastFloor(yf);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        float xs, ys;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = xf - x0;
                ys = yf - y0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(xf - x0);
                ys = hermiteInterpolator(yf - y0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(xf - x0);
                ys = quinticInterpolator(yf - y0);
                break;
        }

        Float2 vec0 = CELL_2D[hash256(x0, y0, seed)];
        Float2 vec1 = CELL_2D[hash256(x1, y0, seed)];

        float lx0x = lerp(vec0.x, vec1.x, xs);
        float ly0x = lerp(vec0.y, vec1.y, xs);

        vec0 = CELL_2D[hash256(x0, y1, seed)];
        vec1 = CELL_2D[hash256(x1, y1, seed)];

        float lx1x = lerp(vec0.x, vec1.x, xs);
        float ly1x = lerp(vec0.y, vec1.y, xs);

        v2[0] += lerp(lx0x, lx1x, ys) * perturbAmp;
        v2[1] += lerp(ly0x, ly1x, ys) * perturbAmp;
    }


    public float getHoney(float x, float y) {
        return singleHoney(seed, x * frequency, y * frequency);
    }

    public float singleHoney(int seed, float x, float y) {
        final float result = (singleSimplex(seed, x, y) + singleValue(seed ^ 0x9E3779B9, x, y)) * 0.5f + 1f;
        return (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
    }

    public float getHoneyFractal(float x, float y) {
        x *= frequency;
        y *= frequency;

        switch (fractalType) {
            case FBM:
                return singleHoneyFractalFBM(x, y);
            case BILLOW:
                return singleHoneyFractalBillow(x, y);
            case RIDGED_MULTI:
                return singleHoneyFractalRidgedMulti(x, y);
            default:
                return 0;
        }
    }

    private float singleHoneyFractalFBM(float x, float y) {
        int seed = this.seed;
        float sum = singleHoney(seed, x, y);
        float amp = 1, t;

        for (int i = 1; i < octaves; i++) {
            t = x;
            x = y * lacunarity;
            y = t * lacunarity;

            amp *= gain;
            sum += singleHoney(seed + i, x, y) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalBillow(float x, float y) {
        int seed = this.seed;
        float sum = Math.abs(singleHoney(seed, x, y)) * 2 - 1;
        float amp = 1, t;

        for (int i = 1; i < octaves; i++) {
            t = x;
            x = y * lacunarity;
            y = t * lacunarity;

            amp *= gain;
            sum += (Math.abs(singleHoney(++seed, x, y)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalRidgedMulti(float x, float y) {
        int seed = this.seed;
        float t;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleHoney(seed + i, x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            t = x;
            x = y * lacunarity;
            y = t * lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getHoneyFractal(float x, float y, float z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        switch (fractalType) {
            case FBM:
                return singleHoneyFractalFBM(x, y, z);
            case BILLOW:
                return singleHoneyFractalBillow(x, y, z);
            case RIDGED_MULTI:
                return singleHoneyFractalRidgedMulti(x, y, z);
            default:
                return 0;
        }
    }

    private float singleHoneyFractalFBM(float x, float y, float z) {
        int seed = this.seed;
        float sum = singleHoney(seed, x, y, z);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += singleHoney(++seed, x, y, z) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalBillow(float x, float y, float z) {
        int seed = this.seed;
        float sum = Math.abs(singleHoney(seed, x, y, z)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleHoney(++seed, x, y, z)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalRidgedMulti(float x, float y, float z) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleHoney(seed + i, x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getHoney(float x, float y, float z) {
        return singleHoney(seed, x * frequency, y * frequency, z * frequency);
    }

    public float singleHoney(int seed, float x, float y, float z){
        final float result = (singleSimplex(seed, x, y, z) + singleValue(seed ^ 0x9E3779B9, x, y, z)) * 0.5f + 1f;
        return (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
    }


    private float singleHoneyFractalFBM(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = singleHoney(seed, x, y, z, w);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += singleHoney(++seed, x, y, z, w) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalBillow(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = Math.abs(singleHoney(seed, x, y, z, w)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleHoney(++seed, x, y, z, w)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalRidgedMulti(float x, float y, float z, float w) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleHoney(seed + i, x, y,  z, w));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getHoney(float x, float y, float z, float w) {
        return singleHoney(seed, x * frequency, y * frequency, z * frequency, w * frequency);
    }

    public float singleHoney(int seed, float x, float y, float z, float w) {
        final float result = (singleSimplex(seed, x, y, z, w) + singleValue(seed ^ 0x9E3779B9, x, y, z, w)) * 0.5f + 1f;
        return (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
    }
    public float getHoneyFractal(float x, float y, float z, float w, float u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;

        switch (fractalType) {
            case FBM:
                return singleHoneyFractalFBM(x, y, z, w, u);
            case BILLOW:
                return singleHoneyFractalBillow(x, y, z, w, u);
            case RIDGED_MULTI:
                return singleHoneyFractalRidgedMulti(x, y, z, w, u);
            default:
                return 0;
        }
    }

    private float singleHoneyFractalFBM(float x, float y, float z, float w, float u) {
        final int seed = this.seed;
        float sum = singleHoney(seed, x, y, z, w, u);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += singleHoney(seed + i, x, y, z, w, u) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalBillow(float x, float y, float z, float w, float u) {
        final int seed = this.seed;
        float sum = Math.abs(singleHoney(seed, x, y, z, w, u)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleHoney(seed + i, x, y, z, w, u)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalRidgedMulti(float x, float y, float z, float w, float u) {
        final int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleHoney(seed + i, x, y, z, w, u));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getHoney(float x, float y, float z, float w, float u) {
        return singleHoney(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency);
    }

    public float singleHoney(int seed, float x, float y, float z, float w, float u) {
        final float result = (singleSimplex(seed, x, y, z, w, u) + singleValue(seed ^ 0x9E3779B9, x, y, z, w, u)) * 0.5f + 1f;
        return (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
    }

    public float getHoneyFractal(float x, float y, float z, float w, float u, float v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        switch (fractalType) {
            case FBM:
                return singleHoneyFractalFBM(x, y, z, w, u, v);
            case BILLOW:
                return singleHoneyFractalBillow(x, y, z, w, u, v);
            case RIDGED_MULTI:
                return singleHoneyFractalRidgedMulti(x, y, z, w, u, v);
            default:
                return 0;
        }
    }

    private float singleHoneyFractalFBM(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = singleHoney(seed, x, y, z, w, u, v);
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += singleHoney(++seed, x, y, z, w, u, v) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalBillow(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = Math.abs(singleHoney(seed, x, y, z, w, u, v)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += (Math.abs(singleHoney(++seed, x, y, z, w, u, v)) * 2 - 1) * amp;
        }

        return sum * fractalBounding;
    }

    private float singleHoneyFractalRidgedMulti(float x, float y, float z, float w, float u, float v) {
        int seed = this.seed;
        float sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(singleHoney(seed + i, x, y, z, w, u, v));
            correction += (exp *= 0.5);
            sum += spike * exp;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    public float getHoney(float x, float y, float z, float w, float u, float v) {
        return singleHoney(seed, x * frequency, y * frequency, z * frequency, w * frequency, u * frequency, v * frequency);
    }

    public float singleHoney(int seed, float x, float y, float z, float w, float u, float v) {
        final float result = (singleSimplex(seed, x, y, z, w, u, v) + singleValue(seed ^ 0x9E3779B9, x, y, z, w, u, v)) * 0.5f + 1f;
        return (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
    }














    /**
     * Produces 1D noise that "tiles" by repeating
     * its output every {@code sizeX} units that {@code x} increases or decreases by. This doesn't precalculate an
     * array, instead calculating just one value so that later calls with different x will tile seamlessly.
     * <br>
     * Internally, this just samples out of a circle from a source of 2D noise.
     * @param x the x-coordinate to sample
     * @param sizeX the range of x to generate before repeating; must be greater than 0
     * @param seed the noise seed, as a long
     * @return continuous noise from -1.0 to 1.0, inclusive 
     */
    public float seamless1D(float x, float sizeX, int seed)
    {
        x /= sizeX;
        return getNoiseWithSeed(cosTurns(x), sinTurns(x), seed);
    }

    /**
     * Produces 2D noise that tiles every {@code sizeX} units on the x-axis and {@code sizeY} units on the y-axis.
     * @param x the x-coordinate to sample
     * @param y the y-coordinate to sample
     * @param sizeX the range of x to generate before repeating; must be greater than 0
     * @param sizeY the range of y to generate before repeating; must be greater than 0
     * @param seed the noise seed, as a long
     * @return continuous noise from -1.0 to 1.0, inclusive 
     */
    public float seamless2D(float x, float y, float sizeX, float sizeY, int seed)
    {
        x /= sizeX;
        y /= sizeY;
        return getNoiseWithSeed(cosTurns(x), sinTurns(x), cosTurns(y), sinTurns(y), seed);
    }


    /**
     * Produces 3D noise that tiles every {@code sizeX} units on the x-axis, {@code sizeY} units on the y-axis, and
     * every {@code sizeZ} units on the z-axis.
     * @param x the x-coordinate to sample
     * @param y the y-coordinate to sample
     * @param z the z-coordinate to sample
     * @param sizeX the range of x to generate before repeating; must be greater than 0
     * @param sizeY the range of y to generate before repeating; must be greater than 0
     * @param sizeZ the range of z to generate before repeating; must be greater than 0
     * @param seed the noise seed, as a long
     * @return continuous noise from -1.0 to 1.0, inclusive 
     */
    public float seamless3D(float x, float y, float z, float sizeX, float sizeY, float sizeZ, int seed)
    {
        x /= sizeX;
        y /= sizeY;
        z /= sizeZ;
        return getNoiseWithSeed(cosTurns(x), sinTurns(x), cosTurns(y), sinTurns(y), cosTurns(z), sinTurns(z), seed);
    }
    /**
     * A fairly-close approximation of {@link Math#sin(double)} that can be significantly faster (between 8x and 80x
     * faster sin() calls in benchmarking, and both takes and returns floats; if you have access to libGDX you should
     * consider its more-precise and sometimes-faster MathUtils.sin() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.sin() does, so one angle in radians,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those floats themselves will lose precision at that scale). The
     * difference between the result of this method and {@link Math#sin(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any float to the valid input range.
     * @param radians an angle in radians as a float, often from 0 to pi * 2, though not required to be.
     * @return the sine of the given angle, as a float between -1f and 1f (both inclusive)
     */
    public static float sin(float radians)
    {
        radians *= 0.6366197723675814f;
        final int floor = (radians >= 0.0 ? (int) radians : (int) radians - 1) & -2;
        radians -= floor;
        radians *= 2f - radians;
        return radians * (-0.775f - 0.225f * radians) * ((floor & 2) - 1);
    }

    /**
     * A fairly-close approximation of {@link Math#cos(double)} that can be significantly faster (between 8x and 80x
     * faster cos() calls in benchmarking, and both takes and returns floats; if you have access to libGDX you should
     * consider its more-precise and sometimes-faster MathUtils.cos() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.cos() does, so one angle in radians,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those floats themselves will lose precision at that scale). The
     * difference between the result of this method and {@link Math#cos(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any float to the valid input range.
     * @param radians an angle in radians as a float, often from 0 to pi * 2, though not required to be.
     * @return the cosine of the given angle, as a float between -1f and 1f (both inclusive)
     */
    public static float cos(float radians)
    {
        radians = radians * 0.6366197723675814f + 1f;
        final int floor = (radians >= 0.0 ? (int) radians : (int) radians - 1) & -2;
        radians -= floor;
        radians *= 2f - radians;
        return radians * (-0.775f - 0.225f * radians) * ((floor & 2) - 1);
    }
    /**
     * A variation on {@link Math#sin(double)} that takes its input as a fraction of a turn instead of in radians (it
     * also takes and returns a float); one turn is equal to 360 degrees or two*PI radians. This can be useful as a
     * building block for other measurements; to make a sine method that takes its input in grad (with 400 grad equal to
     * 360 degrees), you would just divide the grad value by 400.0 (or multiply it by 0.0025) and pass it to this
     * method. Similarly for binary degrees, also called brad (with 256 brad equal to 360 degrees), you would divide by
     * 256.0 or multiply by 0.00390625 before passing that value here. The brad case is especially useful because you
     * can use a byte for any brad values, and adding up those brad values will wrap correctly (256 brad goes back to 0)
     * while keeping perfect precision for the results (you still divide by 256.0 when you pass the brad value to this
     * method).
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a float, with 0.5 here equivalent to PI radians in {@link #sin(float)}
     * @return the sine of the given angle, as a float between -1.0 and 1.0 (both inclusive)
     */
    public static float sinTurns(float turns)
    {
        turns *= 4f;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2f - turns;
        return turns * (-0.775f - 0.225f * turns) * ((floor & 2L) - 1L);
    }

    /**
     * A variation on {@link Math#cos(double)} that takes its input as a fraction of a turn instead of in radians (it
     * also takes and returns a float); one turn is equal to 360 degrees or two*PI radians. This can be useful as a
     * building block for other measurements; to make a cosine method that takes its input in grad (with 400 grad equal
     * to 360 degrees), you would just divide the grad value by 400.0 (or multiply it by 0.0025) and pass it to this
     * method. Similarly for binary degrees, also called brad (with 256 brad equal to 360 degrees), you would divide by
     * 256.0 or multiply by 0.00390625 before passing that value here. The brad case is especially useful because you
     * can use a byte for any brad values, and adding up those brad values will wrap correctly (256 brad goes back to 0)
     * while keeping perfect precision for the results (you still divide by 256.0 when you pass the brad value to this
     * method).
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a float, with 0.5 here equivalent to PI radians in {@link #cos(float)}
     * @return the cosine of the given angle, as a float between -1.0 and 1.0 (both inclusive)
     */
    public static float cosTurns(float turns)
    {
        turns = turns * 4f + 1f;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2f - turns;
        return turns * (-0.775f - 0.225f * turns) * ((floor & 2L) - 1L);
    }

    public static float swayRandomized(int seed, float value) {
        final int floor = value >= 0f ? (int) value : (int) value - 1;
        final float start = ((((seed += floor) ^ 0xD1B54A35) * 0x1D2473 & 0x1FFFFF) - 0x100000) * 0x1p-20f,
                end = (((seed + 1 ^ 0xD1B54A35) * 0x1D2473 & 0x1FFFFF) - 0x100000) * 0x1p-20f;
        value -= floor;
        value *= value * (3f - 2f * value);
        return (1f - value) * start + value * end;
    }

    /**
     * A 32-bit point hash that smashes x and y into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y point with the given state s
     */
    public static int hashAll(int x, int y, int s) {
        s ^= x * 0x1827F5 ^ y * 0x123C21;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }
    /**
     * A 32-bit point hash that smashes x, y, and z into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y,z point with the given state s
     */
    public static int hashAll(int x, int y, int z, int s) {
        s ^= x * 0x1A36A9 ^ y * 0x157931 ^ z * 0x119725;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }

    /**
     * A 32-bit point hash that smashes x, y, z, and w into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y,z,w point with the given state s
     */
    public static int hashAll(int x, int y, int z, int w, int s) {
        s ^= x * 0x1B69E1 ^ y * 0x177C0B ^ z * 0x141E5D ^ w * 0x113C31;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }
    /**
     * A 32-bit point hash that smashes x, y, z, w, and u into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point 
     * @return 32-bit hash of the x,y,z,w,u point with the given state s
     */
    public static int hashAll(int x, int y, int z, int w, int u, int s) {
        s ^= x * 0x1C3360 ^ y * 0x18DA3A ^ z * 0x15E6DA ^ w * 0x134D28 ^ u * 0x110280;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }

    /**
     * A 32-bit point hash that smashes x, y, z, w, u, and v into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param v v position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point 
     * @return 32-bit hash of the x,y,z,w,u,v point with the given state s
     */
    public static int hashAll(int x, int y, int z, int w, int u, int v, int s) {
        s ^= x * 0x1CC1C5 ^ y * 0x19D7AF ^ z * 0x173935 ^ w * 0x14DEAF ^ u * 0x12C139 ^ v * 0x10DAA3;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }

    /**
     * A 8-bit point hash that smashes x and y into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y point with the given state s
     */
    public static int hash256(int x, int y, int s) {
        s ^= x * 0x1827F5 ^ y * 0x123C21;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }
    /**
     * A 8-bit point hash that smashes x, y, and z into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z point with the given state s
     */
    public static int hash256(int x, int y, int z, int s) {
        s ^= x * 0x1A36A9 ^ y * 0x157931 ^ z * 0x119725;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 8-bit point hash that smashes x, y, z, and w into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z,w point with the given state s
     */
    public static int hash256(int x, int y, int z, int w, int s) {
        s ^= x * 0x1B69E1 ^ y * 0x177C0B ^ z * 0x141E5D ^ w * 0x113C31;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * An 8-bit point hash that smashes x, y, z, w, and u into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point 
     * @return 8-bit hash of the x,y,z,w,u point with the given state s
     */
    public static int hash256(int x, int y, int z, int w, int u, int s) {
        s ^= x * 0x1C3360 ^ y * 0x18DA3A ^ z * 0x15E6DA ^ w * 0x134D28 ^ u * 0x110280;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 8-bit point hash that smashes x, y, z, w, u, and v into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param v v position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point 
     * @return 8-bit hash of the x,y,z,w,u,v point with the given state s
     */
    public static int hash256(int x, int y, int z, int w, int u, int v, int s) {
        s ^= x * 0x1CC1C5 ^ y * 0x19D7AF ^ z * 0x173935 ^ w * 0x14DEAF ^ u * 0x12C139 ^ v * 0x10DAA3;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 5-bit point hash that smashes x and y into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y point with the given state s
     */
    public static int hash32(int x, int y, int s) {
        s ^= x * 0x1827F5 ^ y * 0x123C21;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }
    /**
     * A 5-bit point hash that smashes x, y, and z into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y,z point with the given state s
     */
    public static int hash32(int x, int y, int z, int s) {
        s ^= x * 0x1A36A9 ^ y * 0x157931 ^ z * 0x119725;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }

    /**
     * A 5-bit point hash that smashes x, y, z, and w into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y,z,w point with the given state s
     */
    public static int hash32(int x, int y, int z, int w, int s) {
        s ^= x * 0x1B69E1 ^ y * 0x177C0B ^ z * 0x141E5D ^ w * 0x113C31;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }

    /**
     * An 5-bit point hash that smashes x, y, z, w, and u into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point 
     * @return 5-bit hash of the x,y,z,w,u point with the given state s
     */
    public static int hash32(int x, int y, int z, int w, int u, int s) {
        s ^= x * 0x1C3360 ^ y * 0x18DA3A ^ z * 0x15E6DA ^ w * 0x134D28 ^ u * 0x110280;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 5-bit point hash that smashes x, y, z, w, u, and v into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param v v position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point 
     * @return 5-bit hash of the x,y,z,w,u,v point with the given state s
     */
    public static int hash32(int x, int y, int z, int w, int u, int v, int s) {
        s ^= x * 0x1CC1C5 ^ y * 0x19D7AF ^ z * 0x173935 ^ w * 0x14DEAF ^ u * 0x12C139 ^ v * 0x10DAA3;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }
    
    public static final float F2f = 0.3660254f;
    public static final float G2f = 0.21132487f;
    public static final float H2f = 0.42264974f;

    public static final float F3f = 0.33333334f;
    public static final float G3f = 0.16666667f;

    public static final float F4f = (float) ((2.23606797 - 1.0) / 4.0);
    public static final float G4f = (float) ((5.0 - 2.23606797) / 20.0);
    public static final float LIMIT4 = 0.62f;

    private static final float F5f = (float) ((Math.sqrt(6.0) - 1.0) / 5.0);
    private static final float G5f = (float) ((6.0 - Math.sqrt(6.0)) / 30.0);
    private static final float LIMIT5 = 0.7f;

    private static final float F6f = (float) ((Math.sqrt(7.0) - 1.0) / 6.0);
    private static final float G6f = F6f / (1f + 6f * F6f);
    private static final float LIMIT6 = 0.8375f;


    /**
     * Simple container class that holds 2 floats.
     * Takes slightly less storage than an array of float[2] and may avoid array index bounds check speed penalty.
     */
    public static class Float2 {
        public final float x, y;

        public Float2(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Simple container class that holds 3 floats.
     * Takes slightly less storage than an array of float[3] and may avoid array index bounds check speed penalty.
     */
    public static class Float3 {
        public final float x, y, z;

        public Float3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    protected static final float[] GRAD_2D = {
            +0.6499429579167653f, +0.7599829941876370f,
            -0.1551483029088119f, +0.9878911904175052f,
            -0.8516180517334043f, +0.5241628506120981f,
            -0.9518580082090311f, -0.3065392833036837f,
            -0.3856887670108717f, -0.9226289476282616f,
            +0.4505066120763985f, -0.8927730912586049f,
            +0.9712959670388622f, -0.2378742197339624f,
            +0.8120673355833279f, +0.5835637432865366f,
            +0.0842989251943661f, +0.9964405106232257f,
            -0.7024883500032670f, +0.7116952424385647f,
            -0.9974536374007479f, -0.0713178886116052f,
            -0.5940875849508908f, -0.8044003613917750f,
            +0.2252075529515288f, -0.9743108118529653f,
            +0.8868317111719171f, -0.4620925405802277f,
            +0.9275724981153959f, +0.3736432265409930f,
            +0.3189067150428103f, +0.9477861083074618f,
            -0.5130301507665112f, +0.8583705868705491f,
            -0.9857873824221494f, +0.1679977281313266f,
            -0.7683809836504446f, -0.6399927061806058f,
            -0.0130202362193748f, -0.9999152331316848f,
            +0.7514561619680513f, -0.6597830223946701f,
            +0.9898275175279653f, +0.1422725748147741f,
            +0.5352066871710182f, +0.8447211386057674f,
            -0.2941198828144364f, +0.9557685360657266f,
            -0.9175289804081126f, +0.3976689202229027f,
            -0.8985631161871687f, -0.4388443075032474f,
            -0.2505005588110731f, -0.9681164547900940f,
            +0.5729409678802212f, -0.8195966369650838f,
            +0.9952584535626074f, -0.0972656702653466f,
            +0.7207814785200723f, +0.6931623620930514f,
            -0.0583247612407003f, +0.9982976621360060f,
            -0.7965970142012075f, +0.6045107087270838f,
            -0.9771604781144960f, -0.2125027058911242f,
            -0.4736001288089817f, -0.8807399831914728f,
            +0.3615343409387538f, -0.9323587937709286f,
            +0.9435535266854258f, -0.3312200813348966f,
            +0.8649775992346886f, +0.5018104750024599f,
            +0.1808186720712497f, +0.9835164502083277f,
            -0.6299339540895539f, +0.7766487066139361f,
            -0.9996609468975833f, +0.0260382650694516f,
            -0.6695112313914258f, -0.7428019325774111f,
            +0.1293727267195084f, -0.9915960354807594f,
            +0.8376810167470904f, -0.5461597881403947f,
            +0.9595170289111490f, +0.2816506190824391f,
            +0.4095816551369482f, +0.9122734610714476f,
            -0.4271076040148479f, +0.9042008043530463f,
            -0.9647728141412515f, +0.2630844295924223f,
            -0.8269869890664444f, -0.5622210596507540f,
            -0.1102159255238020f, -0.9939076666174438f,
            +0.6837188597775012f, -0.7297455178242300f,
            +0.9989724417383330f, +0.0453217458550843f,
            +0.6148313475439905f, +0.7886586169422362f,
            -0.1997618324529528f, +0.9798444827088829f,
            -0.8744989400706802f, +0.4850274258382270f,
            -0.9369870231562731f, -0.3493641630687752f,
            -0.3434772946489506f, -0.9391609809082988f,
            +0.4905057254335028f, -0.8714379687143274f,
            +0.9810787787756657f, -0.1936089611460388f,
            +0.7847847614201463f, +0.6197684069414349f,
            +0.0390518795551629f, +0.9992371844077906f,
            -0.7340217731995672f, +0.6791259356474049f,
            -0.9931964444524306f, -0.1164509455824639f,
            -0.5570202966000876f, -0.8304988796955420f,
            +0.2691336060685578f, -0.9631028512493016f,
            +0.9068632806061000f, -0.4214249521425399f,
            +0.9096851999779008f, +0.4152984913783901f,
            +0.2756236986873733f, +0.9612656119522284f,
            -0.5514058359842319f, +0.8342371389734039f,
            -0.9923883787916933f, +0.1231474954645637f,
            -0.7385858406439617f, -0.6741594440488484f,
            +0.0323110469045428f, -0.9994778618098213f,
            +0.7805865154410089f, -0.6250477517051506f,
            +0.9823623706068018f, +0.1869870926448790f,
            +0.4963724943556111f, +0.8681096398768929f,
            -0.3371347561867868f, +0.9414564016304079f,
            -0.9346092156607797f, +0.3556762769737983f,
            -0.8777506000588920f, -0.4791178185960681f,
            -0.2063664269701996f, -0.9784747813917093f,
            +0.6094977881394418f, -0.7927877687333024f,
            +0.9986440175043460f, -0.0520588734297966f,
            +0.6886255051458764f, +0.7251171723677399f,
            -0.1035094220814735f, +0.9946284731196666f,
            -0.8231759450656516f, +0.5677863713275190f,
            -0.9665253951623188f, -0.2565709658288005f,
            -0.4331968034012919f, -0.9012993562201753f,
            +0.4034189716368784f, -0.9150153732716426f,
            +0.9575954428121146f, -0.2881162402667889f,
            +0.8413458575409575f, +0.5404971304259356f,
            +0.1360581877502697f, +0.9907008476558967f,
            -0.6644857355505560f, +0.7473009482463117f,
            -0.9998138366647180f, -0.0192948701414780f,
            -0.6351581891853917f, -0.7723820781910558f,
            +0.1741806522163015f, -0.9847137149413040f,
            +0.8615731658120597f, -0.5076334109892543f,
            +0.9457661714829020f, +0.3248481935898273f,
            +0.3678149601703667f, +0.9298990026206456f,
            -0.4676486851245607f, +0.8839144230643990f,
            -0.9757048995218635f, +0.2190889067228882f,
            -0.8006563717736747f, -0.5991238388999518f,
            -0.0650570415691071f, -0.9978815467490495f,
            +0.7160896397121960f, -0.6980083293893113f,
            +0.9958918787052943f, +0.0905503502413954f,
            +0.5784561871098056f, +0.8157134543418942f,
            -0.2439648281544816f, +0.9697840804135497f,
            -0.8955826311865743f, +0.4448952131872543f,
            -0.9201904205900768f, -0.3914710587696841f,
            -0.3005599364234082f, -0.9537629289384008f,
            +0.5294967923694863f, -0.8483119396014800f,
            +0.9888453593035162f, -0.1489458135829932f,
            +0.7558893631265085f, +0.6546993743025888f,
            -0.0062754222469803f, +0.9999803093439501f,
            -0.7640466961212760f, +0.6451609459244744f,
            -0.9868981170802014f, -0.1613446822909051f,
            -0.5188082666339063f, -0.8548906260290385f,
            +0.3125065582647844f, -0.9499156020623616f,
            +0.9250311403279032f, -0.3798912863223621f,
            +0.8899283927548960f, +0.4561002694240463f,
            +0.2317742435145519f, +0.9727696027545563f,
            -0.5886483179573486f, +0.8083892365475831f,
            -0.9969499014064180f, +0.0780441803450664f,
            -0.7072728176724660f, -0.7069407057042696f,
            +0.0775759270620736f, -0.9969864470194466f,
            +0.8081126726681943f, -0.5890279350532263f,
            +0.9728783545459001f, +0.2313173302112532f,
            +0.4565181982253288f, +0.8897140746830408f,
            -0.3794567783511009f, +0.9252094645881026f,
            -0.9497687200714887f, +0.3129526775309106f,
            -0.8551342041690687f, -0.5184066867432686f,
            -0.1618081880753845f, -0.9868222283024238f,
            +0.6448020194233159f, -0.7643496292585048f,
            +0.9999772516247822f, -0.0067450895432855f,
            +0.6550543261176665f, +0.7555817823601425f,
            -0.1484813589986064f, +0.9889152066936411f,
            -0.8480631534437840f, +0.5298951667745091f,
            -0.9539039899003245f, -0.3001119425351840f,
            -0.3919032080850608f, -0.9200064540494471f,
            +0.4444745293405786f, -0.8957914895596358f,
            +0.9696693887216105f, -0.2444202867526717f,
            +0.8159850520735595f, +0.5780730012658526f,
            +0.0910180879994953f, +0.9958492394217692f,
            -0.6976719213969089f, +0.7164173993520435f,
            -0.9979119924958648f, -0.0645883521459785f,
            -0.5994998228898376f, -0.8003748886334786f,
            +0.2186306161766729f, -0.9758076929755208f,
            +0.8836946816279001f, -0.4680637880274058f,
            +0.9300716543684309f, +0.3673781672069940f,
            +0.3252923626016029f, +0.9456134933645286f,
            -0.5072286936943775f, +0.8618114946396893f,
            -0.9846317976415725f, +0.1746431306210620f,
            -0.7726803123417516f, -0.6347953488483143f,
            -0.0197644578133314f, -0.9998046640256011f,
            +0.7469887719961158f, -0.6648366525032559f,
            +0.9907646418168752f, +0.1355928631067248f,
            +0.5408922318074902f, +0.8410919055432124f,
            -0.2876664477065717f, +0.9577306588304888f,
            -0.9148257956391065f, +0.4038486890325085f,
            -0.9015027194859215f, -0.4327734358292892f,
            -0.2570248925062563f, -0.9664047830139022f,
            +0.5673996816983953f, -0.8234425306046317f,
            +0.9945797473944409f, -0.1039765650173647f,
            +0.7254405241129018f, +0.6882848581617921f,
            -0.0515898273251730f, +0.9986683582233687f,
            -0.7925014140531963f, +0.6098700752813540f,
            -0.9785715990807187f, -0.2059068368767903f,
            -0.4795300252265173f, -0.8775254725113429f,
            +0.3552372730694574f, -0.9347761656258549f,
            +0.9412979532686209f, -0.3375768996425928f,
            +0.8683426789873530f, +0.4959647082697184f,
            +0.1874484652642005f, +0.9822744386728669f,
            -0.6246810590458048f, +0.7808800000444446f,
            -0.9994625758058275f, +0.0327804753409776f,
            -0.6745062666468870f, -0.7382691218343610f,
            +0.1226813796500722f, -0.9924461089082646f,
            +0.8339780641890598f, -0.5517975973592748f,
            +0.9613949601033843f, +0.2751721837101493f,
            +0.4157257040026583f, +0.9094900433932711f,
            -0.4209989726203348f, +0.9070611142875780f,
            -0.9629763390922247f, +0.2695859238694348f,
            -0.8307604078465821f, -0.5566301687427484f,
            -0.1169174144996730f, -0.9931416405461567f,
            +0.6787811074228051f, -0.7343406622310046f,
            +0.9992554159724470f, +0.0385825562881973f,
            +0.6201369341201711f, +0.7844935837468874f,
            -0.1931481494214682f, +0.9811696042861612f,
            -0.8712074932224428f, +0.4909149659086258f,
            -0.9393222007870077f, -0.3430361542296271f,
            -0.3498042060103595f, -0.9368228314134226f,
            +0.4846166400948296f, -0.8747266499559725f,
            +0.9797505510481769f, -0.2002220210685972f,
            +0.7889473022428521f, +0.6144608647291752f,
            +0.0457909354721791f, +0.9989510449609544f,
            -0.7294243101497431f, +0.6840615292227530f,
            -0.9939593229024027f, -0.1097490975607407f,
            -0.5626094146025390f, -0.8267228354174018f,
            +0.2626312687452330f, -0.9648962724963078f,
            +0.9040001019019392f, -0.4275322394408211f,
            +0.9124657316291773f, +0.4091531358824348f,
            +0.2821012513235693f, +0.9593846381935018f,
            -0.5457662881946498f, +0.8379374431723614f,
            -0.9915351626845509f, +0.1298384425357957f,
            -0.7431163048326799f, -0.6691622803863227f,
            +0.0255687442062853f, -0.9996730662170076f,
            +0.7763527553119807f, -0.6302986588273021f,
            +0.9836012681423212f, +0.1803567168386515f,
            +0.5022166799422209f, +0.8647418148718223f,
            -0.3307768791887710f, +0.9437089891455613f,
            -0.9321888864830543f, +0.3619722087639923f,
            -0.8809623252471085f, -0.4731864130500873f,
            -0.2129616324856343f, -0.9770605626515961f,
            +0.6041364985661350f, -0.7968808512571063f,
            +0.9982701582127194f, -0.0587936324949578f,
            +0.6935008202914851f, +0.7204558364362367f,
            -0.0967982092968079f, +0.9953040272584711f,
            -0.8193274492343137f, +0.5733258505694586f,
            -0.9682340024187017f, -0.2500458289199430f,
            -0.4392662937408502f, -0.8983569018954422f,
            +0.3972379338845546f, -0.9177156552457467f,
            +0.9556302892322005f, -0.2945687530984589f,
            +0.8449724198323217f, +0.5348098818484104f,
            +0.1427374585755972f, +0.9897605861618151f,
            -0.6594300077680133f, +0.7517659641504648f,
            -0.9999212381512442f, -0.0125505973595986f,
            -0.6403535266476091f, -0.7680803088935230f,
            +0.1675347077076747f, -0.9858661784001437f,
            +0.8581295336101056f, -0.5134332513054668f,
            +0.9479357869928937f, +0.3184615263075951f,
            +0.3740788450165170f, +0.9273969040875156f,
            -0.4616759649446430f, +0.8870486477034012f,
            -0.9742049295269273f, +0.2256651397213017f,
            -0.8046793020829978f, -0.5937097108850584f,
            -0.0717863620135296f, -0.9974200309943962f,
            +0.7113652211526822f, -0.7028225395748172f,
            +0.9964799940037152f, +0.0838309104707540f,
            +0.5839450884626246f, +0.8117931594072332f,
            -0.2374179978909748f, +0.9714075840127259f,
            -0.8925614000865144f, +0.4509258775847768f,
            -0.9228099950981292f, -0.3852553866553855f,
            -0.3069863155319683f, -0.9517139286971200f,
            +0.5237628071845146f, -0.8518641451605984f,
            +0.9878182118285335f, -0.1556122758007173f,
            +0.7602881737752754f, +0.6495859395164404f,
            +0.0004696772366984f, +0.9999998897016406f,
            -0.7596776469502666f, +0.6502998329417794f,
            -0.9879639510809196f, -0.1546842957917130f,
            -0.5245627784110601f, -0.8513717704420726f,
            +0.3060921834538644f, -0.9520018777441807f,
            +0.9224476966294768f, -0.3861220622846781f,
            +0.8929845854878761f, +0.4500872471877493f,
            +0.2383303891026603f, +0.9711841358002995f,
            -0.5831822693781987f, +0.8123413326200348f,
            -0.9964008074312266f, +0.0847669213219385f,
            -0.7120251067268070f, -0.7021540054650968f,
            +0.0708493994771745f, -0.9974870237721009f,
            +0.8041212432524677f, -0.5944653279629567f,
            +0.9744164792492415f, +0.2247499165016809f,
            +0.4625090142797330f, +0.8866145790082576f,
    };
    private static final float[] GRAD_3D =
            {
                    -0.448549002408981f, +1.174316525459290f, +0.000000000000001f, +0.0f,
                    +0.000000000000001f, +1.069324374198914f, +0.660878777503967f, +0.0f,
                    +0.448549002408981f, +1.174316525459290f, +0.000000000000001f, +0.0f,
                    +0.000000000000001f, +1.069324374198914f, -0.660878777503967f, +0.0f,
                    -0.725767493247986f, +0.725767493247986f, -0.725767493247986f, +0.0f,
                    -1.069324374198914f, +0.660878777503967f, +0.000000000000001f, +0.0f,
                    -0.725767493247986f, +0.725767493247986f, +0.725767493247986f, +0.0f,
                    +0.725767493247986f, +0.725767493247986f, +0.725767493247986f, +0.0f,
                    +1.069324374198914f, +0.660878777503967f, +0.000000000000000f, +0.0f,
                    +0.725767493247986f, +0.725767493247986f, -0.725767493247986f, +0.0f,
                    -0.660878777503967f, +0.000000000000003f, -1.069324374198914f, +0.0f,
                    -1.174316525459290f, +0.000000000000003f, -0.448549002408981f, +0.0f,
                    +0.000000000000000f, +0.448549002408981f, -1.174316525459290f, +0.0f,
                    -0.660878777503967f, +0.000000000000001f, +1.069324374198914f, +0.0f,
                    +0.000000000000001f, +0.448549002408981f, +1.174316525459290f, +0.0f,
                    -1.174316525459290f, +0.000000000000001f, +0.448549002408981f, +0.0f,
                    +0.660878777503967f, +0.000000000000001f, +1.069324374198914f, +0.0f,
                    +1.174316525459290f, +0.000000000000001f, +0.448549002408981f, +0.0f,
                    +0.660878777503967f, +0.000000000000001f, -1.069324374198914f, +0.0f,
                    +1.174316525459290f, +0.000000000000001f, -0.448549002408981f, +0.0f,
                    -0.725767493247986f, -0.725767493247986f, -0.725767493247986f, +0.0f,
                    -1.069324374198914f, -0.660878777503967f, -0.000000000000001f, +0.0f,
                    -0.000000000000001f, -0.448549002408981f, -1.174316525459290f, +0.0f,
                    -0.000000000000001f, -0.448549002408981f, +1.174316525459290f, +0.0f,
                    -0.725767493247986f, -0.725767493247986f, +0.725767493247986f, +0.0f,
                    +0.725767493247986f, -0.725767493247986f, +0.725767493247986f, +0.0f,
                    +1.069324374198914f, -0.660878777503967f, +0.000000000000001f, +0.0f,
                    +0.725767493247986f, -0.725767493247986f, -0.725767493247986f, +0.0f,
                    -0.000000000000004f, -1.069324374198914f, -0.660878777503967f, +0.0f,
                    -0.448549002408981f, -1.174316525459290f, -0.000000000000003f, +0.0f,
                    -0.000000000000003f, -1.069324374198914f, +0.660878777503967f, +0.0f,
                    +0.448549002408981f, -1.174316525459290f, +0.000000000000003f, +0.0f,
            };


    protected static final float[] GRAD_4D =
        {
                -0.5875167f, +1.4183908f, +1.4183908f, +1.4183908f,
                -0.5875167f, +1.4183908f, +1.4183908f, -1.4183908f,
                -0.5875167f, +1.4183908f, -1.4183908f, +1.4183908f,
                -0.5875167f, +1.4183908f, -1.4183908f, -1.4183908f,
                -0.5875167f, -1.4183908f, +1.4183908f, +1.4183908f,
                -0.5875167f, -1.4183908f, +1.4183908f, -1.4183908f,
                -0.5875167f, -1.4183908f, -1.4183908f, +1.4183908f,
                -0.5875167f, -1.4183908f, -1.4183908f, -1.4183908f,
                +1.4183908f, -0.5875167f, +1.4183908f, +1.4183908f,
                +1.4183908f, -0.5875167f, +1.4183908f, -1.4183908f,
                +1.4183908f, -0.5875167f, -1.4183908f, +1.4183908f,
                +1.4183908f, -0.5875167f, -1.4183908f, -1.4183908f,
                -1.4183908f, -0.5875167f, +1.4183908f, +1.4183908f,
                -1.4183908f, -0.5875167f, +1.4183908f, -1.4183908f,
                -1.4183908f, -0.5875167f, -1.4183908f, +1.4183908f,
                -1.4183908f, -0.5875167f, -1.4183908f, -1.4183908f,
                +1.4183908f, +1.4183908f, -0.5875167f, +1.4183908f,
                +1.4183908f, +1.4183908f, -0.5875167f, -1.4183908f,
                +1.4183908f, -1.4183908f, -0.5875167f, +1.4183908f,
                +1.4183908f, -1.4183908f, -0.5875167f, -1.4183908f,
                -1.4183908f, +1.4183908f, -0.5875167f, +1.4183908f,
                -1.4183908f, +1.4183908f, -0.5875167f, -1.4183908f,
                -1.4183908f, -1.4183908f, -0.5875167f, +1.4183908f,
                -1.4183908f, -1.4183908f, -0.5875167f, -1.4183908f,
                +1.4183908f, +1.4183908f, +1.4183908f, -0.5875167f,
                +1.4183908f, +1.4183908f, -1.4183908f, -0.5875167f,
                +1.4183908f, -1.4183908f, +1.4183908f, -0.5875167f,
                +1.4183908f, -1.4183908f, -1.4183908f, -0.5875167f,
                -1.4183908f, +1.4183908f, +1.4183908f, -0.5875167f,
                -1.4183908f, +1.4183908f, -1.4183908f, -0.5875167f,
                -1.4183908f, -1.4183908f, +1.4183908f, -0.5875167f,
                -1.4183908f, -1.4183908f, -1.4183908f, -0.5875167f,
                +0.5875167f, +1.4183908f, +1.4183908f, +1.4183908f,
                +0.5875167f, +1.4183908f, +1.4183908f, -1.4183908f,
                +0.5875167f, +1.4183908f, -1.4183908f, +1.4183908f,
                +0.5875167f, +1.4183908f, -1.4183908f, -1.4183908f,
                +0.5875167f, -1.4183908f, +1.4183908f, +1.4183908f,
                +0.5875167f, -1.4183908f, +1.4183908f, -1.4183908f,
                +0.5875167f, -1.4183908f, -1.4183908f, +1.4183908f,
                +0.5875167f, -1.4183908f, -1.4183908f, -1.4183908f,
                +1.4183908f, +0.5875167f, +1.4183908f, +1.4183908f,
                +1.4183908f, +0.5875167f, +1.4183908f, -1.4183908f,
                +1.4183908f, +0.5875167f, -1.4183908f, +1.4183908f,
                +1.4183908f, +0.5875167f, -1.4183908f, -1.4183908f,
                -1.4183908f, +0.5875167f, +1.4183908f, +1.4183908f,
                -1.4183908f, +0.5875167f, +1.4183908f, -1.4183908f,
                -1.4183908f, +0.5875167f, -1.4183908f, +1.4183908f,
                -1.4183908f, +0.5875167f, -1.4183908f, -1.4183908f,
                +1.4183908f, +1.4183908f, +0.5875167f, +1.4183908f,
                +1.4183908f, +1.4183908f, +0.5875167f, -1.4183908f,
                +1.4183908f, -1.4183908f, +0.5875167f, +1.4183908f,
                +1.4183908f, -1.4183908f, +0.5875167f, -1.4183908f,
                -1.4183908f, +1.4183908f, +0.5875167f, +1.4183908f,
                -1.4183908f, +1.4183908f, +0.5875167f, -1.4183908f,
                -1.4183908f, -1.4183908f, +0.5875167f, +1.4183908f,
                -1.4183908f, -1.4183908f, +0.5875167f, -1.4183908f,
                +1.4183908f, +1.4183908f, +1.4183908f, +0.5875167f,
                +1.4183908f, +1.4183908f, -1.4183908f, +0.5875167f,
                +1.4183908f, -1.4183908f, +1.4183908f, +0.5875167f,
                +1.4183908f, -1.4183908f, -1.4183908f, +0.5875167f,
                -1.4183908f, +1.4183908f, +1.4183908f, +0.5875167f,
                -1.4183908f, +1.4183908f, -1.4183908f, +0.5875167f,
                -1.4183908f, -1.4183908f, +1.4183908f, +0.5875167f,
                -1.4183908f, -1.4183908f, -1.4183908f, +0.5875167f,
        };
    /**
     * This gradient vector array was quasi-randomly generated after a lot of rejection sampling. Each gradient should
     * have a magnitude of 2.0, matching the magnitude of the center of an edge of a 5D hypercube.
     * This may bias slightly in some directions. The sums of the x, y, z, w, and u components of all 256 vectors are:
     * <br>
     * x: +0.52959638973427, y: +0.31401370534460, z: -0.14792091580658, w: -0.00781214643439, u: -0.58206620017072
     */
    protected static final float[] GRAD_5D = {
        -1.6797903571f, -0.0690921662f, -0.7098031356f, -0.5887570823f, +0.5683970756f, 0f, 0f, 0f,
        -1.0516780588f, -0.2945340815f, -1.4440603796f, +0.7418854274f, -0.4141480030f, 0f, 0f, 0f,
        +1.0641252713f, -1.5650070200f, +0.4156350353f, +0.1130875224f, +0.4825444684f, 0f, 0f, 0f,
        +0.8695556873f, +1.0264500068f, -0.3870691013f, -1.1785230203f, -0.8071767413f, 0f, 0f, 0f,
        +0.4036843343f, +0.2265883553f, -1.6373381485f, +0.7147763885f, +0.7706589242f, 0f, 0f, 0f,
        +0.1852080234f, -0.7234241829f, +1.3347979534f, -0.7398257504f, -1.0551434605f, 0f, 0f, 0f,
        -0.1798280717f, -0.9172834905f, -0.1660562308f, +1.5451496683f, +0.8433212279f, 0f, 0f, 0f,
        -0.5376087193f, +1.4095478895f, -1.2573362952f, +0.1736254636f, -0.3363201621f, 0f, 0f, 0f,
        -0.8831071523f, +0.4890748406f, +0.7809592873f, -0.9126098448f, +1.2402311964f, 0f, 0f, 0f,
        -1.7880012565f, -0.0832774541f, -0.0688806429f, +0.8681275071f, -0.1942330063f, 0f, 0f, 0f,
        +1.1634898551f, -0.5052769528f, -0.7356836999f, -0.2313504020f, +1.3402361893f, 0f, 0f, 0f,
        +0.5846946797f, -1.2424919047f, +0.6407004403f, +1.3053017243f, -0.0060293368f, 0f, 0f, 0f,
        +0.4938778800f, +0.7783935437f, +0.0680362272f, +0.1949810810f, -1.7628220502f, 0f, 0f, 0f,
        +0.3495453088f, +0.3175464510f, -1.2837807206f, -1.4389420883f, +0.2415265131f, 0f, 0f, 0f,
        -0.0814475545f, -0.3645019914f, +1.2414338549f, +0.7877420883f, -1.3033836658f, 0f, 0f, 0f,
        -0.6130443974f, -1.7598572531f, +0.3278510206f, -0.4244846722f, +0.4892908001f, 0f, 0f, 0f,
        -0.4462734487f, +0.7987181596f, -0.3741235663f, +1.6266729545f, -0.6138859559f, 0f, 0f, 0f,
        -1.1190041124f, +0.4387897882f, +1.5187622470f, +0.2310331368f, +0.4419029812f, 0f, 0f, 0f,
        +1.7898523809f, -0.0730765445f, +0.2593137052f, -0.6196725486f, -0.5829670729f, 0f, 0f, 0f,
        +1.2710361476f, -0.7953333027f, -0.5194550615f, +0.9617110332f, +0.7464518582f, 0f, 0f, 0f,
        +0.3919460233f, -1.2475586928f, -1.4706983192f, -0.1307051020f, -0.3315693791f, 0f, 0f, 0f,
        +0.2652336693f, +0.6189864328f, +0.3777315952f, -1.7165368300f, +0.6762596023f, 0f, 0f, 0f,
        +0.1369902659f, +0.5491538637f, -1.0396634959f, +0.9490333448f, -1.3031113639f, 0f, 0f, 0f,
        -0.2401683431f, -0.3733848671f, -1.4613950663f, -0.7227050436f, +1.0700115833f, 0f, 0f, 0f,
        -0.6698938436f, -1.3422700176f, +0.7466878175f, +1.0575187021f, -0.2714128520f, 0f, 0f, 0f,
        -0.8847555645f, +1.1306623120f, -0.1640964357f, -0.1686079479f, +1.3723899034f, 0f, 0f, 0f,
        -1.1982151304f, +0.3128615080f, -0.8450972304f, -1.3226474382f, -0.0530339816f, 0f, 0f, 0f,
        +0.8151064240f, -0.0707387889f, +0.4722986821f, +0.1916252778f, -1.7523730337f, 0f, 0f, 0f,
        +1.2690966076f, -1.1058707966f, -0.0729186016f, -1.0707270924f, +0.1211195821f, 0f, 0f, 0f,
        +0.2853585791f, -1.5643353649f, -0.5748320773f, +0.5808419374f, -0.8964463588f, 0f, 0f, 0f,
        +0.2535726091f, +1.1620185372f, +1.5502829093f, -0.2230925697f, +0.3636845578f, 0f, 0f, 0f,
        -0.1259274379f, +0.1397280645f, +0.0818804260f, -1.6542088566f, -1.1052180794f, 0f, 0f, 0f,
        -0.7748098968f, -0.7541305772f, -1.3684352844f, +0.6640618209f, +0.7192798250f, 0f, 0f, 0f,
        -0.7154067153f, -1.0897763229f, +1.1541033599f, -0.5995215703f, -0.7805127283f, 0f, 0f, 0f,
        -1.2205329558f, +1.1140489716f, +0.2019395367f, +0.9671922075f, +0.5412521130f, 0f, 0f, 0f,
        +1.7763124224f, +0.3884232272f, -0.5590859360f, -0.0997516807f, -0.6093554733f, 0f, 0f, 0f,
        +0.7941439015f, -0.1125633933f, +1.2801756800f, -1.1687349208f, +0.5931895645f, 0f, 0f, 0f,
        +1.0158348693f, -1.2589195605f, +0.5779670539f, +0.6776054453f, -0.7681184828f, 0f, 0f, 0f,
        +0.2112048908f, +1.7680263830f, -0.3219879142f, -0.4419318676f, +0.7283510216f, 0f, 0f, 0f,
        -0.0026910087f, +0.5409839017f, -1.7270071907f, +0.8213951690f, -0.2237974892f, 0f, 0f, 0f,
        -0.4138014120f, +0.1597450584f, +0.6839984196f, -0.0929507291f, +1.8239397555f, 0f, 0f, 0f,
        -0.7659506384f, -0.5475010929f, -0.3720789651f, -1.7162535971f, -0.1720261813f, 0f, 0f, 0f,
        -0.7070622912f, -0.8458704904f, -1.0146426125f, +0.3071423194f, +1.2886931343f, 0f, 0f, 0f,
        -1.6125362501f, +0.9425610444f, +0.5399791622f, -0.4685942374f, +0.0121435146f, 0f, 0f, 0f,
        +1.0263600815f, +0.3094855666f, -0.1357539876f, +0.9416267863f, -1.3948883530f, 0f, 0f, 0f,
        +1.0884856898f, -0.2412950015f, -1.6426714098f, -0.0397577982f, +0.2388002976f, 0f, 0f, 0f,
        +0.3883496101f, -0.7333843774f, +0.7553963021f, -1.1941140952f, -1.1466472386f, 0f, 0f, 0f,
        +0.1101824785f, +1.9193422531f, -0.0349560249f, +0.4586533562f, +0.3039741964f, 0f, 0f, 0f,
        -0.2151896625f, +0.8619434800f, -1.1688233084f, -0.6467741803f, -1.1942705221f, 0f, 0f, 0f,
        -0.5440612093f, +0.1020041479f, +1.1614695684f, +1.4233071754f, +0.5646040033f, 0f, 0f, 0f,
        -1.3903047596f, -0.7781814736f, +0.1518957001f, +0.0172015182f, -1.1992156077f, 0f, 0f, 0f,
        -1.1352909369f, -1.0508611233f, -0.5994729301f, -0.9722493258f, +0.5496988654f, 0f, 0f, 0f,
        +1.3336722136f, +0.8735367803f, +1.0383655970f, +0.4365890905f, -0.4352456471f, 0f, 0f, 0f,
        +1.3114501486f, +0.4918768452f, +0.3084333813f, -0.6495376384f, +1.2333391190f, 0f, 0f, 0f,
        +0.6896294960f, -0.2419287464f, -0.7141267659f, +1.6588951215f, -0.4516321269f, 0f, 0f, 0f,
        +0.2176968344f, -0.7421851123f, +1.5213707725f, +0.0438834617f, +1.0417651183f, 0f, 0f, 0f,
        -0.0434372972f, +1.6845774504f, +0.3229918793f, -1.0108819828f, -0.1846777672f, 0f, 0f, 0f,
        -0.3651204958f, +0.6939929190f, -0.4562428562f, +0.6199070461f, +1.6711129711f, 0f, 0f, 0f,
        -0.5890165438f, +0.0561767268f, -1.8733437161f, -0.3722429586f, -0.0438427600f, 0f, 0f, 0f,
        -0.7545212813f, -0.3365185970f, +0.3380918399f, +0.9776020270f, -1.4991467755f, 0f, 0f, 0f,
        -1.7417773586f, -0.9568393557f, -0.2040755992f, +0.0614347980f, +0.0724499544f, 0f, 0f, 0f,
        +0.8480496705f, +0.7472072627f, -1.0543920416f, -0.7610320599f, -1.0156676077f, 0f, 0f, 0f,
        +1.1550078136f, +0.5368673805f, +1.0697388270f, +1.0270433372f, +0.4225768470f, 0f, 0f, 0f,
        +0.6091830897f, -0.3632960094f, -0.2588786131f, -0.6327424895f, -1.7405547329f, 0f, 0f, 0f,
        +0.0677925852f, -0.7943979716f, -1.0479221567f, +1.4543495597f, +0.3886676471f, 0f, 0f, 0f,
        -0.2061357682f, +1.6481340611f, +0.7904935004f, +0.1201597286f, -0.7757859417f, 0f, 0f, 0f,
        -0.7481241996f, +0.8815306333f, -0.0389302309f, -1.3935543711f, +0.8483540397f, 0f, 0f, 0f,
        -1.1501637940f, +0.0500560844f, -1.1550196052f, +0.8588373495f, -0.7764958172f, 0f, 0f, 0f,
        -1.4576210450f, -0.4980765043f, +0.9775175852f, -0.3244367280f, +0.7526359448f, 0f, 0f, 0f,
        +1.0804925776f, -1.0462781211f, +0.0745691035f, +1.2771082010f, -0.3182325797f, 0f, 0f, 0f,
        +0.9560363853f, +1.0747532707f, -0.7908249620f, +0.1795273343f, +1.1283907359f, 0f, 0f, 0f,
        +0.5870023920f, +0.3518098165f, +1.5130869695f, -1.0689826362f, -0.3154393619f, 0f, 0f, 0f,
        +0.2461487893f, -0.3086153639f, +0.2921558695f, +0.9112883678f, +1.7112468522f, 0f, 0f, 0f,
        -0.1666414465f, -1.6148302394f, -1.0133051505f, -0.5432021594f, -0.2066349729f, 0f, 0f, 0f,
        -0.2082660083f, +0.8616008908f, +0.9278341202f, +1.0618169303f, +1.1072207669f, 0f, 0f, 0f,
        -1.4200071139f, +1.1449937745f, +0.7148016266f, +0.3951739916f, +0.0739270175f, 0f, 0f, 0f,
        -1.0824868745f, +0.0130967819f, -0.3737068064f, -0.7706672311f, -1.4472269630f, 0f, 0f, 0f,
        +1.3772509463f, -0.3564008886f, -1.3081930141f, +0.4995798772f, +0.1233256728f, 0f, 0f, 0f,
        +0.9497908429f, -1.3263097649f, +0.4502084198f, -0.2307263072f, -1.0406140073f, 0f, 0f, 0f,
        +0.4549745216f, +0.6615623933f, -0.1955222409f, +1.8045985192f, +0.2460256534f, 0f, 0f, 0f,
        +0.3671055129f, +0.3148111115f, -1.6182062419f, +0.2769362348f, -1.0348151463f, 0f, 0f, 0f,
        +0.0481966276f, -0.4532364953f, +1.1128663911f, -1.3414977121f, +0.8684273419f, 0f, 0f, 0f,
        -0.3576449008f, -1.2810416482f, -0.2006980071f, +1.1378443353f, -0.9466007601f, 0f, 0f, 0f,
        -0.5489241973f, +1.4436359278f, -1.0580643935f, -0.2111030853f, +0.6712173717f, 0f, 0f, 0f,
        -0.7396913767f, +0.4241285251f, +0.6373931479f, -1.6490546808f, -0.3838232614f, 0f, 0f, 0f,
        -1.7438367476f, -0.0103026532f, -0.0174746056f, +0.2859053214f, +0.9364187303f, 0f, 0f, 0f,
        +1.4125223773f, -0.6136774864f, -0.9382744610f, -0.7882620843f, -0.3556183326f, 0f, 0f, 0f,
        +0.6333525580f, -1.2469837002f, +0.8203449431f, +0.6945417557f, +0.9426251178f, 0f, 0f, 0f,
        +0.8639745852f, +1.7229496217f, +0.2131097409f, -0.3490329851f, -0.3431511780f, 0f, 0f, 0f,
        +0.1160084005f, +0.1925427348f, -0.5469449523f, -1.4198630543f, +1.2784011391f, 0f, 0f, 0f,
        -0.1960368134f, -0.4241632531f, +1.8889399989f, +0.4605830623f, -0.0377362652f, 0f, 0f, 0f,
        -0.3716846054f, -0.8276497199f, +0.2058886823f, -0.5926340109f, -1.6683049107f, 0f, 0f, 0f,
        -0.7995956039f, +1.4545513458f, -0.5567146701f, +0.9584702276f, +0.1277922200f, 0f, 0f, 0f,
        -0.9905083489f, +0.4012227581f, +1.3537558791f, -0.1090892883f, -1.0066568711f, 0f, 0f, 0f,
        +1.4450754379f, -0.0281787255f, +0.3053200605f, -1.3288357283f, +0.2278995524f, 0f, 0f, 0f,
        +1.2162147152f, -0.7478839823f, -0.4936637037f, +0.4427814597f, -1.2335850364f, 0f, 0f, 0f,
        +0.4288156741f, -1.2286191885f, -1.4078773154f, -0.4695345709f, +0.3225379959f, 0f, 0f, 0f,
        +0.3329858839f, +1.0484961431f, +0.6324502386f, +1.2260808594f, -0.9415458889f, 0f, 0f, 0f,
        -0.0430825232f, +0.6204968828f, -0.7408650600f, -0.2917703779f, +1.7260117393f, 0f, 0f, 0f,
        -0.2831108338f, -0.2973701593f, -1.2778575475f, -1.3826667300f, -0.5354736652f, 0f, 0f, 0f,
        -0.7626701307f, -1.2292796278f, +0.8192695846f, +0.4886037879f, +0.9986338441f, 0f, 0f, 0f,
        -1.1212378397f, +1.4564460164f, -0.1452464147f, -0.6418766528f, -0.4341526800f, 0f, 0f, 0f,
        -1.4371859530f, +0.3591868101f, -0.7832229698f, +0.7741764284f, +0.7698662281f, 0f, 0f, 0f,
        +1.6195535741f, -0.0783305926f, +1.1220763529f, -0.0880739971f, -0.3226424776f, 0f, 0f, 0f,
        +0.6736622539f, -0.5801267229f, -0.0064584923f, -1.2469663463f, +1.2863379696f, 0f, 0f, 0f,
        +0.3808337389f, -1.7282317745f, -0.8266342493f, +0.4213073506f, -0.0857702241f, 0f, 0f, 0f,
        +0.0748521918f, +0.5865055185f, +0.7547226638f, -0.3937892986f, +1.7104771601f, 0f, 0f, 0f,
        -0.3050023119f, +0.3332256435f, +0.2039469964f, +1.9348633092f, +0.1031690730f, 0f, 0f, 0f,
        -0.5486929801f, -0.3926995085f, -0.7835797197f, -0.0323895314f, -1.7116298814f, 0f, 0f, 0f,
        -0.7373648248f, -0.9164391411f, +1.1634541527f, -1.1082134698f, +0.1861981626f, 0f, 0f, 0f,
        -1.2396832556f, +1.1286466143f, +0.2193465590f, +0.4244818926f, -0.9803287488f, 0f, 0f, 0f,
        +1.7118249987f, +0.5111342927f, -0.5816150480f, -0.5527569748f, +0.4056853108f, 0f, 0f, 0f,
        +0.7213413610f, -0.0659398302f, +1.4422534178f, +0.9666694057f, -0.6788032989f, 0f, 0f, 0f,
        +0.9873966195f, -1.2334566504f, +0.7110411579f, +0.0172849954f, +0.9988765230f, 0f, 0f, 0f,
        +0.1849030939f, -1.6262998800f, -0.3182014494f, -0.9668115017f, -0.5338379006f, 0f, 0f, 0f,
        -0.0537861903f, +0.7112275325f, -1.6810226484f, +0.4784138168f, +0.6607159134f, 0f, 0f, 0f,
        -0.7517873085f, +0.3686878741f, +1.1316388506f, -0.9931706665f, -1.0158201777f, 0f, 0f, 0f,
        -0.7479636489f, -0.4087729589f, -0.2801205440f, +1.4488805036f, +1.0467725708f, 0f, 0f, 0f,
        -1.0753364436f, -1.0487010364f, -1.2861467341f, +0.0451559898f, -0.2960830697f, 0f, 0f, 0f,
        -1.6717166425f, +0.6193692618f, +0.3444359164f, -0.5570386011f, +0.6267512114f, 0f, 0f, 0f,
        +1.6653427265f, +0.6514011681f, -0.1843800816f, +0.8463999253f, -0.2278624001f, 0f, 0f, 0f,
        +0.6180555713f, -0.0980890088f, -0.9637326948f, -0.3818490941f, +1.5917903189f, 0f, 0f, 0f,
        +0.3828037090f, -0.7608509481f, +0.9360620892f, +1.5486593545f, -0.0030206309f, 0f, 0f, 0f,
        +0.0416485569f, -1.5762523250f, +0.0019777673f, +0.0585731018f, -1.2289260701f, 0f, 0f, 0f,
        -0.2886712161f, +0.9630135494f, -1.0923275687f, -1.3265794576f, +0.1904763974f, 0f, 0f, 0f,
        -0.5764811629f, +0.1590907789f, +1.1606879290f, +0.6689389883f, -1.3592953154f, 0f, 0f, 0f,
        -1.6356922055f, -0.7138956424f, +0.2340692949f, -0.6808182666f, +0.5445751314f, 0f, 0f, 0f,
        -1.1383732794f, -0.8340752557f, -0.4924316867f, +1.1297774686f, -0.6996703867f, 0f, 0f, 0f,
        +1.2119764801f, +1.0042477319f, +1.1627125168f, +0.1052984231f, +0.3995138920f, 0f, 0f, 0f,
        +1.0848959808f, +0.5299382966f, +0.3338775173f, -1.2410743362f, -0.9436240820f, 0f, 0f, 0f,
        +0.8223389027f, -0.2257269798f, -0.8855454083f, +1.1320984930f, +1.0986211320f, 0f, 0f, 0f,
        +0.1696512818f, -0.6844004252f, +1.7720906716f, -0.3171057932f, -0.5118135090f, 0f, 0f, 0f,
        -0.0617271001f, +1.6228010367f, +0.2362036330f, +1.0239074576f, +0.5084564115f, 0f, 0f, 0f,
        -0.8016909939f, +1.4462165555f, -0.7627188444f, +0.3252216885f, -0.7604209640f, 0f, 0f, 0f,
        -0.6115306073f, +0.1014550431f, -1.4858078470f, -0.7519599396f, +0.9179697607f, 0f, 0f, 0f,
        -1.5359735435f, -0.5360812013f, +0.6803716202f, +0.9022898547f, -0.2763506754f, 0f, 0f, 0f,
        +1.4311848509f, -0.8591027804f, -0.1752995920f, -0.2145555860f, +1.0662496372f, 0f, 0f, 0f,
        +0.7410642280f, +0.7990758023f, -0.9368640780f, +1.3900908545f, -0.0472735412f, 0f, 0f, 0f,
        +0.4550755889f, +0.2813149456f, +0.5064435170f, +0.1454080862f, -1.8536827027f, 0f, 0f, 0f,
        +0.6584368336f, -0.3398656764f, -0.2473926483f, -1.8321141033f, +0.1819534238f, 0f, 0f, 0f,
        +0.0159960331f, -0.7374889492f, -1.0065472324f, +0.7388568967f, -1.3772462858f, 0f, 0f, 0f,
        -0.2299702397f, +1.8176358053f, +0.7442497214f, -0.2206381235f, +0.2018042090f, 0f, 0f, 0f,
        -0.4069426745f, +0.4769186078f, +0.0089269758f, +1.7464025964f, -0.7462871978f, 0f, 0f, 0f,
        -1.4305778226f, +0.1421159811f, -1.2165719887f, +0.3471454458f, +0.5767952644f, 0f, 0f, 0f,
        -1.4621197220f, -0.3747993576f, +0.9054068790f, -0.6585117031f, -0.6843479237f, 0f, 0f, 0f,
        +1.2555507001f, -1.2133185727f, +0.1361145959f, +0.7938459453f, +0.5502107892f, 0f, 0f, 0f,
        +0.9623281537f, +1.3224211051f, -0.8148529505f, -0.2708155140f, -0.7666815323f, 0f, 0f, 0f,
        +0.3174348857f, +0.2633414906f, +1.0144165277f, -1.5786067523f, +0.5557393117f, 0f, 0f, 0f,
        +0.4312067006f, -0.5747179681f, +0.8536422312f, +0.8761256911f, -1.4097725891f, 0f, 0f, 0f,
        -0.1886268643f, -1.0208135472f, -0.6506500504f, -0.9477019512f, +1.2652569429f, 0f, 0f, 0f,
        -0.3048749941f, +1.3023137339f, +1.3472498676f, +0.5983791689f, -0.1946544138f, 0f, 0f, 0f,
        -0.9288706884f, +0.7613446467f, +0.4729501186f, -0.2114483296f, +1.5129974760f, 0f, 0f, 0f,
        -1.1557323498f, +0.0638806278f, -0.3210150212f, -1.5950470819f, -0.1139129657f, 0f, 0f, 0f,
        +1.0864354794f, -0.3052283529f, -1.1052395274f, +0.2022026495f, +1.2099806929f, 0f, 0f, 0f,
        +1.0414087896f, -1.4163018217f, +0.5940404283f, -0.7457758569f, +0.0221635650f, 0f, 0f, 0f,
        +0.5070316235f, +0.9137533277f, -0.2073217572f, +0.8288949911f, -1.4757793099f, 0f, 0f, 0f,
        +0.3763094088f, +0.4850535903f, -1.8754774606f, -0.2080484396f, +0.2498287114f, 0f, 0f, 0f,
        -0.0253081105f, -0.1921838222f, +0.6575303806f, -1.5122491502f, -1.1149803515f, 0f, 0f, 0f,
        -0.6196419069f, -1.6338762858f, -0.2048715266f, +0.7010005938f, +0.6427425729f, 0f, 0f, 0f,
        -0.5308926042f, +1.4556534130f, -0.8522869910f, -0.5344412052f, -0.7662934602f, 0f, 0f, 0f,
        -1.1271692683f, +0.6619484351f, +0.9450688957f, +1.0599681920f, +0.5240476355f, 0f, 0f, 0f,
        -1.8934489402f, +0.0438491543f, +0.0205347023f, -0.0947675875f, -0.6352368005f, 0f, 0f, 0f,
        +0.5103230547f, +1.3058156973f, +0.1990338991f, -0.7882347287f, +1.1719587297f, 0f, 0f, 0f,
        +0.1384792574f, +0.4610276778f, -0.9727270207f, +1.5951805055f, -0.5267620653f, 0f, 0f, 0f,
        -0.2073797520f, -0.2507461010f, +1.5291534160f, -0.0725161583f, +1.2452113349f, 0f, 0f, 0f,
        -0.5725773198f, -1.0055906561f, +0.3247380428f, -1.5826348743f, -0.2252880459f, 0f, 0f, 0f,
        -0.6862103326f, +1.2996571076f, -0.3961010577f, +0.3505477796f, +1.2490904645f, 0f, 0f, 0f,
        -1.0941521107f, +0.4477460716f, +1.5583661596f, -0.4156823874f, -0.0325219850f, 0f, 0f, 0f,
        +1.0615422136f, +0.0168716535f, +0.2909809882f, +0.7952955764f, -1.4682229009f, 0f, 0f, 0f,
        +0.3529574716f, -0.9860437746f, -1.1438219776f, -0.8624789958f, -0.9224640715f, 0f, 0f, 0f,
        +0.3425330274f, +1.5160688884f, +0.9006480000f, +0.7732736314f, +0.4184343698f, 0f, 0f, 0f,
        -0.1182208812f, +0.4689801454f, -0.3711656837f, -0.8412805777f, -1.7089659070f, 0f, 0f, 0f,
        -0.3895150255f, -0.2763904657f, -1.3594381746f, +1.3110052175f, +0.4528570686f, 0f, 0f, 0f,
        -0.8866701020f, -1.1592070785f, +0.9217069399f, +0.0108062128f, -1.0101458419f, 0f, 0f, 0f,
        -0.9839606799f, +1.3163966058f, -0.0810864936f, -1.0154752113f, +0.5110346685f, 0f, 0f, 0f,
        +1.7393575679f, +0.3972242300f, -0.7097572208f, +0.3707578686f, -0.4190840636f, 0f, 0f, 0f,
        +1.2992926783f, -0.0003032116f, +1.0675928831f, -0.5467297666f, +0.9344358954f, 0f, 0f, 0f,
        +0.3309152609f, -1.5010777228f, -0.7884782610f, +0.0452028175f, +1.0067370548f, 0f, 0f, 0f,
        +0.0527154815f, +0.9848513540f, +1.2271602344f, -1.2005994995f, -0.2827145013f, 0f, 0f, 0f,
        -1.1072848983f, -0.5733937749f, -1.2917946615f, -0.8540935843f, -0.2166343341f, 0f, 0f, 0f,
        -0.5785672345f, -0.5892745270f, +0.9002794950f, +0.8827318293f, +1.3146470384f, 0f, 0f, 0f,
        +1.1323242306f, +0.4385085158f, -0.3984529066f, -0.8482583731f, -1.2834504790f, 0f, 0f, 0f,
        +0.6832479100f, -0.0203722774f, +1.8021714033f, +0.5087858832f, +0.1614695700f, 0f, 0f, 0f,
        +0.6295136760f, -0.7957220411f, +0.5735752524f, -0.5094408070f, -1.5433795577f, 0f, 0f, 0f,
        +0.1464145243f, -1.4152600929f, -0.2997028564f, +1.3388224398f, +0.3055066758f, 0f, 0f, 0f,
        -0.1117532528f, +0.8429678828f, -1.5895178521f, +0.1184502189f, -0.8580902647f, 0f, 0f, 0f,
        -0.6186591707f, +0.3491930628f, +0.8652060160f, -1.4602096806f, +0.7839204512f, 0f, 0f, 0f,
        -1.1893740310f, -0.4873888685f, -0.3368700002f, +1.0489488764f, -1.0649255199f, 0f, 0f, 0f,
        -1.1495757072f, -0.9135567011f, -1.1488759605f, -0.3139079113f, +0.6522543198f, 0f, 0f, 0f,
        +1.2507068251f, +0.9082986588f, +0.4849121115f, +1.1269927255f, -0.3247670313f, 0f, 0f, 0f,
        +1.3812528182f, +0.6859061245f, -0.1144675881f, +0.2171605156f, +1.2495646931f, 0f, 0f, 0f,
        +0.8074888914f, -0.0650160121f, -1.3097078722f, -1.2004749134f, -0.4327353465f, 0f, 0f, 0f,
        +0.3228920807f, -0.6888576407f, +1.0170445092f, +0.7876568168f, +1.3290722555f, 0f, 0f, 0f,
        +0.0052441537f, -1.9617941884f, +0.0477654540f, -0.3352049620f, -0.1915519670f, 0f, 0f, 0f,
        -0.2001799378f, +0.5900368361f, -0.5616998042f, +1.3787410489f, +1.1812497512f, 0f, 0f, 0f,
        -0.9015314851f, +0.3110012919f, +1.7320694688f, +0.2992817832f, -0.0297480605f, 0f, 0f, 0f,
        -0.8591940915f, -0.2863601066f, +0.1461357370f, -0.7274398339f, -1.6214990092f, 0f, 0f, 0f,
        +0.9395832683f, +0.9730323926f, +1.0982291200f, -0.1930711401f, -0.9628123284f, 0f, 0f, 0f,
        +0.5731182373f, +0.3581009267f, +0.2258645391f, +1.8565151569f, +0.2136255940f, 0f, 0f, 0f,
        +0.7011674479f, -0.1226870736f, -0.7909781480f, +0.3959247471f, -1.6464839070f, 0f, 0f, 0f,
        +0.0954972405f, -0.5011463729f, +1.8032529962f, -0.6086202714f, +0.3429177553f, 0f, 0f, 0f,
        -0.1412424735f, -1.6893856796f, +0.3886472390f, +0.7238267164f, -0.6716061327f, 0f, 0f, 0f,
        -0.7766531806f, +1.3490341636f, -0.5674058616f, -0.3739667103f, +1.0559906015f, 0f, 0f, 0f,
        -1.5471155376f, -0.4117408550f, +0.6692645122f, +0.3161027907f, +0.9429035051f, 0f, 0f, 0f,
        +1.5334460025f, -1.0006420984f, -0.1888257316f, -0.6902112872f, -0.3677118033f, 0f, 0f, 0f,
        +0.7809057187f, +1.0330833001f, -1.0077018800f, +0.7218704992f, +0.8867722690f, 0f, 0f, 0f,
        +1.0334456008f, +0.8361364463f, +1.3880171764f, -0.3382417163f, -0.4380261325f, 0f, 0f, 0f,
        -0.0634231536f, -1.1102290519f, -1.5755978089f, +0.5124396730f, -0.1351520699f, 0f, 0f, 0f,
        -0.1846156117f, -1.2027985685f, +0.5261837867f, -0.3886987023f, +1.4461108606f, 0f, 0f, 0f,
        -1.4795808324f, -0.2528893855f, +0.7657021415f, -1.0677045314f, +0.1435088265f, 0f, 0f, 0f,
        +0.8358974012f, +1.4130062170f, -0.7246852387f, -0.7614331388f, +0.4469226390f, 0f, 0f, 0f,
        +0.3586931337f, +0.4076326318f, +1.4558997393f, +0.9580949406f, -0.8170586927f, 0f, 0f, 0f,
        +0.2457835444f, -0.3744186486f, +0.9525361175f, -0.3232545651f, +1.6696055091f, 0f, 0f, 0f,
        -0.2213847655f, -0.7780999043f, -0.5024501129f, -1.6139364700f, -0.6987862901f, 0f, 0f, 0f,
        -0.2574375805f, -1.3890644186f, +1.3509472519f, +0.2010518329f, +0.3724857264f, 0f, 0f, 0f,
        -1.2190443421f, +1.0117162629f, +0.6237377737f, -0.8273041068f, -0.6456626053f, 0f, 0f, 0f,
        -1.4202182379f, +0.1260515345f, -0.3099756452f, +1.0152805943f, +0.9166305590f, 0f, 0f, 0f,
        +1.3394545490f, -0.3743458036f, -1.4096086888f, -0.0615786809f, -0.2737483172f, 0f, 0f, 0f,
        +0.7171369574f, -0.9616513483f, +0.4897305876f, -1.1232009395f, +1.0293322446f, 0f, 0f, 0f,
        +0.1779667703f, +0.3504282910f, -1.0568440088f, -0.4869239513f, +1.5784529288f, 0f, 0f, 0f,
        -0.1204364412f, -0.2136700341f, +1.1047461721f, +1.6490450828f, +0.0051371575f, 0f, 0f, 0f,
        -0.3871281276f, -0.7735057325f, -0.0665288715f, -0.0311266269f, -1.8017840428f, 0f, 0f, 0f,
        -1.1000913946f, +0.6549589413f, +0.8947793392f, +0.4521499773f, -1.1643702335f, 0f, 0f, 0f,
        +1.9292603742f, +0.0932676759f, +0.0543343169f, -0.4404212957f, +0.2689468598f, 0f, 0f, 0f,
        +1.0366997829f, -0.4235317472f, -0.7352650119f, +1.1718961062f, -0.9120961013f, 0f, 0f, 0f,
        +0.3604156986f, +1.2092120205f, +0.2110514542f, -1.3105326841f, -0.8036592445f, 0f, 0f, 0f,
        +0.0668638830f, +0.6759071640f, -1.0954065614f, +0.9579351192f, +1.1921088455f, 0f, 0f, 0f,
        -0.2878226515f, -0.1988228335f, +1.7896272052f, -0.5362533838f, -0.6223297975f, 0f, 0f, 0f,
        -0.5998366869f, -0.8396322334f, +0.3455361034f, +1.4029135129f, +0.9206802585f, 0f, 0f, 0f,
        -0.8343248750f, +1.7431140670f, -0.3975040210f, +0.0398856933f, -0.3253537111f, 0f, 0f, 0f,
        +1.7378988722f, +0.1069270956f, +0.5947543807f, +0.7345800753f, -0.2737397409f, 0f, 0f, 0f,
        +0.3669190706f, -1.0350628357f, -1.2227443172f, +1.1386733496f, -0.0483183134f, 0f, 0f, 0f,
        -0.4256376886f, -0.1980842267f, -1.1814821082f, +0.6186059704f, -1.4145748049f, 0f, 0f, 0f,
        -1.0694433220f, -1.1388959357f, +1.0788632536f, -0.5297257984f, +0.3386025507f, 0f, 0f, 0f,
        -0.8783535738f, +1.2475299432f, -0.0376993977f, +1.0653029541f, -0.7320330673f, 0f, 0f, 0f,
        +1.6644650041f, +0.5820689456f, -0.8613458094f, +0.1111061909f, +0.3694466184f, 0f, 0f, 0f,
        +1.0607200718f, +0.0620356569f, +1.0296431488f, -1.0302379349f, -0.8657189441f, 0f, 0f, 0f,
        -0.8817724023f, +0.9515735227f, +0.6010913410f, +0.4766382991f, -1.3147206521f, 0f, 0f, 0f,
        -0.7611137823f, -0.2756268185f, -0.7300242585f, -1.1275552035f, +1.2411363795f, 0f, 0f, 0f,
        -1.3207783071f, +1.1561698454f, +0.2299470218f, -0.2072522588f, +0.9071862105f, 0f, 0f, 0f,
        -1.1816771520f, -0.7596862015f, -0.9827823279f, -0.6774291571f, -0.7757219970f, 0f, 0f, 0f,
        +1.2474994489f, +1.2266679741f, +0.6167132624f, +0.6372268146f, +0.3906885524f, 0f, 0f, 0f,
        +1.4101961346f, +0.8763908320f, -0.0679690545f, -0.3381071150f, -1.0603536005f, 0f, 0f, 0f,
        +0.4303889934f, +0.0075456308f, -0.7318402639f, -1.7280562703f, +0.5412390715f, 0f, 0f, 0f,
        -1.0150772094f, -0.2501828730f, +0.1938295376f, -1.6850991645f, -0.1729095290f, 0f, 0f, 0f,
        -0.2491682380f, -1.8343103261f, +0.5570892947f, +0.4096496582f, +0.3083171940f, 0f, 0f, 0f,
        +0.6707055360f, +0.7050912787f, +1.0951484850f, -0.8144527819f, +1.0910164227f, 0f, 0f, 0f,
        -0.1253944377f, -0.8069577491f, -1.1981624979f, -0.0909347438f, +1.3744936985f, 0f, 0f, 0f,
        +0.4979431688f, +1.0477297741f, -0.4424841168f, -0.9992478515f, -1.2083155460f, 0f, 0f, 0f,
        +0.3391283580f, +0.5297397571f, +1.8127693422f, +0.5200000016f, +0.2187122697f, 0f, 0f, 0f,
        +0.1729941911f, +0.5513060812f, -1.3295779972f, -1.3236932093f, -0.3823522614f, 0f, 0f, 0f,
        -0.1759985101f, -0.1116624120f, +1.0347327507f, +0.7188695866f, +1.5391915677f, 0f, 0f, 0f,
        +1.3834109634f, -0.5319875518f, -1.0053750542f, +0.8686683761f, +0.1944212023f, 0f, 0f, 0f,
        +0.2655537132f, +1.2074447952f, +0.2300093933f, +1.5279397437f, +0.2899208694f, 0f, 0f, 0f,
        -0.7650007456f, -1.7462692514f, -0.2985746155f, -0.2497276182f, +0.4623925569f, 0f, 0f, 0f,
        +1.5421515027f, +0.1809242613f, +0.6454387145f, +0.2020302919f, +1.0637799497f, 0f, 0f, 0f,
    };

    protected static final float[] GRAD_6D = {
            +0.31733186f, +0.04359915f, -0.63578104f, +0.60224147f, -0.06199565f, +0.35587048f, +0f, +0f,
            -0.54645425f, -0.75981513f, -0.03514434f, +0.13137365f, +0.29650029f, +0.13289887f, +0f, +0f,
            +0.72720729f, -0.01705130f, +0.10403853f, +0.57016794f, +0.10006650f, -0.35348266f, +0f, +0f,
            +0.05248672f, +0.16599786f, -0.49406271f, +0.51847470f, +0.63927166f, -0.21933445f, +0f, +0f,
            -0.57224122f, -0.08998594f, +0.44829955f, +0.53836681f, -0.05129933f, -0.41352093f, +0f, +0f,
            -0.35034584f, -0.37367516f, -0.52676009f, +0.12379417f, +0.42566489f, +0.51345191f, +0f, +0f,
            +0.40936909f, +0.33036021f, +0.46771483f, +0.15073372f, +0.51541333f, -0.46491971f, +0f, +0f,
            -0.64339751f, -0.29341468f, -0.50841617f, -0.08065981f, -0.46873502f, -0.12345817f, +0f, +0f,
            +0.46950904f, +0.41685007f, -0.33378791f, -0.39617029f, +0.54659770f, +0.19662896f, +0f, +0f,
            -0.49213884f, +0.50450587f, -0.00732472f, +0.57958418f, +0.39591449f, +0.10272980f, +0f, +0f,
            +0.34572956f, +0.62770109f, +0.12165109f, +0.35267248f, +0.34842369f, -0.47527514f, +0f, +0f,
            +0.07628223f, +0.56461194f, -0.39242673f, -0.20639693f, +0.33197602f, +0.60711436f, +0f, +0f,
            +0.46792592f, -0.38434666f, -0.46719345f, -0.40169520f, -0.06134349f, +0.49993117f, +0f, +0f,
            -0.25398819f, -0.82255018f, +0.40372967f, +0.21051604f, +0.02038482f, +0.22621006f, +0f, +0f,
            +0.23269489f, -0.42234243f, -0.18886779f, +0.44290933f, -0.40895242f, +0.60695810f, +0f, +0f,
            -0.13615585f, +0.26142849f, +0.68738606f, +0.42914965f, +0.26332301f, +0.43256061f, +0f, +0f,
            +0.06145597f, -0.25432792f, +0.65050463f, +0.35622065f, -0.52670947f, -0.32259598f, +0f, +0f,
            -0.28027055f, +0.30275296f, +0.39083872f, +0.17564171f, +0.25278203f, +0.76307625f, +0f, +0f,
            -0.62937098f, -0.24958587f, +0.11855057f, +0.52714220f, +0.47759151f, -0.14687496f, +0f, +0f,
            +0.68607574f, +0.28465344f, +0.57132493f, +0.11365238f, -0.32111327f, -0.07635256f, +0f, +0f,
            +0.42669573f, -0.16439965f, -0.54881376f, -0.56551221f, +0.40271560f, -0.08788072f, +0f, +0f,
            -0.30211042f, -0.47278547f, +0.05013786f, +0.46804387f, -0.39450159f, +0.55497099f, +0f, +0f,
            +0.31255895f, +0.03447891f, -0.07923299f, +0.39803160f, +0.82281399f, +0.24369695f, +0f, +0f,
            -0.55243216f, +0.49350231f, +0.52530668f, +0.25362578f, +0.26218499f, -0.20557247f, +0f, +0f,
            +0.06076301f, -0.02393840f, +0.36557410f, +0.55368747f, +0.25557899f, -0.70014279f, +0f, +0f,
            +0.36398574f, +0.04911046f, -0.24289511f, -0.18733973f, +0.02013080f, +0.87784000f, +0f, +0f,
            -0.62385490f, +0.02094759f, -0.44548631f, -0.21069894f, -0.60559127f, +0.02780938f, +0f, +0f,
            +0.51562840f, -0.27416131f, -0.14365580f, -0.46525735f, +0.16338488f, +0.62862302f, +0f, +0f,
            +0.52085189f, +0.51359303f, +0.02184478f, +0.53521775f, -0.23767218f, -0.34858599f, +0f, +0f,
            +0.12263603f, +0.53912951f, +0.57550729f, -0.10335514f, +0.57524709f, +0.14662748f, +0f, +0f,
            +0.40942178f, +0.17197663f, -0.02523801f, -0.20104824f, -0.60303014f, +0.63094779f, +0f, +0f,
            +0.05168570f, +0.23577798f, -0.19154992f, -0.67743578f, -0.51070301f, +0.43047548f, +0f, +0f,
            +0.21373839f, -0.44348268f, +0.34347986f, -0.49945694f, +0.45888698f, -0.42382317f, +0f, +0f,
            -0.60376535f, -0.06530087f, +0.49448067f, +0.12358559f, +0.58623743f, -0.16656623f, +0f, +0f,
            +0.44140930f, -0.41692548f, -0.23774988f, -0.27542786f, +0.39264397f, +0.58717642f, +0f, +0f,
            -0.67860697f, +0.20709913f, -0.12832398f, -0.58381216f, +0.24050209f, +0.28540774f, +0f, +0f,
            -0.02132450f, +0.00986587f, +0.26949011f, +0.42580554f, -0.82903198f, -0.24128534f, +0f, +0f,
            -0.20344882f, +0.51719618f, +0.24379623f, +0.11303683f, -0.46058654f, -0.63777957f, +0f, +0f,
            +0.15686479f, -0.67777169f, -0.04974608f, +0.51313211f, +0.49928667f, -0.03086314f, +0f, +0f,
            +0.53527130f, -0.50102597f, -0.60754472f, -0.25235098f, +0.13490559f, +0.10708155f, +0f, +0f,
            -0.20613512f, +0.39533044f, -0.34422306f, +0.47921455f, -0.19178040f, -0.64521804f, +0f, +0f,
            +0.33047796f, +0.49148538f, -0.30004348f, +0.33473309f, +0.31079743f, +0.59208027f, +0f, +0f,
            -0.52688857f, +0.40250311f, +0.38833191f, +0.50432308f, -0.33327489f, -0.21015252f, +0f, +0f,
            -0.30306420f, -0.34460825f, -0.26894228f, -0.58579646f, -0.51178483f, +0.33464319f, +0f, +0f,
            -0.20258582f, -0.29195675f, +0.11887973f, +0.91211540f, +0.03411881f, -0.16269371f, +0f, +0f,
            +0.61207678f, -0.21883722f, -0.23415725f, +0.00414476f, -0.34019274f, +0.63788273f, +0f, +0f,
            +0.11272999f, -0.54780877f, -0.62497664f, -0.41373740f, +0.33306010f, +0.12039112f, +0f, +0f,
            +0.24918468f, -0.06873428f, -0.42234580f, +0.12235329f, -0.26545138f, +0.81815148f, +0f, +0f,
            +0.32048708f, -0.40233908f, +0.24633289f, -0.37087758f, -0.55466799f, -0.47908728f, +0f, +0f,
            -0.33748729f, -0.45507986f, -0.50597645f, -0.28637016f, -0.54041997f, -0.22120318f, +0f, +0f,
            -0.23520314f, +0.82195093f, -0.22661283f, +0.16382454f, -0.41400232f, -0.13959354f, +0f, +0f,
            -0.30495751f, -0.47964557f, -0.68490238f, -0.43240776f, -0.13521732f, -0.05088770f, +0f, +0f,
            -0.56629250f, +0.19768903f, -0.08007522f, -0.29952637f, +0.09597442f, -0.73136356f, +0f, +0f,
            -0.21316607f, +0.47585902f, -0.49429850f, -0.24146904f, +0.45631329f, +0.46610972f, +0f, +0f,
            +0.12647584f, -0.10203700f, +0.20801341f, +0.66418891f, -0.65219775f, -0.25261414f, +0f, +0f,
            -0.69345279f, +0.30149980f, -0.46870940f, +0.20092958f, -0.21817920f, +0.34721422f, +0f, +0f,
            -0.69001417f, +0.09722776f, -0.37852252f, -0.24995374f, +0.24829304f, +0.49701266f, +0f, +0f,
            -0.82278510f, +0.05074883f, -0.39347330f, +0.00029980f, -0.34677214f, -0.21301870f, +0f, +0f,
            -0.51821811f, -0.22147302f, +0.53524316f, +0.12892242f, -0.55439554f, -0.26821451f, +0f, +0f,
            -0.21006612f, +0.26079212f, -0.02187063f, +0.72402587f, -0.27651658f, +0.53544979f, +0f, +0f,
            -0.09974428f, -0.45342128f, +0.71954978f, -0.31082396f, -0.26933824f, +0.31233586f, +0f, +0f,
            -0.48121951f, -0.43051247f, -0.50384151f, +0.12342710f, +0.03746782f, -0.55909965f, +0f, +0f,
            -0.51180831f, -0.07995548f, -0.53046702f, +0.48748209f, +0.16148937f, -0.43191028f, +0f, +0f,
            -0.38131649f, +0.46242477f, +0.46416075f, -0.20634110f, -0.53778490f, +0.30582118f, +0f, +0f,
            +0.62450430f, +0.14316692f, -0.14361038f, +0.27519251f, -0.60467865f, -0.35708047f, +0f, +0f,
            +0.52425890f, -0.20390682f, -0.33609142f, +0.51803372f, +0.28921536f, +0.46756035f, +0f, +0f,
            -0.44551641f, +0.31831805f, +0.24217750f, +0.49821219f, -0.47209418f, +0.41285649f, +0f, +0f,
            -0.01585731f, -0.45214512f, -0.14591363f, +0.74070676f, +0.00988742f, -0.47463489f, +0f, +0f,
            +0.24260837f, +0.44639366f, +0.31528570f, +0.45334773f, -0.47964168f, -0.45484996f, +0f, +0f,
            +0.47123463f, +0.64525048f, -0.06425763f, -0.18737730f, -0.11735335f, -0.55549853f, +0f, +0f,
            -0.02519722f, -0.25796327f, +0.26277107f, -0.58236203f, -0.41893538f, +0.59086294f, +0f, +0f,
            -0.48940330f, +0.33728563f, -0.05763492f, +0.44862021f, -0.40048256f, +0.53080564f, +0f, +0f,
            +0.73350664f, -0.02148298f, +0.01656814f, +0.00219059f, +0.49384961f, +0.46619710f, +0f, +0f,
            -0.25151229f, -0.62009962f, -0.26948657f, +0.31711936f, -0.35081923f, +0.50592112f, +0f, +0f,
            +0.00942985f, -0.35925999f, +0.47529205f, -0.26709475f, -0.53352146f, +0.53754630f, +0f, +0f,
            -0.59485495f, -0.53195924f, -0.09438376f, -0.41704491f, -0.41397531f, -0.09463944f, +0f, +0f,
            -0.74917126f, -0.24166385f, +0.22864554f, +0.31721357f, +0.06066292f, -0.47303041f, +0f, +0f,
            -0.33003960f, -0.08758658f, -0.09672609f, -0.39607089f, +0.55566932f, +0.63906648f, +0f, +0f,
            -0.58933068f, -0.38176870f, +0.46748019f, -0.06135883f, +0.36268480f, -0.39127879f, +0f, +0f,
            -0.06655669f, -0.73863083f, -0.32153946f, +0.57454599f, -0.09085689f, -0.09082394f, +0f, +0f,
            -0.36335404f, -0.41643677f, -0.57839830f, -0.03095988f, +0.59897925f, -0.01658256f, +0f, +0f,
            +0.23126668f, +0.21077907f, -0.14272193f, -0.29232225f, -0.48451339f, -0.74934159f, +0f, +0f,
            +0.48188197f, -0.04021475f, -0.15667971f, +0.16054853f, -0.60839754f, -0.58796308f, +0f, +0f,
            +0.31319356f, -0.19280657f, +0.76136690f, -0.08450623f, +0.47687867f, -0.22472488f, +0f, +0f,
            +0.67504537f, +0.36920158f, +0.40321048f, +0.03443604f, -0.29332731f, +0.39774172f, +0f, +0f,
            -0.14591598f, -0.59726183f, -0.03638422f, -0.65093487f, +0.39515711f, -0.20198429f, +0f, +0f,
            +0.60092128f, +0.18110182f, +0.25794919f, -0.39594768f, +0.15112959f, +0.59995268f, +0f, +0f,
            -0.42310244f, -0.26937197f, +0.07470001f, +0.53119510f, +0.41614374f, +0.53618944f, +0f, +0f,
            +0.00716054f, -0.69599782f, -0.05313860f, -0.00054500f, +0.69533871f, +0.17092634f, +0f, +0f,
            +0.12447149f, +0.33265313f, +0.35070015f, +0.53879932f, +0.37648083f, +0.56463759f, +0f, +0f,
            +0.29540077f, +0.04954124f, -0.48345087f, +0.72758494f, +0.07006910f, +0.37718664f, +0f, +0f,
            +0.48824142f, +0.45135801f, +0.48450857f, -0.26042407f, -0.42513580f, +0.27310535f, +0f, +0f,
            -0.49806371f, -0.47197596f, +0.02964708f, -0.13788472f, -0.45346141f, -0.55104701f, +0f, +0f,
            -0.53595119f, -0.53585470f, +0.17710362f, -0.45377632f, +0.41838964f, +0.11527149f, +0f, +0f,
            -0.36846431f, -0.46533180f, +0.65800816f, -0.28691297f, +0.31521457f, +0.18178647f, +0f, +0f,
            -0.29243126f, -0.43529565f, -0.58895978f, -0.49649471f, +0.29271342f, +0.21433587f, +0f, +0f,
            +0.05625669f, -0.50387710f, +0.48145041f, +0.44723671f, -0.55771174f, -0.00924491f, +0f, +0f,
            -0.40973125f, -0.73147173f, -0.09407630f, +0.43033451f, +0.01433427f, -0.32066459f, +0f, +0f,
            +0.26752725f, +0.50477344f, +0.06506951f, +0.36001097f, +0.59393393f, -0.43247366f, +0f, +0f,
            +0.48945720f, +0.60433156f, +0.12458128f, -0.48327805f, -0.25681943f, +0.28316179f, +0f, +0f,
            -0.45182760f, +0.21574002f, -0.31462623f, +0.25279349f, +0.44865729f, -0.62058075f, +0f, +0f,
            +0.44017304f, +0.43789555f, +0.58423563f, +0.41842994f, -0.26836655f, +0.16143005f, +0f, +0f,
            -0.67897032f, -0.32730885f, -0.02439973f, +0.40649244f, +0.47711065f, -0.19596475f, +0f, +0f,
            +0.57441588f, +0.09386994f, +0.28400793f, +0.59394229f, +0.45349906f, +0.14881354f, +0f, +0f,
            -0.33937399f, -0.54929055f, +0.26209493f, +0.07338003f, +0.56557076f, +0.43492125f, +0f, +0f,
            +0.05000799f, +0.74652764f, -0.36432144f, -0.20993543f, -0.13520410f, +0.49508839f, +0f, +0f,
            -0.04133215f, -0.20655741f, +0.52511282f, +0.04724863f, -0.62761217f, -0.53268446f, +0f, +0f,
            -0.18894911f, +0.05188976f, -0.45677123f, +0.42884456f, +0.61612085f, -0.43526216f, +0f, +0f,
            -0.65873541f, -0.09477005f, +0.40844030f, +0.35536013f, -0.16940065f, +0.48506226f, +0f, +0f,
            -0.45779281f, -0.46052673f, +0.34138050f, -0.54943270f, +0.37140594f, -0.14826175f, +0f, +0f,
            -0.06937871f, -0.14845488f, -0.73991837f, +0.41519184f, -0.11098464f, -0.49088356f, +0f, +0f,
            +0.46422563f, +0.46130716f, -0.44207791f, +0.12050605f, +0.34969556f, -0.48933493f, +0f, +0f,
            -0.35482925f, +0.28146983f, -0.35356606f, -0.38774754f, +0.35979702f, -0.62454776f, +0f, +0f,
            -0.48343191f, +0.41492185f, -0.50175316f, +0.21953122f, -0.54083165f, +0.04104095f, +0f, +0f,
            -0.51280508f, -0.54131124f, -0.00992871f, +0.23788701f, +0.43503332f, +0.44505087f, +0f, +0f,
            +0.22538373f, -0.30117119f, +0.46587685f, -0.46672901f, -0.59182069f, +0.27086737f, +0f, +0f,
            +0.43015756f, -0.06785111f, -0.26917802f, -0.57731860f, -0.53950120f, -0.33696522f, +0f, +0f,
            +0.20858352f, +0.63695057f, +0.49453142f, -0.04623537f, -0.54436247f, -0.08807572f, +0f, +0f,
            -0.35626464f, +0.06753954f, -0.18142793f, -0.49044207f, +0.55423882f, +0.53654796f, +0f, +0f,
            +0.52238539f, +0.55175875f, +0.29070268f, -0.14119026f, -0.55841587f, -0.08002963f, +0f, +0f,
            -0.02598800f, +0.46612949f, -0.56880970f, -0.44824563f, -0.03000049f, +0.50663523f, +0f, +0f,
            +0.04728458f, -0.26595723f, +0.21032033f, +0.52986834f, -0.52245334f, -0.57365347f, +0f, +0f,
            -0.31924244f, -0.13888420f, +0.30725800f, +0.49792332f, +0.61035592f, -0.40487771f, +0f, +0f,
            +0.03875857f, -0.53813545f, -0.56167256f, +0.46815373f, -0.14142713f, +0.39276248f, +0f, +0f,
            -0.19936871f, +0.12488860f, -0.62990029f, -0.29296146f, +0.49734531f, +0.46335923f, +0f, +0f,
            -0.07882670f, -0.15548800f, +0.57456768f, +0.55588544f, -0.56893054f, -0.08240882f, +0f, +0f,
            +0.11678856f, +0.53358760f, +0.49302489f, -0.53981846f, -0.23791336f, -0.33251226f, +0f, +0f,
            +0.39126928f, -0.39416116f, -0.35778844f, -0.39395609f, +0.50270356f, -0.39448759f, +0f, +0f,
            -0.17961290f, +0.34239532f, -0.21870225f, -0.23322835f, +0.75997835f, +0.41317237f, +0f, +0f,
            +0.29699501f, +0.17195435f, -0.34903627f, -0.31751884f, -0.59661546f, +0.55102732f, +0f, +0f,
            -0.22372913f, -0.51254305f, -0.31277318f, +0.54270199f, -0.34885011f, +0.41616819f, +0f, +0f,
            +0.53534023f, +0.45905986f, -0.20308675f, +0.01952364f, +0.33785805f, +0.58898336f, +0f, +0f,
            -0.04503846f, -0.52553334f, -0.60985458f, +0.46226027f, -0.36069029f, +0.07798443f, +0f, +0f,
            -0.40129033f, +0.39526722f, -0.20379584f, +0.45466492f, +0.46504795f, -0.46712669f, +0f, +0f,
            -0.43845831f, -0.59284534f, +0.05024190f, -0.36494839f, +0.32363879f, +0.46458051f, +0f, +0f,
            -0.46057360f, -0.34584626f, -0.12264748f, +0.48835437f, +0.21102526f, +0.60843919f, +0f, +0f,
            -0.08604754f, -0.16981605f, -0.37222833f, +0.45158609f, -0.55710254f, +0.55759406f, +0f, +0f,
            +0.54697451f, -0.45070837f, +0.03296252f, -0.48584332f, -0.28055687f, +0.42642516f, +0f, +0f,
            +0.34061925f, +0.38443007f, +0.61614808f, -0.55774172f, -0.07566037f, +0.19938218f, +0f, +0f,
            +0.30626924f, -0.05793904f, -0.10461119f, -0.43956387f, -0.57307193f, +0.60849886f, +0f, +0f,
            -0.52519951f, -0.42567534f, -0.19896500f, +0.48819483f, +0.12539008f, +0.49932157f, +0f, +0f,
            -0.10173361f, -0.07873850f, +0.37135540f, +0.65889542f, +0.63411890f, +0.09641423f, +0f, +0f,
            +0.60342393f, +0.05761737f, +0.35558841f, +0.20766418f, +0.03067018f, -0.67974377f, +0f, +0f,
            -0.07197105f, -0.44567383f, +0.65917594f, +0.44113802f, -0.29627117f, +0.28160739f, +0f, +0f,
            +0.38284479f, +0.43552320f, -0.42823684f, -0.54809258f, -0.27202273f, +0.32551612f, +0f, +0f,
            -0.74755699f, -0.20979308f, +0.19268299f, +0.27864013f, -0.39085278f, +0.36001727f, +0f, +0f,
            -0.64575536f, +0.59253747f, +0.04088551f, -0.20167391f, -0.43481684f, -0.02212841f, +0f, +0f,
            +0.45874103f, -0.00665875f, -0.30494054f, +0.52731059f, -0.64443887f, +0.05626427f, +0f, +0f,
            +0.61573773f, -0.00074622f, +0.25455659f, +0.30670278f, -0.18573195f, +0.65383825f, +0f, +0f,
            -0.08991956f, -0.28968403f, -0.60618287f, +0.53370861f, +0.37921556f, -0.33450055f, +0f, +0f,
            -0.47481167f, +0.38992741f, -0.10479631f, +0.45545456f, +0.12142073f, +0.62397625f, +0f, +0f,
            +0.59154225f, -0.10812441f, -0.46858345f, -0.36007270f, -0.10123747f, +0.52812407f, +0f, +0f,
            -0.01292122f, -0.23607532f, -0.57680411f, -0.44955815f, -0.31913443f, -0.55448100f, +0f, +0f,
            +0.54231398f, -0.31845386f, -0.38636423f, +0.22187979f, -0.63464258f, -0.05659949f, +0f, +0f,
            -0.41950690f, -0.45780289f, +0.31139813f, +0.39787962f, -0.20885901f, +0.56172180f, +0f, +0f,
            -0.03140488f, +0.56267475f, -0.55568153f, +0.33075363f, +0.39071115f, +0.33402949f, +0f, +0f,
            -0.51485161f, -0.34037011f, -0.46826090f, -0.60086679f, -0.07506940f, +0.18202033f, +0f, +0f,
            -0.49669644f, +0.13236483f, +0.53440735f, +0.47201200f, -0.05992551f, -0.47306929f, +0f, +0f,
            -0.32796852f, +0.65593302f, +0.20800030f, -0.38965914f, -0.51564565f, -0.03463672f, +0f, +0f,
            -0.30473794f, +0.12584230f, +0.63911213f, +0.11269477f, +0.62944339f, +0.27191006f, +0f, +0f,
            -0.53642197f, +0.50742224f, -0.22907820f, +0.47022559f, -0.19141256f, +0.38019261f, +0f, +0f,
            -0.28865425f, +0.76169672f, -0.36166127f, -0.30555403f, -0.12541657f, -0.31081403f, +0f, +0f,
            +0.00259784f, +0.37371464f, -0.31515119f, +0.62032810f, +0.60524642f, -0.09939888f, +0f, +0f,
            -0.40019833f, +0.15931480f, -0.61653030f, -0.49479441f, -0.02151791f, -0.43481713f, +0f, +0f,
            -0.26445143f, -0.48401155f, +0.27737058f, -0.12537486f, -0.46956235f, +0.61859207f, +0f, +0f,
            -0.49776294f, +0.65095132f, -0.20147785f, +0.26022926f, +0.39526195f, -0.25288299f, +0f, +0f,
            +0.20792543f, +0.67255995f, +0.01329671f, +0.06908240f, -0.37233547f, +0.60070560f, +0f, +0f,
            -0.60329265f, +0.40708027f, -0.17229997f, -0.52997954f, +0.22211745f, -0.33229784f, +0f, +0f,
            +0.61826884f, -0.62582169f, +0.33820439f, +0.23870919f, -0.20670655f, -0.10953969f, +0f, +0f,
            -0.63678168f, -0.51101649f, -0.19131817f, -0.49493417f, -0.22614515f, +0.02582853f, +0f, +0f,
            +0.70684625f, +0.07293280f, -0.30827034f, -0.52659704f, -0.33954839f, +0.08614532f, +0f, +0f,
            -0.52429050f, +0.39091424f, +0.52819210f, -0.16569162f, +0.44719167f, +0.25667977f, +0f, +0f,
            +0.85033978f, -0.37311666f, -0.03158551f, -0.06354692f, -0.35026506f, +0.09992363f, +0f, +0f,
            -0.43149574f, +0.16017753f, -0.36624037f, +0.49372029f, -0.60067103f, +0.22238962f, +0f, +0f,
            -0.43599537f, -0.36065835f, -0.42475053f, -0.52301759f, +0.03945453f, +0.47362064f, +0f, +0f,
            -0.35793170f, -0.43917817f, -0.49072242f, -0.32880277f, -0.38509560f, -0.42636724f, +0f, +0f,
            -0.04367964f, +0.74697226f, -0.40732954f, -0.48088968f, +0.18029290f, -0.10220931f, +0f, +0f,
            -0.05890257f, +0.00825952f, +0.71365961f, -0.53043791f, +0.22906331f, +0.39155822f, +0f, +0f,
            +0.43459649f, +0.18964470f, +0.15217427f, +0.59694624f, +0.05378658f, +0.62671041f, +0f, +0f,
            -0.48833575f, +0.06890988f, +0.60168404f, -0.05545504f, -0.62426261f, -0.04446193f, +0f, +0f,
            -0.71822145f, +0.05449495f, +0.25733756f, -0.42706881f, -0.44024663f, +0.19687748f, +0f, +0f,
            +0.47232210f, +0.63009683f, +0.21662569f, +0.31063720f, +0.07945588f, +0.47974409f, +0f, +0f,
            -0.39506538f, +0.42517729f, +0.29375773f, +0.04450363f, -0.46173213f, +0.60139575f, +0f, +0f,
            -0.40354126f, +0.41304136f, -0.29533980f, -0.45300699f, +0.23702354f, -0.56385297f, +0f, +0f,
            -0.62315380f, -0.42397903f, +0.53044082f, +0.37874432f, +0.05492271f, +0.06395219f, +0f, +0f,
            +0.41959045f, -0.83420441f, -0.25505372f, +0.25012310f, +0.01097423f, +0.01767574f, +0f, +0f,
            -0.25231575f, -0.17034034f, -0.00222544f, -0.49677710f, +0.43184899f, -0.68850194f, +0f, +0f,
            -0.18528128f, -0.48330898f, +0.13528868f, +0.15202104f, +0.57661281f, -0.59848767f, +0f, +0f,
            +0.64287473f, -0.30923674f, +0.22234318f, +0.09924896f, +0.64370450f, +0.13206961f, +0f, +0f,
            -0.49018899f, +0.68654120f, -0.27238863f, -0.08583242f, +0.44161945f, +0.10856057f, +0f, +0f,
            +0.48795432f, +0.42184193f, -0.43797315f, +0.35186997f, -0.46483432f, +0.22857392f, +0f, +0f,
            +0.52970834f, -0.50684486f, -0.39782161f, -0.39327093f, -0.34863027f, +0.16748196f, +0f, +0f,
            -0.46048505f, -0.38871269f, -0.68287320f, -0.18448530f, -0.25358256f, +0.26870280f, +0f, +0f,
            +0.68895573f, -0.31010227f, -0.35882194f, +0.30088738f, -0.03913954f, -0.45646277f, +0f, +0f,
            -0.21954767f, +0.40838837f, +0.23284186f, +0.30349649f, +0.57233263f, +0.55778817f, +0f, +0f,
            +0.57731035f, +0.09121830f, +0.70670016f, +0.01635803f, +0.39392452f, -0.05935263f, +0f, +0f,
            +0.50055570f, -0.02174979f, +0.56767851f, +0.50580176f, +0.34691320f, +0.22478399f, +0f, +0f,
            -0.37901911f, +0.53804099f, -0.46780195f, +0.51497346f, -0.27981005f, +0.06727844f, +0f, +0f,
            +0.67241900f, +0.07409958f, +0.43138117f, +0.05456751f, -0.37927768f, +0.45764946f, +0f, +0f,
            +0.14529189f, -0.23854982f, +0.45401647f, +0.25466539f, +0.46182069f, -0.66160446f, +0f, +0f,
            -0.15570980f, -0.38476787f, +0.37322840f, -0.43977613f, -0.61243005f, -0.34631643f, +0f, +0f,
            -0.19590302f, +0.42065974f, +0.43447548f, -0.10575548f, +0.70439951f, -0.29754920f, +0f, +0f,
            -0.13558865f, +0.14270734f, +0.49647494f, -0.65533234f, -0.11714854f, +0.52113213f, +0f, +0f,
            -0.62283747f, +0.20812698f, -0.16205154f, +0.20384566f, -0.59321895f, +0.38604941f, +0f, +0f,
            +0.44487837f, -0.37224943f, -0.22188447f, +0.48921538f, +0.41432418f, -0.45087099f, +0f, +0f,
            +0.66422841f, +0.21517761f, +0.09401257f, -0.43581590f, +0.22245680f, -0.51404116f, +0f, +0f,
            -0.11369362f, +0.32284689f, -0.38818285f, +0.49680024f, +0.04768486f, -0.69503480f, +0f, +0f,
            -0.51372007f, -0.50673230f, +0.32715252f, -0.26799714f, -0.47616510f, +0.27153195f, +0f, +0f,
            -0.47315177f, -0.45711495f, -0.31178280f, -0.51697763f, -0.14302372f, -0.42689944f, +0f, +0f,
            -0.05044203f, +0.23609184f, +0.38634880f, +0.56012774f, +0.38963669f, -0.57174382f, +0f, +0f,
            -0.15472134f, -0.15333579f, -0.14189768f, +0.03227926f, -0.66054298f, -0.70360180f, +0f, +0f,
            -0.10345191f, -0.30503725f, +0.31038263f, +0.36878846f, -0.76824774f, +0.27148306f, +0f, +0f,
            -0.06021286f, -0.41727554f, +0.39199300f, -0.44040104f, +0.24955102f, -0.64215903f, +0f, +0f,
            +0.25443195f, -0.01378958f, +0.44365000f, +0.53296203f, -0.55057750f, -0.38867053f, +0f, +0f,
            -0.36068564f, -0.65616661f, -0.48495997f, +0.24088316f, -0.18080297f, -0.33682435f, +0f, +0f,
            -0.53824550f, -0.09672890f, -0.52086198f, +0.33195321f, -0.03226394f, +0.56427315f, +0f, +0f,
            +0.40151657f, -0.44825725f, -0.54910020f, -0.09593627f, +0.57195639f, +0.00097783f, +0f, +0f,
            +0.21961099f, +0.62823723f, -0.01004593f, -0.66105648f, -0.17161595f, -0.30089924f, +0f, +0f,
            +0.27961471f, +0.05452339f, +0.61485903f, +0.11958885f, -0.61032561f, -0.39241856f, +0f, +0f,
            -0.30223065f, -0.23605925f, -0.09697276f, -0.46458104f, -0.37853464f, +0.69599203f, +0f, +0f,
            +0.00236355f, +0.62702100f, +0.49658954f, -0.20369645f, -0.56457560f, +0.00021299f, +0f, +0f,
            -0.64198493f, +0.59676262f, +0.46274573f, +0.08842191f, +0.09802999f, -0.01295307f, +0f, +0f,
            -0.05396543f, +0.13439533f, -0.33103493f, +0.55991756f, -0.58127599f, -0.46696041f, +0f, +0f,
            -0.43965993f, +0.07544961f, +0.15096395f, -0.38868406f, -0.00334360f, -0.79191533f, +0f, +0f,
            -0.21743914f, -0.32019630f, -0.56067107f, +0.02728491f, -0.49444926f, -0.53908992f, +0f, +0f,
            -0.36492599f, +0.52529904f, +0.18002253f, +0.14829474f, +0.17212619f, -0.71194315f, +0f, +0f,
            +0.00518762f, +0.50490293f, +0.24361032f, +0.13688117f, -0.61381291f, -0.53869971f, +0f, +0f,
            +0.66421180f, +0.21833854f, -0.08790993f, +0.15624552f, -0.68780724f, +0.07701505f, +0f, +0f,
            +0.52710630f, -0.42143671f, -0.06996455f, -0.24196341f, -0.68814841f, +0.08695091f, +0f, +0f,
            +0.62392249f, -0.23663281f, -0.59058622f, +0.22685863f, -0.36683948f, -0.14105848f, +0f, +0f,
            +0.18069852f, -0.08382855f, +0.66240167f, +0.16722813f, -0.25503640f, -0.65462662f, +0f, +0f,
            -0.37112528f, +0.43100319f, -0.11342774f, +0.14418808f, +0.57533269f, +0.55842502f, +0f, +0f,
            +0.55378724f, +0.21098160f, -0.32249766f, +0.31268307f, -0.37624695f, -0.55269271f, +0f, +0f,
            +0.26014658f, +0.56373458f, -0.21638357f, +0.41216916f, -0.25078072f, -0.57873208f, +0f, +0f,
            +0.11217864f, +0.54196554f, -0.31989128f, +0.54691221f, +0.24062434f, +0.48409277f, +0f, +0f,
            +0.08756442f, -0.12083081f, +0.69931172f, +0.35220575f, +0.28770484f, -0.53091668f, +0f, +0f,
            +0.33957021f, +0.04252094f, -0.30935928f, +0.61022210f, +0.54650816f, +0.34079124f, +0f, +0f,
            +0.32746112f, +0.32095220f, -0.61142534f, +0.32197324f, -0.38236071f, +0.40749411f, +0f, +0f,
            +0.58741915f, -0.30916030f, -0.57642977f, -0.03884619f, +0.04792671f, -0.47252657f, +0f, +0f,
            +0.02622438f, +0.03176890f, -0.12510902f, +0.36102734f, -0.72217212f, +0.57513252f, +0f, +0f,
            -0.27510374f, -0.51534021f, +0.02577402f, +0.59201067f, +0.40728366f, -0.37645913f, +0f, +0f,
            -0.29983338f, -0.61017291f, -0.18551919f, +0.50515945f, +0.18206593f, -0.46372136f, +0f, +0f,
            -0.64290893f, -0.34887011f, -0.55318606f, -0.21230198f, -0.19828983f, +0.27304198f, +0f, +0f,
            -0.32778879f, -0.09431729f, +0.57811170f, +0.54346692f, +0.17699503f, -0.47197676f, +0f, +0f,
            -0.07573870f, +0.53381750f, -0.13406342f, +0.71765386f, +0.34271060f, +0.24259408f, +0f, +0f,
            -0.30574273f, +0.17419449f, -0.78861555f, +0.43305678f, +0.06485332f, +0.25003806f, +0f, +0f,
            +0.43970359f, -0.51651518f, -0.39723461f, -0.34513492f, +0.32129829f, -0.39965829f, +0f, +0f,
            -0.25184899f, -0.35937572f, +0.15273239f, -0.51640931f, +0.42187157f, -0.58261460f, +0f, +0f,
            -0.57396000f, +0.19127861f, +0.45995634f, -0.43664716f, +0.46016301f, +0.14146310f, +0f, +0f,
            +0.11500068f, +0.05112652f, -0.25672855f, -0.54715738f, +0.67669928f, +0.40118355f, +0f, +0f,
            -0.45252668f, -0.40809988f, -0.06493154f, +0.19116562f, +0.76523014f, +0.04833740f, +0f, +0f,
            -0.08007565f, +0.75305314f, +0.34797424f, +0.29104493f, +0.00401859f, -0.46977598f, +0f, +0f,
            -0.38902576f, +0.49100041f, -0.17812126f, -0.43787557f, -0.46923187f, +0.40489108f, +0f, +0f,
            +0.37433236f, -0.29441766f, -0.06628513f, +0.33217472f, +0.73917165f, +0.33479099f, +0f, +0f,
            -0.02973230f, -0.51371026f, +0.34133522f, -0.41361792f, -0.51561746f, -0.42634124f, +0f, +0f,
            +0.51057171f, -0.23740201f, +0.26673587f, +0.55217673f, +0.16849318f, +0.52774964f, +0f, +0f,
    };

    private static final Float2[] CELL_2D =
        {
            new Float2(-0.4313539279f, 0.1281943404f), new Float2(-0.1733316799f, 0.415278375f), new Float2(-0.2821957395f, -0.3505218461f), new Float2(-0.2806473808f, 0.3517627718f), new Float2(0.3125508975f, -0.3237467165f), new Float2(0.3383018443f, -0.2967353402f), new Float2(-0.4393982022f, -0.09710417025f), new Float2(-0.4460443703f, -0.05953502905f),
            new Float2(-0.302223039f, 0.3334085102f), new Float2(-0.212681052f, -0.3965687458f), new Float2(-0.2991156529f, 0.3361990872f), new Float2(0.2293323691f, 0.3871778202f), new Float2(0.4475439151f, -0.04695150755f), new Float2(0.1777518f, 0.41340573f), new Float2(0.1688522499f, -0.4171197882f), new Float2(-0.0976597166f, 0.4392750616f),
            new Float2(0.08450188373f, 0.4419948321f), new Float2(-0.4098760448f, -0.1857461384f), new Float2(0.3476585782f, -0.2857157906f), new Float2(-0.3350670039f, -0.30038326f), new Float2(0.2298190031f, -0.3868891648f), new Float2(-0.01069924099f, 0.449872789f), new Float2(-0.4460141246f, -0.05976119672f), new Float2(0.3650293864f, 0.2631606867f),
            new Float2(-0.349479423f, 0.2834856838f), new Float2(-0.4122720642f, 0.1803655873f), new Float2(-0.267327811f, 0.3619887311f), new Float2(0.322124041f, -0.3142230135f), new Float2(0.2880445931f, -0.3457315612f), new Float2(0.3892170926f, -0.2258540565f), new Float2(0.4492085018f, -0.02667811596f), new Float2(-0.4497724772f, 0.01430799601f),
            new Float2(0.1278175387f, -0.4314657307f), new Float2(-0.03572100503f, 0.4485799926f), new Float2(-0.4297407068f, -0.1335025276f), new Float2(-0.3217817723f, 0.3145735065f), new Float2(-0.3057158873f, 0.3302087162f), new Float2(-0.414503978f, 0.1751754899f), new Float2(-0.3738139881f, 0.2505256519f), new Float2(0.2236891408f, -0.3904653228f),
            new Float2(0.002967775577f, -0.4499902136f), new Float2(0.1747128327f, -0.4146991995f), new Float2(-0.4423772489f, -0.08247647938f), new Float2(-0.2763960987f, -0.355112935f), new Float2(-0.4019385906f, -0.2023496216f), new Float2(0.3871414161f, -0.2293938184f), new Float2(-0.430008727f, 0.1326367019f), new Float2(-0.03037574274f, -0.4489736231f),
            new Float2(-0.3486181573f, 0.2845441624f), new Float2(0.04553517144f, -0.4476902368f), new Float2(-0.0375802926f, 0.4484280562f), new Float2(0.3266408905f, 0.3095250049f), new Float2(0.06540017593f, -0.4452222108f), new Float2(0.03409025829f, 0.448706869f), new Float2(-0.4449193635f, 0.06742966669f), new Float2(-0.4255936157f, -0.1461850686f),
            new Float2(0.449917292f, 0.008627302568f), new Float2(0.05242606404f, 0.4469356864f), new Float2(-0.4495305179f, -0.02055026661f), new Float2(-0.1204775703f, 0.4335725488f), new Float2(-0.341986385f, -0.2924813028f), new Float2(0.3865320182f, 0.2304191809f), new Float2(0.04506097811f, -0.447738214f), new Float2(-0.06283465979f, 0.4455915232f),
            new Float2(0.3932600341f, -0.2187385324f), new Float2(0.4472261803f, -0.04988730975f), new Float2(0.3753571011f, -0.2482076684f), new Float2(-0.273662295f, 0.357223947f), new Float2(0.1700461538f, 0.4166344988f), new Float2(0.4102692229f, 0.1848760794f), new Float2(0.323227187f, -0.3130881435f), new Float2(-0.2882310238f, -0.3455761521f),
            new Float2(0.2050972664f, 0.4005435199f), new Float2(0.4414085979f, -0.08751256895f), new Float2(-0.1684700334f, 0.4172743077f), new Float2(-0.003978032396f, 0.4499824166f), new Float2(-0.2055133639f, 0.4003301853f), new Float2(-0.006095674897f, -0.4499587123f), new Float2(-0.1196228124f, -0.4338091548f), new Float2(0.3901528491f, -0.2242337048f),
            new Float2(0.01723531752f, 0.4496698165f), new Float2(-0.3015070339f, 0.3340561458f), new Float2(-0.01514262423f, -0.4497451511f), new Float2(-0.4142574071f, -0.1757577897f), new Float2(-0.1916377265f, -0.4071547394f), new Float2(0.3749248747f, 0.2488600778f), new Float2(-0.2237774255f, 0.3904147331f), new Float2(-0.4166343106f, -0.1700466149f),
            new Float2(0.3619171625f, 0.267424695f), new Float2(0.1891126846f, -0.4083336779f), new Float2(-0.3127425077f, 0.323561623f), new Float2(-0.3281807787f, 0.307891826f), new Float2(-0.2294806661f, 0.3870899429f), new Float2(-0.3445266136f, 0.2894847362f), new Float2(-0.4167095422f, -0.1698621719f), new Float2(-0.257890321f, -0.3687717212f),
            new Float2(-0.3612037825f, 0.2683874578f), new Float2(0.2267996491f, 0.3886668486f), new Float2(0.207157062f, 0.3994821043f), new Float2(0.08355176718f, -0.4421754202f), new Float2(-0.4312233307f, 0.1286329626f), new Float2(0.3257055497f, 0.3105090899f), new Float2(0.177701095f, -0.4134275279f), new Float2(-0.445182522f, 0.06566979625f),
            new Float2(0.3955143435f, 0.2146355146f), new Float2(-0.4264613988f, 0.1436338239f), new Float2(-0.3793799665f, -0.2420141339f), new Float2(0.04617599081f, -0.4476245948f), new Float2(-0.371405428f, -0.2540826796f), new Float2(0.2563570295f, -0.3698392535f), new Float2(0.03476646309f, 0.4486549822f), new Float2(-0.3065454405f, 0.3294387544f),
            new Float2(-0.2256979823f, 0.3893076172f), new Float2(0.4116448463f, -0.1817925206f), new Float2(-0.2907745828f, -0.3434387019f), new Float2(0.2842278468f, -0.348876097f), new Float2(0.3114589359f, -0.3247973695f), new Float2(0.4464155859f, -0.0566844308f), new Float2(-0.3037334033f, -0.3320331606f), new Float2(0.4079607166f, 0.1899159123f),
            new Float2(-0.3486948919f, -0.2844501228f), new Float2(0.3264821436f, 0.3096924441f), new Float2(0.3211142406f, 0.3152548881f), new Float2(0.01183382662f, 0.4498443737f), new Float2(0.4333844092f, 0.1211526057f), new Float2(0.3118668416f, 0.324405723f), new Float2(-0.272753471f, 0.3579183483f), new Float2(-0.422228622f, -0.1556373694f),
            new Float2(-0.1009700099f, -0.4385260051f), new Float2(-0.2741171231f, -0.3568750521f), new Float2(-0.1465125133f, 0.4254810025f), new Float2(0.2302279044f, -0.3866459777f), new Float2(-0.3699435608f, 0.2562064828f), new Float2(0.105700352f, -0.4374099171f), new Float2(-0.2646713633f, 0.3639355292f), new Float2(0.3521828122f, 0.2801200935f),
            new Float2(-0.1864187807f, -0.4095705534f), new Float2(0.1994492955f, -0.4033856449f), new Float2(0.3937065066f, 0.2179339044f), new Float2(-0.3226158377f, 0.3137180602f), new Float2(0.3796235338f, 0.2416318948f), new Float2(0.1482921929f, 0.4248640083f), new Float2(-0.407400394f, 0.1911149365f), new Float2(0.4212853031f, 0.1581729856f),
            new Float2(-0.2621297173f, 0.3657704353f), new Float2(-0.2536986953f, -0.3716678248f), new Float2(-0.2100236383f, 0.3979825013f), new Float2(0.3624152444f, 0.2667493029f), new Float2(-0.3645038479f, -0.2638881295f), new Float2(0.2318486784f, 0.3856762766f), new Float2(-0.3260457004f, 0.3101519002f), new Float2(-0.2130045332f, -0.3963950918f),
            new Float2(0.3814998766f, -0.2386584257f), new Float2(-0.342977305f, 0.2913186713f), new Float2(-0.4355865605f, 0.1129794154f), new Float2(-0.2104679605f, 0.3977477059f), new Float2(0.3348364681f, -0.3006402163f), new Float2(0.3430468811f, 0.2912367377f), new Float2(-0.2291836801f, -0.3872658529f), new Float2(0.2547707298f, -0.3709337882f),
            new Float2(0.4236174945f, -0.151816397f), new Float2(-0.15387742f, 0.4228731957f), new Float2(-0.4407449312f, 0.09079595574f), new Float2(-0.06805276192f, -0.444824484f), new Float2(0.4453517192f, -0.06451237284f), new Float2(0.2562464609f, -0.3699158705f), new Float2(0.3278198355f, -0.3082761026f), new Float2(-0.4122774207f, -0.1803533432f),
            new Float2(0.3354090914f, -0.3000012356f), new Float2(0.446632869f, -0.05494615882f), new Float2(-0.1608953296f, 0.4202531296f), new Float2(-0.09463954939f, 0.4399356268f), new Float2(-0.02637688324f, -0.4492262904f), new Float2(0.447102804f, -0.05098119915f), new Float2(-0.4365670908f, 0.1091291678f), new Float2(-0.3959858651f, 0.2137643437f),
            new Float2(-0.4240048207f, -0.1507312575f), new Float2(-0.3882794568f, 0.2274622243f), new Float2(-0.4283652566f, -0.1378521198f), new Float2(0.3303888091f, 0.305521251f), new Float2(0.3321434919f, -0.3036127481f), new Float2(-0.413021046f, -0.1786438231f), new Float2(0.08403060337f, -0.4420846725f), new Float2(-0.3822882919f, 0.2373934748f),
            new Float2(-0.3712395594f, -0.2543249683f), new Float2(0.4472363971f, -0.04979563372f), new Float2(-0.4466591209f, 0.05473234629f), new Float2(0.0486272539f, -0.4473649407f), new Float2(-0.4203101295f, -0.1607463688f), new Float2(0.2205360833f, 0.39225481f), new Float2(-0.3624900666f, 0.2666476169f), new Float2(-0.4036086833f, -0.1989975647f),
            new Float2(0.2152727807f, 0.3951678503f), new Float2(-0.4359392962f, -0.1116106179f), new Float2(0.4178354266f, 0.1670735057f), new Float2(0.2007630161f, 0.4027334247f), new Float2(-0.07278067175f, -0.4440754146f), new Float2(0.3644748615f, -0.2639281632f), new Float2(-0.4317451775f, 0.126870413f), new Float2(-0.297436456f, 0.3376855855f),
            new Float2(-0.2998672222f, 0.3355289094f), new Float2(-0.2673674124f, 0.3619594822f), new Float2(0.2808423357f, 0.3516071423f), new Float2(0.3498946567f, 0.2829730186f), new Float2(-0.2229685561f, 0.390877248f), new Float2(0.3305823267f, 0.3053118493f), new Float2(-0.2436681211f, -0.3783197679f), new Float2(-0.03402776529f, 0.4487116125f),
            new Float2(-0.319358823f, 0.3170330301f), new Float2(0.4454633477f, -0.06373700535f), new Float2(0.4483504221f, 0.03849544189f), new Float2(-0.4427358436f, -0.08052932871f), new Float2(0.05452298565f, 0.4466847255f), new Float2(-0.2812560807f, 0.3512762688f), new Float2(0.1266696921f, 0.4318041097f), new Float2(-0.3735981243f, 0.2508474468f),
            new Float2(0.2959708351f, -0.3389708908f), new Float2(-0.3714377181f, 0.254035473f), new Float2(-0.404467102f, -0.1972469604f), new Float2(0.1636165687f, -0.419201167f), new Float2(0.3289185495f, -0.3071035458f), new Float2(-0.2494824991f, -0.3745109914f), new Float2(0.03283133272f, 0.4488007393f), new Float2(-0.166306057f, -0.4181414777f),
            new Float2(-0.106833179f, 0.4371346153f), new Float2(0.06440260376f, -0.4453676062f), new Float2(-0.4483230967f, 0.03881238203f), new Float2(-0.421377757f, -0.1579265206f), new Float2(0.05097920662f, -0.4471030312f), new Float2(0.2050584153f, -0.4005634111f), new Float2(0.4178098529f, -0.167137449f), new Float2(-0.3565189504f, -0.2745801121f),
            new Float2(0.4478398129f, 0.04403977727f), new Float2(-0.3399999602f, -0.2947881053f), new Float2(0.3767121994f, 0.2461461331f), new Float2(-0.3138934434f, 0.3224451987f), new Float2(-0.1462001792f, -0.4255884251f), new Float2(0.3970290489f, -0.2118205239f), new Float2(0.4459149305f, -0.06049689889f), new Float2(-0.4104889426f, -0.1843877112f),
            new Float2(0.1475103971f, -0.4251360756f), new Float2(0.09258030352f, 0.4403735771f), new Float2(-0.1589664637f, -0.4209865359f), new Float2(0.2482445008f, 0.3753327428f), new Float2(0.4383624232f, -0.1016778537f), new Float2(0.06242802956f, 0.4456486745f), new Float2(0.2846591015f, -0.3485243118f), new Float2(-0.344202744f, -0.2898697484f),
            new Float2(0.1198188883f, -0.4337550392f), new Float2(-0.243590703f, 0.3783696201f), new Float2(0.2958191174f, -0.3391033025f), new Float2(-0.1164007991f, 0.4346847754f), new Float2(0.1274037151f, -0.4315881062f), new Float2(0.368047306f, 0.2589231171f), new Float2(0.2451436949f, 0.3773652989f), new Float2(-0.4314509715f, 0.12786735f),
        };

    private static final Float3[] CELL_3D =
        {
            new Float3(0.1453787434f, -0.4149781685f, -0.0956981749f), new Float3(-0.01242829687f, -0.1457918398f, -0.4255470325f), new Float3(0.2877979582f, -0.02606483451f, -0.3449535616f), new Float3(-0.07732986802f, 0.2377094325f, 0.3741848704f), new Float3(0.1107205875f, -0.3552302079f, -0.2530858567f), new Float3(0.2755209141f, 0.2640521179f, -0.238463215f), new Float3(0.294168941f, 0.1526064594f, 0.3044271714f), new Float3(0.4000921098f, -0.2034056362f, 0.03244149937f),
            new Float3(-0.1697304074f, 0.3970864695f, -0.1265461359f), new Float3(-0.1483224484f, -0.3859694688f, 0.1775613147f), new Float3(0.2623596946f, -0.2354852944f, 0.2796677792f), new Float3(-0.2709003183f, 0.3505271138f, -0.07901746678f), new Float3(-0.03516550699f, 0.3885234328f, 0.2243054374f), new Float3(-0.1267712655f, 0.1920044036f, 0.3867342179f), new Float3(0.02952021915f, 0.4409685861f, 0.08470692262f), new Float3(-0.2806854217f, -0.266996757f, 0.2289725438f),
            new Float3(-0.171159547f, 0.2141185563f, 0.3568720405f), new Float3(0.2113227183f, 0.3902405947f, -0.07453178509f), new Float3(-0.1024352839f, 0.2128044156f, -0.3830421561f), new Float3(-0.3304249877f, -0.1566986703f, 0.2622305365f), new Float3(0.2091111325f, 0.3133278055f, -0.2461670583f), new Float3(0.344678154f, -0.1944240454f, -0.2142341261f), new Float3(0.1984478035f, -0.3214342325f, -0.2445373252f), new Float3(-0.2929008603f, 0.2262915116f, 0.2559320961f),
            new Float3(-0.1617332831f, 0.006314769776f, -0.4198838754f), new Float3(-0.3582060271f, -0.148303178f, -0.2284613961f), new Float3(-0.1852067326f, -0.3454119342f, -0.2211087107f), new Float3(0.3046301062f, 0.1026310383f, 0.314908508f), new Float3(-0.03816768434f, -0.2551766358f, -0.3686842991f), new Float3(-0.4084952196f, 0.1805950793f, 0.05492788837f), new Float3(-0.02687443361f, -0.2749741471f, 0.3551999201f), new Float3(-0.03801098351f, 0.3277859044f, 0.3059600725f),
            new Float3(0.2371120802f, 0.2900386767f, -0.2493099024f), new Float3(0.4447660503f, 0.03946930643f, 0.05590469027f), new Float3(0.01985147278f, -0.01503183293f, -0.4493105419f), new Float3(0.4274339143f, 0.03345994256f, -0.1366772882f), new Float3(-0.2072988631f, 0.2871414597f, -0.2776273824f), new Float3(-0.3791240978f, 0.1281177671f, 0.2057929936f), new Float3(-0.2098721267f, -0.1007087278f, -0.3851122467f), new Float3(0.01582798878f, 0.4263894424f, 0.1429738373f),
            new Float3(-0.1888129464f, -0.3160996813f, -0.2587096108f), new Float3(0.1612988974f, -0.1974805082f, -0.3707885038f), new Float3(-0.08974491322f, 0.229148752f, -0.3767448739f), new Float3(0.07041229526f, 0.4150230285f, -0.1590534329f), new Float3(-0.1082925611f, -0.1586061639f, 0.4069604477f), new Float3(0.2474100658f, -0.3309414609f, 0.1782302128f), new Float3(-0.1068836661f, -0.2701644537f, -0.3436379634f), new Float3(0.2396452163f, 0.06803600538f, -0.3747549496f),
            new Float3(-0.3063886072f, 0.2597428179f, 0.2028785103f), new Float3(0.1593342891f, -0.3114350249f, -0.2830561951f), new Float3(0.2709690528f, 0.1412648683f, -0.3303331794f), new Float3(-0.1519780427f, 0.3623355133f, 0.2193527988f), new Float3(0.1699773681f, 0.3456012883f, 0.2327390037f), new Float3(-0.1986155616f, 0.3836276443f, -0.1260225743f), new Float3(-0.1887482106f, -0.2050154888f, -0.353330953f), new Float3(0.2659103394f, 0.3015631259f, -0.2021172246f),
            new Float3(-0.08838976154f, -0.4288819642f, -0.1036702021f), new Float3(-0.04201869311f, 0.3099592485f, 0.3235115047f), new Float3(-0.3230334656f, 0.201549922f, -0.2398478873f), new Float3(0.2612720941f, 0.2759854499f, -0.2409749453f), new Float3(0.385713046f, 0.2193460345f, 0.07491837764f), new Float3(0.07654967953f, 0.3721732183f, 0.241095919f), new Float3(0.4317038818f, -0.02577753072f, 0.1243675091f), new Float3(-0.2890436293f, -0.3418179959f, -0.04598084447f),
            new Float3(-0.2201947582f, 0.383023377f, -0.08548310451f), new Float3(0.4161322773f, -0.1669634289f, -0.03817251927f), new Float3(0.2204718095f, 0.02654238946f, -0.391391981f), new Float3(-0.1040307469f, 0.3890079625f, -0.2008741118f), new Float3(-0.1432122615f, 0.371614387f, -0.2095065525f), new Float3(0.3978380468f, -0.06206669342f, 0.2009293758f), new Float3(-0.2599274663f, 0.2616724959f, -0.2578084893f), new Float3(0.4032618332f, -0.1124593585f, 0.1650235939f),
            new Float3(-0.08953470255f, -0.3048244735f, 0.3186935478f), new Float3(0.118937202f, -0.2875221847f, 0.325092195f), new Float3(0.02167047076f, -0.03284630549f, -0.4482761547f), new Float3(-0.3411343612f, 0.2500031105f, 0.1537068389f), new Float3(0.3162964612f, 0.3082064153f, -0.08640228117f), new Float3(0.2355138889f, -0.3439334267f, -0.1695376245f), new Float3(-0.02874541518f, -0.3955933019f, 0.2125550295f), new Float3(-0.2461455173f, 0.02020282325f, -0.3761704803f),
            new Float3(0.04208029445f, -0.4470439576f, 0.02968078139f), new Float3(0.2727458746f, 0.2288471896f, -0.2752065618f), new Float3(-0.1347522818f, -0.02720848277f, -0.4284874806f), new Float3(0.3829624424f, 0.1231931484f, -0.2016512234f), new Float3(-0.3547613644f, 0.1271702173f, 0.2459107769f), new Float3(0.2305790207f, 0.3063895591f, 0.2354968222f), new Float3(-0.08323845599f, -0.1922245118f, 0.3982726409f), new Float3(0.2993663085f, -0.2619918095f, -0.2103333191f),
            new Float3(-0.2154865723f, 0.2706747713f, 0.287751117f), new Float3(0.01683355354f, -0.2680655787f, -0.3610505186f), new Float3(0.05240429123f, 0.4335128183f, -0.1087217856f), new Float3(0.00940104872f, -0.4472890582f, 0.04841609928f), new Float3(0.3465688735f, 0.01141914583f, -0.2868093776f), new Float3(-0.3706867948f, -0.2551104378f, 0.003156692623f), new Float3(0.2741169781f, 0.2139972417f, -0.2855959784f), new Float3(0.06413433865f, 0.1708718512f, 0.4113266307f),
            new Float3(-0.388187972f, -0.03973280434f, -0.2241236325f), new Float3(0.06419469312f, -0.2803682491f, 0.3460819069f), new Float3(-0.1986120739f, -0.3391173584f, 0.2192091725f), new Float3(-0.203203009f, -0.3871641506f, 0.1063600375f), new Float3(-0.1389736354f, -0.2775901578f, -0.3257760473f), new Float3(-0.06555641638f, 0.342253257f, -0.2847192729f), new Float3(-0.2529246486f, -0.2904227915f, 0.2327739768f), new Float3(0.1444476522f, 0.1069184044f, 0.4125570634f),
            new Float3(-0.3643780054f, -0.2447099973f, -0.09922543227f), new Float3(0.4286142488f, -0.1358496089f, -0.01829506817f), new Float3(0.165872923f, -0.3136808464f, -0.2767498872f), new Float3(0.2219610524f, -0.3658139958f, 0.1393320198f), new Float3(0.04322940318f, -0.3832730794f, 0.2318037215f), new Float3(-0.08481269795f, -0.4404869674f, -0.03574965489f), new Float3(0.1822082075f, -0.3953259299f, 0.1140946023f), new Float3(-0.3269323334f, 0.3036542563f, 0.05838957105f),
            new Float3(-0.4080485344f, 0.04227858267f, -0.184956522f), new Float3(0.2676025294f, -0.01299671652f, 0.36155217f), new Float3(0.3024892441f, -0.1009990293f, -0.3174892964f), new Float3(0.1448494052f, 0.425921681f, -0.0104580805f), new Float3(0.4198402157f, 0.08062320474f, 0.1404780841f), new Float3(-0.3008872161f, -0.333040905f, -0.03241355801f), new Float3(0.3639310428f, -0.1291284382f, -0.2310412139f), new Float3(0.3295806598f, 0.0184175994f, -0.3058388149f),
            new Float3(0.2776259487f, -0.2974929052f, -0.1921504723f), new Float3(0.4149000507f, -0.144793182f, -0.09691688386f), new Float3(0.145016715f, -0.0398992945f, 0.4241205002f), new Float3(0.09299023471f, -0.299732164f, -0.3225111565f), new Float3(0.1028907093f, -0.361266869f, 0.247789732f), new Float3(0.2683057049f, -0.07076041213f, -0.3542668666f), new Float3(-0.4227307273f, -0.07933161816f, -0.1323073187f), new Float3(-0.1781224702f, 0.1806857196f, -0.3716517945f),
            new Float3(0.4390788626f, -0.02841848598f, -0.09435116353f), new Float3(0.2972583585f, 0.2382799621f, -0.2394997452f), new Float3(-0.1707002821f, 0.2215845691f, 0.3525077196f), new Float3(0.3806686614f, 0.1471852559f, -0.1895464869f), new Float3(-0.1751445661f, -0.274887877f, 0.3102596268f), new Float3(-0.2227237566f, -0.2316778837f, 0.3149912482f), new Float3(0.1369633021f, 0.1341343041f, -0.4071228836f), new Float3(-0.3529503428f, -0.2472893463f, -0.129514612f),
            new Float3(-0.2590744185f, -0.2985577559f, -0.2150435121f), new Float3(-0.3784019401f, 0.2199816631f, -0.1044989934f), new Float3(-0.05635805671f, 0.1485737441f, 0.4210102279f), new Float3(0.3251428613f, 0.09666046873f, -0.2957006485f), new Float3(-0.4190995804f, 0.1406751354f, -0.08405978803f), new Float3(-0.3253150961f, -0.3080335042f, -0.04225456877f), new Float3(0.2857945863f, -0.05796152095f, 0.3427271751f), new Float3(-0.2733604046f, 0.1973770973f, -0.2980207554f),
            new Float3(0.219003657f, 0.2410037886f, -0.3105713639f), new Float3(0.3182767252f, -0.271342949f, 0.1660509868f), new Float3(-0.03222023115f, -0.3331161506f, -0.300824678f), new Float3(-0.3087780231f, 0.1992794134f, -0.2596995338f), new Float3(-0.06487611647f, -0.4311322747f, 0.1114273361f), new Float3(0.3921171432f, -0.06294284106f, -0.2116183942f), new Float3(-0.1606404506f, -0.358928121f, -0.2187812825f), new Float3(-0.03767771199f, -0.2290351443f, 0.3855169162f),
            new Float3(0.1394866832f, -0.3602213994f, 0.2308332918f), new Float3(-0.4345093872f, 0.005751117145f, 0.1169124335f), new Float3(-0.1044637494f, 0.4168128432f, -0.1336202785f), new Float3(0.2658727501f, 0.2551943237f, 0.2582393035f), new Float3(0.2051461999f, 0.1975390727f, 0.3484154868f), new Float3(-0.266085566f, 0.23483312f, 0.2766800993f), new Float3(0.07849405464f, -0.3300346342f, -0.2956616708f), new Float3(-0.2160686338f, 0.05376451292f, -0.3910546287f),
            new Float3(-0.185779186f, 0.2148499206f, 0.3490352499f), new Float3(0.02492421743f, -0.3229954284f, -0.3123343347f), new Float3(-0.120167831f, 0.4017266681f, 0.1633259825f), new Float3(-0.02160084693f, -0.06885389554f, 0.4441762538f), new Float3(0.2597670064f, 0.3096300784f, 0.1978643903f), new Float3(-0.1611553854f, -0.09823036005f, 0.4085091653f), new Float3(-0.3278896792f, 0.1461670309f, 0.2713366126f), new Float3(0.2822734956f, 0.03754421121f, -0.3484423997f),
            new Float3(0.03169341113f, 0.347405252f, -0.2842624114f), new Float3(0.2202613604f, -0.3460788041f, -0.1849713341f), new Float3(0.2933396046f, 0.3031973659f, 0.1565989581f), new Float3(-0.3194922995f, 0.2453752201f, -0.200538455f), new Float3(-0.3441586045f, -0.1698856132f, -0.2349334659f), new Float3(0.2703645948f, -0.3574277231f, 0.04060059933f), new Float3(0.2298568861f, 0.3744156221f, 0.0973588921f), new Float3(0.09326603877f, -0.3170108894f, 0.3054595587f),
            new Float3(-0.1116165319f, -0.2985018719f, 0.3177080142f), new Float3(0.2172907365f, -0.3460005203f, -0.1885958001f), new Float3(0.1991339479f, 0.3820341668f, -0.1299829458f), new Float3(-0.0541918155f, -0.2103145071f, 0.39412061f), new Float3(0.08871336998f, 0.2012117383f, 0.3926114802f), new Float3(0.2787673278f, 0.3505404674f, 0.04370535101f), new Float3(-0.322166438f, 0.3067213525f, 0.06804996813f), new Float3(-0.4277366384f, 0.132066775f, 0.04582286686f),
            new Float3(0.240131882f, -0.1612516055f, 0.344723946f), new Float3(0.1448607981f, -0.2387819045f, 0.3528435224f), new Float3(-0.3837065682f, -0.2206398454f, 0.08116235683f), new Float3(-0.4382627882f, -0.09082753406f, -0.04664855374f), new Float3(-0.37728353f, 0.05445141085f, 0.2391488697f), new Float3(0.1259579313f, 0.348394558f, 0.2554522098f), new Float3(-0.1406285511f, -0.270877371f, -0.3306796947f), new Float3(-0.1580694418f, 0.4162931958f, -0.06491553533f),
            new Float3(0.2477612106f, -0.2927867412f, -0.2353514536f), new Float3(0.2916132853f, 0.3312535401f, 0.08793624968f), new Float3(0.07365265219f, -0.1666159848f, 0.411478311f), new Float3(-0.26126526f, -0.2422237692f, 0.2748965434f), new Float3(-0.3721862032f, 0.252790166f, 0.008634938242f), new Float3(-0.3691191571f, -0.255281188f, 0.03290232422f), new Float3(0.2278441737f, -0.3358364886f, 0.1944244981f), new Float3(0.363398169f, -0.2310190248f, 0.1306597909f),
            new Float3(-0.304231482f, -0.2698452035f, 0.1926830856f), new Float3(-0.3199312232f, 0.316332536f, -0.008816977938f), new Float3(0.2874852279f, 0.1642275508f, -0.304764754f), new Float3(-0.1451096801f, 0.3277541114f, -0.2720669462f), new Float3(0.3220090754f, 0.0511344108f, 0.3101538769f), new Float3(-0.1247400865f, -0.04333605335f, -0.4301882115f), new Float3(-0.2829555867f, -0.3056190617f, -0.1703910946f), new Float3(0.1069384374f, 0.3491024667f, -0.2630430352f),
            new Float3(-0.1420661144f, -0.3055376754f, -0.2982682484f), new Float3(-0.250548338f, 0.3156466809f, -0.2002316239f), new Float3(0.3265787872f, 0.1871229129f, 0.2466400438f), new Float3(0.07646097258f, -0.3026690852f, 0.324106687f), new Float3(0.3451771584f, 0.2757120714f, -0.0856480183f), new Float3(0.298137964f, 0.2852657134f, 0.179547284f), new Float3(0.2812250376f, 0.3466716415f, 0.05684409612f), new Float3(0.4390345476f, -0.09790429955f, -0.01278335452f),
            new Float3(0.2148373234f, 0.1850172527f, 0.3494474791f), new Float3(0.2595421179f, -0.07946825393f, 0.3589187731f), new Float3(0.3182823114f, -0.307355516f, -0.08203022006f), new Float3(-0.4089859285f, -0.04647718411f, 0.1818526372f), new Float3(-0.2826749061f, 0.07417482322f, 0.3421885344f), new Float3(0.3483864637f, 0.225442246f, -0.1740766085f), new Float3(-0.3226415069f, -0.1420585388f, -0.2796816575f), new Float3(0.4330734858f, -0.118868561f, -0.02859407492f),
            new Float3(-0.08717822568f, -0.3909896417f, -0.2050050172f), new Float3(-0.2149678299f, 0.3939973956f, -0.03247898316f), new Float3(-0.2687330705f, 0.322686276f, -0.1617284888f), new Float3(0.2105665099f, -0.1961317136f, -0.3459683451f), new Float3(0.4361845915f, -0.1105517485f, 0.004616608544f), new Float3(0.05333333359f, -0.313639498f, -0.3182543336f), new Float3(-0.05986216652f, 0.1361029153f, -0.4247264031f), new Float3(0.3664988455f, 0.2550543014f, -0.05590974511f),
            new Float3(-0.2341015558f, -0.182405731f, 0.3382670703f), new Float3(-0.04730947785f, -0.4222150243f, -0.1483114513f), new Float3(-0.2391566239f, -0.2577696514f, -0.2808182972f), new Float3(-0.1242081035f, 0.4256953395f, -0.07652336246f), new Float3(0.2614832715f, -0.3650179274f, 0.02980623099f), new Float3(-0.2728794681f, -0.3499628774f, 0.07458404908f), new Float3(0.007892900508f, -0.1672771315f, 0.4176793787f), new Float3(-0.01730330376f, 0.2978486637f, -0.3368779738f),
            new Float3(0.2054835762f, -0.3252600376f, -0.2334146693f), new Float3(-0.3231994983f, 0.1564282844f, -0.2712420987f), new Float3(-0.2669545963f, 0.2599343665f, -0.2523278991f), new Float3(-0.05554372779f, 0.3170813944f, -0.3144428146f), new Float3(-0.2083935713f, -0.310922837f, -0.2497981362f), new Float3(0.06989323478f, -0.3156141536f, 0.3130537363f), new Float3(0.3847566193f, -0.1605309138f, -0.1693876312f), new Float3(-0.3026215288f, -0.3001537679f, -0.1443188342f),
            new Float3(0.3450735512f, 0.08611519592f, 0.2756962409f), new Float3(0.1814473292f, -0.2788782453f, -0.3029914042f), new Float3(-0.03855010448f, 0.09795110726f, 0.4375151083f), new Float3(0.3533670318f, 0.2665752752f, 0.08105160988f), new Float3(-0.007945601311f, 0.140359426f, -0.4274764309f), new Float3(0.4063099273f, -0.1491768253f, -0.1231199324f), new Float3(-0.2016773589f, 0.008816271194f, -0.4021797064f), new Float3(-0.07527055435f, -0.425643481f, -0.1251477955f),
        };
}
