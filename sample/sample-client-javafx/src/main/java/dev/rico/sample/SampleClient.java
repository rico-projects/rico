package dev.rico.sample;

import dev.rico.client.remoting.AbstractRemotingApplication;
import dev.rico.client.remoting.ClientContext;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;

public class SampleClient extends AbstractRemotingApplication {

    @Override
    protected URL getServerEndpoint() throws MalformedURLException {
        return new URL("http://localhost:8080/remoting");
    }

    @Override
    protected void start(Stage primaryStage, ClientContext clientContext) throws Exception {
        SampleView sampleView = new SampleView(clientContext);
        primaryStage.setScene(new Scene(sampleView.getParent()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
