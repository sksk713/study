# Chap03 - 람다 표현식

생성일: 2022년 10월 11일 오후 3:55

익명 클래스로 다양한 동작을 구현할 수 있지만, 만족할만큼 코드가 깔끔하지는 않았다. 다음의 예시를 살펴보자.

```java
// Runnable 인터페이스는 하나의 추상 메서드만을 가지므로 함수형 인터페이스이다.
public interface Runnable {
		void run();
}

// 이런 함수형 인터페이스를 익명 클래스로 구현해보자.
Runnable r = new Runnable() {
		@Override
		void run() {
				System.out.println("Hello, World!");
		}
}
```

앞서 살펴본 동작 파라미터화에서도 알 수 있듯이, 익명 클래스를 활용하면 유연한 코드를 만들 수 있다. 또한 한번만 쓸 코드를 위해 굳이 클래스를 정의할 필요도 없다.

다만 여전히 코드가 깔끔하지는 않다. 람다 표현식을 활용하면 위 코드를 더욱 깔끔하게 사용할 수 있다.

```java
Runnable r = () -> System.out.println("Hello, World!");
```

우선은 람다 표현식이 익명 클래스와 비슷하다고 생각하자.

## 3.2 람다란 무엇인가?

람다 표현식은 메서드로 전달할 수 있는 익명 함수를 단순화한 것이라고 할 수 있다. 람다 표현식에는 이름은 없지만

- 파라미터 리스트
- 바디
- 반환 형식
- 발생할 수 있는 예외리스트

는 가질 수 있다. 또한 다음과 같은 특징을 가진다.

**익명** : 보통의 메서드와 달리 이름이 없으므로 익명이라 표현한다. 

**함수** : 람다는 메서드처럼 특정 클래스에 종속되지 않으므로, 함수라고 부른다. 하지만 메서드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외리스트를 포함한다.

**전달** : 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.

**간결성** : 자질구레한 코드를 구현할 필요가 없다.

다음은 람다 표현식을 사용하지 않은 기존 코드이다.

```java
Comparator<Apple> byWeight = new Comparator<Apple>() {
		public int compare(Apple a1, Apple a2) {
				return a1.getWeight().compareTo(a2.getWeight());
		}
};
```

다음은 람다를 이용한 코드이다.

```java
Comparator<Apple> byWeight =
		(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```

매우 간결해진 것을 확인할 수 있다. 람다는 다음과 같은 세 부분으로 나뉜다.

1. **람다 파라미터** - `(Apple a1, Apple a2)` 
2. **화살표** - `->` 
3. **람다 바디** - `a1.getWeight*().compareTo(a2.getWeight());` 

다음 예제에서 나오는 람다 표현식은 모두 유효한 람다 표현식이다.

```java
// 람다 표현식은 return을 함축하고 있으므로 명시적으로 사용하지 않아도 된다. 
(String s) -> s.length()
(Apple a) -> a.getWeight() > 150

// 리턴값이 없다. (void 리턴)
// 또한 람다 표현식은 여러 행의 문장을 포함할 수 있다.
(int x, int y) -> {
		System.out.println("Result:");
		System.out.println(x + y);
}

// 파라미터가 없으며 int 42를 반환한다.
() -> 42

(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
```

### 퀴즈 3-1. 람다 표현식

```java
() -> {}                               // 유효한 람다식
() -> "Raoul"                          // 유효한 람다식
() -> { return "Mario"; }              // 유효한 람다식
(Integer i) -> return "Alan" + i;      // 유효하지 않은 람다 표현식
(String s) -> { "Iron Man"; }          // 유효하지 않은 람다 표현식
```

1. 파라미터가 없으며 void를 반환하는 람다 표현식이다. 
2. 파라미터가 없으며 문자열을 반환하는 표현식
3. 파라미터가 없으며 명시적으로 return을 표현해 문자열을 반환하는 표현식
4. return은 흐름 제어문이다. 따라서 `(Integer i) -> { return "Alan" + i;}` 처럼 되어야 올바른 표현식이다.
5. “Iron Man”은 구문(statement)이 아니라 표현식(expression)이다.
    
     `(String s) -> "Iron Man"` 이 올바른 표현식이다. 
    
    또는 `(String s) -> {return "Iron Man";}` 처럼 명시적으로 return문을 사용해야 한다.
    

<aside>
ℹ️ expression : 단일값으로 평가될 수 있는 코드. 예를 들어 String인 `"Iron Man"` , int값인 `3` 등이 식에 해당한다.
statement : 실행가능한 최소한의 독립된 코드 조각. `return "Iron Man"` 의 경우 하나의 식(expression)을 포함하고 있는 문(statement)이다. 또한 `String str = "Iron Man";` 또한 하나의 식을 포함하고 있는 문이라 할 수 있다.

</aside>

## 3.2 어디에, 어떻게 람다를 사용할까?

### 3.2.1 함수형 인터페이스

2장에서 만든 Predicate<T> 인터페이스로 필터 메서드를 파라미터화할 수 있었음을 기억하는가? 바로 Predicate<T>가 함수형 인터페이스이다. Predicate<T>는 오직 하나의 추상 메서드만 지정하기 때문이다.

```java
public interface Predicate<T> {
		boolean test (T t);
}
```

간단히 말해, **함수형 인터페이스는 정확히 하나의 추상 메서드를 지정하는 인터페이스이다**.

```java
// 다양한 함수형 인터페이스들

public interface Comparator<T> {
		int compare(T o1, T o2);
}

public interface Runnable {
		void run();
}

public interface ActionListener extends EventListener {
		void actionPerformed(ActionEvent e);
}

public interface Callable<V> {
		V call() throws Exception;
} 

public interface PrivilegedAction<T> {
		T run();
}
```

<aside>
💡 인터페이스는 default method를 포함할 수 있다. 다만 많은 디폴트 메서드가 있더라도 추상 메서드가 오직 하나면 해당 인터페이스는 함수형 인터페이스이다.

</aside>

### 퀴즈 3-2. 함수형 인터페이스

```java
// 다음 인터페이스 중 함수형 인터페이스는 어느 것인가?

// 1.
public interafce Adder {
		int add(int a, int b);
}

// 2.
public interface SmartAdder extends Adder {
		int add(double a, double b);
}

// 3.
public interface Nothing {}
```

정답은 1번만 함수형 인터페이스이다.

- 2번의 경우 부모 인터페이스의 추상 메서드인 `add` 를 포함해서 2개의 추상 메서드를 갖고 있다.
- 3번의 경우 추상 메서드가 없으므로 함수형 인터페이스가 아니다.

함수형 인터페이스로 뭘 할 수 있을까? 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으므로 **전체 표현식을 함수형 인터페이스의 인스턴스로 취급할 수 있다.** (기술적으로 따지만 함수형 인터페이스를 구현한 클래스의 인터페이스)

다음 예제를 살펴보자.

```java
Runnable r1 = () -> System.out.println("Hello, World!"); 

Runnable r2 = new Runnable() {
		public void run() {
				System.out.println("Hello, World2"); // 익명 클래스 사용
		}
};

public static void process(Runnable r) {
		r.run();
}

process(r1); // Hello, World를 출력
provess(r2); // Hello, World2를 출력
process(() -> System.out.println("Hello, World3"));
// 직접 전달된 람다 표현식으로 Hello, World3을 출력
```

### 3.2.2 함수 디스크립터 (Function Descriptor)

<aside>
💡 **메서드 시그니처(Method Signature)란?**
→ 메서드 이름과 매개변수 리스트의 조합을 뜻한다. 아래 예시를 살펴보자.

public void funcA(int x, int y) {
    // …
}

public void funcA(int x, int y, double z) {
    // …
}

두 메서드의 시그니처는 각각
funcA(int, int)
funcA(int, int, double)
이다. **Java 컴파일러는 오버로딩된 함수를 메서드 시그니처를 활용하여 구별하게 된다**.

</aside>

함수형 인터페이스의 추상 메서드 시그니처(Signature)는 람다 표현식의 시그니처를 가리킨다. 

람다 표현식의 시그니처를 서술하는 메서드를 함수 디스크립터라고 부른다. 예를 들어 Runnable 인터페이스의 유일한 추상 메서드 `run()` 은 인수와 반환값이 없으므로 Runnable 인터페이스는 인수와 반환값이 없는 시그니처로 생각할 수 있다.

`() -> void` 표기는 파라미터 리스트가 없으며, `void` 를 반환하는 함수를 의미한다. 즉, 앞에서 설명한 Runnable이 이에 해당한다. `(Apple, Apple) → int` 는 두 개의 Apple을 인수로 받아 int를 반환하는 함수를 가리킨다. 

<aside>
ℹ️ 조금 이상해보일 수 있지만, 아래는 정상적인 람다 표현식이다.

process(() → System.out.println(”This is awesome”));

위 코드에서는 중괄호 (`{}`)를 사용하지 않았고, `System.out.println()` 은 void를 반환하므로 완벽한 표현식이 아닌 것처럼 보인다. 그럼 다음처럼 코드를 중괄호로 감싸면 어떨까?

process(() → { System.out.println(”This is awesome”); });

결론적으로 이미 살펴본 것처럼 중괄호는 필요없다. 자바 언어 명세에서는 void를 반환하는 메서드 호출과 관련한 특별한 규칙을 정하고 있기 때문이다.
**즉, 한 개의 void 메서드 호출은 중괄호로 감쌀 필요가 없다.**

</aside>

**‘왜 함수형 인터페이스를 인수로 받는 메서드에만 람다 표현식을 사용할 수 있을까?’** 라는 의문이 생길 수 있다. 언어 설계자들은 자바에 함수 형식(람다 표현식을 표현하는 데 사용한 시그니처와 같은 특별한 표기법.)을 추가하는 방법도 대안으로 고려했다. 하지만 언어 설계자들은 언어를 더 복잡하게 만들지 않는 현재의 방법을 선택했다. 또한 대부분의 자바 프로그래머가 하나의 추상 메서드를 갖는 인터페이스(예를 들면 이벤트 처리 인터페이스)에 이미 익숙하다는 점도 고려했다.

### 퀴즈 3-3. 어디에 람다를 사용할 수 있는가?

```java
// 다음 중 람다 표현식을 올바르게 사용한 코드는?

// 1.
execute(() -> {});
public void execute(Runnable r) {
		r.run();
}

// 2. 
public Callable<String> fetch() {
		return () -> "Tricky example ;-)";
}

// 3.
Predicate<Apple> p = (Apple a) -> a.getWeight();

/**
 * 1번, 2번이 유효한 람다 표현식이다.
 * 첫번째 예제에서 람다 표현식 () -> {}의 시그니처는 () -> void이며, Runnable의 추상메서드
 * run의 시그니처와 일치하므로, 유효한 람다 표현식이다.
 * 다만 람다의 바디가 비어있으므로 실행하더라도 아무 일도 일어나지 않는다.
**/

/**
 * 2번 : fetch 메서드의 반환 형식은 Callable<String>이다.
 * T를 String으로 대치했을 때 Callable<String> 메서드의 시그니처는 () -> String이 된다. 
 * () -> "Tricky example ;-)"는 () -> String 시그니처이므로 문맥상 유효하다.
**/

/**
 * 세번째 예제에서 (Apple a) -> a.getWeight()의 시그니처는 (Apple) -> Integer이므로
 * Predicate<Apple>: (Apple) -> boolean의 test 메서드의 시그니처와 일치하지 않는다.
 * 따라서 유효한 람다 표현식이 아니다.
**/
```

<aside>
💡 복습 - 익명 클래스를 이용한 인터페이스의 구현
만약 1번 예제에서 람다 표현식을 사용하지 않았다면, 다음과 같이 직접 인터페이스를 구현해야 한다.

Runnable r = new Runnable() {

    @Override
    void run() { System.out.println(”Hello”); }
}
execute(r);

람다 표현식을 사용해 아래와 같이 간단하게 작성할 수 있다.
execute(() → {});

</aside>

<aside>
ℹ️ @FunctionalInterface는 무엇인가?
새로운 자바 API를 살펴보면, 함수형 인터페이스에 @FunctionalInterface 어노테이션이 추가되어 있다. 이는 함수형 인터페이스임을 명시적으로 나타내는 어노테이션이다.
@FunctionalInterface를 선언했지만 실제로 함수형 인터페이스가 아니라면 컴파일러가 에러를 발생시킨다. 예를 들어 추상 메서드가 한 개 이상이라면 Multiplenonoverriding abstract methods found in interfae Foo(인터페이스 Foo에 오버라이드 하지 않은 여러 추상 메서드가 있음)같은 에러가 발생할 수 있다.

</aside>

## 3.3 람다 활용 : 실행 어라운드 패턴

람다와 동작 파라미터화로 유연하고 간결한 코드를 구현하는 데 도움을 주는 실용적인 예제를 살펴보자.

자원 처리(ex: DB의 파일 처리)에 사용하는 순환 패턴(recurrent pattern)은 자원을 열고 처리한 다음에, 자원을 닫는 순서로 이뤄진다. 아래 그림과 같은 형식의 코드를 실행 어라운드 패턴이라고 부른다.

→ 즉 작업을 처리하기 전후로 초기화 - 정리 과정이 필요한 코드를 실행 어라운드 패턴이라고 부른다.

![Untitled](Chap03%20-%20%E1%84%85%E1%85%A1%E1%86%B7%E1%84%83%E1%85%A1%20%E1%84%91%E1%85%AD%E1%84%92%E1%85%A7%E1%86%AB%E1%84%89%E1%85%B5%E1%86%A8%208a82690001ee4c65a60e67d6a0ef2c0d/Untitled.png)

다음 예제에서 굵은 글씨는 파일에서 한 행을 읽는 코드이다.

```java
public String processFile() throws IOException {
		**try (BufferedReader br = 
						new BufferedReader(new FileReader("data.txt")))** {
				return br.readLine(); // 실제 필요한 작업을 하는 행
		}
}

// 자바 7에 추가된 try-with-resources 구문을 사용했다.
// 이를 사용하면 자원을 명시적으로 닫을 필요가 없으므로 간결한 코드를 구현하는 데 도움을 준다.
```

### 3.3.1 1단계 : 동작 파라미터화를 기억하라

위 예제는 파일에서 한 번에 한줄만을 읽을 수 있다. 하지만 

- 한 번에 두줄을 읽으면서 처리
- 가장 자주 사용되는 단어 반환

등 비즈니스 로직이 변화한다면 어떻게 할까? 기존의 설정 (초기화) / 정리 과정은 재사용하고 로직만 다른 동작을 수행할 수 있게끔 한다면 좋을 것이다. → **동작 파라미터화하자!**

processFile 메서드에 수행할 동작을 전달하면 된다. 이 때 람다를 이용해서 동작을 전달할 수 있다. 다음은 두 행을 출력하는 코드이다.

```java
// 람다를 이용해서 processFile의 동작 파라미터화
String result = processFile((BufferedReader br -> 
												br.readLine() + br.readLine());
```

### 3.3.2 2단계 : 함수형 인터페이스를 이용해서 동작 전달

함수형 인터페이스 자리에 람다를 사용할 수 있다. 따라서 BufferedReader → String과 IOException을 던질 수 있는 시그니처와 일치하는 함수형 인터페이스를 만들어야 한다. 이 인터페이스를 BufferedReaderProcessor라고 정의하자.

```java
@FuntionalInterface
public interface BufferedReaderProcessor {
		String process(BufferedReader b) throws IOException;
}
```

### 3.3.3 3단계 : 동작 실행

이제 `BufferedReaderProcessor` 에 정의된 process 메서드의 시그니처 (BufferedReacer → String)와 일치하는 람다를 전달할 수 있다. 람다의 코드가 processFile 내부에서 어떻게 실행되는지 기억하고 있는가?

람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으며 전달된 코드는 함수형 인터페이스의 인스턴스로 전달된 코드와 같은 방식으로 처리한다. 

따라서 processFile 바디 내에서 BufferedReaderProcessors 객체의 process를 호출할 수 있다.

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
		try (BufferedReader br =
				new BufferedReader(new FileReader("data.txt"))) {
				return p.process(br);
		}
}
```

### 3.3.4 4단계 : 람다 전달

이제 람다를 이용해서 다양한 동작을 processFile 메서드로 전달할 수 있다.