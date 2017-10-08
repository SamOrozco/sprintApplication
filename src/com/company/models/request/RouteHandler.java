package com.company.models.request;

import java.util.HashMap;
import java.util.Map;

public class RouteHandler {
    private Map<String, RequestHandlerInterface> runnableMap;

    public RouteHandler() {
        runnableMap = new HashMap<>();
    }

    public Map<String, RequestHandlerInterface> getRoutes() {
        return getRunnableMap();
    }

    public void registerRoute(String route, RequestHandlerInterface routeHandler) {
        runnableMap.put(route, routeHandler);
    }


    private Map<String, RequestHandlerInterface> getRunnableMap() {
        if (runnableMap == null) {
            runnableMap = new HashMap<>();
        }
        return runnableMap;
    }

    private void setRunnableMap(Map<String, RequestHandlerInterface> runnableMap) {
        this.runnableMap = runnableMap;
    }
}
