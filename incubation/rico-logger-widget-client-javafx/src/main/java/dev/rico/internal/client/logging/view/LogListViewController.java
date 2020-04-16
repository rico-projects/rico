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
package dev.rico.internal.client.logging.view;

import dev.rico.client.Client;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.internal.client.logging.widgets.LogListCell;
import dev.rico.internal.core.Assert;
import dev.rico.internal.logging.LogEntryBean;
import dev.rico.internal.logging.LogListBean;
import dev.rico.internal.logging.spi.LogMessage;
import dev.rico.remoting.client.ClientContext;
import dev.rico.remoting.client.javafx.FXBinder;
import dev.rico.remoting.client.javafx.view.AbstractViewController;
import javafx.scene.Node;
import javafx.scene.control.ListView;

import static dev.rico.internal.logging.LoggerRemotingConstants.LOG_LIST_CONTROLLER_NAME;
import static dev.rico.internal.logging.LoggerRemotingConstants.UPDATE_ACTION;

public class LogListViewController extends AbstractViewController<LogListBean> {

    private final ListView<LogMessage> listView;

    public LogListViewController(final ClientContext clientContext) {
        super(clientContext, LOG_LIST_CONTROLLER_NAME);
        listView = new ListView<>();
        listView.setCellFactory(v -> new LogListCell());
    }

    @Override
    protected void init() {
        FXBinder.bind(listView.getItems()).to(getModel().getEntries(), b -> convertBean(b));
        invoke(UPDATE_ACTION);
        final BackgroundExecutor backgroundExecutor = Client.getService(BackgroundExecutor.class);
        backgroundExecutor.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(2_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Client.getService(UiExecutor.class).execute(() -> invoke(UPDATE_ACTION));
            }
        });
    }

    private LogMessage convertBean(final LogEntryBean bean) {
        Assert.requireNonNull(bean, "bean");
        final LogMessage message = new LogMessage();
        message.setMessage(bean.getMessage());
        message.setLevel(bean.getLogLevel());
        message.setTimestamp(bean.getLogTimestamp());
        return message;
    }

    @Override
    public Node getRootNode() {
        return listView;
    }
}
