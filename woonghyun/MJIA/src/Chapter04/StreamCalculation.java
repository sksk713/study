package Chapter04;

import static java.util.stream.Collectors.toList;

import java.util.List;

public class StreamCalculation {
    public static void main(String[] args) {
        /**
         * 스트림 연산은 크게 2가지로 구분할 수 있다.
         * 중간연산과 최종연산.
         * 중간연산 : 다른 스트림 연산과 연결할 수 있는 연산. ex) filter, map, limit
         * 최종연산 : 다른 스트림 연산관 연결할 수 없는 연산. ex) collect
         * collect()의 경우 collect().filter와 같은 연산이 불가능하다.
          */

        List<Dish> menu = DishUtil.getExample();

        // 람다 표현식으로 작성하지 않는 경우 아래와 같이 return을 반드시 넣어야 한다.
        // 이런 코드는 학습용으로는 좋다.
        List<String> names = menu.stream()
                .filter(dish -> {
                    System.out.println("filtering : " + dish.getName());
                    return dish.getCalories() > 300;
                })
                .map(dish -> {
                    System.out.println("mapping : " + dish.getName());
                    return dish.getName();
                })
                .limit(3)
                .collect(toList());
        System.out.println(names);
    }

    /**
     * Stream 중간 연산의 특징 중 하나는 바로 게으른 연산이다.
     * 이점 1. 쇼트서킷 (5장에서 설명)
     * 이점 2. 루프 퓨전 (filter와 map은 서로 다른 연산이지만 한 과정으로 병합)
     */
}
