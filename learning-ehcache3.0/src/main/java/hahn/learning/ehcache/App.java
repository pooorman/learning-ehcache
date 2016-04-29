package hahn.learning.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws Exception {
        CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class, ResourcePoolsBuilder.heap(10)))
                .build();
        manager.init();

        Cache<String, byte[]> preConfigured = manager.getCache("preConfigured", String.class, byte[].class);

        Cache<String, byte[]> myCache = manager
                .createCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class,
                        ResourcePoolsBuilder.heap(10).offheap(10, MemoryUnit.MB)).build());

        myCache.put("KEY_1", new byte[1024]);
        byte[] value = myCache.get("KEY_1");
        System.out.println(value.length);

        manager.removeCache("preConfigured");
        manager.close();
    }

}
