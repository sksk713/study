# Section 13. 웹 애플리케이션과 영속성 관리

생성일: 2022년 9월 14일 오전 12:21

## 13.1 트랜잭션 범위의 영속성 컨텍스트

순수하게 J2SE 환경에서 JPA를 사용하면 개발자가 직접 엔티티 매니저를 생성하고 트랜잭션도 관리해야 한다. 하지만 스프링이나 J2EE 컨테이너 환경에서 JPA를 사용하면 컨테이너가 제공하는 전략을 따라야 한다.

### 스프링 컨테이너의 기본 전략

스프링 컨테이너는 트랜잭션 범위의 영속성 컨텍스트 전략을 기본으로 사용한다. 이 전략은 이름 그대로 트랜잭션의 범위와 영속성 컨텍스트의 생존 범위가 같다는 뜻이다.

좀 더 풀어서 이야기하자면 이 전략은 트랜잭션을 시작할 때 영속성 컨텍스트를 생성하고 트랜잭션이 끝날 때 영속성 컨텍스트를 종료한다. 그리고 같은 트랜잭션 안에서는 항상 같은 영속성 컨텍스트에 접근한다. 

스프링 프레임워크를 사용하면 보통 비즈니스 로직을 시작하는 서비스 계층에 `@Transactional` 어노테이션을 선언해서 트랜잭션을 시작한다. 외부에서는 단순히 서비스 계층의 메소드를 호출하는 것처럼 보이지만, 이 어노테이션이 있으면 호출한 메소드를 실행하기 직전에 스프링의 트랜잭션 AOP가 먼저 동작한다.

따라서 스프링 트랜잭션 AOP는 대상 메소드를 호출하기 직전에 트랜잭션을 시작하고, 대상 메소드가 정상 종료되면 트랜잭션을 커밋하면서 종료한다. 이 때 중요한 일이 일어나는데 트랜잭션을 커밋하면 JPA는 먼저 영속성 컨텍스트를 플러시해서 변경 내용을 DB에 반영한 후, DB 트랜잭션을 커밋한다. 

```java
@Controller
class HelloController {
		@Autowired
		HelloService helloService;
		
		public void hello() {
				// 반환된 member 엔티티는 준영속 상태이다. 
				// 서비스 메소드가 끝나면서 트랜잭션과 영속성 컨텍스트가 종료되었기 때문에!
				Member member = helloService.logic();
		}
}

@Service
class HelloService {
		@PersistenceContext // 엔티티 매니저 주입
		EntityManager em;

		@Autowired
		Repository1 repository1;
		@Autowired
		Repository2 repository2;

		// 트랜잭션 시작
		@Transactional
		public void logic() {
				repository1.hello();
				
				// member는 영속상태이다.
				Member member = repository2.findMember(); 
				return member;
		}
		// 트랜잭션 종료
		// 트랜잭션을 커밋하면 영속성 컨텍스트가 종료된다. 영속성 컨텍스트가 사라졌으므로,
		// 조회한 엔티티(member)는 이제부터 준영속 상태가 된다.
}

@Repository
class Repository1 {
		@PersistenceContext
		EntityManager em;

		public void hello() {
				em.xxx(); // A. 영속성 컨텍스트에 접근
		}
}

@Repository
class Repository2 {
		@PersistenceContext
		EntityManager em;
		
		public Member findMember() {
				return em.find(Member.class, "id1"); // B. 영속성 컨텍스트 접근
		}
}

```

- `javax.persistence.PersistenceContext` 어노테이션을 사용하면 스프링 컨테이너가 엔티티 매니저를 주입해준다.

트랜잭션 범위의 영속성 컨텍스트 전략을 조금 더 구체적으로 살펴보자.

1. **트랜잭션이 같으면 같은 영속성 컨텍스트를 사용한다.**

→ 다양한 위치에서 엔티티 매니저를 주입받아 사용해도 트랜잭션이 같으면 항상 같은 영속성 컨텍스트를 사용한다. 앞선 예제의 경우 `Repository1` 과 `Repository2` 에서 엔티티 매니저를 주입받아 사용했지만 모두 같은 트랜잭션 범위에 있으므로, 동일한 영속성 컨텍스트를 사용한다. 

1. **트랜잭션이 다르면 다른 영속성 컨텍스트를 사용한다.** 

→ 여러 쓰레드에서 동시에 요청이 와서 같은 엔티티 매니저를 사용해도 트랜잭션에 따라 접근하는 영속성 컨텍스트가 다르다. 조금 더 풀어서 설명하자면, **스프링 컨테이너는 스레드마다 각각 다른 트랜잭션을 할당한다**. **따라서 같은 엔티티 매니저를 호출해도 스레드가 다르다면 각각 다른 영속성 컨텍스트를 사용하게 된다**.

## 13.2 준영속 상태와 지연 로딩

트랜잭션은 보통 서비스 계층에서 시작하므로, 서비스 계층이 끝나는 시점에 트랜잭션이 종료되면서 영속성 컨텍스트도 함께 종료된다. 따라서 조회한 엔티티가 서비스와 리포지토리 계층에서는 영속성 컨텍스트에 관리되면서 영속 상태를 유지하지만, 컨트롤러나 뷰 같은 프레젠테이션 계층에서는 준영속 상태가 된다.

```java
@Entity
public class Order {
		@Id @GeneratedValue
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 전략
		private Member member; // 주문 회원
		// ...
}
```

위와 같이 Order 엔티티가 Member 엔티티를 참조(객체 그래프 탐색)하며 지연 로딩 정책을 사용하게 되는 경우를 살펴보자. 

Service나 Repository가 아닌 컨트롤러, 뷰에서 Order 엔티티의 상태는 준영속 상태가 된다. 따라서 변경감지와 지연 로딩이 발생하지 않는다.

```java
class OrderController {
		public String view(Long orderId) {
				Order order = orderService.findOne(orderId);
				Member member = order.getMember();
				member.getName(); // 지연 로딩 예외 발생
		}
}
```

- **준영속 상태와 변경감지**

변경 감지 기능은 영속성 컨텍스트가 살아있는 서비스 계층(트랜잭션 범위)까지만 동작하고 영속성 컨텍스트가 종료된 프레젠테이션 계층에서는 동작하지 않는다. 

다만 프레젠테이션 계층에서 데이터를 수정하는 일은 일반적으로 거의 없다. 오히려 프레젠테이션 계층에서도 변경 감지가 동작하면 애플리케이션 계층이 가지는 책임이 모호해지고 무엇보다 데이터를 어디서 어떻게 변경했는지 확인하기 위해 프레젠테이션 계층까지 모두 확인해야 하므로, 애플리케이션을 유지보수하기 어렵다. 

따라서 프레젠테이션 계층에서 변경감지가 동작하지 않는 것은 특별히 문제가 되지 않는다.

- **준영속 상태와 지연로딩**

가장 골치 아픈점이다.. 예를 들어 뷰를 렌더링할 때 연관된 엔티티도 함께 사용해야 하는데 연관된 엔티티를 지연 로딩으로 설정해서 프록시 객체로 조회했다고 가정하자. 

아직 초기화하지 않은 프록시 객체를 사용하면 실제 데이터를 불러오려고 초기화를 시도한다. 하지만 준영속 상태는 영속성 컨텍스트가 없으므로 지연 로딩을 할 수 없다. 이 때 지연 로딩을 시도하면 문제가 발생한다. 

해결방법은 2가지가 존재한다. 

1. 뷰가 필요한 엔티티를 미리 로딩해두는 방법
2. OSIV를 사용해서 엔티티를 항상 영속 상태로 유지하는 방법

### 13.2.1 글로벌 페치 전략 수정

위 문제를 해결할 수 있는 가장 간단한 방법은 지연 로딩으로 설정한 참조객체를 즉시 로딩으로 변경하는 것이다.

```java
@Entity
public class Order {
		@Id @GeneratedValue
		private Long id;

		@ManyToOne(fetch = FetchType.EAGER) // 즉시 로딩 전략
		private Member member;
		// ...
}
```

위와 같이 엔티티를 설정한 상태에서, 다음 코드를 살펴보자.

```java
Order order = em.find(Order.class, orderId);
List<Order> orders = em.createQuery("select o from Order o");
```

⇒ order와 orders 모두 연관된 member 엔티티를 미리 로딩해서 가진다. 따라서 준영속 상태가 되어도 member를 사용할 수 있다. 하지만 이렇게 글로벌 페치 전략을 즉시 로딩으로 설정하는 것은 2가지 단점이 있다.

### 글로벌 페치 전략에 즉시 로딩 사용 시 단점

- 사용하지 않는 엔티티를 로딩한다.
- N + 1 문제가 발생한다.

JPA를 사용하면서 성능상 가장 조심해야 하는 것이 바로 N + 1 문제이다. 

`em.find()` 메소드로 엔티티를 조회할 때 연관된 엔티티를 로딩하는 전략이 즉시 로딩이라면 데이터베이스에 JOIN 쿼리를 사용해서 한 번에 연관된 엔티티까지 조회한다. 다음 예제는 Order.member를 즉시 로딩으로 설정했다. 

```java
Order order = em.find(Order.class, 1L);

// 이 때 실행되는 SQL은 다음과 같다.
/**
* select o.*, m.*
* from Order o
* left outer join Member m on o.MEMBER_ID = m.MEMBER_ID
* where o.id = 1
**/
```

실행된 SQL을 보면 즉시 로딩으로 설정한 member 엔티티를 JOIN 쿼리로 함께 조회한다. 여기까지 보면 상당히 좋아보이지만, 문제는 JPQL에서 발생한다. 

```java
List<Order> orders = 
		em.createQuery("select o from Order o", Order.class) 
		.getResultList(); // 연관된 모든 엔티티를 조회한다.

// 이 때 실행되는 SQL은 다음과 같다.
/**
* select * from Order
* select * from Member where id=?
* select * from Member whehre id=?
* select * from Member whehre id=?
**/

```

JPA가 JPQL을 분석해서 SQL을 생성할 때는 글로벌 페치 전략을 참고하지 않고 오직 JPQL 자체만 사용한다. 따라서 즉시 로딩이든 지연 로딩이든 구분하지 않고 JPQL 쿼리 자체에 충실하게 SQL을 만든다.

1. select o from Order o JPQL을 분석해서 select * from ORder SQL을 생성한다.
2. 데이터베이스에서 결과를 받아 order 엔티티 인스턴스들을 생성한다.
3. Order.member의 글로벌 페치 전략이 즉시로딩이므로 order를 로딩하는 즉시 연관된 member도 로딩해야 한다.
4. 연관된 member를 영속성 컨텍스트에서 찾는다.
5. 만약 영속성 컨텍스트에 없으면 SELECT * FROM MEMBER WHERE id=? SQL을 조회한 order 엔티티 수만큼 실행한다.

따라서 만약 조회한 order 엔티티가 10만개라면 member를 조회하는 SQL도 10만번 실행한다.

**이처럼 조회한 데이터수만큼 다시 SQL을 사용해서 조회하는 것을 N+1문제라고 한다.**

### 13.2.2 JPQL 페치 조인

글로벌 페치 전략을 즉시 로딩으로 설정하면 애플리케이션 전체에 영향을 주므로 너무 비효율적이다. 이번에는 JPQL을 호출하는 시점에 함께 로딩할 엔티티를 선택할 수 있는 페치 조인을 알아보자.

```java
// 패치 조인 사용 전
JPQL : select o from Order o
SQL : select * from Order

// 페치 조인 사용 후
// 페치 조인을 통해 연관된 객체도 함께 불러온다.
JPQL:
		select o
		  from Order o
		  join fetch o.member

SQL:
		select o.*, m.*
		  from Order o
		  join Member m on o.MEMBER_ID = m.MEMBER_ID

```

### JPQL 페치 조인의 단점

즉시 로딩을 했을 때 발생할 수 있는 N+1 문제를 해결할 수 있는 대안이지만, 무분별하게 사용하면 화면에 맞춘 리포지토리 메소드가 증가할 수 있다. 결국 프레젠테이션 계층이 알게 모르게 데이터 접근 계층을 침범하는 것이다. 

<aside>
💡 화면 A는 Order 엔티티에 대한 정보만 필요하다. 반면에 화면 B는 Order와 함께 연관된 Member 엔티티 둘 다 필요하다.
결국 이를 위해선 
- 화면 A를 위해 order만 조회하는 repository.findOrder() 메소드 (지연 로딩)
- 화면 B를 위해 연관된 member를 페치 조인으로 조회하는 repository.findOrderWithMember() 메소드

결국 이런 문제에 대한 대안은 페치조인으로 둘 다 조회하는 메소드만 사용하는 것이다.

</aside>

### 13.2.3 강제 초기화

영속성 컨텍스트가 살아있을 때, 프레젠테이션 계층이 필요한 엔티티를 강제로 초기화해서 반환하는 방법이다. (기본적으로 객체 참조 전략은 지연 로딩이라 가정한다.)

```java
class OrderService {
		@Transactional
		public Order findOrder(id) {
				Order order = orderRepository.findOrder(id);
				order.getMember().getName(); // 프록시 객체를 강제로 초기화한다.
				return order;
		}
}
```

order.getMember()까지만 호출하면 단순히 프록시 객체만 반환하고 아직 초기화하지 않는다. 프록시 객체는 member.getName()처럼 실제 값을 사용하는 시점에 초기화한다.

또는 하이버네이트를 사용하면 initialize() 메소드를 사용해서 프록시를 강제로 초기화한다.

하지만 이런 코드 역시 결국 프레젠테이션 계층이 서비스 계층을 침해하는 것과 동일하다.. 따라서 서비스 계층에서 프레젠테이션 계층을 위한 프록시 초기화 역할을 분리해야 한다. **FACADE 계층이 그 역할을 담당한다.**

### 13.2.4 FACADE 계층 추가

역할과 특징은 다음과 같다.

- 프레젠테이션 계층과 도메인 모델 계층(서비스 계층) 간의 논리적 의존성을 분리해준다.
- 프레젠테이션 계층에서 필요한 프록시 객체를 초기화한다.
- 서비스 계층을 호출해서 비즈니스 로직을 실행한다.
- 리포지토리를 직접 호출해서 뷰가 요구하는 엔티티를 찾는다.

### 13.2.5 준영속 상태와 지연 로딩의 문제점

지금까지 객체의 준영속 상태 & 지연 로딩으로 발생할 수 있는 문제를 해결하기 위해 

1. 글로벌 페치 전략의 수정 (즉시 로딩)
2. 이 과정에서 발생할 수 있는 n+1 문제를 해결할 수 있는 FETCH JOIN
3. 서비스 로직에 의도적으로 참조한 객체를 호출하는 방법
4. 3의 뷰 - 서비스 간 의존성을 낮추기 위한 FACADE 계층

**→ 영속성 컨텍스트를 뷰까지 살아있게 열어둔다면 결국 모두 해결되는 문제이다.**

## 13.3 OSIV

Open Session In View, 영속성 컨텍스트를 뷰까지 열어둔다는 뜻이다. 영속성 컨텍스트가 살아있으면 엔티티는 영속 상태로 유지된다.

가장 간단한 방법 : 요청 Life Cycle에 맞춰 트랜잭션의 시작과 끝을 맞추는 것. 다만 컨트롤러나 뷰 같은 프레젠테이션 계층이 엔티티를 변경할 수 있다는 점이다. (ex: 고객 이름을 홍길동 → 고길동으로 바꿔 전달해야 하는 경우.)

### 트랜잭션 방식의 OSIV 문제점 해결하기

1. 엔티티를 읽기 전용 인터페이스로 제공하기

```java
interface MemberView {
		// 읽기 전용 메소드만 존재한다. 
		public String getName();
}

@Entity
class Member implements MemberView {
		...
}

class MemberService {
		public MemberView getMember(id) {
				return memberRepository.findById(id);
		}
}
```

1. 엔티티 레핑
2. DTO만 반환

세가지 방식 모두 코드량이 상당히 증가한다는 단점이 있다.

### 13.3.2 스프링 OSIV : 비즈니스 계층 트랜잭션

1. 클라이언트의 요청이 들어오면 미리 영속성 컨텍스트를 만들어둔다. 단 이 때 트랜잭션이 시작하지는 않는다.
2. 서비스 계층에서 비즈니스 로직이 시작될 때 트랜잭션이 시작된다.
3. 서비스 계층의 로직이 종료되면 트랜잭션을 종료한다. 단 영속성 컨텍스트는 종료되지 않는다.
4. 요청이 돌아오면 영속성 컨텍스트를 종료한다. 이 때 플러시를 호출하지 않고 바로 종료한다. 

### 스프링 OSIV의 주의사항

```java
class MemberController {
		public String viewMember(Long id) {
				Member member = memberService.getMember(id);
				member.setName("XXX");
				
				memberService.biz(); // 비즈니스 로직
				return "view";
		}
}

class MemberService {
		@Transactional
		public void biz() {
				// 비즈니스 로직 실행
		}
}	
					
```

위와 같이 컨트롤러에서 엔티티를 수정한 상태에서, 트랜잭션 발생 → 비즈니스 로직 수행 → 트랜잭션 종료와 같은 순서로 동작하게 되면 변경 감지가 발생해 의도치 않게 회원 이름을 “XXX”로 수정할 수 있다.

따라서 위 문제를 해결하기 위해선 `member.setName("XXX")` 를 비즈니스 로직이 끝난 후 가장 마지막에 수행하면 된다.

## 13.4 너무 엄격한 계층

컨트롤러에서 리포지토리 로직을 호출하는 게 될까 안될까?