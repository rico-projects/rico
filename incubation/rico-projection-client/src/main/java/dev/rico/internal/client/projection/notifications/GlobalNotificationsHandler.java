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
package dev.rico.internal.client.projection.notifications;

import dev.rico.internal.projection.message.MessageType;
import dev.rico.internal.projection.notifications.NotificationBean;
import dev.rico.internal.projection.notifications.NotificationData;
import dev.rico.internal.projection.notifications.NotificationWrapperBean;
import dev.rico.remoting.client.ClientContext;
import dev.rico.remoting.client.ControllerProxy;
import org.controlsfx.control.Notifications;

import java.util.concurrent.CompletableFuture;

import static dev.rico.internal.projection.notifications.NotificationConstants.GLOBAL_NOTIFICATIONS_CONTROLLER;

public class GlobalNotificationsHandler {

    private final ClientContext clientContext;

    public GlobalNotificationsHandler(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public CompletableFuture<Void> init() {
        CompletableFuture<ControllerProxy<NotificationWrapperBean>> controllerFuture = clientContext.createController(GLOBAL_NOTIFICATIONS_CONTROLLER);
        return controllerFuture.handle((c, e) -> {
            if (e != null) {
                //TODO
            }
            NotificationWrapperBean model = c.getModel();
            model.notificationProperty().onChanged(event -> {
                NotificationBean bean = model.getNotification();
                if (bean != null) {
                    showNotification(new NotificationData(bean.getTitle(), bean.getDescription(), bean.getMessageType()));
                }
            });

            NotificationBean bean = model.getNotification();
            if (bean != null) {
                showNotification(new NotificationData(bean.getTitle(), bean.getDescription(), bean.getMessageType()));
            }
            return null;
        });
    }

    protected void showNotification(NotificationData notification) {
        Notifications notificationPopup = Notifications.create().title(notification.getTitle()).text(notification.getText());
        if (MessageType.SUCCESS.equals(notification.getMessageType())) {
            notificationPopup.showConfirm();
        } else if (MessageType.INFO.equals(notification.getMessageType())) {
            notificationPopup.showInformation();
        } else if (MessageType.WARNING.equals(notification.getMessageType())) {
            notificationPopup.showWarning();
        } else if (MessageType.ERROR.equals(notification.getMessageType())) {
            notificationPopup.showError();
        } else {
            notificationPopup.show();
        }
    }

    public static CompletableFuture<Void> create(ClientContext clientContext) {
        return new GlobalNotificationsHandler(clientContext).init();
    }
}
