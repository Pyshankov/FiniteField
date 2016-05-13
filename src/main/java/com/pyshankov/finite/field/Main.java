package com.pyshankov.finite.field;


public class Main {

    public static void main(String[] args) {


//        int[] module5 = {1,0,0,1,0,1};
//        int[] module5 = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1};
        int[] module5 = {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1};
//        int[] module5 = {1,0,0,0,0,0,0,1,0,0,1};
        Polynom p5 = new Polynom(module5);

        FiniteField gF2pow5 = new FiniteField(p5);
        TableOfRezults rezults = new TableOfRezults(
                gF2pow5,
                p -> Polynom.powerOf(p, 513, gF2pow5.getModule()),
                p -> Polynom.powerOf(p, 512, gF2pow5.getModule())
        );


    }


}
