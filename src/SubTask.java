
public class SubTask extends Task{
    private int epicId;
    public SubTask(String name, String description) {
        super(name, description);
    }

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
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
                '}';
    }
}
