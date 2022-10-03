## 스프링 데이터 JPA 소개

스프링 프레임워크에서 JPA를 편리하게 사용할 수 있도록 지원하는 프로젝트

- CRUD를 처리하기 위한 공통 인터페이스 제공
- 인터페이스만 작성하면 실행 시점에 스프링 데이터 JPA가 구현 객체를 동적으로 생성해서 주입 (개발자가 직접 구현체를 개발하지 않아도 됨)

```java
public interface MemberRepository extends JpaRepository<Member, Long> {}
```

<br>
<br>

## 스프링 데이터 JPA 설정

- 필요 라이브러리 : `spring-data-jpa`
- 환경 설정 : `JavaConfig`

```java
@Configuration
@EnableJpaRepositories(basePackage = "jpabook.jpashop.repository")
public class AppConfig {}
```

<br>
<br>

## 공통 인터페이스 기능

`JpaRepository` 인터페이스는 간단한 CRUD 기능을 공통으로 제공

이 인터페이스를 상속받아 엔티티 클래스와 식별자 타입을 지정하여 사용

![](https://user-images.githubusercontent.com/63090006/189581506-dadc39f7-9600-475a-8b33-0d7ed53bfaa6.png)


- 스프링 데이터 모듈 : 스프링 데이터 프로젝트가 공통으로 사용하는 인터페이스
- 스프링 데이터 JPA 모듈 : JPA에 특화된 기능 추가 제공
  - `save(S)` : 새로운 엔티티는 저장(EntityManager.persist()), 이미 있는 엔티티는 수정(EntityManager.merge())
  - `delete(T)` : 엔티티 하나 삭제. EntityManager.remove() 호출
  - `findOne(ID)` : 엔티티 하나 조회. EntityManager.find() 호출
  - `getOne(ID)` : 엔티티를 프록시로 조회. EntityManager.getReference() 호출
  - `findAll(…)` : 모든 엔티티 조회. 정렬(Sort)이나 페이징(Pageable) 조건을 파라미터로 제공 가능

<br>
<br>

## 쿼리 메소드 기능

- @Query 어노테이션을 사용해서 리포지토리 인터페이스에 쿼리 직접 정의

### 1. 메소드 이름으로 쿼리 생성

인터페이스에 메소드만 선언하면 해당 메소드의 이름으로 적절한 JPQL 쿼리를 생성해서 실행

- 예) 이메일과 이름으로 회원 조회

  ```java
  public interface MemberRepository extends Repository<Member, Long> {
  		List<Member> findByEmailAndName(String email, String name);
  }
  ```

  ```sql
  select m from Member where m.email = ?1 and m.name = ?2
  ```

- [생성 규칙은 공식문서 참고](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)

<br>

### 2. JPA NamedQuery

메소드 이름으로 JPA Named 쿼리 호출

- 스프링 데이터 JPA는 선언한 “도메인 클래스 + .(점) + 메소드 이름”으로 Named 쿼리 찾아서 실행

```java
// Named 쿼리 정의
@Entity
@NamedQuery (
		name="Member.findByUsername",
		query="select m from Member m where m.username = :username")
public class Member { 
		...
}
```

```java
// JPA 사용
public class MemberRepository {
		
		public List<Member> findByUsername(String username) {
				...
				List<Member> resultList = em.createNamedQuery("Member.findByUasename", Member.class)
																		.setParameter("username", "회원1")
																		.getResultList();
		}
}

// 스프링 데이터 JPA 사용
public interface MemberRepository extends JpaRepository<Member, Long> {
		List<Member> findByUsername(@Paran("username") String username);
}
```

<br>

### 3. @Query, 리포지토리 메소드에 쿼리 정의

```java
public interfae MemberRepository extends JpaRepository<Member, Long> {
		@Query("select m from Member m where m.username = ?1")
		Member findByUsername(String username);
}
```

- 네이티브 SQL을 사용하려면 `@Query 어노테이션에 nativeQuery = true` 설정
  - 위치 기반 파라미터 바인딩을 사용하면 JPQL은 1부터 시작하지만 네이티브 SQL은 0부터 시작

<br>

### 4. 파라미터 바인딩

- 기본값은 위치 기반 파라미터 바인딩
- 이름 기반 파라미터 바인딩을 사용하려면 `@Param` 어노테이션 사용

<br>

### 5. 벌크성 수정 쿼리

- `@Modifying` 어노테이션 사용
- 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트를 초기화하고 싶으면 clearAutomatically 옵션을 true 로 설정 (`@Modifiying(clearAutomatically = ture)`)

<br>

### 6. 반환 타입

- 결과가 한 건 이상이면 컬렉션 : `List<Member> findByName(String name);`
  - 조회 결과 없으면 빈 컬렉션 반환
- 결과가 단건이면 반환 타입 지정 : `Member findByEmail(String email);`
  - 조회 결과가 없으면 null 반환 (JPA 사용시에는 예외 발생)
  - 2건 이상 조회되면 예외 발생

<br>

### 7. 페이징과 정렬

스프링 데이터 JPA는 페이징과 정렬 기능을 사용할 수 있도록 파라미터 제공 

- `Sort` : 정렬 기능

- `Pageable` : 페이징 기능(내부에 Sort 포함)

  - 반환 타입으로 List 나 Page(전체 데이터 건수를 조회하는 count 쿼리 추가 호출) 사용 가능

- 예제

  - 검색 조건 : 이름이 김으로 시작하는 회원
  - 정렬 조건 : 이름으로 내림차순
  - 페이징 조건 : 첫 번째 페이지, 페이지당 보여줄 데이터는 10건

  ```java
  // 페이징 조건과 정렬 조건 설정
  // PageRequest 는 Pageable의 구현체)
  PageRequet pageRequest = new PageRequest(0, 10, new Sort(Direction.DESC, "name"));
  
  Page<Member> result = memberRepository.findByNameStartingWith("김", pageRequest);
  ```

<br>

### 8. 힌트

`@QueryHints` 어노테이션 : JPA 구현체에 제공하는 힌트

```java
@QueryHints(value = { QueryHint(name = "org.hibernate.readOnly", value = "true")}, forCounting = true)
Page<Member> findByName(String name, Pageable pageable);
```

- forCounting 속성 : 반환 타입으로 Page 인터페이스를 적용하면 추가로 호출하는 페이징을 위한 count 쿼리에도 쿼리 힌트를 적용할지 설정

<br>

### 9. Lock

쿼리 시 락 적용

```java
@Lock(LockModeType.PESSOMOSTIC_WRITE)
List<Member> findByName(String name);
```

<br>
<br>

## 명세 (Specification)

- 술어(Predicate) : 참이나 거짓으로 평가됨. AND, OR 등의 연산자로 조합 가능
- 명세 기능을 사용하려면 리포지토리에서 `JpaSpecificationExecutor` 인터페이스 상속

```java
public interface OrderRepositroy extends JpaRepository<Order Long>, JpaSpecificationExecutor<Order> {}

public interface JpaSpecificationExecutor<T> {
		T findOne (Specification<T> spec);
		...
}
```

- 명세 사용 예제

  ```java
  import static org.springframework.data.jpa.domain.Specifications.*; // where(), and()
  import static jpabook.jpashop.domain.spec.OrderSpec.*;
  
  public List<Order> findOrders(String name) {
  		List<Order> result = orderRepository.findAll(
  				where(memberName(name)).and(isOrderStatus())
  		);
  		return result;
  }
  ```

  - Specificatons는 명세들을 조립할 수 있도록 도와주는 클래스. where(), and(), or(), not() 메소드 제공

  - OrderSpec 명세 정의 코드는 책 참고

    ```java
    public class OrderSpec {
    		public static Specification<Order> memberName(final String memberName) {...}  // Specification 인터페이스 구현
    		
    		public static Specification<Order> isOrderStatus() {..}  // Specification 인터페이스 구현
    ```

<br>
<br>

## 사용자 정의 리포지토리 구현

스프링 데이터 JPA로 리포지토리를 개발하면 인터페이스만 정의하고 구현체는 만들지 않는다.

메소드를 직접 구현해야 할 때는 사용자 정의 인터페이스를 구현한 클래스를 작성하는 방법을 스프링 데이터 JPA가 제공한다.

- 클래스 이름 작성 규격 : 리포지토리 인터페이스 이름 + Impl

```java
// 사용자 정의 인터페이스
public interface MemberRepositoryCustom {
		public List<Member> findMemberCustom();
}

// 사용자 정의 구현 클래스
public class MemberRepositoryImpl implements MemberRepositoryCustom {
		
		@Override
		public List<Member> findMemberCustom() {
				.. // 사용자 정의 구현
		}
}

// 리포지토리 인터페이스에 사용자 정의 인터페이스 상속
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom { }
```

<br>
<br>

## Web 확장

스프링 데이터 프로젝트는 스프링 MVC에서 사용할 수 있는 기능 제공

### 1. 설정

- xml 방식

  ```xml
  // 스프링 빈 등록
  <bean class="org.springframework.data.web.config.SpringDataWebConfiguration" />
  ```

- JavaConfig 방식

  ```java
  // @EnableSpringDataWebSupprot 어노테이션 사용
  @Configuration
  @EnableWebMvc
  @EnableSpringDataWebSupprot
  public class WebAppConfig {
  		...
  }
  ```

<br>

### 2. 기능

- 도메인 클래스 컨버터 기능

  HTTP 파라미터로 넘어온 식별자로 엔티티 객체를 찾아서 바인딩

  도메인 클래스 컨버터는 해당 엔티티와 관련된 리포지토리를 사용해서 엔티티를 찾음

  ```java
  @Controller
  public class MemberController {
  		
  		@RequestMapping("member/memberUpdateForm")
  		public String memberUpdateForm(@RequestParam("id") Member member, Model model) {
  				model.addAttribute("member", member);
  				return "member/memberSaveForm";
  		}
  }
  ```

- 페이징과 정렬

  `HandlerMethodArgumentResolver 제공`

  - 페이징 기능 : `PageableHandlerMethodArgumentResolver`
  - 정렬 기능 : `SortHandlerMethodArgumentResolver`

  ```java
  @RequestMapping(value = "/members", method = RequestMethod.GET)
  public String list(Pageable pageable, Model model) {
  		Page<Member> page = memberSerivce.findMembers(pageable);
  		model.addAttribute("members", page.getContent());
  		return "members/memberList";
  }
  ```

  - `Pageable`의 파라미터

    - page : 현재 페이지, 0부터 시작
    - size : 한 페이지에 노출할 데이터 건수
    - sort : 정렬 조건 정의

  - 페이징 정보가 둘 이상이면 접두사 사용해서 구분

    - `@Qualifier` 어노테이션 사용, “(접두사명)_”으로 구분

    ```java
    public String list(
    		@Qualifier("member") Pageable memberPageable,
    		@Qualifier("order") Pageable orderPageable, ...
    ```

  - Pageable의 기본값은 page = 0,  size = 20. `@PageableDefault` 어노테이션 사용하여 기본값 변경

<br>
<br>

## 스프링 데이터 JPA와 QueryDSL 통합
스프링 데이터 JPA가 지원하는 QueryDSL 사용 방법

### 1. QueryDslPredicateExecutor 사용

리포지토리에서 QueryDslPredicateExecutor 상속

- 스프링 데이터 JPA에서 편리하게 QueryDSL을 사용할 수 있지만 join, fetch 등의 기능을 사용할 수 없다.
- QueryDSL이 제공하는 다양한 기능을 사용하려면 JPAQuery를 직접 사용하거나 스프링 데이터 JPA가 제공하는 QueryDslRepositorySupport 사용

<br>

### 2. QueryDslRepositorySupport 사용

QueryDSL의 모든 기능을 사용하려면 JPAQuery 객체를 직접 생성해서 사용하면된다. 이때 스프링 데이터 JPA가 사용하는 `QueryDslRepositorySupport` 를 상속받아 사용하면 편리하게 QueryDSL 사용할 수 있다.