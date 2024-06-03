package model;

import constants.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    LocalDateTime endTime;


    public Epic(String name, String description, Status status) {
        super(name, description, status);
        endTime = null;
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
        endTime = null;
    }

    public Epic(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        endTime = getEndTime();
    }

    public Epic(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration,
                LocalDateTime endTime) {
        super(name, description, id, status, startTime, duration);
        this.endTime = endTime;
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        endTime = null;
    }


    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}


