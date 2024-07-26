# JWT_OAuth_Login

![Layer 5](https://github.com/user-attachments/assets/fea72535-20ad-4c7d-9706-d8e5f22df1e1)

이 프로젝트는 **JWT(JSON Web Token), Redis, Spring Security 등을 활용하여 유저 CRUD 기능과 로그인/로그아웃, 이메일 인증 기능 등을 구현**한 내용을 포함하고 있습니다. 자세한 진행 과정과 코드 설명은 [Velog: 로그인 & 회원가입 프로젝트](https://velog.io/@chanmi125/series/%EB%A1%9C%EA%B7%B8%EC%9D%B8-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%A0%95%EB%A6%AC) 시리즈에서 확인하실 수 있습니다.

## 프로젝트 목표

이 프로젝트는 안전하고 효율적인 사용자 관리 시스템을 구축하여, JWT와 OAuth를 통한 강력한 인증 메커니즘을 제공하고자 합니다. Redis를 사용하여 토큰 관리를 효율적으로 처리하고 사용자 경험을 향상시키는 것을 목표로 합니다. 또한, 기존의 미숙했던 코드 방식을 더욱 효율적이고 범용성 있게 개선하여 실제 현업에 가까운 사용자 관리 시스템을 구축하고자 합니다.

## 주요 기능

- **회원 가입**: 사용자가 회원 가입을 할 수 있으며, 이메일 인증을 통해 계정을 활성화합니다.

- **자체 로그인**: 사용자는 등록된 이메일과 비밀번호로 로그인할 수 있으며, JWT를 사용하여 인증 토큰을 발급합니다.
- **OAuth 2.0 로그인**: 사용자는 외부 서비스 제공자(`Google`, `Naver`, `Kakao`)를 통해 로그인할 수 있으며, JWT를 사용하여 인증 토큰을 발급합니다.
- **로그아웃**: 사용자는 로그아웃 요청을 보내면 해당 사용자의 `Refresh` 토큰을 Redis에서 등록해 사용하지 못하도록 합니다.
- **회원 정보 수정**: 로그인된 사용자는 자신의 정보를 수정할 수 있으며, 변경된 정보는 데이터베이스에 업데이트됩니다.
- **회원 조회, 삭제**: 데이터베이스와의 연동을 통해 특정 회원을 조회하거나 삭제할 수 있습니다.
- **이메일 인증**: 회원 가입 시 이메일을 통해 인증 링크를 전송하고, 링크를 클릭하여 계정을 인증할 수 있습니다.
- **계정 잠금 처리**: 로그인 실패 횟수를 추적하고 최대 실패 횟수를 초과할 경우 계정을 잠그며, 일정 시간이 지나면 자동으로 잠금이 해제됩니다.

## 💻 개발 환경

- **Windows 10 64-bit**
- **IntelliJ Ultimate**
- **JDK 17**
- **Spring Boot 3.3.1**
- **MySQL 8.0.35**
- **Maven**

## 기술 스택

- **Spring Boot**: 주 언어 및 웹 애플리케이션 개발에 사용

- **Spring Security**: 인증 및 권한 부여 처리
- **JWT (JSON Web Token)**: 인증 토큰 발급 및 관리에 사용
- **OAuth 2.0**: 소셜 로그인 관리에 사용
- **JavaMailSender**: 이메일 인증에 사용
- **Jasypt**: 설정 파일 보안을 위해 사용
- **Redis**: 토큰 저장소로 사용
- **MySQL**: 데이터 저장에 사용

## API 명세

| **엔드포인트**                    | **메서드** | **설명**                           | **요청 형식**                                                        | **응답 형식**                                                      | **상태 코드**                                                                                          |
|-----------------------------------|------------|------------------------------------|-----------------------------------------------------------------------|---------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| `/auth/login`                     | POST       | 사용자를 로그인시키고 JWT를 발급합니다. | `{"email": "user@example.com", "password": "password123"}`             | `{"status": 200, "message": "로그인에 성공했습니다.", "data": null}` | 200 OK: 로그인 성공<br>404 NOT FOUND: 사용자 찾을 수 없음<br>401 UNAUTHORIZED: 비활성화 또는 비밀번호 오류<br>423 LOCKED: 계정 잠금 |
| `/auth/logout`                    | DELETE     | 사용자의 Refresh Token을 무효화하여 로그아웃합니다. | `Refresh-Token` 헤더에 토큰 포함                                      | 없음 (`204 NO CONTENT`)                                          | 204 NO CONTENT: 로그아웃 성공<br>500 INTERNAL SERVER ERROR: 로그아웃 실패                                                        |
| `/user/signup`                    | POST       | 새로운 사용자를 등록합니다.         | `{"email": "user@example.com", "password": "password123", "name": "John Doe"}` | `{"status": 201, "message": "회원 가입이 성공적으로 실행되었습니다. 이메일 인증을 완료해주세요.", "data": {"userId": "12345"}}` | 201 CREATED: 회원 가입 성공<br>400 BAD REQUEST: 이미 등록된 이메일 |
| `/user/{userId}`                  | GET        | 사용자의 정보를 조회합니다.          | 없음 (URL 파라미터: `userId`)                                         | `{"status": 200, "message": "회원 정보를 성공적으로 조회했습니다.", "data": {"userId": "12345", "name": "John Doe"}}` | 200 OK: 정보 조회 성공<br>404 NOT FOUND: 사용자를 찾을 수 없음                                                    |
| `/user/{userId}`                  | PUT        | 사용자의 정보를 수정합니다.          | `{"email": "newemail@example.com", "name": "New Name"}`                | `{"status": 200, "message": "회원 정보를 성공적으로 수정했습니다. 이메일 인증을 완료해주세요.", "data": {"userId": "12345", "name": "New Name"}}` | 200 OK: 정보 수정 성공<br>400 BAD REQUEST: 잘못된 요청                                                        |
| `/user/{userId}`                  | DELETE     | 사용자를 삭제합니다.                 | 없음 (URL 파라미터: `userId`)                                         | `{"status": 204, "message": "회원 정보를 성공적으로 삭제했습니다.", "data": null}` | 204 NO CONTENT: 사용자 삭제 성공<br>404 NOT FOUND: 사용자를 찾을 수 없음                                             |
| `/user/verify/{token}`            | GET        | 사용자의 이메일 인증을 완료합니다.   | 없음 (URL 파라미터: `token`)                                           | `{"status": 200, "message": "이메일 인증이 완료되었습니다.", "data": {"userId": "12345"}}` | 200 OK: 인증 성공                                                                                   |
| `/oauth2/login/success`           | GET        | OAuth2 로그인 성공 후 처리합니다.    | 없음 (OAuth2 인증 후 호출됨)                                          | `{"status": 200, "message": "로그인 성공", "data": {"name": "John Doe", "email": "user@example.com"}}` | 200 OK: 로그인 성공                                                                                   |
| `/oauth2/login/failure`           | GET        | OAuth2 로그인 실패 후 처리합니다.    | 없음 (OAuth2 인증 실패 후 호출됨)                                      | `{"status": 401, "message": "로그인 실패", "data": null}`           | 401 UNAUTHORIZED: 로그인 실패                                                                                   |

## 실행 방법

1. 프로젝트를 클론합니다.
   ```bash
   git clone https://github.com/kcm02/JWT_OAuth_Login.git
   ```

2. 프로젝트 디렉토리로 이동합니다.
   ```bash
   cd JWT_OAuth_Login
   ```

3. 필요한 설정 (데이터베이스 연결 정보, 이메일 설정 등)을 수정합니다.

4. 프로젝트를 빌드 후 실행합니다.
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. 브라우저에서 `http://localhost:9090` 등의 URL로 접속하여 애플리케이션을 사용할 수 있습니다.

---
