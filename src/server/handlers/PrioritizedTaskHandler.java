package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;
import java.io.IOException;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    public PrioritizedTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            String prioritizedTaskToJson = gson.toJson(manager.getPrioritizedTask());
            sendText(httpExchange, prioritizedTaskToJson, 200);
        }
    }
}