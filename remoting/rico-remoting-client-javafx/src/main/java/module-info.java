module dev.rico.remoting.client.javafx {

    exports dev.rico.remoting.client.javafx;
    exports dev.rico.remoting.client.javafx.window;
    exports dev.rico.remoting.client.javafx.binding;
    exports dev.rico.remoting.client.javafx.view;

    requires transitive dev.rico.remoting.client;
    requires transitive dev.rico.client.javafx;

    requires static org.apiguardian.api;
    requires org.slf4j;
    requires javafx.graphics;
    requires javafx.fxml;
}
