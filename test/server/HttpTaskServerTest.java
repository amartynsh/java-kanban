package server;

import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    TaskManager taskManager = new InMemoryTaskManager();


    @Test
    public void serverCreateTest() throws IOException, InterruptedException {
        HttpTaskServer server = new HttpTaskServer(taskManager);
        assertNotNull(server);
        server.start();
        URI url1 = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/subtasks");
        URI url3 = URI.create("http://localhost:8080/epics");
        URI url4 = URI.create("http://localhost:8080/history");
        URI url5 = URI.create("http://localhost:8080/prioritized");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .GET()
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "Код ответа /tasks не верный");

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode(), "Код ответа /subtasks не верный");

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode(), "Код ответа /epics не верный");

        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url4)
                .GET()
                .build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response4.statusCode(), "Код ответа /history не верный");

        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(url5)
                .GET()
                .build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response5.statusCode(), "Код ответа /prioritized не верный");
        client.close();
        server.stop();
    }
}