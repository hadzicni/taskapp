package com.bbzbl.task.views.auth;

import com.bbzbl.task.security.AuthenticatedUser;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Login view that allows unauthenticated users to log in.
 * Redirects authenticated users to the home page.
 */
@AnonymousAllowed
@PageTitle("Login | Task Manager")
@Route(value = "login", autoLayout = false)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final LoginForm loginForm = new LoginForm();

    /**
     * Constructor sets up the UI for the login form.
     *
     * @param authenticatedUser reference to the current authenticated user
     */
    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        configureLayout();
        add(buildLoginCard());
    }

    /**
     * Redirects authenticated users to the home page.
     * Displays an error message on failed login attempts.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            event.forwardTo("");
        }

        boolean loginError = event.getLocation().getQueryParameters()
                .getParameters()
                .containsKey("error");
        loginForm.setError(loginError);
    }

    /**
     * Configures the root layout styling.
     */
    private void configureLayout() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)");
    }

    /**
     * Builds the login card layout.
     *
     * @return the login card as a component
     */
    private Div buildLoginCard() {
        H2 title = new H2("Willkommen zurück");
        title.getStyle()
                .set("font-size", "2em")
                .set("color", "#007bff")
                .set("margin-bottom", "10px");

        Span subtitle = new Span("Bitte melden Sie sich mit Ihrem Account an.");
        subtitle.getStyle()
                .set("font-size", "1.1em")
                .set("color", "#555");

        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.getStyle().set("margin-bottom", "10px");

        Anchor registerLink = new Anchor("/register", "Noch keinen Account? Hier registrieren");
        registerLink.getStyle()
                .set("display", "block")
                .set("margin-top", "6px")
                .set("color", "#007bff")
                .set("font-size", "1em")
                .set("text-decoration", "none")
                .set("transition", "0.3s");

        registerLink.getElement().setAttribute("onmouseover", "this.style.color='#0056b3'");
        registerLink.getElement().setAttribute("onmouseout", "this.style.color='#007bff'");

        Div loginCard = new Div(title, subtitle, loginForm, registerLink);
        loginCard.getStyle()
                .set("background", "#ffffff")
                .set("padding", "30px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
                .set("text-align", "center")
                .set("max-width", "400px")
                .set("width", "100%");

        return loginCard;
    }
}
