package com.bbzbl.task.views.home;

import com.bbzbl.task.data.entity.Task;
import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.security.AuthenticatedUser;
import com.bbzbl.task.services.TaskService;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Home view that displays a welcome message and task information for the authenticated user.
 */
@PermitAll
@PageTitle("Home")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
public class HomeView extends VerticalLayout {

    /**
     * Constructor for the HomeView.
     *
     * @param authenticatedUser the authenticated user
     * @param taskService       the task service to fetch user tasks
     */
    public HomeView(AuthenticatedUser authenticatedUser, TaskService taskService) {

        String userName = authenticatedUser.get().map(User::getFullName).orElse("Gast");
        List<Task> userTasks = taskService.getUserTasks();
        LocalDate today = LocalDate.now();

        List<Task> upcomingTasks = userTasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> !task.getStatus().equalsIgnoreCase("Erledigt"))
                .filter(task -> !task.getDueDate().isBefore(today) && task.getDueDate().isBefore(today.plusDays(4)))
                .toList();

        List<Task> overdueTasks = userTasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> !task.getStatus().equalsIgnoreCase("Erledigt"))
                .filter(task -> task.getDueDate().isBefore(today))
                .toList();

        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle().set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)");

        Div welcomeBanner = new Div();
        welcomeBanner.setText("Willkommen zurück, " + userName);
        welcomeBanner.getStyle()
                .set("font-size", "2em")
                .set("font-weight", "bold")
                .set("color", "#ffffff")
                .set("background", "linear-gradient(to right, #007bff, #00bfff)")
                .set("padding", "15px 30px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.15)")
                .set("margin-bottom", "20px")
                .set("text-align", "center");

        Div infoCard = new Div();
        infoCard.getStyle()
                .set("background", "#ffffff")
                .set("padding", "25px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 4px 12px rgba(0, 0, 0, 0.1)")
                .set("max-width", "600px")
                .set("text-align", "center");

        Span description = new Span("Modul 426 BBZBL Task Manager (TeamTask)");
        description.getStyle()
                .set("font-size", "1.3em")
                .set("color", "#444")
                .set("font-weight", "500")
                .set("display", "block")
                .set("margin-bottom", "15px");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);

        VerticalLayout upcomingList = new VerticalLayout();
        upcomingList.setPadding(false);
        upcomingList.setSpacing(false);

        upcomingTasks.forEach(task -> {
            Span item = new Span("• " + task.getTitle() + " (fällig am " + task.getDueDate().format(df) + ")");
            item.getStyle().set("color", "#007bff");
            upcomingList.add(item);
        });

        Details upcomingDetails = new Details("Bevorstehende Tasks (nächste 3 Tage)", upcomingList);
        upcomingDetails.setOpened(false);

        VerticalLayout overdueList = new VerticalLayout();
        overdueList.setPadding(false);
        overdueList.setSpacing(false);

        overdueTasks.forEach(task -> {
            Span item = new Span("• " + task.getTitle() + " (war fällig am " + task.getDueDate().format(df) + ")");
            item.getStyle().set("color", "#dc3545");
            overdueList.add(item);
        });

        Details overdueDetails = new Details("Überfällige Tasks", overdueList);
        overdueDetails.setOpened(false);

        infoCard.add(description);
        if (!upcomingTasks.isEmpty()) {
            infoCard.add(upcomingDetails);
        }
        if (!overdueTasks.isEmpty()) {
            infoCard.add(overdueDetails);
        }
        add(welcomeBanner, infoCard);
    }
}
