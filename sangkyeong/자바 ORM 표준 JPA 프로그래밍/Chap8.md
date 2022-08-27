# 프록시
---
연관관계에 놓여있는 엔티티를 실제로 사용하기 전까지 데이터베이스에서 조회를 지연시키는데 이것을 `지연 로딩`이라고 한다. 지연 로딩을 하는 이유는 만약 하나의 엔티티를 불러왔을 때, 연관관계의 엔티티를 모두 한꺼번에 조회해오면 성능적으로 효율적이지 않기 때문이다.

지연 로딩을 하기 위해서는 실제 엔티티 대신 가짜 엔티티 객체가 필요한데 이것을 `프록시 객체`라고 한다.

<br>

# 프록시 호출
---
```java
Member member = em.find(Member.class, "member1");
```
위 처럼 `EntityManager.find()`를 사용하게 되면 영속성 컨텍스트 및 데이터베이스를 조회한다.

```java
Member member = em.getReference(Member.class, "member1");
```
이 코드는 실제 엔티티 객체를 데이터베이스에서 조회하지 않고, 데이터베이스 접근을 위임한 프록시 객체를 반환한다.

`프록시 객체는 실제 클래스를 상속 받아서 만들어지므로 실제 클래스와 동일하며, 실제 객체에 대한 참조를 할 수 있는 권한이 있다`

<br>

# 프록시 객체 초기화
---
프록시 객체의 초기화는 어떤 엔티티의 메소드를 실행하게 되면 데이터베이스에서 조회 후, 실제 엔티티를 생성한다. 그 다음에 메소드를 실행해서 값을 가져온다.

<br>

# 프록시의 특징
---
1. 프록시 객체는 처음 사용 시, 한번만 초기화 된다.
2. 프록시 객체가 초기화 되었다는 것은, 프록시 객체를 통해 실제 엔티티에 접근할 수 있다는 것을 말한다.
3. 프록시 객체는 원본 엔티티를 상속받은 객체이므로 타입 체크 시에 주의해야 한다.
4. 영속성 컨텍스트에 이미 엔티티가 존재하면, 프록시 객체가 아닌 실제 엔티티를 반환한다.
5. 준영속 상태에서는 프록시 객체 초기화 시, 예외가 발생한다.

<br>

# 프록시와 식별자
---
프록시로 조회할 때, PK값을 파라미터로 전달하는데 프록시 객체는 이 PK 값을 보관하기 때문에 getId()메소드를 호출할 때 초기화를 하지 않는다.
- 단 `@Access(AccessType.PROPERTY)`로 설정한 경우에만 초기화하지 않음

<br>

# 즉시 로딩
---
즉시 로딩은 엔티티를 조회할 때 연관된 엔티티도 모두 함께 조회하는 것을 말한다.

```java
@Entity
public class Memeber {
    //...
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}
```
```java
Member member em.find(Member.class, "member1");
```
이렇게 즉시 로딩으로 설정하게 되면 Member를 조회하는 순간 Team까지 모두 조회한다.
- 여기서 JPA가 조인 쿼리를 날려서 조회를 두번하지 않고 한번에 조회한다.

<br>

# 지연 로딩
---
지연 로딩은 멤버 객체를 불러올 때, 팀 객체는 프록시 객체로 불러오는 것을 말하는데 이처럼 실제 데이터베이스 조회를 미룬다고 해서 `지연 로딩`이라고 부른다.

`지연 로딩과 즉시 로딩의 사용 시기는 애플리케이션 로직에 따라 다른데 대부분 연관관계의 엔티티와 함께 많이 사용하면 즉시 로딩을, 그렇지 않다면 지연 로딩을 하는 것이 좋다`

<br>

# 영속성 전이
---
영속성 전이란 연관관계의 엔티티까지 영속 상태로 만드는 것을 말한다.

저장
```java
@Entity
public class Parent {
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Child> children = new ArrayList<Child>();
}

private static void saveWithCascade(EntityManager em) {

    Child child1 = new Child();
    Child child2 = new Child();

    Parent parent = new Parent();
    child1.setParent(parent);
    child2.setParent(parent);
    parent.getChildren().add(child1);
    parent.getChildren().add(child2);

    em.persist(parent);
}
```
저장 외에도,

- 삭제(REMOVE)
- 병합(MERGE)
- 모두 적용(ALL)

등등 존재한다.

<br>

# 고아 객체
---
JPA는 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공한다.

```java
@Entity
public class Parent {

    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Child> children = new ArrayList<Child>();
}
```