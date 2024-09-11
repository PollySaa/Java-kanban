package components;

import service.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer idEpic;

    public Subtask(String taskName, String taskDescription, Integer idEpic) {
        super(taskName, taskDescription);
        this.idEpic = idEpic;
    }

    public Subtask(Type type, String taskName, Status status, String taskDescription,
                   LocalDateTime startTime, Duration duration, Integer idEpic) {
        super(type, taskName, status, taskDescription, startTime, duration);
        this.idEpic = idEpic;
    }

    public Subtask(Integer id, Type type, String taskName, String taskDescription, Status status,
                   LocalDateTime startTime, Duration duration, Integer idEpic) {
        super(id, type, taskName, status, taskDescription, startTime, duration);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getTaskName() + "," + getStatus() + "," + getTaskDescription() + "," +
                getStartTime() + "," + getDuration() + "," + getIdEpic();
    }
}

