package sample;

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class DetailModel {

    private Property<String> id;

    private Property<String> name;

    private Property<String> description;

    public Property<String> idProperty() {
        return id;
    }

    public Property<String> nameProperty() {
        return name;
    }

    public Property<String> descriptionProperty() {
        return description;
    }
}
