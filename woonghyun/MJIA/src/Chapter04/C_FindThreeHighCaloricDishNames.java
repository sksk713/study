package Chapter04;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 03.
 */
public class C_FindThreeHighCaloricDishNames {
    public static void main(String[] args) {
        List<Dish> menu = DishUtil.getExample();

        /**
         * 데이터 소스는 요리메뉴 (menu)이다. menu는 연속된 요소를 stream에 전달한다.
         * 다음으로 filter, map, limit, collect로 이어지는 일련의 데이터 처리 연산을 적용한다.
         * collect 제외한 모든 연산은 서로 파이프라인을 형성할 수 있도록 stream을 반환한다.
         * 마지막으로 collect 연산으로 파이프라인을 처리해서 결과를 반환한다.
         */
        // 데이터 소스는 요리 메뉴 (menu)이다.

        List<String> threeHighCaloricDishNames =
                menu.stream()
                        .filter(dish -> dish.getCalories() > 300)
                        .map(Dish::getName)
                        .limit(3)
                        .collect(Collectors.toList());

        System.out.println(threeHighCaloricDishNames);
    }

    /**
     * Collection vs Stream
     * Example 01. DVD에 저장되어 있는 영화의 경우
     * DVD에는 영화 데이터가 일정한 자료구조로 저장되어 있다. 따라서 DVD도 컬렉션이다.
     * Example 02. 인터넷 스트리밍으로 영화를 시청하는 경우
     * 스트리밍으로 영화를 재생할 때는 사용자가 시청할 부분의 몇 프레임을 미리 받아놓는다.
     * 그러면 영화의 다른 프레임을 미리 처리하지 않더라도 사용자는 내려받은 프레임부터 영화를 재생할 수 있다.
     * 결국 컬렉션과 스트림의 가장 큰 차이는 "데이터를 언제 처리하느냐"이다.
     *
     *
     */
}
