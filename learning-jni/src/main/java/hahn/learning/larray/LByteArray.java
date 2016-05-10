package hahn.learning.larray;

import java.nio.ByteBuffer;

/**
 * Created by jianghan on 16-5-10.
 */
public class LByteArray {

    private static final MemoryAllocator allocator = new DefaultMemoryAllocator();
    private int size;
    private Memory m;

    public static LByteArray newLByteArray(int size) {
        return new LByteArray(size);
    }

    public static LByteArray newLByteArray(byte[] source) {
        LByteArray lba = new LByteArray(source.length);
        lba.readFromArray(source);
        return lba;
    }

    private LByteArray(int size) {
        this.size = size;
        this.m = allocator.allocate(size);
    }

    public void readFromArray(byte[] source) {
        ByteBuffer bb = UnsafeUtil.newDirectByteBuffer(m.address(), source.length);
        bb.put(source, 0, source.length);
    }

    public byte[] getBytes() {
        ByteBuffer bb = UnsafeUtil.newDirectByteBuffer(m.address(), size);
        byte[] value = new byte[bb.remaining()];
        bb.get(value);
        return value;
    }

    public int length() {
        return size;
    }

    public void free() {
        allocator.release(m);
    }

    public Memory m() {
        return m;
    }

}
