# Blind-Dating
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/6b771471-8e6c-49b8-aa9a-ba8bbb86b5da)

## 👉🏻 프로젝트 소개
 - 관심사가 맞는 이성과 친해지고 싶을 때
 - 간편하게 여러 사람들과 대화를 나누고 싶을때
 - 이성을 소개해주는 블라인드 데이팅을 이용해주세요!    

[👉🏻블라인드 데이팅 이용해보기 Click](https://fe-zeta.vercel.app)


## 🛠프로젝트 아키텍처
![dffd](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/86abd2cb-2bb2-4700-b08f-3e4fa97df224)




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
[👉🏻 DB 상세페이지로 이동](https://www.erdcloud.com/d/FveRbu3AKcJ2Zd3ko)
   
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/346b2553-ffc7-472b-9cdd-fae6a350bbdb)


## 🪧API 문서
[👉🏻 API 문서 상세보기](https://blind-dating.site/swagger-ui/index.html)   
API 문서는 Swagger를 통해 작성되었습니다.

## 💡담당 업무
 - Github Action을 도입하여 CI/CD를 구현했습니다.
 - 엑세스 토큰과 리프레시 토큰을 기반으로 한 인증/인가 시스템을 구현했습니다.
 - 실시간 채팅 기능 구현을 위해 웹소켓을 활용하고 메세지 브로커로 Redis를 도입했습니다.
 - 채팅방 리스트에서 웹소켓 연결을 통해 새로운 메세지가 도착할 때마다 최신 메세지 내용이 갱신되도록 했습니다.
 - Swagger를 이용하여 REST API 문서화를 진행하여 협업의 효율성을 높였습니다.

## 💡주요 기능
1. 회원가입
2. 로그인
3. 이성 추천 리스트
4. 좋아요 기능
5. 실시간 채팅

## 💡 주요 페이지

### :mag: 회원 추천 받기
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/4d68260a-5aac-4de9-8842-ab47d7b7d822)    
- 해당 페이지에서 이성을 추천받을 수 있고, 좋아요 또는 싫어요를 할 수 있습니다.
- 상대방과 내가 좋아요를 누를 시 채팅방이 생성됩니다.    

### :mag: 채팅방 리스트
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/dbbb15db-1ee5-45db-8e43-851dfb833501)    
- 내가 참여한 채팅방 리스트를 확인할 수 있습니다.
- 웹 소켓을 연결해 상대방이 문자를 보내면 실시간으로 메지를 미리 볼 수 있습니다.
- 또한, 읽지 않은 메시지 개수를 실시간으로 업데이트해줍니다.

### :mag: 채팅방
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/d5b78490-1169-4b50-a1a6-0ef9fbfe9f42)
- 웹 소켓을 연결해 상대방과 실시간 채팅을 진행할 수 있습니다.  


## :rocket: 트러블 슈팅
1. ElastiCache에서 요금 발생     
AWS에서 발생한 만원 이상의 요금을 조사하기 위해 요금 명세서를 확인해 보았습니다. 결과적으로 Redis를 위한 ElastiCache에서 요금이 발생한 것을 확인할 수 있었습니다. 
해당 서비스의 구성을 확인해 본 결과 노드의 수가 3개로 설정되어있었고, 이 3개의 노드가 24시간 동안 작동하여 기본 제공량인 750시간을 초과하면서 요금이 발생한 것을 파악했습니다.
이에 따른 과금 문제를 해결하기 위해 노드의 수를 3개에서 1개로 줄여서 문제를 해결하였습니다.
2. Post/Patch 테스트 코드 동작시 403 Forbidden 발생    
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




