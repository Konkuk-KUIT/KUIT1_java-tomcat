package http;

import http.constant.HttpHeader;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private Map<HttpHeader, String> headers = new HashMap<>();
    private static final String DELIMITER = ": ";
    private static final String CRLF = "\r\n";

    public HttpHeaders(){}

    public void put(HttpHeader header, String value) {
        headers.put(header, value);
    }

    @Override
    public String toString(){
        String str = "";
        if(headers.isEmpty())
            return str;
        for(HttpHeader key : headers.keySet()){
            str += key.getHeader() + DELIMITER + headers.get(key) + CRLF;
        }
        return str + CRLF;
    }

    public boolean containsKey(HttpHeader header) {
        return headers.containsKey(header);
    }

    public String get(HttpHeader header){
        return headers.get(header);
    }

}
