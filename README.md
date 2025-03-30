<div align="center">
<img width="350px" src="https://github.com/user-attachments/assets/98ddfacd-d8a4-4083-81c4-d7170d1dfe12" alt="TMS"/>

# GLT KOREA TMS PROJECT

### 🚚더 스마트한 운송 관리, TMS🚚

</div>

온라인 이커머스의 확장과 함께, 일반 소비자를 위한 배송(라스트마일)과 기업 간 거래를 지원하는 플랫폼 기반 물류(미들마일) 수요가 증가하고 있습니다. 운송 중 발생할 수 있는 다양한 조건들을 체계적으로 관리하고 최적의 배차 기능을 제공할 수 있는 솔루션이 필요하지 않으신가요?

(주)지엘티코리아와 연계하여 진행된 이 프로젝트는 이러한 변화하는 물류 환경 속에서 운송(배송, 수송, 배달) 주문을 더욱 효율적으로 관리하여 물류비를 절감하고 고객 서비스 품질을 향상시키는 것을 목표로 합니다.

<br/>

## 🛠️ Skills

### 백엔드
<img width="700px" src='https://github.com/user-attachments/assets/972a6a4b-589a-4249-a0e3-7c6aade371b9'  alt="Backend"/>

### 인프라
<img width="700px" src='https://github.com/user-attachments/assets/be82b1c7-8375-4bb8-bd00-9eaf82c2f28d'  alt="Skills"/>

## ⚙️ 애플리케이션 아키텍처
<img width="700px" src='https://github.com/user-attachments/assets/e788a21f-c92e-43d1-878b-b9a89d704b6a'  alt="Application Architecture"/>

### 요청 흐름
① User는 브라우저(Chrome, Safari 등)를 실행한다.<br>
② 브라우저를 통해 Vercel로 배포된 Client에 접속한다.<br>
③ Client는 Server에 API를 요청한다.<br>
④ Let's Encrypt 인증서를 사용한 Nginx를 통해 SSL 인증서 기반 암호화된 HTTPS 통신을 수행한다.<br>
  서버의 도메인으로 들어온 request는 프록시 패스를 통해 Spring이 실행 중인 포트 번호로 전달된다.<br>
⑤ 컨테이너 내부 네트워크를 통해 Redis에 Data를 읽고 쓴다.<br>
⑥ MySQL 엔진을 사용한 RDS를 통해 Data를 읽고 쓴다.<br>
⑦ 경로 최적화 Request인 경우, 경로 최적화 서버에 넘겨 GraphHopper 라이브러리를 통해 경로를 최적화하여 응답한다.<br>
⑧ 요청 처리 중 예기치 못한 오류가 발생한 경우, Sentry에 기록하여 에러 모니터링을 한다.

### CI/CD 흐름
① Issue 및 PR 이벤트 발생 시 Webhook을 통해 Discord에 알림을 전송한다.<br>
② 개발자는 작성된 코드를 Main branch에 push 한다.<br>
③ Main branch에 push 이벤트가 감지되면 Github Actions WorkFlow가 작동된다.<br>
④ Github Actions는 Repository 환경 설정에 추가된 Secret을 통해 환경 변수를 주입하여 Spring 프로젝트를 빌드하고, Docker Hub에 빌드된 이미지를 push한다.<br>
⑤ 빌드가 성공적으로 완료되면, Github Actions는 SSH를 통해 Prod Server에 접근하고 스크립트를 통해 최신화된 image를 Docker Hub에서 가져온다.<br>

## 🛠 트러블 슈팅

### 1. 경로 최적화 서버 부하 문제
#### 문제 상황
Spring의 라이브러리인 GraphHopper와 JSprit를 이용하여 경로 최적화 기능을 구현했으나, 하나의 인스턴스에서 여러 작업이 동시에 이루어지면서 서버 부하 문제가 발생<br>
경로 최적화 작업 도중 서버에 부하가 생겨 속도가 저하되거나 메모리 부족으로 인해 서버가 종료되는 상황이 발생

#### 해결 방법
서버 부하를 줄이기 위해 경로 최적화 기능만을 추출하여 경로 최적화 서버로 분리<br>
별도의 인스턴스로 운영 서버와 경로 최적화 서버를 분리함으로써 서버 부하를 효과적으로 분산<br>
이로 인해 애플리케이션 성능 저하와 메모리 부족 문제를 해결

### 2. 컨트롤러 내 로그인 구현에 대한 성능 및 보안성 문제
#### 문제 상황
인증이 컨트롤러에 도달한 후 처리되므로 Spring Security를 이용한 필터보다 보안성이 낮고, 불필요한 Servlet 호출 발생

#### 해결 방법
Spring Security를 이용하여 로그인 및 인증을 구현
토큰 기반 인증 / 권한 기반 인증 같은 다양한 필터 체인을 사용
컨트롤러에 도달하기 전 요청을 처리하므로 리소스 낭비 절감 및 보안성 향상

### 3. JWT Token의 취약성
#### 문제 상황
Access Token(AT)와 Refresh Token(RT), 두 토큰이 모두 탈취당했을 때 토큰의 유효기간이 끝날 때까진 공격자가 자원에 접근 가능한 문제가 발생

#### 해결 방법
RTR 기법을 적용하여 AT가 만료되어 재발급을 받을 때, 기존 RT는 폐기 후 새로운 RT를 재발급해주는 방식을 적용
ID/PW 탈취가 아닌 토큰만 탈취한 경우, 공격자는 오직 AT의 유효 기간 동안만 자원에 접근이 가능

### 4. Swagger 응답 예시 출력 문제
#### 문제 상황
동일한 Http Status Code의 여러 개 response가 있을 때, 가장 앞에 위치한 response 예시만 출력 되는 문제가 발생
#### 해결 방법
스웨거의 기본 동작 방식과 유사하게 사용할 수 있도록 커스텀 어노테이션을 생성하여 응답에 대한 데이터를 받고 Operation의 Response 객체를 커스텀하여 기본적으로 단일 객체로 설정되어 있던 Response를 List 형태로 변경

### 4. Filter 내 Exception 처리 문제
#### 문제 상황

#### 해결 방법
