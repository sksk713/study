## 1. 컬렉션 팩토리

자바 9에서는 작은 컬렉션 객체를 쉽게 만들 수 있는 방법을 제공합니다.

```java
List<String> friends = new ArrayList<>();
friends.add("Raphael");
friends.add("Olivia");
friends.add("Thibaut");
```

다음과 같은 코드를 **`Arrays.asLiat()`** 팩토리 메서드를 이용해서 코드를 줄일수 있습니다. 

```java
List<String> freinds = Arrays.asList("Raphael", "Olivia", "Thibaut");
```

위의 코드는 **고정 크기의 리스트를 생성**한 것으로 요소를 갱신할 순 있지만, 새 요소를 추가하거나 요소를 삭제할 수 없습니다.*(요소를 추가하려고 하면 UnsupportedOperationException 발생)*

### 1-1. 리스트 팩토리 - List.of()

`List.of()` 팩토리 메서드를 이용해서 **변경할 수 없는 리스트**를 만들 수 있습니다. *(요소 갱신, 요소 추가, 요소 삭제 불가능)*

```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
```

>💡 **오버 로딩 vs 가변 인수**
>
>List 인터페이스에는 List.of의 다양한 오버로드 버전이 있습니다.
>static <E> List<E> of(E e1, E e2)
>static <E> List<E> of(E e1, E e2, E e3)
>
>static <E> List<E> of(E… elements)
>이 메서드는 다중 요소를 받을 수 있는 가변 인수 버전입니다. 내부적으로 추가 배열을 할당해서 리스트로 감싸기 때문에 배열을 할당하고 초기화하며 나중에 가비지 컬렉션을 하는 비용도 지불해야 합니다.
>
>따라서 고정 인수를 이용하는 메서드를 오버로드하는 버전이 존재합니다.

<br>

### 1-2. 집합 팩토리 - Set.of()

`Set.of()` 팩토리 메서드를 이용해서 **변경할 수 없는 집합**을 만들 수 있습니다. 중복된 요소를 제공해 집합을 만들려고 하면 IllegalArgumentException이 발생합니다.

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
```

<br>
  
### 1-3. 맵 팩토리 - Map.of(), Map.ofEntries()

`Map.of()` , `Map.ofEntries()` 팩토리 메서드를 이용해서 두 가지 방법의 **변경할 수 없는 맵**을 만들 수 있습니다.

1. 키와 값을 번갈아 제공하는 방법
    
    ```java
    Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25)
    ```
    

2. `Map.Entry<K, V>` 객체를 인수로 제공하는 방법

Map.entry 는 Map.Entry 객체를 만드는 팩토리 메서드입니다.

```java
Map<String, Integer> ageOfFriends = Map.ofEntries(Map.entry("Raphael", 30),
		Map.entry("Olivia", 25),
		Map.entry("Thibaut", 26));
```

<br>
<br>
  
## 2. 리스트와 집합 처리

자바 8에서 List, Set 인터페이스에 새로운 결과를 만드는 스트림 동작과 달리 기존 컬렉션을 바꾸는 메서드를 추가했습니다.
  
### 2-1. removeIf 메서드

`removeIf` 메서드는 프레디케이트를 만족하는 요소를 제거합니다. List나 Set을 구현하거나 그 구현을 상속받은 모든 클래스에서 이용할 수 있습니다.

```java
for (Transaction transaction : transactions) {
		if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
				transactions.remove(transaction);
		}
}
```

다음 코드는 ConcurrentModificationException을 발생시킵니다. 내부적으로 for-each 루프는 Iterator 객체를 사용하므로 아래의 코드와 같이 해석됩니다.

```java
for (Iterator<Transcation> iterator = transactions.iterator(); iterator.hasNex();) {
		Transaction transaction = iterator.next();
		if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
				transactions.remove(transaction);
		}
}
```

별도의 Iterator 객체와 Collection 객체가 컬렉션을 관리(변경)하면서 문제가 발생합니다. 

```java
for (Iterator<Transcation> iterator = transactions.iterator(); iterator.hasNex();) {
		Transaction transaction = iterator.next();
		if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
				iterator.remove();
		}
}
```

Iterator 객체를 명시적으로 사용하고 그 객체의 remove() 메서드를 호출함으로 이 문제를 해결할 수 있습니다.

위 코드는 removeIf 메서드로 단순하고 안전하게 구현할 수 있습니다.

```java
transactions.removeIf(transaction -> Character.isDigit(transaction.getReferenceCode.charAt(0)));
```

<br>
  
### 2-2. replaceAll 메서드

List 인터페이스의 `replaceAll` 메서드를 이용해 리스트의 각 요소를 새로운 요소로 바꿀 수 있습니다. UnaryOperator 함수를 이용해 요소를 바꿉니다.

```java
referenceCodes.stream().map(code -> Character.toUpperCase(code.charAt(0)) + 
		code.substring(1))
		.collect(Collectors.toList())
		.forEach(System.out::println);
```

다음 코드는 스트림 API를 사용하여 새로운 요소로 바꾸는 코드입니다. 하지만 새 문자열 컬렉션을 만듭니다.

기존 컬렉션을 바꾸기 위해서 ListIterator 객체를 이용할 수 있습니다.

```java
for (ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext(); {
		String code = iterator.next();
		iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
}
```

replaceAll 메서드를 이용하면 간단하게 구현할 수 있습니다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
```

<br>
  
### 2-3. sort 메서드

`sort` 메서드는 List 인터페이스에서 제공하는 기능으로 리스트를 정렬합니다.

<br>
<br>
  
## 3. 맵 처리

자바 8에서 Map 인터페이스에 추가된 몇 가지 디폴트 메서드에 대해 소개하겠습니다.

### 3-1. forEach 메서드

맵에서는 Map.Entry<K, V> 의 반복자를 이용해 맵의 항목 집합을 반복할 수 있습니다.

```java
for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
		String friend = entry.getKey();
		Integer age = entry.getValue();
		System.out.println(friend + " is " + age + " years old");
}
```

Map 인터페이스의 BiConsumer를 인수로 받는 `forEach` 메서드로 코드를 간단하게 구현할 수 있습니다.

```java
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
```
  
<br>
  
### 3-2. 정렬 메서드 - Entry.comparingByValue(), Entry.comparingByKey()

`Entry.comparingByValue`, `Entry.comparingByKey`  유틸리티를 이용하면 맵의 항목을 값 또는 키를 기준으로 정렬할 수 있습니다.

```java
Map<String, String> favoriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"), entry("Cristina", "Matrix"), entry("Olivia", "James Bond");

favoriteMovies.entrySet().stream().sorted(Entry.comparingByKey())
		.forEachOrdered(System.out.println);

/*
Cristina=Matrix
Olivia=James Bond
Raphael=Star Wars
*/
```

<br>
  
### 3-3. getOrDefault 메서드

`getOrDefault` 메서드는 찾으려는 키가 존재하지 않을 때, 두 번째 인수로 받은 기본값을 반환합니다.*(NullPointerException을 방지합니다.)*

```java
Map<String, String> favoriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"), entry("Olivia", "James Bond");

System.out.println(favoriteMovies.getOfDefault("Olivia", "Matrix")); // James Bond
System.out.println(favoriteMovies.getOfDefault("Thibaut", "Matrix")); // Matrix
```

<br>
  
### 3-4. 계산 패턴 - computeIfAbsent(), computeIfPresent(), compute()

맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야 하는 상황에 사용합니다.

- `computeIfAbsent` : 제공된 키에 해당하는 값이 없으면(값이 없거나 null), 키를 이용해 새 값을 계산하고 맵에 추가합니다.
- `computeIfPresent` :제공된 키가 존재하면 새 값을 계산하고 맵에 추가합니다.
- `compute` : 제공된 키로 새 값을 계산하고 맵에 저장합니다.

```java
String friend = "Raphael";
List<String> movies = friendsToMovies.get(friend);
if (movies == null) {
		movies = new ArrayList<>();
		friendsToMovies.put(friend, movies);
}
movies.add("Star Wars");
```

위의 코드처럼 맵의 value에 요소를 추가할 때 객체가 초기화되어 있는지 확인해야 합니다. computeIfAbsent 메서드를 이용하면 간단하게 구현할 수 있습니다.

```java
friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>()).add("Star Wars");
```
  
<br>  

### 3-5. 삭제 패턴 - remove()

키가 특정한 값과 연관되어있을 때만 항목을 제거하는 오버로드 버전 메서드를 제공합니다.

```java
String key = "Raphael";
String value = "Jack Reacher 2";
if (favoriteMovies.containsKey(key) && Objects.eqauls(favoriteMovies.get(key), value)) {
		favoriteMovies.remove(key);
		return true;
} else {
		return false;
}
```

위의 코드를 remove 메서드를 사용해서 간단하게 구현할 수 있습니다.

```java
favoriteMovies.remove(key, value);
```

<br>
  
### 3-6. 교체 패턴 - replaceAll(), replace()

맵의 항목을 바꾸는 데 사용할 수 있는 메서드를 제공합니다.

- `replaceAll` : BiFunction을 적용한 결과로 각 항목의 값을 교체합니다. 이 메서드는 List의 replaceAll과 비슷한 동작을 수행합니다.
- `replace` : 키가 존재하면 맵의 값을 바꿉니다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 있습니다.

```java
Map<String, String> favoriteMovies = new HashMap<>();
favoriteMovies.put("Raphael", "Star Wars");
favoriteMovies.put("Olivia", "james bond");
favoriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
```

<br>
  
### 3-7. 합침 - putAll(), merge()

`putAll` 메서드를 사용하여 맵을 합칠 수 있지만, 값을 좀 더 유연하게 합치기 위해서 `merge` 메서드를 이용할 수 있습니다. 이 메서드는 중복된 키를 어떻게 합칠지 결정하는 BiFunction을 인수로 받습니다.

```java
Map<String, String> family = Map.ofEntries(entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));

Map<String, String> friends = Map.ofEntries(entry("Raphael", "Star Wars"), entry("Cristina", "Matrix"));
```

위의 두 맵의 Cristina는 다른 값을 가지고 있습니다. 합칠 때 충돌을 해결하기 위해 forEach와 merge 메서드를 이용할 수 있습니다.

```java
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((k, v) -> everyone.merge(k, v (movie1, movie2) -> movie1 + " & " + movie2));  // 중복된 키가 있으면 두 값을 연결
```

merge 메서드는 키와 관련된 값이 null인 경우 결과를 처리하는 기능도 할 수 있습니다. *(ex. 값을 대치하거나 항목을 제거)*

```java
Map<String, Long> moviesToCount = new HashMap<>();
String movieName = "JamesBond";
long count = moviesToCount.get(movieName);
if (count == null) {
		moviesToCount.put(movieName, 1);
} else {
		moviesToCount.put(movieName, count + 1);
}

```

위 코드를 다음처럼 구현할 수 있습니다.

```java
moviesToCount.put(movieName, 1L, (key, count) -> count + lL);
```

<br>
<br>

## 4. 개선된 ConcurrentHashMap

ConcurrentHashMap 클래스는 HashMap 보다 동시성 친화적이며 **내부 자료구조의 특정 부분만 잠궈 동시 추가, 갱신 작업을 허용**합니다. 따라서 동기화된 Hashtable 버전에 비해 읽기 쓰기 연산 성능이 월등합니다. *(HashMap은 비동기)*

### 4-1. 리듀스와 검색

ConcurrentHashMap은 세 가지 연산을 지원합니다.

- forEach : 각 (키, 값) 쌍에 주어진 액션을 실행
- reduce : 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
- search : null이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수를 적용

그리고 네 가지 연산 형태를 지원합니다.

- 키, 값으로 연산(forEach, reduce, search)
- 키로 연산(forEachKey, reducKeys, searchKeys)
- 값으로 연산(forEachValue, reduceValues, searchValues)
- Map.Entry 객체로 연산(forEachEntry, reduceEntries, searchEntries)

위의 연산은 ConcurrentHashMap의 **상태를 잠그지 않고 연산을 수행**합니다. 따라서 연산에 제공하는 함수는 계산이 진행되는 동안 바뀔 수 있는 객체, 값, 순서 등에 의존하지 않아야 합니다.

또한 위의 연산은 **병렬성 기준값(threshold)을 지정**해야 합니다. 맵의 크기가 주어진 기준값보다 작으면 순차적으로 연산을 실행합니다.

```java
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
long parallelismThreshold = 1;
Optional<Integer> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
```

<br>
  
### 4-2. 계수

ConcurrentHashMap 클래스는 맵의 매핑 개수를 반환하는 `**mappingCount**` 메서드를 제공합니다. 매핑의 개수가 int의 범위를 넘어서는 이후의 상황을 대처하기 위해 기존의 size 메서드 대신 mappingCount 메서드를 사용하는 것이 좋습니다.

<br>
  
### 4-3. 집합뷰

ConcurrentHashMap 클래스는 집합 뷰로 반환하는 **`keySet`** 메서드를 제공합니다. `newKeySet` 메서드는 ConcurrentHashMap으로 유지되는 집합을 만듭니다. 
