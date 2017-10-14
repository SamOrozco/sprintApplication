package com.company.sprint;

import com.company.discover.DiscoverCall;
import com.company.models.User;
import com.company.models.request.*;
import com.company.utils.Utils;
import com.company.vote.Vote;
import org.codehaus.jackson.map.ObjectMapper;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.company.utils.ValidationUtils.nullOrEmpty;

public class SprintServer {
    SprintApplication sprintApplication;

    public SprintServer(SprintApplication sprintApplication) throws IOException {
        this.sprintApplication = sprintApplication;
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

        this.startDiscoverInterval(this);
        this.startDiscoverServer(this);
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private void startDiscoverServer(SprintServer sprintServer) {
        final ExecutorService serverExecutor = Executors.newFixedThreadPool(1);
        Runnable udpServerTask = () -> {
            try {
                byte[] contents = new byte[1000];
                InetAddress inetAddress = InetAddress.getByName("localhost");
                DatagramPacket datagramPacket = new DatagramPacket(contents, contents.length, inetAddress, 9776);
                while (true) {
                    DatagramSocket socket = new DatagramSocket(9777);
                    socket.receive(datagramPacket);
                    String data = new String(datagramPacket.getData());
                    data = data.substring(data.indexOf("{"), data.indexOf("}") + 1);
                    ObjectMapper objectMapper = new ObjectMapper();
                    DiscoverCall discoverCall = objectMapper.readValue(data, DiscoverCall.class);
                    sprintServer.sprintApplication.addUser(discoverCall);
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        serverExecutor.submit(udpServerTask);
    }


    public void startDiscoverInterval(SprintServer sprintServer) throws SocketException {
        //data gram socket sending the request
        final DatagramSocket datagramSocket = new DatagramSocket(9776);
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                sprintServer.sendDiscover(sprintServer.sprintApplication, datagramSocket);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 5000);
    }


    private void sendDiscover(SprintApplication sprintApplication, DatagramSocket datagramSocket) {
        //building discover call
        DiscoverCall discoverCall = new DiscoverCall();
        discoverCall.setDateSent(new Date());
        discoverCall.setHost(sprintApplication.ip);
        discoverCall.setLeader(sprintApplication.getCurrentUser().teamLeader);
        discoverCall.setName(sprintApplication.getCurrentUser().name);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = null;
        String baseHost = "";

        //getting ip and parsing json
        baseHost = Utils.getBroadCastIP(sprintApplication.ip);
        try {
            jsonRequest = objectMapper.writeValueAsString(discoverCall);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing discover request");
        }

        //sending requests to base ip + .1 - > .244
        for (int i = 1; i < 255; i++) {
            String tempHost = baseHost + "." + i;
            if (tempHost.equals(sprintApplication.ip)) {
                continue;
            }
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.setHost(tempHost);
            httpRequest.setPath("/discover");
            httpRequest.addHeader("content-type", "json/application");
            httpRequest.setMethod("POST");
            httpRequest.setBody(jsonRequest);
            try {
                String requestString = httpRequest.getHttpRequest();
                InetAddress inetAddress = InetAddress.getByName(tempHost);
                DatagramPacket datagramPacket = new DatagramPacket(requestString.getBytes(), requestString.getBytes().length, inetAddress, 9777);
                datagramSocket.send(datagramPacket);
                Thread.sleep(5);
            } catch (InterruptedException | IOException e) {
            }
        }

    }


    public void startRound(String roundName) throws IOException {
        Collection<User> users = sprintApplication.getUsers().values();
        for (User user : users) {
            String host = user.host;
            InetAddress inetAddress = InetAddress.getByName(host);
            Socket socket = new Socket(inetAddress, 9776);
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.setMethod("GET");
            httpRequest.setPath("/acceptround");
            httpRequest.getHeaders().put("round", roundName);
            httpRequest.sendHttpRequest(socket.getOutputStream(), socket);
        }
        sprintApplication.startRound(roundName);
    }


    private Map<String, RequestHandlerInterface> getRouteHandlers(SprintApplication sprintApplication) {
        RouteHandler routeHandler = new RouteHandler();
        //going to parse the discover call
        //POST discover

        //ACCEPT ROUND
        routeHandler.registerRoute("/acceptround", (customRequest -> {
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


    public void sendVote(int voteValue, SprintApplication sprintApplication) throws IOException {
        Vote sendVote = new Vote();
        if (sprintApplication.getCurrentUser() == null) {
            //TODO INITIALIZE USER
            throw new RuntimeException("User not initalized properly");
        }
        sendVote.name = sprintApplication.getCurrentUser().name;
        sendVote.round = sprintApplication.currentRoundID;
        sendVote.value = voteValue;
        sendVote.voteTime = new Date();

        //adding vote to own application
        sprintApplication.placeVote(sendVote);
        for (User user : sprintApplication.getUsers().values()) {
            user.sendVote(sendVote);
        }
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
                if (inputStream == null) throw new RuntimeException("The incoming socket's input stream was null");
                int length = inputStream.available();
                byte[] contents = IOUtils.readFully(inputStream, length, true);
                String requestString = new String(contents);
                String[] split = requestString.split("\n");
                CustomRequest customRequest = new CustomRequest(split);
                RequestHandler.handleRequest(customRequest, runnableMap);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
