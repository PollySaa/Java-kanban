package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

public class UserHandler extends BaseHttpHandler {

    public UserHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY -> handleHistory(exchange);

            case GET_PRIORITIZED -> handlePrioritize(exchange);

            case UNKNOWN -> sendNotFound(exchange);
        }
    }

    void handleHistory(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getHistoryTasks()));
    }

    void handlePrioritize(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathOfPart = requestPath.split("/");
        String lastPath = pathOfPart[pathOfPart.length - 1];

        if (!requestMethod.equals("GET")) {
            return Endpoint.UNKNOWN;
        }

        if (lastPath.equals("history")) {
            return Endpoint.GET_HISTORY;
        }

        return Endpoint.GET_PRIORITIZED;
    }

    protected enum Endpoint {
        GET_HISTORY,
        GET_PRIORITIZED,
        UNKNOWN
    }
}
