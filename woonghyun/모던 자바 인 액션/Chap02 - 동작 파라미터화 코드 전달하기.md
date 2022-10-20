# Chap02 - 동작 파라미터화 코드 전달하기

생성일: 2022년 10월 11일 오후 3:55

동작 파라미터화를 이용하면 빠르게 변화하는 사용자의 요구사항에 효과적으로 대응할 수 있다.

**동작 파라미터화란 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록을 의미한다. 이 코드블록은 나중에 프로그램에서 호출한다. 즉 코드 블록의 실행은 나중으로 미뤄진다.** 

예를 들어, 나중에 실행될 메서드의 인수로 코드 블록을 전달할 수 있다. 결과적으로 코드 블록에 따라 메서드의 동작이 파라미터화된다.

예를 들어 컬렉션을 처리할 때 다음과 같은 메서드를 구현한다고 가정하자.

- 리스트의 모든 요소에 대해서 ‘어떤 동작’을 수행할 수 있음
- 리스트 관련 작업을 끝낸 다음에 ‘어떤 다른 동작’을 수행할 수 있음
- 에러가 발생하면 ‘정해진 어떤 다른 동작’을 수행할 수 있음

**동작 파라미터화**로 이처럼 다양한 기능을 수행할 수 있다. 

## 2.1 변화하는 요구사항에 대응하기

### 2.1.1 첫번째 시도 : 녹색 사과 필터링

```java
enum Color { RED, GREEN }

public static List<Apple> filterGreenApples(List<Apple> inventory) {
		List<Apple> result = new ArrayList<>();

		for (Apple apple : inventory) {
				if (GREEN.equals(apple.getColor()) {
						result.add(apple);
				}
		}
		return result;
}
```

그런데 만약 농부가 변심해서 녹색 사과 말고 빨간 사과도 필터링하고 싶다면? 크게 고민하지 않는다면 메서드를 복사해서 새로운 이름을 짓고, if문의 조건만 변경하면 될 것이다. 

하지만 이런 방법은 이후 농부가 좀 더 다양한 색으로 필터링을 했을 때 반복되는 코드가 증가하며 유연하게 대처할 수 없다. 

> 거의 비슷한 코드가 반복존재한다면 그 코드를 추상화한다.
> 

### 2.1.2 두번째 시도 : 색을 파라미터화

```java
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
		List<Apple> result = new ArrayList<>();

		for (Apple apple : inventory) {
				if (apple.getColor().equals(color)) {
						result.add(apple);
				}
		}
		return result;
}
```

이제 농부도 만족할 것이다. 또한 다음처럼 구현한 메서드를 호출할 수 있다.

```java
List<Apple> greenApples = filterApplesByColor(inventory, GREEN);
List<Apple> greenApples = filterApplesByColor(inventory, RED);
```

그런데 갑자기 농부가 다시 나타나서는, “색 이외에도 가벼운 사과와 무거운 사과로 구분할 수 있다면 정말 좋겠네요. 보통 무게가 150그램 이상인 사과가 무거운 사과입니다.”라고 요구한다. 

따라서 위 메서드에 무게 정보 파라미터도 추가했다.

```java
public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
		List<Apple> result = new ArrayList<>();

		for (Apple apple : inventory) {
				if (apple.getWeight() > weight) {
						result.add(apple);
				}
		}
		return result;
}
```

위 코드도 좋은 해결책이다. 하지만 구현 코드를 자세히 보면 색 필터링 코드와 많은 부분이 중복된다. 

### 2.1.3 세번째 시도 : 가능한 모든 속성으로 필터링

다음은 만류에도 불구하고 모든 속성을 메서드 파라미터로 추가한 모습이다.

```java
public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
		List<Apple> result = new ArrayList<>();

		for (Apple apple : inventory) {
				if (flag && apple.getColor().equals(color)) || // flag에 따라 필터링할 요소를 결정한다.
						(!flag && apple.getWeight() > weight)) {
						result.add(apple);
				}
		}
		return result;
}
```

위 메서드는 다음처럼 사용할 수 있다.

```java
List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
List<Apple> greenApples = filterApples(inventory, null, 150, false);
```

→ 형편없는 코드다. 대체 true와 false가 무엇을 의미하는 건지도 모르고 요구사항이 바뀌었을 때도 유연하게 대처할 수 없다.

## 2.2 동작 파라미터화

사과의 어떤 속성(색, 무게, 출하지 등등..)에 따라 boolean값을 반환하는 방법이 우리가 선택해야 할 모든 조건의 기초이다. 이처럼 어떤 조건에 따라 참 또는 거짓을 반환하는 함수를 Predicate라고 한다. **선택 조건을 결정하는 인터페이스를 정의하자**.

```java
public interface ApplePredicate {
		boolean test (Apple apple);
}
```

이제 이 인터페이스를 구현하는 구현체를 작성하면서 다양한 선택 조건을 만들 수 있다.

```java
// 무거운 사과만 선택하기
public class AppleHeavyWeightPredicate implements ApplePredicate {
		public boolean test(Apple apple) {
				return apple.getWeight() > 150;
		}
}

// 녹색 사과만 선택하기
public class AppleGreenColorPredicate implements ApplePredicate {
		public boolean test(Apple apple) {
				return GREEN.equals(apple.getColor());
		}
}
```


![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/d98cf1cc-d2f8-4672-adf3-437212992796/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20221020%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20221020T094921Z&X-Amz-Expires=86400&X-Amz-Signature=ce34678ca99bd6c4fdc63c84c2d90585ece717ab5927b87f97907a9abd8f1aa3&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)


위 조건에 따라 filter 메서드가 다르게 동작할 것이라고 예상할 수 있다. 이를 전략 디자인 패턴(Strategy design Pattern)이라고 부른다. 

<aside>
ℹ️ 전략 디자인 패턴은 각 알고리즘(전략이라고 불리는)을 캡슐화하는 알고리즘 패밀리르 정의해둔 다음에 런타임에 알고리즘을 선택하는 기법이다.
우리 예제에서는 ApplePredicate가 알고리즘 패밀리이고 AppleHeavyWeightPredicate와 AppleGreenColorPredicate가 전략이다.

</aside>

### 2.2.1 네번째 시도 : 추상적 조건으로 필터링

```java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
		List<Apple> result = new ArrayList<>();

		for(Apple apple : inventory) {
				if(p.test(apple)) {
						result.add(apple);
				}
		}
		return result;
}
```

### 코드/동작 전달하기

예를 들어 농부가 150그램 이상인 빨간 사과를 검색해달라고 부탁한다면, 다음과 같이 작성하면 된다.

```java
public class AppleRedAndHeavyPredicate implements APplePredicate {
		@Override
		public boolean test(Apple apple) {
				return RED.equals(apple.getColor()) && apple.getWeight() > 150;
		}
}

List<Apple> redAndHeavyApples = filterApples(inventory, new AppleRedAndHeavyPredicate());
```

**→ 결국 우리가 전달한 ApplePredicate 객체에 의해 filterApples 메서드의 동작이 결정된다. 즉 filterApples 메서드의 동작을 파라미터화한 것이다.**

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/2e39b415-e153-4310-bd22-da9ca0ff9e5c/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20221020%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20221020T094943Z&X-Amz-Expires=86400&X-Amz-Signature=e53a231ff91903611511a584f89c2c5ea3879625fadf58cc1d2cabb20a2a72db&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

안타깝게도 메서드는 객체만 인수로 받으므로 test 메서드를 ApplePredicate 객체로 감싸서 전달해야 한다. (위 사진에서 굵게 표시된 코드)

<aside>
ℹ️ 이후에는 굳이 여러 개의 ApplePredicate 구현체를 만들지 않고도 동일한 동작을 하는 코드를 살펴볼 것이다.

</aside>

### 한 개의 파라미터, 다양한 동작

지금까지 살펴본 것처럼

- 컬렉션 탐색 로직(필터링 로직)과
- 각 항목에 적용할 동작(**필터링을 적용하는 로직..?**)

을 분리할 수 있다는 것이 동작 파라미터화의 강점이다.

### 퀴즈 2-1 : 유연한 PrettyPrintApple 메서드 구현하기

## 2.3 복잡한 과정 간소화

다만 위 예시에서 현재 `filterApples` 메서드로 새로운 동작 (즉 새로운 필터링의 적용)을 전달하려면 `ApplePredicate` 인터페이스를 구현하는 여러 클래스를 정의한 다음, 인스턴스화해야 한다. 이는 상당히 번거로운 작업이며 시간 낭비이다.

```java
// 1. ApplePredicate 인터페이스를 구현하는 여러 클래스 정의
public class AppleHeavyWeightPredicate implements ApplePredicate {
		// 무거운 사과 선택 로직
}

public class AppleGreenColorPredicate implements ApplePredicate {
		// 녹색 사과 선택 로직
}

public class FIlteringApples {
		public static void main(String[] args) {
				List<Apple> inventory = Arrays.asList(new Apple(80, GREEN),
																							new Apple(155, GREEN),
																							new Apple(120, RED));
				
				// 인터페이스 구현체 인스턴스화 (new)
				List<Apple> heavyApples = filterApples(inventory, new AppleHeavyWeightPredicate());
		
				List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate());

		}

		public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
				List<Apple> result = new ArrayList<>();
				
				for (Apple apple : inventory) {
						if (p.test(apple)) {
								result.add(apple);
						}
				}
				return result;
		}
				
}
```

위와 같이 

- 인터페이스를 구현하는 클래스 정의하기
- 인스턴스화

과정에서 반복되는 코드를 줄여줄 수 있는 방법은 바로 **익명 클래스(Anonymous class)**를 사용하는 것이다.

### 2.3.1 익명 클래스

자바의 지역 클래스(block class, {} 내부에서 선언된 클래스)와 비슷한 개념이다. 말 그대로 이름이 없는 클래스를 뜻한다.

**→ 익명 클래스를 이용하면 클래스 선언과 인스턴스화를 동시에 할 수 있다**.

### 2.3.2 다섯번째 시도 : 익명 클래스 사용

다음은 익명 클래스를 이용해서 ApplePredicate를 구현하는 객체를 만드는 방법으로 필터링 예제를 다시 구현한 코드이다. 

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
		public boolean test(Apple apple) {
				return RED.equals(apple.getColor());
		}
});
```

다만 익명 클래스를 사용하더라도, 여전히 반복되는 지저분한 부분이 존재한다. 아래 코드에서 굵게 표시된 부분이다.

```java
**List<Apple> redApples = filterApples(inventory, new ApplePredicate() { // 반복되어
		public boolean test(Apple apple) {                                 // 지저분한**
				return RED.equals(apple.getColor());
		**}
}); // 코드들**
```

### 퀴즈 2-2 : 익명 클래스 문제

### 2.3.3 여섯번째 시도 : 람다 표현식 사용하기

위 예제 코드를 Java 8의 람다 표현식을 이용해서 다음처럼 간단하게 꾸밀 수 있다.

```java
List<Apple> result =
		filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

### 2.3.4 일곱번째 시도 : 리스트 형식으로 추상화

```java
public interface Predicate<T> {
		boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
		List<T> result = new ArrayList<>();
		for(T e: list) {
				if(p.test(e)) {
						result.add(e);
				}
		}
		return result;
}

// 이제 객체의 종류에 상관없이 리스트에 필터 메서드를 사용할 수 있다.
// 예제
List<Apple> redApples =
		filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));

List<Integer> evenNumbers =
		filter(numbers, (Integer i) -> i & 2 == 0);
```

## 2.4 실전 예제