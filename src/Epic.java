public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }
}

