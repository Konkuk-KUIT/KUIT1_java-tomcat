package webserver.CustomHandler;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface Controller {

    byte[] process(HttpRequest request, HttpResponse response) throws IOException;
}
