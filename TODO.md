# **리팩토링**

## 요구사항 1: Enum 활용
* HttpHeader, 
* HttpMethod, 
* URL, 
* 상태 코드, 
* User의 queryKey

## 요구사항 2: HttpRequest 메시지 분리
* HttpRequest 클래스 생성

## 요구사항 3: HttpResponse 메시지 분리
* HttpResponse 클래스 생성
* forward(path) 함수 : 원하는 html 파일을 보여주는 함수
* redirect(path) 함수 : redirect 시켜주는 함수

## 요구사항 4: url마다의 작업 처리 분리
* 각 작업별 Controller Class
* Controller Interface
* ForwardController
* HomeController
* SignUpController
* LoginController
* ListController

## 요구사항 5: 분기문 처리
* Map을 이용한 if문 제거
* 위 Controller들을 Map에 담기

