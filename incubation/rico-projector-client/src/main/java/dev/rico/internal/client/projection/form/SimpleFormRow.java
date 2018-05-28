/*
 * Copyright 2018 Karakun AG.
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

import dev.rico.internal.client.projection.projection.Utils;
import dev.rico.internal.projection.form.FormField;
import dev.rico.client.remoting.BidirectionalConverter;
import dev.rico.client.remoting.Converter;
import dev.rico.client.remoting.FXBinder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Callback;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public class SimpleFormRow extends AbstractFormLayoutRegion {

    private FormField formFieldBean;

    private Control titleLabel;

    private Control editor;

    private DoubleProperty spacing;

    private Callback<FormField, Control> labelFactory;

    private Callback<FormField, Control> editorFactory;

    public SimpleFormRow(FormField formFieldBean) {
        this.formFieldBean = formFieldBean;

        labelFactory = bean -> createLabel(bean);
        editorFactory = bean -> createEditor(bean);

        this.titleLabel = labelFactory.call(formFieldBean);
        this.editor = editorFactory.call(formFieldBean);
        this.spacing = new SimpleDoubleProperty(8);
        getChildren().addAll(titleLabel, editor);

        getStyleClass().add("simple-form-row");
    }

    public void updateSizes() {
        internMaxEditorWidthProperty().setValue(editor.maxWidth(getHeight()));
        internMinEditorWidthProperty().setValue(editor.minWidth(getHeight()));
        internPrefEditorWidthProperty().setValue(editor.prefWidth(getHeight()));

        internMaxTitleWidthProperty().setValue(titleLabel.maxWidth(getHeight()));
        internMinTitleWidthProperty().setValue(titleLabel.minWidth(getHeight()));
        internPrefTitleWidthProperty().setValue(titleLabel.prefWidth(getHeight()));
    }

    @Override
    protected double computePrefHeight(double width) {
        return getPadding().getTop() + Math.max(titleLabel.prefHeight(width), editor.prefHeight(width)) + getPadding().getBottom();
    }

    @Override
    protected double computeMinHeight(double width) {
        return getPadding().getTop() + Math.max(titleLabel.minHeight(width), editor.minHeight(width)) + getPadding().getBottom();
    }

    @Override
    protected double computeMaxHeight(double width) {
        return getPadding().getTop() + Math.min(titleLabel.maxHeight(width), editor.maxHeight(width)) + getPadding().getBottom();
    }

    @Override
    protected double computePrefWidth(double height) {
        return getPadding().getLeft() + titleLabel.prefWidth(height) + spacing.get() + editor.prefWidth(height) + getPadding().getRight();
    }

    @Override
    protected double computeMinWidth(double height) {
        return getPadding().getLeft() + titleLabel.prefWidth(height) + spacing.get() + editor.minWidth(height) + getPadding().getRight();
    }

    @Override
    protected double computeMaxWidth(double height) {
        return getPadding().getLeft() + titleLabel.prefWidth(height) + spacing.get() + editor.maxWidth(height) + getPadding().getRight();
    }

    @Override
    protected void layoutChildren() {
        titleLabel.relocate(getPadding().getLeft(), getPadding().getTop());
        titleLabel.resize(getTitleWidth() - getPadding().getLeft(), getHeight() - getPadding().getTop() - getPadding().getBottom());

        editor.relocate(getTitleWidth() + spacing.get(), getPadding().getTop());
        double editorWidth = getWidth() - getTitleWidth() - getPadding().getRight() - spacing.get();
        double editorHeight = editor.maxHeight(editorWidth);
        editor.resize(editorWidth, Math.min(editorHeight, getHeight() - getPadding().getTop() - getPadding().getBottom()));
    }

    protected Control createEditor(FormField bean) {
        try {
            Control editor = null;
            if (bean.getContentType().equals(String.class)) {
              //  if (((StringFormFieldBean) bean).isMultiline()) {
                //      editor = new TextArea();
                //     ((TextArea) editor).setWrapText(true);
                //  } else {
                      editor = new TextField();
                //  }
                FXBinder.bind(((TextInputControl) editor).textProperty()).bidirectionalTo(bean.valueProperty());

            } else if (bean.getContentType().equals(Boolean.class)) {
                editor = new CheckBox();
                FXBinder.bind(((CheckBox) editor).textProperty()).bidirectionalTo(bean.titleProperty());
                FXBinder.bind(((CheckBox) editor).selectedProperty()).bidirectionalTo(bean.valueProperty());
            } else if (bean.getContentType().equals(Date.class)) {
                editor = new DatePicker();
                FXBinder.bind(((DatePicker) editor).valueProperty()).bidirectionalTo(bean.valueProperty(), new BidirectionalConverter<Date, LocalDate>() {

                    @Override
                    public LocalDate convert(Date value) {
                        return LocalDate.from(value.toInstant());
                    }

                    @Override
                    public Date convertBack(LocalDate value) {
                        return Date.from(Instant.from(value));
                    }
                });
            }
            FXBinder.bind(editor.disableProperty()).to(bean.disabledProperty());


            Utils.registerTooltip(editor, bean);
            editor.setMaxWidth(Double.MAX_VALUE);
            editor.getStyleClass().add("simple-form-editor");
            return editor;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected Label createLabel(FormField bean) {
        Label label = new Label();
        FXBinder.bind(label.textProperty()).to(bean.titleProperty(), new Converter<String, String>() {
            @Override
            public String convert(String value) {
                if (value == null || value.isEmpty()) {
                    return "";
                } else {
                    return value + ":";
                }
            }
        });
        label.setAlignment(Pos.BASELINE_RIGHT);
        label.setMaxHeight(Double.MAX_VALUE);


      //  if (bean instanceof StringFormFieldBean && ((StringFormFieldBean) bean).isMultiline()) {
      //      label.setAlignment(Pos.TOP_RIGHT);
      //  }
        if (bean.getContentType().equals(Boolean.class)) {
            label.setVisible(false);
        } else {
            FXBinder.bind(label.visibleProperty()).to(bean.titleProperty(), new Converter<String, Boolean>() {
                @Override
                public Boolean convert(String value) {
                    if (value == null || value.isEmpty()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
        }

        Utils.registerTooltip(label, bean);

        label.getStyleClass().add("simple-form-label");
        return label;
    }
}
