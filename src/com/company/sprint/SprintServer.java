package com.company.sprint;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SprintServer {
    private HttpServer httpServer;

    public SprintServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(9777), 0);

    }
}
