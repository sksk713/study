package Chapter04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 01.
 */
public class A_FindLowCalories {

    public static void main(String[] args) {

        List<Dish> menu = DishUtil.getExample();

        List<Dish> lowCaloricDishesName = new ArrayList<>();
        for (Dish dish : menu) {
            if (dish.getCalories() < 400) {
                lowCaloricDishesName.add(dish);
            }
        }

        Collections.sort(lowCaloricDishesName, new Comparator<Dish>() {
            @Override
            public int compare(Dish o1, Dish o2) {
                return Integer.compare(o1.getCalories(), o2.getCalories());
            }
        });

        System.out.println(lowCaloricDishesName);

    }
}
