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
    public String ip;
    public String currentRoundID;
    private User currentUser;


    public SprintApplication() {
        ip = getIPStringForMac();
        discoverClient = new DiscoverClient();
    }

    public boolean hasCurrentRound() {
        return currentRoundID != null;
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


    public void vote(String[] commandArgs) throws IOException {
        if (commandArgs.length == 1) {
            System.out.print("Enter fibonacci vote value: ");
            Scanner scanner = new Scanner(System.in);
            int value = scanner.nextInt();
            if (!validValue(value)) {
                System.out.println("Not a valid vote. Try again.");
                vote(commandArgs);
            }
            sprintServer.sendVote(value, this);
        } else {
            Integer voteValue = Integer.parseInt(commandArgs[1]);
            sprintServer.sendVote(voteValue, this);
        }

    }

    private boolean validValue(int value) {
        Set<Integer> validValues = new HashSet<>();
        validValues.add(1);
        validValues.add(2);
        validValues.add(3);
        validValues.add(5);
        validValues.add(8);
        validValues.add(12);
        validValues.add(20);
        validValues.add(32);
        validValues.add(52);
        validValues.add(84);
        return validValues.contains(value);
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
        String username = user.name;
        if (username == null) return;
        user.teamLeader = discoverCall.isLeader();
        user.lastUpdated = discoverCall.getDateSent();
        user.host = discoverCall.getHost();
        if (!getUsers().keySet().contains(username)) {
            if (user.teamLeader) {
                System.out.println(String.format("Team Leader Joined: %s", user.name));
            } else {
                System.out.println(String.format("User Joined: %s", user.name));
            }
        }
        getUsers().put(user.name, user);
    }

    public void startRound(String roundName) {
        System.out.println(String.format("The leader has started round: %s", roundName));
        currentRoundID = roundName;
        getRoundMap().put(roundName, new HashMap<>());
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


    public void clearRound() {
        roundVoteMap = new HashMap<>();
    }


    public void listenForInputCommand(SprintApplication sprintApplication) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        if (command.toLowerCase().equals("quit")) {
            System.exit(0);
        }
        SprintClient sprintClient = new SprintClient(sprintServer);
        sprintClient.executeCommand(command, sprintApplication);
        listenForInputCommand(sprintApplication);
    }

    public void addTestUser() {
        DiscoverCall discoverCall = new DiscoverCall();
        discoverCall.setName("User ten");
        discoverCall.setDateSent(new Date());
        discoverCall.setHost("connector1.ngrok.io");
        this.addUser(discoverCall);
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}