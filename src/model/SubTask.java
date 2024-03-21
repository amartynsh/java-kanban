package model;

import constants.Status;

import java.util.Objects;

public class SubTask extends Task{
    public int epicId;

    public SubTask(String name, String description, int epicId ) {
        super(name, description);
        this.epicId = epicId;
    }
    public SubTask(String name, String description, Status status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "subTask{" + "id='" + super.getId() +
                "', name='" +super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() + ", epic=" + getEpicId() +
                '}' + '\n';
    }

/*    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }*/

 /*   @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }*/
}
