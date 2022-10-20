## **변화하는 요구사항에 대응**

현실세계에서, 요구사항은 끊임없이 바뀐다. 이렇게 시시각각 변하는 요구사항들을 유연하고 효과적으로 대응을 해야한다. 이것이 프로그래밍에서 어떻게 되는지 알아본다.

하나의 과정을 통해 생각해보자

### **첫번째 방법: 녹색 사과 필터링**

```
enum Color { RED, GREEN }

public static List<Apple> filterGreenApples(List<Apple> inventory) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if (GREEN.equals(apple.getColor()) {
			result.add(apple);
		}
	}
}
```

현재 녹색 사과에 대해서만 필터링을 하고 있는데, 요구사항이 변경되어 다른 색의 사과들이 추가되면 어떻게 수정을 해야할까?

단순히 filterAnyApples()메소드를 추가해서 처리할 수 있겠지만, 색이 엄청 많아진다면 적절하게 대응하기는 힘들어진다.

> 비슷한 코드가 반복 존재하면 해당 코드를 추상화하자.

### **두번째 방법: 색을 파라미터화**

```
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if (apple.getColor().equals(color) {
			result.add(apple);
		}
	}
}
```

위 코드와 같이, 파라미터로 색을 받는다면 각각의 색을 파라미터로 호출을 하면 되기 때문에 편리해진다.

하지만 여기서 농부의 요구사항이 더 추가된다면 어떻게 될까? 가령 사과의 무게가 x이상이 되는 사과를 필터하고 싶다는 조건이 추가된다면 단순히 파라미터를 하나 넣고 조건을 하나 더 추가하면 될것이다.

여기서도 문제는 있다.

-   해당 메소드를 지속적으로 호출하게 될텐데 여기서 계속 대부분의 코드가 중복되므로 성능면에서 이점이 있기가 힘들다.

### **세번째 방법: 가능한 모든 속성을 파라미터화**

```
public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if ((flag && apple.getColor().equals(color)) ||
			(!falg && apple.getWeight() > weight)) {
			result.add(apple);
		}
	}
	return result;
}
```

위 코드를 호출해보자

```
List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
List<Apple> heavyApples = filterApples(inventory, null, 150, false);
```

정말 좋지 않은 코드다. 여기서 true와 false가 의미하는 것이 무엇인지 조차 알 수 없다. 따라서 요구사항에 유연하게 대처할 수 없게 된다.

## **동작 파라미터화**

위에서 요구사항이 변화할 때마다, 파라미터를 추가하여 대응하는 것이 좋지 않음을 알았다. 이제 다르게 접근을 해보자

사과의 어떤 속성에 대해 불리언값을 반환하는 방법을 생각해보자. 이렇게 참과 거짓을 반환하는 함수를 `프레디케이트`라고 하며, 선택 조건을 결정하는 인터페이스를 정의해보자.

```
public interface ApplePredicate {
	boolean test (Apple apple);
}

public class AppleHeavyWeightPrdicate Implements ApplePredicate {
	public boolean test(Apple apple) {
		return apple.getWeight() > 150;
	}
}

public class AppleGreenColorPredicate implements Applepredicate {
	public boolean test(Apple apple) {
		return GRREN.equals(apple.getColor());
	}
}
```

이제 ApplePredicate라는 사과 전략을 캡슐화 했고, 사과 조건에 따라 다양한 필터를 구현할 수 있다. 이 전략을 `strategy design pattern`이라고 한다.

-   이 전략 디자인 패턴은 각 알고리즘을 캡슐화하는 알고리즘 패밀리(ApplePredicate)를 정의해둔 다음 런타임에 알고리즘을 선택하는 방법이다.

이것으로 어떻게 메소드에 적용할 수 있을까?

### **네번째 방법: 추상적 조건으로 필터링**

```
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple : inventory) {
		if (p.test(apple)) {
			reuslt.add(apple);
		}
	}
	return result;
}
```

이제 filterApples()메소드를 다음처럼 동작 자체를 파라미터로 받을 수 있다. 이 후에 추가하고 싶은 Predicate는 간단하게 구현을 하면 되기 때문에 엄청난 유연성을 가지게 되었다.

이렇게 ApplePredicate 객체로 동작을 파라미터로 넘기게 되어 filterApples() 메소드의 동작이 결정되었다.

하지만 여기에서도, 우리는 인터페이스를 구현하는 여러 클래스와 그것을 인스턴스화 하는 불필요한 과정을 거치게 된다. 이것을 어떻게 개선할 수 있을까?

## **익명 클래스**

익명 클래스는 자바의 local class와 비슷한 개념인데 말 그대로 이름이 없고, 클래스 선언과 인스턴스화를 동에 할 수 있다.

### **다섯번째 방법: 익명 클래스 사용**

```
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
	public boolean test(Apple apple) {
		return RED.equals(apple.getColor());
	}
});
```

하지만 익명클래스 또한 코드 구현이 장황하고 익명 클래스는 사용하는데 조금 익숙하지 않은 단점도 있다. 

> 결론적으로 새로운 동작을 정의하는 메서드를 구현해야 하는 점은 변하지 않는다.

### **여섯번째 방법: 람다 표현식 사용**

```
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor());
```

위 익명 클래스의 코드를 위와 같은 코드로 바꿀 수 있다.

### **일곱번째 방법: 리스트 형식으로 추상화**

```
public interface Predicate<T> {
	boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
	List<T> result = new ArrayList<>();
	for (T e : list) {
		if (p.test(e)) {
			result.add(e);
		}
	}
	return result;
}

List<Apple> redApples = filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

이제 list에는 사과뿐만 아니라 다른 여러 과일, 객체들도 올 수 있게 됐다. 

## Comparator로 정렬하기

Comparator 인터페이스

```
public interface Comparator<T> {
	int compare(T o1, T o2);
}

inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
});
```

컬렉션의 sort메소드 매개변수로 Comparator 익명클래스를 생성해서 위와 같이 구현할 수 있다.

이것을 더욱 간단하게 람다식으로 바꿔보자

```
inventory.sort(
    (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

## **결론**

1\. 동작 파라미터화는 메서드가 다양한 동작을 수행할 수 있도록 코드를 파라미터로 전달하는 것을 말한다.  
2\. 인터페이스를 상속받아 여러 클래스를 구현해야 하는 것보다 람다 표현식을 사용하자.