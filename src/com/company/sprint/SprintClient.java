package com.company.sprint;

import java.io.IOException;

public class SprintClient {
    SprintServer sprintServer;

    private static final String VOTE = "vote";

    public SprintClient(SprintServer sprintServer) {
        this.sprintServer = sprintServer;
    }


    public void executeCommand(String command, SprintApplication sprintApplication) throws IOException {
        if (!sprintApplication.hasCurrentRound()) {
            System.out.println("Your leader has not started a round");
            return;
        }
        String[] commandArgs = command.split(" ");
        switch (commandArgs[0].toLowerCase()) {
            case VOTE:
                sprintApplication.vote(commandArgs);
                break;
            default:
                System.out.println("Command not valid");
        }
    }


}
