package com.company;

import com.company.models.User;
import com.company.sprint.SprintApplication;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import static com.company.utils.Utils.clearConsole;
import static com.company.utils.ValidationUtils.nullOrEmpty;

public class Main {

    public static void main(String[] args) throws IOException {
        clearConsole();
        //getting user name
        User user = new User();
        user.name = getUserName();
        String lastChar = user.name.substring(user.name.length() - 1, user.name.length());
        if (lastChar.toLowerCase().equals("-")) {
            user.teamLeader = true;
        }
        System.out.println("Loading.. please wait");
        //loading because we need to get ip which takes a while
        SprintApplication sprintApplication = new SprintApplication();
        sprintApplication.setCurrentUser(user);
        System.out.println("Waiting for teammates to join and team leader to start..");
        sprintApplication.startServer();
        sprintApplication.listenForInputCommand(sprintApplication);
    }


    public static String getUserName() {
        System.out.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String userName = scanner.nextLine();
        if (nullOrEmpty(userName)) {
            return getUserName();
        }
        return userName;
    }
}
