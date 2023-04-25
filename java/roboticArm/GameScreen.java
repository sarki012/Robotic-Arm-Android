package com.esark.roboticarm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.esark.framework.Game;
import com.esark.framework.Graphics;
import com.esark.framework.Input;
import com.esark.framework.Pixmap;
import com.esark.framework.Screen;
import com.esark.video.FloatingWindow;

import static com.esark.framework.AndroidGame.bufferSize;
import static com.esark.roboticarm.Assets.blueJoystick;
import static com.esark.roboticarm.Assets.excavatorTabletLandscapeBackground;
import static com.esark.roboticarm.Assets.redJoystick;
import static com.esark.roboticarm.Assets.robotPortraitBackground;
import static com.esark.roboticarm.ConnectedThread.arrayFilled;
import static com.esark.roboticarm.ConnectedThread.count;

import java.util.List;

public class GameScreen extends Screen implements Input {
    Context context = null;
    int x = 0;
    int y = 0;
    public static int clawOpen = 0;
    public static int clawClosed = 0;
    public static int left = 0;
    public static int right = 0;
    public static int up = 0;
    public static int down = 0;
    public static int out = 0;
    public static int in = 0;
    public static int tipDown = 0;
    public static int tipUp = 0;
    public static int record = 0;
    public static int repeat = 0;

    public int i = 1;

    public final int arraySize = 100;
    public static int[] boomADC = new int[50];
    public static int[] stickADC = new int[1000];
    public static int[] tipADC = new int[100];
    public int[] tip = new int[100];
    public static int[] clawADC = new int[1000];

    public int stickBuffer = 0;
    public int stickAvg = 0;
    public int tipBuffer = 0;
    public int tipAvg = 0;
    public int stickAvgPrev;
    public int stickCount = 0;
    public int stickADCPrev = 0;
    public int start = 0;
    public int k = 0;
    public int m = 0;
    public int n = 0;
    public int q = 0;
    public int r = 0;
    public double stickSD = 0;
    public double tipSD = 0;
    public Pixmap backgroundPixmap = null;
    private static final int INVALID_POINTER_ID = -1;
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    //public static int tipADC = 0;
    public int filled = 0;
    public int fillCount = 0;

    //Constructor
    public GameScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime, Context context) {
        //framework.input
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        updateRunning(touchEvents, deltaTime, context);
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime, Context context) {
        //updateRunning() contains controller code of our MVC scheme
        //Here is where we draw to the canvas
        Graphics g = game.getGraphics();
        backgroundPixmap = Assets.robotPortraitBackground;
        g.drawPortraitPixmap(backgroundPixmap, 0, 0);
/*
        if(filled == 0) {
            tip[fillCount] = tipADC;
            fillCount++;
            if(fillCount == arraySize){
                filled = 1;
            }
        }
        else {
            for (k = arraySize - 1; k > 0; k--) {
                tip[k] = tip[k - 1];        //Shift all ints to the right
            }
            tip[0] = tipADC;

            for (k = 0; k < arraySize; k++) {
                tipBuffer += tip[k];
            }
            tipAvg = tipBuffer / arraySize;
            tipBuffer = 0;
            for (k = 0; k < arraySize; k++) {
                tipSD += Math.pow(tip[k] - tipAvg, 2);
            }
            tipSD = Math.sqrt(tipSD / arraySize);
            for (k = 0; k < arraySize; k++) {
                if ((Math.abs(tip[k] - tipAvg)) > (3 * tipSD)) {
                    tip[k] = tipAvg;
                }
            }
            for (k = 0; k < arraySize; k++) {
                tipBuffer += tip[k];
            }
            tipAvg = tipBuffer / arraySize;
            g.drawFBRect(830, 960);
            g.drawText(String.valueOf(tipAvg), 830, 1160);
            tipBuffer = 0;
        }
*/
        tipBuffer = 0;
        tipSD = 0;
        for (k = 0; k < arraySize; k++) {
            tip[k] = tipADC[k];
        }
        for (m = 0; m < arraySize; m++) {
            tipBuffer += tip[m];
        }
        tipAvg = tipBuffer / arraySize;
        tipBuffer = 0;

        for (n = 0; n < arraySize; n++) {
            tipSD += Math.pow((double)(tip[n] - tipAvg), 2);
        }
        tipSD = Math.sqrt(tipSD / arraySize);
        for (q = 0; q < arraySize; q++) {
            if ((Math.abs(tip[q] - tipAvg)) > (1 * tipSD)){
                tip[q] = tipAvg;
            }
        }
        for (r = 0; r < arraySize; r++) {
            tipBuffer += tip[r];
        }
        tipAvg = tipBuffer / arraySize;


        g.drawFBRect(830, 960);
        g.drawText(String.valueOf(tipAvg), 830, 1160);
       // tipBuffer = 0;


        /*
        stickBuffer += stickADC[k];
        stickAvg = stickBuffer/2;
        stickSD = Math.pow(stickADC[k] - stickAvg, 2);
        stickSD = Math.sqrt(stickSD / 2);
        if(Math.abs(stickADC[k] - stickSD) < 30){
            g.drawFBRect(830, 580);
            g.drawText(String.valueOf(stickAvg), 830, 780);
            stickAvgPrev = stickAvg;
        }
        else{
            stickAvg = stickAvgPrev;
        }
        k++;
        if(k >= 10){
            k = 0;
        }
        stickBuffer = 0;
        stickCount = 0;
*/
     //   g.drawFBRect(830, 200);
       // g.drawText(String.valueOf(boomADC[i]), 830, 400);
        /*
        if(start == 0){
            stickADCPrev = stickADC[0];
            start = 1;
        }
        if(Math.abs(stickADCPrev - stickADC[0]) < 10){
            stickADCPrev = stickADC[0];
            g.drawFBRect(830, 580);
            g.drawText(String.valueOf(stickADC[0]), 830, 780);
        }
        else{
            g.drawFBRect(830, 580);
            g.drawText(String.valueOf(stickADCPrev), 830, 780);
        }
        if(Math.abs(tipADC[i - 1] - tipADC[i]) < 5){
            g.drawFBRect(830, 960);
            g.drawText(String.valueOf(tipADC[i]), 830, 1160);
        }
        else{
            g.drawFBRect(830, 960);
            g.drawText(String.valueOf(tipADC[i - 1]), 830, 1160);
        }
        if(Math.abs(clawADC[i - 1] - clawADC[i]) < 5) {
            g.drawFBRect(830, 1340);
            g.drawText(String.valueOf(clawADC[i]), 830, 1540);
        }
        else{
            g.drawFBRect(830, 1340);
            g.drawText(String.valueOf(clawADC[i - 1]), 830, 1540);
        }
        i++;
        if(i >= 50){
            i = 1;
        }*/
    //    g.drawTestRect(1100, 1850);
        /*
        g.drawTestRect(2250, 400);
        //.drawTestRect(1825, 4100);
        //g.drawTestRect(1825, 3075);
        g.drawTestRect(200, 4100);
        g.drawTestRect(2850, 4100);
        g.drawTestRect(600, 3450);
        g.drawTestRect(2325, 3450);
        g.drawTestRect(1120, 3000);
        g.drawTestRect(1120, 3950);
        g.drawTestRect(1800, 3950);
        g.drawTestRect(1800, 3000);
        g.drawTestRect(2850, 4100);
        g.drawTestRect(175, 2550);  //Claw open
        g.drawTestRect(2800, 2550); //Claw closed


         */

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            mActivePointerId = event.pointer;
            //The pointer points to which finger or thumb. The first finger to touch is 0
            Log.d("ADebugTag", "mActivePointerId: " + mActivePointerId);
            Log.d("ADebugTag", "event.x: " + event.x);
            Log.d("ADebugTag", "event.y: " + event.y);
            x = event.x;          //Get the x and y coordinates of the first touch
            y = event.y;
            if (event.type == TouchEvent.TOUCH_UP) {    //A thumb is lifted. The following code figures out in which region of the screen the thumb was lifted
                if(x > 600 && x < 1100 && y > 3450 && y < 3950){
                    left = 0;
                }
                else if(x > 2325 && x < 2825 && y > 3450 && y < 3950){
                    right = 0;
                }
                else if(x > 1120 && x < 1620 && y > 3000 && y < 3500){
                    up = 0;
                }
                else if(x > 1120 && x < 1620 && y > 3950 && y < 4450){
                    down = 0;
                }
                else if(x > 1800 && x < 2300 && y > 3950 && y < 4450){
                    in = 0;
                }
                else if(x > 1800 && x < 2300 && y > 3000 && y < 3500){
                    out = 0;
                }
                else if(x > 200 && x < 700 && y > 4100 && y < 4600){
                    tipDown = 0;
                }
                else if(x > 2850 && x < 3350 && y > 4100 && y < 4600){
                    tipUp = 0;
                }
                else if(x > 175 && x < 675 && y > 2550 && y < 3050){
                    clawOpen = 0;
                }
                else if(x > 2800 && x < 3300 && y > 2550 && y < 3050){
                    clawClosed = 0;
                }
                else if(x > 950 && x < 1450 && y > 2100 && y < 2600){
                    record = 0;
                }
                else if(x > 1900 && x < 2400 && y > 2100 && y < 2600){
                    repeat = 0;
                }

            }
            if (event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN) {    //A thumb is pressed or dragging the screen
                //The following code determines which region of the screen the thumb was pressed
                if (x > 1100 && x < 1160 && y > 1850 && y < 2350) {       //Back button
                    //Back Button Code Here
                     backgroundPixmap.dispose();
                    Intent intent2 = new Intent(context.getApplicationContext(), RoboticArm.class);
                    context.startActivity(intent2);
                    return;
                }
                else if(x > 600 && x < 1100 && y > 3450 && y < 3950){
                    left = 1;
                }
                else if(x > 2325 && x < 2825 && y > 3450 && y < 3950){
                    right = 1;
                }
                else if(x > 1120 && x < 1620 && y > 3000 && y < 3500){
                    up = 1;
                }
                else if(x > 1120 && x < 1620 && y > 3950 && y < 4450){
                    down = 1;
                }
                else if(x > 1800 && x < 2300 && y > 3950 && y < 4450){
                    in = 1;
                    out = 0;
                }
                else if(x > 1800 && x < 2300 && y > 3000 && y < 3500){
                    out = 1;
                    in = 0;
                }
                else if(x > 200 && x < 700 && y > 4100 && y < 4600){
                    tipDown = 1;
                }
                else if(x > 2850 && x < 3350 && y > 4100 && y < 4600){
                    tipUp = 1;
                }
                else if(x > 175 && x < 675 && y > 2550 && y < 3050){
                    clawOpen = 1;
                }
                else if(x > 2800 && x < 3300 && y > 2550 && y < 3050){
                    clawClosed = 1;
                }
                else if(x > 950 && x < 1450 && y > 2100 && y < 2600){
                    record = 1;
                }
                else if(x > 1900 && x < 2400 && y > 2100 && y < 2600){
                    repeat = 1;
                }
             //   else if(x > 2250 && x < 2750 && y > 400 && y < 900){
               //     startService(new Intent(this, FloatingWindow.class));
                //}
            }
        }
    }

    @Override
    public void present ( float deltaTime){
        Graphics g = game.getGraphics();
    }

    @Override
    public void pause () {

    }

    @Override
    public void resume () {

    }

    @Override
    public void dispose () {
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return false;
    }

    @Override
    public int getTouchX(int pointer) {
        return 0;
    }

    @Override
    public int getTouchY(int pointer) {
        return 0;
    }

    @Override
    public float getAccelX() {
        return 0;
    }

    @Override
    public float getAccelY() {
        return 0;
    }

    @Override
    public float getAccelZ() {
        return 0;
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        return null;
    }
}
