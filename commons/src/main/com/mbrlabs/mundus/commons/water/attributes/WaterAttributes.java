package com.mbrlabs.mundus.commons.water.attributes;

import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.MundusAttribute;

import java.util.Comparator;
import java.util.Iterator;

/**
 *  Copied from libGDX Attributes, due to 64 attribute limit in libGDX.
 * @author JamesTKhan
 * @version August 13, 2022
 */
public class WaterAttributes implements Iterable<MundusAttribute>, Comparator<MundusAttribute>, Comparable<WaterAttributes> {
    protected long mask;
    protected final Array<MundusAttribute> MundusAttributes = new Array<MundusAttribute>();

    protected boolean sorted = true;

    /** Sort the MundusAttributes by their ID */
    public final void sort () {
        if (!sorted) {
            MundusAttributes.sort(this);
            sorted = true;
        }
    }

    /** @return Bitwise mask of the ID's of all the containing MundusAttributes */
    public final long getMask () {
        return mask;
    }

    /** Example usage: ((BlendingMundusAttribute)material.get(BlendingMundusAttribute.ID)).sourceFunction;
     * @return The MundusAttribute (which can safely be cast) if any, otherwise null */
    public final MundusAttribute get (final long type) {
        if (has(type)) for (int i = 0; i < MundusAttributes.size; i++)
            if (MundusAttributes.get(i).type == type) return MundusAttributes.get(i);
        return null;
    }

    /** Example usage: ((BlendingMundusAttribute)material.get(BlendingMundusAttribute.ID)).sourceFunction;
     * @return The MundusAttribute if any, otherwise null */
    public final <T extends MundusAttribute> T get (Class<T> clazz, final long type) {
        return (T)get(type);
    }

    /** Get multiple MundusAttributes at once. Example: material.get(out, ColorMundusAttribute.Diffuse | ColorMundusAttribute.Specular |
     * TextureMundusAttribute.Diffuse); */
    public final Array<MundusAttribute> get (final Array<MundusAttribute> out, final long type) {
        for (int i = 0; i < MundusAttributes.size; i++)
            if ((MundusAttributes.get(i).type & type) != 0) out.add(MundusAttributes.get(i));
        return out;
    }

    /** Removes all MundusAttributes */
    public void clear () {
        mask = 0;
        MundusAttributes.clear();
    }

    /** @return The amount of MundusAttributes this material contains. */
    public int size () {
        return MundusAttributes.size;
    }

    private final void enable (final long mask) {
        this.mask |= mask;
    }

    private final void disable (final long mask) {
        this.mask &= ~mask;
    }

    /** Add a MundusAttribute to this material. If the material already contains an MundusAttribute of the same type it is overwritten. */
    public final void set (final MundusAttribute MundusAttribute) {
        final int idx = indexOf(MundusAttribute.type);
        if (idx < 0) {
            enable(MundusAttribute.type);
            MundusAttributes.add(MundusAttribute);
            sorted = false;
        } else {
            MundusAttributes.set(idx, MundusAttribute);
        }
        sort(); //FIXME: See #4186
    }

    /** Add multiple MundusAttributes to this material. If the material already contains an MundusAttribute of the same type it is overwritten. */
    public final void set (final MundusAttribute MundusAttribute1, final MundusAttribute MundusAttribute2) {
        set(MundusAttribute1);
        set(MundusAttribute2);
    }

    /** Add multiple MundusAttributes to this material. If the material already contains an MundusAttribute of the same type it is overwritten. */
    public final void set (final MundusAttribute MundusAttribute1, final MundusAttribute MundusAttribute2, final MundusAttribute MundusAttribute3) {
        set(MundusAttribute1);
        set(MundusAttribute2);
        set(MundusAttribute3);
    }

    /** Add multiple MundusAttributes to this material. If the material already contains an MundusAttribute of the same type it is overwritten. */
    public final void set (final MundusAttribute MundusAttribute1, final MundusAttribute MundusAttribute2, final MundusAttribute MundusAttribute3,
                           final MundusAttribute MundusAttribute4) {
        set(MundusAttribute1);
        set(MundusAttribute2);
        set(MundusAttribute3);
        set(MundusAttribute4);
    }

    /** Add an array of MundusAttributes to this material. If the material already contains an MundusAttribute of the same type it is
     * overwritten. */
    public final void set (final MundusAttribute... MundusAttributes) {
        for (final MundusAttribute attr : MundusAttributes)
            set(attr);
    }

    /** Add an array of MundusAttributes to this material. If the material already contains an MundusAttribute of the same type it is
     * overwritten. */
    public final void set (final Iterable<MundusAttribute> MundusAttributes) {
        for (final MundusAttribute attr : MundusAttributes)
            set(attr);
    }

    /** Removes the MundusAttribute from the material, i.e.: material.remove(BlendingMundusAttribute.ID); Can also be used to remove multiple
     * MundusAttributes also, i.e. remove(MundusAttributeA.ID | MundusAttributeB.ID); */
    public final void remove (final long mask) {
        for (int i = MundusAttributes.size - 1; i >= 0; i--) {
            final long type = MundusAttributes.get(i).type;
            if ((mask & type) == type) {
                MundusAttributes.removeIndex(i);
                disable(type);
                sorted = false;
            }
        }
        sort(); //FIXME: See #4186
    }

    /** @return True if this collection has the specified MundusAttribute, i.e. MundusAttributes.has(ColorMundusAttribute.Diffuse); Or when multiple
     *         MundusAttribute types are specified, true if this collection has all specified MundusAttributes, i.e. MundusAttributes.has(out,
     *         ColorMundusAttribute.Diffuse | ColorMundusAttribute.Specular | TextureMundusAttribute.Diffuse); */
    public final boolean has (final long type) {
        return type != 0 && (this.mask & type) == type;
    }

    /** @return the index of the MundusAttribute with the specified type or negative if not available. */
    protected int indexOf (final long type) {
        if (has(type)) for (int i = 0; i < MundusAttributes.size; i++)
            if (MundusAttributes.get(i).type == type) return i;
        return -1;
    }

    /** Check if this collection has the same MundusAttributes as the other collection. If compareValues is true, it also compares the
     * values of each MundusAttribute.
     * @param compareValues True to compare MundusAttribute values, false to only compare MundusAttribute types
     * @return True if this collection contains the same MundusAttributes (and optionally MundusAttribute values) as the other. */
    public final boolean same (final WaterAttributes other, boolean compareValues) {
        if (other == this) return true;
        if ((other == null) || (mask != other.mask)) return false;
        if (!compareValues) return true;
        sort();
        other.sort();
        for (int i = 0; i < MundusAttributes.size; i++)
            if (!MundusAttributes.get(i).equals(other.MundusAttributes.get(i))) return false;
        return true;
    }

    /** See {@link #same(WaterAttributes, boolean)}
     * @return True if this collection contains the same MundusAttributes (but not values) as the other. */
    public final boolean same (final WaterAttributes other) {
        return same(other, false);
    }

    /** Used for sorting MundusAttributes by type (not by value) */
    @Override
    public final int compare (final MundusAttribute arg0, final MundusAttribute arg1) {
        return (int)(arg0.type - arg1.type);
    }

    /** Used for iterating through the MundusAttributes */
    @Override
    public final Iterator<MundusAttribute> iterator () {
        return MundusAttributes.iterator();
    }

    /** @return A hash code based on only the MundusAttribute values, which might be different compared to {@link #hashCode()} because the latter
     * might include other properties as well, i.e. the material id. */
    public int MundusAttributesHash () {
        sort();
        final int n = MundusAttributes.size;
        long result = 71 + mask;
        int m = 1;
        for (int i = 0; i < n; i++)
            result += mask * MundusAttributes.get(i).hashCode() * (m = (m * 7) & 0xFFFF);
        return (int)(result ^ (result >> 32));
    }

    @Override
    public int hashCode () {
        return MundusAttributesHash();
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof WaterAttributes)) return false;
        if (other == this) return true;
        return same((WaterAttributes)other, true);
    }

    @Override
    public int compareTo (WaterAttributes other) {
        if (other == this)
            return 0;
        if (mask != other.mask)
            return mask < other.mask ? -1 : 1;
        sort();
        other.sort();
        for (int i = 0; i < MundusAttributes.size; i++) {
            final int c = MundusAttributes.get(i).compareTo(other.MundusAttributes.get(i));
            if (c != 0)
                return c < 0 ? -1 : (c > 0 ? 1 : 0);
        }
        return 0;
    }
}
