package Chapter05;

import java.util.Arrays;
import java.util.List;

public class F_Reducing {
    public static void main(String[] args) {
        /**
         * 요소의 합 구하기
         * reduce를 이용해 다음처럼 스트림의 모든 요소를 더할 수 있다.
         * reduce는 2개의 인수를 갖는다.
         * 1. 초깃값 0
         * 2. 두 요소를 조합해서 새로운 값을 만드는 BinaryOperator<T>.
         */
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        int sum = numbers.stream().reduce(0, (a, b) -> a + b);

        // 곱셈은 다음과 같다.
        int product = numbers.stream().reduce(0, (a, b) -> a * b);

        // 메서드 참조를 이용해서 이 코드를 좀 더 간결하게 만들 수 있다.



    }
}
