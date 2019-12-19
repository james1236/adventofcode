package com.company;

public class Main {

    private String[] input = {"<x=5, y=4, z=4>",
            "<x=-11, y=-11, z=-3>",
            "<x=0, y=7, z=0>",
            "<x=-13, y=2, z=10>"};

    private Moon[] moons = new Moon[4];
    private int step = 0;

    public static void main(String[] args) {
        Main main = new Main();
        main.decodeInput();

        main.steps(1000);

        System.out.println(main.getEnergy());
    }

    private void step() {
        updateVelocities();
        updatePositions();
    }

    private void stepPrint() {
        step();
        printStatus();
    }

    private void steps(int steps) {
        for (int i = 0; i < steps; i++) {
            step();
        }
    }

    private void stepsPrint(int steps) {
        for (int i = 0; i < steps; i++) {
            step();
            printStatus();
        }
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

    private void printStatus() {
        for (Moon moon: moons) {
            System.out.println("x: " + moon.position[0] + " , y:" + moon.position[1] + " , z:" + moon.position[2] + "   ,   Vx: " + moon.velocity[0] + " , Vy:" + moon.velocity[1] + " , Vz:" + moon.velocity[2]);
        }
        System.out.println();
    }
}
