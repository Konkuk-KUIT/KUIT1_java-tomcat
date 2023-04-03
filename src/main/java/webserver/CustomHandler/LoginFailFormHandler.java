package webserver.CustomHandler;

import http.util.IOUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LoginFailFormHandler implements CustomHandler{

    private final File file = new File("webapp/user/login_failed.html");

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        BufferedReader fr = new BufferedReader(new FileReader(file));
        String fileData = IOUtils.readData(fr, (int) file.length());
        return fileData.getBytes();
    }
}
