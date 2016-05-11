package hahn.learning.jni;

import com.google.common.cache.*;
import sun.misc.Cleaner;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jianghan on 16-5-9.
 */
public class TestBuffer {

    private static final int MB = 1024 * 1024;

    public static void main(String[] args) throws Exception {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        LoadingCache<String, ByteBuffer> cache = CacheBuilder.newBuilder()
                .maximumWeight(2048L * MB)
                .weigher(weigher)
                .removalListener(removalListener)
                .build(CacheLoader.asyncReloading(cacheLoader, threadPool));


        for (int i = 0; i < 1000000; i++) {
            long start = System.currentTimeMillis();
            for (int idx = 0; idx < 20000; idx++) {
                String key = String.format("KEY_%05d", idx);
                byte[] source = new byte[MB];
                Arrays.fill(source, (byte) 48);

                ByteBuffer bb = ByteBuffer.allocateDirect(MB);
                bb = bb.put(source, 0, source.length);
                bb.rewind();
                cache.put(key, bb);

                // Thread.sleep(100);
            }
            long end = System.currentTimeMillis();
            System.out.println("Put: " + (end - start));
            for (int idx = 0; idx < 20000; idx++) {
                String key = String.format("KEY_%05d", idx);
                ByteBuffer value = cache.get(key);

                if (value == null) {
                    System.out.println(key);
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Get: " + (end - start));
            System.out.println("Cache size: " + cache.size());

            cache.invalidateAll();
            end = System.currentTimeMillis();
            System.out.println("Remove: " + (end - start));
        }

    }

    private static final CacheLoader<String, ByteBuffer> cacheLoader = new CacheLoader<String, ByteBuffer>() {
        @Override
        public ByteBuffer load(String key) throws Exception {
            byte[] source = new byte[MB];
            Arrays.fill(source, (byte) 48);

            ByteBuffer bb = ByteBuffer.allocateDirect(MB);
            bb = bb.put(source, 0, source.length);
            bb.rewind();
            return bb;
        }
    };
    private static final Weigher<String, ByteBuffer> weigher = new Weigher<String, ByteBuffer>() {
        @Override
        public int weigh(String key, ByteBuffer value) {
            return value.remaining();
        }
    };
    private static final RemovalListener<String, ByteBuffer> removalListener = new RemovalListener<String, ByteBuffer>() {
        @Override
        public void onRemoval(RemovalNotification<String, ByteBuffer> notification) {
            try {
                // System.out.println("Remove: " + notification.getKey());
                ByteBuffer bb = notification.getValue();
                Field cleanerField = bb.getClass().getDeclaredField("cleaner");
                cleanerField.setAccessible(true);
                Cleaner cleaner = (Cleaner) cleanerField.get(bb);
                cleaner.clean();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

}
