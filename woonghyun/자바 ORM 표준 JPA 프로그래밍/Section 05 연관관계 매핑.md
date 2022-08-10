# Section 05. 연관관계 매핑

생성일: 2022년 7월 25일 오후 11:24

## 5.1 단방향 연관관계

연관관계 중에선 다대일(N:1) 단방향 관계를 가장 먼저 이해해야 한다.

- 회원과 팀이 있다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계다.

![Untitled](Section%2005%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20240a4828ef984418bba720ebaf9594bd/Untitled.png)

- 객체 연관관계
    - 회원 객체는 Member.team 필드로 Team 객체와 연관관계를 맺는다.
    - 회원 객체와 팀 객체는 단방향 관계다. Member 객체는 Member.team 필드를 통해서 팀을 알 수 있지만, 반대로 팀은 회원을 알 수 없다.
- 테이블 연관관계
    - 회원 테이블은 TEAM_ID FK로 팀 테이블과 연관관계를 맺는다.
    - 회원 테이블과 팀 테이블은 양방향 관계다. 회원 테이블의 TEAM_ID FK를 통해서 Member와 Team을 JOIN할 수 있고, 반대로 팀과 회원도 JOIN할 수 있다. 예를 들어, MEMBER 테이블의 TEAM_ID FK 하나로 MEMBER JOIN TEAM과 TEAM JOIN MEMBER 둘 다 가능하다.

외래 키 하나로 어떻게 양방향으로 조인하는지 알아보자. 다음은 회원과 팀을 Join하는 SQL이다.

```sql
SELECT *
  FROM MEMBER M
	JOIN TEAM T ON M.TEAM_ID = T.ID
```

다음은 반대로 팀과 회원을 Join하는 SQL이다.

```sql
SELECT *
  FROM TEAM T
  JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID
```

- 객체 연관관계와 테이블 연관관계의 가장 큰 차이
    - 참조를 통한 연관관계는 언제나 단방향이다. 객체 간에 연관관계를 양방향으로 만들고 싶으면 반대쪽에도 필드를 추가해서 참조를 보관해야 한다. 결국 연관관계를 하나 더 만들어야 한다. 이렇게 양쪽에서 서로 참조하는 것을 **양방향 연관관계**라 한다. 하지만 정확히 얘기하면 이는 양방향 관계가 아니라 서로 다른 단방향 관계 2개다. 반면에 테이블은 외래 키 하나로 양방향으로 Join할 수 있다.
        
        다음은 단방향 연관관계다.
        
        ```java
        class A {
        		B b;
        }
        
        class B {}
        ```
        
        다음은 양방향 연관관계다.
        
        ```java
        class A {
        		B b;
        }
        
        class B {
        		A a;
        }
        ```
        
- 객체 연관관계 vs 테이블 연관관계 정리
    - 객체는 참조(주소)로 연관관계를 맺는다.
    - 테이블은 FK로 연관관계를 맺는다.
    - 참조를 사용하는 객체의 연관관계는 단방향이다.
    - 외래키를 사용하는 테이블의 연관관계는 양방향이다. ( `A JOIN B` 가 가능하면 `B JOIN A` 도 가능하다.)
    - 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.
    

### 5.1.1 순수한 객체 연관관계

```java
public class Member {
		private String id;
		private String username;

		private Team team;

		public void setTeam(Team team) {
				this.team = team;
		}

		// Getter, Setter
}

public class Team {
		private String id;
		private String name;

		// Getter, Setter
}
```

```java
public static void main(String[] args) {
		Member member1 = new Member("member1", "회원1");
		Member member2 = new Member("member2", "회원2");
		Team team1 = new Team("team1", "팀1");

		member1.setTeam(team1);
		member2.setTeam(team1);

		Team findTeam = member1.getTeam();
```

### 5.1.2 테이블 연관관계

```sql
CREATE TABLE MEMBER (
		MEMBER_ID VARCHAR(255) NOT NULL,
		TEAM_ID VARCHAR(255),
		USERNAME VARCHAR(255),
		PRIMARY KEY (MEMBER_ID)
)

CREATE TABLE TEAM (
		TEAM_ID VARCHAR(255) NOT NULL,
		NAME VARCHAR(255),
		PRIMARY KEY (TEAM_ID)
)

ALTER TABLE MEMBER ADD CONSTRAINT FK_MEMBER_TEAM
		FOREIGN KEY (TEAM_ID)
		REFERENCES TEAM
```

이제 다음 SQL을 실행해서 회원1과 회원2를 팀1에 소속시키자.

```sql
INSERT INTO TEAM(TEAM_ID, NAME) VALUES('team1', '팀1');
INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME)
VALUES('member1', 'team1', '회원1');
INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME)
VALUES('member2', 'team1', '회원2');

SELECT T.*
	FROM MEMBER M
				JOIN TEAM T ON M.TEAM_ID = T.ID
 WHERE M.MEMBER_ID = 'member1'
```

### 5.1.3 객체 관계 매핑

지금까지

- 객체만 사용한 연관관계
- 테이블만 사용한 연관관계

를 각각 알아보았다. 이제 JPA를 사용해서 둘을 매핑해보자.

```java
// Member 클래스

@ManyToOne
@JoinColumn(name = "TEAM_ID")
private Team team;

// Team 클래스
package jpabook.start;

import javax.persistence.*;

@Entity
public class Team {
    @Id
    @Column(name = "TEAM_ID")
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```