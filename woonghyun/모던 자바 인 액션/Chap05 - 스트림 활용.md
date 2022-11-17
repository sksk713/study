# Chap05 - 스트림 활용

생성일: 2022년 11월 17일 오후 2:44

# 5.1 필터링

## 5.1.1 Predicate로 필터링

스트림 인터페이스는 filter 메서드를 지원한다. filter 메서드는 프레디케이트(Predicate. Boolean을 반환하는 함수형 인터페이스)를 인수로 받아서 프레디케이트와 일치하는 모든 요소를 포함하는 스트림을 반환한다. 

```java
List<Dish> vegetarianMenu = menu.stream()
				.filter(Dish::isVegetarian)
				.collect(toList());
```

## 5.1.2 고유 요소 필터링

스트림은 고유 요소로 이루어진 스트림을 반환하는 distinct 메서드도 지원한다. (이 때 고유 여부는 스트림에서 만든 객체의 hashCode, equals로 결정된다.) 예를 들어 다음 코드는 리스트의 모든 짝수를 선택하고 중복을 필터링한다.

```java
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream()
		.filter(i -> i % 2 == 0) // 이 때 나오는 결과는 2, 2, 4이다.
		.distinct()              // distinct()에 의해 2, 4가 최종 결과로 나온다.
		.forEach(System.out::println);

// 출력 결과
2
4
```

# 5.2 스트림 슬라이싱

## 5.2.1 Predicate를 이용한 스트림 슬라이싱

### TAKEWHILE 활용

```java
List<Dish> specialMenu = Arrays.asList(
		new Dish("seasonal fruit", true, 120, Dish.Type.OTHER),
		new Dish("prawns", false, 300, Dish.Type.FISH),
		new Dish("rice", true, 350, Dish.Type.OTHER),
		new Dish("chicken", false, 400, Dish.Type.MEAT),
		new Dish("french fries", true, 530, Dish.Type.OTHER));
```

이 때 어떻게 320칼로리 이하의 요리를 선택할 수 있을까?

```java
List<Dish> filteredMenu =
		specialMenu.stream()
								.filter(dish -> dish.getCalories() < 320)
								.collect(toList());
```

filter 연산을 이용하면 전체 스트림을 반복하면서 각 요소에 프레디케이트를 적용하게 된다. 따라서 리스트가 이미 정렬되어 있다는 사실을 이용해, 320칼로리보다 크거나 같은 요리가 나왔을 때 반복 작업을 중단할 수 있다. 

→ takeWhile 연산을 이용해 간단하게 처리할 수 있다.

```java
List<Dish> slicedMenu1 =
		specialMenu.stream()
								.takeWhile(dish -> dish.getCalories() < 320)
								.collect(toList());
```

### DROPWHILE 활용

takeWhile을 이용해 320칼로리보다 큰 요소가 나올때까지 작업을 수행할 수 있다. 만약 320칼로리보다 큰 요소는 어떻게 탐색할까?

```java
List<Dish> slicedMenu2 = specialMenu.stream()
								.dropWhile(dish -> dish.getCalories() < 320)
								.collect(toList());
```

`dropWhile` 은 `takeWhile` 과 정반대의 작업을 수행한다. `dropWhile` 은 프레디케이트가 처음으로 거짓이 되는 지점까지 발견된 요소를 버린다. 프레디케이트가 거짓이 되면 그 지점에서 작업을 중단하고 남은 모든 요소를 반환한다.

## 5.2.2 스트림 축소

스트림은 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환하는 `limit(n)` 메서드를 지원한다. 스트림이 정렬되어 있으면 최대 요소 n개를 반환할 수 있다. 

```java
// 예를 들어 다음처럼 300칼로리 이상의 세 요리를 선택해서 리스트를 만들 수 있다.
List<Dish> dishes = specialMenu.stream()
				.filter(dish -> dish.getCalories() > 300)
				.limit(3)
				.collect(toList());
```

정렬되지 않은 스트림(예를 들면 소스가 Set)에도 limit을 사용할 수 있다. 소스가 정렬되어 있지 않았다면 limit의 결과도 정렬되지 않은 상태로 반환된다.

## 5.2.3 요소 건너뛰기

 stream은 `skip(n)` 메서드를 지원한다. n개 이하의 요소를 포함하는 스트림에 `skip(n)` 을 호출하면 빈 스트림이 반환된다. `limit(n)` 과 `skip(n)` 은 상호 보완적인 연산을 수행한다.

예를 들어 다음 코드는 300칼로리 이상의 처음 두 요리를 건너뛴 다음, 300칼로리가 넘는 나머지 요리를 반환한다.

```java
List<Dish> dishes = menu.stream()
						.filter(d -> d.getCalories() > 300)
						.skip(2)
						.collect(toList());
```

# 5.3 매핑

## 5.3.1 스트림의 각 요소에 함수 적용하기

스트림은 함수를 인수로 받는 map 메서드를 지원한다. 인수로 제공되는 함수는 스트림의 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다. 

```java
List<String> dishNames = menu.stream()
				.map(Dish::getName)
				.collect(toList());
```

`getName` 은 문자열을 반환하므로, map 메서드의 출력 스트림은 Stream<String> 형식을 갖는다.

ex) 단어 리스트가 주어졌을 때, 각 단어가 포함하는 글자 수의 리스트를 반환한다고 가정하자. 

```java
List<String> words = Arrays.asList("Modern", "Java", "In", "Action");

List<Integer> wordLengths = words.stream() 
						.map(String::length)
						.collect(toList());
```

## 5.3.2 스트림 평면화

예를 들어 [”Hello”, “World”] 리스트가 있다면 결과로 

`["H", "e", "l", "o", "W", "r", "d"]` 가 반환되어야 한다고 가정하자. 이때는 스트림을 활용해 어떻게 코드를 작성해야할까? 다음과 같이 추측하는 독자도 있을 것이다.

```java
words.stream()
		.map(word -> word.split(""))
		.distinct()
		.collect(toList());
```

위 코드에서 map으로 전달한 람다는 각 단어의 String[]을 반환한다는 점이 문제다. 따라서 map 메소드가 반환한 스트림의 형식은 Stream<String[]>이다. 하지만 우리가 원하는 것은 Stream<String>이다. 

![Untitled](Chap05%20-%20%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7%20%E1%84%92%E1%85%AA%E1%86%AF%E1%84%8B%E1%85%AD%E1%86%BC%207a7f454454ac429fac4a0a1697f4c2c4/Untitled.png)

다행히 `flatMap` 이라는 메서드를 이용해서 이 문제를 해결할 수 있다.

### map과 Arrays.stream 활용

우선 배열 스트림 대신, 문자열 스트림이 필요하다. 

```java
String[] arrayOfWords = { "GoodBye", "World" };
Stream<String> streamOfwords = Arrays.stream(arrayOfWords);

words.stream()
		.map(word -> word.split(""))
		.map(Arrays::stream)
		.distinct()
		.collect(toList());
```

결과적으로 List<Stream<String>>가 만들어지면서 문제가 해결되지 않았다.

### flatMap 활용

```java
List<String> uniqueCharacters = 
		words.stream()
				.map(word -> word.split(""))
				.flatMap(Arrays::stream)
				.distinct()
				.collect(toList());
```

`flatMap` 은 각 배열을 스트림이 아니라 스트림의 컨텐츠로 매핑한다. 즉, map(Arrays::stream)과 달리 `flatMap` 은 하나의 평면화된 스트림을 반환한다. 

![Untitled](Chap05%20-%20%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7%20%E1%84%92%E1%85%AA%E1%86%AF%E1%84%8B%E1%85%AD%E1%86%BC%207a7f454454ac429fac4a0a1697f4c2c4/Untitled%201.png)

# 5.4 검색과 매칭

## 5.4.1 프레디케이트가 적어도 한 요소와 일치하는지 확인하기

Predicate가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인할 때, `anyMatch` 메서드를 이용한다. 

ex) menu에 채식요리가 있는지 확인하는 예제이다.

```java
if (menu.stream().anyMatch(Dish::isVegetarian)) {
		System.out.println("The menu is (someWhat) vegetarian friendly!!");
}
```

`anyMatch` 는 불리언을 반환하므로, 최종연산이다.

## 5.4.2 프레디케이트가 모든 요소와 일치하는지 검사

`allMatch` 메서드는 `anyMatch` 와 달리 스트림의 모든 요소가 주어진 Predicate와 일치하는지 검사한다.

ex) 모든 메뉴가 건강식인지 확인할 수 있다.

```java
boolean isHealthy = menu.stream()
													.allMatch(dish -> dish.getCalories() < 1000);
```

### NONEMATCH

`noneMatch` 는 위의 `allMatch` 와 반대 연산을 수행한다. 즉, `noneMatch` 는 주어진 Predicate와 일치하는 요소가 없는지를 확인한다.

위에서 보여준 예시를 아래와 같이도 작성할 수 있다.

```java
boolean isHealthy = menu.stream()
												.noneMatch(d -> d.getCalories() >= 1000);
```

`anyMatch` , `allMatch` , `noneMatch` 세 메서드는 스트림 쇼트서킷 기법, 즉 자바의 `&&` , `||` 같은 연산을 활용한다.

<aside>
ℹ️ 쇼트서킷 평가

</aside>