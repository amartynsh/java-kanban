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



public class HistoryHandlerTest {
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
    public void getHistory() throws IOException, InterruptedException {
        taskManager.addTask(task);
        taskManager.addTask(task1);

        assertEquals(0, taskManager.getHistory().size(), "История пуста");
        taskManager.getTaskById(task.getId());

        assertEquals(1, taskManager.getHistory().size(), "В истории должно быть 1 значение");

        //Получаем историю через GET запрос
        URI url = URI.create("http://localhost:8080/history");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> responseBody = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseBody.statusCode(), "Код не 200");

        //Дергаем методом вторую таску
        URI url2 = URI.create("http://localhost:8080/tasks/1");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> responseGetTask = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGetTask.statusCode(), "Код не 200");

        String jsonBody1 = responseBody.body();


        class TaskListTypeToken extends TypeToken<List<Task>> {
        }

        //Получаем историю
        List<Task> history = gson.fromJson(jsonBody1, new TaskListTypeToken().getType());
        assertEquals(1, history.size(), "Количество тасок отличается!");

        //Проверяем что таска создалась
        Task newTask = gson.fromJson(responseGetTask.body(), Task.class);
        assertEquals(task1, newTask, "Таски различаются!");

        //Получаем историю через GET запрос
        responseBody = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseBody.statusCode(), "Код не 200");
        jsonBody1 = responseBody.body();
        history = gson.fromJson(jsonBody1, new TaskListTypeToken().getType());
        assertEquals(2, history.size(), "Количество тасок отличается!");

        client.close();
    }


}