# ch3 람다 표현식

## 3.1 람다란 무엇인가?

**람다 표현식**

> 메서드로 전달할 수 있는 익명 함수를 단순화 한 것
> 
- 익명: 보통의 메서드와 달리 이름이 없음
- 함수: 메서드처럼 특정 클래스에 종속되지 않음. but 메서드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외 리스트 포함
- 전달: 메서드 인수로 전달 또는 변수로 저장 가능
- 간결성: 익명 클래스처럼 코드를 구현할 필요가 없음

예시) custom comparator 객체

기존 코드

```java
Comparator<Apple> byWeight = new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
    }
}
```

람다 적용 후

```java
Comparator<Apple> byWeight =
        (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```

**람다 표현식 구성: 파라미터 + 화살표 + 바디**

`(Apple a1, Apple a2) → a1.getWeight().compareTo(a2.getWeight());`

- 파라미터 리스트: comparator의 compare 메서드 파라미터(사과 2개)
- 화살표: 람다의 파라미터 리스트와 바디 구분
- 람다 바디: 반환값에 해당하는 표현식. 두사과의 무게 비교

| 사용 사례 | 람다 예제 |
| --- | --- |
| 불리언 표현식 | (List<String> list) → list.isEmpty() |
| 객체 생성 | () → new Apple(10) |
| 객체에서 소비 | (Apple a) →{System.out.println(a.getWeight();} |
| 객체에서 선택/추출 | (String s) → s.length() |
| 두 값을 조합 | (int a, int b) → a * b |
| 두 객체 비교 | (Apple a1, Apple a2) → a1.getWeight().compareTo(a2.getWeight()) |

예시)

```java
(String s) -> s.length() //String 형식의 파라미터를 가지며 int를 반환

(Apple a) -> a.getLength() > 150 //Apple 형식의 파라미터를 가지며, boolean을 반환

(int x, int y) -> {  //int 형식의 파라미터 2개를 가지며 리턴값이 없음
  System.out.println("Result :");
  System.out.println(x + y);
}

() -> 42 //파라미터가 없으며, int 42를 반환

(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
//Apple 형식의 파라미터 2개를 가지며, int(두 사과의 무게 비교 결과)를 반환
```

- 람다 표현식에는 return이 함축되어 있으므로 return 문을 명시적을 사용 X
- 여러행의 문장(코드) 포함 가능

## 3.2 어디에, 어떻게 람다를 사용할까?

<aside>
💡 함수형 인터페이스라는 문맥에서 람다 표현식을 사용할 수 있다.

</aside>

### 3.2.1 함수형 인터페이스

> `**Predicate<T>`** 오직 하나의 추상 메서드만 지정하는 인터페이스
> 

ex) Comparator, Runnable 등

```java
public interface Predicate<T> {
    boolean test (T t);
}
public interface Comparator<T> {
    int compare (T o1, T o2);
}
public interface Runnable {
    void run();
}
```

※ default 메서드(인터페이스의 메서드를 구현하지 않은 클래스를 고려해서 기본 구현을 제공하는 바디를 포함하는 메서드)가 있더라고 추상 메서드가 오직 하나면 함수형 인터페이스

⇒ ****람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달 가능

**= 전체 표현식을 함수형 인터페이스의 인스턴스** 취급 가능(함수형 인터페이스를 **구현한 클래스**의 인스턴스)

```java
// 람다
Runable r1 = () -> System.out.println("hello world");

// 익명 클래스
Runable r2 = new Runnable() {
  public void run() {
    System.out.println("hello world");
  }
};
```

### 3.2.2 함수 디스크립터

> 함수형 인터페이스의 추상 메서드 시그니처 signature는 람다 표현식의 시그니처
→ 함수 디스크립터는 **람다 표현식의 시그니처를 서술하는 메서드**
> 

ex) `Runnable` 인터페이스의 유일한 추상 메서드 `run()`은 인수화 반환값이 없으므로(`void` 반환) `Runnable` 인터페이스는 인수와 반환값이 없는 시그니처

```java
public void process(Runnable r) {
	r.run();
}

process(() -> System.out.println("This is awesome!!");
// 출력 결과: This is awesome!!
```

- `() → System.out.println()`은 인수가 없으며 void를 반환하는 람다 표현식
- `Runnable` 인터페이스의 `run` 메서드 시그니처와 같다

## 3.3 람다 활용: 실행 어라운드 패턴

순환 패턴 recurrent pattern

- 자원처리(DB의 파일 처리)에 사용. 자원을 열고 처리한 다음에 자원을 닫는 순서
- 설정 setup과 정리 cleanup 과정은 유사 → 실제 자원을 처리하는 코드를 설정과 정리 두 과정이 둘러싸는 형태

⇒ **실행 어라운드 패턴 execute around pattern**

![Untitled](ch3%20%E1%84%85%E1%85%A1%E1%86%B7%E1%84%83%E1%85%A1%20%E1%84%91%E1%85%AD%E1%84%92%E1%85%A7%E1%86%AB%E1%84%89%E1%85%B5%E1%86%A8%20fe6b5de57c3b479a94eab21e8bd15f42/Untitled.png)

```java
public String processFile() throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        return br.readLine(); //실제 필요한 작업을 하는 행
    }
}
```

### 3.3.1 (1단계) 동작 파라미터화를 기억하라

현재 코드는 파일에서 한 번에 한 줄만 읽을 수 있음 → 여러줄을 읽거나 자주 사용되는 단어를 반환하고 싶다면?

기존의 설정, 정리 과정은 재사용하고 processFile 메서드만 다른 동작을 수행하도록 한다

= ***processFile의 동작을 파라미터화***

```java
// 한 번에 2행을 읽을 수 있다.
String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

### 3.3.2 (2단계) 함수형 인터페이스를 이용해서 동작 전달

함수형 인터페이스 자리에 람다 사용

- 기존 시그니처: BufferedReader —> String/IOException
- 함수형 인터페이스
    
    ```java
    @FunctionalInterface
    public interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }
    
    // 정의한 인터페이스를 processFile 메서드의 인수로 전달 가능
    public String processFile(BufferedReaderProcessor p) throws IOException {
        ...
    }
    ```
    

### 3.3.3 (3단계) 동작 실행

`processFile`에서 `BufferedReaderProcessor`객체의 `process` 호출

: 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으며, 전달된 코드는 함수형 인터페이스의 인스턴스로 전달된 코드와 같은 방식으로 처리

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        return p.process(br); //BufferedReader 객체 처리
    }
}
```

### 3.3.4 (4단계) 람다 전달

```java
//한 행을 처리하는 코드
String oneLine = processFile((BufferedReader br) -> br.readLine());
//두 행을 처리하는 코드
String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

## 3.4 함수형 인터페이스 사용

함수형 인터페이스의 추상 메서드는 람다 표현식의 시그니처를 묘사한다

→ 함수형 인터페이스의 추상 메서드 시그니처 = **함수 디스크립터 function desciptor**

### 3.4.1 Predicate

`Predicate<T>` 인터페이스는 `test`라는 추상 메서드를 정의

- `test`는 제네릭 형식 T의 객체를 인수로 받아 **불리언을 반환**한다.
- 인터페이스와 같은 형태로 따로 정의할 필요 없어 바로 사용 가능
- T형식의 객체를 사용하는 boolean 표현식이 필요한 상황에서 사용 가능

ex) `String`객체를 인수로 받는 람다 정의

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}

public<T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> results = new ArrayList<>();
    for(T t : list) {
        if(p.test(t)) {
            results.add(t);
        }
    }
    return results;
}

Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```

### 3.4.2 Consumer

`Consumer<T>` 인터페이스는 `accept`라는 추상 메서드를 정의

- 제네릭 형식 T의 객체를 인수로 받아 **어떤 동작을 수행하고 싶을때** 사용할 수 있다.

ex) `forEach`와 람다를 이용해서 리스트의 모든 항목 출력

```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}

public<T> void forEach(List<T> list, Consumer<T> c) {
    for(T t : list) {
        c.accept(t);
    }
}

forEach(Arrays.asList(1,2,3,4,5), (Integer i) -> System.out.println(i));
```

### 3.4.4 Function

`Function<T, R>` 인터페이스는 제네릭 형식 T를 인수로 받아서 제네릭 형식 R 객체를 반환하는 추상 메서드 `apply`를 정의

- 입력을 출력으로 매핑하는 람다를 정의할때 활용할 수 있다.

ex) `String` 리스트를 인수로 받아 각 String의 길이를 포함하는 `Integer` 리스트로 반환하는 `map` 메서드

```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

public<T, R> List<R> map(List<T> list, Function<T, R> f) {
    List<R> result = new ArrayList<>();
    for(T t : list) {
        result.add(f.apply(t));
    }
    return result;
}

// [7, 2, 6]
List<Integer> l = map(
    Arrays.asList("lambdas", "in", "action"),
    (String s) -> s.length()
);
```

### 기본형 특화

자바의 모든 형식은 **`참조형 reference type`**(ex. Byte, Integer, Object, List, ...) 또는 **`기본형 primitive type`** (int, double, byte, char, ...)이다.

but. 제네릭의 내부 구현으로 제네릭 파라미터 `T`에는 **참조형만 사용할수 있다.**

⇒ 자바에서는 변화하는 기능 제공

- 박싱 boxing: 기본형을 참조형으로 변환
- 언박싱 unboxing: 참조형을 기본형으로 변환
- 오토박싱 autoboxing: 박싱과 언박싱을 자동으로 처리하는 기능

boxing한 값은 기본형을 감싸는 wrapper이며 heap에 저장되기 때문에 메모리를 더 소비(+기본형을 가져올 때도 메모리 탐색하는 과정 필요)

→ 자바8에서는 auto boxing을 피할 수 있도록 특별한 버전의 함수형 인터페이스도 제공한다.

ex) IntPredicate는 1000이라는 값을 박싱하지 않지만 Predicate<Integer>는 Integer 객체로 박싱

```java
public interface IntPredicate {
  boolean test(int t);
}

IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbes.test(1000);//참(박싱x)

Predicate<Integer> oddNumbers = (integer i) -> i % 2 != 0;
oddNumbes.test(1000);//거짓(박싱)
```