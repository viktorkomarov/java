package ru.otus;

import com.google.common.math.IntMath;

import java.util.Scanner;

public class HelloOtus {
    public static void main(String... args) {
        System.out.print("Write random number:");
        Scanner in = new Scanner(System.in);
        var x = in.nextInt();
        System.out.println("Is " + x + " power of 2 ? " + IntMath.isPowerOfTwo(x));
    }
}
