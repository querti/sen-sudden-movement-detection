package com.example.myfirstapp;

import android.util.Log;

/**
 * Created by John on 21-Oct-17.
 */

public class PreviousValueSaver {

    private static final int LARGE_NUMBER_SET = 1500;
    private static final int LARGE_NUMBER_COMPARE = 1000;
    public static final int COORD_X = 2000;
    public static final int COORD_Y = 2001;
    public static final int COORD_Z = 2002;

    private double[][] previousValues = new double[3][50]; //x, y, z
    private double[] previousAccelerations = new double[50];
    private double[] previousAccelerationsForce = new double[250];
    private int spinIndex=0;
    private int spinIndexForce=0;

    public PreviousValueSaver() {

        for (int i = 0; i<50; i++) {
            previousValues[0][i]=LARGE_NUMBER_SET;
            previousValues[1][i]=LARGE_NUMBER_SET;
            previousValues[2][i]=LARGE_NUMBER_SET;
            previousAccelerations[i]=LARGE_NUMBER_SET;
        }
        for (int i = 0; i<250; i++) {

            previousAccelerationsForce[i]=LARGE_NUMBER_SET;
        }

    }

    public void saveNewValues(double x, double y, double z, boolean absolute) {

        if (absolute == false) {
            previousValues[0][spinIndex] = x;
            previousValues[1][spinIndex] = y;
            previousValues[2][spinIndex] = z;

        } else {
            previousValues[0][spinIndex] = Math.abs(x);
            previousValues[1][spinIndex] = Math.abs(y);
            previousValues[2][spinIndex] = Math.abs(z);
        }

        double totalAcceleration = Math.sqrt(x*x + y*y + z*z);
        previousAccelerations[spinIndex] = totalAcceleration;
        previousAccelerationsForce[spinIndexForce] = totalAcceleration;

        spinIndex = (spinIndex + 1) % 50; //iterate over indexes
        spinIndexForce = (spinIndexForce + 1) % 250; //iterate over indexes
    }

    public double getHighestDelta(int axis) {

        double minimumValue = 10000;
        double maximumValue = -10000;
        int axisArray = 3;

        if (axis == COORD_X) {
            axisArray = 0;
        } else if (axis == COORD_Y) {
            axisArray = 1;
        } else if (axis == COORD_Z) {
            axisArray = 2;
        }

        for (int i = 0; i<50; i++) {
            if (previousValues[axisArray][i]>maximumValue && previousValues[axisArray][i]<LARGE_NUMBER_COMPARE) {
                maximumValue = previousValues[axisArray][i];
            }
            if (previousValues[axisArray][i]<minimumValue && previousValues[axisArray][i]<LARGE_NUMBER_COMPARE) {
                minimumValue = previousValues[axisArray][i];
            }
        }

        return Math.abs(maximumValue-minimumValue);
    }

    public double getTotalDelta() {

        double deltaX = getHighestDelta(COORD_X);
        double deltaY = getHighestDelta(COORD_Y);
        double deltaZ = getHighestDelta(COORD_Z);

        return deltaX + deltaY + deltaZ;
    }

    public double getAccelerationDelta() {

        double minimumValue = 10000;
        double maximumValue = -10000;
        int maxIndex = 0, minIndex = 0;

        for (int i = 0; i<50; i++) {
            if (previousAccelerations[i]>maximumValue && previousAccelerations[i]<LARGE_NUMBER_COMPARE) {
                maximumValue = previousAccelerations[i];
                maxIndex = i;
            }

            if (previousAccelerations[i]<minimumValue && previousAccelerations[i]<LARGE_NUMBER_COMPARE) {
                minimumValue = previousAccelerations[i];
                minIndex = i;
            }
        }
        double kek = Math.abs(maximumValue-minimumValue);
        String value = Double.toString(kek);
        //Log.d("STABILITY","delta: " + value);

        return Math.abs(maximumValue-minimumValue);
    }

    public double getAccelerationDeltaForce() {

        double minimumValue = 10000;
        double maximumValue = -10000;
        int maxIndex = 0, minIndex = 0;

        for (int i = 0; i<250; i++) {
            if (previousAccelerationsForce[i]>maximumValue && previousAccelerationsForce[i]<LARGE_NUMBER_COMPARE) {
                maximumValue = previousAccelerationsForce[i];
                maxIndex = i;
            }

            if (previousAccelerationsForce[i]<minimumValue && previousAccelerationsForce[i]<LARGE_NUMBER_COMPARE) {
                minimumValue = previousAccelerationsForce[i];
                minIndex = i;
            }
        }
        double kek = Math.abs(maximumValue-minimumValue);
        String value = Double.toString(kek);
        //Log.d("STABILITY","delta: " + value);

        return Math.abs(maximumValue-minimumValue);
    }

    public double getMax() {

        double maximumValue = -10000;

        for (int i = 0; i<250; i++) {
            if (previousAccelerationsForce[i]>maximumValue && previousAccelerationsForce[i]<LARGE_NUMBER_COMPARE) {
                maximumValue = previousAccelerationsForce[i];
            }
        }
        return maximumValue;
    }

    public boolean isAccelerationStable(double maxVariance, int stabilityLength) {

        for (int j=0; j<stabilityLength;j++) {
            int currentIndex = (spinIndex-j);
            if (currentIndex<0) {
                currentIndex = 50 + currentIndex;
            }
            int lastIndex = (currentIndex - 1);
            if (lastIndex<0) {
                lastIndex = 50 + lastIndex;
            }

            //Log.d("STABILITY", "i: "+ Integer.toString(i) + " currentIndex: "+ Integer.toString(currentIndex)+ " lastIndex: "+Integer.toString(lastIndex));
            if (Math.abs(previousAccelerations[currentIndex] - previousAccelerations[lastIndex]) > maxVariance) {
                Log.d("STABILITY", "Method ended");
                return false;
            }
        }
        return true;
    }

    public void printData() {
        for (int i = 0; i<50; i++) {
            Log.d("GRAPH", Double.toString(previousAccelerations[(spinIndex + i) % 50]));
        }
    }

}
