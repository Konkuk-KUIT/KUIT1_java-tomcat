package http.request;



import http.constants.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


//Header 정보를 Map으로 저장하는 클래스
public class HttpHeaders {
    //헤더 정보 Map
    private Map<HttpHeader, String> headers;
    // :을 상수로 정의
    private static final String DISCRIMINATOR = ": ";

    public HttpHeaders(final Map<HttpHeader, String> headers) {
        this.headers = headers;
    }

    //* HttpHeaders를 생성해주는 함수
    // br을 넘겨주면 header를 읽고 Map으로 바꿔서 생성
    public static HttpHeaders from(final BufferedReader bufferedReader) throws IOException {
        return new HttpHeaders(readAllHeaders(bufferedReader));
    }

    //* header를 읽는 실질적인 함수
    private static Map<HttpHeader, String> readAllHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<HttpHeader, String> headers = new LinkedHashMap<>();


        //Header 정보 다 읽어올 때까지 반복
        while (true) {
            final String line = bufferedReader.readLine();
            if (line.equals("")) {
                break;
            }

            //header를 type과 value로 나눠 list에 넣어주고
            final List<String> header = parseHeader(line);
            //리스트를 통해 type과 value를 key, value로 map에 넣어줌
            final String headerType = header.get(0).trim();
            HttpHeader headerKey = HttpHeader.getHeaderInstance(headerType);
            final String headerValue = header.get(1).trim();
            if (headerKey != null) {
                headers.put(headerKey, headerValue);
            }
        }
        //헤더 정보가 담긴 map 반환
        return headers;
    }

    //* header 분리 함수
    // : 기준으로 데이터를 나눠주고 해당 헤더 값이 정당한 헤더 값인지 검증 후 return
    private static List<String> parseHeader(final String line) {
        final List<String> header = Arrays.asList(line.split(DISCRIMINATOR));
        validateHeader(header);
        return header;
    }

    //* 검증 함수
    // header 값에 key 혹은 value 값이 없다면 에러 발생
    private static void validateHeader(final List<String> header) {
        if (header.size() < 2) {
            throw new IllegalArgumentException("요청 정보가 잘못되었습니다.");
        }
    }

    public boolean contains(final HttpHeader httpHeaderType) {
        return headers.containsKey(httpHeaderType);
    }

    public String get(final HttpHeader httpHeaderType) {
        return headers.get(httpHeaderType);
    }

    public Set<HttpHeader> keySet() {
        return headers.keySet();
    }

    public void put(HttpHeader httpHeaderType, final String httpHeader) {
        headers.put(httpHeaderType, httpHeader);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key,value) -> sb.append(key.getHeader()).append(": ").append(value).append("\r\n"));

        return sb.append("\r\n").toString();
    }
}