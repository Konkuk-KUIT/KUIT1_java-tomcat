package http.request;

import http.constant.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private Map<HttpHeader, String> header;

    public HttpHeaders(Map<HttpHeader, String> header) {
        this.header = header;
    }

    public static HttpHeaders from(BufferedReader br) throws IOException {
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

        return new HttpHeaders(httpHeader);
    }

    public void putHeader(String key, String value) {
        HttpHeader headerKey = HttpHeader.getHttpHeader(key);
        header.put(headerKey, value);
    }

    public boolean hasKey(HttpHeader key) {
        return header.containsKey(key);
    }

    public String getValue(HttpHeader key) {
        // 이 메서드를 사용하는 곳에서 모두 hasKey를 통해 먼저 키가 있는지 확인하고 호출해서 따로 유효성 검사를 하지 않음,,
        return header.get(key);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        header.forEach((key, value) ->
                stringBuilder.append(key.getHeader()).append(": ").append(value).append("\r\n"));

        return stringBuilder.toString();
    }
}
