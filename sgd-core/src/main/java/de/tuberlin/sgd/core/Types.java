package de.tuberlin.sgd.core;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public final class Types {

    // Disallow instantiation.
    private Types() {}

    // ---------------------------------------------------

    public enum PrimitiveType {

        SHORT(2),

        INT(4),

        LONG(8),

        FLOAT(4),

        DOUBLE(8);

        public final int size;

        PrimitiveType(final int size) {
            Preconditions.checkArgument(size > 0);
            this.size = size;
        }
    }

    // ---------------------------------------------------

    public interface ITypeInformation {

        public abstract int size();

        public abstract int getNumberOfFields();

        public abstract ITypeInformation getField(int[] selector);

        public abstract ITypeInformation getField(int pos);

        public abstract int getBaseOffset();

        public abstract int getFieldOffset(int[] selector);

        public abstract int getFieldOffset(int pos);
    }

    // ---------------------------------------------------

    public static final class PrimitiveTypeInformation implements ITypeInformation {

        public final PrimitiveType type;

        public final int baseOffset;

        public PrimitiveTypeInformation(final int baseOffset, final PrimitiveType type) {
            Preconditions.checkNotNull(type);
            this.baseOffset = baseOffset;
            this.type = type;
        }

        @Override
        public int size() {
            return type.size;
        }

        @Override
        public int getNumberOfFields() {
            return 1;
        }

        @Override
        public ITypeInformation getField(int[] selector) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ITypeInformation getField(int pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getBaseOffset() {
            return baseOffset;
        }

        @Override
        public int getFieldOffset(int[] selector) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFieldOffset(int pos) {
            return 0;
        }

        @Override
        public String toString() {
            return type.toString();
        }
    }

    // ---------------------------------------------------

    public static final class CompoundTypeInformation implements ITypeInformation {

        public final ITypeInformation[] fields;

        public final int baseOffset;

        public final int[] fieldOffsets;

        public final int size;

        public CompoundTypeInformation(final int baseOffset, final ITypeInformation[] fields) {
            Preconditions.checkNotNull(fields);
            Preconditions.checkArgument(fields.length > 0);
            this.fields = fields;
            this.baseOffset = baseOffset;
            this.fieldOffsets = new int[fields.length];
            int size = 0, i = 0;
            for (final ITypeInformation type : fields) {
                size += type.size();
                fieldOffsets[i++] = baseOffset + size;
            }
            this.size = size;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getNumberOfFields() {
            return fields.length;
        }

        @Override
        public ITypeInformation getField(int[] selector) {
            Preconditions.checkNotNull(selector);
            ITypeInformation ti = this;
            for (int p : selector)
                ti = ti.getField(p);
            return ti;
        }

        @Override
        public ITypeInformation getField(int pos) {
            return fields[pos];
        }

        @Override
        public int getBaseOffset() {
            return baseOffset;
        }

        @Override
        public int getFieldOffset(int[] selector) {
            Preconditions.checkNotNull(selector);
            ITypeInformation ti = this;
            for (int p : selector)
                ti = ti.getField(p);
            return ti.getBaseOffset();
        }

        @Override
        public int getFieldOffset(int pos) {
            return 0;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < fields.length; ++i) {
                sb.append(fields[i].toString());
                if (i < fields.length - 1)
                    sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // ---------------------------------------------------

    public static final class TypeInfoBuilder {

        private TypeInfoBuilder parent;

        private List<ITypeInformation> types;

        private int offset = 0;

        private int baseOffset = 0;

        public TypeInfoBuilder(final TypeInfoBuilder parent, final int offset) {
            this.parent = parent;
            this.types = new ArrayList<>();
            this.baseOffset = this.offset = offset;
        }

        public TypeInfoBuilder open() {
            final TypeInfoBuilder tib = new TypeInfoBuilder(this, offset);
            return tib;
        }

        public TypeInfoBuilder close() {
            Preconditions.checkState(parent != null);
            parent.add(build(baseOffset));
            return parent;
        }

        public TypeInfoBuilder add(final ITypeInformation ti) {
            Preconditions.checkNotNull(ti);
            types.add(ti);
            offset += ti.size();
            return this;
        }

        public TypeInfoBuilder add(final PrimitiveType type) {
            Preconditions.checkNotNull(type);
            PrimitiveTypeInformation pti = new PrimitiveTypeInformation(offset, type);
            types.add(pti);
            offset += pti.size();
            return this;
        }

        public ITypeInformation build(int offset) {
            Preconditions.checkState(!types.isEmpty());
            if (types.size() == 1)
                return types.get(0);
            else {
                final ITypeInformation[] tia = new ITypeInformation[types.size()];
                return new CompoundTypeInformation(offset, types.toArray(tia));
            }
        }

        public ITypeInformation build() {
            return build(offset);
        }


        public static TypeInfoBuilder newType() {
            return  new TypeInfoBuilder(null, 0);
        }
    }

    // ---------------------------------------------------

    public static void main(final String[] args) {

        ITypeInformation ti = TypeInfoBuilder.newType()
                .add(PrimitiveType.SHORT)
                .add(PrimitiveType.INT)
                .open()
                    .add(PrimitiveType.DOUBLE)
                    .add(PrimitiveType.LONG)
                    .add(PrimitiveType.FLOAT)
                    .open()
                        .add(PrimitiveType.INT)
                        .add(PrimitiveType.INT)
                    .close()
                .close()
                .build();

        System.out.println("sizeof(" + ti.toString() + ") = " + ti.size());

        System.out.println("relative offset: " + ti.getFieldOffset(new int[] {2, 3, 1}));
    }
}
