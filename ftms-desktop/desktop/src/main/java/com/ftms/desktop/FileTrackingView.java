package com.ftms.desktop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority; // For layout
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;

import java.net.URI;
import java.net.URLEncoder;
import java.net.URL; // For CSS loading
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileTrackingView extends VBox {
    private final TextField searchField;
    private final Button searchButton;
    private final TableView<File> fileTable;
    private final Label messageLabel; // Renamed for general messages

    public FileTrackingView() {
        // --- UI Component Initialization ---
        Label viewTitle = new Label("File Tracking & Search");
        viewTitle.getStyleClass().add("header");
        viewTitle.setMaxWidth(Double.MAX_VALUE);

        searchField = new TextField();
        searchField.setPromptText("Enter search query (e.g., Title, Status, Course Code)");
        searchField.getStyleClass().add("text-field");

        searchButton = new Button("Search Files");
        searchButton.getStyleClass().add("button");
        searchButton.setMaxWidth(Double.MAX_VALUE); // Make button fill width


        fileTable = new TableView<>();
        configureTableColumns(); // Setup table columns
        fileTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(fileTable, Priority.ALWAYS); // Allow table to grow vertically

        messageLabel = new Label("Enter a query and click Search."); // Initial message
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);


        // --- Layout ---
        // Search Bar Layout (GridPane for alignment)
        GridPane searchPane = new GridPane();
        searchPane.setHgap(10);
        searchPane.setPadding(new Insets(0, 0, 10, 0)); // Padding below search bar
        searchPane.add(new Label("Search:"), 0, 0);
        searchPane.add(searchField, 1, 0);
        searchPane.add(searchButton, 2, 0);
        GridPane.setHgrow(searchField, Priority.ALWAYS); // Allow field to expand
        // Optional: Set fixed width for button or label if needed

        // Main VBox Setup
        setSpacing(10); // Spacing between search bar, message, and table
        setPadding(new Insets(15)); // Overall padding for the view
        getChildren().addAll(viewTitle, searchPane, messageLabel, fileTable);

        // Load CSS
        loadStyles();

        // Search button action
        searchButton.setOnAction(event -> performSearch());
    }

    private void loadStyles() {
        URL cssUrl = getClass().getResource("/css/styles.css");
        if (cssUrl != null) {
            getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("FileTrackingView CSS loaded.");
        } else {
            System.err.println("Warning: FileTrackingView styles.css not found.");
        }
    }

    private void configureTableColumns() {
        TableColumn<File, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        idColumn.setPrefWidth(100); // Example width

        TableColumn<File, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        titleColumn.setPrefWidth(200);

        TableColumn<File, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        statusColumn.setPrefWidth(100);

        TableColumn<File, String> officerColumn = new TableColumn<>("Officer ID");
        officerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCurrentOfficer()));
        officerColumn.setPrefWidth(100);

        TableColumn<File, String> courseCodeColumn = new TableColumn<>("Course Code");
        courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        courseCodeColumn.setPrefWidth(120);

        TableColumn<File, String> examSessionColumn = new TableColumn<>("Exam Session"); // Added Exam Session
        examSessionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExamSession()));
        examSessionColumn.setPrefWidth(120);


        fileTable.getColumns().addAll(idColumn, titleColumn, statusColumn, officerColumn, courseCodeColumn, examSessionColumn);
    }


    private void performSearch() {
        String currentUserId = LoginView.getUserId();
        if (currentUserId == null) {
            setMessage("Error: Not logged in. Cannot perform search.", true);
            return;
        }

        String query = searchField.getText().trim();
        setMessage("Searching...", false); // Indicate searching
        fileTable.getItems().clear(); // Clear previous results

        new Thread(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/files/search?query=" + encodedQuery))
                        .header("User-Id", currentUserId)
                        .GET().build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Gson gson = GsonConfig.getGson();
                        TypeToken<List<File>> listType = new TypeToken<List<File>>() {};
                        List<File> files = gson.fromJson(response.body(), listType.getType());

                        if (files != null && !files.isEmpty()) {
                            fileTable.getItems().setAll(files);
                            setMessage("Found " + files.size() + " file(s).", false);
                        } else {
                            setMessage("No files found matching the query.", false);
                            // fileTable remains empty
                        }
                    } else {
                        setMessage("Search failed: " + response.statusCode() + " " + response.body(), true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> setMessage("Error during search: " + e.getMessage(), true));
            }
        }).start();
    }

    // Helper to set message and style (same as UpdateFileView)
    private void setMessage(String text, boolean isError) {
        messageLabel.setText(text);
        if (isError) {
            messageLabel.getStyleClass().remove("success-message");
            messageLabel.getStyleClass().add("error-message");
        } else {
            messageLabel.getStyleClass().remove("error-message");
            // Use default style or add success style
            messageLabel.getStyleClass().add("success-message"); // Add success style for non-errors
        }
    }


    // Keep the showAlert utility method (optional, can use setMessage)
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public VBox getView() {
        return this;
    }
}