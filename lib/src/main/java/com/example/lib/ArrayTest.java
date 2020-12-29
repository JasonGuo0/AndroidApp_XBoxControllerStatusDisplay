package com.example.lib;

public class ArrayTest {
    public static boolean someValuesBig(float[] arr) {
        for (float var: arr)
            if (var > 1.2f) return true;
        return false;
    }

    public static void main(String args[]) {
        float[] arr1 = {1.0f, 1.1f, 1.2f, 1.3f};
        float[] arr2 = {11.0f, 12.1f, 13.2f, 14.3f};
        float[] arr3 = {11.0f, 12.1f, 13.2f, 14.3f, 15.2f};
        for (float var: arr1)
            System.out.println(var);
        arr1 = arr2;
        for (float var: arr1)
            System.out.println(var);
        System.out.println(someValuesBig(arr1));
        System.out.println(someValuesBig(arr3));
    }
}
