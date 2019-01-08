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
package dev.rico.internal.client.projection.chat;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class ChatBubble extends Region {

    private Rectangle mainBubble;

    private Label chatText;

    private Rectangle mySpeakerSign;

    private Rectangle oppositeSpeakerSign;

    double chatInsets = 12;

    private PseudoClass mePseudoClass = PseudoClass.getPseudoClass("me");

    private PseudoClass oppositePseudoClass = PseudoClass.getPseudoClass("opposite");

    private BooleanProperty sendByMe = new SimpleBooleanProperty();

    private StringProperty message = new SimpleStringProperty();

    public ChatBubble() {

        getStyleClass().addAll("chat-bubble-wrapper");


        mainBubble = new Rectangle();
        mainBubble.getStyleClass().addAll("chat-bubble", "chat-bubble-rect");
        mainBubble.setManaged(false);

        mySpeakerSign = new Rectangle();
        mySpeakerSign.getStyleClass().add("chat-bubble");
        mySpeakerSign.setRotate(45.0);
        mySpeakerSign.setManaged(false);

        oppositeSpeakerSign = new Rectangle();
        oppositeSpeakerSign.getStyleClass().add("chat-bubble");
        oppositeSpeakerSign.setRotate(45.0);
        oppositeSpeakerSign.setManaged(false);

        chatText = new Label();
        chatText.setWrapText(true);
        chatText.getStyleClass().add("chat-text");

        getChildren().addAll(mainBubble, mySpeakerSign, oppositeSpeakerSign, chatText);

        sendByMe.addListener(e -> {
            if(isSendByMe()) {
                setChatPseudoClass(mePseudoClass, true);
                setChatPseudoClass(oppositePseudoClass, false);
            } else {
                setChatPseudoClass(mePseudoClass, false);
                setChatPseudoClass(oppositePseudoClass, true);
            }
        });
        mySpeakerSign.visibleProperty().bind(sendByMe);
        oppositeSpeakerSign.visibleProperty().bind(sendByMe.not());
        chatText.textProperty().bind(message);

        setMaxHeight(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(0);
    }

    private void setChatPseudoClass(PseudoClass cls, boolean value) {
        mainBubble.pseudoClassStateChanged(cls, value);
        mySpeakerSign.pseudoClassStateChanged(cls, value);
        oppositeSpeakerSign.pseudoClassStateChanged(cls, value);
    }

    @Override
    protected void layoutChildren() {
        mainBubble.setX(0);
        mainBubble.setY(0);
        mainBubble.setWidth(getWidth());
        mainBubble.setHeight(getHeight() - 24);

        mySpeakerSign.setX(mainBubble.getWidth() - 24 - 18);
        // - 4 to hide round corners
        mySpeakerSign.setY(mainBubble.getHeight() - 12 - 4);
        mySpeakerSign.setWidth(24);
        mySpeakerSign.setHeight(24);

        oppositeSpeakerSign.setX(18);
        // - 4 to hide round corners
        oppositeSpeakerSign.setY(mainBubble.getHeight() - 12 - 4);
        oppositeSpeakerSign.setWidth(24);
        oppositeSpeakerSign.setHeight(24);

        chatText.relocate(mainBubble.getX() + chatInsets, mainBubble.getY() + chatInsets);
        chatText.resize(mainBubble.getWidth() - chatInsets - chatInsets, mainBubble.getHeight() - chatInsets - chatInsets);
    }

    @Override
    protected double computePrefHeight(double width) {
        double prefHeight = chatText.prefHeight(width - chatInsets - chatInsets) + chatInsets + chatInsets + 24;
        return prefHeight;
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    public boolean isSendByMe() {
        return sendByMe.get();
    }

    public BooleanProperty sendByMeProperty() {
        return sendByMe;
    }

    public void setSendByMe(boolean sendByMe) {
        this.sendByMe.set(sendByMe);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}
