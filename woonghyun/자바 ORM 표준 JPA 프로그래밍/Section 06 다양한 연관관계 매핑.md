# Section 06. 다양한 연관관계 매핑

생성일: 2022년 8월 16일 오후 5:05

엔티티의 연관관계를 매핑할 때는 다음 3가지를 고려해야 한다.

- 다중성 : 1:1, 1:N, N:1, …
- 단방향, 양방향 : 두 엔티티 중 한쪽만 참조하는 단방향 관계인지, 서로 참조하는 양방향 관계인지
- 연관관계의 주인 : 양방향 관계인 경우 연관관계의 주인 정하기

1. 다중성
- 다대일
- 일대다
- 일대일
- 다대다

다중성을 판단하기 어려울 때는 반대방향을 생각해보면 된다. 예를 들어 앞서 살펴본 Member - Team 관계를 생각해보자. 한 명의 회원은 오직 한 개의 팀에만 속하게 된다. 반면 한 개의 팀은 여러 명의 회원을 포함할 수 있다. 따라서 Team - Member 관계는 일대다 (**@OneToMany**) 관계이고, Member - Team 관계는 그 반대인 다대일(**@ManyToOne**) 관계이다.

![회원(Member)의 입장에서 한 명의 회원은 오직 한 개의 팀만을 가질 수 있다. 반면 한 개 팀은 여러 명의 회원을 가질 수 있다. 따라서 회원 - 팀 관계는 다대일 관계이다.](Section%2006%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db717f713a724dd582acaa977dbba4d4/Untitled.png)

회원(Member)의 입장에서 한 명의 회원은 오직 한 개의 팀만을 가질 수 있다. 반면 한 개 팀은 여러 명의 회원을 가질 수 있다. 따라서 회원 - 팀 관계는 다대일 관계이다.

1. 단방향, 양방향

테이블은 외래 키 하나로 조인을 사용해서 양방향으로 쿼리가 가능하므로 사실상 방향이라는 개념이 없다. 반면에 객체는 참조용 필드를 가지고 있는 객체만 연관된 객체를 조회할 수 있다. 객체 관계에서 한 쪽만 참조하는 것을 단방향 관계라 하고, 양쪽이 서로 참조하는 것을 양방향 관계라 한다.

1. 연관관계의 주인

DB는 외래 키 하나로 두 테이블이 연관관계를 맺으므로 테이블의 연관관계를 관리하는 포인트는 외래 키 하나다. 

반면에 엔티티를 양방향으로 매핑하면 A → B, B → A 2곳에서 서로를 참조한다. 따라서 객체의 연관관계를 관리하는 포인트는 2곳이다. 

JPA는 두 객체에 대한 2개의 연관관계 중 하나를 정해서 데이터베이스 외래 키를 관리하는데 이것을 연관관계의 주인이라고 한다. 따라서

- A → B
- B → A

둘 중 하나를 정해서 외래 키를 관리해야 한다. 외래 키를 가진 테이블과 매핑한 엔티티가 외래 키를 관리하는 게 효율적이므로 보통 이곳을 연관관계의 주인으로 선택한다. 주인이 아닌 방향은 외래 키를 변경할 수 없고 읽기만 가능하다.

## 6.1 다대일

다대일(N:1) 관계에서 연관관계의 주인, 즉 외래키는 항상 다쪽에 있다. 

### 6.1.1 다대일 단방향 [N:1]

![Untitled](Section%2006%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db717f713a724dd582acaa977dbba4d4/Untitled%201.png)

```java
@Entity
public class Member {
		@Id @GeneratedValue
		@Column(name = "MEMBER_ID")
		private Long id;
		
		private String username;
		
		@ManyToOne
		@JoinColumn(name = "TEAM_ID")
		private Team team;
		// ...
}

@Entity
public class Team {
		@Id @GeneratedValue
		@Column(name = "TEAM_ID")
		private Long id;
		
		private String name;

		// ...
}
```

- `@JoinColumn(name = "TEAM_ID")` 를 사용해서 Member.team 필드를 TEAM_ID 외래 키와 매핑했다. 따라서 Member.team 필드로 MEMBER 테이블의 TEAM_ID 외래키를 관리한다.

### 6.1.2 다대일 양방향 [N:1, 1:N]

![상단의 객체 연관관계에서, 실선은 연관관계의 주인이고 점섬은 연관관계의 주인이 아니다.](Section%2006%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db717f713a724dd582acaa977dbba4d4/Untitled%202.png)

상단의 객체 연관관계에서, 실선은 연관관계의 주인이고 점섬은 연관관계의 주인이 아니다.

```java
@Entity
public class Member {
		@Id @GeneratedValue
		@Column(name = "MEMBER_ID")
		private Long id;
		
		private String username;
		
		@ManyToOne
		@JoinColumn(name = "TEAM_ID")
		private Team team;
		// ...

		public void setTeam(Team team) {
				this.team = team;

				// 무한루프에 빠지지 않도록 체크한다.
				if (!team.getMembers().contains(this)) {
						team.getMembers().add(this);
				}
}

@Entity
public class Team {
		@Id @GeneratedValue
		@Column(name = "TEAM_ID")
		private Long id;
		
		private String name;

		@OneToMany(mappedBy = "team")
		private List<Member> members = new ArrayList<Member>();
		
		public void addMember(Member member) {
				this.members.add(member);
				if (member.getTeam() != this) {// 무한 루프에 빠지지 않도록 체크
						member.setTeam(this);
				}

		// ...
}
```

<aside>
⚠️ 양방향 연관관계는 항상 서로를 참조해야 한다.
양방향 연관관계는 항상 서로 참조해야 한다. 어느 한 쪽만 참조하면 양방향 연관관계가 성립하지 않는다. 항상 서로 참조하게 하려면 연관관계 편의 메소드를 작성하는 것이 좋은데 회원의 `setTeam()` , 팀의 `addMember()` 메소드가 이런 편의 메소드들이다. 편의 메소드는 한 곳에만 작성하거나 양쪽 다 작성할 수 있는데, 양쪽에 다 작성하면 무한루프에 빠지므로 주의해야 한다. 예제 코드는 편의 메소드를 양쪽에 다 작성해서 둘 중 하나만 호출하면 된다. 또한 무한루프에 빠지지 않도록 검사하는 로직도 있다.

</aside>

## 6.2 일대다

앞서 Team 객체에서 살펴본 것처럼, Java Collection인 Collection, List, Set, Map 중에 하나를 사용해야 한다. (하나의 팀은 여러 명의 회원을 가질 수 있으므로, 여러 명을 담을 수 있는 필드가 필요하다.)

### 6.2.1 일대다 단방향 [1:N]

일대다 단방향 관계는 JPA 2.0부터 지원한다.

![Untitled](Section%2006%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db717f713a724dd582acaa977dbba4d4/Untitled%203.png)

일대다 단방향 관계는 약간 특이한데, Team 엔티티의 Team.members로 회원 테이블의 TEAM_ID 외래키를 관리한다. 보통 자신이 매핑한 테이블의 외래키를 관리하는데, 이 매핑은 반대쪽 테이블에 있는 외래 키를 관리한다. 그럴수 밖에 없는 것이, 일대다 관계에서 외래 키는 항상 다쪽 테이블에 있다. 하지만 다쪽인 Member 엔티티에는 외래 키를 매핑할 수 있는 참조 필드가 없다. 대신에 반대쪽인 Team 엔티티에만 참조 필드인 members가 있다. 

```java
@Entity
public class Team {
		@Id @GeneratedValue
		@Column(name = "TEAM_ID")
		private Long id;
		
		private String name;
		
		@OneToMany
		@JoinColumn(name = "TEAM_ID") // MEMBER 테이블의 TEAM_ID (FK)
		private List<Member> members = new ArrayList<>();

		// ...
}

@Entity
public class Member {
		@Id @GeneratedValue
		@Column(name = "MEMBER_ID")
		private Long id;
		
		private String username;
		
		// ...
}
```

일대다 단방향 관계를 매핑할 때는 반드시 `@JoinColumn` 을 명시해야 한다. 그렇지 않으면 JPA는 연결테이블을 중간에 두고 연관관계를 관리하는 JoinTable 전략을 기본으로 사용해서 매핑한다.

- 일대다 단방향 매핑의 단점

→ 매핑한 객체가 관리하는 외래 키가 다른 테이블에 있다는 점이다. 본인 테이블에 외래 키가 있으면 엔티티의 저장과 연관관계 처리를 INSERT SQL 한 번으로 끝낼 수 있지만, 다른 테이블에 외래 키가 있으면 연관관계 처리를 위한 UPDATE SQL을 추가로 실행해야 한다.

```java
// 일대다 단방향 매핑의 단점
public void testSave() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");

		Team team1 = new Team("team1");
		team1.getMembers().add(member1);
		team1.getMembers().add(member2);

		em.persist(member1);
		em.persist(member2);
		em.persist(team1);

		transaction.commit();
}
```

위 예제를 통해 실행되는 결과 SQL은 다음과 같다. 

```java
INSERT INTO MEMBER (MEMBER_ID, USERNAME) VALUES (NULL, ?)
INSERT INTO MEMBER (MEMBER_ID, USERNAME) VALUES (NULL, ?)
INSERT INTO TEAM (TEADM_ID, NAME) VALUES (NULL, ?)
UPDATE MEMBER SET TEAM_ID=? WHERE MEMBER_ID=?
UPDATE MEMBER SET TEAM_ID=? WHERE MEMBER_ID=?
```

→ Member 엔티티는 Team 엔티티를 모른다. 그리고 연관관계에 대한 정보는 Team 엔티티의 Members가 관리한다. 따라서 Member 엔티티를 저장할 때는 MEMBER 테이블의 TEAM_ID 외래 키에 아무 값도 저장되지 않는다. 대신 Team 엔티티를 저장할 때 Team.members의 참조값을 확인해서 회원 테이블에 있는 TEAM_ID 외래 키를 업데이트한다.

### 6.2.2 일대다 양방향 [1:N, N:1]

일대다 양방향 매핑은 존재하지 않는다. 대신 다대일 양방향 매핑을 사용해야 한다. 

→ 정확히 말하자면, 양방향 매핑에서 `@OneToMany` 는 연관관계의 주인이 될 수 없다. 왜냐하면 관계형 데이터베이스의 특성상 일대다, 다대일 관계는 항상 다(N)쪽에 외래 키가 있다. 따라서 `@OneToMany` , `@ManyToOne` 둘 중에 연관관계의 주인은 항상 다쪽인 `@ManyToOne` 을 사용한 곳이다. 이런 이유 때문에, `@ManyToOne` 에는 `mappedBy` 속성이 없다.

그렇다고 해서 일대다 양방향 매핑이 완전히 불가능한 것은 아니다. 일대다 단방향 매핑 반대편에 같은 외래 키를 사용하는 다대일 단방향 매핑을 읽기 전용으로 하나 추가하면 된다.

![Untitled](Section%2006%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db717f713a724dd582acaa977dbba4d4/Untitled%204.png)

```java
@Entity
public class Team {
    @Id @GeneratedValue
		@Column(name = "TEAM_ID")
		private Long id;

		private String name;

		@OneToMany
		@JoinColumn(name = "TEAM_ID")
		private List<Member> members = new ArrayList<Member>();
		
		// Getter, Setter, ...

}

@Entity
public class Member {
		@Id @GeneratedValue
		@Column(name = "MEMBER_ID")
		private Long id;
		private String username;

		@ManyToOne
		@JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
		private Team team;

		// Getter, Setter, ...
}
```

Member 엔티티에 위와 같이 다대일 단방향 매핑을 추가했다. 이 때 일대다 단방향 매핑과 같이 TEAM_ID 외래 키 컬럼을 매핑했다. 이렇게 되면 둘 다 같은 키를 관리하므로 문제가 발생할 수 있다. 따라서 반대편인 다대일 쪽은 insertable, updatable 설정을 통해 읽기만 가능하게 했다.

## 6.3 일대일 [1:1]

예를 들어 한 명의 회원이 하나의 사물함만 사용하거나, 하나의 사물함 역시 한 명의 회원에게만 할당되는 경우가 바로 일대일 관계이다.

**테이블 관계에서 다대일, 일대다는 항상 다(N)쪽이 외래 키를 갖는다. 반면 일대일 관계에선 두 테이블 중 어느 곳이나 외래 키를 가질 수 있다.**

![Untitled](Section%2006%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db717f713a724dd582acaa977dbba4d4/Untitled%205.png)

### 6.3.1 주 테이블에 외래 키

일대일 관계를 구성할 때 객체 지향 개발자들은 주 테이블에 외래 키가 있는 것을 선호한다. JPA도 주 테이블에 외래 키가 있으면 좀 더 편리하게 매핑할 수 있다. 주 테이블에 외래 키가 있는 단방향 관계를 먼저 살펴보고, 양방향 관계도 살펴보자.

### 단방향

```java
@Entity
public class Member {
		// ...
		
		@OneToOne
		@JoinColumn(name = "LOCKER_ID")
		private Locker locker;

		// ...
}

@Entity
public class Locker {
		@Id
		@GeneratedValue
		@Column(name = "LOCKER_ID")
		private Long id;

		private String name;
}
```

### 양방향

```java
@Entity
public class Member {
		// ...
		
		@OneToOne
		@JoinColumn(name = "LOCKER_ID")
		private Locker locker;

		// ...
}

@Entity
public class Locker {
		@Id
		@GeneratedValue
		@Column(name = "LOCKER_ID")
		private Long id;

		private String name;

		@OneToOne(mappedBy = "locker")
		private Member member;
		
}
```

양방향이므로 반드시 연관관계의 주인을 정해야 한다. 위 코드에선 MEMBER 테이블이 외래 키를 가지고 있으므로 Member 엔티티에 있는 Member.locker가 연관관계의 주인이다. 따라서 반대 매핑인 사물함의 Locker.member는 `mappedBy` 속성을 통해 연관관계의 주인이 아니라고 설정했다.

### 6.3.2 대상 테이블(Locker)에 외래 키

### 단방향 : 지원하지 않는다.

### 양방향

## 6.4 다대다 [N:N]

관계형 데이터베이스는 정규화된 2개의 테이블로 다대다 관계를 표현할 수 없다. 그래서 보통 다대다 관계를 일대다, 다대일 관계로 풀어내는 연결 테이블을 사용한다.

예를 들어 회원과 상품의 관계를 살펴보자. 1명의 회원은 여러 개의 상품을 주문할 수 있으며, 1개의 상품은 여러 명의 회원이 구매할 수 있다. 이런 관계를 다대다 관계라고 한다.

그런데 객체는 데이터베이스와 다르게 Collection 등을 활용해 객체 2개로 다대다 관계를 만들 수 있다. 예를 들어 회원 객체는 컬렉션을 사용해서 상품들을 참조하면 되고, 반대로 상품들도 컬렉션을 사용해서 회원들을 참조하면 된다. `@ManyToMany` 를 사용하면 이런 다대다 관계를 편리하게 매핑할 수 있다.

### 6.4.1 다대다: 단방향

```java
@Entity
public class Member {
		@Id @Column(name = "MEMBER_ID")
		private String id;
		
		private String username;

		@ManyToMany
		@JoinTable(name = "MEMBER_PRODUCT",
						joinColumn = @JoinColumn(name = "MEMBER_ID"),
						inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID"))
		private List<Product> products = new ArrayList<>();
}

@Entity
public class Product {
		@Id @Column(name = "PRODUCT_ID")
		private String id;
		
		private String name;
		// ...
}
```

여기서 중요한 점은 `@ManyToMany` 와 `@JoinTable` 을 사용해서 연결 테이블을 바로 매핑한 것이다. 따라서 중계 엔티티인 `Member_Product` 를 별도로 생성하지 않더라도 매핑을 완료할 수 있다.

- `@JoinTable.name` : 연결 테이블의 이름을 지정한다.
- `@JoinTable.joinColumns` : 현재 방향인 회원과 매핑할 조인 컬럼 정보를 지정한다. MEMBER_ID로 지정했다.
- `@JoinTable.inverseJoinColumns` : 반대 방향인 상품과 매핑할 조인 컬럼 정보를 지정한다. PRODUCT_ID로 지정했다.

이제 다대다 관계를 저장해보자.

```java
public void save() {
		Product productA = new Product();
		productA.setId("productA");
		productA.setName("상품A");
		em.persist(productA);

		Member member1 = new Member();
		member1.setId("member1");
		member1.setUsername("회원1");
		member1.getProducts().add(productA) // 연관관계 설정
		em.persist(member1);
}
```

위 코드를 실행하면 다음과 같은 SQL이 실행된다.

```sql
INSERT INTO PRODUCT ...
INSERT INTO MEMBER ...
INSERT INTO MEMBER_PRODUCT ...
```

이제 회원을 조회했을 때 회원이 구매한 상품도 함께 조회되는지 확인해보자.

```java
public void find() {
		Member member = em.find(Member.class, "member1");
		
		List<Product> products = member.getProducts();
		for (Product product : products) {
				System.out.println("product.name = " + product.getName());
		}
}
```

위 코드를 실행했을 때 실행되는 SQL문은 다음과 같다.

```sql
SELECT * FROM MEMBER_PRODUCT MP
INNER JOIN PRODUCT P ON MP.PRODCT_ID = P.PRODUCT_ID
WHERE MP.MEMBER_ID = ?
```

### 6.4.2 다대다: 양방향

Product 엔티티의 코드를 아래와 같이 수정한다.

```java
@Entity
public class Product {
		@Id
		private String id;
		
		@ManyToMany(mappedBy = "products")
		private List<Member> members;
	
}

// 회원 엔티티에 다음과 같은 편의 메소드를 추가한다.
@Entity
public class Member {
		// ...
		// 양방향 연관관계는 편의 메소드를 추가해서 관리하는 것이 편리하다.
		public void addProduct(Product product) {
				products.add(product);
				product.getMembers().add(this);
		}
		// ...
}
```

```java
public void findInserve() {
		Product product = em.find(Product.class, "productA");
		List<Member> members = product.getMembers();
		for (Member member : members) {
				System.out.println("member = " + member.getUsername());
		}
}
```

### 6.4.3 다대다: 매핑의 한계와 극복, 연결 엔티티 사용

보통 회원이 상품을 주문하면 연결 테이블에 단순히 주문한 회원 아이디와 상품 아이디만 담고 끝나지 않는다. 보통은 연결 테이블에 주문한 수량, 주문한 날짜 같은 컬럼이 더 필요하다. 즉 추가 정보가 필요하다.

→ 이 경우 더이상 @ManyToMany를 사용할 수 없다. 그 이유는 이런 추가 컬럼을 추가할 수 없기 때문이다.

→ 결국 객체 관계에서 일대다 - 다대일 관계로 풀어서 작성해야 한다.

```java
@Entity
public class Member {
		@Id
		@Column(name = "MEMBER_ID")
		private String id;
		
		// 역방향
		@OneToMany(mappedBy = "member")
		private List<MemberProduct> memberProducts;

		// ...
}

@Entity
public class Product {
		// Product에는 객체 그래프 탐색 기능이 필요하지 않다고 생각해 MemberProduct 엔티티의 참조를 하지 않게 작성했다.
		@Id
		@Column(name = "PRODUCT_ID")
		private String id;
		private String name;
		// ...
}

@Entity
@IdClass(MemberProductId.class)
public class MemberProduct {
		@Id
		@ManyToOne
		@JoinColumn(name = "MEMBER_ID")
		private Member member; // MemberProductId.member와 연결
		
		@Id
		@ManyToOne
		@JoinColumn(name = "PRODUCT_ID")
		private Product product; // MemberProductId.product와 연결
		private int orderAmount;

		// ...
}
	
```