package com.company.sprint;

import com.company.models.User;
import com.company.utils.Utils;

import java.io.IOException;
import java.util.Set;

import static com.company.utils.Utils.getIPStringForMac;

public class SprintApplication {
    public Set<User> users;
    DiscoverClient discoverClient;
    SprintClient sprintClient;
    public static final String ip = getIPStringForMac();

    public SprintApplication() {
        discoverClient = new DiscoverClient();
    }

    public static void start() {
    }


    public void sendInitRequests(User user) {
        discoverClient.sendAware(user, ip);
    }

    public void startServer() throws IOException {
         sprintClient = new SprintClient();
    }

}
