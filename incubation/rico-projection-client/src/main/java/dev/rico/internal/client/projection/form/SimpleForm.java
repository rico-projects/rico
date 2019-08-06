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

import dev.rico.internal.client.projection.action.SimpleActionBar;
import dev.rico.internal.client.projection.projection.Projector;
import dev.rico.internal.projection.form.Form;
import dev.rico.internal.projection.form.FormSection;
import dev.rico.core.functional.Subscription;
import dev.rico.client.remoting.ControllerProxy;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class SimpleForm extends Region {

    private Label titleLabel;

    private Label descriptionLabel;

    private ObjectProperty<Form> formBean;

    private DoubleProperty spacing;

    private Callback<FormSection, SimpleFormSection> sectionFactory;

    private Subscription updateSectionSubscription;

    private SimpleActionBar actionBar;

    private final ControllerProxy controllerProxy;

    private final Projector projector;

    public SimpleForm(ControllerProxy controllerProxy, Form formBean, Projector projector) {
        this(controllerProxy, projector);
        this.formBean.setValue(formBean);
    }

    public SimpleForm(ControllerProxy controllerProxy, Projector projector) {
        this.controllerProxy = controllerProxy;
        this.projector = projector;
        formBean = new SimpleObjectProperty<>();
        this.formBean.addListener(e -> {
            if(updateSectionSubscription != null) {
                updateSectionSubscription.unsubscribe();
            }
            getChildren().clear();
            Form bean = this.formBean.get();
            if(bean != null) {
                updateSectionSubscription = bean.getSections().onChanged(e2 -> {
                    getChildren().clear();
                    for (FormSection formSectionBean : bean.getSections()) {
                        getChildren().add(sectionFactory.call(formSectionBean));
                    }
                    if(actionBar != null) {
                        getChildren().add(actionBar);
                    }
                    requestParentLayout();
                });
                for (FormSection formSectionBean : bean.getSections()) {
                    getChildren().add(sectionFactory.call(formSectionBean));
                }
                actionBar = new SimpleActionBar(controllerProxy, bean.getActions(), projector);
                actionBar.getStyleClass().add("simple-form-action-bar");
                actionBar.parentProperty().addListener(e2 -> {
                    System.out.println("");
                });
                getChildren().add(actionBar);
                requestParentLayout();
            }
        });

        spacing = new SimpleDoubleProperty(8);

        sectionFactory = bean -> new SimpleFormSection(bean);

        getStyleClass().add("simple-form");

        setMinHeight(USE_PREF_SIZE);
    }

    protected List<SimpleFormSection> getRows() {
        List<SimpleFormSection> rows = new ArrayList<>();
        for (Node child : getChildren()) {
            if (child instanceof SimpleFormSection) {
                rows.add((SimpleFormSection) child);
            }
        }
        return rows;
    }

    @Override
    protected double computePrefWidth(double height) {
        updateSizes();
        double prefWidth = getPadding().getLeft() + getPadding().getRight();
        for (SimpleFormSection row : getRows()) {
            prefWidth = Math.max(prefWidth, getPadding().getLeft() + row.prefWidth(-1) + getPadding().getRight());
        }
        if(actionBar != null) {
            prefWidth = Math.max(prefWidth, getPadding().getLeft() + actionBar.prefWidth(-1) + getPadding().getRight());
        }
        return prefWidth;
    }

    @Override
    protected double computeMinWidth(double height) {
        updateSizes();
        double minWidth = getPadding().getLeft() + getPadding().getRight();
        for (SimpleFormSection row : getRows()) {
            minWidth = Math.max(minWidth, getPadding().getLeft() + row.minWidth(-1) + getPadding().getRight());
        }
        if(actionBar != null) {
            minWidth = Math.max(minWidth, getPadding().getLeft() + actionBar.minWidth(-1) + getPadding().getRight());
        }
        return minWidth;
    }

    @Override
    protected double computeMaxWidth(double height) {
        updateSizes();
        double maxWidth = Double.MAX_VALUE;
        for (SimpleFormSection row : getRows()) {
            maxWidth = Math.min(maxWidth, getPadding().getLeft() + row.maxWidth(-1) + getPadding().getRight());
        }
        if(actionBar != null) {
            maxWidth = Math.min(maxWidth, getPadding().getLeft() + actionBar.maxWidth(-1) + getPadding().getRight());
        }
        return maxWidth;
    }

    @Override
    protected double computePrefHeight(double width) {
        updateSizes();
        double prefHeight = getPadding().getTop() + getPadding().getBottom();
        for (SimpleFormSection row : getRows()) {
            prefHeight = prefHeight + row.prefHeight(width);
        }
        prefHeight = prefHeight + Math.max(0, (getRows().size() - 1) * spacing.get());
        if(actionBar != null) {
            prefHeight = prefHeight + spacing.get() + actionBar.prefHeight(width);
        }
        return prefHeight;
    }

    @Override
    protected double computeMinHeight(double width) {
        updateSizes();
        double minHeight = getPadding().getTop() + getPadding().getBottom();
        for (SimpleFormSection row : getRows()) {
            minHeight = minHeight + row.minHeight(width);
        }
        minHeight = minHeight + Math.max(0, (getRows().size() - 1) * spacing.get());
        if(actionBar != null) {
            minHeight = minHeight + spacing.get() + actionBar.minHeight(width);
        }
        return minHeight;
    }

    @Override
    protected double computeMaxHeight(double width) {
        updateSizes();
        double maxHeight = getPadding().getTop();
        for (SimpleFormSection row : getRows()) {
            maxHeight = maxHeight + row.maxHeight(width);
        }
        maxHeight = maxHeight + Math.max(0, (getRows().size() - 1) * spacing.get());
        if(actionBar != null) {
            maxHeight = maxHeight + spacing.get() + actionBar.maxHeight(width);
        }
        maxHeight = maxHeight + getPadding().getBottom();
        return maxHeight;
    }

    protected void updateSizes() {
        double labelWidth = 0;
        for (SimpleFormSection row : getRows()) {
            row.updateSizes();
            labelWidth = Math.max(labelWidth, row.getPrefTitleWidth());
        }

        for (SimpleFormSection row : getRows()) {
            row.setTitleWidth(labelWidth);
        }
    }

    @Override
    protected void layoutChildren() {
        updateSizes();
        double width = getWidth() - getPadding().getLeft() - getPadding().getRight();
        double startY = getPadding().getTop();

        for (SimpleFormSection row : getRows()) {
            row.setTitleWidth(row.getTitleWidth());
            row.relocate(getPadding().getLeft(), startY);
            double height = row.prefHeight(width);
            row.resize(width, height);
            startY = startY + spacing.get() + height;
        }
        if(actionBar != null) {
            double height = actionBar.prefHeight(width);
            actionBar.relocate(getPadding().getLeft(), getHeight() - getPadding().getBottom() - height);
            actionBar.resize(width, height);
        }

    }
}
