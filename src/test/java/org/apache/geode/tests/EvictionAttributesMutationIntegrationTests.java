package org.apache.geode.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.EvictionAttributesMutator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.junit.Test;

/**
 * The EvictionAttributesMutationIntegrationTests class...
 *
 * @author John Blum
 * @since 1.0.0
 */
public class EvictionAttributesMutationIntegrationTests {

	private static final String APACHE_GEODE_LOG_LEVEL = "error";

	@Test
	public void cacheXmlEvictionAttributesMutated() {

		Cache cache = null;

		try {
			cache = new CacheFactory()
				.set("name", EvictionAttributesMutationIntegrationTests.class.getSimpleName())
				.set("log-level", APACHE_GEODE_LOG_LEVEL)
				.set("cache-xml-file", "eviction-cache.xml")
				.create();

			assertThat(cache).isNotNull();
			assertThat(cache.getName()).isEqualTo(EvictionAttributesMutationIntegrationTests.class.getSimpleName());

			Region<Object, Object> example = cache.getRegion("Example");

			assertThat(example).isNotNull();
			assertThat(example.getName()).isEqualTo("Example");
			assertThat(example.getAttributes()).isNotNull();

			EvictionAttributes evictionAttributes = example.getAttributes().getEvictionAttributes();

			assertThat(evictionAttributes).isNotNull();
			assertThat(evictionAttributes.getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
			assertThat(evictionAttributes.getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
			assertThat(evictionAttributes.getMaximum()).isEqualTo(500);

			example.getAttributesMutator().getEvictionAttributesMutator().setMaximum(1000);

			assertThat(example.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(1000);
		}
		finally {
			Optional.ofNullable(cache).ifPresent(Cache::close);
		}
	}

	@Test
	public void evictionAttributesMutated() {

		Cache cache = null;

		try {
			cache = new CacheFactory()
				.set("name", EvictionAttributesMutationIntegrationTests.class.getSimpleName())
				.set("log-level", APACHE_GEODE_LOG_LEVEL)
				.create();

			assertThat(cache).isNotNull();
			assertThat(cache.getName()).isEqualTo(EvictionAttributesMutationIntegrationTests.class.getSimpleName());

			EvictionAttributes evictionAttributes =
				EvictionAttributes.createLRUEntryAttributes(101, EvictionAction.LOCAL_DESTROY);

			Region<Object, Object> example = cache.createRegionFactory(RegionShortcut.LOCAL)
				.setEvictionAttributes(evictionAttributes)
				.create("Example");

			assertThat(example).isNotNull();
			assertThat(example.getName()).isEqualTo("Example");
			assertThat(example.getAttributes()).isNotNull();

			EvictionAttributes actual = example.getAttributes().getEvictionAttributes();

			assertThat(actual).isEqualTo(evictionAttributes);

			AttributesMutator<Object, Object> attributesMutator = example.getAttributesMutator();

			assertThat(attributesMutator).isNotNull();

			EvictionAttributesMutator evictionAttributesMutator = attributesMutator.getEvictionAttributesMutator();

			assertThat(evictionAttributesMutator).isNotNull();

			evictionAttributesMutator.setMaximum(202);

			assertThat(example.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(202);
		}
		finally {
			Optional.ofNullable(cache).ifPresent(Cache::close);
		}
	}
}
