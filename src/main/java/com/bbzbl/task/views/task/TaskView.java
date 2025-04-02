package com.bbzbl.task.views.task;

import com.bbzbl.task.data.entity.Task;
import com.bbzbl.task.services.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * TaskView class for displaying and managing tasks.
 * It provides a grid view of tasks with options to create, edit, delete, and filter tasks.
 */
@PermitAll
@PageTitle("Tasks")
@Route(value = "tasks")
@Menu(order = 0, icon = LineAwesomeIconUrl.BARS_SOLID)
public class TaskView extends VerticalLayout {

    private final TaskService taskService;
    private final Grid<Task> taskGrid = new Grid<>(Task.class, false);

    private Task selectedTask;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);
    DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);

    private final Dialog taskDialog = new Dialog();
    private final Span taskIdSpan = new Span();
    private final TextField titleField = new TextField("Titel");
    private final TextArea descriptionField = new TextArea("Beschreibung");
    private final TextField durationField = new TextField("Dauer (z. B. 2h)");
    private final ComboBox<String> priorityField = new ComboBox<>("Priorität");
    private final Button deleteSelectedButton;
    private final ComboBox<String> statusComboBox = new ComboBox<>("Status");
    private final DatePicker dueDateField = new DatePicker("Fälligkeitsdatum");
    private final TextField searchField;
    private final ComboBox<String> statusFilter;

    /**
     * Constructor for TaskView.
     *
     * @param taskService the service for managing tasks
     */
    public TaskView(TaskService taskService) {
        this.taskService = taskService;

        setSpacing(false);
        setPadding(true);
        setWidth("100%");
        setHeightFull();
        getStyle().set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)");

        searchField = new TextField();
        searchField.setPlaceholder("🔍 Suche nach Titel...");
        searchField.setClearButtonVisible(true);
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> refreshGrid());

        statusFilter = new ComboBox<>("Status");
        statusFilter.setItems("Alle", "Offen", "In Bearbeitung", "Erledigt");
        statusFilter.setValue("Alle");
        statusFilter.setWidth("200px");
        statusFilter.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout rightFilters = new HorizontalLayout(searchField, statusFilter);
        rightFilters.setSpacing(true);
        rightFilters.setAlignItems(Alignment.END);

        Button addTaskButton = new Button("Neuer Task", VaadinIcon.PLUS.create(), event -> openTaskDialog(null));
        addTaskButton.getStyle()
                .set("background-color", "#007bff")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 15px")
                .set("font-weight", "bold");
        deleteSelectedButton = new Button("🗑 Ausgewählte löschen", e -> {
            List<Task> selected = taskGrid.getSelectedItems().stream().toList();
            if (selected.isEmpty()) {
                Notification.show("⚠️ Keine Tasks ausgewählt", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Bestätigen");
            confirmDialog.setText("Wollen Sie wirklich " + selected.size() + " Tasks löschen?");
            confirmDialog.setConfirmText("Löschen");
            confirmDialog.setCancelText("Abbrechen");
            confirmDialog.addConfirmListener(event -> {
                selected.forEach(task -> taskService.deleteTask(task.getId()));
                Notification.show(selected.size() + " Task(s) gelöscht", 3000, Notification.Position.TOP_END);
                refreshGrid();
            });
            confirmDialog.open();
        });
        deleteSelectedButton.getStyle()
                .set("background-color", "#dc3545")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("font-weight", "bold");
        taskGrid.addSelectionListener(event -> deleteSelectedButton.setVisible(!event.getAllSelectedItems().isEmpty()));
        deleteSelectedButton.setVisible(false);

        Button testTasksButton = new Button("⚙ Testdaten", e -> {
            String[] titles = {
                    "Meeting vorbereiten", "Code Review durchführen", "Präsentation erstellen", "Dokumentation schreiben",
                    "Bug fixen", "Feature testen", "E-Mail beantworten", "Deployment vorbereiten", "Sprint Planning", "Daily Standup"
            };

            String[] descriptions = {
                    "Bitte bis morgen erledigen", "Wichtig für das Release", "Kundenrückmeldung berücksichtigen",
                    "Absprache mit dem Team erforderlich", "Deadline beachten", "Technisches Konzept prüfen",
                    "Feedback einholen", "Abstimmung mit Produktmanager", "Tests ergänzen", "Review durch QA-Team"
            };

            String[] durations = {"30min", "1h", "2h", "3h", "Halber Tag", "Ganzer Tag"};
            String[] statuses = {"Offen", "In Bearbeitung", "Erledigt"};
            String[] priorities = {"Niedrig", "Mittel", "Hoch", "Sehr hoch"};

            Random random = new Random();

            for (int i = 0; i < 50; i++) {
                String title = titles[random.nextInt(titles.length)] + " #" + (i + 1);
                String desc = descriptions[random.nextInt(descriptions.length)];
                String duration = durations[random.nextInt(durations.length)];
                String status = statuses[random.nextInt(statuses.length)];
                String priority = priorities[random.nextInt(priorities.length)];
                LocalDate due = LocalDate.now().plusDays(random.nextInt(30) - 10); // ±10 Tage von heute

                taskService.createTask(title, desc, duration, status, priority, due);
            }

            Notification.show("✅ 50 realistische Testtasks erstellt");
            refreshGrid();
        });
        testTasksButton.getStyle().set("margin-right", "auto");

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setPadding(true);
        topBar.setSpacing(true);
        topBar.setAlignItems(Alignment.END);
        topBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        topBar.add(addTaskButton, deleteSelectedButton, testTasksButton, rightFilters);

        setSizeFull();
        taskGrid.setSizeFull();
        add(topBar);
        add(createTaskGrid());
        topBar.getStyle().set("z-index", "1").set("background", "#eef2f3");
        expand(taskGrid);

        setupTaskDialog();
    }

    private Grid<Task> createTaskGrid() {
        taskGrid.setItems(taskService.getUserTasks());
        taskGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        taskGrid.setSizeFull();
        taskGrid.getStyle()
                .set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "10px")
                .set("background-color", "#ffffff")
                .set("overflow", "auto");
        taskGrid.setClassName("task-grid");
        taskGrid.setHeightFull();

        taskGrid.addSelectionListener(event -> {
            deleteSelectedButton.setVisible(!event.getAllSelectedItems().isEmpty());
        });

        taskGrid.addColumn(Task::getTitle).setHeader("Titel").setAutoWidth(true).setSortable(true).setComparator(Task::getTitle);
        taskGrid.addColumn(task -> {
            if (task.getDescription().length() > 50) {
                return task.getDescription().substring(0, 50) + "...";
            } else {
                return task.getDescription();
            }
        }).setHeader("Beschreibung").setAutoWidth(true).setSortable(true);
        taskGrid.addColumn(task -> formatter.format(task.getCreatedAt())).setHeader("Erstellt am").setAutoWidth(true).setSortable(true).setComparator(Task::getCreatedAt);
        taskGrid.addComponentColumn(task -> {
            Span dueDateSpan;

            if (task.getDueDate() != null) {
                LocalDate today = LocalDate.now();
                long daysUntilDue = today.until(task.getDueDate()).getDays();

                dueDateSpan = new Span(dateOnlyFormatter.format(task.getDueDate()));

                if (daysUntilDue <= 3) {
                    dueDateSpan.getStyle()
                            .set("color", "#dc3545")
                            .set("font-weight", "bold");
                } else {
                    dueDateSpan.getStyle()
                            .set("color", "#333");
                }
            } else {
                dueDateSpan = new Span("Nicht festgelegt");
                dueDateSpan.getStyle().set("color", "#999");
            }

            return dueDateSpan;
        }).setHeader("Fälligkeitsdatum").setAutoWidth(true).setSortable(true).setComparator(task -> task.getDueDate() != null ? task.getDueDate() : LocalDate.MAX);

        taskGrid.addComponentColumn(task ->
                setupBadgeEditor(task, "priority", task.getPriority(), List.of("Niedrig", "Mittel", "Hoch", "Sehr hoch"))
        ).setHeader("Priorität").setAutoWidth(true);

        taskGrid.addComponentColumn(task ->
                setupBadgeEditor(task, "status", task.getStatus(), List.of("Offen", "In Bearbeitung", "Erledigt"))
        ).setHeader("Status").setAutoWidth(true);

        taskGrid.addComponentColumn(task -> {
            HorizontalLayout actionLayout = new HorizontalLayout();

            Icon editIcon = new Icon(VaadinIcon.EDIT);
            editIcon.getStyle()
                    .set("cursor", "pointer")
                    .set("margin-right", "8px")
                    .set("color", "#007bff")
                    .set("transition", "0.2s");

            editIcon.addClickListener(e -> openTaskDialog(task));

            Icon deleteIcon = new Icon(VaadinIcon.TRASH);
            deleteIcon.getStyle()
                    .set("cursor", "pointer")
                    .set("color", "#dc3545")
                    .set("transition", "0.2s");

            deleteIcon.addClickListener(e -> openDeleteDialog(task));

            actionLayout.add(editIcon, deleteIcon);
            return actionLayout;
        }).setHeader("Aktionen").setAutoWidth(true);

        taskGrid.addItemDoubleClickListener(event -> openTaskDialog(event.getItem()));
        return taskGrid;
    }

    private void setupTaskDialog() {

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.getStyle()
                .set("background-color", "#ffffff")
                .set("padding", "25px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 6px 15px rgba(0, 0, 0, 0.1)")
                .set("max-width", "480px")
                .set("width", "100%");

        statusComboBox.setPlaceholder("Status auswählen");
        statusComboBox.setItems("Offen", "In Bearbeitung", "Erledigt");
        if (selectedTask == null) {
            statusComboBox.setValue("Offen");
        }

        priorityField.setPlaceholder("Priorität auswählen");
        priorityField.setItems("Niedrig", "Mittel", "Hoch", "Sehr hoch");
        if (selectedTask == null) {
            priorityField.setValue("Mittel");
        }

        titleField.setWidthFull();
        descriptionField.setWidthFull();
        priorityField.setWidthFull();
        durationField.setWidthFull();
        statusComboBox.setWidthFull();
        dueDateField.setWidthFull();

        Button saveButton = new Button("Speichern", e -> {
            if (!titleField.getValue().trim().isEmpty()) {
                if (selectedTask == null) {
                    taskService.createTask(
                            titleField.getValue(),
                            descriptionField.getValue(),
                            durationField.getValue(),
                            statusComboBox.getValue(),
                            priorityField.getValue(),
                            dueDateField.getValue()
                    );
                    Notification.show("Task erstellt!", 3000, Notification.Position.TOP_END);
                } else {
                    taskService.updateTask(
                            selectedTask.getId(),
                            titleField.getValue(),
                            descriptionField.getValue(),
                            durationField.getValue(),
                            statusComboBox.getValue(),
                            priorityField.getValue(),
                            dueDateField.getValue()
                    );
                    Notification.show("Task aktualisiert!", 3000, Notification.Position.TOP_END);
                }
                taskDialog.close();
                refreshGrid();
            } else {
                Notification.show("Titel darf nicht leer sein!", 3000, Notification.Position.TOP_END);
            }
        });
        saveButton.getStyle()
                .set("background", "#007bff")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s");

        saveButton.getStyle()
                .set("background", "#007bff")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s");

        Button cancelButton = new Button("Abbrechen", event -> taskDialog.close());
        cancelButton.getStyle()
                .set("background", "#6c757d")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s");

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        dialogLayout.add(taskIdSpan, titleField, descriptionField, priorityField, durationField, statusComboBox, dueDateField, buttonLayout);
        taskDialog.add(dialogLayout);
    }

    private void openTaskDialog(Task task) {
        if (task == null) {
            taskDialog.setHeaderTitle("Neuer Task");
            selectedTask = null;
            titleField.clear();
            descriptionField.clear();
            durationField.clear();
            priorityField.clear();
            statusComboBox.clear();
            dueDateField.clear();
            statusComboBox.setVisible(false);
            durationField.setVisible(false);
        } else {
            taskDialog.setHeaderTitle("Task bearbeiten");
            selectedTask = task;
            taskIdSpan.setText("ID: " + task.getId());
            titleField.setValue(task.getTitle());
            descriptionField.setValue(task.getDescription());
            durationField.setValue(task.getDuration() != null ? task.getDuration() : "");
            priorityField.setValue(task.getPriority() != null ? task.getPriority() : "Mittel");
            statusComboBox.setValue(task.getStatus() != null ? task.getStatus() : "Offen");
            dueDateField.setValue(task.getDueDate());
            statusComboBox.setVisible(true);
            durationField.setVisible(true);
        }
        taskDialog.open();
    }

    private void openDeleteDialog(Task task) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Task löschen");

        Span confirmText = new Span("Möchten Sie diesen Task wirklich löschen?");
        confirmText.getStyle()
                .set("color", "#333")
                .set("font-size", "1em");

        Button deleteButton = new Button("Löschen", e -> {
            taskService.deleteTask(task.getId());
            Notification.show("Task gelöscht!", 3000, Notification.Position.TOP_END);
            refreshGrid();
            dialog.close();
        });
        deleteButton.getStyle()
                .set("background", "#dc3545")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s");

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());
        cancelButton.getStyle()
                .set("background", "#6c757d")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "bold")
                .set("transition", "0.3s");

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, deleteButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout(confirmText, buttonLayout);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void refreshGrid() {
        String keyword = searchField.getValue() != null ? searchField.getValue().toLowerCase() : "";
        String selectedStatus = statusFilter.getValue();

        taskGrid.setItems(taskService.getUserTasks().stream()
                .filter(task -> task.getTitle().toLowerCase().contains(keyword))
                .filter(task -> selectedStatus.equals("Alle") || task.getStatus().equals(selectedStatus))
                .toList()
        );
    }

    private Span setupBadgeEditor(Task task, String field, String value, List<String> options) {
        Span badge = new Span(value != null ? value : "Keine");
        badge.getStyle()
                .set("border-radius", "999px")
                .set("padding", "4px 12px")
                .set("font-size", "0.8em")
                .set("font-weight", "600")
                .set("color", "white")
                .set("cursor", "pointer");

        switch (value) {
            case "Niedrig" -> badge.getStyle().set("background-color", "#28a745");
            case "Mittel" -> badge.getStyle().set("background", "#ffc107").set("color", "#333");
            case "Hoch" -> badge.getStyle().set("background-color", "#007bff");
            case "Sehr hoch" -> badge.getStyle().set("background-color", "#dc3545");
            case "Offen" -> badge.getStyle().set("background-color", "#28a745");
            case "In Bearbeitung" -> badge.getStyle().set("background-color", "#ffc107").set("color", "#333");
            case "Erledigt" -> badge.getStyle().set("background-color", "#17a2b8");
            default -> badge.getStyle().set("background-color", "#6c757d");
        }

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(options);
        comboBox.setValue(value);
        comboBox.setWidth("160px");

        comboBox.addValueChangeListener(e -> {
            if (e.getValue() != null && !e.getValue().equals(value)) {
                if (field.equals("status")) {
                    task.setStatus(e.getValue());
                } else if (field.equals("priority")) {
                    task.setPriority(e.getValue());
                }
                taskService.updateTask(task.getId(), task.getTitle(), task.getDescription(), task.getDuration(), task.getStatus(), task.getPriority(), task.getDueDate());
                Notification.show("Task aktualisiert", 2000, Notification.Position.TOP_END);
                taskGrid.getDataProvider().refreshItem(task);
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOpenOnClick(true);
        contextMenu.setTarget(badge);
        contextMenu.add(comboBox);

        return badge;
    }

}