package com.company.models;

import com.company.models.request.HttpRequest;
import com.company.vote.Vote;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

public class User {
    public String name;
    public List<Vote> votes;
    public boolean teamLeader;
    public String host;
    public Date lastUpdated;
    public Socket mySocket;

    public User() {
    }

    @Override
    public boolean equals(Object user) {
        User thatUser = (User) user;
        User thisUser = this;
        if (thatUser.name != thisUser.name) return false;
        if (thatUser.host != thisUser.host) return false;
        return true;
    }


    public static boolean isTeamLeader(String username) {
        String lastTwoChar = username.substring(username.length() - 2, username.length());
        if (lastTwoChar.toLowerCase().equals("xx")) return true;
        return false;
    }


    public void sendVote(Vote vote) throws IOException {
        Socket socket = getMySocket();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(vote);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("POST");
        httpRequest.setHost(host);
        httpRequest.setPath("/acceptvote");
        httpRequest.setBody(jsonBody);
        httpRequest.sendHttpRequest(socket.getOutputStream());
    }


    public Socket getMySocket() throws IOException {
        if (mySocket == null) {
            InetAddress myAddress = InetAddress.getByName(host);
            mySocket = new Socket(myAddress, 9776);
        }
        return mySocket;
    }

}
