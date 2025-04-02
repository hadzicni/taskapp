package com.bbzbl.task.layout;

import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.Optional;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final AuthenticatedUser authenticatedUser;
    private Dialog logoutConfirmDialog;
    private H1 viewTitle;

    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        setPrimarySection(Section.NAVBAR);
        createLogoutConfirmationDialog();
        addHeaderContent();
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private void addHeaderContent() {
        viewTitle = new H1("TaskApp");
        viewTitle.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);
        viewTitle.getStyle()
                .set("color", "#2c3e50")
                .set("font-weight", "600");

        HorizontalLayout navLinks = createMenuLinks();

        HorizontalLayout leftSection = new HorizontalLayout(viewTitle, navLinks);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSection.setSpacing(true);
        leftSection.setFlexGrow(1, navLinks);
        leftSection.getStyle().set("gap", "24px");

        HorizontalLayout header = new HorizontalLayout(leftSection, createProfileArea());
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidthFull();
        header.setPadding(true);
        header.getStyle()
                .set("background", "linear-gradient(to right, #ffffff, #eef3fb)")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.04)")
                .set("padding", "12px 32px")
                .set("border-bottom", "1px solid #e0e0e0")
                .set("flex-wrap", "wrap");

        addToNavbar(true, header);
    }

    private HorizontalLayout createMenuLinks() {
        HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);
        menu.getStyle().set("flex-wrap", "wrap");

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();

        for (MenuEntry entry : menuEntries) {
            Anchor link = new Anchor(entry.path(), entry.title());
            link.getStyle()
                    .set("color", "#34495e")
                    .set("font-weight", "500")
                    .set("text-decoration", "none")
                    .set("padding", "8px 14px")
                    .set("border-radius", "8px")
                    .set("transition", "background-color 0.2s, color 0.2s");

            link.getElement().addEventListener("mouseover", e ->
                    link.getStyle()
                            .set("background-color", "#ecf5ff")
                            .set("color", "#007bff"));

            link.getElement().addEventListener("mouseout", e ->
                    link.getStyle()
                            .set("background-color", "")
                            .set("color", "#34495e"));

            menu.add(link);
        }

        return menu;
    }

    private Component createProfileArea() {
        return authenticatedUser.get()
                .map(user -> {
                    Avatar avatar = new Avatar(user.getFullName());
                    avatar.getElement().setProperty("title", "Benutzermenü");
                    avatar.getStyle()
                            .set("border", "2px solid #007bff")
                            .set("cursor", "pointer")
                            .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");

                    ContextMenu menu = new ContextMenu(avatar);
                    menu.setOpenOnClick(true);
                    menu.addItem("Profil", e -> UI.getCurrent().navigate("profile"));
                    menu.addItem("Abmelden", e -> logoutConfirmDialog.open());

                    HorizontalLayout layout = new HorizontalLayout(avatar);
                    layout.setAlignItems(FlexComponent.Alignment.CENTER);
                    return layout;
                })
                .orElseGet(this::createLoginIcon);
    }

    private HorizontalLayout createLoginIcon() {
        Icon loginIcon = new Icon(VaadinIcon.SIGN_IN);
        loginIcon.setSize("22px");
        loginIcon.getStyle()
                .set("color", "#007bff")
                .set("cursor", "pointer")
                .set("transition", "color 0.3s ease-in-out");

        loginIcon.getElement().addEventListener("click", e -> UI.getCurrent().navigate("login"));
        loginIcon.getElement().addEventListener("mouseover", e ->
                loginIcon.getStyle().set("color", "#0056b3"));
        loginIcon.getElement().addEventListener("mouseout", e ->
                loginIcon.getStyle().set("color", "#007bff"));

        HorizontalLayout layout = new HorizontalLayout(loginIcon);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        return layout;
    }

    private void createLogoutConfirmationDialog() {
        logoutConfirmDialog = new Dialog();
        logoutConfirmDialog.setHeaderTitle("Abmelden");

        Paragraph confirmationText = new Paragraph("Möchten Sie sich wirklich abmelden?");
        Button cancelButton = new Button("Abbrechen", e -> logoutConfirmDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button logoutButton = new Button("Abmelden");
        logoutButton.addClickListener(e -> {
            logoutConfirmDialog.close();
            UI.getCurrent().navigate("logout");
        });

        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, logoutButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        VerticalLayout layout = new VerticalLayout(confirmationText, buttonLayout);
        layout.setPadding(true);
        layout.setSpacing(true);

        logoutConfirmDialog.add(layout);
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
