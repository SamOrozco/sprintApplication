package com.company.models.request;

import java.io.IOException;

@FunctionalInterface
public interface RequestHandlerInterface {
    void check(CustomRequest customRequest) throws IOException;
}
