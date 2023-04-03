package webserver.CustomHandler;

import http.util.IOUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.constant.Http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static webserver.constant.Http.*;

public class LoginFailFormHandler implements CustomHandler{

    private final File file = new File(WEBAPP.getValue() + USER_LOGIN_FAILED.getValue());

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        BufferedReader fr = new BufferedReader(new FileReader(file));
        String fileData = IOUtils.readData(fr, (int) file.length());
        return fileData.getBytes();
    }
}
