package com.company;

import java.util.ArrayList;

public class Main {
    private String[] input = {"<x=5, y=4, z=4>",
            "<x=-11, y=-11, z=-3>",
            "<x=0, y=7, z=0>",
            "<x=-13, y=2, z=10>"};

    private Moon[] moons = new Moon[4];

    private String[] initialHashes = new String[3];
    private long[] axisCycleLengths = new long[3];

    /*
        Brute forcing universe loop point even with optimized hash method takes too int,

        If it ever reaches the same state as before it must loop forever - a cycle
        Axes act independently but moons are related, can calculate the length of each axis cycle as 3 independent probabilities
        Can calculate axes independently to reduce computation time by the cube root (i.e 1,000,000,000 secs -> 1,000 secs)
        Find the length of each cycle and find the LCM between them to get first conjoined loop point
    */

    public static void main(String[] args) {
        Main main = new Main();
        main.decodeInput();

        System.out.println("This may take a few seconds...");
        System.out.println();
        main.findAxesCycleLengths();
        System.out.println("Finding LCM of axis cycle lengths (universe cycle length)...");
        System.out.println();
        System.out.println(getLCM(main.axisCycleLengths));
    }

    private void step() {
        updateVelocities();
        updatePositions();
    }

    private void stepPrint() {
        step();
        printStatus(moons);
    }

    private void steps(int steps) {
        for (int i = 0; i < steps; i++) {
            step();
        }
    }

    private void stepsPrint(int steps) {
        for (int i = 0; i < steps; i++) {
            stepPrint();
        }
    }

    private static long getLCM(long... numbers) {
        /*
            Calculating LCM by getting the prime factor list for each number,
            converting that list into a list of unique prime factors across all numbers,
            finding the maximum number of times each prime factor is found in any number
            and adding the unique prime factor into the output list that many times,
            and finally multiplying all the elements of the output list together
        */

        //Generating a list of prime factor lists
        ArrayList<ArrayList<Long>> primeFactorLists = new ArrayList<>();
        for (long number : numbers) {
            primeFactorLists.add(primeFactors(number));
        }

        ArrayList<Long> unique = getUnique(primeFactorLists);
        ArrayList<Integer> mostOccurrences = new ArrayList<>();

        //Creating mostOccurrences
        for (Long uniqueElm : unique) {
            int most = 0;
            for (ArrayList<Long> primeFactorList : primeFactorLists) {
                int frequency = getFrequency(primeFactorList, uniqueElm);
                if (frequency > most) {
                    most = frequency;
                }
            }
            mostOccurrences.add(most);
        }

        //Build factorization list
        ArrayList<Long> factorizationList = new ArrayList<>();
        for (int i = 0; i < unique.size(); i++) {
            //Repeat by the highest number of occurrences of unique prime factor in any prime factor list
            for (int r = 0; r < mostOccurrences.get(i); r++) {
                factorizationList.add(unique.get(i));
            }
        }

        //Get final LCM by multiplying all elements of factorization list
        long total = 1;
        for (Long elm: factorizationList) {
            total *= elm;
        }
        return total;
    }

    private static int getFrequency(ArrayList<Long> arrayList, long num) {
        //Returns the number of times num occurs in arrayList
        int total = 0;
        for (long elm: arrayList) {
            if (elm == num) {
                total++;
            }
        }
        return total;
    }

    private static ArrayList<Long> getUnique(ArrayList<ArrayList<Long>> lists) {
        //Given a list of lists, return a list of the unique elements
        ArrayList<Long> unique = new ArrayList<>();
        for (ArrayList<Long> list: lists) {
            for (long element: list) {
                if (unique.indexOf(element) == -1) {
                    unique.add(element);
                }
            }
        }
        return unique;
    }

    private static ArrayList<Long> primeFactors(long num) {
        //Generate a list of prime factors for num
        ArrayList<Long> primeFactors = new ArrayList<>();

        long factor;
        for (factor = 2; factor < num; factor++) {
            //Only check factors if they're prime
            if (!isPrime(factor)) {
                continue;
            }

            //Check if it's a factor
            if (num % factor == 0) {
                primeFactors.add(factor);
                //Recursion to get the prime factors of (num / factor)
                primeFactors.addAll(primeFactors(num / factor));
                return primeFactors;
            }
        }

        primeFactors.add(num);
        return primeFactors;
    }

    private static boolean isPrime(long num) {
        //Nothing below 2 is prime
        if (num < 2) {
            return false;
        }

        //Check all possible factors from 0 to num/2
        long factor = 2;
        while (factor <= num / 2) {
            if (num % factor == 0) {
                return false;
            }
            factor++;
        }
        return true;
    }

    private void findAxesCycleLengths() {
        //Set initial hashes
        for (int axis = 0; axis < 3; axis++) {
            initialHashes[axis] = getAxisHash(axis);
        }

        //Find all axes cycle lengths at once (stop incrementing an axis' cycle length after found)
        boolean[] solvedAxes = {false,false,false};
        while (!allTrue(solvedAxes)) {
            step();
            for (int axis = 0; axis < 3; axis++) {
                if (!solvedAxes[axis]) {
                    axisCycleLengths[axis]++;
                    if (getAxisHash(axis).equals(initialHashes[axis])) {
                        solvedAxes[axis] = true;
                        System.out.println("Axis "+axis+" cycle length = "+axisCycleLengths[axis]);
                    }
                }
            }
        }
    }

    public static boolean allTrue(boolean... array)
    {
        for(boolean bool : array) {
            if(!bool) {
                return false;
            }
        }
        return true;
    }

    private String getAxisHash(int axis) {
        StringBuilder hash = new StringBuilder();
        for (Moon moon : moons) {
            hash.append(moon.position[axis]);
            hash.append(moon.velocity[axis]);
        }
        return hash.toString();
    }

    private void decodeInput() {
        for (int i = 0; i < 4; i++) {
            String line = input[i];
            line = line.replace("<", "");
            line = line.replace(">", "");
            line = line.replace("=", "");
            line = line.replace("x", "");
            line = line.replace("y", "");
            line = line.replace("z", "");
            line = line.replace(" ", "");

            String[] positions = line.split(",");
            int[] position = new int[3];
            for (int axis = 0; axis < 3; axis++) {
                position[axis] = Integer.parseInt(positions[axis]);
            }
            moons[i] = new Moon(position[0],position[1],position[2]);
        }
    }

    private void updateVelocities() {
        for (int source = 0; source < moons.length; source++) {
            for (int target = source; target < moons.length; target++) {
                for (int axis = 0; axis < 3; axis++) {
                    if (moons[source].position[axis] > moons[target].position[axis]) {
                        moons[source].velocity[axis] -= 1;
                        moons[target].velocity[axis] += 1;
                    } else {
                        if (moons[source].position[axis] < moons[target].position[axis]) {
                            moons[source].velocity[axis] += 1;
                            moons[target].velocity[axis] -= 1;
                        }
                    }
                }
            }
        }
    }

    private void updatePositions() {
        for (Moon moon: moons) {
            moon.applyVelocity();
        }
    }

    private int getEnergy() {
        int total = 0;
        for (Moon moon: moons) {
            total += moon.getEnergy();
        }
        return total;
    }

    private void printStatus(Moon[] moonArray) {
        for (Moon moon: moonArray) {
            System.out.println("x: " + moon.position[0] + " , y:" + moon.position[1] + " , z:" + moon.position[2] + "   ,   Vx: " + moon.velocity[0] + " , Vy:" + moon.velocity[1] + " , Vz:" + moon.velocity[2]);
        }
        System.out.println();
    }
}
