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

class EpicHttpHandlerTest {
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

        URI url = URI.create("http://localhost:8080/epics");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не верный");

        String responseBody = response.body();

        class TaskListTypeToken extends TypeToken<List<Epic>> {
        }

        List<Task> epics = gson.fromJson(responseBody, new TaskListTypeToken().getType());
        assertNotNull(epics, "Не должен быть пустым");
        assertEquals(1, epics.size(), "Неверное количество тасок");
        assertEquals(epics.get(0), epic);
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
        URI url = URI.create("http://localhost:8080/epics/0");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Status code is not 200");
        String jsonBody = response.body();
        Task epicFromRequest = gson.fromJson(jsonBody, Epic.class);
        assertNotNull(epicFromRequest, "Таска пустая!");
        assertEquals(epic, epicFromRequest, "Таски различаются!l");
        client.close();
    }


    @Test
    public void IfEpicNotFound() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/epics/33");
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
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Новая тестовый эпик",
                "Описание тестового эпика", Status.NEW);
        SubTask subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        assertEquals(1, taskManager.getAllEpics().size(), "Должна быть 1 таска");
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Должен быть  200");
        assertEquals(0, taskManager.getAllEpics().size(), "Таска есть");
        client.close();
    }

    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        String epicToJson = "{\n" +
                "\t\t\"name\": \"Очень большая работа 1\",\n" +
                "\t\t\"description\": \"Эта работа делится на 2 подзадачи\",\n" +
                "\t\t\"status\": \"NEW\",\n" +
                "\t  \"duration\": 240,\n" +
                "\t\t\"startTime\": \"2024-05-01 12:00\",\n" +
                "\t  \"endTime\": \"2024-05-01 14:00\"\n" +
                "}";


        URI url = URI.create("http://localhost:8080/epics");


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус код не равен 201");

        assertEquals(1, taskManager.getAllEpics().size(), "Количество тасок отличается !!");
    }

    @Test
    public void shouldAddSubtaskToEpic() throws IOException, InterruptedException {
        String epicToJson = "{\n" +
                "\t\t\"name\": \"Очень большая работа 1\",\n" +
                "\t\t\"description\": \"Эта работа делится на 2 подзадачи\",\n" +
                "\t\t\"status\": \"NEW\",\n" +
                "\t  \"duration\": 240,\n" +
                "\t\t\"startTime\": \"2024-05-01 12:00\",\n" +
                "\t  \"endTime\": \"2024-05-01 14:00\"\n" +
                "}";

        SubTask subTask = new SubTask("Новая тестовая таска",
                "Описание таски", Status.NEW,1,
                LocalDateTime.of(2024, 5, 1, 9, 0, 0),
                Duration.ofMinutes(60));
        String subTaskToJson =gson.toJson(subTask);
        URI url = URI.create("http://localhost:8080/epics");
        URI url2 = URI.create("http://localhost:8080/epics/subtasks");


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestSubtask = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson))
                .build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус код не равен 201");
        assertEquals(200, responseSubtask.statusCode(), "Статус код не равен 200");

        assertEquals(1, taskManager.getAllEpics().size(), "Количество тасок отличается !!");
    }
}