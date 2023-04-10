package ru.otus;

import ru.otus.logger.TestLoggingInterface;
import ru.otus.logger.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

public class IoCLog {
    private IoCLog(){};

    public static <T>T of(T internal, Class<T> clazz) {
        return clazz.cast(Proxy.newProxyInstance(
                IoCLog.class.getClassLoader(),
                new Class<?>[]{clazz},
                new LoggingHandler<T>(
                        getMethodsWithAnnotation(internal.getClass(), Log.class),
                        internal
                )));

    }
    private static List<Method> getMethodsWithAnnotation(Class<?> clazz,Class<? extends Annotation> annotation) {
        return  Arrays.stream(clazz.getDeclaredMethods()).
                filter(method -> method.isAnnotationPresent(annotation)).
                toList();
    }

    static class LoggingHandler<T> implements InvocationHandler {
        private final List<Method> logMarkedMethods;
        private final T inner;
        public LoggingHandler(List<Method> logMethods, T inner) {
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
