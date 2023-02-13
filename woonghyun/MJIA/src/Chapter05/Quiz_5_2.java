package Chapter05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Quiz_5_2 {
    public static void main(String[] args) {
        /**
         * 숫자 리스트가 주어졌을 때, 각 숫자의 제곱으로 이루어진 리스트를 반환하시오.
         * 예를 들어 [1, 2, 3, 4, 5]가 주어지면 [1, 4, 9, 16, 25]
         */
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        List<Integer> result = numbers.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());

        /**
         * 두 개의 숫자 리스트가 있을 때, 모든 숫자 쌍의 리스트를 반환하시오.
         * 예를 들어 [1, 2, 3]과 [3, 4]가 주어지면 [(1,3), (1,4), (2,3), (2,4), (3,3), (3,4)
         * 를 반환해야 한다.
         */
        List<Integer> number1 = Arrays.asList(1, 2, 3);
        List<Integer> number2 = Arrays.asList(3, 4);

        // External Iteration을 이용한 고전적인 풀이법
        List<List<Integer>> result2 = new ArrayList<>();

        for (Integer num : number1) {
            for (Integer num2 : number2) {
                result2.add(Arrays.asList(num, num2));
            }
        }

        // Internal Iteration을 이용한 풀이법
        number1.stream()
                .flatMap(i -> number2.stream()
                        .map(j -> new int[] {i, j})
                )
                .collect(Collectors.toList());

        /**
         * 이전 예제에서 합이 3으로 나누어 떨어지는 쌍만 반환하려면 어떻게 해야할까?
         * 예를 들어, (2,4), (3,3)을 반환해야 한다.
         */
        List<int[]> result3 = number1.stream()
                .flatMap(i -> number2.stream()
                        .filter(j -> (i + j) % 3 == 0)
                        .map(j -> new int[] {i, j})
                )
                .collect(Collectors.toList());
    }
}
