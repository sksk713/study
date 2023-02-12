package Chapter05;

import Chapter04.Dish;
import Chapter04.DishUtil;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 01.
 */
public class Filtering {
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
    }
}
