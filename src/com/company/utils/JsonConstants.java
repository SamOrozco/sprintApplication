package com.company.utils;

import com.company.models.User;

public class JsonConstants {

    public static String getSendAwareJsonFromUser(User user) {
        String jsonValue = String.format("{\"name\": %s, \"leader\": %s}", user.name, user.teamLeader);
        return jsonValue;
    }
}
