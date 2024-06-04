package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import constants.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationTypeAdapter;
import server.adapters.LocalDateTimeAdapterType;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskHttpHandlerTest {
    TaskManager taskManager;
    HttpTaskServer server;
    HttpClient client;
    Gson gson;
    Task task;
    Task task1;

    @BeforeEach
    public void start() throws IOException {

        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
        task = new Task("Новая тестовая задача",
                "Описание тестовой задачи",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 8, 0, 0),
                Duration.ofMinutes(60));

        task1 = new Task("Новая тестовая задача1",
                "Описание тестовой задачи1",
                Status.NEW,
                LocalDateTime.of(2024, 5, 1, 8, 0, 0),
                Duration.ofMinutes(60));

        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapterType())
                .create();
    }

    @AfterEach
    public void stop() {
        server.stop();
    }

    @Test
    public void MustGetTasksList() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не верный");

        String responseBody = response.body();

        class TaskListTypeToken extends TypeToken<List<Task>> {
        }

        List<Task> tasks = gson.fromJson(responseBody, new TaskListTypeToken().getType());
        assertNotNull(tasks, "Не должен быть пустым");
        assertEquals(1, tasks.size(), "Неверное количество тасок");
        assertEquals(tasks.get(0), task);
        client.close();
    }

    @Test
    public void tryGetTaskById() throws InterruptedException, IOException {
        taskManager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/0");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");
        String jsonBody = response.body();
        Task taskFromRequest = gson.fromJson(jsonBody, Task.class);
        assertNotNull(taskFromRequest, "Таска пустая!");
        assertEquals(task, taskFromRequest, "Таски различаются!l");
        client.close();
    }

    @Test
    public void checkIncorrectParams() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks/abc");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Код должен быть 400");

        client.close();
    }

    @Test
    public void IfTaskNotFound() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/tasks/33");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный статус код, должен быть  404");
        client.close();
    }

    @Test
    public void shouldNotCreateTaskIfDateTimeCrossed() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks");
        String taskToJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Статус код не равен 406");
        assertEquals(1, taskManager.getAllTask().size(), "Количество тасок отличается !!");
    }

    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        taskManager.addTask(task);
        assertEquals(1, taskManager.getAllTask().size(), "Должна быть 1 таска");
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Должен быть  200");
        assertEquals(0, taskManager.getAllTask().size(), "Таска есть");
        client.close();
    }
}