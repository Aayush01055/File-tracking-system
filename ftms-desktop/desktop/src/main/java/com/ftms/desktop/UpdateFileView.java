package com.ftms.desktop;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UpdateFileView extends VBox {
    private final TextField fileIdField;
    private final TextField titleField;
    private final ComboBox<String> statusComboBox;
    private final ComboBox<User> officerComboBox;
    private final TextField courseCodeField;
    private final TextField examSessionField;
    private final Button updateButton;
    private final Button loadDataButton;
    private final Label messageLabel;

    private String loadedFileCurrentOfficerId = null;

    private static final String NO_STATUS_CHANGE = "-- No Change --";
    private static final List<String> STATUS_OPTIONS = Arrays.asList("Draft", "In Progress", "Completed");

    // Constructor and other methods (loadStyles, configureOfficerComboBoxCellFactories, fetchOfficers, loadFileData) remain the same...
    public UpdateFileView() {
        // --- UI Component Initialization ---
        Label viewTitle = new Label("Update Existing File");
        viewTitle.getStyleClass().add("header");
        viewTitle.setMaxWidth(Double.MAX_VALUE);

        fileIdField = new TextField();
        fileIdField.setPromptText("Enter File ID to load/update (Required)");
        fileIdField.getStyleClass().add("text-field");

        loadDataButton = new Button("Load File Data");
        loadDataButton.getStyleClass().add("button");
        loadDataButton.setMaxWidth(Double.MAX_VALUE);


        titleField = new TextField();
        titleField.setPromptText("Enter new title (Optional)");
        titleField.getStyleClass().add("text-field");

        List<String> statusOptionsWithNoChange = new ArrayList<>();
        statusOptionsWithNoChange.add(NO_STATUS_CHANGE);
        statusOptionsWithNoChange.addAll(STATUS_OPTIONS);
        statusComboBox = new ComboBox<>(FXCollections.observableArrayList(statusOptionsWithNoChange));
        statusComboBox.setPromptText("Select new status (Optional)");
        statusComboBox.getStyleClass().add("combo-box");
        statusComboBox.setValue(NO_STATUS_CHANGE);
        statusComboBox.setMaxWidth(Double.MAX_VALUE);

        officerComboBox = new ComboBox<>();
        officerComboBox.setPromptText("Select new officer (Optional)");
        officerComboBox.getStyleClass().add("combo-box");
        officerComboBox.setMaxWidth(Double.MAX_VALUE);

        courseCodeField = new TextField();
        courseCodeField.setPromptText("Enter new course code (Optional)");
        courseCodeField.getStyleClass().add("text-field");

        examSessionField = new TextField();
        examSessionField.setPromptText("Enter new exam session (Optional)");
        examSessionField.getStyleClass().add("text-field");

        updateButton = new Button("Update File");
        updateButton.getStyleClass().add("button");
        updateButton.setMaxWidth(Double.MAX_VALUE);
        updateButton.setDisable(true); // Disable update button initially until file is loaded


        messageLabel = new Label("Enter a File ID and click 'Load File Data'."); // Initial message
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);

        // --- Layout using GridPane ---
        GridPane formPane = new GridPane();
        formPane.setHgap(15);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(20));

        // Row 0: File ID Input
        Label fileIdLabel = new Label("File ID:");
        fileIdLabel.getStyleClass().add("label");
        formPane.add(fileIdLabel, 0, 0);
        formPane.add(fileIdField, 1, 0);
        GridPane.setHgrow(fileIdField, Priority.ALWAYS);

        // Row 1: Load Button (Span 2 columns)
        formPane.add(loadDataButton, 0, 1, 2, 1);
        GridPane.setValignment(loadDataButton, VPos.CENTER);

        formPane.add(new Separator(), 0, 2, 2, 1);

        // Row 3: Title
        Label titleLabel = new Label("New Title:");
        titleLabel.getStyleClass().add("label");
        formPane.add(titleLabel, 0, 3);
        formPane.add(titleField, 1, 3);
        GridPane.setHgrow(titleField, Priority.ALWAYS);

        // Row 4: Status
        Label statusLabel = new Label("New Status:");
        statusLabel.getStyleClass().add("label");
        formPane.add(statusLabel, 0, 4);
        formPane.add(statusComboBox, 1, 4);

        // Row 5: Officer
        Label officerLabel = new Label("New Officer:");
        officerLabel.getStyleClass().add("label");
        formPane.add(officerLabel, 0, 5);
        formPane.add(officerComboBox, 1, 5);

        // Row 6: Course Code
        Label courseCodeLabel = new Label("New Course Code:");
        courseCodeLabel.getStyleClass().add("label");
        formPane.add(courseCodeLabel, 0, 6);
        formPane.add(courseCodeField, 1, 6);
        GridPane.setHgrow(courseCodeField, Priority.ALWAYS);

        // Row 7: Exam Session
        Label examSessionLabel = new Label("New Exam Session:");
        examSessionLabel.getStyleClass().add("label");
        formPane.add(examSessionLabel, 0, 7);
        formPane.add(examSessionField, 1, 7);
        GridPane.setHgrow(examSessionField, Priority.ALWAYS);

        // Row 8: Update Button (Span 2 columns)
        formPane.add(updateButton, 0, 8, 2, 1);
        GridPane.setValignment(updateButton, VPos.CENTER);

        // Row 9: Message Label (Span 2 columns)
        formPane.add(messageLabel, 0, 9, 2, 1);


        // --- Main VBox Setup ---
        setSpacing(15);
        setPadding(new Insets(10));
        getChildren().addAll(viewTitle, formPane);

        // Load CSS
        loadStyles();

        // Populate officer combo box
        fetchOfficers();

        // Action Listeners
        loadDataButton.setOnAction(event -> loadFileData());
        updateButton.setOnAction(event -> updateFile());

        // Re-enable update button only if File ID field changes after a successful load
        // Or disable it if the file ID is cleared
        fileIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            // If the file ID changes OR is cleared, disable update until re-loaded
            if (updateButton != null && !updateButton.isDisabled()) { // Check if button exists and is enabled
                updateButton.setDisable(true);
                loadedFileCurrentOfficerId = null; // Reset loaded officer state
                setMessage("File ID changed or cleared. Please Load File Data again before updating.", false);
            }
            // Optionally clear other fields: clearFieldsExceptId();
        });

    }

    private void loadStyles() {
        URL cssUrl = getClass().getResource("/css/styles.css");
        if (cssUrl != null) {
            getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("UpdateFileView CSS loaded.");
        } else {
            System.err.println("Warning: UpdateFileView styles.css not found.");
        }
    }

    private void configureOfficerComboBoxCellFactories() {
        officerComboBox.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername() + " (ID: " + item.getUserId() + ")");
            }
        });

        // Add a null option representing "No Change"
        officerComboBox.getItems().add(0, null);

        officerComboBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("-- No Change --"); // Display text for null/no change option
                } else {
                    setText(item.getUsername());
                }
            }
        });
        officerComboBox.setValue(null); // Default to no change
    }

    private void fetchOfficers() {
        new Thread(() -> {
            String currentUserId = LoginView.getUserId();
            if (currentUserId == null) {
                Platform.runLater(() -> setMessage("Error: Not logged in.", true));
                return;
            }
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/users"))
                        .header("User-Id", currentUserId)
                        .GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        User[] users = GsonConfig.getGson().fromJson(response.body(), User[].class);
                        // Filter out null users if any possibility exists from backend/gson parsing
                        List<User> validUsers = Arrays.stream(users).filter(java.util.Objects::nonNull).toList();
                        officerComboBox.getItems().setAll(validUsers); // Set items first
                        configureOfficerComboBoxCellFactories(); // Then configure display & add null
                        // setMessage("", false); // Don't clear message during initial setup
                    } else {
                        setMessage("Failed to fetch officers: " + response.body(), true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> setMessage("Error fetching officers: " + e.getMessage(), true));
            }
        }).start();
    }

    private void loadFileData() {
        String currentUserId = LoginView.getUserId();
        if (currentUserId == null) {
            setMessage("Error: Not logged in.", true);
            return;
        }

        String fileId = fileIdField.getText().trim();
        if (fileId.isEmpty()) {
            setMessage("File ID is required to load data.", true);
            return;
        }

        // Reset state before loading
        clearFieldsExceptId();
        updateButton.setDisable(true);
        loadedFileCurrentOfficerId = null;
        setMessage("Loading file data for ID: " + fileId + "...", false);

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/files/" + fileId))
                        .header("User-Id", currentUserId)
                        .GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        File file = GsonConfig.getGson().fromJson(response.body(), File.class);

                        loadedFileCurrentOfficerId = file.getCurrentOfficer();

                        titleField.setText(file.getTitle() != null ? file.getTitle() : "");
                        courseCodeField.setText(file.getCourseCode() != null ? file.getCourseCode() : "");
                        examSessionField.setText(file.getExamSession() != null ? file.getExamSession() : "");

                        if (file.getStatus() != null && STATUS_OPTIONS.contains(file.getStatus())) {
                            statusComboBox.setPromptText("Current: " + file.getStatus());
                            statusComboBox.setValue(NO_STATUS_CHANGE);
                        } else {
                            statusComboBox.setPromptText("Select new status (Optional)");
                            statusComboBox.setValue(NO_STATUS_CHANGE);
                        }

                        if (loadedFileCurrentOfficerId != null) {
                            Optional<User> currentOfficer = officerComboBox.getItems().stream()
                                    .filter(user -> user != null && loadedFileCurrentOfficerId.equals(user.getUserId()))
                                    .findFirst();
                            officerComboBox.setPromptText(currentOfficer.map(u -> "Current: " + u.getUsername()).orElse("Current ID: " + loadedFileCurrentOfficerId));
                            officerComboBox.setValue(null);
                        } else {
                            officerComboBox.setPromptText("Select new officer (Optional)");
                            officerComboBox.setValue(null);
                        }

                        setMessage("File data loaded. You can now make changes.", false);
                        updateButton.setDisable(false);
                    } else {
                        setMessage("Failed to load file data: " + response.statusCode() + " " + response.body(), true);
                        clearFieldsExceptId();
                        updateButton.setDisable(true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setMessage("Error loading file data: " + e.getMessage(), true);
                    clearFieldsExceptId();
                    updateButton.setDisable(true);
                });
            }
        }).start();
    }


    private void updateFile() {
        String currentUserId = LoginView.getUserId();
        String currentUserRole = LoginView.getUserRole();

        if (currentUserId == null || currentUserRole == null) {
            setMessage("Error: Not logged in.", true);
            return;
        }

        String fileId = fileIdField.getText().trim();
        if (fileId.isEmpty()) {
            setMessage("File ID is required.", true);
            return;
        }

        if ("officer".equals(currentUserRole)) {
            if (loadedFileCurrentOfficerId == null || !loadedFileCurrentOfficerId.equals(currentUserId)) {
                setMessage("Update Denied: Officers can only update files currently assigned to them.", true);
                return;
            }
        }

        String newTitle = titleField.getText().trim();
        String newStatus = statusComboBox.getValue();
        User selectedOfficer = officerComboBox.getValue();
        String newCourseCode = courseCodeField.getText().trim();
        String newExamSession = examSessionField.getText().trim();

        File updatePayload = new File();
        boolean changesMade = false;

        if (!newTitle.isEmpty()) {
            updatePayload.setTitle(newTitle);
            changesMade = true;
        }
        if (newStatus != null && !newStatus.equals(NO_STATUS_CHANGE)) {
            updatePayload.setStatus(newStatus);
            changesMade = true;
        }
        if (selectedOfficer != null) {
            updatePayload.setCurrentOfficer(selectedOfficer.getUserId());
            changesMade = true;
        }
        if (!newCourseCode.isEmpty()) {
            updatePayload.setCourseCode(newCourseCode);
            changesMade = true;
        }
        if (!newExamSession.isEmpty()) {
            updatePayload.setExamSession(newExamSession);
            changesMade = true;
        }

        if (!changesMade) {
            setMessage("No changes detected to update.", false);
            return;
        }

        setMessage("Submitting updates for File ID: " + fileId + "...", false);
        updateButton.setDisable(true); // Disable button during update process

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String jsonBody = GsonConfig.getGson().toJson(updatePayload);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/files/" + fileId))
                        .header("Content-Type", "application/json")
                        .header("User-Id", currentUserId)
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        setMessage("File updated successfully!", false);
                        // *** CHANGE HERE: Do NOT automatically call loadFileData() ***
                        // Let the success message persist.
                        // User can click "Load File Data" again if needed.

                        // Optionally clear the input fields for the *next* update
                        clearFieldsExceptId();
                        // Keep the update button disabled until a file ID is re-loaded? Or re-enable?
                        // Let's disable it to force reload for confirmation.
                        updateButton.setDisable(true);


                    } else {
                        setMessage("Failed to update file: " + response.statusCode() + " " + response.body(), true);
                        updateButton.setDisable(false); // Re-enable button on failure to allow retry
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setMessage("Error updating file: " + e.getMessage(), true);
                    updateButton.setDisable(false); // Re-enable button on error to allow retry
                });
            }
        }).start();
    }

    // Helper to set message and style
    private void setMessage(String text, boolean isError) {
        Platform.runLater(() -> {
            messageLabel.setText(text);
            messageLabel.getStyleClass().removeAll("success-message", "error-message");
            if (isError) {
                messageLabel.getStyleClass().add("error-message");
            } else {
                messageLabel.getStyleClass().add("success-message");
            }
        });
    }

    // Clears fields used for updates, keeps File ID
    private void clearFieldsExceptId() {
        // Avoid clearing during initial setup or if called unnecessarily
        if (titleField != null) titleField.clear();
        if (statusComboBox != null) {
            statusComboBox.setValue(NO_STATUS_CHANGE);
            statusComboBox.setPromptText("Select new status (Optional)");
        }
        if (officerComboBox != null) {
            officerComboBox.setValue(null); // Reset to null ("No Change")
            officerComboBox.setPromptText("Select new officer (Optional)");
        }
        if (courseCodeField != null) courseCodeField.clear();
        if (examSessionField != null) examSessionField.clear();
    }

    public VBox getView() {
        return this;
    }
}