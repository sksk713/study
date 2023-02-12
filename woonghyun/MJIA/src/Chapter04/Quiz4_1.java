package Chapter04;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Quiz4_1 {
    /**
     * 다음 코드를 Stream을 이용해 리팩토링해보세요.
     */
    public static void main(String[] args) {
        List<Dish> menu = DishUtil.getExample();
        List<String> highCaloricDishes = new ArrayList<>();
        Iterator<Dish> iterator = menu.iterator();

        while (iterator.hasNext()) {
            Dish dish = iterator.next();
            if (dish.getCalories() > 300) {
                highCaloricDishes.add(dish.getName());
            }
        }

        System.out.println(highCaloricDishes);
        // Solution of Quiz
        System.out.println("Refactoring result:");
        refactoring();
    }

    public static void refactoring() {
        List<Dish> menu = DishUtil.getExample();
        List<String> highCaloricDishes = menu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .map(Dish::getName)
                .collect(Collectors.toList());

        System.out.println(highCaloricDishes);
    }
}
