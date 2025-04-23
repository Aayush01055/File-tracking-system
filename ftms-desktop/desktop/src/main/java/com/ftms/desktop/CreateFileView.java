package com.ftms.desktop;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections; // Import for ComboBox items
import javafx.geometry.Insets;
import javafx.geometry.VPos; // For vertical alignment
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority; // For layout growth
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class CreateFileView extends VBox {
    private final TextField titleField;
    // Status field replaced with ComboBox
    private final ComboBox<String> statusComboBox;
    private final ComboBox<User> officerComboBox;
    private final TextField courseCodeField;
    private final TextField examSessionField;
    private final Button createButton;
    private final Label errorLabel;
    private final Gson gson;

    // Define standard statuses
    private static final List<String> STATUS_OPTIONS = Arrays.asList("Draft", "In Progress", "Completed");

    public CreateFileView() {
        this.gson = GsonConfig.getGson();

        // --- UI Component Initialization ---
        Label viewTitle = new Label("Create New File");
        viewTitle.getStyleClass().add("header"); // Use header style
        viewTitle.setMaxWidth(Double.MAX_VALUE); // Allow label to expand

        titleField = new TextField();
        titleField.setPromptText("Enter file title (Required)");
        titleField.getStyleClass().add("text-field");

        statusComboBox = new ComboBox<>(FXCollections.observableArrayList(STATUS_OPTIONS));
        statusComboBox.setPromptText("Select status (Required)");
        statusComboBox.getStyleClass().add("combo-box");
        statusComboBox.setMaxWidth(Double.MAX_VALUE);

        officerComboBox = new ComboBox<>();
        officerComboBox.setPromptText("Select current officer (Required)");
        officerComboBox.getStyleClass().add("combo-box");
        officerComboBox.setMaxWidth(Double.MAX_VALUE);


        courseCodeField = new TextField();
        courseCodeField.setPromptText("Enter course code (Optional)");
        courseCodeField.getStyleClass().add("text-field");

        examSessionField = new TextField();
        examSessionField.setPromptText("Enter exam session (Optional)");
        examSessionField.getStyleClass().add("text-field");

        createButton = new Button("Create File");
        createButton.getStyleClass().add("button");
        createButton.setMaxWidth(Double.MAX_VALUE); // Make button wider

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message"); // Use error style
        errorLabel.setWrapText(true);

        // --- Layout using GridPane ---
        GridPane formPane = new GridPane();
        formPane.setHgap(15); // Increased horizontal gap
        formPane.setVgap(10); // Vertical gap
        formPane.setPadding(new Insets(20)); // Increased padding
        // formPane.getStyleClass().add("form-pane"); // Optional: Add specific style

        // Row 0: Title
        Label titleLabel = new Label("Title:");
        titleLabel.getStyleClass().add("label");
        formPane.add(titleLabel, 0, 0);
        formPane.add(titleField, 1, 0);
        GridPane.setHgrow(titleField, Priority.ALWAYS); // Allow field to grow

        // Row 1: Status
        Label statusLabel = new Label("Status:");
        statusLabel.getStyleClass().add("label");
        formPane.add(statusLabel, 0, 1);
        formPane.add(statusComboBox, 1, 1);

        // Row 2: Officer
        Label officerLabel = new Label("Current Officer:");
        officerLabel.getStyleClass().add("label");
        formPane.add(officerLabel, 0, 2);
        formPane.add(officerComboBox, 1, 2);

        // Row 3: Course Code
        Label courseCodeLabel = new Label("Course Code:");
        courseCodeLabel.getStyleClass().add("label");
        formPane.add(courseCodeLabel, 0, 3);
        formPane.add(courseCodeField, 1, 3);
        GridPane.setHgrow(courseCodeField, Priority.ALWAYS);

        // Row 4: Exam Session
        Label examSessionLabel = new Label("Exam Session:");
        examSessionLabel.getStyleClass().add("label");
        formPane.add(examSessionLabel, 0, 4);
        formPane.add(examSessionField, 1, 4);
        GridPane.setHgrow(examSessionField, Priority.ALWAYS);

        // Row 5: Create Button (Span across 2 columns)
        formPane.add(createButton, 0, 5, 2, 1); // Span 2 columns
        GridPane.setValignment(createButton, VPos.CENTER); // Center vertically

        // Row 6: Error Label (Span across 2 columns)
        formPane.add(errorLabel, 0, 6, 2, 1);

        // --- Main VBox Setup ---
        setSpacing(15); // Spacing between elements in VBox
        setPadding(new Insets(10)); // Padding around the VBox
        getChildren().addAll(viewTitle, formPane); // Add title and form

        // Load CSS
        loadStyles();

        // Fetch officers when the view is created
        fetchOfficers();

        // Create button action
        createButton.setOnAction(event -> createFile());
    }

    private void loadStyles() {
        URL cssUrl = getClass().getResource("/css/styles.css");
        if (cssUrl != null) {
            getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("CreateFileView CSS loaded.");
        } else {
            System.err.println("Warning: CreateFileView styles.css not found.");
        }
    }

    private void fetchOfficers() {
        // (Fetch logic remains the same as previous version)
        new Thread(() -> {
            String currentUserId = LoginView.getUserId();
            if (currentUserId == null) {
                Platform.runLater(() -> errorLabel.setText("Error: Not logged in. Cannot fetch officers."));
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
                        List<User> userList = Arrays.asList(users);
                        officerComboBox.getItems().setAll(userList);
                        configureOfficerComboBoxCellFactories(); // Setup display
                    } else {
                        errorLabel.setText("Failed to fetch officers: " + response.body());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> errorLabel.setText("Error fetching officers: " + e.getMessage()));
            }
        }).start();
    }

    private void configureOfficerComboBoxCellFactories() {
        // Set how User objects are displayed in the ComboBox list
        officerComboBox.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername() + " (ID: " + item.getUserId() + ")"); // Show username and ID
            }
        });
        // Set how the selected User object is displayed in the ComboBox button area
        officerComboBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });
    }


    private void createFile() {
        String currentUserId = LoginView.getUserId();
        if (currentUserId == null) {
            errorLabel.setText("Error: Not logged in. Cannot create file.");
            return;
        }

        // Get values from fields
        String title = titleField.getText().trim();
        String status = statusComboBox.getSelectionModel().getSelectedItem(); // Get from ComboBox
        User selectedOfficer = officerComboBox.getSelectionModel().getSelectedItem();
        String courseCode = courseCodeField.getText().trim();
        String examSession = examSessionField.getText().trim();

        // --- Validation ---
        if (title.isEmpty()) {
            errorLabel.setText("Title is required.");
            return;
        }
        if (status == null || status.isEmpty()) { // Check ComboBox selection
            errorLabel.setText("Status is required.");
            return;
        }
        if (selectedOfficer == null) {
            errorLabel.setText("Current officer is required.");
            return;
        }
        // -------------------

        String currentOfficerId = selectedOfficer.getUserId();

        File file = new File();
        file.setTitle(title);
        file.setStatus(status); // Use status from ComboBox
        file.setCurrentOfficer(currentOfficerId);
        file.setCourseCode(courseCode.isEmpty() ? null : courseCode);
        file.setExamSession(examSession.isEmpty() ? null : examSession);

        // Perform network operation in background thread
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String jsonBody = gson.toJson(file);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/files/register"))
                        .header("Content-Type", "application/json")
                        .header("User-Id", currentUserId)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        errorLabel.getStyleClass().remove("error-message");
                        errorLabel.getStyleClass().add("success-message"); // Use success style
                        errorLabel.setText("File created successfully!");
                        clearFields();
                    } else {
                        errorLabel.getStyleClass().remove("success-message");
                        errorLabel.getStyleClass().add("error-message"); // Ensure error style
                        errorLabel.setText("Failed to create file: " + response.statusCode() + " " + response.body());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.getStyleClass().remove("success-message");
                    errorLabel.getStyleClass().add("error-message");
                    errorLabel.setText("Error creating file: " + e.getMessage());
                });
            }
        }).start();
    }

    private void clearFields() {
        titleField.clear();
        statusComboBox.getSelectionModel().clearSelection(); // Clear ComboBox
        officerComboBox.getSelectionModel().clearSelection();
        courseCodeField.clear();
        examSessionField.clear();
        // Keep the success message or clear it after a delay if desired
        // errorLabel.setText("");
    }

    public VBox getView() {
        return this;
    }
}