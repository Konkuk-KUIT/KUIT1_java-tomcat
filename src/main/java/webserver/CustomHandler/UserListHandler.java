package webserver.CustomHandler;

import http.util.IOUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class UserListHandler implements CustomHandler{

    private final File file = new File("webapp/user/list.html");

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> headerMap = request.getHeaderMap();
        String cookieValue = headerMap.get("Cookie");
        String[] split = cookieValue.split(";");
        if (split[0].equals("logined=true")) {
            BufferedReader fr = new BufferedReader(new FileReader(file));
            String fileData = IOUtils.readData(fr, (int) file.length());
            return fileData.getBytes();
        }

        setStatusCodeAndLocation(response);
        return "/index.html".getBytes();
    }

    private static void setStatusCodeAndLocation(HttpResponse response) {
        response.setStatusCode("302");
        response.setHeader("location", "/index.html");
    }
}
