package ar.com.pmp.subastados.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@AutoConfigureBefore(value = { WebConfigurer.class, DatabaseConfiguration.class })
public class CacheConfiguration extends JCacheConfigurerSupport{

	private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

	public CacheConfiguration(JHipsterProperties jHipsterProperties) {
		JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

		jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries())).withExpiry(Expirations.timeToLiveExpiration(Duration.of(ehcache.getTimeToLiveSeconds(), TimeUnit.SECONDS))).build());
	}

	@Bean
    @Override
    public CacheManager cacheManager() {
        javax.cache.CacheManager jCacheCacheManager = createCacheManager();
        cacheManagerCustomizer().customize(jCacheCacheManager);
        return new JCacheCacheManager(jCacheCacheManager);
    }
	
	private javax.cache.CacheManager createCacheManager()  {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        return cachingProvider.getCacheManager();
    }
	@Bean
	public JCacheManagerCustomizer cacheManagerCustomizer() {
		return cm -> {
			cm.createCache(ar.com.pmp.subastados.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.User.class.getName(), jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.Authority.class.getName(), jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.User.class.getName() + ".authorities", jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.PersistentToken.class.getName(), jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.User.class.getName() + ".persistentTokens", jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.SocialUserConnection.class.getName(), jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.DestacadoCaballo.class.getName(), jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.Subscriber.class.getName(), jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.domain.Event.class.getName(), jcacheConfiguration);
			cm.createCache(ar.com.pmp.subastados.repository.EventRepository.EVENT_BY_ID_CACHE, jcacheConfiguration);
			
			
			// jhipster-needle-ehcache-add-entry
		};
	}
	
	 @Bean("simpleCacheManager")
	    public SimpleCacheManager simpleCacheManager() {
	        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
	        simpleCacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("usersByLogin")));

	        return simpleCacheManager;

	    }
}
