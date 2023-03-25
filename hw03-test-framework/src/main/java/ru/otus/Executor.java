package ru.otus;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public class Executor {

    private final List<TestCase> cases;
    public static Executor from(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        List<TestCase> cases = Parser.buildTestCases(clazz);
        return new Executor(cases);
    }

    private Executor(List<TestCase> cases) {
        this.cases = cases;
    }

    public ExecutionStat launch() {
        return cases.
                stream().
                map(TestCase::safeRun).
                reduce(ExecutionStat.ZERO, ExecutionStat::apply);
    }
}

class Parser{
    public static List<TestCase> buildTestCases(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        var methods = clazz.getDeclaredMethods();
        var beforeHooks = pickMethodByAnnotation(methods, Before.class);
        var tests = pickMethodByAnnotation(methods, Test.class);
        var afterHooks = pickMethodByAnnotation(methods, After.class);
        var constructorNoArgs = findConstructorNoArgs(clazz.getConstructors());
        if (constructorNoArgs.isEmpty()) {
            //
        }

        return TestCase.builder().
                WithTests(tests).
                WithAfterHooks(afterHooks).
                WithBeforeHooks(beforeHooks).
                WithClassName(constructorNoArgs.get()).
                build();
    }

    private static List<Method> pickMethodByAnnotation(Method[] methods, Class<? extends Annotation> annotation) {
        return Arrays.
                stream(methods).
                filter(method -> method.isAnnotationPresent(annotation)).
                toList();
    }

    private static Optional<Constructor<?>> findConstructorNoArgs(Constructor<?>[] constructors) {
       return Arrays.
               stream(constructors).
               filter(c -> c.getParameterCount() == 0).
               findAny();
    }
}

class TestCase {
    private final List<Method> beforeHooks;
    private final Optional<Method> test;
    private final List<Method> afterHooks;
    private final Constructor<?> constructor;

    public static class Builder{
        private List<Method> beforeHooks;
        private List<Method> tests;
        private List<Method> afterHooks;
        private Constructor<?> constructor;

        public Builder WithBeforeHooks(List<Method> beforeHooks) {
            this.beforeHooks = beforeHooks;
            return this;
        }

        public Builder WithAfterHooks(List<Method> afterHooks) {
            this.afterHooks = afterHooks;
            return this;
        }

        public Builder WithTests(List<Method> tests) {
            this.tests = tests;
            return this;
        }

        public Builder WithClassName(Constructor<?> constructor) {
            this.constructor = constructor;
            return this;
        }

        public List<TestCase> build() {
            if (tests.isEmpty()) {
                if (beforeHooks.isEmpty() && afterHooks.isEmpty()) {
                    return Collections.emptyList();
                }
                return List.of(new TestCase(constructor, beforeHooks, Optional.empty(),afterHooks));
            }

            return tests.
                    stream().
                    map((test) -> new TestCase(constructor, beforeHooks, Optional.of(test), afterHooks)).
                    toList();
        }
    }
    private TestCase(Constructor<?> constructor, List<Method> beforeHooks, Optional<Method> test, List<Method> afterHooks){
        this.beforeHooks = beforeHooks;
        this.test = test;
        this.afterHooks = afterHooks;
        this.constructor = constructor;
    }

    public static Builder builder(){
        return new Builder();
    }

    public ExecutionStat safeRun() {
        try {
            Object obj = constructor.newInstance();
            beforeHooks.forEach(
                    method -> {
                        Ob
                    }
            );


        } catch (Exception e) {
            return ExecutionStat.SINGLE_FAIL;
        }
        return ExecutionStat.SINGLE_SUCCESS;
    }
}

