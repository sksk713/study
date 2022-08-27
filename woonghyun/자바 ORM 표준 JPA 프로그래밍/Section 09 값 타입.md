# Section 09. 값 타입

생성일: 2022년 8월 27일 오전 10:01

JPA의 값 타입은 다음과 같이 나뉜다.

1. 엔티티 타입 : @Entity로 정의하는 객체
2. 값 타입 : int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입 또는 객체

또한 값 타입은 다음과 같은 분류로 나뉜다.

- 기본값 타입
- 임베디드 타입
- 켈렉션 값 타입

### 9.2 임베디드 타입

예를 들어 `Team team` , `Period period` 등 엔티티에서 다른 엔티티를 참조할 때 정의하는 유형이 있다. 이 때 이런 필드를 임베디드 타입이라고 얘기한다. → “응집도"를 위해 사용한다.

```java
@Entity
public class Member {
		@Embedded Period period;
		@Embedded Address address;
} 

@Embeddable
public class Period { ... }
@Embeddable
public class Address { ... }
```

### 9.2.1 임베디트 타입과 테이블 매핑

→ 임베디드 타입 또한 엔티티의 관점에서 살펴보면 결국 하나의 필드, 즉 값 타입에 불과하다. 따라서 테이블에선 일반적인 값 타입이 매핑되는 것과 동일하게 매핑된다.

### 9.2.2 임베디드 타입과 연관관계

임베디드 타입은 값 타입을 포함하거나, 엔티티를 참조할 수 있다. 

```java
@Embeddable
public class Address {
		String street;
		String city;
		String state;
		@Embedded Zipcode zipcode; // 임베디드 타입을 포함
}

@Embeddable
public class Zipcode { ... }

@Embeddable
public class PhoneNumber {
		@ManyToOne PhoneServiceProvider provider; // 임베디드 타입의 엔티티 참조
		// ...
}	
```

### 9.2.3 @AttributeOverride : 속성 재정의

```java
@Entity
public class Member {
		@Id @GeneratedValue
		private Long id;
		private String name;
	
		@Embedded Address homeAddress;
		@Embedded Address companyAddress;
}

```

위와 같이 주소를 하나 추가했다. 문제는 테이블에서 매핑되는 컬럼명이 중복되는 것이다. 따라서 아래와 같이 `@AttributeOverride` 를 통해 매핑정보를 재정의해야 한다.

```java
@Embedded
@AttributeOverrides({
		@AttributeOverride(name = "city", column=@Column(name = "COMPANY_CITY")),
		@AttributeOverride(name = "street", column=@Column(name = "COMPANY_STREET")),
		@AttributeOverride(name = "zipcode", column=@Column(name = "COMPANY_ZIPCODE"))
})
Address companyAddress;
```

<aside>
⚠️ @AttributeOverride는 반드시 엔티티에 설정해야 한다. 임베디드 타입이 임베디드 타입을 가지고 있어도 엔티티에 설정해야 한다.

</aside>

## 9.3 값 타입과 불변 객체

임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면, 회원 2의 주소를 변경하려고 했는데 회원 1의 주소도 함께 변경되는 일이 발생할 수 있다. 따라서 동일한 객체를 사용하는 게 아닌, DeepCopy가 이뤄져야 한다. 

### 9.3.3 불변 객체

위와 같이 임베디드 타입과 같은 값 타입의 경우 부작용이 발생할 수 있다. 그 이유는 임베디드 타입이 불변하지 않기 때문이다. 따라서 값 타입을 불변 객체(IMMUTABLE OBJECT)로 설계한다면 이러한 부작용을 피할 수 있다.

가장 간단한 방법 중 하나는 생성자로만 값을 설정하고, 수정자를 만들지 않으면 된다.

```java
@Embeddable
public class Address {
		private String city;
		protected Address() {};

		public Address(String city) { this.city = city; }

		// 접근자는 노출한다.
		public String getCity() {
				return city;
		}
		
		// 수정자는 만들지 않는다.
}
```

## 9.5 값 타입 컬렉션

`ElementCollection` : 하나 이상의 값을 컬렉션으로 만들고, 이를 DB에 저장하려고 할 때 사용한다. 

```java
@ElementCollection
@CollectionTable(name = "FAVORITE_FOODS",
		joinColumns = @JoinColumn(name = "MEMBER_ID"))
@Column(name = "FOOD_NAME")
private Set<String> favoriteFoods = new HashSet<String>();

```

### 9.5.2 값 타입 컬렉션의 제약사항