package components;

import service.Type;

public class Subtask extends Task {
    private final Integer idEpic;

    public Subtask(String taskName, String taskDescription, Integer idEpic) {
        super(taskName, taskDescription);
        this.idEpic = idEpic;
    }

    public Subtask(Integer id, Type type, String taskName, Status status, String taskDescription, Integer idEpic) {
        super(id, type, taskName, status, taskDescription);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getTaskName() + "," + getStatus() + "," + getTaskDescription() + "," +
                getIdEpic();
    }
}

