package com.bbzbl.task.views.user;

import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
@PageTitle("Profil")
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
        getStyle().set("background", "linear-gradient(to right, #f0f4f8, #dfe9f3)");

        H2 title = new H2("Mein Profil");
        title.getStyle()
                .set("font-size", "2.2em")
                .set("font-weight", "600")
                .set("color", "#2c3e50")
                .set("margin-bottom", "20px");

        TextField firstNameField = new TextField("Vorname");
        TextField lastNameField = new TextField("Nachname");
        TextField usernameField = new TextField("Benutzername");
        EmailField emailField = new EmailField("E-Mail");

        firstNameField.setWidthFull();
        lastNameField.setWidthFull();
        usernameField.setWidthFull();
        emailField.setWidthFull();

        HorizontalLayout nameLayout = new HorizontalLayout(firstNameField, lastNameField);
        nameLayout.setWidthFull();
        nameLayout.setSpacing(true);
        nameLayout.getStyle().set("gap", "20px");

        Button saveButton = new Button("Änderungen speichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("padding", "10px 24px")
                .set("border-radius", "8px");

        Button deleteUserButton = new Button("Benutzer löschen");
        deleteUserButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteUserButton.getStyle()
                .set("padding", "10px 24px")
                .set("border-radius", "8px");

        Span changePasswordLink = new Span("🔒 Passwort ändern");
        changePasswordLink.getStyle()
                .set("display", "block")
                .set("margin-top", "12px")
                .set("color", "#007bff")
                .set("font-size", "0.95em")
                .set("cursor", "pointer")
                .set("text-decoration", "underline");

        changePasswordLink.addClickListener(e -> openPasswordDialog());
        deleteUserButton.addClickListener(e -> openDeleteUserDialog());

        loadCurrentUser();

        binder.bind(usernameField, User::getUsername, User::setUsername);
        binder.bind(firstNameField, User::getFirstName, User::setFirstName);
        binder.bind(lastNameField, User::getLastName, User::setLastName);
        binder.bind(emailField, User::getEmail, User::setEmail);

        saveButton.addClickListener(e -> saveUser());

        VerticalLayout fieldsLayout = new VerticalLayout(nameLayout, usernameField, emailField);
        fieldsLayout.setSpacing(false);
        fieldsLayout.setPadding(false);
        fieldsLayout.setWidthFull();
        fieldsLayout.getStyle().set("gap", "12px");

        VerticalLayout buttonLayout = new VerticalLayout(saveButton, deleteUserButton, changePasswordLink);
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(false);

        Div card = new Div(title, fieldsLayout, buttonLayout);
        card.getStyle()
                .set("background", "#ffffff")
                .set("padding", "32px")
                .set("border-radius", "16px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.06)")
                .set("width", "100%")
                .set("max-width", "460px");

        add(card);
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