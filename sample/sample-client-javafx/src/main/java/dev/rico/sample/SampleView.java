package dev.rico.sample;

import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.FXBinder;
import dev.rico.client.remoting.FXWrapper;
import dev.rico.client.remoting.view.AbstractViewController;
import dev.rico.remoting.Property;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

import java.util.function.Function;

public class SampleView extends AbstractViewController<TestModel> {

    private FlowPane rootPane;

    private Slider sliderA;
    private Slider sliderB;

    private TableView<Item> tableView;

    public SampleView(ClientContext clientContext) {
        super(clientContext, "TestController");

        rootPane = new FlowPane(Orientation.VERTICAL);
        rootPane.setHgap(24);
        rootPane.setVgap(24);
        sliderA = new Slider();
        sliderB = new Slider();
        sliderB.setDisable(true);
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
        rootPane.getChildren().addAll(sliderA, sliderB, tableView);
    }

    @Override
    protected void init() {
        FXBinder.bind(tableView.getItems()).to(getModel().getItems());
        FXBinder.bind(sliderA.valueProperty()).bidirectionalToNumeric(getModel().getValueA());
        FXBinder.bind(sliderB.valueProperty()).bidirectionalToNumeric(getModel().getValueB());
    }

    @Override
    public Node getRootNode() {
        return rootPane;
    }

    private <T> TableColumn<Item, T> createColumn(String name, Function<Item, Property<T>> propertyFunction) {
        TableColumn<Item, T> column = new TableColumn<>(name);
        column.setCellValueFactory(e -> FXWrapper.wrapObjectProperty(propertyFunction.apply(e.getValue())));
        return column;
    }
}
