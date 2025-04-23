package com.ftms.desktop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; // For input layout
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AuditView extends VBox {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = GsonConfig.getGson();

    // UI Elements
    private final TextField fileIdField;
    private final Button auditButton;
    private final TextArea resultArea;
    private final Label messageLabel;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public AuditView() {
        // --- UI Components ---
        Label header = new Label("File Audit Log Viewer");
        header.getStyleClass().add("header");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        // --- Input Section ---
        Label fileIdLabel = new Label("File ID:");
        fileIdLabel.getStyleClass().add("label");
        fileIdField = new TextField();
        fileIdField.setPromptText("Enter File ID");
        fileIdField.getStyleClass().add("text-field");
        HBox.setHgrow(fileIdField, Priority.ALWAYS); // Allow field to grow

        auditButton = new Button("View Audit Log");
        // auditButton.setGraphic( /* Optional Icon */ );
        auditButton.getStyleClass().add("button");
        // Button disable logic is handled in MainApp based on role

        // Layout for input controls
        HBox inputControls = new HBox(10);
        inputControls.setAlignment(Pos.CENTER_LEFT);
        inputControls.getChildren().addAll(fileIdLabel, fileIdField, auditButton);


        // --- Results Section ---
        messageLabel = new Label("Enter the File ID for which you want to view the audit log.");
        messageLabel.getStyleClass().add("label");
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(5, 0, 5, 0)); // Add padding

        resultArea = new TextArea();
        resultArea.setPromptText("Audit log entries will be displayed here...");
        resultArea.setEditable(false);
        resultArea.setWrapText(false); // Prevent wrapping for better log format alignment
        resultArea.getStyleClass().add("text-area");
        // Consider using a monospace font for the result area via CSS for better alignment
        // .audit-log-area { -fx-font-family: "Consolas", "Monospace"; }
        resultArea.getStyleClass().add("audit-log-area"); // Add specific style class
        VBox.setVgrow(resultArea, Priority.ALWAYS);


        // --- Main VBox Setup ---
        setSpacing(15);
        setPadding(new Insets(20));
        getChildren().addAll(
                header,
                inputControls,
                new Separator(javafx.geometry.Orientation.HORIZONTAL), // Separator line
                messageLabel,
                resultArea
        );

        // Load CSS
        loadStyles();

        // --- Action Listener ---
        auditButton.setOnAction(e -> fetchAuditLog());
        fileIdField.setOnAction(e -> fetchAuditLog()); // Allow fetch on Enter key
    }

    private void loadStyles() {
        URL cssUrl = getClass().getResource("/css/styles.css");
        if (cssUrl != null) {
            getStylesheets().add(cssUrl.toExternalForm());
            // Add specific CSS for audit log area if needed
            // For example: view.getStylesheets().add(getClass().getResource("/css/audit-styles.css").toExternalForm());
            System.out.println("AuditView CSS loaded.");
        } else {
            System.err.println("Error: AuditView styles.css not found.");
        }
        // Add inline style for monospace font as fallback if CSS fails
        resultArea.setStyle("-fx-font-family: 'Monospace';");
    }

    private void fetchAuditLog() {
        String currentUserId = LoginView.getUserId();
        if (currentUserId == null || !"admin".equals(LoginView.getUserRole())) {
            setMessage("Error: Audit requires Admin privileges and login.", true);
            return;
        }

        String fileId = fileIdField.getText().trim();
        if (fileId.isEmpty()) {
            setMessage("Error: Please enter a File ID.", true);
            fileIdField.requestFocus();
            return;
        }

        setMessage("Fetching audit log for File ID: " + fileId + "...", false);
        resultArea.clear();


        new Thread(() -> { // Network on background thread
            try {
                Request request = new Request.Builder()
                        .url("http://localhost:8080/api/audit/" + fileId)
                        .get()
                        .header("User-Id", currentUserId)
                        .build();

                try (Response response = client.newCall(request).execute()) { // Use try-with-resources
                    String responseBody = response.body() != null ? response.body().string() : "(No response body)";

                    if (response.isSuccessful()) {
                        Type logListType = new TypeToken<List<AuditLog>>(){}.getType();
                        List<AuditLog> logs = gson.fromJson(responseBody, logListType);

                        Platform.runLater(() -> { // Update UI on FX thread
                            if (logs != null && !logs.isEmpty()) {
                                StringBuilder result = new StringBuilder();
                                result.append(String.format("Audit Log for File ID: %s (%d entries)\n", fileId, logs.size()));
                                result.append("===================================================\n");
                                result.append(String.format("%-20s | %-12s | %s\n", "Timestamp", "User ID", "Action"));
                                result.append("---------------------------------------------------\n");
                                for (AuditLog log : logs) {
                                    String timestampStr = log.getTimestamp() != null ? log.getTimestamp().format(TIMESTAMP_FORMATTER) : "N/A";
                                    result.append(String.format("%-20s | %-12s | %s\n",
                                            timestampStr,
                                            nullToNA(log.getUserId()),
                                            nullToNA(log.getAction())
                                    ));
                                }
                                resultArea.setText(result.toString());
                                setMessage("Audit log loaded successfully.", false);
                            } else {
                                resultArea.setText("No audit log entries found for File ID: " + fileId);
                                setMessage("No logs found for this File ID.", false); // Not an error
                            }
                        });
                    } else {
                        final String errorMsg = String.format("Failed to fetch audit log: %d %s (%s)",
                                response.code(), response.message(), responseBody);
                        Platform.runLater(() -> {
                            resultArea.setText("Error loading audit log. See message below.");
                            setMessage(errorMsg, true);
                        });
                    }
                }
            } catch (IOException | com.google.gson.JsonSyntaxException ex) {
                final String errorMsg = "Error during audit log fetch: " + ex.getClass().getSimpleName() + " - " + ex.getMessage();
                Platform.runLater(() -> {
                    resultArea.setText("An error occurred while fetching the log.");
                    setMessage(errorMsg, true);
                });
            }
        }).start();
    }

    // Helper to handle null values in display
    private String nullToNA(String input) {
        return (input == null || input.trim().isEmpty()) ? "N/A" : input;
    }

    // Helper to set message and style
    private void setMessage(String text, boolean isError) {
        Platform.runLater(() -> { // Ensure UI update is on correct thread
            messageLabel.setText(text);
            messageLabel.getStyleClass().removeAll("success-message", "error-message"); // Clear previous
            if (isError) {
                messageLabel.getStyleClass().add("error-message");
            } else {
                messageLabel.getStyleClass().add("success-message");
            }
        });
    }


    public VBox getView() {
        return this;
    }
}