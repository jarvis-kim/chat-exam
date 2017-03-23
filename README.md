# 채팅 서버 예제(네티)

- 블로그를 참고(거의 따라치기)를 하여 예제를 만들었다.(http://wonwoo.ml/index.php/post/553)

## Command
- J : 입장하기
    - J [사용할 아이디]
- A : 전체 보내기
    - A [메시지]
- T : 특정 사용자에게 보내기
    - T [target id] [메시지]
- E :  종료

## 실행해보기
1. telnet localhost [port]
2. J jarvis
    - jarvis로 입장
3. A hello~
    - hello~ 전체 메시지 보내기
4. E
    - 퇴장

---

- 남은 과제
    - AttributeKey<> 가무엇인지 파악.
    - ChannelGroup에 new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)를 하는 이유
    - Unit 테스트는 어떻게...?
        - 일단 찾아보니 EmbeddedChannel을 사용.
    