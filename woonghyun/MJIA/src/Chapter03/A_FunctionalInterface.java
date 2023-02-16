package Chapter03;

public class A_FunctionalInterface {

    /**
     * 오직 하나의 추상 메서드만을 가지고 있는 인터페이스를 함수형 인터페이스(Functional Interface)라 한다.
     * @param <T>
     */
    public interface Predicate<T> {
        boolean test(T t);
    }

    /**
     * 그렇다면 이런 함수형 인터페이스로 무엇을 할 수 있을까?
     * 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있다!
     * 람다 표현식의 전체 표현식을 함수형 인터페이스의 인스턴스(함수형 인터페이스를 구현한 클래스의 인터페이스가 보다 적절..)로 취급할 수 있다.
     * 아래 예시를 살펴보자.
     */

    // 1. 람다 사용
    // Runnable.run() 추상 메서드를 아래와 같이 람다 표현식으로 구현했음을 알 수 있다.
    static Runnable r1 = () -> System.out.println("Hello, World!");

    // 2. 익명 클래스 사용
    static Runnable r2 = new Runnable() {
        @Override
        public void run() {
            System.out.println("Hello World 2");
        }
    };

    /*
        원래대로라면 Runnable 인터페이스를 구현하는 구현체 클래스를 생성하고, 해당 클래스에 run() 메서드를 구현한 뒤,
        new 연산자로 해당 클래스를 생성해야 한다.
     */

    public static void process(Runnable r) {
        r.run();
    }


    public static void main(String[] args) {
        process(r1);
        process(r2);
        process(() -> System.out.println("Hello World 3"));
    }



}
