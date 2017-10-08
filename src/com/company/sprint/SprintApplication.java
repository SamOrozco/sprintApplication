package com.company.sprint;

import com.company.models.User;

import java.io.IOException;
import java.util.Set;

import static com.company.utils.Utils.getIPStringForMac;

public class SprintApplication {
    public Set<User> users;
    DiscoverClient discoverClient;
    SprintServer sprintServer;
    public String ip;

    public SprintApplication() {
        ip = getIPStringForMac();
        discoverClient = new DiscoverClient();
    }

    public static void start() {
    }


    public void sendInitRequests(User user) {
        discoverClient.sendAware(user, ip);
    }

    public void startServer() throws IOException {
        sprintServer = new SprintServer();
    }

}
