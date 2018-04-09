package org.apache.geode.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;
import org.junit.Test;

/**
 * Integration tests for Apache Geode's {@literal DEFAULT} {@link Pool}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.Pool
 * @see <a href="https://issues.apache.org/jira/browse/GEODE-4511">Avoid creation of unnecessary default pool</a>
 * @since 1.0.0
 */
public class DefaultPoolTests {

	private static final String APACHE_GEODE_LOG_LEVEL = "error";

	@Test
	public void defaultPoolExists() {

		ClientCache clientCache = null;

		try {
			clientCache = new ClientCacheFactory()
				.set("name", DefaultPoolTests.class.getSimpleName())
				.set("log-level", APACHE_GEODE_LOG_LEVEL)
				.addPoolLocator("localhost", 10334)
				.setPoolSubscriptionEnabled(true)
				.create();

			assertThat(clientCache).isNotNull();
			assertThat(clientCache.getName()).isEqualTo(DefaultPoolTests.class.getSimpleName());

			/*
			Region<Object, Object> example =
				clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("Example");

			assertThat(example).isNotNull();
			assertThat(example.getName()).isEqualTo("Example");
			*/

			Pool defaultPool = PoolManager.find("DEFAULT");

			assertThat(defaultPool).isNotNull();
			assertThat(defaultPool.getName()).isEqualTo("DEFAULT");
		}
		finally {
			Optional.ofNullable(clientCache).ifPresent(ClientCache::close);
		}
	}
}
