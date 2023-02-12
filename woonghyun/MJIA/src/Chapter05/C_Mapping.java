package Chapter05;

import Chapter04.Dish;
import Chapter04.DishUtil;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 03.
 */
public class C_Mapping {
    public static void main(String[] args) {
        /**
         * 특정 객체에서 특정 데이터를 선택하는 작업은 데이터 처리 과정에서
         * 자주 수행되는 연산이다.
         * Stream API의 map과 flatMap 메서드는 이런 기능을 제공한다.
         */
        List<Dish> menu = DishUtil.getExample();
        List<String> dishNames = menu.stream()
                .map(Dish::getName)
                .collect(toList());
        System.out.println("dishNames : " + dishNames);

        /**
         * 만약 위 예제에서 출력한 요리 이름의 길이를 알고싶다면?
         */
        List<Integer> dishNamesLength = menu.stream()
                .map(Dish::getName)
                .map(String::length)
                .collect(toList());

        /**
         * 5.3.2 Stream 평면화
         * 리스트에서 고유문자로 이루어진 리스트를 반환해보자.
         * 예를 들어 ["Hello", "World"] 리스트가 있다면
         * 결과로 ["H", "e", "l", "o", "W", "r", "d"]가 출력되어야 한다.
         *
         */
        List<String> words = Arrays.asList("Hello", "World");

        List<String[]> result = words.stream()
                .map(word -> word.split(""))
                .distinct()
                .collect(toList());

        for (String word:words) {
            System.out.println("연습 결과!!");
            System.out.println(word);
        }

        /**
         * 출력 결과로 Hello와 World가 각각 출력되는 것을 확인할 수 있다.
         * 첫번째 중간연산인 map()에서 Stream<String[]>을 반환한다.
         * 따라서 Hello, World에 대해서 distinct가 동작하게 되므로, 달라지는게 없다.
         */

    }
}
