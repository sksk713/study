# Section 04. 엔티티 매핑

생성일: 2022년 7월 25일 오후 11:24

JPA를 사용하는 데 가장 중요한 일은 엔티티와 테이블을 정확히 매핑하는 것이다. 따라서 Mapping Annotation을 숙지하고 사용해야 한다. JPA는 다양한 매핑 어노테이션을 지원하는데, 크게 4가지로 분류할 수 있다.

- @Entity, @Table
- @Id
- @Column
- @ManyToOne, @JoinColumn

## 4.1 @Entity

JPA를 사용해서 테이블과 매핑할 클래스는 이 어노테이션을 필수로 붙여야 한다. @Entity가 붙은 클래스는 JPA가 관리하는 것으로, 엔티티라 부른다. @Entity에 사용할 수 있는 속성은 다음과 같다.

- name : JPA에서 사용할 엔티티의 이름을 지정한다. 보통은 기본값인 클래스 이름을 사용한다.

@Entity 적용 시 주의 사항은 다음과 같다.

- 기본 생성자(Constructor)는 필수이다. (파라미터가 없는 public 또는 protected Constructor)
- final 클래스, enum, interface, inner 클래스에는 사용할 수 없다.
- 저장할 필드에 final을 지정해선 안된다.

만약 다음과 같이 생성자를 만들면

```java
// 직접 만든 임의의 생성자
public Member(Long id, String name, String city, String street, String zipcode) {
		this.id = id;
		this.name = name;
		this.city = city;
		..
}
```

더이상 기본 생성자를 사용할 수 없으므로, 반드시 기본 생성자를 별도로 정의해주어야 한다.

```java
// 기본 생성자
public Member() {
		// Constructor
}
```

## 4.2 @Table

엔티티와 매핑할 테이블을 지정한다. 생략하면 매핑할 엔티티 이름을 테이블 이름으로 사용한다. 사용할 수 있는 속성은 다음과 같다.

- name : 매핑할 테이블 이름. 기본값으로 엔티티 이름을 사용한다.
- catalog : catalog 기능이 있는 데이터베이스에서 catalog를 매핑한다.
- schema : schema 기능이 있는 데이터베이스에서 schema를 매핑한다.

## 4.3 다양한 매핑 사용

다음 예제를 보자.

```java
@Entity
public class Member {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

		// Getter, Setter
		// ...
}
```

- description의 경우 회원을 설명하는 필드로 길이 제한이 없다. 따라서 DB의 VARCHAR 타입 대신에 CLOB 타입으로 저장해야 한다. 이 경우 @Lob을 사용하면 CLOB, BLOB 타입을 매핑할 수 있다.
- 자바의 날짜 타입은 @Temporal을 사용해서 매핑한다.

## 4.4 데이터베이스 스키마 자동 생성

JPA는 DB 스키마를 자동으로 생성하는 기능을 지원한다. 

<aside>
💡 스키마란?
데이터베이스에서 자료의 구조, 자료의 표현방법, 자료 간의 관계를 형식 언어로 정의한 구조이다. DBMS가 주어진 설정에 따라 데이터베이스 스키마를 생성하며, 데이터베이스 사용자가 자료를 저장, 조회, 삭제, 변경할 때 DBMS는 자신이 생성한 데이터베이스 스키마를 참조하여 명령을 수행한다.
(출처: 위키백과)

</aside>

스키마 자동 생성 기능을 사용해보자. 먼저 persistence.xml에 다음 속성을 추가하자.

```xml
<property name="hibernate.hbm2ddl.auto" value="create" />
```

이 속성을 추가하면 애플리케이션 실행 시점에 DB 테이블을 자동으로 생성한다. 이 때 value에 들어갈 수 있는 속성값의 종류는 다음과 같다.

- create : 기존 테이블을 삭제하고 새로 생성한다. DROP + CREATE
- create-drop : create 속성에 추가로 애플리케이션을 종료할 때 생성한 DDL(Data Definition Language)을 제거한다. DROP + CREATE + DROP
- update : 데이터베이스 테이블과 엔티티 매핑정보를 비교해서, 변경 사항만 수정한다.
- validate : 데이터베이스 테이블과 엔티티 매핑정보를 비교해서 차이가 있으면 경고를 남기고 애플리케이션을 실행하지 않는다. 이 설정은 DDL을 수정하지 않는다.
- none : 자동 생성 기능을 사용하지 않으려면 `hibernate.hbm2ddl.auto` 속성 자체를 삭제하거나 유효하지 않은 옵션값을 주면 된다. (참고로 none은 유효하지 않은 옵션값이다.)

<aside>
⚠️ 운영 서버에서 create, create-drop, update처럼 DDL을 수정하는 옵션은 절대 사용하면 안된다. 오직 개발서버 또는 개발 단계에서만 사용해야 한다.

</aside>

<aside>
💡 JPA는 2.1부터 스키마 자동 생성 기능을 표준으로 지원한다. 하지만 하이버네이트의 hibernate.hb2ddl.auto 속성이 지원하는 update, validate 옵션을 지원하지 않는다.

아래의 속성은 JPA에서 직접 지원하는 내용이다. (Not hibernate!)
<property name=”javax.persistence.schema-generation.database.action” value=”drop-and-create” />

</aside>

## 4.5 DDL 생성 기능

“회원이름은 필수로 입력되어야 하며, 10자를 초과해선 안된다”는 새로운 제약사항이 추가되었다고 가정하자. 이를 `Member` 엔티티에 적용하려면 다음과 같이 작성하면 된다.

```java
@Column(name = "NAME", nullable = false, length = 10)
private String username;
```

만약 `nullable` 속성을 준 필드에 값을 부여하지 않으면 아래와 같은 에러가 발생한다.

```java
public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Member member = new Member();
        member.setId("memberA");
        
        em.persist(member);

        tx.commit();

        em.close();

    }
}

// main 메소드 실행 시 다음과 같은 에러가 터미널창에 출력됩니다. (임의로 줄바꿈. \로 표시)
Exception in thread "main" javax.persistence.PersistenceException: \
org.hibernate.PropertyValueException: \
not-null property references a null or transient value : \
jpabook.start.Member.username
```

이번에는 유니크 제약 조건을 만들어 주는 @Table의 `uniqueConstraints` 속성을 알아보자.

```java
@Entity
@Table(name = "MEMBER", uniqueConstraints = {@UniqueConstraint( // 추가
    name = "NAME_AGE_UNIQUE",
    columnNames = {"NAME", "AGE"})})// )})
public class Member {
		// ...
}
```

- `name` : 만들려고 하는 유니크 제약조건의 이름
- `columnNames` : 유니크 제약조건을 적용할 Column의 이름

위와 같이 작성한 후 서버를 구동하면 다음과 같은 로그를 확인할 수 있다.

```bash
Hibernate: 
    
    alter table MEMBER 
       add constraint NAME_AGE_UNIQUE unique (NAME, age)
```

앞서 살펴본 @Column의 length와 nullable 속성을 포함해서, 이런 기능들은 단지 DDL을 자동생성할 때만 생성되고 JPA의 실행 로직에는 영향을 주지 않는다. 따라서 스키마 자동 생성 기능을 사용하지 않고 직접 DDL을 만든다면 사용할 이유가 없다.

## 4.6 기본키 매핑

```java
public class Member {
    @Id
    @Column(name = "ID")
    private String id;
```

위와 같이 지금까지는 `@Id` 어노테이션을 특정 필드에 직접 할당하여 회원의 기본키를 애플리케이션(=Java)에서 직접 할당하였다. 

만약 애플리케이션에서 기본키를 직접 할당하는 대신에 DB가 생성해주는 값을 사용하려면 어떻게 해야할까? 예를 들어 Oracle의 `Sequence Object`나 MySQL의 `AUTO_INCREMENT` 같은 기능을 사용해서 생성된 값을 기본키로 사용하려면 어떻게 해야할까?

JPA가 제공하는 DB 기본키 생성 전략은 다음과 같다.

1. 직접할당

```java
public class Member {
		@Id
		private String id;
		// ...
}

public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
		
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();		

		// ...
		Member member = new Member();
		member.setId("memberA");

		em.persist(member);
		tx.commit();

		em.close();

}
```

1. 자동 생성 : 대리 키 사용 방식
    - **IDENTIY** : 기본키 생성을 데이터베이스에 위임한다.
    - **SEQUENCE** : 데이터베이스 시퀀스를 사용해서 기본 키를 할당한다.
    - **TABLE** : 키 생성 테이블을 사용한다.

→ 자동 생성 전략이 이렇게 다양한 이유는 데이터베이스 벤더마다 지원하는 방식이 다르기 때문이다. 예를 들어 Oracle DB는 시퀀스를 제공하지만, MySQL은 시퀀스를 제공하지 않는다. 대신에 MySQL은 기본 키 값을 자동으로 채워주는 AUTO_INCREMENT 기능을 제공한다. 따라서 `IDENTITY` , `SEQUENCE` 와 같은 자동 생성 전략은 사용하는 DB에 의존한다. 반면 `Table` 전략은 키 생성용 테이블을 하나 만들어두고 마치 시퀀스처럼 사용하는 방법이다. 이 전략은 테이블을 활용하므로 모든 DB에서 사용할 수 있다.

기본 키를 직접 할당하려면 위 예시와 같이 어노테이션만 할당하면 되고, 자동 생성 전략을 사용하려는 경우 

`@GeneratedValue` 를 추가하고 원하는 키 생성 전략을 선택하면 된다.

<aside>
⚠️ 키 생성 전략을 사용하려면 `persistence.xml` 에 `hibernate.id.new_generator_mapping=true` 속성을 반드시 추가해야 한다. 하이버네이트는 더 효과적이고 JPA 규격에 맞는 새로운 키 생성 전략을 개발했는데, 과거 버전과의 호환성 유지를 위해 기본값을 false로 두었다. 

<property name=”hibernate.id.new_generator_mappings” value=”true” />

</aside>

### 4.6.1 기본 키 직접 할당 전략

### 4.6.2 IDENTITY 전략

기본키 생성을 데이터베이스에 위임하는 전략이다. 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용한다. IDENTITY 전략은 AUTO_INCREMENT를 사용한 것처럼 **“데이터베이스에 값을 저장하고 나서야 기본 키 값을 구할 수 (알 수 ) 있을 때” 사용한다**.

```java
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

이제 값을 저장하고 나면, H2에서 다음과 같이 자동으로 기본키가 입력되었음을 확인할 수 있다.

![Untitled](Section%2004%20%E1%84%8B%E1%85%A6%E1%86%AB%E1%84%90%E1%85%B5%E1%84%90%E1%85%B5%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%200ebb1d4a4d894c47bb9e08a3dcfcc2d4/Untitled.png)

또는 다음과 같이 확인할 수도 있다.

```java
Board board = new Board();

em.persist(board); **// 이 부분 좀 궁금..**

System.out.println("board.id : " + board.getId());
```

<aside>
💡 IDENTITY 전략과 최적화
IDENTITY 전략은 데이터를 DB에 INSERT한 후에 기본 키 값을 조회할 수 있다. 따라서 엔티티에 식별자 값을 할당하려면 JPA는 추가로 DB를 조회해야 한다. JDBC3에 추가된 Statement.getGeneratedKeys()를 사용하면 데이터를 저장하면서 동시에 생성된 기본 키 값도 얻어올 수 있다. 하이버네이트는 이 메소드를 사용해서 데이터베이스와 한번만 통신한다.

</aside>

<aside>
⚠️ **엔티티가 영속 상태가 되려면 반드시 식별자가 필요하다. 그런데 IDENTITY 식별자 생성 전략은 엔티티를 DB에 저장해야 식별자를 구할 수 있으므로, em.persist()를 호출하는 즉시 INSERT SQL이 데이터베이스에 전달된다. 따라서 이 전략은 트랜잭션을 지원하는 쓰기 지연이 동작하지 않는다.**

</aside>

### 4.6.3 SEQUENCE 전략

다음 예제를 보자.

```java
@Entity
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ", // 매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 1
)
public class Board {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GENERATOR")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
```

- `BOARD_SEQ_GENERATOR` : 시퀀스 생성기를 등록한다.
- `BOARD_SEQ` : JPA는 시퀀스 생성기를 실제 데이터베이스의 BOARD_SEQ 시퀀스와 매핑한다.
- `@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GENERATOR")` : 방금 등록한 시퀀스 생성기를 선택한다.

이 경우

```java
Board board = new Board();

em.persist(board);

System.out.println("board.id : " + board.getId());
```

처럼 `IDENTITY` 전략으로 기본 키를 자동 할당한 경우와 동일한 코드를 작성하더라도, **내부적으로 동작하는 방식은 다르다**.

1. em.persist()를 호출할 때 `SEQUENCE` 전략의 경우 먼저 DB 시퀀스를 사용해서 식별자를 조회한다.
2. 그리고 조회한 식별자를 엔티티에 할당한 후에 엔티티를 영속성 컨텍스트에 저장한다. (Q1. 만약 Setter가 없다면? → 상관없이 된다. But 기본 생성자밖에 없고, 임의로 정의한 생성자도 없는데?)
3. 이후 트랜잭션을 커밋해서 플러시가 일어나면 엔티티를 DB에 저장한다.

### @SequenceGenerator

사용할 수 있는 속성은 다음과 같다.

- name : 식별자 생성기 이름
- sequenceName : 데이터베이스에 등록되어 있는 시퀀스 이름
- initialValue : DDL 생성 시에만 사용됨. 시퀀스 DDL을 생성할 때 처음 시작하는 수
- allocationSize : 시퀀스 한번 호출에 증가하는 수. 기본값은 50이다. 이는 성능 최적화와 연관되어 있다.
- catalog, schema

<aside>
💡 SEQUENCE 전략과 최적화
SEQUENCE 전략을 통해 기본키를 할당하게 되면 DB에 2번 통신한다.
1. 시퀀스 값 조회
2. 엔티티 저장
JPA는 시퀀스에 접근하는 횟수를 줄이기 위해 @SequenceGenerator.allocationSize를 사용한다. 간단히 설명하면 여기에 설정한 값만큼 한 번에 시퀀스 값을 증가시키고 나서 그만큼 메모리에 시퀀스 값을 할당한다.
예를 들어 allocationSize 값이 50이면 시퀀스를 한번에 50 증가시킨 다음, 1 ~ 50까지는 메모리에서 식별자를 할당한다. 그리고 51이 되면 시퀀스 값을 100으로 증가시킨 다음 51~100까지 메모리에서 식별자를 할당한다. (Q2. 메모리 어디에 저장해놓는건가..?)
이 최적화 방법은 시퀀스 값을 선점하므로 여러 JVM이 동시에 동작해도 기본 키 값이 충돌하지 않는다는 장점이 있다. 반면에 데이터베이스에 직접 접근해서 데이터를 등록할 때 시퀀스 값이 한 번에 많이 증가한다는 점을 염두해야 한다. 
참고로 앞서 설명한 `hibernate.id.new_generator_mappings` 속성을 true로 설정해야 지금까지 설명한 최적화 방법이 적용된다.

</aside>

<aside>
💡 @SequenceGenerator는 다음과 같이 @GeneratedValue 옆에 사용해도 된다.

</aside>

```java
@Entity
public class Board {
		@Id
		@GeneratedValue(...)
		@SequenceGenerator(...)
		private String id;
		// ...
}
```

### 4.6.4 TABLE 전략

키 생성 전용 테이블을 하나 만들고, 여기에 이름과 값으로 사용할 컬럼을 만들어 데이터베이스 시퀀스를 흉내내는 전략이다. 이 전략을 사용하기 위해선 테이블을 만들어야 한다.

```sql
create table MY_SEQUENCES (
		sequence_name varchar(255) not null, -- 사용할 시퀀스의 이름
		next_val bigint,  -- 시퀀스 값
		primary key ( sequence_name )
)
```

엔티티 클래스는 다음과 같이 작성한다.

```java
package jpabook.start;

import javax.persistence.*;

@Entity
@TableGenerator(
        name = "BOARD_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "BOARD_SEQ", allocationSize = 1
)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,
    generator = "BOARD_SEQ_GENERATOR")
    private Long id;

    public Long getId() {
        return id;
    }
}
```

1. @TableGenerator : 테이블 키 생성기 등록. `BOARD_SEQ_GENERATOR` 라는 테이블 키 생성기를 등록하고, `MY_SEQUENCES` 테이블을 키 생성용 테이블로 매핑했다.
2. TABLE 전략 설정 with @GeneratedValue

내부 동작 방식은 SEQUENCE와 동일하다.

![Untitled](Section%2004%20%E1%84%8B%E1%85%A6%E1%86%AB%E1%84%90%E1%85%B5%E1%84%90%E1%85%B5%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%200ebb1d4a4d894c47bb9e08a3dcfcc2d4/Untitled%201.png)

### 4.6.5 AUTO 전략

`GeneratedType.AUTO` 는 선택한 데이터베이스 방언에 따라 IDENTITY, SEQUENCE, TABLE 전략 중 하나를 자동으로 선택한다. 

### 4.6.6 기본 키 매핑 정리

Persistenct Context(영속성 컨텍스트)는 엔티티를 식별자 값으로 구분하므로, 엔티티를 영속 상태로 만드려면 반드시 식별자 값이 있어야 한다. `em.persist()` 를 호출한 직후에 발생하는 일을 식별자 할당 전략 별로 정리하면 다음과 같다.

- 직접할당 : `em.persist()` 를 호출하기 전에, 애플리케이션에서 직접 식별자 값을 할당해야 한다. 그렇지 않은 경우 예외가 발생한다.
- SEQUENCE : 데이터베이스 시퀀스에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.
- TABLE : 데이터베이스 시퀀스 생성용 테이블에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.
- IDENTITY : 데이터베이스에 엔티티를 저장해서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다. ( 따라서 쓰기 지연이 동작하지 않는다.)

## 4.7 필드와 컬럼 매핑 : 레퍼런스