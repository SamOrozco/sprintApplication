package com.company.sprint;

import com.company.utils.Utils;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.IOException;

public class SprintClient {
    SprintServer sprintServer;

    private static final String VOTE = "vote";
    private static final String DUMP_USERS = "dumpusers";
    private static final String DUMP_VOTES = "dumpround";
    private static final String START_ROUND = "startround";

    public SprintClient(SprintServer sprintServer) {
        this.sprintServer = sprintServer;
    }


    public void executeCommand(String command, SprintApplication sprintApplication) throws IOException {
        String[] commandArgs = command.split(" ");
        switch (commandArgs[0].toLowerCase()) {
            case VOTE:
                if (!sprintApplication.hasCurrentRound()) {
                    System.out.println("Your leader has not started a round");
                }
                sprintApplication.vote(commandArgs);
                break;
            case DUMP_USERS:
                sprintApplication.dumpUsers();
                break;
            case DUMP_VOTES:
                sprintApplication.dumpRoundMap();
                break;
            case START_ROUND:
                System.out.println("Enter round name: ");
                String roundName = Utils.scanUserLine();
                sprintServer.startRound(roundName);
                break;
            default:
                System.out.println("Command not valid");
        }
    }


}
