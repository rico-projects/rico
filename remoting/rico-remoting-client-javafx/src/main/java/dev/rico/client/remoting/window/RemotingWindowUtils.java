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
package dev.rico.client.remoting.window;

import dev.rico.client.remoting.view.AbstractViewController;
import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Class that provides some helper methods.
 */
@API(since = "0.x", status = MAINTAINED)
public class RemotingWindowUtils {
	
	/**
	 * A JavaFX {@link Stage} that contains the view of a {@link AbstractViewController} and will automatically call
	 * {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 * @param <M> type of the model
	 */
	private static class RemotingStage<M> extends Stage {

	    /**
	     * Constructor
	     * @param viewBinder the viewBinder
	     */
	    public RemotingStage(final AbstractViewController<M> viewBinder) {
	        Assert.requireNonNull(viewBinder, "viewBinder");
	        RemotingWindowUtils.destroyOnClose(this, viewBinder);
	        setScene(new Scene(viewBinder.getParent()));
	    }
	}
	
	/**
	 * A JavaFX {@link Window} that contains the view of a {@link AbstractViewController} and will automatically call
	 * {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 *
	 * @param <M> type of the model
	 */
	 private static class RemotingWindow<M> extends Window {

	    /**
	     * Constructor
	     * @param viewBinder the viewBinder
	     */
	    public RemotingWindow(final AbstractViewController<M> viewBinder) {
	        Assert.requireNonNull(viewBinder, "viewBinder");
	        RemotingWindowUtils.destroyOnClose(this, viewBinder);
	        setScene(new Scene(viewBinder.getParent()));
	    }
	    
	   
	}

    /**
     * The method will register an event handler to the window that will automatically call the {@link AbstractViewController#destroy()}
     * method when the windows becomes hidden.
     * @param window the window
     * @param viewBinder the view binder
     * @param <M> the model type
     * @return a subscription to unsubsribe / deregister the handler.
     */
    public static <M> Subscription destroyOnClose(final Window window, final AbstractViewController<M> viewBinder) {
        Assert.requireNonNull(window, "window");
        Assert.requireNonNull(viewBinder, "viewBinder");
        final EventHandler<WindowEvent> handler = e -> viewBinder.destroy();
        window.addEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
        return () -> window.removeEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
    }

	/**
	 * The method will create a JavaFX {@link Window} that contains the view of a {@link AbstractViewController} and will automatically call
	 * {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 *
	 * @param <M> type of the model
	 */
    public static final <M> Window createWindow(final AbstractViewController<M> viewBinder) {
       Assert.requireNonNull(viewBinder, "viewBinder");
	   return new RemotingWindow<M>(viewBinder);
    }
    
	/**
	 * The method will create a JavaFX {@link Stage} that contains the view of a {@link AbstractViewController} and will automatically
	 * call {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 * @param <M> type of the model
	 */
    public static final <M> Stage createStage(final AbstractViewController<M> viewBinder) {
    	Assert.requireNonNull(viewBinder, "viewBinder");
    	return new RemotingStage<M>(viewBinder);
     }

}
