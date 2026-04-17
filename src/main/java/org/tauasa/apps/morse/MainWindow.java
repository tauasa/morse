package org.tauasa.apps.morse;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Main application window.
 *
 * Layout:
 *   MenuBar        File > Exit  |  Help > About
 *   ┌────────────────────────────────────────────┐
 *   │  Text  (label)                             │
 *   │  ┌──────────────────────────────────────┐  │
 *   │  │  plain-text TextArea                 │  │
 *   │  └──────────────────────────────────────┘  │
 *   │  [Encode →]   ☐ Play sound                 │
 *   ├────────────────────────────────────────────┤
 *   │  Morse  (label)                            │
 *   │  ┌──────────────────────────────────────┐  │
 *   │  │  Morse-code TextArea  (monospaced)   │  │
 *   │  └──────────────────────────────────────┘  │
 *   │  [← Decode]   ☐ Play sound                 │
 *   └────────────────────────────────────────────┘
 *   Status bar
 */
public class MainWindow {

    // ── Constants ──────────────────────────────────────────────────────────────
    private static final String APP_TITLE  = "Morse Code Converter";
    private static final String CSS_PATH   = "/styles.css";

    private static final double WIN_W      = 700;
    private static final double WIN_H      = 620;
    private static final double MIN_W      = 500;
    private static final double MIN_H      = 480;
    private static final double AREA_H     = 170;

    // ── State ──────────────────────────────────────────────────────────────────
    private final Stage          stage;
    private final MorseConverter converter = new MorseConverter();
    private final MorsePlayer    player    = new MorsePlayer();

    // ── Controls (assigned during build, used in action handlers) ─────────────
    private TextArea textArea;
    private TextArea morseArea;
    private CheckBox textPlayCheck;
    private CheckBox morsePlayCheck;
    private Label    statusLabel;
    private Button   encodeBtn;
    private Button   decodeBtn;

    // ── Constructor ────────────────────────────────────────────────────────────

    public MainWindow(Stage stage) {
        this.stage = stage;
    }

    // ── Public entry point ─────────────────────────────────────────────────────

    public void show() {
        stage.setTitle(APP_TITLE);
        stage.setScene(buildScene());
        stage.setMinWidth(MIN_W);
        stage.setMinHeight(MIN_H);
        // Close audio cleanly when the window's X button is clicked
        stage.setOnCloseRequest(e -> player.close());
        stage.show();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Scene / layout construction
    // ══════════════════════════════════════════════════════════════════════════

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar());
        root.setCenter(buildCenter());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, WIN_W, WIN_H);
        loadStylesheet(scene);
        return scene;
    }

    // ── Menu bar ───────────────────────────────────────────────────────────────

    private MenuBar buildMenuBar() {
        /* File menu */
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> { player.close(); Platform.exit(); });
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(exitItem);

        /* Help menu */
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        MenuBar bar = new MenuBar(fileMenu, helpMenu);
        bar.setUseSystemMenuBar(false);   // dark bar looks the same on all OS
        return bar;
    }

    // ── Centre content ─────────────────────────────────────────────────────────

    private VBox buildCenter() {
        VBox center = new VBox(14);
        center.setPadding(new Insets(18, 18, 12, 18));
        VBox.setVgrow(center, Priority.ALWAYS);

        center.getChildren().addAll(
            buildTextPanel(),
            buildDivider(),
            buildMorsePanel()
        );
        return center;
    }

    /** Upper panel – plain text input + Encode button. */
    private VBox buildTextPanel() {
        Label heading = sectionLabel("Text");

        textArea = new TextArea();
        textArea.setPromptText("Type or paste plain text here…");
        textArea.setPrefHeight(AREA_H);
        textArea.setWrapText(true);
        textArea.getStyleClass().add("text-area-main");
        VBox.setVgrow(textArea, Priority.ALWAYS);

        encodeBtn = new Button("Encode  →");
        encodeBtn.getStyleClass().add("action-btn");
        encodeBtn.setOnAction(e -> doEncode());

        textPlayCheck = new CheckBox("Play sound");

        HBox toolbar = toolbar(encodeBtn, textPlayCheck);
        return panel(heading, textArea, toolbar);
    }

    /** Lower panel – Morse code input + Decode button. */
    private VBox buildMorsePanel() {
        Label heading = sectionLabel("Morse");

        morseArea = new TextArea();
        morseArea.setPromptText("Type or paste Morse code here  ( e.g.  ... --- ... )");
        morseArea.setPrefHeight(AREA_H);
        morseArea.setWrapText(true);
        morseArea.getStyleClass().addAll("text-area-main", "morse-area");
        VBox.setVgrow(morseArea, Priority.ALWAYS);

        decodeBtn = new Button("←  Decode");
        decodeBtn.getStyleClass().add("action-btn");
        decodeBtn.setOnAction(e -> doDecode());

        morsePlayCheck = new CheckBox("Play sound");

        HBox toolbar = toolbar(decodeBtn, morsePlayCheck);
        return panel(heading, morseArea, toolbar);
    }

    private Separator buildDivider() {
        Separator sep = new Separator();
        VBox.setMargin(sep, new Insets(2, 0, 2, 0));
        return sep;
    }

    private Label buildStatusBar() {
        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-bar");
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        return statusLabel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Actions
    // ══════════════════════════════════════════════════════════════════════════

    private void doEncode() {
        String input = textArea.getText();
        if (input == null || input.isBlank()) {
            setStatus("⚠  Enter some text to encode.", true);
            return;
        }
        try {
            String morse = converter.encode(input);
            morseArea.setText(morse);
            setStatus("Encoded successfully.", false);
            if (textPlayCheck.isSelected()) playAsync(morse);
        } catch (IllegalArgumentException ex) {
            setStatus("⚠  " + ex.getMessage(), true);
        }
    }

    private void doDecode() {
        String input = morseArea.getText();
        if (input == null || input.isBlank()) {
            setStatus("⚠  Enter some Morse code to decode.", true);
            return;
        }
        try {
            String text = converter.decode(input);
            textArea.setText(text);
            setStatus("Decoded successfully.", false);
            if (morsePlayCheck.isSelected()) playAsync(input);
        } catch (IllegalArgumentException ex) {
            setStatus("⚠  " + ex.getMessage(), true);
        }
    }

    private void playAsync(String morse) {
        if (!player.isAvailable()) {
            setStatus("⚠  Audio output unavailable on this system.", true);
            return;
        }
        setButtonsEnabled(false);
        setStatus("♪  Playing…", false);
        player.playAsync(morse, () ->
            Platform.runLater(() -> {
                setButtonsEnabled(true);
                setStatus("♪  Done.", false);
            })
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  About dialog
    // ══════════════════════════════════════════════════════════════════════════

    private void showAboutDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("About Morse");
        dialog.setResizable(false);

        Label title = new Label("Morse 2.0.0");
        title.getStyleClass().add("about-title");

        Label body = new Label("by the Grand High Poobah\nof Upper Butt Crack");
        body.getStyleClass().add("about-body");
        body.setAlignment(Pos.CENTER);

        Button ok = new Button("OK");
        ok.getStyleClass().add("action-btn");
        ok.setDefaultButton(true);
        ok.setPrefWidth(88);
        ok.setOnAction(e -> dialog.close());

        VBox layout = new VBox(12, title, body, ok);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(32, 40, 28, 40));
        layout.getStyleClass().add("about-box");

        Scene scene = new Scene(layout, 360, 200);
        loadStylesheet(scene);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helper factories & utilities
    // ══════════════════════════════════════════════════════════════════════════

    /** Stacks heading + main control + toolbar into a VBox panel. */
    private static VBox panel(Label heading, Control main, HBox toolbar) {
        VBox p = new VBox(6, heading, main, toolbar);
        VBox.setVgrow(p, Priority.ALWAYS);
        return p;
    }

    /** Left-aligned horizontal toolbar row. */
    private static HBox toolbar(Button btn, CheckBox check) {
        HBox h = new HBox(14, btn, check);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(4, 0, 0, 0));
        return h;
    }

    /** Bold section heading label ("Text" / "Morse"). */
    private static Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(null, FontWeight.BOLD, 13));
        l.getStyleClass().add("section-label");
        return l;
    }

    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        // Override only the fill colour; other styling comes from CSS
        statusLabel.setStyle(error
            ? "-fx-text-fill: #d00000;"
            : "-fx-text-fill: #3c3c43;");
    }

    private void setButtonsEnabled(boolean on) {
        encodeBtn.setDisable(!on);
        decodeBtn.setDisable(!on);
    }

    /** Loads styles.css from the JAR classpath. Falls back silently if missing. */
    private static void loadStylesheet(Scene scene) {
        var url = MainWindow.class.getResource(CSS_PATH);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
        }
    }
}
