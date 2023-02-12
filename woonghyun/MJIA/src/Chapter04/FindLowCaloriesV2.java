package Chapter04;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 02.
 */
public class FindLowCaloriesV2 {
    public static void main(String[] args) {
        List<Dish> menu = DishUtil.getExample();

        List<String> lowCaloricDishesName =
        // 병렬 처리하려면 parallelStream을 활용한다.
//                menu.parallelStream()
                menu.stream()
                .filter(d -> d.getCalories() < 400)
                .sorted(Comparator.comparing(Dish::getName))
                .map(Dish::getName)
                .collect(toList());

        System.out.println(lowCaloricDishesName);
    }
}
