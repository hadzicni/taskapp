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
                .set("font-size", "2.2em")
                .set("font-weight", "600")
                .set("color", "#2c3e50")
                .set("margin-bottom", "12px");

        Span subtitle = new Span("Bitte melden Sie sich mit Ihrem Account an.");
        subtitle.getStyle()
                .set("font-size", "1.1em")
                .set("color", "#555")
                .set("margin-bottom", "20px")
                .set("display", "block");

        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.getStyle()
                .set("width", "100%")
                .set("max-width", "320px")
                .set("margin", "0 auto")
                .set("margin-bottom", "10px");

        Anchor registerLink = new Anchor("/register", "Noch keinen Account? Jetzt registrieren");
        registerLink.getStyle()
                .set("display", "block")
                .set("margin-top", "12px")
                .set("color", "#007bff")
                .set("font-size", "1em")
                .set("text-decoration", "none")
                .set("transition", "color 0.3s");

        registerLink.getElement().setAttribute("onmouseover", "this.style.color='#0056b3'");
        registerLink.getElement().setAttribute("onmouseout", "this.style.color='#007bff'");

        VerticalLayout layout = new VerticalLayout(title, subtitle, loginForm, registerLink);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidthFull();

        Div card = new Div(layout);
        card.getStyle()
                .set("background", "#ffffff")
                .set("padding", "36px")
                .set("border-radius", "16px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.06)")
                .set("text-align", "center")
                .set("max-width", "420px")
                .set("width", "100%");

        return card;
    }
}
