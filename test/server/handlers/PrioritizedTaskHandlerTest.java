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

public class PrioritizedTaskHandlerTest {
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
                LocalDateTime.of(2024, 3, 1, 8, 0, 0),
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
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {

        taskManager.addTask(task);
        taskManager.addTask(task1);

        assertEquals(2, taskManager.getPrioritizedTask().size(), "Должно быть 2 таски");

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код не 200");

        String task2json = response.body();

        class TaskListTypeToken extends TypeToken<List<Task>> {
        }

        List<Task> prioritized = gson.fromJson(task2json, new TaskListTypeToken().getType());
        assertNotNull(prioritized, "Не должно быть пустым");
        assertEquals(2, prioritized.size(), "Количество тасок различается!!");
        client.close();
    }
}