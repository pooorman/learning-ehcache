package hahn.learning.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jianghan on 16-4-28.
 */
public class OffHeapCache {

    public static void main(String[] args) throws Exception {
        CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class, ResourcePoolsBuilder.heap(10)))
                .build();
        manager.init();

        final Cache<String, byte[]> myCache = manager
                .createCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class,
                        ResourcePoolsBuilder.heap(1).offheap(10, MemoryUnit.GB)).withEvictionAdvisor(new EvictionAdvisor<String, byte[]>() {
                    @Override
                    public boolean adviseAgainstEviction(String key, byte[] value) {
                        // System.out.println("eviction: " + key);
                        return true;
                    }
                }).build());

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            long start = System.currentTimeMillis();
            List<String> keys = new ArrayList<>();
            for (int idx = 0; idx < 20000; idx++) {
                keys.add(String.format("KEY_%04d", i));
            }
            for (final String key : keys) {
                byte[] value = new byte[1024 * 1024];
                Arrays.fill(value, (byte) 48);
                myCache.put(key, value);
            }
            long end = System.currentTimeMillis();
            System.out.println("Put: " + (end - start));
            for (String key : keys) {
                byte[] value = myCache.get(key);
                if (value == null) {
                    System.out.println(key);
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Get: " + (end - start));
            Iterator<Cache.Entry<String, byte[]>> iter = myCache.iterator();
            int size = 0;
            while (iter.hasNext()) {
                ++size;
                iter.next();
            }
            System.out.println("Cache size: " + size);
            for (String key : keys) {
                myCache.remove(key);
            }
            end = System.currentTimeMillis();
            System.out.println("Remove: " + (end - start));
        }

        manager.removeCache("preConfigured");
        manager.close();
    }

}
