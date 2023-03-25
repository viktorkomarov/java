package ru.otus;


import org.junit.jupiter.api.Test;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HappyPathTestClass {
    public static int beforeCountCall = 0;
    public static int afterCountCall = 0;
    private HappyPathTestClass(){}

    @Before
    public void before(){
        beforeCountCall++;
    }

    @ru.otus.annotations.Test
    public int test() {
        return 0;
    }

    @ru.otus.annotations.Test
    public void test1() {

    }

    @After
    private String after(){
        afterCountCall++;
        return null;
    }
}

class BeforeMethodThrowsExceptions {
    public static int testCountCall = 0;
    public static int afterCountCall = 0;
    @Before
    public void before(){
        throw new RuntimeException();
    }

    @ru.otus.annotations.Test
    public void test() {
        testCountCall++;
    }

    @After
    public void after(){
        afterCountCall++;
    }
}


class TestThrowExceptions {
    public static int beforeCountCall = 0;
    public static int afterCountCall = 0;
    @Before
    public void before(){
        beforeCountCall++;
    }

    @ru.otus.annotations.Test
    public void happyTest() {}

    @ru.otus.annotations.Test
    public void exceptionTest() {throw new RuntimeException();}

    @After
    public void after(){
        afterCountCall++;
    }
}


class ExecutorTest{
    @Test
    public void happyTestClassPathExecutor() {
        var stat = Executor.launch(HappyPathTestClass.class.getName());

        assertThat(2).isEqualTo(stat.getAllCases());
        assertThat(2).isEqualTo(stat.getSuccessfulCases());
        assertThat(0).isEqualTo(stat.getFailedCases());
        assertThat(2).isEqualTo(HappyPathTestClass.beforeCountCall);
        assertThat(2).isEqualTo(HappyPathTestClass.afterCountCall);
    }

    @Test
    public void beforeThrowException() {
        var stat = Executor.launch(BeforeMethodThrowsExceptions.class.getName());

        assertThat(1).isEqualTo(stat.getAllCases());
        assertThat(0).isEqualTo(stat.getSuccessfulCases());
        assertThat(1).isEqualTo(stat.getFailedCases());
        assertThat(1).isEqualTo(BeforeMethodThrowsExceptions.afterCountCall);
        assertThat(0).isEqualTo(BeforeMethodThrowsExceptions.testCountCall);
    }

    @Test
    public void testThrowException() {
        var stat = Executor.launch(TestThrowExceptions.class.getName());

        assertThat(2).isEqualTo(stat.getAllCases());
        assertThat(1).isEqualTo(stat.getSuccessfulCases());
        assertThat(1).isEqualTo(stat.getFailedCases());
        assertThat(2).isEqualTo(TestThrowExceptions.afterCountCall);
        assertThat(2).isEqualTo(TestThrowExceptions.beforeCountCall);
    }

}