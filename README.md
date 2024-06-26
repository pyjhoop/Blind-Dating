# Blind-Dating
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/6b771471-8e6c-49b8-aa9a-ba8bbb86b5da)

## 👉🏻 프로젝트 소개
 - 관심사가 맞는 이성과 친해지고 싶을 때
 - 간편하게 여러 사람들과 대화를 나누고 싶을때
 - 이성을 소개해주는 블라인드 데이팅을 이용해주세요!    

👉🏻[블라인드 데이팅 이용해보기 Click](https://fe-zeta.vercel.app)   




## ⚙기술 스택

### ✔ Back-end
<div>
 <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
 <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
 <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
 <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
 <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>
 </br>
 <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"/>
 <img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"/>
 <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"/>
 <img src="https://img.shields.io/badge/elasticache-527FFF?style=for-the-badge&logo=elasti&logoColor=white"/>
 <img src="https://img.shields.io/badge/codedeploy-007054?style=for-the-badge&logo=elasti&logoColor=white"/>
</div>

### ✔ Dev tools
<div>
 <img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white"/>
 <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
 <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/>
</div>    

## 🪧DB 설계
👉🏻[DB 상세페이지로 이동](https://www.erdcloud.com/d/FveRbu3AKcJ2Zd3ko)
   
<img width="604" alt="image" src="https://github.com/pyjhoop/Blind-Dating/assets/59335316/c077cb5c-3fe5-4d86-b415-973efaba3509">


## 💡주요 기능
1. 일반 로그인 / 회원가입
2. 이성 추천 및 좋아요 기능
3. 실시간 채팅
4. 게시글 CRUD

## 💡담당 업무
 - Jenkins와 Nginx로 CI/CD와 무중단 배포 구현
 - 엑세스 토큰과 리프레시 토큰을 기반으로 한 인증/인가 시스템을 구현했습니다.
 - 실시간 채팅 기능 구현을 위해 웹소켓을 활용하고 메세지 브로커로 STOMP 적용했습니다.
 - 채팅방 리스트에서 웹소켓 연결을 통해 새로운 메세지가 도착할 때마다 최신 메세지 내용이 갱신되도록 했습니다.
 - 신뢰도 있는 API를 제공하기 위해 Restdocs를 작성하였고, 테스트를 위해 OpenAPI로 사양 변경 후 SWAGGER와 연동했습니다.



## :rocket: 개발 이슈
### 비관적 락을 이용한 동시성 이슈 해결

여러명의 유저가 동일한 게시판을 조회할 때 조회수의 증가가 분실된 현상이 발생했습니다. 이는 여러 트랜잭션이 동일한 로우의 데이터를 수정하면서 발생한 이슈였고, 이를 해결하기 위해
JPA의 락 중에 비관적 락을 이용해 동시성 문제를 해결했습니다.    

👉🏻[JPA 동시성 이슈 해결](https://laohcoding.tistory.com/entry/JPA-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88-%ED%95%B4%EA%B2%B0%ED%95%98%EA%B8%B0)

### 다중 패치 조인시 오류 발생
추천 유저 조회시 해당 엔티티와 OneToMany관계를 맺는 관심사와 질의 답변 엔티티도 함께 조회해야하고, 이는 N+1의 문제를 야기했습니다. 해당문제를 해결하기 위해 하나의 쿼리로 유저정보, 관심사, 질의답변 내용을
조회하고자 패치 조인을 시도했고, 테스트시 MultipleBagFetchException이 발생했습니다. 해당 예외는 List 타입의 2개 이상의 엔티티를 패치조인 할때 발생하는 예외고, 이 문제를 해결하기 위해 
BatchSize를 적용했습니다. 기본적으로 추천 유저는 페이지네이션 처리를 하였고, 한 페이지당 10명을 조회하기에 BatchSize는 10으로 정해서 MultipleBagFetchException과 N+1 문제를 해결했습니다.   
👉🏻[MultipleBagFetchException 해결](https://laohcoding.tistory.com/entry/JPA-cannot-simultaneously-fetch-multiple-bags-%EC%98%A4%EB%A5%98)

### Redis를 이용한 AccessToken 재발급 속도 17% 향상
기존에 RDB에 RefreshToken을 저장한 뒤 AccessToken 재발급시에 사용했습니다. AccessToken의 만료시간이 1시간이고 프론트쪽에서 새로고침하면 토큰을 초기화시켜서 재요청 한다고 하셔서 추후에 
RDB의 사용량이 증가할때 토큰 재발급의 속도가 느려질거라 생각했습니다. 이를 해결하기 위해 조회 속도가 빠르고 RDB와는 다른 DB를 찾게되었고, 결국 인메모리 방식의 DB인 Redis를 적용했습니다.
RDB 평균응답 속도는 34ms, Redis 평균응답 속도는 29ms로 측정되었고, 응답속도가 17% 향상되었습니다.

### ElastiCache에서 요금 발생  
AWS에서 발생한 만원 이상의 요금을 조사하기 위해 요금 명세서를 확인해 보았습니다. 결과적으로 Redis를 위한 ElastiCache에서 요금이 발생한 것을 확인할 수 있었습니다. 
해당 서비스의 구성을 확인해 본 결과 노드의 수가 3개로 설정되어있었고, 이 3개의 노드가 24시간 동안 작동하여 기본 제공량인 750시간을 초과하면서 요금이 발생한 것을 파악했습니다.
이에 따른 과금 문제를 해결하기 위해 노드의 수를 3개에서 1개로 줄여서 문제를 해결하였습니다.

### Post/Patch 테스트 코드 동작시 403 Forbidden 발생    
Get 테스트 코드는 잘 실행되는데 Post, Patch, Delete 테스트 코드 실행 시 csrfToken이 요청 시 주어지지 않아 403 Forbidden이 발생하는 것을 확인하였습니다.
이에 대해 조사한 결과, @WebMvcTest는 보안 구성을 자동으로 로드하지 않는 것을 확인하였습니다. 이를 해결하기 위해 SecurityConfig 클래스를 `@Import(SecurityConfig.class)`를 추가해
로드해주고, `csrf().disable()` 설정을 통해 csrfToken을 사용하지 않도록 함으로써 해당 문제를 해결할 수 있었습니다.




## 👻 Blind-Dating 팀원들!
<table>
  <tbody>
    <tr>
     <th>Role</th>
     <th>Name</th>
     <th>Github</th>
    <tr/>
    <tr>
     <td>팀장 FE</td>
     <td>Ted JV</td>
     <td>https://github.com/ted-jv</td>
    </tr>
    <tr>
     <td>팀원 FE</td>
     <td>mijeong</td>
     <td>https://github.com/snakechickensoup</td>
    </tr>
    <tr>
     <td>팀원 BE</td>
     <td>박연준</td>
     <td>https://github.com/pyjhoop</td>
    </tr>
      
  </tbody>
</table>




