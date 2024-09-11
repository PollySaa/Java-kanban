package components;

import service.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private final String taskName;
    private final String taskDescription;
    private Status status;
    private Integer id;
    private Type type;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = Status.NEW;
    }

    public Task(Type type, String taskName, Status status, String taskDescription, LocalDateTime startTime,
                Duration duration) {
        this.type = type;
        this.taskName = taskName;
        this.status = status;
        this.taskDescription = taskDescription;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, Type type, String taskName, Status status, String taskDescription) {
        this.id = id;
        this.type = type;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public Task(Type type, String taskName, Status status, String taskDescription) {
        this.type = type;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public Task(Integer id, Type type, String taskName, Status status, String taskDescription, LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.type = type;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return id + "," + type + "," + taskName + "," + status + "," + taskDescription + "," + startTime + "," +
                duration;
    }
}

