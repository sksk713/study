package Chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public class D_FlatMap {
    public static void main(String[] args) {

        /**
         * 다음 코드는 문자열을 받아 스트림을 만든다.
         */
        String[] arrayOfWords = {"Goodbye", "World"};
        Stream<String> streamOfWords = Arrays.stream(arrayOfWords);

        // streamOfWords.collect()를 수행하게 되면 List<String>이 된다.
        // [Goodbye, World]

        /**
         * flatMap 활용하기
         * flatMap()을 활용하면 우리가 원하는 결과를 얻을 수 있다.
         */
        List<String> words = Arrays.asList("Hello", "World");
        List<String> result = words.stream()
                .map(word -> word.split("")) // Stream<String[]>
                .flatMap(Arrays::stream)
                .distinct()
                .collect(toList());

        System.out.println("flatMap 결과 : " + result);

        /**
         * flatMap은 각 배열을 스트림이 아니라 스트림의 컨텐츠로 매핑한다.
         * 즉, map(Arrays::stream)과 달리 flatMap은 하나의 평면화된 스트림을 반환한다.
         * 요약하면 flatMap 메서드는 모든 개별 스트림을 하나의 스트림으로 연결하는 기능을 수행한다.
         */


    }
}
