package http.request;

import http.HttpHeaders;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import http.constant.HttpHeader;
import http.constant.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.constant.HttpHeader.*;

public class HttpRequest {

    private BufferedReader br;
    private HttpRequestStartLine startLine;
    private HttpHeaders requestHeader;
    private String body;


    private HttpRequest(HttpRequestStartLine startLine, HttpHeaders requestHeader, String body) throws IOException {
        this.startLine = startLine;
        this.requestHeader = requestHeader;
        this.body = body;
        System.out.println("HttpRequest - startLine : " + startLine.toString());
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        /* startLine parsing */
        String readStartLine = br.readLine();
        HttpRequestStartLine startLine = parsingStartLine(readStartLine);

        /* header parsing */
        HttpHeaders headers = new HttpHeaders();
        while (true) {
            final String line = br.readLine();

            if (line==null || line.equals(""))
                break;
            if (line.startsWith(CONTENT_LENGTH.getHeader())) {
                headers.put(CONTENT_LENGTH, line.split(": ")[1].strip());
            }
            if (line.startsWith(COOKIE.getHeader())) {
                headers.put(COOKIE, line.split(": ")[1].strip());
            }
        }

        /* body parsing */
        String body = parsingBody(br, headers);

        return new HttpRequest(startLine, headers, body);
    }

    private static HttpRequestStartLine parsingStartLine(String readStartLine) {
        return HttpRequestStartLine.from(readStartLine);
    }

    private static String parsingBody(BufferedReader br, HttpHeaders headers) throws IOException {
        if(!headers.containsKey(CONTENT_LENGTH))
            return "";
        String content_length = headers.get(CONTENT_LENGTH);
        return IOUtils.readData(br, Integer.parseInt(content_length));
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getBody(){
        return body;
    }

    public Map<String, String> getQuery() {
        if(startLine.getQueries() != null)
            return startLine.getQueries();

        return HttpRequestUtils.parseQueryParameter(body);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public HttpHeaders getHeader() {
        return requestHeader;
    }
}
