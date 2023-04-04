package ru.otus;

import ru.otus.logger.TestLoggingInterface;
import ru.otus.logger.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IoCTestLoggingInterface {
    private IoCTestLoggingInterface(){};

    public static TestLoggingInterface of(TestLoggingInterface internal) {
        return (TestLoggingInterface) Proxy.newProxyInstance(
                IoCTestLoggingInterface.class.getClassLoader(),
                new Class<?>[]{TestLoggingInterface.class},
                new TestLoggingHandler(
                        getMethodsWithAnnotation(internal.getClass(), Log.class),
                        internal
                )
        );
    }
    private static List<Method> getMethodsWithAnnotation(Class<?> clazz,Class<? extends Annotation> annotation) {
        return  Arrays.stream(clazz.getDeclaredMethods()).
                filter(method -> method.isAnnotationPresent(annotation)).
                toList();
    }

    static class TestLoggingHandler implements InvocationHandler {
        private final List<Method> logMarkedMethods;
        private final TestLoggingInterface inner;
        public TestLoggingHandler(List<Method> logMethods, TestLoggingInterface inner) {
            this.logMarkedMethods = logMethods;
            this.inner = inner;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            var match = logMarkedMethods.stream().filter(
                    storedMethod->areMethodsEqual(storedMethod, method)
            ).findAny();
            if (match.isPresent()) {
                logMessageInvokedMethod(method, args);
            }
            return method.invoke(inner, args);
        }

        private void logMessageInvokedMethod(Method method, Object[] args) {
            System.out.print("Invoked method name " + method.getName() + " ");
            for (int i = 0; i < args.length; i++) {
                System.out.print("Param" + (i + 1) + "-"+args[i]);
                if (i != args.length-1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

        private static boolean areMethodsEqual(Method lhs, Method rhs) {
            return lhs.getName().equals(rhs.getName()) &&
                    Arrays.equals(lhs.getParameterTypes(), rhs.getParameterTypes());
        }

    }
}
