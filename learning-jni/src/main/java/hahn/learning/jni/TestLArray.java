package hahn.learning.jni;

import com.google.common.cache.*;
import hahn.learning.larray.LByteArray;
import sun.misc.Cleaner;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jianghan on 16-5-9.
 */
public class TestLArray {

    private static final int MB = 1024 * 1024;

    public static void main(String[] args) throws Exception {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        LoadingCache<String, LByteArray> cache = CacheBuilder.newBuilder()
                .maximumWeight(2048L * MB)
                .weigher(weigher)
                .removalListener(removalListener)
                .build(CacheLoader.asyncReloading(cacheLoader, threadPool));

        for (int i = 0; i < 100000; i++) {
            long start = System.currentTimeMillis();
            for (int idx = 0; idx < 20000; idx++) {
                String key = String.format("KEY_%05d", idx);
                byte[] source = new byte[MB];
                Arrays.fill(source, (byte) 48);
                LByteArray lba = LByteArray.newLByteArray(source);
                cache.put(key, lba);
            }
            long end = System.currentTimeMillis();
            System.out.println("Put: " + (end - start));
            for (int idx = 0; idx < 20000; idx++) {
                String key = String.format("KEY_%05d", idx);
                LByteArray value = cache.get(key);

                if (value == null) {
                    System.out.println(key);
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Get: " + (end - start));
            System.out.println("Cache size: " + cache.size());
            for (LByteArray lba : cache.asMap().values()) {
                System.out.println(lba.m().address());
            }

            cache.invalidateAll();
            end = System.currentTimeMillis();
            System.out.println("Remove: " + (end - start));
        }

    }

    private static final CacheLoader<String, LByteArray> cacheLoader = new CacheLoader<String, LByteArray>() {
        @Override
        public LByteArray load(String key) throws Exception {
            byte[] source = new byte[MB];
            Arrays.fill(source, (byte) 48);
            LByteArray lba = LByteArray.newLByteArray(MB);
            lba.readFromArray(source);
            return lba;
        }
    };
    private static final Weigher<String, LByteArray> weigher = new Weigher<String, LByteArray>() {
        @Override
        public int weigh(String key, LByteArray value) {
            return value.length();
        }
    };
    private static final RemovalListener<String, LByteArray> removalListener = new RemovalListener<String, LByteArray>() {
        @Override
        public void onRemoval(RemovalNotification<String, LByteArray> notification) {
            // System.out.println("Remove: " + notification.getKey());
            notification.getValue().free();
        }
    };

}
