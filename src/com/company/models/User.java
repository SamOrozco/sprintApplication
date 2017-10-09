package com.company.models;

import com.company.vote.Vote;
import com.sun.deploy.net.BasicHttpRequest;
import com.sun.deploy.net.HttpRequest;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class User {
    public String name;
    public List<Vote> votes;
    public boolean teamLeader;
    public String host;
    public Date lastUpdated;

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
        String voteUrl = host + "/acceptvote";
        URL url = new URL(voteUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        ObjectMapper objectMapper = new ObjectMapper();
        String voteJson = objectMapper.writeValueAsString(vote);
        httpURLConnection.setDoOutput(true);
        DataOutputStream body = new DataOutputStream(httpURLConnection.getOutputStream());
        body.writeBytes(voteJson);
        body.flush();
        body.close();
        System.out.println(httpURLConnection.getResponseCode());
    }

}
