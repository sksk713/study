package Chapter05;

import Chapter04.Dish;
import Chapter04.DishUtil;
import java.util.List;
import java.util.Optional;

public class E_SearchingAndMatching {
    public static void main(String[] args) {
        /**
         * 프레디케이트가 적어도 한 요소와 일치하는지 확인
         * 프레디케이트가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인할 때, anyMatch() 메서드를 이용한다.
         * 예를 들어 다음 코드는 menu에 채식 요리가 있는지 확인하는 예제이다.
         */
        List<Dish> menu = DishUtil.getExample();

        if (menu.stream().anyMatch(Dish::isVegetarian)) {
            System.out.println("The menu is (somewhat) vegetarian friendly!");
        }

        /**
         * 프레디케이트가 모든 요소와 일치하는지 검사
         * allMatch 메서드는 anyMatch와 달리 스트림의 모든 요소가 주어진 프레디케이트와 일치하는지 검사한다.
         * 예를 들어, 모든 메뉴가 건강식(100칼로리 이하)인지 확인할 수 있다.
         */
        if (menu.stream().allMatch(dish -> dish.getCalories() < 100)) {
            System.out.println("The menu is good for health!");
        }

        // noneMatch는 이와 반대인 경우를 연산한다. 즉 주어진 프레디케이트에 대해 모든 요소가 해당되지 않는지를 검사한다.
        boolean isHealthy = menu.stream()
                .noneMatch(d -> d.getCalories() >= 1000);

        /**
         * 쇼트서킷 평가
         * 때로는 전체 스트림을 처리하지 않았더라도 결과를 반환할 수 있다. 예를 들어, ||나 && 같은 연산자를 예로 들 수 있다.
         */

        /**
         * finaAny()는 현재 스트림에서 임의의 한 요소를 반환한다.
         */
        Optional<Dish> dish = menu.stream()
                .filter(Dish::isVegetarian)
                .findAny();
    }
}
