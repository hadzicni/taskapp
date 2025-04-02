package com.bbzbl.task.views.home;

import com.bbzbl.task.data.entity.Task;
import com.bbzbl.task.data.entity.User;
import com.bbzbl.task.security.AuthenticatedUser;
import com.bbzbl.task.services.TaskService;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@PermitAll
@PageTitle("Home")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
public class HomeView extends VerticalLayout {

    public HomeView(AuthenticatedUser authenticatedUser, TaskService taskService) {
        String userName = authenticatedUser.get().map(User::getFullName).orElse("Gast");
        List<Task> tasks = taskService.getUserTasks();
        LocalDate today = LocalDate.now();

        setLayoutStyles();
        Div welcomeBanner = createWelcomeBanner(userName);
        Div infoCard = createInfoCard(tasks, today);

        add(welcomeBanner, infoCard);
    }

    private void setLayoutStyles() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)");
    }

    private Div createWelcomeBanner(String userName) {
        Div banner = new Div("Willkommen zurück, " + userName);
        banner.getStyle()
                .set("font-size", "2em")
                .set("font-weight", "bold")
                .set("color", "#ffffff")
                .set("background", "linear-gradient(to right, #007bff, #00bfff)")
                .set("padding", "15px 30px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.15)")
                .set("margin-bottom", "20px")
                .set("text-align", "center");
        return banner;
    }

    private Div createInfoCard(List<Task> tasks, LocalDate today) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set("padding", "25px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 4px 12px rgba(0, 0, 0, 0.1)")
                .set("max-width", "90%")
                .set("width", "100%")
                .set("text-align", "center");

        card.add(createAppDescription());

        List<Task> upcoming = getUpcomingTasks(tasks, today);
        List<Task> overdue = getOverdueTasks(tasks, today);

        Span stats = new Span("Gesamt: " + tasks.size() + " | Bevorstehend: " + upcoming.size() + " | Überfällig: " + overdue.size());
        stats.getStyle()
                .set("font-size", "1em")
                .set("color", "#666")
                .set("margin-bottom", "10px")
                .set("display", "block");
        card.add(stats);

        tasks.stream()
                .filter(t -> t.getDueDate() != null && !"Erledigt".equalsIgnoreCase(t.getStatus()))
                .min(Comparator.comparing(Task::getDueDate))
                .ifPresent(next -> {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);
                    Span nextDeadline = new Span("Nächste Deadline: " + next.getTitle() + " am " + next.getDueDate().format(df));
                    nextDeadline.getStyle()
                            .set("font-weight", "bold")
                            .set("color", "#007bff")
                            .set("margin-bottom", "15px")
                            .set("display", "block");
                    card.add(nextDeadline);
                });

        if (!upcoming.isEmpty()) {
            card.add(createTaskDetails("Bevorstehende Tasks (nächste 3 Tage)", upcoming, "#007bff"));
        }

        if (!overdue.isEmpty()) {
            card.add(createTaskDetails("Überfällige Tasks", overdue, "#dc3545"));
        }

        Anchor allTasksLink = new Anchor("/tasks", "Alle Tasks anzeigen");
        allTasksLink.getStyle()
                .set("display", "inline-block")
                .set("margin-top", "20px")
                .set("color", "#007bff")
                .set("text-decoration", "underline");
        card.add(allTasksLink);

        return card;
    }

    private Span createAppDescription() {
        Span description = new Span("TaskApp by Nikola Hadzic");
        description.getStyle()
                .set("font-size", "1.3em")
                .set("color", "#444")
                .set("font-weight", "500")
                .set("display", "block")
                .set("margin-bottom", "10px");
        return description;
    }

    private Details createTaskDetails(String caption, List<Task> tasks, String color) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);
        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(false);

        tasks.forEach(task -> {
            String dateText = caption.contains("Überfällige") ? "war fällig am" : "fällig am";
            Span item = new Span("• " + task.getTitle() + " (" + dateText + " " + task.getDueDate().format(df) + ")");
            item.getStyle().set("color", color).set("margin-bottom", "5px");
            list.add(item);
        });

        Details details = new Details(caption, list);
        details.setOpened(false);
        return details;
    }

    private List<Task> getUpcomingTasks(List<Task> tasks, LocalDate today) {
        return tasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> !"Erledigt".equalsIgnoreCase(t.getStatus()))
                .filter(t -> !t.getDueDate().isBefore(today) && t.getDueDate().isBefore(today.plusDays(4)))
                .toList();
    }

    private List<Task> getOverdueTasks(List<Task> tasks, LocalDate today) {
        return tasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> !"Erledigt".equalsIgnoreCase(t.getStatus()))
                .filter(t -> t.getDueDate().isBefore(today))
                .toList();
    }
}
