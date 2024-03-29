# 3.1 람다란 무엇인가?
메서드로 전달할 수 있는 익명 함수를 단순화한 것이다. 
- 익명 : 보통의 메서드와 달리 이름이 없으므로 익명이라 표현한다.
- 함수 : 람다는 메서드처럼 특정 클래스에 종속되지 않으므로, 함수라고 부른다. 하지만 메서드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외 리스트를 포함한다.
- 전달 : 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
- 간결성 : 익명 클래스처럼 많은 자질구레한 코드를 구현할 필요가 없다.

람다 표현식은 파라미터, 화살표, 바디로 이루어진다.
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
    람다 파라미터       화살표               람다 바디


## 자바 8의 유효한 람다 표현식
```text
(String s) -> s.length(); // String 형식의 파라미터 하나를 가지며 int를 반환한다.
(Apple a) -> a.getWeight() > 150
(int x, int y) -> {  // int 형식의 파라미터 2개를 가지며 리턴값이 없다. (void 리턴)
    System.out.println("Result : ");
    System.out.println("x + y);
() -> 42 // 파라미터가 없으며 int 42를 반환한다.
(Apple a1, Apple a2) ->< a1.getWeight().compareTo(a2.getWeight())
```
람다 표현식에는 return이 함축되어 있으므로 명시적으로 사용하지 않아도 된다.


# 3.2 함수형 인터페이스
추상메서드가 오직 한개인 인터페이스

## 3.2.2 함수 디스크립터
