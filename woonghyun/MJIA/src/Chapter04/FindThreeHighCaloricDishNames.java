package Chapter04;

import java.util.List;
import java.util.stream.Collectors;

public class FindThreeHighCaloricDishNames {
    public static void main(String[] args) {
        List<Dish> menu = DishUtil.getExample();

        List<String> threeHighCaloricDishNames =
                menu.stream()
                        .filter(dish -> dish.getCalories() > 300)
                        .map(Dish::getName)
                        .limit(3)
                        .collect(Collectors.toList());

        System.out.println(threeHighCaloricDishNames);
    }
}
