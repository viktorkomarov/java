package ru.otus;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class Executor {
    public static ExecutionStat launch (String className)  {
        try {
            List<TestCase> cases = Parser.buildTestCases(className);
            return cases.
                    stream().
                    map(TestCase::run).
                    reduce(ExecutionStat.ZERO, ExecutionStat::apply);
        } catch (Exception e) {
            return ExecutionStat.SINGLE_FAIL;
        }
    }

}

class Parser{
    public static List<TestCase> buildTestCases(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        var methods = clazz.getDeclaredMethods();
        var beforeHooks = pickMethodByAnnotation(methods, Before.class);
        var tests = pickMethodByAnnotation(methods, Test.class);
        var afterHooks = pickMethodByAnnotation(methods, After.class);
        var constructorNoArgs = prepareConstructorNoArgs(clazz);
        if (constructorNoArgs.isEmpty()) {
            throw new IllegalArgumentException("constructor without args is required");
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

    private static Optional<Constructor<?>> prepareConstructorNoArgs(Class<?> clazz) {
        try {
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return Optional.of(constructor);
        } catch(Exception e) {
            return Optional.empty();
        }
    }
}

class TestCase {
    private final List<Method> beforeHooks;
    private final Method test;
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
            return tests.
                    stream().
                    map((test) -> new TestCase(constructor, beforeHooks, test, afterHooks)).
                    toList();
        }
    }
    private TestCase(Constructor<?> constructor, List<Method> beforeHooks, Method test, List<Method> afterHooks){
        this.beforeHooks = beforeHooks;
        this.test = test;
        this.afterHooks = afterHooks;
        this.constructor = constructor;
    }

    public static Builder builder(){
        return new Builder();
    }

    public ExecutionStat run() {
        var obj = constructObject();
        if (obj.isEmpty()) return ExecutionStat.SINGLE_FAIL;

        var testObj = obj.get();
        var stat = safeMethodsExecution(beforeHooks, testObj);
        if (!stat.isFailed()) {
            stat = stat.apply(safeMethodExecution(test, testObj));
        }
        stat = stat.apply( safeMethodsExecution(afterHooks, testObj));

        return stat.isFailed() ? ExecutionStat.SINGLE_FAIL : ExecutionStat.SINGLE_SUCCESS;
    }


    private Optional<Object> constructObject() {
        try {
            Object obj = constructor.newInstance();
            return Optional.of(obj);
        }catch (Exception e) {
            return Optional.empty();
        }
    }
    private ExecutionStat safeMethodsExecution (List<Method> methods, Object obj) {
        return methods.
                stream().
                map(method -> safeMethodExecution(method, obj)).
                reduce(ExecutionStat.ZERO, ExecutionStat::apply);
    }

    private ExecutionStat safeMethodExecution(Method method, Object obj) {
        try {
            method.setAccessible(true);
            method.invoke(obj);
        }catch (Exception e) {
            return ExecutionStat.SINGLE_FAIL;
        }

        return ExecutionStat.SINGLE_SUCCESS;
    }
}
