package controller;

import enumclass.EnumURL;
import http.util.HttpRequest;
import http.util.HttpResponse;

import javax.swing.text.html.CSS;
import java.io.IOException;

public class ForwardController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if(httpRequest.getURL().endsWith(EnumURL.CSS.getUrl())){
            httpResponse.forward(ROOT_URL+STYLE_CSS_URL, CSS_TYPE);
            return;
        }
        httpResponse.forward(ROOT_URL + httpRequest.getURL(), HTML_TYPE);
    }
}
