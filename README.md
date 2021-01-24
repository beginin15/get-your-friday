# Get Your Friday
| *Get Your Freitag*

## 서비스 소개
Freitag 홈페이지에 업데이트된 신상품을 실시간으로 알려주는 Telegram Bot입니다.

- Freitag 제품들은 트럭 방수천을 재활용하여 패턴을 제작하기 때문에 모든 가방이 유일무이합니다.
- 오프라인, 온라인을 불문하고 유니크한 패턴을 가진 가방은 금방 판매되며 재입고되지 않습니다.

*Get Your Friday*는 홈페이지에 제품이 업데이트되자마자 패턴을 확인하고 원하는 제품을 바로 구입할 수 있도록 도와드립니다.

## 사용 방법
![how_to_use](https://user-images.githubusercontent.com/33659848/105625811-013ba900-5e6f-11eb-9a1c-e73acb320561.png)

## 동작 매커니즘
### 클라이언트-서버
![mechanism_between_client_and_server](https://user-images.githubusercontent.com/33659848/105624790-bf5b3480-5e67-11eb-971a-e07e0cc6c4b3.png)

### 서버 내부
![mechanism_server](https://user-images.githubusercontent.com/33659848/105626146-6ee8d480-5e71-11eb-9c9f-7cd717fcc33a.png)

## 기술 스택
### 1. Backend
- Java 8

- Spring boot
    - 의존성 주입, 빈 라이프사이클 관리, 요청 스레드 관리, 스케줄링 등 Spring 프레임워크에서 제공하는 기능을 이용하여 코드 생산성을 높이고자 했습니다.
    
- JUnit5

- Selenium
    - 본래 웹 브라우저 자동화 프레임워크이지만 HTML를 쉽게 파싱할 수 있기 때문에 스크랩핑할 때 많이 활용됩니다.
    - 스크랩핑 하고자 하는 [Freitag](https://www.freitag.ch/en) 홈페이지의 데이터가 동적으로 렌더링되기 때문에 정적 파싱 라이브러리(Jsoup, Beautiful Soup 등) 대신 사용했습니다. 
 
- Amazon DynamoDB
    - 본 프로젝트의 데이터 모델은 단순하기 때문에 복잡한 연관 관계가 없습니다. 또한 안전성보다 속도가 중요하기 때문에 NoSQL을 선택했습니다.
    - Amazon DynamoDB 프리티어는 기간에 상관없이 일정 수준 미만의 사용량은 무료로 제공합니다.

### 2. Client
- Telegram Bot
    - 특히 모바일 환경에서 Notification 역할을 할 수 있는 클라이언트가 필요했습니다.
    - 사용자 입장에서 접근성이 좋고 사용 방법이 간단합니다.

### 3. Infra
### AWS EC2
 - Telegram에 등록할 수 있는 Webhook url은 https 프로토콜이어야 합니다. AWS EC2는 https 인증 과정이 복잡하지 않습니다.

