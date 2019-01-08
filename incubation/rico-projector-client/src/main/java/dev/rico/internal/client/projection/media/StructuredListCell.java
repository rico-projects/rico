/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.client.projection.media;

import dev.rico.internal.client.projection.css.CssHelper;
import dev.rico.internal.client.projection.css.DefaultPropertyBasedCssMetaData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class StructuredListCell<T> extends ListCell<T> {

    private ObjectProperty<Node> leftContent;

    private ObjectProperty<Node> centerContent;

    private ObjectProperty<Node> rightContent;

    public StructuredListCell() {
        getStyleClass().add("structured-list-cell");

        leftContent = new SimpleObjectProperty<>();
        centerContent = new SimpleObjectProperty<>();
        rightContent = new SimpleObjectProperty<>();

        addEventHandler(MouseEvent.MOUSE_CLICKED, e -> simpleSelect());
    }

    public Node getLeftContent() {
        return leftContent.get();
    }

    public void setLeftContent(Node leftContent) {
        this.leftContent.set(leftContent);
    }

    public ObjectProperty<Node> leftContentProperty() {
        return leftContent;
    }

    public Node getCenterContent() {
        return centerContent.get();
    }

    public void setCenterContent(Node centerContent) {
        this.centerContent.set(centerContent);
    }

    public ObjectProperty<Node> centerContentProperty() {
        return centerContent;
    }

    public Node getRightContent() {
        return rightContent.get();
    }

    public void setRightContent(Node rightContent) {
        this.rightContent.set(rightContent);
    }

    public ObjectProperty<Node> rightContentProperty() {
        return rightContent;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new StructuredListCellSkin(this);
    }

    private void simpleSelect() {
        ListView lv = getListView();
        int index = getIndex();
        MultipleSelectionModel sm = lv.getSelectionModel();
        lv.getSelectionModel().clearAndSelect(index);
    }

    private static class StyleableProperties {
        private static final DefaultPropertyBasedCssMetaData<StructuredListCell, Number> SPACING = CssHelper.createMetaData("-fx-spacing", StyleConverter.getSizeConverter(), "spacing", 0);
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES = CssHelper.createCssMetaDataList(StructuredListCell.getClassCssMetaData(), SPACING);
    }

}
