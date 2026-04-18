package org.tauasa.apps.morse;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application entry point.
 *
 * Keeping the launcher separate from Application allows the fat JAR produced
 * by maven-shade-plugin to work correctly when JavaFX is on the class-path
 * rather than the module-path (the module system no longer complains about
 * a missing JavaFX bootstrap when the real main is a plain class).
 */
public class MorseGui extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Pass HostServices so HyperlinkLabel can open the browser
        new MainWindow(primaryStage, getHostServices()).show();
    }

    /** Fat-JAR / class-path compatible launcher. */
    public static void main(String[] args) {
        launch(args);
    }
}

