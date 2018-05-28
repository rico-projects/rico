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
package dev.rico.internal.projection.media;

import dev.rico.internal.projection.base.WithActions;
import dev.rico.internal.projection.base.WithDescription;
import dev.rico.internal.projection.base.WithLayoutMetadata;
import dev.rico.internal.projection.base.WithTitle;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public interface Media extends WithTitle, WithDescription, WithActions, WithLayoutMetadata {

    Property<String> imageUrlProperty();

    default String getImageUrl() {
        return imageUrlProperty().get();
    }

    default void setImageUrl(String imageUrl) {
        imageUrlProperty().set(imageUrl);
    }
}
