package Chapter04;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 04.
 */
public class D_ConsumedStream {
    public static void main(String[] args) {
        List<String> title = Arrays.asList("Java8", "In", "Action");
        Stream<String> s = title.stream();
        s.forEach(System.out::println); // 타이틀의 각 단어를 출력한다.
        s.forEach(System.out::println);

        /**
         * 2번째 println() 메서드를 수행하는 경우 다음과 같은 에러가 발생한다.
         * -> stream has already been operated upon or closed
         * 즉 Stream 요소는 단 한번만 소비할 수 있다.
         */
    }
}
