package dev.rico.server.remoting.test;

import dev.rico.internal.server.remoting.test.SpringTestBootstrap;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import dev.rico.internal.server.remoting.test.TestClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = SpringTestBootstrap.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class clientConnectorExtension implements BeforeEachCallback, AfterEachCallback {

    @Autowired
    private TestClientContext clientContext;

    public TestClientContext getContext() {
        return clientContext;
    }


    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        try {
   //         clientContext.disconnect().get();
        } catch (Exception e) {
            throw new ControllerTestException("Can not disconnect client context!", e);
        }
        System.out.println("This is after");
    }

    //This is getting null
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        System.out.println("This is before");
     //   clientContext.connect().get();
    }
}
