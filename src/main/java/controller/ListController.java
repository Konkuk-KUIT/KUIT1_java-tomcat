package controller;

import http.constant.HttpHeader;
import http.constant.RequestUrl;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class ListController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        // 로그인이 되어있지 않은 상태
        if (!checkCookie(request)) {
            response.redirect(RequestUrl.LOGIN_URL.getUrl(), false);
            return;
        }
        // 로그인이 되어있는 상태
        response.forward(RequestUrl.LIST_URL.getUrl());
    }

    private boolean checkCookie(HttpRequest request) {
        // Cookie: logined=true 확인
        if (!request.hasHeaderKey(HttpHeader.COOKIE)) {
            return false;
        }
        if (!request.getHeaderValue(HttpHeader.COOKIE).contains("logined=true")) {
            return false;
        }
        return true;
    }
}
