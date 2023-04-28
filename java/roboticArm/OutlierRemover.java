package com.esark.roboticarm;


import static com.esark.framework.AndroidGame.boomADC;
import static com.esark.framework.AndroidGame.clawADC;
import static com.esark.framework.AndroidGame.stickADC;
import static com.esark.framework.AndroidGame.tipADC;

public class OutlierRemover {       //Takes an array and returns an average with the outliers remover
    private int buffer = 0;
    private double standardDev = 0;
    private int avg = 0;
    private int i = 0;
    private final int arraySize = 10;
    private int[] array = new int[10];
    public OutlierRemover(){
    }
    public int removeOutliers(char input){
        buffer = 0;
        standardDev = 0;
        if(input == 'b') {   //Boom
            for (i = 0; i < arraySize; i++) {
                array[i] = boomADC[i];
                buffer += array[i];
            }
        }
        else if(input == 's') {   //Stick
            for (i = 0; i < arraySize; i++) {
                array[i] = stickADC[i];
                buffer += array[i];
            }
        }
        else if(input == 't'){
            for (i = 0; i < arraySize; i++) {
                array[i] = tipADC[i];
                buffer += array[i];
            }
        }
        else if(input == 'c'){
            for (i = 0; i < arraySize; i++) {
                array[i] = clawADC[i];
                buffer += array[i];
            }
        }
        avg = buffer / arraySize;
        buffer = 0;
/*
        for (i = 0; i < arraySize; i++) {
            standardDev += Math.pow((double) (array[i] - avg), 2);
        }
        standardDev = Math.sqrt(standardDev / arraySize);
        for (i = 0; i < arraySize; i++) {
            if ((Math.abs(array[i] - avg)) > (2 * standardDev)) {
                array[i] = avg;
            }
        }
        for (i = 0; i < arraySize; i++) {
            buffer += array[i];
        }

        avg = buffer / arraySize;

 */
        return(avg);
    }
}
