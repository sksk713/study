package Chapter04;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 05.
 */
public class IternalAndExternalIteration {
    public static void main(String[] args) {
        /**
         * External Iteration.
         * 사용자가 직접 요소를 반복하는 로직을 작성해야 한다.
         */
        List<Dish> menu = DishUtil.getExample();
        List<String> names = new ArrayList<>();
        for (Dish dish : menu) {
            names.add(dish.getName());
        }

        /**
         * Internal Iteration.
         * 사용자는 반복 로직을 직접 작성할 필요가 없다.
         * 어딘가에서 알아서 계산해준다.
         */
        names.clear();
        Iterator<Dish> iterator = menu.iterator();
        // for문을 작성할 필요가 없다.
        while (iterator.hasNext()) {
            Dish dish = iterator.next();
            names.add(dish.getName());
        }

        /**
         * Internal Iteration With Stream
         * 내부 반복을 사용하면 병렬로 수행할 수도 있다.
         * for-each를 이용하면 병렬성을 개발자가 직접 정의해야 한다.
         */
        names.clear();
        names = menu.stream()
                .map(Dish::getName)
                .collect(toList());

        /**
         * Internal Iteration With Stream parallelStream
         */
        names.clear();
        names = menu.parallelStream()
                .map(Dish::getName)
                .collect(toList());


    }
}
