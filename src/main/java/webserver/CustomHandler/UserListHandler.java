package webserver.CustomHandler;

import http.util.IOUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static webserver.constant.Http.*;

public class UserListHandler implements Controller {

    private final File file = new File(WEBAPP.getValue() + LIST.getValue());

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        System.out.println("진입");
        Map<String, String> headerMap = request.getHeaderMap();
        String cookieValue = headerMap.get(COOKIE.getValue());
        String[] split = cookieValue.split(";");
        System.out.println("split[0] = " + split[0]);
        if (split[0].equals(LOGINED_TRUE.getValue())) {
            BufferedReader fr = new BufferedReader(new FileReader(file));
            String fileData = IOUtils.readData(fr, (int) file.length());
            return fileData.getBytes();
        }

        setStatusCodeAndLocation(response);
        return INDEX.getValue().getBytes();
    }

    private static void setStatusCodeAndLocation(HttpResponse response) {
        response.setStatusCode(FOUND.getValue());
        response.setHeader(LOCATION.getValue(), INDEX.getValue());
    }
}
