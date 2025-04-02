package com.bbzbl.task.views.auth;

import com.bbzbl.task.security.AuthenticatedUser;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;

@Route(value = "logout")
@PageTitle("Logout | Task Manager")
@AnonymousAllowed
@PermitAll
public class LogoutView extends VerticalLayout {

    private final AuthenticatedUser authenticatedUser;

    public LogoutView(AuthenticatedUser authenticatedUser) {
        UI.getCurrent().navigate("logged-out");
        this.authenticatedUser = authenticatedUser;
        configureLayout();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        System.out.println("🚪 LogoutView attached");
        authenticatedUser.logout();

        add(createTitle(), createMessage(), createLoginButton());
    }

    private void configureLayout() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle()
                .set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)")
                .set("padding", "40px");
    }

    private H1 createTitle() {
        H1 title = new H1("Sie wurden abgemeldet");
        title.getStyle()
                .set("color", "#2c3e50")
                .set("font-size", "2em")
                .set("margin-bottom", "10px");
        return title;
    }

    private Paragraph createMessage() {
        Paragraph message = new Paragraph("Danke, dass Sie den Task Manager genutzt haben.");
        message.getStyle()
                .set("font-size", "1.1em")
                .set("color", "#555")
                .set("margin-bottom", "30px");
        return message;
    }

    private Button createLoginButton() {
        Button loginButton = new Button("Zurück zum Login", event -> UI.getCurrent().navigate("login"));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.getStyle()
                .set("padding", "10px 24px")
                .set("border-radius", "8px");
        return loginButton;
    }
}
