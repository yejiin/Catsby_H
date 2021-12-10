# Catsby 😺


### 밥그룻(QR코드) 중심 길고양이 관리 애플리케이션

<br>

## 🚩 목표
길고양이의 밥그릇에 QR코드를 부착, 밥그릇 중심으로 캣맘 소통체계 구축

<br>

## 📄 기능
- 밥그릇에 부착할 QR 코드 생성 및 밥그릇 중심으로 사용자 Grouping (커뮤니티 형성)
- 고양이 먹이 급여 완료 알림 및 먹이 급여 스케줄 관리
- 위치 기반 동네 정보 교류 커뮤니티
- 위치 기반 동네 길고양이 정보 제공

<br>

## 🛠 Architecture
![architecture](https://user-images.githubusercontent.com/63090006/145583712-c691fa3d-7e1d-4bba-bd05-710c010e53b2.PNG)

<br>

## 🙋‍♀️ 담당 역할
 ### Android & Backend
  - 서버 인프라 구축
    - AWS EC2, RDS, S3 생성 
  - 카카오 로그인
    - 카카오 로그인 API (Android)  
    - Firebase custom token 생성 (BackEnd)
  - 급여 알림
    - Firebase Messaging Cloud Service 사용 (BackEnd)
      - 사용자에게 PUSH 알림 전송
  - 알림 스케줄링
    - Spring Scheduler 사용 (BackEnd)
      - 일정시간 내 급여되지 않은 밥그릇의 사용자 확인 및 알림 전송
  
<br>

> GitLab으로 이전하기 전 코드 [repositoy(Catsby)](https://github.com/yejiin/Catsby)
