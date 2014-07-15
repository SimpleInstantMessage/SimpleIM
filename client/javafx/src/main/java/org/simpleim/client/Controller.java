package org.simpleim.client;

import javafx.application.Platform;


public class Controller {

    protected MainApp mainApp;

    protected static void runInJavaFXApplicationThread(Runnable run) {
        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
