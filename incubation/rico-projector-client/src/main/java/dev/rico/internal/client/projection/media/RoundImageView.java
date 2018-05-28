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
package dev.rico.internal.client.projection.media;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;

public class RoundImageView extends Control {

    private SimpleObjectProperty<Image> image;

    private SimpleDoubleProperty defaultSize;

    public RoundImageView() {
        getStyleClass().add("round-image-view");
        image = new SimpleObjectProperty<>();
        defaultSize = new SimpleDoubleProperty(64.0);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RoundImageViewSkin(this);
    }

    public SimpleDoubleProperty defaultSizeProperty() {
        return defaultSize;
    }

    public SimpleObjectProperty<Image> imageProperty() {
        return image;
    }

    public Image getImage() {
        return image.get();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public double getDefaultSize() {
        return defaultSize.get();
    }

    public void setDefaultSize(double defaultSize) {
        this.defaultSize.set(defaultSize);
    }
}
