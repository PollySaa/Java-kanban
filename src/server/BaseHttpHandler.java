package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;
    protected TaskManager taskManager;

    public BaseHttpHandler(Gson gson) {
        this.gson = gson;
    }

    public BaseHttpHandler(Gson gson, TaskManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        String response = "{\"status\":\"error\",\"message\":\"object not found\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        String response = "{\"status\":\"error\",\"message\":\"task has interactions\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void writeResponse(Object body, HttpExchange exchange) throws IOException {
        String responseJson = gson.toJson(body);
        byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }

    protected String readBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = isr.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(path[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}

