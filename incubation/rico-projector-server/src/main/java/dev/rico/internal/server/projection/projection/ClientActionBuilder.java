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
package dev.rico.internal.server.projection.projection;

import dev.rico.internal.projection.action.ClientAction;
import dev.rico.internal.projection.action.StringClientActionBean;
import dev.rico.internal.server.projection.i18n.BeanLocalization;
import dev.rico.remoting.BeanManager;

public class ClientActionBuilder<T, U extends ClientAction<T>> extends ActionBuilder<U> {


    private String actionName;

    public ClientActionBuilder(Class<U> actionClass, BeanManager beanManager, String actionName, BeanLocalization localization) {
        super(actionClass, beanManager, localization);
        this.actionName = actionName;
    }

    public ClientActionBuilder(Class<U> actionClass, BeanManager beanManager, String actionName) {
        this(actionClass, beanManager, actionName, null);
    }

    public ClientActionBuilder(Class<U> actionClass, BeanManager beanManager) {
        this(actionClass, beanManager, null, null);
    }

    public ClientActionBuilder(Class<U> actionClass, BeanManager beanManager, BeanLocalization localization) {
        this(actionClass, beanManager, null, localization);
    }

    ClientActionBuilder<T, U> withActionName(String actionName) {
        this.actionName = actionName;
        return this;
    }

    @Override
    public U build() {
        U action = super.build();
        action.setActionName(actionName);
        return action;
    }

    public static ClientActionBuilder<String, StringClientActionBean> createWithStringResult(BeanManager beanManager) {
        return new ClientActionBuilder<>(StringClientActionBean.class, beanManager);
    }

    public static ClientActionBuilder<String, StringClientActionBean> createWithStringResult(BeanManager beanManager, BeanLocalization localization) {
        return new ClientActionBuilder<>(StringClientActionBean.class, beanManager, localization);
    }

    public static ClientActionBuilder<String, StringClientActionBean> createWithStringResult(String actionName, BeanManager beanManager) {
        return new ClientActionBuilder<>(StringClientActionBean.class, beanManager).withActionName(actionName);
    }

    public static ClientActionBuilder<String, StringClientActionBean> createWithStringResult(String actionName, BeanManager beanManager, BeanLocalization localization) {
        return new ClientActionBuilder<>(StringClientActionBean.class, beanManager, localization).withActionName(actionName);
    }
}
