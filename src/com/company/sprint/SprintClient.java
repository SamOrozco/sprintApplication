package com.company.sprint;

import com.company.utils.Utils;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.IOException;

import static com.company.utils.Utils.needTeamLeaderPermissionsTo;
import static com.company.utils.ValidationUtils.nullOrEmpty;

public class SprintClient {
    SprintServer sprintServer;

    private static final String VOTE = "vote";
    private static final String DUMP_USERS = "dumpusers";
    private static final String DUMP_USERS_0 = "dump users";
    private static final String CLEAR_USERS = "clearusers";
    private static final String CLEAR_USERS_0 = "clear users";
    private static final String DUMP_VOTES = "dumpround";
    private static final String DUMP_VOTES_0 = "dump round";
    private static final String CLEAR_ROUND = "clearround";
    private static final String CLEAR_ROUND_0 = "clear round";
    private static final String START_ROUND = "startround";
    private static final String START_ROUND_0 = "start round";
    private static final String COMMANDS = "commands";
    private static final String ROUND = "round";
    private static final String CLEAR = "clear";
    private static final String CLOSE_ROUND = "closeround";
    private static final String CLOSE_ROUND_0 = "close round";

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
            case DUMP_USERS_0:
                sprintApplication.dumpUsers();
                break;
            case DUMP_VOTES:
            case DUMP_VOTES_0:
                sprintApplication.dumpRoundMap();
                break;
            case START_ROUND:
            case START_ROUND_0:
                if (!sprintApplication.getCurrentUser().teamLeader) {
                    needTeamLeaderPermissionsTo("Start a round");
                    return;
                }
                System.out.println("Enter round name: ");
                String roundName = Utils.scanUserLine();
                sprintServer.startRound(roundName);
                break;
            case CLEAR_ROUND:
            case CLEAR_ROUND_0:
                sprintApplication.clearRound();
                break;
            case COMMANDS:
                this.showCommands();
                break;
            case ROUND:
                if (nullOrEmpty(sprintApplication.currentRoundID)) {
                    System.out.println("You are currently not in a round.");
                    return;
                }
                System.out.println(String.format("Current Round: %s", sprintApplication.currentRoundID));
                break;
            case CLEAR:
                Utils.clearConsole();
                break;
            case CLEAR_USERS:
            case CLEAR_USERS_0:
                sprintApplication.clearUsers();
                break;
            case CLOSE_ROUND:
            case CLOSE_ROUND_0:
                sprintApplication.closeCurrentRound();
                break;
            default:
                System.out.println("Command not valid");
        }
    }


    private void showCommands() {
        System.out.println("`vote` || `vote 3`, this command will place a vote for the current round");
        System.out.println("`dumpusers`, this command will print to the console the current users you have in memory");
        System.out.println("`dumpround`, this command will print to the console the current round and  votes received");
        System.out.println("`clearround`, this command will clear the current round on your computer");
        System.out.println(" -- TEAM LEADER --");
        System.out.println("`startround`, this command will propmt you then start a round");
    }


}
