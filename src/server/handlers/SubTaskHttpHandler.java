package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TimeCrossingException;
import model.SubTask;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubTaskHttpHandler extends BaseHttpHandler {

    public SubTaskHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                try {
                    if (pathParts.length == 2) {
                        String result = gson.toJson(manager.getAllSubTasks());
                        sendText(httpExchange, result, 200);
                        break;
                    }
                    if (pathParts.length == 3) {
                        Optional<Integer> taskId = getTaskId(httpExchange);
                        if (taskId.isPresent() && manager.getSubTaskById(taskId.get()) != null) {
                            String result = gson.toJson(manager.getSubTaskById(taskId.get()));
                            sendText(httpExchange, result, 200);
                        }
                        break;
                    }
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange);
                } catch (NumberFormatException e) {
                    sendText(httpExchange, "ID должны быть цифры", 400);
                }
            case "POST":
                if (pathParts.length == 2) {
                    String requestBody = new String(httpExchange.getRequestBody().readAllBytes());
                    Task task = gson.fromJson(requestBody, SubTask.class);
                    int taskId = task.getId();
                    try {
                        if (taskId == 0) {
                            manager.addTask(task);
                            sendText(httpExchange, "Task added", 201);
                        } else {
                            manager.updateTask(task);
                            sendText(httpExchange, "Task updated", 201);
                        }
                    } catch (TimeCrossingException e) {
                        sendHasInteractions(httpExchange, "Not Acceptable");
                    } catch (IOException e) {
                        sendText(httpExchange, "Internal Server Error", 500);
                    }
                    break;
                }

            case "DELETE":
                if (pathParts.length == 3) {
                    Optional<Integer> taskId = getTaskId(httpExchange);
                    taskId.ifPresent(integer -> manager.dellSubTaskById(integer));
                    sendText(httpExchange, "Task deleted", 200);
                    break;
                }
        }
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            Integer taskId = Integer.parseInt(pathParts[2]);
            return Optional.of(taskId);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}