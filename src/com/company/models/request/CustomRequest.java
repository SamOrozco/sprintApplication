package com.company.models.request;

import sun.security.x509.CertAttrSet;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CustomRequest {
    private String[] requestContents;
    private String requestType;
    private String context;
    private String httpVersion;
    private Map<String, String> headers;
    private String body;

    public CustomRequest(String[] requestContents) {
        this.requestContents = requestContents;
        body = parseContents(requestContents);
    }


    private String parseContents(String[] requestContents) {
        int index = 0;
        int length = requestContents.length;
        for (String line : requestContents) {
            //if the line is empty we know the next line is the body
            if (line.isEmpty()) {
                return (index > length) ? "" : requestContents[++index];
            }
            parseLine(line, index);
            ++index;
        }
        return "";
    }


    private void parseLine(String line, int index) {
        switch (index) {
            case 0:
                parseMetaLine(line);
                break;
            default:
                parseHeader(line);
        }
    }


    private void parseMetaLine(String line) {
        if (line == null) throw new RuntimeException("Invalid Http request, empty line");
        String[] pair = line.split(" ");
        if (pair.length < 1 || pair.length < 3) {
            throw new RuntimeException("Could not parse meta line" + line);
        }
        requestType = pair[0];
        context = pair[1];
        httpVersion = pair[2];
    }

    private void parseHeader(String line) {
        if (line == null) throw new RuntimeException("Invalid Http request, empty line");
        String[] pair = line.split(":");
        if (pair.length < 1) throw new RuntimeException("Could not parse header: " + line);
        String name = pair[0];
        String value = pair[1];
        getHeaders().put(name, value);
    }


    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        return headers;
    }

    public String[] getRequestContents() {
        return requestContents;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getContext() {
        return context;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getBody() {
        return body;
    }
}
