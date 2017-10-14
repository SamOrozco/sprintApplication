package com.company.models.request;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.company.utils.ValidationUtils.nullOrEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpRequest {
    private String method;
    private String host;
    private String path;
    private String body;
    private Map<String, String> headers;
    private static final String HTTP_VERSION = "HTTP/1.1";

    public HttpRequest() {
    }


    public String getHttpRequest() {
        String request = "";
        if (method == null) throw new RuntimeException("Request method not defined");
        request += String.format("%s %s %s", method, path, HTTP_VERSION) + "\r\n";
        if (!getHeaders().isEmpty()) {
            for (String key : getHeaders().keySet()) {
                String value = getHeaders().get(key);
                request += String.format("%s : %s", key, value) + "\r\n";
            }
        }
        if (!nullOrEmpty(body)) {
            request += "" + "\r\n";
            request += body;
        }
        return request;
    }


    public void sendHttpRequest(OutputStream outputStream, Socket socket) throws IOException {
        if (method == null) throw new RuntimeException("Request method not defined");
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(String.format("%s %s %s", method, path, HTTP_VERSION));
        if (!getHeaders().isEmpty()) {
            for (String key : getHeaders().keySet()) {
                String value = getHeaders().get(key);
                printWriter.println(String.format("%s:%s", key.trim(), value.trim()));
            }
        }
        if (!nullOrEmpty(body)) {
            printWriter.println("");
            printWriter.print(body);
        }
        outputStream.close();
        socket.close();
    }

    public void addHeader(String key, String value) {
        if (nullOrEmpty(key) || nullOrEmpty(value)) {
            throw new RuntimeException("Key and value cannot be null or empty when settings heades");
        }
        getHeaders().put(key, value);
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return headers;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
