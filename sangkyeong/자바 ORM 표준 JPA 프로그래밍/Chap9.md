# 임베디드 타입(복합 값 타입)
---
임베디드 타입은 새로운 값 타입을 직접 정의해서 사용하는 것을 말한다.

```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String name;

    // 근무기간
    @Temporal(TemporalType.DATE)
    java.util.Date startDate;
    @Temporal(TemporalTyep.DATE)
    java.util.Date endDate;

    // 집주소
    private String city;
    private String street;
    private String zipcode;
}
```
위 코드 처럼 나타낼수도 있겠지만 회원을 구분하기 위해서는
- 이름, 근무 시작일, 근무 종료일, 주소 도시, 주소 번지, 주소 우편번호로 나누는 것보다
- 이름, 근무 기간, 집 주소로<br>
명확하게 나누는게 좋다.

```java
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String name;

    // 근무기간
    @Embedded Period workPeriod;
    // 집주소
    @Embedded Address homeAddress;
}

@Embeddable
public class Period {

    @Temporal(TemporalType.DATE)
    java.util.Date startDate;
    @Temporal(TemporalTyep.DATE)
    java.util.Date endDate;

}

@Embeddable
public class Address {
    // 집주소
    private String city;
    private String street;
    private String zipcode;
}
```
임베디드 타입을 사용해서 이렇게 나눌 수 있다.

<br>

# 값 타입과 불변 객체
---
임베디드 타입 같은 값 타입을 여러 엔티티가 공유하면 위험하다.
- clone 메소드를 사용해서 복사 후, 사용하자.

다른 방법은 객체를 불변하게 만들어 수정이 불가능하도록 만드는 것이다.
- 값을 수정할 수 없기 때문에, 사용자는 값을 수정하고 싶은 경우에 무조건 새로운 객체를 반드시 만들어야 하기 때문에 공유의 위험을 없앨 수 있다.