# CH2 동작 파라미터화 코드 전달하기

> **동적 파라미터화 Behavior Parameterization**
> 
> : 어떻게 실행할 것인지 결정하지 않은 코드 블록



## 2.1 변화하는 요구사항에 대응하기

녹색 사과 필터링

```java
public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getColor() == Color.GREEN) {
        result.add(apple);
      }
    }
    return result;
  }
```

색 파라미터화 `Color color`

```java
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getColor() == color) {
        result.add(apple);
      }
    }
    return result;
  }
```

무게 정보 파라미터

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



## 2.2 동작 파라미터화

프레디케이트 Predicate: True 또는 False를 반환하는 함수

선택 조건을 결정하는 인터페이스

```java
public class ApplePredicate {
   boolean test(Apple apple);
 }

 public class AppleHeavyWeightPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
      return apple.getWeight() > 150;
    }
  }
```

![https://blog.kakaocdn.net/dn/VNuHa/btq6EVNBGw6/wqJHUIpUCQ0of1RjHsok6K/img.png](https://blog.kakaocdn.net/dn/VNuHa/btq6EVNBGw6/wqJHUIpUCQ0of1RjHsok6K/img.png)

선택 전략 캡슐화(Strategy Pattern)

```java
 public static List<Apple> filter(List<Apple> inventory, ApplePredicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (p.test(apple)) {
        result.add(apple);
      }
    }
    return result;
  }
```

메서드가 다양한 동작(또는 전략)을 받아서 내부적으로 다양한 동작 수행

**추상적 조건으로 필터링**

:Predicate 객체로 캡슐화된 Boolean 표현식을 이용해서 사과를 필터링

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
  public boolean test(Apple a) {
    return RED.equals(apple.getColor());
  }
});
```

- 코드/동작 전달 → 다양한 ApplePredicate를 만들어서 filterApples 메서드로 전달 가능
- test 메서드: 새로운 동작을 정의
  - 인수를 객체만으로
  - test 메서드를 ApplePredicate 객체로 감싸서 전달
  
  

## 2.3 복잡한 과정 간소화

**익명클래스**

- 자바의 지역 클래스(local class)와 비슷한 개념
- 클래스 선언과 인스턴스화 동시에 가능 → 즉석에서 필요한 구현 만들어 사용

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
  public boolean test(Apple a) {
    return RED.equals(apple.getColor());
  }
});
```

**람다 표현식**

```java
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

![](C:\Users\NAMEUNSEONG\AppData\Roaming\marktext\images\2022-10-19-22-41-02-image.png)



**리스트 형식으로 추상화(자바8부터 가능)**

```java
public interface predicate<T> {
  boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
  List<T> result = new ArrayList<>();
  for(T e : list) {
    if(p.test(e)) {
      result.add(e);
    }
  }
  return result;
}

List<Apple> redApples = filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
List<Apple> evenNumers = filter(numbers, (Integer i) -> i % 2 == 0);
```

## 2.4 실전 예제

**Comparator로 정렬**

```java
public interface Comparator<T> {
  int compare(T o1, T 02);
}

inventory.sort(new Comparator<Apple>() {
  public int compare(Apple a1, Apple a2) {
    return a1.getWeight().compareTo(a2.getWeight());
  }
});

// 람다식
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

**Runnable로 코드 블록 실행하기**

```java
public interface Runnable {
  void run();
}

Thread t = new Thread(new Runnable() {
  public void run() {
    System.out.println("hello world");
  }
});

// 람다식
Thread t = new Thread(() -> System.out.println("Hello world"));
```

**Callable로 결과 반환**

자바5부터 지원하는 ExecutorService 인터페이스를 통해 task를 thread pool로 보내고 결과를 Future로 저장
