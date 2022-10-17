package com.gyeom.infrastructure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OuterClass {
    private final int a;

    public OuterClass(int a) {
//        HashSet<Object> set1 = new HashSet<>();
//        set1 = new HashSet<Integer>();
//
//        HashSet<?> set2 = new HashSet<>();
//        set2 = new HashSet<Integer>();

        this.a = a;
    }


//    public static OuterClass valueOf(int a) {
//        return new OuterClass(a);
//    }


//    static class InnerClass {
//
//    }

    public <T> T[] toArray(T[] a) {
        Object[] elements = new Object[2];
        @SuppressWarnings("unchecked") T[] result = (T[]) Arrays.copyOf(elements, elements.length, a.getClass());

        return result;
    }

    public void variableArgs(@Nullable String a, @NotNull String args) {
        Stream<String> fruits = Stream.of("banana", "apple", "mango", "kiwi", "peach", "cherry", "lemon");
//        Map<String, String> fruitHashSet = fruits.collect(Collectors.toMap(e -> e, e -> e));
        Map groupMap = fruits.collect(Collectors.groupingBy(s -> s.substring(1, s.length() - 1)));
        List<String> list = Phase.Transition.VALS;
        list.get(0);
        Instant instant = Instant.now();


        HashMap<String, Integer> map = new HashMap<>();
        map.merge("A", 1, (v1, v2) -> v1 + v2);

        BooleanSupplier booleanSupplier = new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        };

        Supplier supplier = new Supplier() {
            @Override
            public Object get() {
                return null;
            }
        };
    }

}

enum Phase {
    SOLID, LIQUID, GAS;

    enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID), BOIL(LIQUID, GAS);

        private final Phase from;
        private final Phase to;

        private static final String[] PRIVATE_VALS = {"1", "2"};
        public static final List<String> VALS = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALS));

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        private static final Map<Phase, Map<Phase, Transition>> m =
                Stream
                        .of(Transition.values())
                        .collect(Collectors
                                .groupingBy(
                                        t -> t.from,
                                        () -> new EnumMap<>(Phase.class),
                                        Collectors.toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase.class))
                                )
                        );
    }
}






