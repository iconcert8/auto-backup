package com.gyeom.infrastructure;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(FunctionalTest.class);


    @Test
    void executeOrderTest() {
        int[] array = {1, 2, 3, 4};
        int total = Arrays.stream(array)
                .peek(i -> log.info(Integer.toString(i)))
                .filter(i -> i < 5)
                .reduce((i1, i2) -> {
                    log.info(String.format("sum: %d, i2: %d", i1, i2));
                    return i1 + i2;
                }).orElseGet(() -> 0);

        System.out.println(total);
    }

    @Test
    void groupByTest() {
        Stream<String> fruits = Stream.of("banana", "apple", "mango", "kiwi", "peach", "cherry", "lemon", "anaconda", "apple");
//        Map<String, String> fruitHashSet = fruits.collect(Collectors.toMap(e -> e, e -> e));
        fruits
                .distinct()
                .collect(Collectors.groupingBy(s -> s.substring(0, 1)))
                .forEach((key, val) -> log.info(String.format("k: %s, v: %s", key, val)));


    }


}
