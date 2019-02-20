package com.paramgy.braintrain;

import java.util.ArrayList;
import java.util.Random;

public class MyRandomNumber extends Random {

    /*
    It is a random number generator class to help generate unique random numbers
    each instance of this class generates a random number only once and never repeated.

    Created by: @author : Mohamed Salama a.k.a The Enigma
    Date: 1/10/2018

     */

    ArrayList<Integer> generatedIntegerNumbers = new ArrayList<>();

    // those are not used yet but the concept is the same.
    ArrayList<Double> generatedDoubleNumbers = new ArrayList<>();
    ArrayList<Long> generatedLongNumbers = new ArrayList<>();

    public int nextUniqueInt(int bound, int excluded) {
        int uniqueRandom = super.nextInt(bound);
        while (uniqueRandom == excluded || generatedIntegerNumbers.indexOf(uniqueRandom) != -1) {
            uniqueRandom = super.nextInt(bound);
        }

        generatedIntegerNumbers.add(uniqueRandom);
        return uniqueRandom;
    }

    public int nextUniqueInt(int bound, int... excluded) {
        int uniqueRandom = super.nextInt(bound);
        int i = 0;
        while ((uniqueRandom == excluded[i] || generatedIntegerNumbers.indexOf(uniqueRandom) != -1) && i < excluded.length) {
            uniqueRandom = super.nextInt(bound);
            i++;
        }
        generatedIntegerNumbers.add(uniqueRandom);
        return uniqueRandom;
    }



}
