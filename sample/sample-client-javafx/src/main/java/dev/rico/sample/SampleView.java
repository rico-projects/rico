package dev.rico.sample;

import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.FXBinder;
import dev.rico.client.remoting.FXWrapper;
import dev.rico.client.remoting.view.AbstractViewController;
import dev.rico.remoting.Property;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.function.Function;

public class SampleView extends AbstractViewController<TestModel> {

    private TableView<Item> tableView;

    public SampleView(ClientContext clientContext) {
        super(clientContext, "TestController");

        tableView = new TableView<>();

        tableView.getColumns().add(createColumn("String", i -> i.stringValueProperty()));
        tableView.getColumns().add(createColumn("Boolean", i -> i.booleanValueProperty()));
        tableView.getColumns().add(createColumn("Double", i -> i.doubleValueProperty()));
        tableView.getColumns().add(createColumn("Integer", i -> i.integerValueProperty()));
        tableView.getColumns().add(createColumn("Long", i -> i.longValueProperty()));
        tableView.getColumns().add(createColumn("Float", i -> i.floatValueProperty()));
        tableView.getColumns().add(createColumn("Date", i -> i.dateValueProperty()));
        tableView.getColumns().add(createColumn("Calender", i -> i.calendarValueProperty()));
        tableView.getColumns().add(createColumn("UUID", i -> i.uuidValueProperty()));
        tableView.getColumns().add(createColumn("Enum", i -> i.enumValueProperty()));
    }

    @Override
    protected void init() {
        FXBinder.bind(tableView.getItems()).to(getModel().getItems());
    }

    @Override
    public Node getRootNode() {
        return tableView;
    }

    private <T> TableColumn<Item, T> createColumn(String name, Function<Item, Property<T>> propertyFunction) {
        TableColumn<Item, T> column = new TableColumn<>(name);
        column.setCellValueFactory(e -> FXWrapper.wrapObjectProperty(propertyFunction.apply(e.getValue())));
        return column;
    }
}
