package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager manager) {
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
                        String result = gson.toJson(manager.getAllEpics());
                        sendText(httpExchange, result, 200);
                        break;
                    }
                    if (pathParts.length == 3) {
                        Optional<Integer> taskId = getTaskId(httpExchange);
                        if (taskId.isPresent() && manager.getEpicById(taskId.get()) != null) {
                            String result = gson.toJson(manager.getEpicById(taskId.get()));
                            sendText(httpExchange, result, 200);
                        }
                        break;
                    }
                    if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                        Optional<Integer> taskId = getTaskId(httpExchange);
                        if (taskId.isPresent() && manager.getEpicById(taskId.get()) != null) {
                            String result = gson.toJson(manager.getEpicSubtasks(taskId.get()));
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
                    //Получаем из строки таску
                    Epic epic = gson.fromJson(requestBody, Epic.class);

                    int taskId = epic.getId();
                    try {
                        if (taskId == 0) {
                            manager.addEpic(epic);
                            sendText(httpExchange, "Epic added", 201);
                        } else {
                            manager.updateEpic(epic);
                            sendText(httpExchange, "Epic updated", 201);
                        }
                    } catch (IllegalArgumentException e) {
                        sendHasInteractions(httpExchange, "Not Acceptable");
                    } catch (IOException e) {
                        sendText(httpExchange, "Internal Server Error", 500);
                    }
                    break;
                }

            case "DELETE":
                if (pathParts.length == 3) {
                    Optional<Integer> taskId = getTaskId(httpExchange);
                    taskId.ifPresent(integer -> manager.dellEpicById(integer));
                    sendText(httpExchange, "Epic deleted", 200);
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
