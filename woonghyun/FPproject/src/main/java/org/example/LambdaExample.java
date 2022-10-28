package org.example;

public class LambdaExample {
    // 1. 익명 클래스 활용하기
    Runnable r = new Runnable() {
        @Override
        public void run() {
            System.out.println("Hello!");
        }
    };

    // 2. 람다 표현식 활용하기
    Runnable r1 = () -> System.out.println("Hello!");
}
