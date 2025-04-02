package com.bbzbl.task.views.task;

import com.bbzbl.task.data.entity.Task;
import com.bbzbl.task.services.TaskService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
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
import java.util.Optional;
import java.util.Set;

/**
 * The KanbanView class represents the main view for the task board.
 * It provides functionalities to create, update, delete, and display tasks in a Kanban board layout.
 */
@PermitAll
@PageTitle("Task Board")
@Route(value = "board")
@Menu(order = 1, icon = LineAwesomeIconUrl.BARS_SOLID)
public class KanbanView extends VerticalLayout {

    private final TaskService taskService;
    private final Set<String> availableStatuses = Set.of("Offen", "In Bearbeitung", "Erledigt");

    private final Dialog taskDialog = new Dialog();
    private final Span taskIdSpan = new Span();
    private final TextField titleField = new TextField("Titel");
    private final TextArea descriptionField = new TextArea("Beschreibung");
    private final ComboBox<String> priorityField = new ComboBox<>("Priorität");
    private final TextField durationField = new TextField("Dauer (z. B. 2h)");
    private final ComboBox<String> statusComboBox = new ComboBox<>("Status");
    private final DatePicker dueDateField = new DatePicker("Fälligkeitsdatum");
    private final TextField searchField;
    private Task selectedTask;

    private final Div todoTasks;
    private final Div inProgressTasks;
    private final Div doneTasks;

    private Span todoHeader;
    private Span inProgressHeader;
    private Span doneHeader;

    /**
     * Constructor for KanbanView.
     * Initializes the task board view and sets up the UI components.
     *
     * @param taskService the service to manage tasks
     */
    public KanbanView(TaskService taskService) {
        this.taskService = taskService;

        setSpacing(false);
        setPadding(true);
        setWidth("100%");
        getStyle().set("background", "linear-gradient(to right, #eef2f3, #dfe9f3)");

        setupTaskDialog();

        Span title = new Span("Task Board");
        title.getStyle()
                .set("font-size", "2em")
                .set("color", "#007bff")
                .set("font-weight", "bold")
                .set("margin-bottom", "20px")
                .set("text-align", "center")
                .set("width", "100%");

        Button addTaskButton = new Button("Neuer Task", VaadinIcon.PLUS.create(), e -> openTaskDialog(null));
        addTaskButton.getStyle()
                .set("background-color", "#007bff")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 15px")
                .set("font-weight", "bold");

        searchField = new TextField();
        searchField.setPlaceholder("🔍 Suche nach Titel...");
        searchField.setClearButtonVisible(true);
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> loadTasks());

        HorizontalLayout topBar = new HorizontalLayout(addTaskButton, searchField);
        topBar.setWidthFull();
        topBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        topBar.setAlignItems(Alignment.END);
        topBar.setPadding(true);

        add(topBar);

        HorizontalLayout kanbanLayout = new HorizontalLayout();
        kanbanLayout.setWidthFull();
        kanbanLayout.setSpacing(true);
        kanbanLayout.getStyle()
                .set("gap", "20px")
                .set("align-items", "flex-start");

        VerticalLayout todoColumn = new VerticalLayout();
        ColumnElements todo = setupColumn(todoColumn, "📋 Offen", "Offen", "#28a745");
        todoHeader = todo.header;
        todoTasks = todo.container;

        VerticalLayout inProgressColumn = new VerticalLayout();
        ColumnElements inProgress = setupColumn(inProgressColumn, "⏳ In Bearbeitung", "In Bearbeitung", "#ffc107");
        inProgressHeader = inProgress.header;
        inProgressTasks = inProgress.container;

        VerticalLayout doneColumn = new VerticalLayout();
        ColumnElements done = setupColumn(doneColumn, "✅ Erledigt", "Erledigt", "#007bff");
        doneHeader = done.header;
        doneTasks = done.container;

        kanbanLayout.add(todoColumn, inProgressColumn, doneColumn);
        kanbanLayout.setFlexGrow(1, todoColumn, inProgressColumn, doneColumn);
        add(title, kanbanLayout);

        loadTasks();
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
                refreshTasks();
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
            refreshTasks();
            Notification.show("Task gelöscht!", 3000, Notification.Position.TOP_END);
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

    private ColumnElements setupColumn(VerticalLayout columnLayout, String title, String status, String color) {
        columnLayout.setWidth("100%");
        columnLayout.setAlignItems(Alignment.CENTER);

        Span header = new Span(title);
        if (status.equals("In Bearbeitung")) {
            header.getStyle().set("color", "#333");
        } else {
            header.getStyle().set("color", "white");
        }
        header.getStyle()
                .set("font-size", "1.3em")
                .set("font-weight", "600")
                .set("background", color)
                .set("padding", "12px 0")
                .set("border-radius", "8px")
                .set("width", "100%")
                .set("text-align", "center")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)")
                .set("margin-bottom", "15px");

        Div taskContainer = new Div();
        taskContainer.getElement().setAttribute("status", status);
        taskContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "10px")
                .set("padding", "20px")
                .set("width", "100%")
                .set("min-height", "400px")
                .set("box-shadow", "0px 4px 10px rgba(0, 0, 0, 0.1)")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center");

        columnLayout.add(header, taskContainer);

        DropTarget<Div> dropTarget = DropTarget.create(taskContainer);
        taskContainer.getElement().executeJs("""
    addEventListener('dragenter', () => this.style.backgroundColor = '#f0f0f0');
    addEventListener('dragleave', () => this.style.backgroundColor = 'white');
""");

        dropTarget.addDropListener(event -> {
            Component dragSource = event.getDragSourceComponent().orElse(null);
            if (dragSource instanceof Div draggedCard) {
                String taskId = draggedCard.getElement().getAttribute("task-id");
                if (taskId != null) {
                    taskService.getTaskById(Long.valueOf(taskId)).ifPresent(task -> {
                        String newStatus = taskContainer.getElement().getAttribute("status");
                        if (newStatus != null && availableStatuses.contains(newStatus) && !newStatus.equals(task.getStatus())) {
                            task.setStatus(newStatus);
                            taskService.updateTask(task.getId(), task.getTitle(), task.getDescription(), task.getDuration(), newStatus, task.getPriority(), task.getDueDate());

                            Optional<Component> optionalParent = draggedCard.getParent();
                            if (optionalParent.isPresent() && optionalParent.get() instanceof Div parentContainer) {
                                parentContainer.remove(draggedCard);
                                taskContainer.add(draggedCard);
                            }

                            Notification.show("Task verschoben nach „" + newStatus + "“", 2000, Notification.Position.TOP_END);
                        }
                    });
                }
                taskContainer.getStyle().set("background-color", "white");
            }
            todoHeader.setText("📋 Offen (" + todoTasks.getComponentCount() + ")");
            inProgressHeader.setText("⏳ In Bearbeitung (" + inProgressTasks.getComponentCount() + ")");
            doneHeader.setText("✅ Erledigt (" + doneTasks.getComponentCount() + ")");
        });

        return new ColumnElements(header, taskContainer);
    }

    private Div createTaskCard(Task task) {
        Div card = new Div();
        card.getElement().setAttribute("task-id", String.valueOf(task.getId()));
        card.getElement().executeJs("""
    addEventListener('dragstart', function (event) {
        event.dataTransfer.setDragImage(this, 50, 20);
    });
""");
        card.getElement().executeJs("""
    addEventListener('dragstart', () => this.style.opacity = '0.6');
    addEventListener('dragend', () => this.style.opacity = '1');
""");


        card.getStyle()
                .set("background", "white")
                .set("padding", "16px")
                .set("margin-bottom", "12px")
                .set("border-radius", "12px")
                .set("box-shadow", "0px 2px 5px rgba(0, 0, 0, 0.1)")
                .set("cursor", "grab")
                .set("transition", "box-shadow 0.3s ease-in-out")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        card.addClickListener(event -> {
            todoTasks.getChildren().forEach(c -> c.getElement().getStyle().remove("background-color"));
            inProgressTasks.getChildren().forEach(c -> c.getElement().getStyle().remove("background-color"));
            doneTasks.getChildren().forEach(c -> c.getElement().getStyle().remove("background-color"));

            card.getStyle().set("background-color", "#e9f5ff");
        });

        card.getElement().executeJs("""
    addEventListener('mouseover', () => this.style.boxShadow='0px 5px 15px rgba(0, 0, 0, 0.2)');
    addEventListener('mouseout', () => this.style.boxShadow='0px 2px 5px rgba(0, 0, 0, 0.1)');
""");


        Span title = new Span(task.getTitle());
        title.getStyle()
                .set("font-weight", "bold")
                .set("display", "block")
                .set("width", "100%");

        Span description = new Span(task.getDescription());
        description.getStyle()
                .set("color", "#666")
                .set("font-size", "0.9em")
                .set("display", "block")
                .set("width", "100%");

        DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);
        LocalDate heute = LocalDate.now();

        Span dueDate;
        if (task.getDueDate() != null) {
            long daysUntilDue = heute.until(task.getDueDate()).getDays();
            dueDate = new Span("Fällig: " + dateOnlyFormatter.format(task.getDueDate()));

            if (daysUntilDue <= 3) {
                dueDate.getStyle()
                        .set("color", "#dc3545")
                        .set("font-weight", "bold")
                        .set("font-size", "0.9em");
            } else {
                dueDate.getStyle()
                        .set("color", "#999")
                        .set("font-size", "0.8em");
            }
        } else {
            dueDate = new Span("Fällig: Nicht festgelegt");
            dueDate.getStyle()
                    .set("color", "#999")
                    .set("font-size", "0.8em");
        }

        Span duration;
        if (task.getDuration() != null && !task.getDuration().isBlank()) {
            duration = new Span("Dauer: " + task.getDuration());
        } else {
            duration = new Span("Dauer: Nicht festgelegt");
        }
        duration.getStyle()
                .set("color", "#666")
                .set("font-size", "0.8em")
                .set("display", "block")
                .set("width", "100%");

        Icon deleteIcon = new Icon(VaadinIcon.TRASH);
        deleteIcon.getElement().setAttribute("title", "Löschen");
        deleteIcon.getStyle().set("cursor", "pointer").set("color", "#dc3545");
        deleteIcon.addClickListener(e -> openDeleteDialog(task));

        Icon editIcon = new Icon(VaadinIcon.EDIT);
        editIcon.getElement().setAttribute("title", "Bearbeiten");
        editIcon.getStyle().set("cursor", "pointer");
        editIcon.addClickListener(e -> openTaskDialog(task));

        editIcon.getStyle().set("cursor", "pointer").set("color", "#007bff");
        editIcon.getElement().executeJs("""
    addEventListener('mouseover', () => this.style.color='#0056b3');
    addEventListener('mouseout', () => this.style.color='#007bff');
""");

        deleteIcon.getStyle().set("cursor", "pointer").set("color", "#dc3545");
        deleteIcon.getElement().executeJs("""
    addEventListener('mouseover', () => this.style.color='#a71d2a');
    addEventListener('mouseout', () => this.style.color='#dc3545');
""");

        HorizontalLayout header = new HorizontalLayout(title, editIcon, deleteIcon);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span priorityBadge = new Span(task.getPriority() != null ? task.getPriority() : "Keine Priorität");
        priorityBadge.getStyle()
                .set("border-radius", "999px")
                .set("padding", "4px 12px")
                .set("font-size", "0.8em")
                .set("font-weight", "600")
                .set("color", "white")
                .set("margin-top", "8px");

        String priority = task.getPriority();
        if (priority == null) {
            priority = "";
        }

        switch (priority) {
            case "Niedrig" -> priorityBadge.getStyle().set("background", "#28a745");
            case "Mittel" -> priorityBadge.getStyle().set("background", "#ffc107").set("color", "#333");
            case "Hoch" -> priorityBadge.getStyle().set("background", "#007bff");
            case "Sehr hoch" -> priorityBadge.getStyle().set("background", "#dc3545");
            default -> priorityBadge.getStyle().set("background", "#999");
        }

        HorizontalLayout footer = new HorizontalLayout(priorityBadge);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);
        footer.setPadding(false);
        footer.setSpacing(false);
        footer.getStyle().set("margin-top", "auto");

        card.add(header, description, dueDate, duration, footer);

        DragSource<Div> dragSource = DragSource.create(card);
        dragSource.setDraggable(true);

        return card;
    }

    private void loadTasks() {
        if (searchField == null) return;

        todoTasks.removeAll();
        inProgressTasks.removeAll();
        doneTasks.removeAll();

        List<Task> tasks = taskService.getUserTasks();

        String keyword = searchField.getValue() != null ? searchField.getValue().toLowerCase() : "";

        tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(keyword))
                .forEach(task -> {
                    Div card = createTaskCard(task);
                    switch (task.getStatus()) {
                        case "Offen" -> todoTasks.add(card);
                        case "In Bearbeitung" -> inProgressTasks.add(card);
                        case "Erledigt" -> doneTasks.add(card);
                    }
                });

        todoHeader.setText("📋 Offen (" + todoTasks.getComponentCount() + ")");
        inProgressHeader.setText("⏳ In Bearbeitung (" + inProgressTasks.getComponentCount() + ")");
        doneHeader.setText("✅ Erledigt (" + doneTasks.getComponentCount() + ")");

    }

    private void refreshTasks() {
        getUI().ifPresent(ui -> ui.access(this::loadTasks));
    }

    private static class ColumnElements {
        Span header;
        Div container;

        ColumnElements(Span header, Div container) {
            this.header = header;
            this.container = container;
        }
    }
}