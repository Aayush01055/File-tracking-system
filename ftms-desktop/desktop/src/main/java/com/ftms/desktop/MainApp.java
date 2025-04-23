package com.ftms.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // For alignment
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator; // Import Separator
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority; // For Vgrow
import javafx.stage.Stage;
import java.net.URL; // For CSS loading

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane mainLayout;
    private VBox navBar;
    private Label userInfoLabel; // Label to display user info in navbar

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainLayout = new BorderPane();

        // Initialize components
        userInfoLabel = new Label("Status: Not Logged In");
        userInfoLabel.getStyleClass().add("label"); // Use standard label style
        userInfoLabel.setPadding(new Insets(5, 0, 0, 0)); // Padding above the label

        navBar = createNavBar(); // Create initial navbar
        mainLayout.setLeft(navBar);

        showLoginView(); // Start with Login view

        primaryStage.setTitle("FTMS Desktop - File Tracking Management System"); // More descriptive title
        primaryStage.setScene(new Scene(mainLayout, 1100, 700)); // Slightly larger window
        primaryStage.show();
    }

    // Method to update the navigation bar's button states and user info
    public void updateNavBar() {
        Platform.runLater(() -> {
            navBar = createNavBar(); // Recreate the navbar based on current state
            mainLayout.setLeft(navBar);
            updateUserInfoLabel(); // Update the user info display
        });
    }

    // Updates the user info label text
    private void updateUserInfoLabel() {
        String userId = LoginView.getUserId();
        String userRole = LoginView.getUserRole();
        if (userId != null && userRole != null) {
            userInfoLabel.setText("User: " + userId + "\nRole: " + userRole);
            userInfoLabel.getStyleClass().remove("error-message"); // Ensure default style
        } else {
            userInfoLabel.setText("Status: Not Logged In");
            userInfoLabel.getStyleClass().add("error-message"); // Use error style for emphasis
        }
    }


    // Method to show the appropriate view after successful login
    public void showDefaultViewForRole() {
        Platform.runLater(() -> { // Ensure UI updates are on FX thread
            String role = LoginView.getUserRole();
            if ("admin".equals(role)) {
                showFileTrackingView(); // Show tracking view by default for admin
            } else if ("officer".equals(role)) {
                showFileTrackingView(); // Show tracking view by default for officer
            } else {
                showLoginView(); // Fallback to login view
            }
        });
    }


    private void showLoginView() {
        LoginView loginView = new LoginView(this);
        mainLayout.setCenter(loginView.getView());
        updateNavBar(); // Update navbar when showing login view
    }

    // Logout Functionality
    private void logout() {
        LoginView.setLoginState(null, null); // Clear static user state in LoginView
        showLoginView(); // Go back to the login screen
        // updateNavBar() is called within showLoginView()
        System.out.println("User logged out.");
    }


    private void showRegisterView() {
        // Check if admin is logged in before allowing view change
        if (LoginView.getUserId() != null && "admin".equals(LoginView.getUserRole())) {
            RegisterView registerView = new RegisterView();
            mainLayout.setCenter(registerView.getView());
        } else {
            System.err.println("Access Denied: Admin privileges required for Register View.");
            // Optionally show an alert dialog
        }
    }

    private void showCreateFileView() {
        if (LoginView.getUserId() != null && "admin".equals(LoginView.getUserRole())) {
            CreateFileView createFileView = new CreateFileView();
            mainLayout.setCenter(createFileView.getView());
        } else {
            System.err.println("Access Denied: Admin privileges required for Create File View.");
        }
    }
    private void showUpdateFileView() {
        if (LoginView.getUserId() != null && ("admin".equals(LoginView.getUserRole()) || "officer".equals(LoginView.getUserRole()))) { // Allow Officer too maybe? Check requirement
            UpdateFileView updateFileView = new UpdateFileView();
            mainLayout.setCenter(updateFileView.getView());
        } else {
            System.err.println("Access Denied: Login required for Update File View.");
        }
    }
    private void showFileTrackingView() {
        if (LoginView.getUserId() != null && ("officer".equals(LoginView.getUserRole()) || "admin".equals(LoginView.getUserRole()))) {
            FileTrackingView fileTrackingView = new FileTrackingView();
            mainLayout.setCenter(fileTrackingView.getView());
        } else {
            System.err.println("Access Denied: Login required for File Tracking View.");
        }
    }

    private void showSearchView() {
        if (LoginView.getUserId() != null && ("officer".equals(LoginView.getUserRole()) || "admin".equals(LoginView.getUserRole()))) {
            SearchView searchView = new SearchView();
            mainLayout.setCenter(searchView.getView());
        } else {
            System.err.println("Access Denied: Login required for Search View.");
        }
    }

    private void showAuditView() {
        if (LoginView.getUserId() != null && "admin".equals(LoginView.getUserRole())) {
            AuditView auditView = new AuditView();
            mainLayout.setCenter(auditView.getView());
        } else {
            System.err.println("Access Denied: Admin privileges required for Audit View.");
        }
    }

    // Creates the NavBar based on the *current* login state
    private VBox createNavBar() {
        VBox newNavBar = new VBox(8); // Slightly reduced spacing between items
        newNavBar.setPadding(new Insets(15)); // Increased padding
        newNavBar.getStyleClass().add("nav-bar");
        newNavBar.setPrefWidth(200); // Give navbar a preferred width
        newNavBar.setAlignment(Pos.TOP_LEFT); // Align items to top-left

        Label navHeader = new Label("Navigation Menu");
        navHeader.getStyleClass().add("header"); // Use header style but maybe smaller?
        navHeader.setStyle("-fx-font-size: 16px; -fx-padding: 0 0 10px 0;"); // Override style slightly


        // --- User/Auth Section ---
        Button authButton; // Button for Login or Logout
        if (LoginView.getUserId() != null) {
            authButton = new Button("Logout");
            // authButton.setGraphic( /* Icon: sign-out-alt */ );
            authButton.setOnAction(e -> logout());
        } else {
            authButton = new Button("Login");
            // authButton.setGraphic( /* Icon: sign-in-alt */ );
            authButton.setOnAction(e -> showLoginView());
        }
        authButton.getStyleClass().add("nav-button");
        authButton.setMaxWidth(Double.MAX_VALUE); // Make buttons fill width

        Button registerButton = new Button("Register User");
        // registerButton.setGraphic( /* Icon: user-plus */ );
        registerButton.getStyleClass().add("nav-button");
        registerButton.setOnAction(e -> showRegisterView());
        registerButton.setDisable(LoginView.getUserId() == null || !"admin".equals(LoginView.getUserRole())); // Admin Only
        registerButton.setMaxWidth(Double.MAX_VALUE);


        // --- File Management Section ---
        Button createFileButton = new Button("Create File");
        // createFileButton.setGraphic( /* Icon: file-plus */ );
        createFileButton.getStyleClass().add("nav-button");
        createFileButton.setOnAction(e -> showCreateFileView());
        createFileButton.setDisable(LoginView.getUserId() == null || !"admin".equals(LoginView.getUserRole())); // Admin Only
        createFileButton.setMaxWidth(Double.MAX_VALUE);

        Button updateButton = new Button("Update File");
        // updateButton.setGraphic( /* Icon: file-edit */ );
        updateButton.getStyleClass().add("nav-button");
        updateButton.setOnAction(e -> showUpdateFileView());
        // Allow Admin or Officer to update
        updateButton.setDisable(LoginView.getUserId() == null ||
                !("admin".equals(LoginView.getUserRole()) || "officer".equals(LoginView.getUserRole())));
        updateButton.setMaxWidth(Double.MAX_VALUE);


        // --- Tracking & Audit Section ---
        Button trackFileButton = new Button("Track/Search Files"); // Combined label slightly
        // trackFileButton.setGraphic( /* Icon: search-location / tasks */ );
        trackFileButton.getStyleClass().add("nav-button");
        trackFileButton.setOnAction(e -> showFileTrackingView());
        trackFileButton.setDisable(LoginView.getUserId() == null ||
                !("officer".equals(LoginView.getUserRole()) || "admin".equals(LoginView.getUserRole())));
        trackFileButton.setMaxWidth(Double.MAX_VALUE);

        Button searchContentButton = new Button("File Content Search");
        // searchContentButton.setGraphic( /* Icon: search */ );
        searchContentButton.getStyleClass().add("nav-button");
        searchContentButton.setOnAction(e -> showSearchView());
        searchContentButton.setDisable(LoginView.getUserId() == null ||
                !("officer".equals(LoginView.getUserRole()) || "admin".equals(LoginView.getUserRole())));
        searchContentButton.setMaxWidth(Double.MAX_VALUE);


        Button auditButton = new Button("View Audit Logs");
        // auditButton.setGraphic( /* Icon: history / list-alt */ );
        auditButton.getStyleClass().add("nav-button");
        auditButton.setOnAction(e -> showAuditView());
        auditButton.setDisable(LoginView.getUserId() == null || !"admin".equals(LoginView.getUserRole())); // Admin Only
        auditButton.setMaxWidth(Double.MAX_VALUE);


        // --- Structure Navbar ---
        VBox spacer = new VBox(); // Spacer to push user info to bottom
        VBox.setVgrow(spacer, Priority.ALWAYS); // Allow spacer to grow

        // Add components to the VBox
        newNavBar.getChildren().addAll(
                navHeader,
                authButton,
                registerButton, // Admin only section
                new Separator(), // Separator
                createFileButton,
                updateButton,
                new Separator(), // Separator
                trackFileButton,
                searchContentButton, // Renamed from generic search
                auditButton, // Admin only section
                spacer, // Pushes user info down
                userInfoLabel // Add user info label at the bottom
        );

        // Load CSS for the NavBar
        loadNavBarStyles(newNavBar);
        updateUserInfoLabel(); // Update label text on creation

        return newNavBar;
    }

    private void loadNavBarStyles(VBox bar) {
        URL cssUrl = getClass().getResource("/css/styles.css");
        if (cssUrl != null) {
            bar.getStylesheets().add(cssUrl.toExternalForm());
            // System.out.println("NavBar CSS loaded successfully.");
        } else {
            System.err.println("Error: styles.css not found for NavBar at /css/styles.css");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}