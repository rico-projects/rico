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
package dev.rico.server.data.mapping;

import dev.rico.server.data.DataWithId;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public interface BeanMapper {

    <ID extends Serializable, B, E extends DataWithId<ID>> E toEntity(B bean, Class<E> entityClass);

    <ID extends Serializable, B, E extends DataWithId<ID>> B toBean(E entity, Class<B> beanClass);

    <ID extends Serializable, B, E extends DataWithId<ID>> B updateBean(E entity, B bean, Class<B> beanClass);

    default <ID extends Serializable, B, E extends DataWithId<ID>> List<E> toEntityList(List<B> beanList, Class<E> entityClass) {
        return beanList.stream()
                .map(b -> toEntity(b, entityClass))
                .collect(Collectors.toList());
    }

    default <ID extends Serializable, B, E extends DataWithId<ID>> List<B> toBeanyList(List<E> entityList, Class<B> beanClass) {
        return entityList.stream()
                .map(b -> toBean(b, beanClass))
                .collect(Collectors.toList());
    }


}
