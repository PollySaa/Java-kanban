package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import components.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpTask {

    public SubtaskHandler(Gson gson, TaskManager taskManager, String path) {
        super(gson, taskManager, path);
    }

    @Override
    void handleAll(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtask());
        sendText(exchange, response);
    }

    @Override
    void handleById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> id = getIdFromPath(exchange);

            if (id.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            Subtask subtask = taskManager.getAllSubtasksById(id.get());

            if (subtask == null) {
                sendNotFound(exchange);
                return;
            }

            String response = gson.toJson(subtask, Subtask.class);
            sendText(exchange, response);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    @Override
    void handleAddOrUpdate(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            Optional<Subtask> optionalSubtask = parseSubtask(input);

            if (optionalSubtask.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            Subtask subtask = optionalSubtask.get();

            try {
                if (Optional.ofNullable(subtask.getId()).isPresent()) {
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, "Задача успешно обновлена");
                } else {
                    taskManager.addSubtask(subtask);
                    sendText(exchange, "Задача успешно добавлена");
                }
            } catch (Exception e) {
                sendHasInteractions(exchange);
            }
        }
    }

    @Override
    void handleRemove(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> id = getIdFromPath(exchange);

            if (id.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            taskManager.removeSubtaskById(id.get());
            sendText(exchange, "Задача успешно удалена");
         } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    @Override
    void handleSubtaskOfEpic(HttpExchange exchange) throws IOException {

    }

    private Optional<Subtask> parseSubtask(InputStream bodyInput) throws IOException {
        String body = new String(bodyInput.readAllBytes(), StandardCharsets.UTF_8);

        if (!body.contains("taskName") || !body.contains("taskDescription") || !body.contains("startTime")
            || !body.contains("status") || !body.contains("duration")) {
            return Optional.empty();
        }

        Subtask subtask = gson.fromJson(body, Subtask.class);

        return Optional.of(subtask);
    }

}
