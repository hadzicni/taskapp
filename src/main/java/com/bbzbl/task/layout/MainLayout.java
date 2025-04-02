package com.bbzbl.task.layout;

import com.bbzbl.task.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.Optional;

/**
 * Main layout structure for authenticated and anonymous users.
 * Includes a navigation drawer, header with user info, and logout confirmation dialog.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private Dialog logoutConfirmDialog;
    private H1 viewTitle;

    /**
     * Constructor for MainLayout.
     *
     * @param authenticatedUser the authenticated user
     */
    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        setPrimarySection(Section.NAVBAR);
        createLogoutConfirmationDialog();
        addDrawerContent();
        addHeaderContent();
    }

    /**
     * Checks authentication before navigating to a route.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isEmpty()) {
            event.forwardTo("login");
        }
    }

    /**
     * After navigating to a view, update the page title.
     */
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    /**
     * Adds a header to the layout with a title and user profile information.
     */
    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout leftLayout = new HorizontalLayout(toggle, viewTitle);
        leftLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout headerLayout = new HorizontalLayout(leftLayout, createProfileHeader());
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.setPadding(true);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.getStyle()
                .set("background", "#f8f9fa")
                .set("padding", "10px 20px")
                .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)");

        addToNavbar(true, headerLayout);
    }

    /**
     * Creates a profile header with user information or a login link.
     *
     * @return the profile header component
     */
    private Component createProfileHeader() {
        return authenticatedUser.get()
                .map(user -> {
                    Avatar avatar = new Avatar(Optional.ofNullable(user.getFullName()).orElse("User"));
                    avatar.getStyle()
                            .set("border-radius", "50%")
                            .set("border", "2px solid #007bff")
                            .set("cursor", "pointer");

                    Span userName = new Span(Optional.ofNullable(user.getFullName()).orElse("User"));
                    userName.getStyle()
                            .set("font-weight", "bold")
                            .set("color", "#333")
                            .set("margin-right", "4px");

                    HorizontalLayout profileLayout = new HorizontalLayout(avatar, userName);
                    profileLayout.setAlignItems(FlexComponent.Alignment.CENTER);

                    Anchor profileLink = new Anchor("profile");
                    profileLink.add(profileLayout);
                    profileLink.getStyle().set("text-decoration", "none");

                    return profileLink;
                })
                .orElseGet(this::createLoginLink);
    }

    private void addDrawerContent() {
        Span appName = new Span("TaskApp");
        appName.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE);
        appName.getStyle().set("color", "#007bff");

        Header header = new Header(appName);
        header.getStyle()
                .set("padding", "15px")
                .set("background", "#f8f9fa")
                .set("border-bottom", "1px solid #e0e0e0");

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    /**
     * Creates the navigation menu with items based on the MenuConfiguration.
     *
     * @return the navigation component
     */
    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();

        for (MenuEntry entry : menuEntries) {
            SideNavItem item = (entry.icon() != null)
                    ? new SideNavItem(entry.title(), entry.path(), new Icon(entry.icon()))
                    : new SideNavItem(entry.title(), entry.path());

            item.getStyle()
                    .set("padding", "5px 5px")
                    .set("border-radius", "8px")
                    .set("transition", "0.3s ease-in-out");

            nav.addItem(item);
        }

        return nav;
    }

    /**
     * Creates the footer with a logout confirmation dialog or login link.
     *
     * @return the footer component
     */
    private Footer createFooter() {
        Footer footer = new Footer();
        footer.getStyle()
                .set("padding", "15px")
                .set("background", "#f8f9fa")
                .set("border-top", "1px solid #e0e0e0")
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center");

        Component footerContent = authenticatedUser.get().isPresent()
                ? createSignOutLayout()
                : createLoginLink();

        footer.add(footerContent);
        return footer;
    }

    /**
     * Creates a sign-out layout with an icon and text.
     *
     * @return the sign-out layout component
     */
    private Component createSignOutLayout() {
        Icon signOutIcon = new Icon(VaadinIcon.SIGN_OUT);
        signOutIcon.setSize("20px");

        Span signOutText = new Span("Abmelden");
        signOutText.getStyle().set("margin-left", "8px");

        HorizontalLayout layout = new HorizontalLayout(signOutIcon, signOutText);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.getStyle()
                .set("cursor", "pointer")
                .set("color", "#dc3545")
                .set("transition", "color 0.3s ease-in-out")
                .set("padding", "8px");

        layout.getElement().addEventListener("mouseover", e ->
                layout.getStyle().set("color", "#b02a37"));
        layout.getElement().addEventListener("mouseout", e ->
                layout.getStyle().set("color", "#dc3545"));

        layout.addClickListener(e -> logoutConfirmDialog.open());

        return layout;
    }

    /**
     * Creates a login link for anonymous users.
     *
     * @return the login link component
     */
    private Anchor createLoginLink() {
        Anchor loginLink = new Anchor("login", "Anmelden");
        loginLink.getStyle()
                .set("font-weight", "600")
                .set("color", "#007bff")
                .set("text-decoration", "none")
                .set("transition", "color 0.3s ease-in-out");

        loginLink.getElement().addEventListener("mouseover", e ->
                loginLink.getStyle().set("color", "#0056b3"));
        loginLink.getElement().addEventListener("mouseout", e ->
                loginLink.getStyle().set("color", "#007bff"));

        return loginLink;
    }

    /**
     * Creates a logout confirmation dialog.
     */
    private void createLogoutConfirmationDialog() {
        logoutConfirmDialog = new Dialog();
        logoutConfirmDialog.setHeaderTitle("Abmelden");

        Paragraph confirmationText = new Paragraph("Sind Sie sich sicher dass Sie sich abmelden möchten?");
        Button cancelButton = new Button("Abbrechen", e -> logoutConfirmDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button logoutButton = new Button("Abmelden", e -> {
            logoutConfirmDialog.close();
            authenticatedUser.logout();
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

    /**
     * Retrieves the current page title from the MenuConfiguration.
     *
     * @return the current page title
     */
    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
