package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import components.Epic;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpTask {

    public EpicHandler(Gson gson, TaskManager taskManager, String path) {
        super(gson, taskManager, path);
    }

    @Override
    void handleAll(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpic());
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

            Epic epic = taskManager.getAllEpicsById(id.get());

            if (epic == null) {
                sendNotFound(exchange);
                return;
            }

            String response = gson.toJson(epic, Epic.class);
            sendText(exchange, response);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    @Override
    void handleAddOrUpdate(HttpExchange exchange) throws IOException {
        try(InputStream input = exchange.getRequestBody()) {
            Optional<Epic> optionalEpic = parseEpic(input);

            if (optionalEpic.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            Epic epic = optionalEpic.get();

            try {
                if (Optional.ofNullable(epic.getId()).isPresent()) {
                    taskManager.updateEpic(epic);
                    sendText(exchange, "Задача успешно обновлена");
                } else {
                    taskManager.addEpic(epic);
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

            taskManager.removeEpicById(id.get());
            sendText(exchange, "Задача успешно удалена");
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    @Override
    void handleSubtaskOfEpic(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> id = getIdFromPath(exchange);
            if (id.isEmpty()) {
                sendNotFound(exchange);
                return;
            }

            Integer epicId = id.get();


            String responseString = gson.toJson(taskManager.getSubtasksByIdEpic(taskManager.getAllEpicsById(epicId)));
            sendText(exchange, responseString);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    private Optional<Epic> parseEpic(InputStream bodyInput) throws IOException {
        String body = new String(bodyInput.readAllBytes(), StandardCharsets.UTF_8);

        if (!body.contains("taskName") || !body.contains("taskDescription") || !body.contains("status")) {
            return  Optional.empty();
        }

        Epic epic = gson.fromJson(body, Epic.class);

        return Optional.of(epic);
    }
}
