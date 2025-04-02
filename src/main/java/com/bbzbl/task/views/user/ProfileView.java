package com.bbzbl.task.views.user;

import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Profile view for displaying and editing user information.
 */
@PermitAll
@PageTitle("Profil | Task Manager")
@Route("profile")
public class ProfileView extends VerticalLayout {

    private final UserService userService;
    private User currentUser;

    private final Binder<User> binder = new Binder<>(User.class);

    /**
     * Constructor for the ProfileView.
     *
     * @param userService the user service to manage user data
     */
    public ProfileView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)");

        H2 title = new H2("Mein Profil");
        title.getStyle()
                .set("font-size", "2em")
                .set("color", "#007bff")
                .set("margin-bottom", "10px");

        TextField firstNameField = new TextField("Vorname");
        firstNameField.setWidthFull();
        TextField lastNameField = new TextField("Nachname");
        lastNameField.setWidthFull();
        TextField usernameField = new TextField("Benutzername");
        usernameField.setWidthFull();
        EmailField emailField = new EmailField("E-Mail");
        emailField.setWidthFull();

        HorizontalLayout nameLayout = new HorizontalLayout(firstNameField, lastNameField);
        nameLayout.setWidthFull();
        nameLayout.setSpacing(true);

        Button saveButton = new Button("Speichern");
        saveButton.getStyle()
                .set("background", "#007bff")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s");

        Button deleteUserButton = new Button("Benutzer löschen");
        deleteUserButton.getStyle()
                .set("background", "#dc3545")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s")
                .set("margin-top", "5px");

        Span changePasswordLink = new Span("Passwort ändern");
        changePasswordLink.getStyle()
                .set("display", "block")
                .set("margin-top", "8px")
                .set("color", "#6c757d")
                .set("font-size", "0.9em")
                .set("text-decoration", "none")
                .set("cursor", "pointer");

        changePasswordLink.getElement().setAttribute("onmouseover", "this.style.color='#007bff'");
        changePasswordLink.getElement().setAttribute("onmouseout", "this.style.color='#6c757d'");

        changePasswordLink.addClickListener(event -> openPasswordDialog());
        deleteUserButton.addClickListener(event -> openDeleteUserDialog());

        loadCurrentUser();

        binder.bind(usernameField, User::getUsername, User::setUsername);
        binder.bind(firstNameField, User::getFirstName, User::setFirstName);
        binder.bind(lastNameField, User::getLastName, User::setLastName);
        binder.bind(emailField, User::getEmail, User::setEmail);

        saveButton.addClickListener(event -> saveUser());

        Div profileCard = new Div();
        profileCard.getStyle()
                .set("background", "#ffffff")
                .set("padding", "30px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
                .set("text-align", "center")
                .set("max-width", "400px")
                .set("width", "100%");

        VerticalLayout buttonLayout = new VerticalLayout(saveButton, deleteUserButton);
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.getStyle().set("gap", "1px");


        profileCard.add(title, nameLayout, usernameField, emailField, changePasswordLink, buttonLayout);

        add(profileCard);
    }

    /**
     * Loads the current user information into the form fields.
     */
    private void loadCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            Optional<User> userOptional = userService.getUserByUsername(username);
            if (userOptional.isPresent()) {
                currentUser = userOptional.get();
                binder.setBean(currentUser);
            } else {
                showErrorDialog("Fehler", "Benutzer konnte nicht geladen werden.");
            }
        }
    }

    /**
     * Saves the user information from the form fields.
     */
    private void saveUser() {
        try {
            binder.writeBean(currentUser);
            userService.updateUser(currentUser);
            showSuccessDialog("Gespeichert", "Ihre Änderungen wurden erfolgreich gespeichert.");
        } catch (ValidationException e) {
            showErrorDialog("Fehler", "Bitte alle Felder korrekt ausfüllen.");
        }
    }

    /**
     * Opens a dialog to confirm user deletion.
     */
    private void openDeleteUserDialog() {
        Dialog deleteDialog = new Dialog();
        deleteDialog.setHeaderTitle("Benutzer löschen");

        Span message = new Span("Sind Sie sicher, dass Sie Ihr Konto löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden. Alle Tasks werden ebenfalls gelöscht.");
        Button confirmButton = new Button("Löschen", event -> {
            userService.deleteUser(currentUser);
            deleteDialog.close();
            showSuccessDialog("Benutzer gelöscht", "Ihr Konto wurde erfolgreich gelöscht.");
        });
        confirmButton.getStyle().set("background", "#dc3545").set("color", "white");

        Button cancelButton = new Button("Abbrechen", event -> deleteDialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(message, confirmButton, cancelButton);
        deleteDialog.add(dialogLayout);
        deleteDialog.open();
    }

    /**
     * Opens a dialog to change the user's password.
     */
    private void openPasswordDialog() {
        Dialog passwordDialog = new Dialog();
        passwordDialog.setHeaderTitle("Passwort ändern");

        PasswordField newPasswordField = new PasswordField("Neues Passwort");
        PasswordField confirmPasswordField = new PasswordField("Passwort bestätigen");

        Button savePasswordButton = new Button("Speichern", event -> {
            String newPassword = newPasswordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (!newPassword.equals(confirmPassword)) {
                showErrorDialog("Fehler", "Die Passwörter stimmen nicht überein.");
                return;
            }

            if (!userService.isValidPassword(newPassword)) {
                showErrorDialog("Schwaches Passwort", "Passwort muss mindestens 8 Zeichen, eine Zahl und ein Sonderzeichen enthalten.");
                return;
            }

            currentUser.setPassword(userService.encodePassword(newPassword));
            userService.saveUser(currentUser);
            showSuccessDialog("Erfolgreich", "Passwort wurde geändert.");
            passwordDialog.close();
        });

        Button cancelButton = new Button("Abbrechen", event -> passwordDialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(newPasswordField, confirmPasswordField, savePasswordButton, cancelButton);
        passwordDialog.add(dialogLayout);
        passwordDialog.open();
    }

    /**
     * Retrieves the current logged-in user's username.
     *
     * @return the username of the current user
     */
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : null;
    }

    /**
     * Displays an error dialog with a title and message.
     *
     * @param title   the title of the dialog
     * @param message the message to display
     */
    private void showErrorDialog(String title, String message) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout(new Span(title), new Span(message), new Button("OK", e -> dialog.close()));
        dialog.add(layout);
        dialog.open();
    }

    /**
     * Displays a success dialog with a title and message.
     *
     * @param title   the title of the dialog
     * @param message the message to display
     */
    private void showSuccessDialog(String title, String message) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout(new Span(title), new Span(message), new Button("OK", e -> dialog.close()));
        dialog.add(layout);
        dialog.open();
    }
}