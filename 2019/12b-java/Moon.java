package com.company;

public class Moon {
    public int[] position = new int[3];
    public int[] velocity = new int[3];

    Moon (int x, int y, int z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    public int getEnergy() {
        int potential = 0;
        int kinetic = 0;
        for (int axis = 0; axis < 3; axis++) {
            potential += Math.abs(position[axis]);
            kinetic += Math.abs(velocity[axis]);
        }
        return potential*kinetic;
    }

    public void applyVelocity() {
        for (int axis = 0; axis < 3; axis++) {
            position[axis] += velocity[axis];
        }
    }
}
