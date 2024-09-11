package components;

import service.Type;

public class Task {
    private final String taskName;
    private final String taskDescription;
    private Status status;
    private Integer id;
    private Type type;

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = Status.NEW;
    }

    public Task(Integer id, Type type, String taskName, Status status, String taskDescription) {
        this.id = id;
        this.type = type;
        this.taskName = taskName;
        this.status = status;
        this.taskDescription = taskDescription;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return id + "," + type + "," + taskName + "," + status + "," + taskDescription;
    }
}

