# Section 10. 객체 지향 쿼리 언어

생성일: 2022년 8월 30일 오후 10:07

# 10.1 객체지향 쿼리 소개

EntityManager.find() 메소드를 사용하면 식별자로 엔티티 하나를 조회할 수 있다. 이렇게 조회한 엔티티에 객체 그래프 탐색을 사용하면 연관된 엔티티를 찾을 수 있다. 

하지만 이 기능만으로 애플리케이션을 개발하기는 어렵다. 만약 나이가 30살 이상인 회원을 모두 검색하고 싶다면 더 복잡한 검색 방법이 필요하다.

그렇다고 해서, 모든 회원을 영속성 컨텍스트에 저장하고 애플리케이션에서 반복문을 이용해 30살 이상인 회원을 찾는 것은 현실성이 없다. JPQL은 이런 문제를 해결하기 위해 등장했다.

JPQL은 다음과 같은 특징이 있다. 

1. 테이블이 아닌, 객체를 대상으로 검색하는 객체지향 쿼리이다.
2. SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.

즉, SQL이 데이터베이스 테이블을 대상으로 하는 데이터 중심의 쿼리라면, JPQL은 엔티티 객체를 대상으로 하는 객체지향 쿼리이다.

## 10.1.1 JPQL (Java Persistence Query Language) 소개

JPQL은 엔티티 객체를 조회하는 객체지향 쿼리이다. JPQL은 SQL을 추상화하기 때문에, 특정 데이터베이스에 의존하지 않는다. 또한 DB 방언만 적절하게 적용한다면 JPQL을 수정하지 않아도 자연스럽게 데이터베이스를 변경할 수 있다.

또한 JPQL은 SQL보다 간결하다. 직접 사용 예시를 살펴보자.

```java
@Entity(name = "Member")
public class Member {
		@Column(name = "name")
		private String username;
		// ...
}
```

```java
// 쿼리 생성
String jpql = "select m from Member as m where m.username = 'kim'";
List<Member> resultList = em.createQuery(jpql, Member.class).getResultList();
```

→ `em.createQuery()` 메소드에 실행할 JPQL과 반환할 엔티티의 클래스 타입인 `Member.class` 를 넘겨주고 `getResultList()` 메소드를 실행하면 JPA는 JPQL을 SQL로 변환해서 DB를 조회한다.

## 10.1.2 Criteria 쿼리 소개

Criteria는 JPQL을 생성하는 Builder 클래스이다.  Criteria의 장점은 문자가 아닌 query.select(m).where(…)처럼 프로그래밍 코드로 JPQL을 작성할 수 있다는 점이다.

예를 들어 JPQL에서 `select m from Membeeee m` 처럼 오타가 있다고 가정해보자. 그래도 컴파일은 성공하고 애플리케이션을 서버에 배포할 수 있다. 문제는 해당 쿼리가 실행되는 런타임 시점에 오류가 발생한다는 점이다. 이것이 문자기반 쿼리의 단점이다. 반면에 Criteria는 문자가 아닌 코드로 JPQL을 작성한다. 따라서 컴파일 시점에 오류를 발견할 수 있다.

따라서 문자로 작성한 JPQL보다, 코드로 작성한 Criteria의 장점은 다음과 같다.

1. 컴파일 시점에 오류를 발견할 수 있다. (SQLException이 아닌 애플리케이션 단에서 Exception이 발생한다.)
2. IDE를 사용하면 코드 자동완성을 지원한다.
3. 동적 쿼리를 작성하기 편하다. 

이제 간단한 Criteria 사용 코드를 살펴보자.

```java
// Crieteria 사용 준비
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Member> query = cb.createQuery(Member.class);

// 루트 클래스 (조회를 시작할 클래스)
Root<Member> m = query.from(Member.class));

// 쿼리 생성
CriteriaQuery<Member> cq = 
				query.select(m).where(cb.equal(m.get("username"), "kim")); // 만약 username도 문자가 아닌 코드로 작성하고 싶다면? 메타 모델 사용
List<Member> resultList = em.createQuery(cq).getResultList();
```

메타 모델 API에 대해 알아보자. 자바가 제공하는 Annotation Processor 기능을 사용하면 어노테이션을 분석해서 클래스를 생성할 수 있다. JPA는 이 기능을 사용해서 Member 엔티티 클래스로부터 `Member_` 라는 Criteria 전용 클래스를 생성하는데, 이것을 메타 모델이라 한다. 

## 10.1.3 QueryDSL 소개

Criteria와 마찬가지로 QueryDSL 또한 JPQL의 빌더 역할을 한다. 장점은 단순하면서 코드 기반이라는 점이다. 또한 작성한 코드도 JPQL과 비슷해서 한눈에 들어온다. 

```java
// 준비
JPAQuery query = new JPAQuery(em);
QMember member = QMember.member;

// 쿼리, 결과조회
List<Member> members = query.from(member).where(member.username.eq("kim"))
		.list(member);
```

QueryDSL 또한 Criteria와 마찬가지로 엔티티를 기반으로 전용 클래스를 만들어야 한다. QMember가 바로 이 전용 클래스에 해당한다.

## 10.1.4 네이티브 SQL 소개

JPA는 SQL을 직접 사용할 수 있는 기능을 지원하는데, 이를 네이티브 SQL이라 한다.

JPQL을 사용해도 가끔은 특정 DB에 의존하는 기능을 사용해야할 때가 있다. 예를 들어 Oracle DB에서만 사용하는 `CONNECT BY` 기능이나 특정 DB에서만 동작하는 SQL 힌트는 같은 것이다. 하지만 이런 기능들은 전혀 표준화되어 있지 않아 JPQL에서 사용할 수 없다. 또한 SQL은 지원하지만 JPQL은 지원하지 않는 기능도 있다. 이때는 네이티브 SQL을 사용하면 된다.

단점은 특정 DB에 의존하는 SQL을 작성해야 한다는 것이다. 따라서 DB를 변경하면 이에 따라 네이티브 SQL도 수정해야 한다.

```java
String sql = "SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = 'kim'";
List<Member> resultList = 
				em.createNativeQuery(sql, Member.class).getResultList();
```

# 10.2 JPQL

위에서 언급한 모든 방법의 시작은 결국 JPQL이다. 특징을 다시 한번 정리해보자. 

- JPQL은 객체지향 쿼리 언어이다. 따라서 테이블을 대상으로 쿼리하는 것이 아니라 엔티티 객체를 대상으로 쿼리한다.
- JPQL은 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다.
- JPQL도 결국은 SQL로 변환된다.

### 도메인 모델 : 354p 참고

## 10.2.1 기본 문법과 쿼리 API

JQPL도 SQL과 비슷하게 SELECT, UPDATE, DELETE 문을 사용할 수 있다. 참고로 엔티티를 저장할 때는 `entityManager.persist()` 를 사용하면 되므로, INSERT문은 없다.

```java
// JPQL 문법
select_문 :: = 
		select_절
		from_절
		[where_절]
		[groupby_절]
		[having_절]
		[orderby_절]

update_문 :: = update_절 [where_절]
delete_문 :: = delete_절 [where_절]
```

JPQL에서 UPDATE, DELETE는 벌크 연산이라고 하는데, 이는 이후에 다시 설명하겠다.

### SELECT 문

select문은 다음과 같이 사용한다.

```java
SELECT m FROM Member AS m where m.username = 'Hello'
```

- **대소문자 구분**
    
    엔티티와 속성은 대소문자를 구분한다. 예를 들어 Member, username은 대소문자를 구분한다. 반면에 SELECT, FROM, AS와 같은 JPQL 키워드는 대소문자를 구분하지 않는다.
    
- **엔티티 이름**
    
    JPQL에서 사용한 Member는 클래스 명이 아니라 엔티티 명이다. 엔티티 명은 `@Entity(name="XXX")` 로 지정할 수 있다. 엔티티 명을 지정하지 않으면 클래스명을 기본값으로 사용한다.
    
- **별칭은 필수**
    
    Member AS m을 보면 Member에 m이라는 별칭을 주었다. JPQL은 별칭을 필수로 사용해야 한다. 따라서 다음 코드처럼 별칭 없이 작성하면 잘못된 문법이라는 오류가 발생한다.
    
    ```java
    SELECT username FROM Member m // 잘못된 문법, username을 m.username으로 고쳐야 한다.
    ```
    
    AS는 생략할 수 있다. 따라서 `Member m` 처럼 사용해도 된다.
    
    <aside>
    💡 별칭은 alias를 뜻한다.
    
    </aside>
    

### TypeQuery, Query