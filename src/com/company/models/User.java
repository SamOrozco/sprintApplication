package com.company.models;

import com.company.vote.Vote;

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

}
