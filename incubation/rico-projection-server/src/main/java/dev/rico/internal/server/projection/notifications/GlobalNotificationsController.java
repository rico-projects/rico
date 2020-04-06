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
package dev.rico.internal.server.projection.notifications;

import dev.rico.internal.projection.notifications.NotificationBean;
import dev.rico.internal.projection.notifications.NotificationWrapperBean;
import dev.rico.server.remoting.BeanManager;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.event.RemotingEventBus;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static dev.rico.internal.projection.notifications.NotificationConstants.GLOBAL_NOTIFICATIONS_CONTROLLER;
import static dev.rico.internal.server.projection.notifications.NotificationsTopics.GLOBAL_NOTIFICATION;

@RemotingController(GLOBAL_NOTIFICATIONS_CONTROLLER)
public class GlobalNotificationsController {

    @RemotingModel
    private NotificationWrapperBean model;

    @Inject
    private RemotingEventBus eventBus;

    @Inject
    private BeanManager beanManager;

    @PostConstruct
    public void init() {
        eventBus.subscribe(GLOBAL_NOTIFICATION, e -> {
            NotificationBean bean = beanManager.create(NotificationBean.class);
            bean.setTitle(e.getData().getTitle());
            bean.setDescription(e.getData().getText());
            bean.setMessageType(e.getData().getMessageType());
            model.setNotification(bean);
        });
    }

}
