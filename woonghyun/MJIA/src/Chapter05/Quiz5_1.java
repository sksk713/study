package Chapter05;

import static java.util.stream.Collectors.toList;

import Chapter04.Dish;
import Chapter04.DishUtil;
import java.util.List;
import java.util.stream.Collectors;

public class Quiz5_1 {
    public static void main(String[] args) {
        /**
         * 스트림을 이용해서 처음 등장하는 두 고기 요리를 필터링하시오.
         */
        List<Dish> menu = DishUtil.getExample();
        List<Dish> meatDishes = menu.stream()
                .filter(d -> d.getType() == Dish.Type.FISH)
                .limit(2)
                .collect(toList());
    }
}
