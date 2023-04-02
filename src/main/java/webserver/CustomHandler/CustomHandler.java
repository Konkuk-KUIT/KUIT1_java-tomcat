package webserver.CustomHandler;

import java.io.IOException;
import java.util.Map;

public interface CustomHandler {

    byte[] process(Map<String, String> paramMap) throws IOException;
}
