## **람다 표현식**

> 람다 표현식이란 메서드로 전달할 수 있는 익명 함수를 단순화한 것

람다는 이름이 없는 **익명**이며, 클래스에 종속되지 않으므로 메서드 보다는 **함수**라고 부른다.

ex)

```
(Apple a1, Apple a2) -> a.getWeight().compareTo(a2.getWeight());
```

람다 표현식은 위와 같이, 파라미터 리스트, 화살표, 람다 바디로 구성되어 있다.

여기서 유효한 람다 표현식을 사용하도록 문법이 있는데 다음과 같다.

1\. (parameters) -> expression

2\. (parameters) -> { statements; }

expression은 특정 값을 가지는 모든 코드 조각을 이야기하며, statements는 한개 이상의 expression이 포함되어 어떤 로직이 진행되는 것을 말한다. 따라서 {....} 내부에 단순히 expression 하나만 있는 것이 아니라, 어떤 로직에 의해 도출된 return 값이나 메서드가 존재해야 한다. 특별히 void를 반환하는 함수는 중괄호가 필요하지 않다.

## 람다를 어디에 사용하면 좋을까?

### 1\. 함수형 인터페이스

함수형 인터페이스란 여러 개의 디폴트 메서드가 존재하더라도, 단 하나의 추상 메서드를 가진다.

따라서 람다 표현식으로 함수형 인터페이스를 구현하게 된다면, 단 하나의 추상 메서드를 구현하는 것이므로 함수형 인터페이스의 인스턴스로 취급할 수 있다.

### 2\. 함수 디스크립터

함수형 인터페이스의 시그니처(파라미터와, 반환값 타입)를 작성한 것을 함수 디스크립터라고 한다.

```
public void process(Runnable r) {
	r.run();
}

process(() -> System.out.println("Hello World 3"));
```

함수 인자로 Runnable 인터페이스의 run 메서드 시그니처가 인자를 갖지 않고, void를 리턴하기 때문에 위 람다식은 유효하다.

## 람다 활용 : 실행 어라운드 패턴

어라운드 패턴은 작업 시작 전후에, 명시적으로 초기화를 하거나 정리하는 코드를 작성할 필요가 없어지므로 필요한 작업의 로직에만 집중할 수 있게 도와준다.

[##_Image|kage@bz4Jfc/btrPEoKYdUL/qNIVejz1KMWApksgUTrz21/img.png|CDM|1.3|{"originWidth":447,"originHeight":186,"style":"alignCenter","caption":"어라운드 패턴"}_##]

```
public String processFile() throws IOException {
        try (BufferdReader br = new BufferedReader(new FileReader("data.txt"))) {
                return br.readLine();
    }
}
```

한줄씩 읽어오는 processFile메소드가 존재하는데 두 줄씩 읽으려면 이 메소드를 두번실행 하거나, return에 br.readLine()을 하나 더 추가 하면 된다. 

하지만 여기서 processFile메소드는 단순히 파일을 읽는 코드로 초기에 세팅되어야 하고 우린 그것을 이용해서 여러 작업을 진행해야 하기 때문에 아래와 같이 람다 표현식을 사용해서 몇줄을 읽을 것인지 정해준다.

```
processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

이 코드가 실행이 되어야 두 줄씩 읽을 수 있지만, 현재 processFile 메소드는 인자를 받는 함수형 인터페이스가 존재하지 않기 때문에  BufferedReader를 인자로 받고 String을 리턴하는 시그니처를 가지는 함수형 인터페이스를 만들어야 한다.

```
@FunctionalInterface
public interface BufferedReaderProcessor {
	String process(BufferedReader b) throws IOException;
}
```

이제 processFile메소드를 BufferedReader를 인자로 받고 String으로 리턴하는 시그니처에 맞도록 수정한다.

```
public String processFile(BufferedReaderProcessor p) throws IOException {
        try (BufferdReader br = new BufferedReader(new FileReader("data.txt"))) {
                return p.process(br);
    }
}
```

이제 시그니처에 맞게 완료됐고, 람다식을 통해 호출을 하게 되면 n개의 줄을 읽을 수 있는 함수가 됐다.

## 함수형 인터페이스 사용

### 1\. Predicate

```
@FunctionalInterface
public interface Predicate<T> {
    booelan test(T t);
}

public <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> results = new ArrayList<>();
    for(T t : list) {
        if (p.test(t)) {
            results.add(t);
        }
    }
    return results;
}

Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```

**함수형 인터페이스에서 정해놓은 추상메서드의 시그니처에 맞는지 확인하면서 보면 알아보기 쉬운 거 같다.**

T 객체를 받아서 Boolean Return

### 2\. Consumer

Consumer는 T 객체를 인수로 받아 어떤 동작을 수행하고 void를 반환하는 함수형 인터페이스다.

### 3\. Function

Function은 T 객체를 인수로 받아 R 객체를 반환하는 추상 메서드 apply를 정의한다.

-   예를 들면 사과 리스트가 있는데 사과의 무게만을 추출하는 경우에 사용할 수 있다.

## **람다 형식 검사, 추론**

람다의 형식 검사는 다음의 과정으로 진행이 된다.

1\. 람다 표현식 작성

2\. 람다 표현식의 대상 형식 확인

3\. 대상 형식의 추상메서드 확인

4\. 추상메서드의 반환값과 람다표현식의 반환값을 비교 (시그니처 확인)

형식 추론은 람다 표현식에서 파라미터의 타입을 명시하지 않을 때, 컴파일러가 추론하는 것을 말하는데 코드를 한번 살펴보자

```
// 타입 명시
Comparator<Apple> = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

// 타입 생략
Comparator<Apple> = (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
```

내 생각에는 제너릭 타입에 사용자가 직접 정의한 객체를 사용하는 경우에, 타입을 명시해주는 것이 가독성이 좋을 거 같다.

### **제약**

람다 표현식은 지역변수를 본인의 바디에서 사용할 수 있는데, 이 동작은 **람다 캡처링(capturing lambda)** 이라고 한다. 이 람다 캡처링은 지역변수를 사용하는 그 타이밍에 해당 변수를 final로 취급하는데, 람다 표현식에서 사용된 후, 해당 지역변수가 재할당 되면 해당 코드는 컴파일 되지 않는다.

> 재할당이 불가능한 이유는 지역변수는 스택에 존재하기 때문에, 모든 과정을 마치고 할당이 해제되면 더이상 사용할 수 없다. 할당이 해제되고 난 후에, 람다 스레드가 추후에 해당 지역변수를 호출할 수 도 있기 때문에 처음에 지역변수를 복사해서 사용하고, 복사본의 값을 바꿀 수 없어서 재할당이 불가능하다.

## **메서드 참조**

메서드 참조는 :: 를 사용해서 해당 클래스에 정의된 메소드를 참조하는 것을 말한다.

메서드 참조 유형에는 총 가지가 존재한다.

1\. 정적 메서드 참조

2\. 다양한 형식의 인스턴스 메서드 참조

3\. 기존 객체의 인스턴스 메서드 참조

이 세가지의 유형이 어떤 방식으로 메서드 참조가 이뤄지는지 에시를 통해 알아보자.

먼저 정적 메서드 참조의 예시는 Integer의 parseInt()로 살펴보자

```
public static int parseInt(String s) throws NumberFormatException {
        return parseInt(s,10);
}

// 1
(String s) -> Integer.parseInt(s)

// 2
Integer::parseInt

// 적용
Function<String, Integer> stringToInteger = Integer::parseInt
```

**복습**! Funciton<T, R>은 T를 인수로 받아 로직을 거친 후 R객체를 반환하는 함수형 인터페이스

두번째로, 다양한 형식의 인스턴스 메서드 참조를 보자

```
BiPredicate<List<String>, String> contains = (list, element) -> list.contains(element);

// 메소드 참조 
BiPredicate<List<String>, String> contains = List::contains;

// 함수 디스크립터 분석
public interface BiPredicate<T, U> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    boolean test(T t, U u);
}

(List<String> list, String s) -> list.contains(s);
ClassName(List)::instanceMethod(contains)

// 적용
BiPredicate<List<String>, String> contains = List::contains;

final String y = "yy";
List<String> sList = new ArrayList<>();
sList.add("a");
sList.add("yy");
sList.add("c");

// 리스트에 있는지 없는지 적용
contains.test(sList, y);

// 문자열 하나씩 보려면 이렇게 할 수 밖에?
System.out.println(
    sList
            .stream()
            .map((String x) -> x.contains(y))
            .toList()
);
```

마지막으로 기존 객체의 인스턴스 메서드 참조인데

```
Predicate<String> startsWithNumber = (String string) -> this.startsWithNumber(string);
```

어떻게 동작하는거지????????? 어떻게 활용??????????

## **생성자 참조**

생성자 참조는 new키워드를 사용해서 기존 생성자의 참조를 만들 수 있다.

Supplier나 Function을 통해서 만들 수 있는데 간단히 예시로 알아보면 아래와 같다.

```
// Supplier의 get 메서드 호출해서 객체 만들기
Supplier<Apple> c1 = Apple::new;
Apple a1 = c1.get();


// Function apply 메서드로 객체 만들기
Fuction<Integer, Apple> c2 = Apple::new; // 생성자는 다음과 같다. Apple(Intger weight)
Apple a2 = c2.apply(110); //


// Function 확장 -> 생성자 파라미터 두개 받기
BiFunction<Color, Integer, Apple> c3 = Apple::new; // Apple(String color, Integer weight)
Apple c3 = c3.apply(GREEN, 110);
```