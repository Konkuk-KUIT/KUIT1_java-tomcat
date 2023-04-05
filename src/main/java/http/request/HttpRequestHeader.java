package http.request;

import http.constant.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {
    private Map<HttpHeader, String> header;

    public HttpRequestHeader(Map<HttpHeader, String> header) {
        this.header = header;
    }

    public static HttpRequestHeader from(BufferedReader br) throws IOException {
        Map<HttpHeader, String> httpHeader = new HashMap<>();

        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            HttpHeader key = HttpHeader.getHttpHeader(line.split(": ")[0]);
            String value = line.split(": ")[1];

            httpHeader.put(key, value);
        }

        return new HttpRequestHeader(httpHeader);
    }

    public boolean hasKey(HttpHeader key) {
        return header.containsKey(key);
    }

    public String getValue(HttpHeader key) {
        return header.get(key);
    }
}
