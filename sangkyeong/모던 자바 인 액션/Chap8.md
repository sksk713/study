# 8장

## 컬렉션 팩토리

```java
List<String> friends = Arrays.asList("Raphael", "Olivia", Thibaut");
```

asList() 팩토리 메소드를 사용해서 간단하게 생성할 수 있지만, 리스트의 크기는 고정적이고, 갱신이 가능하지만, 요소 추가나 삭제는 불가능 하다.

- UnsupportedOperationException 예외 발생

### 리스트 팩토리

List.of 팩토리 메소드 사용

List.of 메소드의 구현은 다음과 같다.

```java
static <E> List<E> of(E e1, E e2, E e3, E e4)
static <E> List<E> of(E e1, E e2, E e3, E e4, E e5)
```

인수에 따라 여러가지 오버로드 버전이 존재하고, 가변인자로도 받을 수 있지만 할당 및 제거 하는 부분에서 리소스가 필요하므로 열 개 이상의 요소를 추가할 때 사용한다.

### 집합 팩토리

Set.of 팩토리 메소드 사용

- 중복 발생 시, IllegalArgumentException 발생

### 맵 팩토리

Map.of 팩토리 메서드 사용

Key, Value를 번갈아가면서 넣어야하는 번거로움 때문에 Mpa.ofEntries 사용

```java
Map<String, Integer> ageOfFriends = Map.ofEntries(entry("Raphael", 30), 
																									entry("Olivia", 25), 
																									entry("Thibaut", 26));
```

### removeIf

```java
for (Transaction transaction : transactions) {
		if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
				transactions.remove(transaction);
		}
}
```

위 코드는 transactions 컬렉션에 for-each문에서 Iterator 객체와 remove 메소드에서 동시에 접근하고 있고, 이때 remove시 컬렉션의 리스트를 앞으로 당기는 수정 작업이 발생하므로 ConcurrentModification 예외가 발생한다.

- Iterator 객체에 remove 메소드를 사용하면 해결 가능

이 코드를 removeIf를 사용해 쉽게 간단하게 바꿔보자

```java
transactions.removeIf(transaction -> Character.isDigit(transaction.getReferenceCode().charAt(0)));
```

### replaceAll

컬렉션에서 각 요소를 새로운 요소로 바꾸는 방법은 몇 가지가 존재한다.

1. 스트림 API로 새로운 컬렉션 만들기
    1. 하지만 이 방식은 기존 컬렉션에 수정하는 방법이 아니고 새로운 컬렉션을 만들기 때문에 좋지 않다.
2. ListIterator에서 Set메소드를 사용해서 직접 요소 바꾸기
    1. ListIterator는 코드가 복잡할 뿐더러, 컬렉션에 조회 및 수정이 동시에 발생할 가능성이 있어 좋지않다.
3. replacAll

```java
referenceCodes.replaceAll(code -> Charactoer.toUpperCase(code.charAt(0)) + code.substring(1));
```

### Map에서의 forEach 메서드

BiConsumer를 인수로 받아 forEach 메서드를 사용할 수 있다.

```java
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
```

### Map에서의 정렬

Key를 기준으로 정렬

- Entry.comparingByKey

Value를 기준으로 정렬

- Entry.comparingByValue

## 계산 패턴

computeIfAbsent

- 키가 없으면 새로운 값을 계산하고 Value에 추가
- 키가 있으면 값이 바뀌지 않기 때문에 캐시로 이용 가능

computeIfPresent

- 키가 있으면 새로운 값을 계산하고 Value에 추가

compute

- 있든 없든 새로운 값을 계산하고 Value에 추가

## 삭제 패턴

remove(key, value)

## 교체 패턴

replaceAll

- BiFunction(x, y, return)의 return값으로 각 항목의 Value를 바꿈

```java
Map컬렉션.replaceAll((Key, Value) → Value 수정 메서드);
```

## ConcurrentHashMap

비동기적이고, thread-safe하지 않은 HashMap의 단점을 보완하기 위해 나옴

- HashMap은 하나의 쓰레드가 모든 버킷을 다 Lock시켜서 Multi-thread환경에서는 매우 효율이 안좋다.
- ConcurrentHashMap은 특정 버킷만 Lock을 걸어서 동시에 처리가 가능