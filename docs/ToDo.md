# Tomcat 구현
### Tomcat을 사용하기 전에 직접 구현해보는 단계

### Step
Step 1 - Http Request 메시지를 상상하며 실질적으로 실행되는 run() 메소드 구현에 집중<br>
Step 2 - 객체지향적인 고민을 통한 리팩토링.<br><br>

***
## 요구사항
> #### ~~요구사항 1: index.html 반환하기~~

클라이언트에서 `http://localhost:{port}/` 혹은 `http://localhost:{port}/index.html` 요청이 올 시에
webapp 폴더의 'index.html' 화면을 반환한다.
<br><br>

> #### _~~요구사항 2: GET 방식으로 회원가입하기~~_ 

로그인 하기 위해 SignUp 버튼을 클릭하면 user/form.html 화면으로 이동해야 한다. 

form.html 에서 정보를 입력한 뒤 회원 가입하면 url의 쿼리스트링으로부터
MemoryUserRepository에 정보를 받아와 다시 index.html 화면을 띄운다.

- `처음부터 form.html의 form 태그가 post로 되어있어 생략`

<br><br>

> #### ~~요구사항 3: POST 방식으로 회원가입하기~~

위 GET 방식으로 구현했다면 이번엔 form.html의 form 태그의 method를 post로 변경하고 post 방식으로 로그인되게끔 변경해보자.

이외의 작업은 위 2번과 동일하다.
<br><br>

> #### 요구사항 4: 302 status code 적용

현재는 post signup이 끝났을 때 반환되는 웹페이지는 index.html이지만, 위 브라우저에는 여전히 ~/user/signup 일 것이다. 

이를 해결하기 위해 상태코드 302에 대해서 알아보고 적용해보자.
<br><br>

> #### 요구사항 5: 로그인하기

로그인 버튼을 누르면 로그인 페이지로 이동한다.

로그인 시 전달되는 유저와 repository에 있는 유저가 동일한지 확인하고 동일하다면
헤더에 `Cookie: logined=true`를 추가하고, index.html 화면으로 redirect 한다.

로그인이 실패한다면 logined_failed.html로 redirect한다.
<br><br>

> #### 요구사항 6: 사용자 목록 출력

위 요구사항 5번에서 Set-Cookie가 잘 동작되었다면,
클라이언트가 다시 서버에 요청할 때 자동으로 헤더에 Cookie: logined=true가 추가되어있을 것이다.

헤더에 `Cookie: logined=true`가 되어있을 때만 user list 버튼 클릭 시 user list 화면이 나오게끔 해보자.

로그인이 되어있지 않은 상태라면 login.html 화면으로 redirect 한다.

repository에 있는 유저 리스트를 보여줄 필요는 없다.

단지 유저 리스트를 보여주는 것이 필요하다는 것만 상기하자.
<br><br>

> #### ~~요구사항 7: CSS 출력~~

현재는 styles.css가 적용되지 않고 있는 상태일 것이다.

GET ./css/style.css HTTP/1.1
Host: ~
Accept: text/css,*/*;q=0.1

헤더의 요청을 받을 수 있도록 처리한다.
url이 css 확장자로 끝난다면 Content-Type을 text/css로 해야한다.
