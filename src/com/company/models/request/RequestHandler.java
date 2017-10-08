package com.company.models.request;

import java.util.Map;

public class RequestHandler {
    public RequestHandler() {
    }

    public static void handleRequest(CustomRequest customRequest, Map<String, Runnable> runnableMap) {
        Runnable runnable = runnableMap.get(customRequest.getContext());
        if (runnable == null)
            throw new RuntimeException(String.format("The runnable registered for %s was null", customRequest.getContext()));
        runnable.run();
    }
}
