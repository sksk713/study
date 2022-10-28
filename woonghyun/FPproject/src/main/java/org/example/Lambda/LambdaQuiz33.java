package org.example.Lambda;

import com.sun.corba.se.spi.orb.Operation;

import java.util.function.Supplier;

/*
    다음 예제 중 유효한 람다식은?
    람다 표현식만 작성하면 Not a statement 에러가 발생..
 */
public class LambdaQuiz33 {
    public static void main(String[] args) {
        // 1.
        Runnable r = () -> {};
        // 2.
        Supplier supplier = () -> "Raoul";
        // 3.
        Operation operation = (Object i) -> return "Alan" + i;
        // 3-1. 올바르게 작성하려면 다음과 같이 작성한다.
        Operation operation1 = (Object i) -> { return "Alan" + i; };
        // 4.
        Operation operation2 = (Object s) -> { "Iron Man" };
        // 4-1. 올바르게 작성하려면 다음과 같이 작성한다.
        Operation operation3 = (Object s) -> { return "Iron Man"; };
    }


}
