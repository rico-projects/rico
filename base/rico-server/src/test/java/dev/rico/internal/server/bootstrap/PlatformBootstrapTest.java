package dev.rico.internal.server.bootstrap;

import dev.rico.core.Configuration;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ServerCoreComponents;
import dev.rico.server.spi.ServerModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Test for {@link PlatformBootstrap}.
 */
public class PlatformBootstrapTest {

    private MockServerCoreComponents coreComponents;

    @BeforeMethod
    public void setUp() {
        coreComponents = new MockServerCoreComponents();
    }

    @Test
    public void testInitEmptyModule() throws Exception {
        // given
        final PlatformBootstrap platform = new PlatformBootstrap();

        // when
        platform.initModules(emptySet(), null);

        // then
        assertThat(coreComponents.getInstance(Modules.class), is(empty()));
    }


    @Test
    public void testInitSingleModule() throws Exception {
        // given
        final PlatformBootstrap platform = new PlatformBootstrap();

        // when
        platform.initModules(Set.of(Module1.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), contains(Module1.class));
    }

    @ModuleDefinition(name = "Module1")
    public static class Module1 implements ServerModule {

        @Override
        public boolean shouldBoot(Configuration configuration) {
            return true;
        }

        @Override
        public void initialize(ServerCoreComponents coreComponents) {
            coreComponents.getInstance(Modules.class).add(getClass());
        }
    }

    private static class MockServerCoreComponents implements ServerCoreComponents {

        private final Map<Class<?>, Object> instances = new HashMap<>();

        private MockServerCoreComponents() {
            instances.put(Modules.class, new Modules());
        }

        @Override
        public <T> void provideInstance(Class<T> cls, T instance) {
            instances.put(cls, instance);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getInstance(Class<T> cls) {
            return (T) instances.get(cls);
        }
    }

    private static class Modules extends ArrayList<Class<?>> {
    }
}
