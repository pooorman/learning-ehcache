package hahn.learning.jni;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by jianghan on 16/5/1.
 */
public class App {

    // private static final int MB = 1024 * 1024;
    private static final int MB = 10;

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // Unsafe f = Unsafe.getUnsafe();
        /** Field f = Unsafe.class.getDeclaredField("theUnsafe");
         f.setAccessible(true);
         Unsafe us = (Unsafe) f.get(null);
         long addr = us.allocateMemory(1024);
         System.out.println(addr);
         us.reallocateMemory(addr, 1024);
         us.freeMemory(addr);*/

        byte[] source = new byte[MB];
        Arrays.fill(source, (byte) 49);

        ByteBuffer bb = ByteBuffer.allocateDirect(MB);
        System.out.println(bb.remaining());
        bb.put(source, 0, source.length);
        System.out.println(bb.remaining());
        bb.rewind();
        bb.mark();
        System.out.println(bb.remaining());

        byte[] target = new byte[MB];
        bb.get(target, 0, bb.remaining());
        System.out.println(bb.remaining());

        bb.reset();
        System.out.println(bb.remaining());

        byte[] target2 = new byte[MB];
        bb.get(target2, 0, bb.remaining());
        System.out.println(bb.remaining());

        bb.reset();
        System.out.println(bb.remaining());
    }

}
