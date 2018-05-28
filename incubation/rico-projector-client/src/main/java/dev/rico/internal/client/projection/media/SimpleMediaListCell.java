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

import dev.rico.internal.projection.media.Media;
import dev.rico.core.functional.Binding;
import dev.rico.core.functional.Subscription;
import dev.rico.client.remoting.FXBinder;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.util.Callback;

public class SimpleMediaListCell<T extends Media> extends MediaListCell<T> {

    private RoundImageView imageView;

    private Binding titleBinding;

    private Binding descriptionBinding;

    private Subscription imageSubscription;

    public SimpleMediaListCell() {
        imageView = new RoundImageView();
        setLeftContent(imageView);
        getStyleClass().add("simple-media-cell");
        itemProperty().addListener(e -> {

            if (titleBinding != null) {
                titleBinding.unbind();
            }
            if (descriptionBinding != null) {
                descriptionBinding.unbind();
            }
            if (imageSubscription != null) {
                imageSubscription.unsubscribe();
            }
            setTitle(null);
            setDescription(null);
            imageView.setImage(null);

            if (getItem() != null) {
                titleBinding = FXBinder.bind(titleProperty()).to(getItem().titleProperty());
                descriptionBinding = FXBinder.bind(descriptionProperty()).to(getItem().descriptionProperty());
                imageSubscription = getItem().imageUrlProperty().onChanged(ev -> imageView.setImage(new Image(getItem().getImageUrl())));
                imageView.setImage(new Image(getItem().getImageUrl()));
            } else {
                //TODO: Not loaded

            }
        });
    }

    public static <T extends Media> Callback<ListView<T>, ListCell<T>> createDefaultCallback() {
        return v -> new SimpleMediaListCell<>();
    }
}
