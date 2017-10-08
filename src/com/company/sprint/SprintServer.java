package com.company.sprint;

import com.company.models.request.CustomRequest;
import com.company.models.request.RequestHandler;
import com.company.models.request.RequestHandlerInterface;
import com.company.models.request.RouteHandler;
import com.company.discover.DiscoverCall;
import com.company.vote.Vote;
import org.codehaus.jackson.map.ObjectMapper;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.company.utils.ValidationUtils.nullOrEmpty;

public class SprintServer {
    public SprintServer(SprintApplication sprintApplication) throws IOException {
        final ExecutorService serverExecutor = Executors.newFixedThreadPool(5);
        final Map<String, RequestHandlerInterface> runnableMap = getRouteHandlers(sprintApplication);
        Runnable serverTask = () -> {
            try {
                ServerSocket socket = new ServerSocket(9776);
                while (true) {
                    Socket currentSocket = socket.accept();
                    serverExecutor.submit(new ClientTask(currentSocket, runnableMap));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }


    private Map<String, RequestHandlerInterface> getRouteHandlers(SprintApplication sprintApplication) {
        RouteHandler routeHandler = new RouteHandler();
        //going to parse the discover call
        //POST discover
        routeHandler.registerRoute("/discover", (request) -> {
            String jsonBody = request.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            DiscoverCall discoverCall = objectMapper.readValue(jsonBody, DiscoverCall.class);
            if (discoverCall == null) throw new RuntimeException("Json body was empty");
            sprintApplication.addUser(discoverCall);
        });

        //ACCEPT ROUND
        routeHandler.registerRoute("/acceptround", (customRequest -> {
            sprintApplication.closeCurrentRound();
            String roundName = customRequest.getHeaders().get("round");
            if (nullOrEmpty(roundName)) throw new RuntimeException("round value is empty or not found");
            sprintApplication.startRound(roundName);
        }));

        //ACCEPT VOTE
        routeHandler.registerRoute("/acceptvote", (customRequest -> {
            String jsonBody = customRequest.getBody();
            if (jsonBody == null) throw new RuntimeException("jsonbody was null for vote request");
            ObjectMapper objectMapper = new ObjectMapper();
            Vote currentVote = objectMapper.readValue(jsonBody, Vote.class);
            sprintApplication.placeVote(currentVote);
        }));

        //DUMP
        //DUMP VOUTES
        routeHandler.registerRoute("/dumpvotes", (customRequest -> {
            sprintApplication.dumpRoundMap();
        }));

        //DUMP USERS
        routeHandler.registerRoute("/dumpusers", (customRequest -> {
            sprintApplication.dumpUsers();
        }));

        return routeHandler.getRoutes();
    }


    public class ClientTask implements Runnable {
        Socket socket;
        Map<String, RequestHandlerInterface> runnableMap;

        public ClientTask(Socket socket, Map<String, RequestHandlerInterface> runnableMap) {
            if (socket == null || runnableMap == null) {
                throw new RuntimeException("Socket and RunnableMap can't be null");
            }
            this.socket = socket;
            this.runnableMap = runnableMap;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = this.socket.getInputStream();
                if (inputStream == null) throw new RuntimeException("The incoming socket's inout stream was null");
                int length = inputStream.available();
                byte[] contents = IOUtils.readFully(inputStream, length, true);
                String requestString = new String(contents);
                String[] split = requestString.split("\r\n");
                CustomRequest customRequest = new CustomRequest(split);
                RequestHandler.handleRequest(customRequest, runnableMap);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
