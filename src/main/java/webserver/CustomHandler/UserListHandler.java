package webserver.CustomHandler;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static webserver.constant.Http.*;

public class UserListHandler implements Controller {

    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        String cookieValue = request.getHeader(COOKIE.getValue());
        if (cookieValue == null || !cookieValue.equals(LOGINED_TRUE.getValue())) {
            response.redirect(LOGIN_FORM.getValue());
            return;
        }
        response.forward(request.getRequestUri());
    }

}
