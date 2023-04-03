package webserver.CustomHandler;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface CustomHandler {

    byte[] process(HttpRequest request, HttpResponse response) throws IOException;
}
