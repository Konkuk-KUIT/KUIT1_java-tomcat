# Tomcat 구현 힌트 및 상세정보
### 구현 힌트 및 싱세정보 포함 문서
구현 마친 후 읽어보기

<br>

---

## 요구사항 1: index.html 반환하기

```
클라이언트에서 `http://localhost:{port}/` 혹은 `http://localhost:{port}/index.html` 요청이 올 시에
webapp 폴더의 'index.html' 화면을 반환한다.
```
<br>

### 힌트 및 상세정보
- 서버와 통신이 연결된 후에는 Socket을 통해 통신한다.
    - 자바의 Socket Library에서 뿐만 아니라 대부분의 TCP 통신에서는 **환영소켓**을 통해 통신을 받아들이고, 연결이 되었을 때 실질적으로 **연결 소켓**을 만들어 그 소켓을 통해 통신한다.
- 연결 소켓에서 `InputStream`과 `OutputStream`을 통해 데이터를 읽어 들여온다.
    - 제공한 코드에서는 `InputStream`을 `BufferedReader`로 감쌌다.
    - 제공한 코드에서는 `OutputStream`을 `DataOutputStream`으로 감쌌다.
    - 위 두 라이브러리에 대해서는 추가적으로 학습해보도록 한다.
        - 참고 : 두 `Stream` 라이브러리는 데코레이터 패턴을 통해 `InputStream` 과 `OutputStream` 에 기능을 더해준다.
- 즉, `InputStream`에는 클라이언트의 요청 메시지가 들어있다.
    - 이 정보를 해석하여 알맞은 처리를 할 수 있다.
    - 데이터를 읽어드릴때 조심해야할 점은 **br.readline을 무심코 하면 뒤에 /r/n으로 끝나지 않는다면 서버에서 계속 데이터가 들어오는 것을 기다려 broken pipe 오류가 나는 것을 확인할 수 있다.**
    - 즉, /r/n으로 분리되어 있지 않은 **body의 내용은 꼭! IOUtils에 있는 것 처럼 contentLength와 함께 넘겨주어 읽어야만 정확히 데이터를 읽어올 수 있다.**
- File 내용을 반환할 때는 `FileInputStream` 라이브러리를 통해 데이터를 읽어, response body에 해당 내용을 전달한다.
    - `Files.readAllBytes(Paths.get(url))` 을 통해 쉽게 파일 내용을 읽어 byte로 반환할 수 있다.
- response header도 위 읽어온 데이터에 따라 적절히 처리해주어야한다.

<br><br>

---

### 요구사항 2: GET 방식으로 회원가입하기

```
로그인 하기 위해 SignUp 버튼을 클릭하면 user/form.html 화면으로 이동해야 한다. 

form.html 에서 정보를 입력한 뒤 회원 가입하면 url의 쿼리스트링으로부터
MemoryUserRepository에 정보를 받아와 다시 index.html 화면을 띄운다.
```

<br>

### 힌트 및 상세 정보
- 현재 회원가입이 get 방식이기 때문에 요청 url을 잘 보면 queryString 형식으로 전달되는 것을 알 수 있다.
- 위 정보를 잘 parsing해서 새로운 User Instance를 생성하고, 이를 MemoryUserRepository 저장해보자.
- 단지 body에 index.html 을 전달하면 url이 그대로 /user/signup?~일 것이다. 신경쓰이지 않는가? status code 302 redirect에 대해 알아보고 적용해보자. 이는 다음 4번째 요구사항에도 있다.

<br><br>

---

### 요구사항 3: POST 방식으로 회원가입하기

```
위 GET 방식으로 구현했다면 이번엔 form.html의 form 태그의 method를 post로 변경하고 post 방식으로 로그인되게끔 변경해보자.

이외의 작업은 위 2번과 동일하다.
```

<br>

### 힌트 및 상세 정보
- post 방식을 사용했더니 queryString이 사라진 것을 알 수 있을 것이다.
- 이 queryString은 post 방식에서 request body에 들어가있다.
- request body안의 정보를 읽어오기 위해서는 Header의 Content-Length 값이 필요하고, BufferedReader의 offset을 body 값 바로 앞에 위치시켜야한다.
- 그것을 구현하는 코드는 다음과 같다.
  ```java
    while (true) {
    	final String line = br.readLine();
    	if (line.equals("")) {
    		break;
    	}
    	// header info
    	if (line.startsWith("Content-Length")) {
    		requestContentLength = Integer.parseInt(line.split(": ")[1]);
    	}
    }
  ```
- 이제 IOUtils 클래스의 readBody 메소드에 contentLength와 BufferedReader를 함께 보내어 body 값을 읽을 수 있을 것이다.
- 읽어온 body값을 parsing하는 것은 이미 요구사항 2번에서 했으므로 익숙하리라 믿는다.

<br><br>

---

### 요구사항 4: 302 status code 적용

```
현재는 post signup이 끝났을 때 반환되는 웹페이지는 index.html이지만, 위 브라우저에는 여전히 ~/user/signup 일 것이다. 

이를 해결하기 위해 상태코드 302에 대해서 알아보고 적용해보자.
```

<br>

### 힌트 및 상세 정보
- response의 상태코드를 302로 변경한다.
- 나머지 헤더는 Location 헤더만 필요하므로 삭제한다.
- body 값도 필요없다.
- Location 헤더값의 value는 파일의 위치가 아니라 클라이언트가 요청하는 url을 쓴다.
    - e.g., /index.html

<br><br>

---

### 요구사항 5: 로그인하기

```
로그인 버튼을 누르면 로그인 페이지로 이동한다.

로그인 시 전달되는 유저와 repository에 있는 유저가 동일한지 확인하고 동일하다면
헤더에 `Cookie: logined=true`를 추가하고, index.html 화면으로 redirect 한다.

로그인이 실패한다면 logined_failed.html로 redirect한다.
```

<br><br>

---

### 요구사항 6: 사용자 목록 출력

```
위 요구사항 5번에서 Set-Cookie가 잘 동작되었다면,
클라이언트가 다시 서버에 요청할 때 자동으로 헤더에 Cookie: logined=true가 추가되어있을 것이다.

헤더에 `Cookie: logined=true`가 되어있을 때만 user list 버튼 클릭 시 user list 화면이 나오게끔 해보자.

로그인이 되어있지 않은 상태라면 login.html 화면으로 redirect 한다.

repository에 있는 유저 리스트를 보여줄 필요는 없다.

단지 유저 리스트를 보여주는 것이 필요하다는 것만 상기하자.
```

<br>

### 힌트 및 상세 정보
- 헤더에 Cookie 값을 확인해서 그 값이 logined=true이면 유저 리스트를 보여준다.
- 현재 회원인 유저 리스트를 보여주고 싶지만, 현재 우리가 만드는 프레임워크에서는 Html을 동적으로 생성하는 것이 매우 귀찮고 힘든 일이다.
- 이러한 단점 때문에 서블릿과 JSP가 나왔다는 것만 상기하자.

<br><br>

---

### 요구사항 7: CSS 출력

```
현재는 styles.css가 적용되지 않고 있는 상태일 것이다.

GET ./css/style.css HTTP/1.1
Host: ~
Accept: text/css,*/*;q=0.1

헤더의 요청을 받을 수 있도록 처리한다.
url이 css 확장자로 끝난다면 Content-Type을 text/css로 해야한다.
```
<br>