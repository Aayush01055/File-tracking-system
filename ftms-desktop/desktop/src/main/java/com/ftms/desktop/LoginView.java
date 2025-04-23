package com.ftms.desktop;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane; // Use StackPane for background effect
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color; // For background color (optional)
import javafx.scene.shape.Rectangle; // For background shape (optional)
import okhttp3.*;
import java.io.IOException;
import java.net.URL;

public class LoginView {
    private static String userId = null;
    private static String userRole = null;

    private final VBox view;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson;
    private final MainApp mainApp;
    private final Label resultLabel; // Make resultLabel a field for easier access

    public LoginView(MainApp mainApp) {
        this.mainApp = mainApp;
        gson = GsonConfig.getGson();

        // --- UI Components ---
        Label header = new Label("FTMS Secure Login"); // More specific title
        header.getStyleClass().add("header");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username:");
        // Consider adding graphic placeholder comment: /* Icon: user */
        usernameLabel.getStyleClass().add("label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("text-field");

        Label passwordLabel = new Label("Password:");
        // Consider adding graphic placeholder comment: /* Icon: lock */
        passwordLabel.getStyleClass().add("label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("text-field");

        Button loginButton = new Button("Sign In"); // Changed button text
        // Consider adding graphic placeholder comment: /* Icon: sign-in-alt */
        loginButton.getStyleClass().add("button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        resultLabel = new Label(); // Initialize the field
        resultLabel.getStyleClass().add("label");
        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(Double.MAX_VALUE);
        resultLabel.setAlignment(Pos.CENTER);
        resultLabel.setMinHeight(30); // Ensure space for messages


        // --- Layout using GridPane ---
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(15); // Increased gap
        formGrid.setVgap(12); // Increased gap
        formGrid.setPadding(new Insets(30)); // Increased padding
        // Optional background for the grid
        // formGrid.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");


        // Row 0: Username
        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        GridPane.setHgrow(usernameField, Priority.ALWAYS);

        // Row 1: Password
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);

        // Row 2: Login Button (Span 2 columns)
        formGrid.add(loginButton, 0, 2, 2, 1);
        GridPane.setValignment(loginButton, VPos.CENTER);
        GridPane.setMargin(loginButton, new Insets(10, 0, 0, 0)); // Add top margin to button

        // Row 3: Result Label (Span 2 columns)
        formGrid.add(resultLabel, 0, 3, 2, 1);
        GridPane.setMargin(resultLabel, new Insets(10, 0, 0, 0)); // Add top margin


        // --- Main VBox Setup ---
        view = new VBox(25); // Increased spacing between header and form
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(30)); // Increased overall padding
        view.getChildren().addAll(header, formGrid);
        // Apply root background if needed from CSS
        view.getStyleClass().add("root");

        // Load CSS
        loadStyles();


        // --- Action Listener ---
        loginButton.setOnAction(e -> {
            String inputUsername = usernameField.getText().trim();
            String inputPassword = passwordField.getText();
            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                setResultMessage("Username and password are required.", true);
                // No need to update state/navbar here, as nothing changed
                return;
            }

            setResultMessage("Signing in...", false); // Use non-error style for progress

            new Thread(() -> { // Perform network op off UI thread
                try {
                    RequestBody body = RequestBody.create(
                            gson.toJson(new User(null, inputUsername, inputPassword, null)),
                            MediaType.parse("application/json")
                    );
                    Request request = new Request.Builder()
                            .url("http://localhost:8080/api/auth/login")
                            .post(body)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        final String responseBody = response.body() != null ? response.body().string() : null; // Read body once

                        if (response.isSuccessful() && responseBody != null) {
                            User user = gson.fromJson(responseBody, User.class);
                            // Update UI on the JavaFX Application Thread
                            Platform.runLater(() -> {
                                setLoginState(user.getUserId(), user.getRole());
                                setResultMessage("Login successful! Welcome.", false);
                                System.out.println("Login successful: userId=" + userId + ", role=" + userRole);
                                mainApp.updateNavBar(); // Update nav bar based on new state
                                mainApp.showDefaultViewForRole(); // Navigate to appropriate view
                            });
                        } else {
                            // Construct error message carefully
                            final String errorMsg = "Login failed: " + response.code() + " " + response.message() +
                                    (responseBody != null && !responseBody.isEmpty() ? " (" + responseBody + ")" : " (Check credentials or server status)");
                            Platform.runLater(() -> {
                                setLoginState(null, null); // Ensure state is null on failure
                                setResultMessage(errorMsg, true);
                                System.err.println(errorMsg);
                                mainApp.updateNavBar(); // Reflect failed login in navbar
                            });
                        }
                    } // Response is closed here
                } catch (IOException | com.google.gson.JsonSyntaxException ex) { // Catch network and parse errors
                    final String errorMsg = "Error during login: " + ex.getClass().getSimpleName() + " - " + ex.getMessage();
                    Platform.runLater(() -> {
                        setLoginState(null, null); // Ensure state is null on error
                        setResultMessage(errorMsg, true);
                        System.err.println("Login error: " + ex.getMessage());
                        mainApp.updateNavBar(); // Reflect failed login in navbar
                    });
                }
            }).start(); // Start the background thread
        });
    }

    private void loadStyles() {
        URL cssUrl = getClass().getResource("/css/styles.css");
        if (cssUrl != null) {
            view.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("LoginView CSS loaded.");
        } else {
            System.err.println("Warning: LoginView styles.css not found.");
        }
    }

    // Updated helper to set result message and style using the field
    private void setResultMessage(String message, boolean isError) {
        Platform.runLater(() -> { // Ensure UI update is on correct thread
            resultLabel.setText(message);
            resultLabel.getStyleClass().removeAll("success-message", "error-message"); // Clear previous
            if (isError) {
                resultLabel.getStyleClass().add("error-message");
            } else {
                resultLabel.getStyleClass().add("success-message");
            }
        });
    }


    static void setLoginState(String id, String role) {
        userId = id;
        userRole = role;
    }

    public VBox getView() {
        return view;
    }

    public static String getUserId() { return userId; }
    public static String getUserRole() { return userRole; }
}