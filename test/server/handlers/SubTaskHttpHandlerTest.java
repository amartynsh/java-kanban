package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import constants.Status;
import model.Epic;
import model.SubTask;
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

class SubTaskHttpHandlerTest {
    TaskManager taskManager;
    HttpTaskServer server;
    HttpClient client;
    Gson gson;

    @BeforeEach
    public void start() throws IOException {

        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
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
        Epic epic = new Epic("Новая тестовый эпик",
                "Описание тестового эпика", Status.NEW);
        SubTask subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60));


        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не верный");

        String responseBody = response.body();

        class TaskListTypeToken extends TypeToken<List<SubTask>> {
        }

        List<Task> subTasks = gson.fromJson(responseBody, new TaskListTypeToken().getType());
        assertNotNull(subTasks, "Не должен быть пустым");
        assertEquals(1, subTasks.size(), "Неверное количество тасок");
        assertEquals(subTasks.get(0), subTask);
        client.close();
    }

    @Test
    public void tryGetTaskById() throws InterruptedException, IOException {
        Epic epic = new Epic("Новая тестовый эпик",
                "Описание тестового эпика", Status.NEW);
        SubTask subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60));

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        URI url = URI.create("http://localhost:8080/subtasks/1");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");
        String jsonBody = response.body();
        Task subTaskFromRequest = gson.fromJson(jsonBody, SubTask.class);
        assertNotNull(subTaskFromRequest, "Таска пустая!");
        assertEquals(subTask, subTaskFromRequest, "Таски различаются!l");
        client.close();
    }


    @Test
    public void IfTaskNotFound() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/subtasks/33");
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
        Epic epic = new Epic("Новая тестовый эпик",
                "Описание тестового эпика", Status.NEW);
        SubTask subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60));

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");
        String taskToJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статус код не равен 406");

        assertEquals(1, taskManager.getAllSubTasks().size(), "Количество тасок отличается !!");
    }

    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Новая тестовый эпик",
                "Описание тестового эпика", Status.NEW);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60));
        taskManager.addSubTask(subTask);

        assertEquals(1, taskManager.getAllSubTasks().size(), "Должна быть 1 таска");
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Должен быть  200");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Таска есть");
        client.close();
    }
}