package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

abstract class BaseHttpTask extends BaseHttpHandler {
    protected TaskManager taskManager;
    protected String path;

    BaseHttpTask(Gson gson, TaskManager taskManager, String path) {
        super(gson);
        this.taskManager = taskManager;
        this.path = path;
    }

    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL -> handleAll(exchange);

            case GET_BY_ID -> handleById(exchange);

            case GET_SUBTASK_OF_EPIC -> handleSubtaskOfEpic(exchange);

            case ADD_OR_UPDATE -> handleAddOrUpdate(exchange);

            case DELETE -> handleRemove(exchange);

            case UNKNOWN -> sendNotFound(exchange);
        }
    }

    abstract void handleAll(HttpExchange exchange) throws IOException;

    abstract void handleById(HttpExchange exchange) throws IOException;

    abstract void handleAddOrUpdate(HttpExchange exchange) throws IOException;

    abstract void handleRemove(HttpExchange exchange) throws IOException;

    abstract void handleSubtaskOfEpic(HttpExchange exchange) throws IOException;


    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] partOfPath = requestPath.split("/");
        String lastPath = partOfPath[partOfPath.length - 1];
        switch (requestMethod) {
            case "GET" -> {
                if (lastPath.equals(path)) {
                    return Endpoint.GET_ALL;
                } else if (lastPath.equals("subtasks") && partOfPath[1].equals(path)) {
                    return Endpoint.GET_SUBTASK_OF_EPIC;
                } else {
                    return Endpoint.GET_BY_ID;
                }
            }

            case "POST" -> {
                return Endpoint.ADD_OR_UPDATE;
            }

            case "DELETE" -> {
                return Endpoint.DELETE;
            }

            default -> {
                return Endpoint.UNKNOWN;
            }
        }
    }

    protected enum Endpoint {
        GET_ALL,
        GET_BY_ID,
        GET_SUBTASK_OF_EPIC,
        ADD_OR_UPDATE,
        DELETE,
        UNKNOWN
    }
}
