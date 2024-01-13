package http.request;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static webserver.constant.Http.CONTENT_LENGTH;

public class HttpRequest {

    private String requestUri;
    private int contentLength;
    private Map<String, String> paramMap;
    private Map<String, String> headerMap;

    private HttpRequest() {}

    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        String startLine = getStartLine(br);
        httpRequest.requestUri = getURIFromRequestTarget(getRequestTarget(startLine));
        httpRequest.headerMap = getRequestHeader(br);

        if (httpRequest.headerMap.get(CONTENT_LENGTH.getValue()) == null) httpRequest.contentLength = 0;
        else httpRequest.contentLength = Integer.parseInt(httpRequest.headerMap.get(CONTENT_LENGTH.getValue()));

        httpRequest.paramMap = HttpRequestUtils.parseQueryParameter(IOUtils.readData(br, httpRequest.contentLength));

        return httpRequest;
    }

    private static String getStartLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private static String getRequestTarget(String startLine) throws IOException {
        String[] split = startLine.split(" ");
        return split[1];
    }

    private static String getURIFromRequestTarget(String RequestTarget) throws IOException {
        String[] split = RequestTarget.split("\\?");
        return split[0];
    }


    public static Map<String, String> getRequestHeader(BufferedReader br) {
        HashMap<String, String> map = new HashMap<>();
        try {
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) return map;

                // header info
                String[] split = line.split(":");
                map.put(split[0].strip(), split[1].strip());
            }
        } catch (IOException e) {
            System.out.println("헤더매핑오류");
            return map;
        }
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getParamValue(String key) {
        return paramMap.get(key);
    }

    public String getHeader(String headerName) {
        return headerMap.get(headerName);
    }
}
