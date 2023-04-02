package webserver.CustomHandler;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class LoginFormHandler implements CustomHandler{

    private final File file = new File("webapp/user/login.html");

    @Override
    public byte[] process(Map<String, String> paramMap) throws IOException {
        BufferedReader fr = new BufferedReader(new FileReader(file));
        String fileData = IOUtils.readData(fr, (int) file.length());
        return fileData.getBytes();
    }
}
