package com.bbzbl.task.views.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Logout confirmation view shown after a user logs out.
 */
@PageTitle("Logout | Task Manager")
@Route(value = "logout", autoLayout = false)
@AnonymousAllowed
public class LogoutView extends VerticalLayout {

    /**
     * Constructs the logout view with a message and a button to return to the login page.
     */
    public LogoutView() {
        configureLayout();
        add(createTitle(), createMessage(), createLoginButton());
    }

    /**
     * Configures the layout styling and alignment.
     */
    private void configureLayout() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle()
                .set("padding", "20px")
                .set("background-color", "#f4f7f6")
                .set("text-align", "center");
    }

    /**
     * Creates the title component.
     *
     * @return an H1 component with the logout title
     */
    private H1 createTitle() {
        return new H1("You have been logged out.");
    }

    /**
     * Creates the message component shown below the title.
     *
     * @return a Span with a logout confirmation message
     */
    private Span createMessage() {
        return new Span("Thank you for using the Task Manager. You have successfully logged out.");
    }

    /**
     * Creates a button that redirects the user back to the login page.
     *
     * @return a Button component
     */
    private Button createLoginButton() {
        return new Button("Back to Login", event -> UI.getCurrent().navigate("login"));
    }
}
