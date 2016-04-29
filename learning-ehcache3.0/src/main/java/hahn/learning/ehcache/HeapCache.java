package hahn.learning.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * Created by jianghan on 16-4-28.
 */
public class HeapCache {
    public static void main(String[] args) throws Exception {
        CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class, ResourcePoolsBuilder.heap(10)))
                .build();
        manager.init();

        Cache<String, byte[]> preConfigured = manager.getCache("preConfigured", String.class, byte[].class);

        Cache<String, byte[]> myCache = manager
                .createCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class,
                        ResourcePoolsBuilder.heap(1).build()).withEvictionAdvisor(new EvictionAdvisor<String, byte[]>() {
                    @Override
                    public boolean adviseAgainstEviction(String key, byte[] value) {
                        System.out.println("eviction: " + key);
                        return true;
                    }
                }));

        byte[] value;
        myCache.put("KEY_1", new byte[1024]);
        value = myCache.get("KEY_1");
        System.out.println(value.length);

        myCache.put("KEY_2", new byte[1024]);
        value = myCache.get("KEY_2");
        System.out.println(value.length);
        value = myCache.get("KEY_1");
        System.out.println(value.length);

        manager.removeCache("preConfigured");
        manager.close();
    }
}
