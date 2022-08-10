---
layout: post
title: "02. 엔티티 매핑"
author: "SangKyenog Lee"
tags: "[Book]자바-ORM-표준-JPA-프로그래밍"
---

# @Entity
---
`@Entity`를 붙이면 해당 클래스는 JPA가 관리하는 엔티티로 등록이 된다.
- 기본 생성자는 필수다.(public, protected)
    - private를 하게 되면 프록시 객체를 생성할 수가 없기 때문에 안된다.
- 필드에 `final`을 붙이면 안된다. 
    - Why? JPA에 의해 엔티티가 프록시 객체로 확장될 때, Refletion API를 사용한다. Reflection으로 객체를 생성하면 필드들을 초기화하기 위해 `Setter`를 사용하는데 final이 붙은 필드들을 초기화할 수 없다.

<br>

# @Table
---
`@Table`은 엔티티와 매핑할 테이블을 지정하는 애노테이션.
- 옵션에는 `name`, `catalog`, `schema`, `unique 제약 조건`이 있다.

<br>

# 스키마 자동 생성
---
`create`
- drop + create 테이블을 만들 때, 기존 테이블 삭제
- 테이블이 존재하지 않을 때, 생성 용도로 사용

create-drop
- 애플리케이션 종료 시, 테이블 삭제

update
- 테이블과 엔티티를 비교하며, 변경 정보만 수정

`validate`
- 테이블과 엔티티를 비교하며, 차이가 있으면 경고를 남기고 실행 안함

`none`
- 자동 생성 기능 사용 x
- create로 테이블 생성 후, 운영 서버 none으로 변경

<br>

# 기본 키 매핑 - IDENTITY 전략
---
기본 키 생성을 데이터베이스에 위임하는 전략으로, 엔티티가 영속 상태가 되려면 식별자가 필요하다. `But` IDENTITY 전략은 엔티티를 데이터베이스에 저장해야 식별자를 구할 수 있고, 그 후에 식별자를 엔티티에 할당하고 영속성 컨텍스트에 저장된다. 따라서, 트랜잭션을 지원하는 쓰기 지연이 동작하지 않는다.

<br>

# 기본 키 매핑 - SEQUENCE 전략
---
IDENTITY와는 다르게 SEQUENCE는 em.persist()를 호출할 때, 먼저 식별자를 조회하고 엔티티에 할당한 후에 영속성 컨텍스트에 저장을 한다. 따라서 쓰기 지연이 가능하다

<br>

# 기본 키 매핑 - TABLE 전략
---
TABLE전략은 SEQUENCE 전략과 흡사한데, 시퀀스 값을 저장하는 테이블을 만들어서 해당 테이블을 이용하는 것을 말한다. `So` 데이터베이스에 종속적이지 않아서 모든 DB에서 사용 가능하다.

<br>