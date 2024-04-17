package com.pletnev;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String expr=new String("2+2*2*x*y*x*pow(2,2)");
        System.out.println(new MathParser().calculation(expr));
    }
}
