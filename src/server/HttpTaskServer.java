package server;

import com.sun.net.httpserver.HttpServer;
import exceptions.ManagerSaveException;
import manager.Managers;
import server.handlers.*;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private static final File file = new File("taskStorage.txt");
    HttpServer httpServer;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        int port  = 8080;
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TaskHttpHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTaskHttpHandler(taskManager));
        httpServer.createContext("/epics", new EpicHttpHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedTaskHandler(taskManager));
    }

    public void start() {
        httpServer.start();
        System.out.println("Server started at port: 8080");
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) throws ManagerSaveException, IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer restServer = new HttpTaskServer(taskManager);

        // запускаем сервер
        restServer.start();
    }
}