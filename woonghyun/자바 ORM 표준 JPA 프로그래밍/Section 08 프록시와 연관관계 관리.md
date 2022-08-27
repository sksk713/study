# Section 08.  프록시와 연관관계 관리

생성일: 2022년 8월 27일 오전 9:22

## 8.1 프록시

다음의 경우를 살펴보자.

```java
@Entity
public class Member {
		private String username;

		@ManyToOne
		private Team team;

		// Getter, Setter ...
}

@Entity
public class Team {
		private String name;

		// Getter, Setter ...
		...
}

// 메인 로직
public void printUserTeam(String memberId) {
		Member member = em.find(Member.class, memberId);
		Team team = member.getTeam();
		Sout(member.getUsername);
		Sout(team.getTeam);
} 

// 만약 Member 객체에서, Team 객체를 호출할 일이 없다면?
	
```

→ 지연 로딩을 통해서 Team 객체가 실제로 호출될 때 해당 객체를 DB에서 가져오는 기능을 제공한다.

그런데 이런 기능을 사용하려면, 실제 엔티티 객체 대신에 DB 조회를 지연할 수 있는 가짜 객체가 필요한데, 이를 프록시 객체라고 한다. 

### 8.1.1 프록시 기초

```java
Member member = em.getReference(Member.class, "member1");
```

`em.getReference()` 를 통해 실제 엔티티 대신에 데이터베이스 접근을 위임한 프록시 객체를 위임한다.

프록시 객체는 다음과 같은 특징이 있다.

- 실제 클래스를 상속받아 만들어지므로, 실제 클래스와 모양이 같다.
- member.getTeam()처럼 실제 사용될 때 데이터베이스를 조회해서 실제 엔티티 객체를 생성한다. 이를 **“프록시 객체의 초기화"**라고 한다. 초기화 과정은 다음과 같다.
    - 프록시 객체에 member.getTeam을 호출한다.
    - **프록시 객체는 실제 엔티티가 생성되어 있지 않으면 영속성 컨텍스트에 실제 엔티티 생성을 요청하는데 이것을 초기화라고 한다.**
    - 영속성 컨텍스트는 DB를 조회해 실제 엔티티 객체를 생성한다.
    - 프록시 객체는 생성된 실제 엔티티 객체의 참조를 `Member target` 멤버변수에 보관한다.
    - 실제 엔티티 객체의 getTeam을 호출해 결과를 반환한다.
- 객체를 처음 사용할 때, 한번만 초기화된다.
- 프록시 객체를 초기화한다고 해서 프록시 객체가 실제 엔티티로 바뀌는 것은 아니다.
- 프록시 객체는 원본 엔티티를 상속받은 객체이므로, 타입 체크시에 주의해서 사용해야 한다.
- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 데이터베이스를 조회할 필요가 없으므로, em.getReference()를 사용해서 프록시가 아닌 실제 엔티티를 반환한다.
- 초기화는 영속성 컨텍스트의 도움을 받아야 가능하다. 따라서 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태의 프록시를 초기화하면 문제가 발생한다.

### 8.1.2 프록시와 식별자 (PK)

프록시의 초기화를 통해 실제 엔티티를 조회할 때, 식별자를 기준으로 조회를 한다. 따라서 프록시 객체에는 실제 엔티티의 식별자가 포함되어 있다. 만약 `MemberProxy.getId()` 를 하더라도 이미 프록시에 식별자가 보관되어 있으므로 실제 엔티티를 조회하지는 않는다.

### 엔티티 접근 방식의 차이

- `Access(AccessType.PROPERTY)` : 초기화가 발생하지 않는다.
- `Access(AccessType.FIELD)` : getId()가 식별자만 조회하는 메소드인지, 다른 필드까지 활용해서 어떤 일을 하는 메소드인지 알지 못하므로 프록시 객체의 초기화가 발생한다.

### 8.1.3 프록시 확인

`PersistenceUnitUtil.isLoaded(Object Entity)` 메소드를 사용하면, 프록시 인스턴스의 초기화 여부를 확인할 수 있다. 아직 초기화되지 않은 프록시 인스턴스는 false를 반환한다. 이미 초기화되었거나 프록시 인스턴스가 아니라면 true를 반환한다. 

```java
em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(entity);
// 또는
emf.getPersistenceUnitUtil().isLoaded(entity);

// 클래스 명에 ..javassist.. 가 포함되어 있다면 프록시 객체이다.
```

<aside>
ℹ️ JPA 표준에는 프록시 객체의 강제 초기화 메소드가 없다. 단 하이버네이트의 initialize() 메소드를 사용하면 프록시를 강제로 초기화할 수 있다.

</aside>

## 8.2 즉시로딩과 지연로딩

### 8.2.1 즉시 로딩

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "TEAM_ID")
private Team team;
```

<aside>
ℹ️ 즉시 로딩을 통해 회원과 팀을 조회하는 쿼리를 2번 호출할 것 같지만, 실제로는 JOIN을 이용해 1번만 호출한다. 따라서 한번의 쿼리로 Member와 Team 객체 모두를 조회할 수 있다.

</aside>

### 8.2.2 지연 로딩

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "TEAM_ID")
private Team team;
```

이 경우 Member 엔티티를 조회했을 때 Team 객체에는 프록시 객체를 넣어둔다. 

### 8.3.1 프록시와 컬렉션 래퍼

하이버네이트는 엔티티를 영속 상태로 만들 때, 엔티티에 컬렉션이 있으면 컬렉션을 추적하고 관리할 목적으로 원본 컬렉션을 하이버네이트가 제공하는 내장 컬렉션으로 변경하는데, 이를 컬렉션 래퍼라고 한다. 

→ 엔티티를 지연로딩하면 프록시 객체를 사용해서 지연 로딩을 수행하지만, 주문 내역과 같은 컬렉션은 컬렉션 래퍼가 지연 로딩을 처리해준다. 

<aside>
ℹ️ 컬렉션 래퍼 또한 컬렉션에 대한 프록시 역할을 하므로, 따로 구분하지는 않고 모두 프록시라 부른다.

</aside>

<aside>
⚠️ 참고로 member.getOrders()와 같이 컬렉션을 조회해도 초기화는 진행되지 않는다. member.getOrders().get(0)와 같이 컬렉션에서 실제 데이터를 조회할 때 비로소 초기화가 진행된다.

</aside>

### 8.3.2 JPA의 기본 FETCH 전략

- `ManyToOne` , `OneToOne` : 즉시 로딩 (FetchType.EAGER)
- `OneToMany` , `ManyToMany` : 지연 로딩 (FetchType.LAZY)

→ 연관된 엔티티가 하나면 즉시 로딩, 컬렉션이면 지연 로딩을 사용한다.  추천하는 방식은 일단 모든 연관 엔티티에 지연 로딩을 걸어두고 이후에 점차 점차 즉시 로딩을 필요한 곳에 적용하는 것이다.

## 8.4 영속성 전이 : CASCADE

만약 특정 엔티티를 영속 상태로 만들 때, 연관된 엔티티도 함께 영속 상태로 만들고 싶으면 영속성 전이 기능을 사용하면 된다. 이런 기능을 JPA에선 CASCADE 옵션으로 지원한다. 

### 8.4.1 영속성 전이 : 저장

```java
@OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
```

위와 같이 설정을 해두면 Child 엔티티도 함께 영속 상태화해서 저장한다. 

### 8.4.2 영속성 전이 : 삭제

```java
@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
```

## 8.5 고아 객체

JPA는 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공하는데, 이를 고아 객체 제거라 한다. 

이 기능을 사용해서 부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제된다.

```java
@OneToMany(mappedBy = "parent", orphanrRemoval = true)
private List<Child> children = new ArrayList<>();

// 이제 다음 사용 코드를 보자.
Parent parent1 = em.find(Parent.class, id);
parent1.getChildren().remove(0); // 자식 엔티티를 컬렉션에서 제거
// 고아 객체 옵션으로 인해, 컬렉션에서의 엔티티 삭제가 곧 DB 데이터 삭제로 이어진다.
```