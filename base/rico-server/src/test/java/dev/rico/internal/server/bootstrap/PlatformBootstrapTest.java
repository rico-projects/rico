package dev.rico.internal.server.bootstrap;

import dev.rico.core.Configuration;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ModuleInitializationException;
import dev.rico.server.spi.ServerCoreComponents;
import dev.rico.server.spi.ServerModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Test for {@link PlatformBootstrap}.
 */
public class PlatformBootstrapTest {

    private static final String MODULE_1 = "Module1";
    private static final String MODULE_2 = "Module2";

    private MockServerCoreComponents coreComponents;
    private PlatformBootstrap platform;

    @BeforeMethod
    public void setUp() {
        platform = new PlatformBootstrap();
        coreComponents = new MockServerCoreComponents();
    }

    @Test
    public void testInitEmptyModule() throws Exception {
        // when
        platform.initModules(emptyList(), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), is(empty()));
    }

    @Test
    public void testInitSingleModule() throws Exception {
        // when
        platform.initModules(List.of(TestModule.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), contains(TestModule.class));
    }

    @Test
    public void testInitTwoModule() throws Exception {
        // when
        platform.initModules(List.of(TestModule.class, SecondModule.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), containsInAnyOrder(TestModule.class, SecondModule.class));
    }

    @Test
    public void testInitTwoModuleReversed() throws Exception {
        // when
        platform.initModules(List.of(SecondModule.class, TestModule.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), containsInAnyOrder(TestModule.class, SecondModule.class));
    }

    @Test
    public void testInitTwoDependentModule() throws Exception {
        // when
        platform.initModules(List.of(TestModule.class, ModuleWithCorrectOrderForDependencies.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), contains(TestModule.class, ModuleWithCorrectOrderForDependencies.class));
    }

    @Test
    public void testInitTwoDependentModuleReversed() throws Exception {
        // when
        platform.initModules(List.of(ModuleWithCorrectOrderForDependencies.class, TestModule.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), contains(TestModule.class, ModuleWithCorrectOrderForDependencies.class));
    }

    @Test(expectedExceptions = ModuleInitializationException.class)
    public void testInitTwoDependentModuleWithIncorrectOrder() throws Exception {
        platform.initModules(Set.of(TestModule.class, ModuleWithIncorrectOrderForDependencies.class), coreComponents);
    }

    @Test(expectedExceptions = ModuleInitializationException.class)
    public void testInitTwoModulesWithSameName() throws Exception {
        platform.initModules(Set.of(TestModule.class, ModuleWithDuplicatedName.class), coreComponents);
    }

    @Test(expectedExceptions = ModuleInitializationException.class)
    public void testNotAModule() throws Exception {
        platform.initModules(Set.of(NotAModule.class), coreComponents);
    }

    @Test
    public void testInitSecondModuleWithSameNameButNotActive() throws Exception {
        // when
        platform.initModules(List.of(TestModule.class, InactiveModuleWithDuplicatedName.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), contains(TestModule.class));
    }

    @Test
    public void testInitSecondModuleWithSameNameButNotActiveReversed() throws Exception {
        // when
        platform.initModules(List.of(InactiveModuleWithDuplicatedName.class, TestModule.class), coreComponents);

        // then
        assertThat(coreComponents.getInstance(Modules.class), contains(TestModule.class));
    }

    @ModuleDefinition(name = MODULE_1)
    public static class TestModule implements ServerModule {
        @Override
        public boolean shouldBoot(Configuration configuration) {
            return true;
        }

        @Override
        public void initialize(ServerCoreComponents coreComponents) {
            coreComponents.getInstance(Modules.class).add(getClass());
        }
    }

    @ModuleDefinition(name = MODULE_2)
    public static class SecondModule extends TestModule {
    }

    @ModuleDefinition(name = MODULE_2, moduleDependencies = MODULE_1, order = 101)
    public static class ModuleWithCorrectOrderForDependencies extends TestModule {
    }

    @ModuleDefinition(name = MODULE_2, moduleDependencies = MODULE_1, order = 99)
    public static class ModuleWithIncorrectOrderForDependencies extends TestModule {
    }

    @ModuleDefinition(name = MODULE_1)
    public static class ModuleWithDuplicatedName extends TestModule {
    }

    @ModuleDefinition(name = MODULE_1)
    public static class InactiveModuleWithDuplicatedName extends TestModule {
        @Override
        public boolean shouldBoot(Configuration configuration) {
            return false;
        }
    }

    @ModuleDefinition(name = MODULE_2)
    public static class NotAModule {
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
