package com.company.sprint;

import com.company.models.request.CustomRequest;
import com.company.models.request.RequestHandler;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SprintServer {
    public SprintServer() throws IOException {
        final ExecutorService serverExecutor = Executors.newFixedThreadPool(5);
        final Map<String, Runnable> runnableMap = getRouteHandlers();
        Runnable serverTask = () -> {
            try {
                ServerSocket socket = new ServerSocket(9776);
                System.out.println("Waiting for requests");
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


    private Map<String, Runnable> getRouteHandlers() {
        Map<String, Runnable> tempMap = new HashMap<>();

        final String test = "/test";
        Runnable testRunnable = () -> {
            System.out.println("Test");
        };

        final String discover = "/discover";
        Runnable discoverRunnable = () -> {
            System.out.println("discover");
        };

        tempMap.put(discover, discoverRunnable);
        tempMap.put(test, testRunnable);
        return tempMap;
    }


    public class ClientTask implements Runnable {
        Socket socket;
        Map<String, Runnable> runnableMap;

        public ClientTask(Socket socket, Map<String, Runnable> runnableMap) {
            if (socket == null || runnableMap == null) {
                throw new RuntimeException("Socket and RunnableMap can't be null");
            }
            this.socket = socket;
            this.runnableMap = runnableMap;
        }

        @Override
        public void run() {
            System.out.println("Receiving request..");
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
