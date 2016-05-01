package hahn.learning.jni;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by jianghan on 16/5/1.
 */
public class App {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // Unsafe f = Unsafe.getUnsafe();
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe us = (Unsafe) f.get(null);
        long addr = us.allocateMemory(1024);
        System.out.println(addr);
        us.reallocateMemory(addr, 1024);

        ByteBuffer bb = ByteBuffer.allocateDirect(1024);
    }

}
