package model;

import constants.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    protected int epicId;

    public SubTask(String name, String description, Status status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, int id, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    /* (name, description, epicId, LocalDateTime, Duration.ofMinutes*/
    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "description='" + description + '\'' +
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}' + "\n";
    }
}
