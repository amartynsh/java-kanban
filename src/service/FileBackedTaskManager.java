package service;

import constants.Status;
import constants.TaskType;
import model.*;
import exceptions.ManagerSaveException;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private DateTimeFormatter formatter;

    public FileBackedTaskManager(File file) {
        this.file = file;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.loadBackup();
        return taskManager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void dellSubTaskById(int idSubTask) {
        super.dellSubTaskById(idSubTask);
        save();
    }

    @Override
    public void dellAllSubTasks() {
        super.dellAllSubTasks();
        save();
    }

    @Override
    public void dellAllEpics() {
        super.dellAllEpics();
        save();
    }

    @Override
    public void dellEpicById(int idEpic) {
        super.dellEpicById(idEpic);
        save();
    }

    @Override
    public void delTaskById(int idTask) {
        super.delTaskById(idTask);
        save();
    }

    @Override
    public void delAllTask() {
        super.delAllTask();
        save();
    }

    private void loadBackup() {
        Task task = null;
        int newId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file.getPath()))) {
            reader.readLine(); // Пропускаем первую строку
            String line = reader.readLine(); // Читаем первую строку
            // Пошли циклом по строкам
            while (line != null) {
                task = fromString(line);
                if (task != null) {
                    if (task.getId() > newId) {
                        newId = task.getId();
                    }
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof SubTask) {
                        subTasks.put(task.getId(), (SubTask) task);
                    } else {
                        tasks.put(task.getId(), task);
                    }
                    //По ТЗ - Добавляем в историю просмотра все таски, что не в состоянии NEW
                    if (task.getStatus() == Status.DONE || task.getStatus() == Status.IN_PROGRESS) {
                        historyManager.add(task);
                    }
                    //читаем следующую строку
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        } finally {
            // Меняем ID последнего таска, чтобы новый создался с +1
            changeTotalTask(newId);
        }
    }

    private String taskToString(Task task) {
        TaskType taskType = TaskType.TASK;

        int id = task.getId();
        String name = task.getName();
        Status status = task.getStatus();
        String description = task.getDescription();
        String startTime = "";
        String duration = "";
        if (task.getStartTime() != null) {
            startTime = formatter.format(task.getStartTime());
        }
        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }
        String endTime = "";

        if (task instanceof Epic) {
            taskType = TaskType.EPIC;
            if (task.getEndTime() != null) {
                endTime = formatter.format(task.getEndTime());
            }
        }
        if (task instanceof SubTask) {
            taskType = TaskType.SUBTASK;
        }

        String taskString = "";
        StringBuilder builder = new StringBuilder();

        builder.append(id).append(",")
                .append(taskType).append(",")
                .append(name).append(",")
                .append(status).append(",")
                .append(description).append(",")
                .append(startTime).append(",")
                .append(duration).append(",");

        if (task.getClass().equals(Task.class)) {
            builder.append("\n");
            taskString = builder.toString();
        }

        if (task.getClass().equals(Epic.class)) {
            builder.append(endTime).append(",").append("\n");
            taskString = builder.toString();
        }
        if (task.getClass().equals(SubTask.class)) {
            SubTask subTask = (SubTask) task;
            int epicId = subTask.getEpicId();
            builder.append(epicId).append(",").append("\n");
            taskString = builder.toString();

        }
        return taskString;
    }

    private Task fromString(String lineFromFile) {
        Task task = null;
        if (lineFromFile != null) {
            String[] tasksTemp = lineFromFile.split(",");
            //String name, String description, Status status, int id Конструктор Task
            //Порядок в файле id, type,  name,  description, status
            int id = Integer.parseInt(tasksTemp[0]);
            TaskType taskType = TaskType.valueOf(tasksTemp[1]);
            String name = tasksTemp[2];
            Status status = Status.valueOf(tasksTemp[3]);
            String description = tasksTemp[4];
            LocalDateTime startTime = LocalDateTime.parse(tasksTemp[5], formatter);
            Duration duration = Duration.ofMinutes(Long.parseLong(tasksTemp[6]));


            switch (taskType) {
                case TASK:
                    task = new Task(name, description, id, status, startTime, duration);
                    break;
                case EPIC:
                    LocalDateTime endTime = LocalDateTime.parse(tasksTemp[7], formatter);
                    task = new Epic(name, description, id, status, startTime, duration, endTime);

                    break;
                case SUBTASK:
                    int epicId = Integer.parseInt(tasksTemp[7]);
                    task = new SubTask(name, description, status, id, epicId, startTime, duration);
                    break;
            }
        }
        return task;
    }

    private void save() {

        try (BufferedWriter fileWriterBuffer = new BufferedWriter(new FileWriter(file.getPath()))) {

            //Превращаем пришедшую таску в строку и разбираем ее по запятой
            // (id).(",").(taskType).(",").(name).(",").(status).(",").(description).(",").(startTime).(",").(duration).(",");
            fileWriterBuffer.write("id,type,name,status,description,epic" + "\n");

            for (Task task : tasks.values()) {
                String taskToWrite = taskToString(task);
                fileWriterBuffer.append(taskToWrite);
            }

            for (Epic epic : epics.values()) {
                String taskToWrite = taskToString(epic);
                fileWriterBuffer.append(taskToWrite);
            }

            for (SubTask subTask : subTasks.values()) {
                String taskToWrite = taskToString(subTask);
                fileWriterBuffer.append(taskToWrite);
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка");
        }
    }
}