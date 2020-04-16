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
package dev.rico.internal.client.projection.form;

import dev.rico.internal.projection.form.FormField;
import dev.rico.internal.projection.form.FormSection;
import dev.rico.remoting.client.javafx.FXWrapper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class SimpleFormSection extends AbstractFormLayoutRegion {

    private FormSection formSectionBean;

    private DoubleProperty spacing;

    private Label header;

    private Label descriptionLabel;

    private Callback<FormField, SimpleFormRow> rowFactory;

    public SimpleFormSection(FormSection formSectionBean) {
        this.formSectionBean = formSectionBean;
        spacing = new SimpleDoubleProperty(8);

        header = new Label();
        header.getStyleClass().add("simple-form-section-title");
        header.textProperty().bind(FXWrapper.wrapStringProperty(formSectionBean.titleProperty()));
        header.visibleProperty().bind(FXWrapper.wrapStringProperty(formSectionBean.titleProperty()).isNotEmpty());

        descriptionLabel = new Label();
        descriptionLabel.getStyleClass().add("simple-form-section-description");
        descriptionLabel.textProperty().bind(FXWrapper.wrapStringProperty(formSectionBean.descriptionProperty()));
        descriptionLabel.visibleProperty().bind(FXWrapper.wrapStringProperty(formSectionBean.descriptionProperty()).isNotEmpty());

        getChildren().addAll(header, descriptionLabel);

        rowFactory = formFieldBean -> new SimpleFormRow(formFieldBean);

        formSectionBean.getFields().onChanged(e -> {
            getChildren().clear();
            getChildren().addAll(header, descriptionLabel);
            for (FormField formFieldBean : formSectionBean.getFields()) {
                getChildren().add(rowFactory.call(formFieldBean));
            }
            requestParentLayout();
        });
        for (FormField formFieldBean : formSectionBean.getFields()) {
            getChildren().add(rowFactory.call(formFieldBean));
        }

        titleWidthProperty().addListener(e -> {
            for (SimpleFormRow row : getRows()) {
                row.setTitleWidth(getTitleWidth());
            }
        });
        getStyleClass().add("simple-form-section");
    }

    public FormSection getFormSectionBean() {
        return formSectionBean;
    }

    @Override
    protected double computePrefWidth(double height) {
        updateSizes();
        double prefWidth = getPadding().getLeft() + getPadding().getRight();
        for (SimpleFormRow row : getRows()) {
            prefWidth = Math.max(prefWidth, getPadding().getLeft() + row.prefWidth(-1) + getPadding().getRight());
        }
        return prefWidth;
    }

    @Override
    protected double computeMaxWidth(double height) {
        updateSizes();
        double maxWidth = Double.MAX_VALUE;
        for (SimpleFormRow row : getRows()) {
            maxWidth = Math.min(maxWidth, getPadding().getLeft() + row.maxWidth(-1) + getPadding().getRight());
        }
        return maxWidth;
    }

    @Override
    protected double computeMinWidth(double height) {
        updateSizes();
        double minWidth = getPadding().getLeft() + getPadding().getRight();
        for (SimpleFormRow row : getRows()) {
            minWidth = Math.max(minWidth, getPadding().getLeft() + row.minWidth(-1) + getPadding().getRight());
        }
        return minWidth;
    }

    @Override
    protected double computePrefHeight(double width) {
        updateSizes();
        double prefHeight = getPadding().getTop();
        if (header.isVisible()) {
            prefHeight = prefHeight + header.prefHeight(width);
        }
        if (descriptionLabel.isVisible()) {
            prefHeight = prefHeight + descriptionLabel.prefHeight(width);
        }

        for (SimpleFormRow row : getRows()) {
            prefHeight = prefHeight + row.prefHeight(width);
        }
        prefHeight = prefHeight + Math.max(0, (getRows().size() - 1) * spacing.get());
        prefHeight = prefHeight + getPadding().getBottom();
        return prefHeight;
    }

    @Override
    protected double computeMaxHeight(double width) {
        updateSizes();
        double maxHeight = getPadding().getTop();
        if (header.isVisible()) {
            maxHeight = maxHeight + header.maxHeight(width);
        }
        if (descriptionLabel.isVisible()) {
            maxHeight = maxHeight + descriptionLabel.maxHeight(width);
        }

        for (SimpleFormRow row : getRows()) {
            maxHeight = maxHeight + row.maxHeight(width);
        }
        maxHeight = maxHeight + Math.max(0, (getRows().size() - 1) * spacing.get());

        maxHeight = maxHeight + getPadding().getBottom();
        return maxHeight;
    }

    @Override
    protected double computeMinHeight(double width) {
        updateSizes();
        double minHeight = getPadding().getTop();
        if (header.isVisible()) {
            minHeight = minHeight + header.minHeight(width);
        }
        if (descriptionLabel.isVisible()) {
            minHeight = minHeight + descriptionLabel.minHeight(width);
        }

        for (SimpleFormRow row : getRows()) {
            minHeight = minHeight + row.minHeight(width);
        }
        minHeight = minHeight + Math.max(0, (getRows().size() - 1) * spacing.get());

        minHeight = minHeight + getPadding().getBottom();
        return minHeight;
    }

    protected void updateSizes() {
        double editorMaxWidth = Double.MAX_VALUE;
        double editorMinWidth = 0;
        double editorPrefWidth = 0;
        double labelMaxWidth = Double.MAX_VALUE;
        double labelMinWidth = 0;
        double labelPrefWidth = 0;
        for (SimpleFormRow row : getRows()) {
            row.updateSizes();
            editorMaxWidth = Math.min(editorMaxWidth, row.getMaxEditorWidth());
            editorMinWidth = Math.max(editorMinWidth, row.getMinEditorWidth());
            editorPrefWidth = Math.max(editorPrefWidth, row.getPrefEditorWidth());
            labelMaxWidth = Math.min(labelMaxWidth, row.getMaxTitleWidth());
            labelMinWidth = Math.max(labelMinWidth, row.getMinTitleWidth());
            labelPrefWidth = Math.max(labelPrefWidth, row.getPrefTitleWidth());
        }
        internMaxEditorWidthProperty().setValue(editorMaxWidth);
        internMinEditorWidthProperty().setValue(editorMinWidth);
        internPrefEditorWidthProperty().setValue(editorPrefWidth);
        internMaxTitleWidthProperty().setValue(labelMaxWidth);
        internMinTitleWidthProperty().setValue(labelMinWidth);
        internPrefTitleWidthProperty().setValue(labelPrefWidth);
    }

    protected List<SimpleFormRow> getRows() {
        List<SimpleFormRow> rows = new ArrayList<>();
        for (Node child : getChildren()) {
            if (child instanceof SimpleFormRow) {
                rows.add((SimpleFormRow) child);
            }
        }
        return rows;
    }

    @Override
    protected void layoutChildren() {
        double startY = getPadding().getTop();

        if (header.isVisible()) {
            double headerHeight = header.prefHeight(getWidth());
            header.relocate(getPadding().getLeft(), startY);
            header.resize(getWidth(), headerHeight);
            startY = startY + headerHeight;
        }
        if (descriptionLabel.isVisible()) {
            double descHeight = descriptionLabel.prefHeight(getWidth());
            descriptionLabel.relocate(getPadding().getLeft(), startY);
            descriptionLabel.resize(getWidth(), descHeight);
            startY = startY + descHeight;
        }

        for (SimpleFormRow row : getRows()) {
            row.setTitleWidth(getTitleWidth());
            row.relocate(getPadding().getLeft(), startY);
            double height = row.prefHeight(getWidth() - getPadding().getLeft() - getPadding().getRight());
            row.resize(getWidth() - getPadding().getLeft() - getPadding().getRight(), height);
            startY = startY + height + spacing.get();
        }
    }
}
