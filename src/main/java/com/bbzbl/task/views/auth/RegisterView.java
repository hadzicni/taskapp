package com.bbzbl.task.views.auth;

import com.bbzbl.task.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.stream.Stream;

/**
 * Registration view for new users to create an account.
 */
@AnonymousAllowed
@PageTitle("Registrieren | Task Manager")
@Route(value = "register", autoLayout = false)
public class RegisterView extends VerticalLayout {

    private final UserService userService;
    private final PasswordField passwordField = new PasswordField("Passwort");
    private final PasswordField confirmPasswordField = new PasswordField("Passwort bestätigen");
    private final ProgressBar passwordStrengthBar = new ProgressBar();

    private final Span lengthRequirement = new Span("Mindestlänge: 8 Zeichen");
    private final Span numberRequirement = new Span("Mindestens eine Zahl");
    private final Span specialCharRequirement = new Span("Mindestens ein Sonderzeichen");

    /**
     * Constructs the register view.
     *
     * @param userService the user service for handling registration logic
     */
    public RegisterView(UserService userService) {
        this.userService = userService;

        configureLayout();
        add(buildRegisterCard());
    }

    /**
     * Configures the layout styling and alignment.
     */
    private void configureLayout() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)");
    }

    /**
     * Builds the registration card with input fields and buttons.
     *
     * @return a Div containing the registration form
     */
    private Div buildRegisterCard() {
        H2 title = new H2("Registrieren");
        title.getStyle()
                .set("font-size", "2em")
                .set("font-weight", "600")
                .set("color", "#2c3e50")
                .set("margin-bottom", "20px");

        TextField firstNameField = new TextField("Vorname");
        TextField lastNameField = new TextField("Nachname");
        TextField usernameField = new TextField("Benutzername");
        EmailField emailField = new EmailField("E-Mail");

        setFullWidth(firstNameField, lastNameField, usernameField, emailField, passwordField, confirmPasswordField);
        configurePasswordStrengthBar();
        configureRequirementsStyling();

        HorizontalLayout nameLayout = new HorizontalLayout(firstNameField, lastNameField);
        nameLayout.setWidthFull();
        nameLayout.setSpacing(true);
        nameLayout.getStyle().set("gap", "16px");

        Button registerButton = createRegisterButton(firstNameField, lastNameField, usernameField, emailField);
        Anchor loginLink = createLoginLink();

        registerButton.addClickListener(e -> handleRegister(
                firstNameField.getValue(),
                lastNameField.getValue(),
                usernameField.getValue(),
                emailField.getValue(),
                passwordField.getValue(),
                confirmPasswordField.getValue()
        ));

        VerticalLayout passwordFeedback = new VerticalLayout(lengthRequirement, numberRequirement, specialCharRequirement, passwordStrengthBar);
        passwordFeedback.setPadding(false);
        passwordFeedback.setSpacing(false);
        passwordFeedback.getStyle().set("margin-bottom", "10px");

        VerticalLayout fieldsLayout = new VerticalLayout(
                nameLayout,
                usernameField,
                emailField,
                passwordField,
                passwordFeedback,
                confirmPasswordField,
                registerButton,
                loginLink
        );
        fieldsLayout.setSpacing(false);
        fieldsLayout.setPadding(false);
        fieldsLayout.setWidthFull();
        fieldsLayout.getStyle().set("gap", "12px");

        Div card = new Div(title, fieldsLayout);
        card.getStyle()
                .set("background", "#ffffff")
                .set("padding", "36px")
                .set("border-radius", "16px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.06)")
                .set("max-width", "460px")
                .set("width", "100%")
                .set("text-align", "left");

        return card;
    }

    /**
     * Handles the registration logic when the register button is clicked.
     *
     * @param firstName      the first name of the user
     * @param lastName       the last name of the user
     * @param username       the username of the user
     * @param email          the email address of the user
     * @param password       the password entered by the user
     * @param confirmPassword the confirmation password entered by the user
     */
    private void handleRegister(String firstName, String lastName, String username, String email, String password, String confirmPassword) {
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showErrorDialog("Ungültige E-Mail", "Bitte eine gültige E-Mail-Adresse eingeben.");
            return;
        }
        if (!isValidPassword(password)) {
            showErrorDialog("Schwaches Passwort", "Passwort muss mindestens 8 Zeichen, eine Zahl und ein Sonderzeichen enthalten.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showErrorDialog("Passwörter stimmen nicht überein", "Die eingegebenen Passwörter müssen identisch sein.");
            return;
        }

        boolean success = userService.registerUser(username, password, firstName, lastName, email);
        if (success) {
            showSuccessDialog("Registrierung erfolgreich!", "Sie können sich jetzt anmelden.");
            UI.getCurrent().navigate("login");
        } else {
            showErrorDialog("Fehler", "Benutzername oder E-Mail existiert bereits.");
        }
    }

    /**
     * Configures the password strength bar and its requirements.
     */
    private void configurePasswordStrengthBar() {
        passwordStrengthBar.setWidth("100%");
        passwordStrengthBar.setValue(0);

        passwordField.setMinLength(8);
        confirmPasswordField.setMinLength(8);
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);

        passwordField.addValueChangeListener(event -> {
            String password = event.getValue();
            int progress = 0;

            if (password.length() >= 8) {
                lengthRequirement.getStyle().set("color", "green");
                progress += 33;
            } else {
                lengthRequirement.getStyle().set("color", "#6c757d");
            }

            if (password.matches(".*\\d.*")) {
                numberRequirement.getStyle().set("color", "green");
                progress += 33;
            } else {
                numberRequirement.getStyle().set("color", "#6c757d");
            }

            if (password.matches(".*[!@#$%^&*].*")) {
                specialCharRequirement.getStyle().set("color", "green");
                progress += 34;
            } else {
                specialCharRequirement.getStyle().set("color", "#6c757d");
            }

            passwordStrengthBar.setValue(progress / 100.0);
        });
    }

    /**
     * Configures the styling for the password requirements.
     */
    private void configureRequirementsStyling() {
        Stream.of(lengthRequirement, numberRequirement, specialCharRequirement).forEach(span ->
                span.getStyle().set("display", "block").set("color", "#6c757d").set("margin-bottom", "5px")
        );
    }

    /**
     * Creates a button for user registration and sets its enabled state based on input validation.
     *
     * @param firstName the first name field
     * @param lastName  the last name field
     * @param username  the username field
     * @param email     the email field
     * @return a Button component for registration
     */
    private Button createRegisterButton(TextField firstName, TextField lastName, TextField username, EmailField email) {
        Button registerButton = new Button("Registrieren");
        registerButton.setEnabled(false);

        Runnable checkFields = () -> {
            boolean allFilled = !firstName.isEmpty() && !lastName.isEmpty() &&
                    !username.isEmpty() && !email.isEmpty() &&
                    !passwordField.isEmpty() && !confirmPasswordField.isEmpty();
            registerButton.setEnabled(allFilled);
        };

        Stream.of(firstName, lastName, username, email, passwordField, confirmPasswordField)
                .forEach(field -> field.addValueChangeListener(e -> checkFields.run()));

        registerButton.getStyle()
                .set("background", "#007bff")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("margin-top", "10px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s");

        return registerButton;
    }

    /**
     * Creates a link that redirects the user back to the login page.
     *
     * @return an Anchor component for the login link
     */
    private Anchor createLoginLink() {
        Anchor loginLink = new Anchor("/login", "Zurück zum Login");
        loginLink.getStyle()
                .set("display", "block")
                .set("margin-top", "10px")
                .set("color", "#007bff")
                .set("font-size", "1.1em")
                .set("text-decoration", "none")
                .set("transition", "0.3s");

        loginLink.getElement().setAttribute("onmouseover", "this.style.color='#0056b3'");
        loginLink.getElement().setAttribute("onmouseout", "this.style.color='#007bff'");

        return loginLink;
    }

    /**
     * Validates the password based on specific criteria.
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$");
    }

    /**
     * Displays an error dialog with a title and message.
     *
     * @param title   the title of the dialog
     * @param message the message to display
     */
    private void showErrorDialog(String title, String message) {
        showDialog(title, message);
    }

    /**
     * Displays a success dialog with a title and message.
     *
     * @param title   the title of the dialog
     * @param message the message to display
     */
    private void showSuccessDialog(String title, String message) {
        showDialog(title, message);
    }

    /**
     * Displays a dialog with a title and message.
     *
     * @param title   the title of the dialog
     * @param message the message to display
     */
    private void showDialog(String title, String message) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout(
                new Span(title),
                new Span(message),
                new Button("OK", e -> dialog.close())
        );
        dialog.add(layout);
        dialog.open();
    }

    /**
     * Sets the width of the components to full.
     *
     * @param components the components to set the width for
     */
    private void setFullWidth(Component... components) {
        for (Component component : components) {
            if (component instanceof HasSize) {
                ((HasSize) component).setWidthFull();
            }
        }
    }
}