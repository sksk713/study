package Chapter05;

import Chapter04.Dish;
import Chapter04.DishUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 01.
 */
public class A_Filtering {
    public static void main(String[] args) {
        /**
         * Stream API의 filter()는 Predicate를 인수로 받아
         * Predicate와 일치하는 모든 요소를 포함하는 스트림을 반환한다.
         */

        List<Dish> menu = DishUtil.getExample();
        List<Dish> vegetarianMenu = menu.stream()
                .filter(Dish::isVegetarian)
                .collect(Collectors.toList());

        /**
         * Stream은 고유 요소로 이루어진 스트림을 반환하는 distinct 메서드도 지원한다.
         */
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0) // 짝수만 필터링
                .distinct() // 중복 제거
                .forEach(System.out::println); // 각 요소 출력
    }
}
