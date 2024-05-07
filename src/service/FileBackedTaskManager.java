package service;

import constants.Status;
import constants.TaskType;
import model.*;
import exceptions.ManagerSaveException;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
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
                    } else if (task instanceof Task) {
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
        } finally {// Меняем ID последнего таска, чтобы новый создался с +1
            changeTotalTask(newId);
        }
    }

    private String taskToString(Task task) {
        TaskType taskType = TaskType.TASK;

        if (task instanceof Epic) {
            taskType = TaskType.EPIC;
        }
        if (task instanceof SubTask) {
            taskType = TaskType.SUBTASK;
        }

        int id = task.getId();
        String name = task.getName();
        String description = task.getDescription();
        Status status = task.getStatus();
        String taskString = "";

        if (task.getClass().equals(Task.class)) {
            taskString = id + "," + taskType + "," + name + "," + status + "," + description + "\n";
        }
        if (task.getClass().equals(Epic.class)) {
            taskString = id + "," + taskType + "," + name + "," + status + "," + description + "\n";
        }
        if (task.getClass().equals(SubTask.class)) {
            SubTask subTask = (SubTask) task;
            int epicId = subTask.getEpicId();
            taskString = id + "," + taskType + "," + name + "," + status + "," + description + "," + epicId + "\n";
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
            String name = tasksTemp[2];
            String description = tasksTemp[4];
            Status status = Status.valueOf(tasksTemp[3]);
            TaskType taskType = TaskType.valueOf(tasksTemp[1]);

            switch (taskType) {
                case TASK:
                    task = new Task(name, description, status, id);
                    break;
                case EPIC:
                    task = new Epic(name, description, status, id);
                    break;
                case SUBTASK:
                    int epicId = Integer.parseInt(tasksTemp[5]);
                    task = new SubTask(name, description, status, id, epicId);
                    break;
            }
        }
        return task;
    }

    private void save() {
        try (BufferedWriter fileWriterBuffer = new BufferedWriter(new FileWriter(file.getPath()))) {
            //Превращаем пришедшую таску в строку и разбираем ее по запятой
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
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла");
        }
    }
}