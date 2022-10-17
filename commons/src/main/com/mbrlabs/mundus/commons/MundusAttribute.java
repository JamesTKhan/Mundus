package com.mbrlabs.mundus.commons;

import com.badlogic.gdx.utils.Array;

/**
 * Copied from libGDX Attribute, due to 64 attribute limit in libGDX.
 *
 * @author JamesTKhan
 * @version August 13, 2022
 */
public abstract class MundusAttribute implements Comparable<MundusAttribute> {
    /** The registered type aliases */
    private final static Array<String> types = new Array<String>();

    /** @return The ID of the specified attribute type, or zero if not available */
    public final static long getAttributeType (final String alias) {
        for (int i = 0; i < types.size; i++)
            if (types.get(i).compareTo(alias) == 0) return 1L << i;
        return 0;
    }

    /** @return The alias of the specified attribute type, or null if not available. */
    public final static String getAttributeAlias (final long type) {
        int idx = -1;
        while (type != 0 && ++idx < 63 && (((type >> idx) & 1) == 0))
            ;
        return (idx >= 0 && idx < types.size) ? types.get(idx) : null;
    }

    /** Call this method to register a custom attribute type, see the wiki for an example. If the alias already exists, then that ID
     * will be reused. The alias should be unambiguously and will by default be returned by the call to {@link #toString()}.
     * @param alias The alias of the type to register, must be different for each dirrect type, will be used for debugging
     * @return the ID of the newly registered type, or the ID of the existing type if the alias was already registered */
    protected final static long register (final String alias) {
        long result = getAttributeType(alias);
        if (result > 0) return result;
        types.add(alias);
        return 1L << (types.size - 1);
    }

    /** The type of this attribute */
    public final long type;

    private final int typeBit;

    protected MundusAttribute (final long type) {
        this.type = type;
        this.typeBit = Long.numberOfTrailingZeros(type);
    }

    /** @return An exact copy of this attribute */
    public abstract MundusAttribute copy ();

    protected boolean equals (MundusAttribute other) {
        return other.hashCode() == hashCode();
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MundusAttribute)) return false;
        final MundusAttribute other = (MundusAttribute)obj;
        if (this.type != other.type) return false;
        return equals(other);
    }

    @Override
    public String toString () {
        return getAttributeAlias(type);
    }

    @Override
    public int hashCode () {
        return 7489 * typeBit;
    }
}
