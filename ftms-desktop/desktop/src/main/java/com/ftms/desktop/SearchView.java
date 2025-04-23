package com.ftms.desktop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // For Alignment
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; // For Buttons
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.URLEncoder;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.lang.reflect.Type;

public class SearchView extends VBox {
    private final TextField queryField;
    private final Button searchButton;
    private final Button clearButton; // Added Clear Button
    private final TextArea resultArea;
    private final Label messageLabel;
    private final Gson gson = GsonConfig.getGson();

    public SearchView() {
        // --- UI Component Initialization ---
        Label viewTitle = new Label("File Content Search"); // More specific title
        viewTitle.getStyleClass().add("header");
        viewTitle.setMaxWidth(Double.MAX_VALUE);
        viewTitle.setAlignment(Pos.CENTER);

        // --- Search Input Section ---
        Label queryLabel = new Label("Search Term:");
        queryLabel.getStyleClass().add("label");
        queryField = new TextField();
        queryField.setPromptText("Enter keyword, title fragment, status, etc.");
        queryField.getStyleClass().add("text-field");

        searchButton = new Button("Search");
        searchButton.getStyleClass().add("button");
        // searchButton.setGraphic( /* Optional Icon */ );

        clearButton = new Button("Clear"); // Initialize clear button
        clearButton.getStyleClass().add("button"); // Or a different style like "secondary-button" if defined
        // clearButton.setGraphic( /* Optional Icon */ );


        // Layout for search controls
        HBox searchControls = new HBox(10); // Use HBox for horizontal layout
        searchControls.setAlignment(Pos.CENTER_LEFT);
        searchControls.getChildren().addAll(queryLabel, queryField, searchButton, clearButton);
        HBox.setHgrow(queryField, Priority.ALWAYS); // Allow query field to grow


        // --- Results Section ---
        messageLabel = new Label("Enter a search term and click 'Search'.");
        messageLabel.getStyleClass().add("label");
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(5, 0, 5, 0)); // Padding around message

        resultArea = new TextArea();
        resultArea.setPromptText("Search results will be displayed here...");
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.getStyleClass().add("text-area");
        VBox.setVgrow(resultArea, Priority.ALWAYS); // Allow area to grow vertically

        // Group results section
        VBox resultsBox = new VBox(5, messageLabel, resultArea); // VBox for message and results area
        resultsBox.setPadding(new Insets(10, 0, 0, 0)); // Padding above results


        // --- Main VBox Setup ---
        setSpacing(15); // Spacing between title, search, results
        setPadding(new Insets(20)); // Overall padding
        getChildren().addAll(viewTitle, searchControls, resultsBox);


        // Load CSS
        loadStyles();


        // --- Action Listeners ---
        searchButton.setOnAction(e -> performSearch());
        clearButton.setOnAction(e -> clearResults()); // Action for clear button
        queryField.setOnAction(e -> performSearch()); // Allow searching by pressing Enter in text field

    }

    private void loadStyles() {
        URL cssUrl = getClass().getResource("/css/styles.css");
        if (cssUrl != null) {
            getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("SearchView CSS loaded.");
        } else {
            System.err.println("Warning: SearchView styles.css not found.");
        }
    }

    private void performSearch() {
        String currentUserId = LoginView.getUserId();
        if (currentUserId == null) {
            setMessage("Error: Not logged in. Please log in to search.", true);
            return;
        }

        String query = queryField.getText().trim();
        if (query.isEmpty()) {
            setMessage("Please enter a search term.", true); // Require a query term
            return;
        }

        setMessage("Searching for files containing '" + query + "'...", false);
        resultArea.clear();


        new Thread(() -> { // Network operation on background thread
            try {
                HttpClient client = HttpClient.newHttpClient();
                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/files/search?query=" + encodedQuery))
                        .header("User-Id", currentUserId)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> { // Update UI on FX thread
                    if (response.statusCode() == 200) {
                        Type fileListType = new TypeToken<List<File>>(){}.getType();
                        List<File> files = gson.fromJson(response.body(), fileListType);

                        if (files != null && !files.isEmpty()) {
                            StringBuilder resultText = new StringBuilder("Found " + files.size() + " matching file(s):\n");
                            resultText.append("========================================\n");
                            for (File file : files) {
                                resultText.append("File ID:     ").append(nullToNA(file.getId())).append("\n");
                                resultText.append("Title:       ").append(nullToNA(file.getTitle())).append("\n");
                                resultText.append("Status:      ").append(nullToNA(file.getStatus())).append("\n");
                                resultText.append("Officer ID:  ").append(nullToNA(file.getCurrentOfficer())).append("\n");
                                resultText.append("Course Code: ").append(nullToNA(file.getCourseCode())).append("\n");
                                resultText.append("Exam Session:").append(nullToNA(file.getExamSession())).append("\n");
                                resultText.append("----------------------------------------\n");
                            }
                            resultArea.setText(resultText.toString());
                            setMessage("Search complete. " + files.size() + " result(s) found.", false);
                        } else {
                            resultArea.setText("No files found matching your query: '" + query + "'");
                            setMessage("No results found.", false); // Not an error, just no results
                        }
                    } else {
                        String errorDetails = response.body() != null ? response.body() : "";
                        resultArea.setText("Error fetching search results.\nStatus Code: " + response.statusCode() + "\n" + errorDetails);
                        setMessage("Search failed: Server returned status " + response.statusCode(), true);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    resultArea.setText("An error occurred during the search:\n" + ex.getMessage());
                    setMessage("Error during search: " + ex.getClass().getSimpleName(), true);
                });
            }
        }).start();
    }

    // Helper to clear results and reset message
    private void clearResults() {
        queryField.clear();
        resultArea.clear();
        setMessage("Enter a search term and click 'Search'.", false); // Reset initial message
        queryField.requestFocus(); // Set focus back to query field
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
                // Use default label style or success-message if preferred for non-errors
                messageLabel.getStyleClass().add("success-message");
            }
        });
    }


    public VBox getView() {
        return this;
    }
}