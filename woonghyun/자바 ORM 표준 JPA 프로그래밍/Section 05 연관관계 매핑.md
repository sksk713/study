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

여기서 다음의 코드를 분석해보자.

```java
@ManyToOne
@JoinColumn(name = "TEAM_ID")
private Team team;

```

- `@ManyToOne` : 이름 그대로 다대일(N:1) 관계라는 매핑 정보다. 회원과 팀은 다대일 관계다.
- `@JoinColumn(name="TEAM_ID")` : 조인 컬럼은 외래 키를 매핑할 때 쓴다. name 속성에는 매핑할 외래 키의 이름을 지정한다. 회원과 팀 테이블은 TEAM_ID 외래 키로 연관관계를 맺으므로 이 값을 지정하면 된다. 이 어노테이션은 생략할 수 있다.

### 5.1.4 @JoinColumn

주요 속성은 다음과 같습니다.

name : 매핑할 외래키 이름

referencedColumnName : 외래 키가 참조하는 대상 테이블의 컬럼명

foreignKey (DDL) : 외래 키 제약 조건을 직접 지정할 수 있다. 이 속성은 테이블을 생성할 때만 사용한다.

## 172 ~ 173

## 5.2 연관관계 사용

### 5.2.1 저장

```java
public void testSave() {
		// 팀 1 저장
		Team team1 = new Team("team1", "팀1");

		em.persist(team1);

		// 회원 1 저장 
		Member member1 = new Member("member1", "회원1");
		member1.setTeam(team1); // 참조를 이용한 연관관계 설정
		em.persist(member1);

		// 회원2 저장
		Member member2 = new Member("member2", "회원2");
		member2.setTeam(team1); // 참조를 이용한 연관관계 설정
		em.persist(member2);
		
```

<aside>
⚠️ JPA에서 엔티티를 저장할 때, 연관된 모든 엔티티는 영속 상태여야 한다. 즉 위 예시에서 member1과 member2를 저장하려고 할때 참조하는 team1은 반드시 영속 상태여야 한다.

</aside>

중요 부분을 분석해보자.

```java
member1.setTeam(team1); // 회원 -> 팀 참조
em.persist(member1); // 저장
```

회원 엔티티는 팀 엔티티를 참조하고 저장했다. JPA는 참조한 팀의 식별자(Team.id)를 외래 키로 사용해서 적절한 등록 쿼리를 생성한다. 이 때 실행된 SQL은 다음과 같다.

```sql
INSERT INTO TEAM (TEAM_ID, NAME) VALUES ('team1, '팀1')
INSERT INTO MEMBER (MEMBER_ID, NAME, TEAM_ID) VALUES ('member1', '회원1', 'team1')
INSERT INTO MEMBER (MEMBER_ID, NAME, TEAM_ID) VALUES ('member2', '회원2', 'team1')

```

### 5.2.2 조회

연관관계가 있는 엔티티를 조회하는 방법은 크게 2가지이다. 

- 객체 그래프 탐색 (객체 연관관계를 사용한 조회)
    
    ```java
    Member member = em.find(Member.class, "member1");
    Team team = member.getTeam(); // 객체 그래프 탐색
    System.out.println("팀 이름 = " + team.getName());
    
    // 이처럼 객체를 통해 연관된 엔티티를 조회하는 것을 객체 그래프 탐색이라 한다.
    ```
    
- 객체지향 쿼리 사용 (JPQL)
    
    ```java
    private static void queryLogicJoin(EntityManager em) {
    		String jpql = "select m from Member m join m.team t where " +
    				"t.name=:teamName";
    
    		List<Member> resultList = em.createQuery(jpql, Member.class)
    				.setParameter("teamName", "팀1")
    				.getResultList();
    
    		for (Member member : resultList) {
    				System.out.println("[query] member.username" +
    						member.getUsername());
    
    		}
    }
    ```
    

### 5.2.3 수정

팀1 소속이었던 회원을 새로운 팀2에 소속하도록 수정해보자.

```java
private static void updateRelaion(EntityManager em) {
		
		// 새로운 팀2
		Team team2 = new Team("team2", "팀2");
		em.persist(team2);

		// 회원1에 새로운 팀2 설정
		Member member = em.find(Member.class, "member1");
		member.setTeam(team2);
}
```

앞서 언급했듯이, 수정은 `em.update()` 같은 메소드가 없다. 단순히 불러온 엔티티의 값만 변경해두면 트랜잭션을 커밋할 때 플러시(DB에 엔티티를 반영하는 것)가 일어나면서 초기의 스냅샷과 현재 1차 캐시의 Entity를 비교하게 된다. 달라진 경우 update 쿼리를 작성해 쓰기 지연 SQL 저장소에 저장하고, 최종적으로 해당 쿼리가 반영된다. → **이는 연관관계의 수정에서도 동일하게 동작한다.**

### 5.2.4 연관관계 제거

```java
private static void deleteRelation(EntityManager em) {
		Member member1 = em.find(Member.class, "member1");
		member1.setTeam(null);; // 연관관계 제거
}
```

### 5.2.5 연관된 엔티티 삭제

연관된 엔티티를 삭제하려면, 기존에 있던 연관관계를 먼저 제거하고 삭제해야 한다. 그렇지 않으면 외래 키 제약조건으로 인해, DB에서 오류가 발생한다. 

```java

// team 객체를 참조하고 있던 member1과 member2 객체의 team field를 null로 수정.
member1.setTeam(null);
member2.setTeam(null);

// 더이상 다른 Member 객체에서 참조하고 있지 않으므로, 삭제할 수 있다.
em.remove(team);
```

## 5.3 양방향 연관관계

지금까지는 회원에서 팀으로만 접근하는 다대일 단방향 매핑을 알아보았다. 이번에는 반대 방향인 팀에서 회원으로 접근하는 관계를 추가하자. 그래서 회원에서 팀으로 접근하고 반대 방향인 팀에서도 회원으로 접근할 수 있도록 양방향 연관관계를 매핑해보자.

1. 회원과 팀은 다대일 관계이다.
2. 팀과 회원은 일대다 관계이다.

일대다 관계의 경우 여러 건과 연관관계를 맺을 수 있으므로 Collection을 사용해야 한다. 

그렇다면 테이블에서의 관계는 어떨까? **전혀 변경 사항 없다**.

### 5.3.1 양방향 연관관계 매핑

먼저 Member 엔티티의 경우 수정 사항이 없다. Team 엔티티는 다음과 같이 수정한다.

```java
@Entity
public class Team {
    @Id
    @Column(name = "TEAM_ID")
    private String id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

		// Getter, Setter ...

}
```

- `@OneToMany` : 일대다 관계를 매핑하기 위해 어노테이션 추가.
- `mappedBy` 속성 : 양방향 매핑일 때 사용하며, 반대쪽 매핑의 필드 이름을 값으로 주면 된다. 반대쪽 매핑이 Member.team이므로 team을 값으로 주었다.

### 5.3.2 일대다 컬렉션 조회

이제 팀에서 해당 팀에 소속된 회원을 조회해보자.

```java
public void biDirection() {
		Team team = em.find(Team.class, "team1");
		List<Member> members = team.getMembers();
		
		for (Member member : members) {
				System.out.println("member.username = " + 
						member.getUsername();
		}
}
```

## 5.4 연관관계의 주인

> @OneToManey는 직관적으로 이해가 된다. 문제는 `mappedBy` 속성이다. 왜 필요할까?
> 

엄밀히 말하면, 객체에는 양방향 연관관계라는 것이 없다. 정확히 말하자면 단방향 연관관계 2개를 양방향 연관관계인 것처럼 보이게 할 뿐이다. 반면, DB 테이블의 경우 앞서 설명했듯이 FK 하나로 양쪽이 서로 JOIN할 수 있다. 따라서 테이블은 외래 키 하나만으로 양방향 연관관계를 맺는다.

엔티티를 단방향으로 매핑하면(참조하면) 참조를 하나만 사용하므로 이 참조로 외래 키를 관리하면 된다. 그런데 엔티티를 양방향으로 매핑하면 `회원 -> 팀` , `팀 -> 회원` 두 곳에서 서로를 참조한다.

엔티티를 양방향 연관관계로 설정하면 객체의 참조는 둘인데, 외래 키는 하나다. 따라서 둘 사이에 차이가 발생한다. (Member의 외래키 역할은 Team 객체, Team의 외래키 역할은 List<Member>) 그렇다면 둘 중 어떤 관계를 사용해서 외래 키를 관리해야 할까?

이런 차이로 인해 JPA에서는 두 객체 연관관계 중 하나를 정해서 테이블의 외래키를 관리해야 하는데 이것을 연관관계의 주인이라고 한다.

### 5.4.1 양방향 매핑의 규칙 : 연관관계의 주인

**양방향 연관관계 매핑 시, 지켜야 할 규칙이 있는데 두 연관관계 중 하나를 연관관계의 주인으로 정해야 한다. 연관관계의 주인만이 데이터베이스 연관관계와 매핑되고 외래 키를 관리(등록, 수정, 삭제)할 수 있다. 반면에 주인이 아닌 쪽은 읽기만 할 수 있다. 어떤 연관관계를 주인으로 정할지는 `mappedBy` 속성을 사용하면 된다.**

- 주인은 `mappedBy` 속성을 사용하지 않는다.
- 주인이 아니면 `mappedBy` 속성을 사용해서 속성의 값으로 연관관계의 주인을 지정해야 한다.

그렇다면 `Member.team` , `Team.members` 둘 중 어떤 것을 연관관계의 주인으로 정해야할까?

![Untitled](Section%2005%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20240a4828ef984418bba720ebaf9594bd/Untitled%201.png)

```java
class Member {
		@ManyToOne
		@JoinColumn(name = "TEAM_ID")
		private Team team;
		..
}

class Team {
		@OneToMany
		private List<Member> members = new ArrayList<Member>();
		...
}
```