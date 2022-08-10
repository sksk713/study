---
layout: post
title: "03. 연관관계 매핑 기초"
author: "SangKyenog Lee"
tags: "[Book]자바-ORM-표준-JPA-프로그래밍"
---

# 연관관계 매핑의 요소
---
방향
- 단방향
- 양방향

`방향은 객체관계에만 존재, 테이블 관계에서는 항상 양방향이다. 또한 객체에서는 참조를 통해, 테이블은 외래 키를 통해 연관관계를 맺는다.`

<br>

다중성
- 다대일
- 일대다
- 다대다

<br>

연관관계의 주인
- 양방향 연관관계에서는 주인이 존재해야함

<br>

# 저장
---
```java
public void testSave() {

    Team team1 = new Team("team1", "팀1");
    em.persist(team1);

    Memeber member1 = new Member("member1", "회원1");
    member1.setTeam(team1);
    em.persist(member1);
}
```

`JPA에서 엔티티를 저장할 때, 연관된 모든 엔티티는 영속 상태여야 한다.`

<br>

# 조회
---
조회 방식은 두가지가 있다.
- 객체 그래프 탐색(GET 메소드)
- 객체지향 쿼리 사용(JPQL)

<br>

# 수정
---
수정은 별다른 메소드가 없고 영속성 컨텍스트에서 수정이 감지되면 플러시가 발생할 때, 자동으로 DB에 반영된다.
- IDENTITY 전략인 경우 어떻게 될까?

<br>

# 양방향 연관관계 매핑
---

```java

// Member
public class Memeber {

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public void setTeam(Team team) {
        this.team = team;
    }
}


// Team 
public class Team {

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<Member>();

}
```

`외래 키 하나로 양쪽이 서로 조인할 수 있기 때문에, mappedBy를 통해 연관관계의 주인 정해준다. 연관관계의 주인은 외래 키가 존재하는 곳`

<br>

# 양방향 연관관계 저장
---

```java
// set 메소드를 수정해서 양방향으로 관리할 수 있는 메소드를 만들자
public void save() {

    Team team1 = new Team();
    em.persist(team1);

    Member member1 = new Member();
    member1.setTeam(team1);
    em.persist(member1);

    public void setTeam(Team team) {

    if (this.team != null) {
        this.team.getMembers().remove(this);
    }
        this.team = team;
        team.getMembers().add(this);
    }
}   
```

- team 엔티티 list를 삭제 하지 않아도 대부분의 경우 문제는 발생하지 않는다. 하지만 관계 변경 직후, 영속성 컨텍스트에 남아있다면 원치않는 값을 받을 수 있다.
- 순수한 객체를 고려해서 양쪽 방향에 모두 값을 입력해주는 것이 좋다.
