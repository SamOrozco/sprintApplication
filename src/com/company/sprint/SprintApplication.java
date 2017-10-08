package com.company.sprint;

import com.company.models.User;
import com.company.vote.Vote;
import com.company.discover.DiscoverCall;
import com.company.discover.DiscoverClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.company.utils.Utils.getIPStringForMac;

public class SprintApplication {
    private Map<String, User> users;
    private Map<String, Map<String, Vote>> roundVoteMap;
    DiscoverClient discoverClient;
    SprintServer sprintServer;
    private String ip;
    private String currentRoundID;


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
        sprintServer = new SprintServer(this);
    }

    public Map<String, User> getUsers() {
        if (users == null) {
            users = new ConcurrentHashMap<String, User>();
        }
        return users;
    }

    public Map<String, Map<String, Vote>> getRoundMap() {
        if (roundVoteMap == null) {
            roundVoteMap = new ConcurrentHashMap<>();
        }
        return roundVoteMap;
    }

    public void placeVote(Vote vote) {
        //if vote isn't for current round ignore
        if (currentRoundID == null) {
            //TODO HANDLE NON ROUND WHEN TRYING TO VOTE
            throw new RuntimeException("Round was not started");
        }
        if (!vote.round.equals(currentRoundID)) {
            //TODO: HANDLE ROUND MISMATCH
        }
        Map<String, Vote> currentMap = getRoundMap().computeIfAbsent(currentRoundID, (key) -> new HashMap<>());
        currentMap.put(vote.name, vote);
    }

    public void addUser(DiscoverCall discoverCall) {
        User user = new User();
        user.name = discoverCall.getName();
        System.out.println(String.format("User Joined: %s", user.name));
        user.teamLeader = discoverCall.isLeader();
        user.lastUpdated = discoverCall.getDateSent();
        user.host = discoverCall.getHost();
        getUsers().put(user.name, user);
    }

    public void startRound(String roundName) {
        System.out.println(String.format("start roundname %s", roundName));
        currentRoundID = roundName;
        getRoundMap().put(roundName, new HashMap<>());
    }

    public void closeCurrentRound() {
        System.out.println("closing current round");
    }


    public void dumpUsers() {
        if (getUsers().isEmpty()) System.out.println("This client has no users.");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String value = objectMapper.writeValueAsString(getUsers());
            System.out.println(value);
        } catch (IOException e) {
            throw new RuntimeException("There was an issue parsing your users json");
        }
    }


    public void dumpRoundMap() {
        if (getUsers().isEmpty()) System.out.println("This client has no votes.");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String value = objectMapper.writeValueAsString(getRoundMap());
            System.out.println(value);
        } catch (IOException e) {
            throw new RuntimeException("There was an issue parsing your Round Votes json");
        }
    }

}
