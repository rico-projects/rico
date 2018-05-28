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

import dev.rico.internal.projection.action.DefaultServerActionBean;
import dev.rico.internal.projection.action.ServerAction;
import dev.rico.internal.server.projection.i18n.BeanLocalization;
import dev.rico.remoting.BeanManager;

public class ServerActionBuilder<T extends ServerAction> extends ActionBuilder<T> {

    private String actionName;

    public ServerActionBuilder(Class<T> actionClass, BeanManager beanManager, String actionName, BeanLocalization localization) {
        super(actionClass, beanManager, localization);
        this.actionName = actionName;
    }

    public ServerActionBuilder(Class<T> actionClass, BeanManager beanManager, String actionName) {
        this(actionClass, beanManager, actionName, null);
    }

    public ServerActionBuilder(Class<T> actionClass, BeanManager beanManager) {
        this(actionClass, beanManager, null, null);
    }

    public ServerActionBuilder(Class<T> actionClass, BeanManager beanManager, BeanLocalization localization) {
        this(actionClass, beanManager, null, localization);
    }

    ServerActionBuilder<T> withActionName(String actionName) {
        this.actionName = actionName;
        return this;
    }

    @Override
    public T build() {
        T action = super.build();
        action.setActionName(actionName);
        return action;
    }

    public static ServerActionBuilder<DefaultServerActionBean> create(BeanManager beanManager) {
        return new ServerActionBuilder<>(DefaultServerActionBean.class, beanManager);
    }

    public static ServerActionBuilder<DefaultServerActionBean> create(BeanManager beanManager, BeanLocalization localization) {
        return new ServerActionBuilder<>(DefaultServerActionBean.class, beanManager, localization);
    }

    public static ServerActionBuilder<DefaultServerActionBean> create(String actionName, BeanManager beanManager) {
        return new ServerActionBuilder<>(DefaultServerActionBean.class, beanManager).withActionName(actionName);
    }

    public static ServerActionBuilder<DefaultServerActionBean> create(String actionName, BeanManager beanManager, BeanLocalization localization) {
        return new ServerActionBuilder<>(DefaultServerActionBean.class, beanManager, localization).withActionName(actionName);
    }
}
