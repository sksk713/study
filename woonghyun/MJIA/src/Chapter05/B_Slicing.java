package Chapter05;

import static java.util.stream.Collectors.toList;

import Chapter04.Dish;
import Chapter04.DishUtil;
import java.util.Arrays;
import java.util.List;

/**
 * 02.
 */
public class B_Slicing {
    static List<Dish> specialMenu = Arrays.asList(
            new Dish("seasonal fruit", true, 120, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER)
    );
    static List<Dish> menu = DishUtil.getExample();

    public static void main(String[] args) {
        /**
         * 320 칼로리 미만의 요리 선택하기
         * 예제 1. 앞에서 배운 filter 그대로 활용
         */
        List<Dish> filteredMenu = specialMenu.stream()
                .filter(dish -> dish.getCalories() < 320)
                .collect(toList());

        /**
         * TAKEWHILE 활용 (Java9)
         * 다만 이미 specialMenu는 이미 칼로리순으로 정렬되어 있다.
         * 그럼에도 filter 메서드를 활용하면 스트림의 모든 요소에 대해 반복된다.
         * 이미 정렬되어 있다면, 320 칼로리가 초과하는 Dish가 나왔을 땐 스트림 연산을 중단하면 된다.
         * 이를 TAKEWHILE을 활용해 구현할 수 있다.
         */
        List<Dish> slicedMenu1 = specialMenu.stream()
                .takeWhile(dish -> dish.getCalories() < 320)
                .collect(toList());

        /**
         * DROPWHILE 활용 (Java9)
         * 만약 320칼로리 미만이 아닌 320칼로리 이상의 Dish를 선택하고 싶다면
         * DROPWHILE을 활용하면 된다.
         */
        List<Dish> slicedMenu2 = specialMenu.stream()
                .dropWhile(dish -> dish.getCalories() < 320)
                .collect(toList());


        /**
         * TAKEWHILE, DROPWHILE 활용 조건 : 리스트가 필터링할 값을 기준으로 정렬되어 있어야 한다.
         * 위 예시에선 칼로리를 기준으로 오름차순으로 정렬되어 있어 활용 가능했다.
         */

        /**
         * Stream의 처음 n개 요소를 건너뛰고자 할땐, skip()을 활용한다.
         * n개 이하의 요소를 포함하는 스트림에 skip(n)을 호출하면 빈 스트림이 반환된다.
         */
        List<Dish> dishes = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2) // 처음 두 요리를 건너뛴 다음, 나머지 요리에서 300칼로리가 넘는 요리를 반환
                .collect(toList());
    }

}
