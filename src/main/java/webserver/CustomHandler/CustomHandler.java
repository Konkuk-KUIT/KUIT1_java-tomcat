package webserver.CustomHandler;

import java.io.IOException;

public interface CustomHandler {

    byte[] process() throws IOException;
}
