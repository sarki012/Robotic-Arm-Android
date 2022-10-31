package com.esark.roboticArm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.esark.framework.Game;
import com.esark.framework.Graphics;
import com.esark.framework.Input;
import com.esark.framework.Pixmap;
import com.esark.framework.Screen;

import static com.esark.roboticArm.Assets.blueJoystick;
import static com.esark.roboticArm.Assets.excavatorTabletLandscapeBackground;
import static com.esark.roboticArm.Assets.redJoystick;

import java.util.List;

public class GameScreen extends Screen implements Input {
    Context context = null;
    int xTouch1 = 0;
    int yTouch1 = 0;

    int xR = 0;
    int yR = 0;
    int xL = 0;
    int yL = 0;
    int scaledXR = 0;
    int scaledYR = 0;
    int scaledXL = 0;
    int scaledYL = 0;
    double angleR = 0;
    double angleL = 0;
    public static int c = 0;
    public static int b = 0;
    public static int o = 0;
    public static int s = 0;
    public static int l = 0;
    public static int r = 0;
    public static int stopSendingLeft = 1;
    public static int stopSendingRight = 1;
    public static int stopSendingLeftTrack = 1;
    public static int stopSendingRightTrack = 1;
    public static int stopSendingBoom = 0;
    public static int stopSendingCurl = 0;
    public static int stopSendingOrbit = 0;
    public static int stopSendingStick = 0;
    public static int backgroundCount = 0;
    public static int joystickCount = 0;
    public static int blueJoystickCount = 0;
    int count = 0;
    public Pixmap backgroundPixmap = null;
    public int xTouchBottomLeft = 1225;
    public int yTouchBottomLeft = 2325;
    public int xTouchBottomRight = 3775;
    public int yTouchBottomRight = 2325;
    public int yTrackLeft = 675;
    public int yTrackRight = 675;
    public int yTrackLeftUp = 0;
    public int yTrackRightUp = 0;
    public int renderCount = 1;
    public int leftThumbOutOfCircle = 0;
    public int rightThumbOutOfCircle = 0;
    public static int delay = 50;
    public String delayString = "0";
    public int delayUpCount = 0;
    public int delayDownCount = 0;
    public int xLeftUp = 0;
    public int yLeftUp = 0;
    public int xLeftScaled = 0;
    public int yLeftScaled = 0;
    public double leftUpAngle = 0;
    public int leftUpHyp = 0;
    public int xRightScaled = 0;
    public int yRightScaled = 0;
    public double rightUpAngle = 0;
    public int rightUpHyp = 0;
    public int xRightUp = 0;
    public int yRightUp = 0;
    public int xLeftSolidJoystick = 0;
    public int yLeftSolidJoystick = 0;
    public int leftHyp = 0;
    public int xRightSolidJoystick = 0;
    public int yRightSolidJoystick = 0;
    public int rightHyp = 0;

    private static final int INVALID_POINTER_ID = -1;
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;


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
        backgroundPixmap = Assets.excavatorTabletLandscapeBackground;
        if (count == 0) {
            g.drawLandscapePixmap(excavatorTabletLandscapeBackground, 0, 0);
            g.drawJoystick(redJoystick, 850, 1950);            //Draw bottom Left Joystick
            g.drawJoystick(redJoystick, 3400, 1950);           //Draw bottom Right joystick
            g.drawBlueJoystick(blueJoystick, 295, 425);        //Draw left track
            g.drawBlueJoystick(blueJoystick, 4110, 425);       //Draw right track
            g.drawText("50", 2800, 720);                //Draw delay text
            g.drawText("%", 3200, 720);
        }
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            mActivePointerId = event.pointer;
            //The pointer points to which finger or thumb. The first finger to touch is 0
            Log.d("ADebugTag", "mActivePointerId: " + mActivePointerId);
            Log.d("ADebugTag", "event.x: " + event.x);
            Log.d("ADebugTag", "event.y: " + event.y);
            if (event.type == TouchEvent.TOUCH_UP) {    //A thumb is lifted. The following code figures out in which region of the screen the thumb was lifted
                if (event.x < 2500 && event.y > 1300) {     //Touch up is in the region of the bottom left joystick
                    xLeftUp = event.x - 1225;       //Normalize the coordinates so (0,0) is in the middle
                    yLeftUp = 2325 - event.y;
                    stopSendingLeft = 1;            //Flag so we know the thumb is lifted
                    //Calculate the hypotenuse of where the thumb was lifted so we can make the joystick return to 0
                    leftUpHyp = ((int) Math.sqrt((xLeftUp * xLeftUp + yLeftUp * yLeftUp))) - 375;
                    //Calculate the angle of where the thumb was lifted so we can make the joystick return to 0
                    leftUpAngle = Math.atan2((double) yLeftUp, (double) xLeftUp);
                }
                if (event.x >= 2500 && event.y > 1300) {    //Touch up is in the region of the bottom right joystick
                    xRightUp = event.x - 3775;       //Normalize the coordinates so (0,0) is in the middle
                    yRightUp = 2325 - event.y;
                    stopSendingRight = 1;            //Flag so we know the thumb is lifted
                    //Calculate the hypotenuse of where the thumb was lifted so we can make the joystick return to 0
                    rightUpHyp = ((int) Math.sqrt((xRightUp * xRightUp + yRightUp * yRightUp))) - 375;
                    //Calculate the angle of where the thumb was lifted so we can make the joystick return to 0
                    rightUpAngle = Math.atan2((double) yRightUp, (double) xRightUp);
                }
                if (event.x < 1500 && event.y < 1300) {     //Touch up is in the region of the left track
                    stopSendingLeftTrack = 1;       //Flag so we know the thumb is lifted
                    yTrackLeftUp = 675 - event.y;   //Normalize the coordinates so (0,0) is in the middle
                    if(yTrackLeftUp > 300){         //Keep -300 <= yTrackLeftUp <= 300
                        yTrackLeftUp = 300;
                    }
                    else if(yTrackLeftUp < -300){
                        yTrackLeftUp = -300;
                    }

                }
                if (event.x > 4000 && event.y < 1300) {     //Touch up is in the region of the right track
                    stopSendingRightTrack = 1;       //Flag so we know the thumb is lifted
                    yTrackRightUp = 675 - event.y;   //Normalize the coordinates so (0,0) is in the middle
                    if(yTrackRightUp > 300){         //Keep -300 <= yTrackRightUp <= 300
                        yTrackRightUp = 300;
                    }
                    else if(yTrackRightUp < -300){
                        yTrackRightUp = -300;
                    }
                }
                if (event.x > 2300 && event.x < 2600 && event.y < 600) {    //Delay up button
                    delayUpCount = 0;       //Flag so we only increment the delay by 5 once per touch
                }
                if (event.x > 2300 && event.x < 2600 && event.y >= 600 && event.y < 1000) {     //Delay down button
                    delayDownCount = 0;     //Flag so we only decrement the delay by 5 once per touch
                }
            }
            if (event.type == TouchEvent.TOUCH_DRAGGED || event.type == TouchEvent.TOUCH_DOWN) {    //A thumb is pressed or dragging the screen
                //The following code determines which region of the screen the thumb was pressed
                count = 1;
                xTouch1 = event.x;          //Get the x and y coordinates of the first touch
                yTouch1 = event.y;
                if (event.x > 1100 && event.x < 2000 && event.y < 1200) {       //Back button
                    //Back Button Code Here
                    backgroundPixmap.dispose();
                    Intent intent2 = new Intent(context.getApplicationContext(), RoboticArm.class);
                    context.startActivity(intent2);
                    return;
                }

                //In the region of the stick and spin circle
                if (xTouch1 < 2500 && yTouch1 > 1300) {
                    xTouchBottomLeft = xTouch1;
                    yTouchBottomLeft = yTouch1;
                    stopSendingLeft = 0;    //Clear the touch up flag
                }
                //In the region of the boom and curl circle
                else if (xTouch1 >= 2500 && yTouch1 > 1300) {
                    xTouchBottomRight = xTouch1;
                    yTouchBottomRight = yTouch1;
                    stopSendingRight = 0;   //Clear the touch up flag
                }
                //In the region of the left track slider
                else if (xTouch1 <= 1000 && yTouch1 <= 1300) {
                    yTrackLeft = yTouch1;
                    stopSendingLeftTrack = 0;   //Clear the touch up flag
                }
                //In the region of the right track slider
                else if (xTouch1 >= 4000 && yTouch1 <= 1300) {
                    yTrackRight = yTouch1;
                    stopSendingRightTrack = 0;      //Clear the touch up flag
                }
                //In the region of the delay up button
                else if (xTouch1 > 2300 && xTouch1 < 2600 && yTouch1 < 600) {
                    if (delayUpCount == 0) {    //Flag so delay gets incremented by 5 once per touch
                        delay += 5;         //Increment the delay by 5%
                        delayUpCount = 1;
                    }
                } else if (xTouch1 > 2300 && xTouch1 < 2600 && yTouch1 >= 600 && yTouch1 < 1000) {
                    if (delayDownCount == 0) {    //Flag so delay gets decremented by 5 once per touch
                        delay -= 5;         //Decrement the delay by 5%
                        delayDownCount = 1;
                    }
                }
                if (delay > 100) {      //Delay maxes out at 100%
                    delay = 100;
                } else if (delay < 0) {
                    delay = 0;      //Min delay is 0%
                }
            }
            xL = xTouchBottomLeft - 1225;       //Normalize the x and y coordinates so (0,0) is in the middle of the circle
            yL = 2325 - yTouchBottomLeft;
            if (((int) Math.sqrt(Math.abs((xL * xL + yL * yL)))) > 570) {       //Thumb is outside of the circle
                //The following code sets the new scaled x and scaled y so the joystick is maxed out at the edge of the circle
                //Inverse tangent to find the angle
                angleL = Math.atan2((double) yL, (double) xL);
                //cos for x
                scaledXL = (int) (570 * Math.cos(angleL));      //New scaled x-coordinate
                //sin for y
                scaledYL = (int) (570 * Math.sin(angleL));      //New scaled y-coordinate
                leftHyp = 570;      //We need the hypotenuse for then the thumb is lifted so the hypotenuse can shorten up
                                    // as the joystick returns to the origin
                leftThumbOutOfCircle = 1;       //Flag so we know to use the scaled x and y coordinates
                if(stopSendingLeft == 0) {
                    o = scaledXL;   //o for orbit, or rotate, is the value sent out over Bluetooth in the connected thread
                    s = scaledYL;   //s for stick is the value sent out over Bluetooth in the connected thread
                }
                else{
                    o = 0;      //Thumb is lifted, send 0s for orbit and stick
                    s = 0;
                }
            } else if (((int) Math.sqrt(Math.abs((xL * xL + yL * yL)))) <= 570) {   //Thumb is in the circle
                leftThumbOutOfCircle = 0;       //Clear the flag
                leftHyp = (int) Math.sqrt(xL * xL + yL * yL);      //We need the hypotenuse for then the thumb is lifted so the
                                                                   // hypotenuse can shorten up as the joystick returns to the origin
                angleL = Math.atan2((double) yL, (double) xL);     //We need the angle for drawing the joystick circles and for then the thumb is lifted so the hypotenuse
                                                                   // can shorten up at the same angle as the joystick returns to the origin
                if(stopSendingLeft == 0) {
                    o = xL;   //o for orbit, or rotate, is the value sent out over Bluetooth in the connected thread
                    s = yL;   //s for stick is the value sent out over Bluetooth in the connected thread
                }
                else{
                    o = 0;      //Thumb is lifted, send 0s for orbit and stick
                    s = 0;
                }
            }
            if(o > -300 && o < 300){        //If o, the x-coordinate, is within 300 of the middle
                stopSendingOrbit = 1;       //We only want to move the stick. "#"s are sent out in ConnectedThread to signal the firmware not to touch the rotate
            }
            else{
                stopSendingOrbit = 0;       //If the absolute value of the touch is greater than 300, we want to rotate. Clear the flag
            }
            xR = xTouchBottomRight - 3775;
            yR = 2325 - yTouchBottomRight;
            if (((int) Math.sqrt(Math.abs((xR * xR + yR * yR)))) > 570) {       //Thumb is outside of the circle
                //The following code sets the new scaled x and scaled y so the joystick is maxed out at the edge of the circle
                //Inverse tangent to find the angle
                angleR = Math.atan2((double) yR, (double) xR);
                //cos for x
                scaledXR = (int) (570 * Math.cos(angleR));      //New scaled x-coordinate
                //sin for y
                scaledYR = (int) (570 * Math.sin(angleR));      //New scaled y-coordinate
                rightHyp = 570;      //We need the hypotenuse for then the thumb is lifted so the
                                     // hypotenuse can shorten up as the joystick returns to the origin
                rightThumbOutOfCircle = 1;       //Flag so we know to use the scaled x and y coordinates
                if(stopSendingRight == 0) {
                    c = scaledXR;       //c for curl is the value sent out over Bluetooth in the connected thread
                    b = scaledYR;       //b for boom is the value sent out over Bluetooth in the connected thread
                }
                else{
                    c = 0;      //Thumb is lifted, send 0s for curl and boom
                    b = 0;
                }
            }
            if ((((int) Math.sqrt(Math.abs((xR * xR + yR * yR))) <= 570))) {
                //The thumb is within the circle. Draw the joystick at the thumb press
                rightThumbOutOfCircle = 0;
                rightHyp = (int) Math.sqrt(xR*xR + yR*yR);      //We need the hypotenuse for then the thumb is lifted so the
                                                                // hypotenuse can shorten up as the joystick returns to the origin
                angleR = Math.atan2((double) yR, (double) xR);     //We need the angle for drawing the joystick circles and for then the thumb is lifted so the hypotenuse
                                                                   // can shorten up at the same angle as the joystick returns to the origin
                if(stopSendingRight == 0) {
                    c = xR;       //c for curl is the value sent out over Bluetooth in the connected thread
                    b = yR;       //b for boom is the value sent out over Bluetooth in the connected thread
                }
                else{
                    c = 0;      //Thumb is lifted, send 0s for curl and boom
                    b = 0;
                }
            }
        }
        if (renderCount == 5) {     //We're only going to draw the background and joysticks every 5th frame
             g.drawLandscapePixmap(excavatorTabletLandscapeBackground, 0, 0);    //Draw background
            //If thumb is withing the gray circle
            if (leftThumbOutOfCircle == 0 && stopSendingLeft == 0) {
                for (int k = 0; k <= leftHyp; k += 10) {    //For loop draws red joysticks 10 pixels apart from the origin to the thumb press
                    //Trigonometry which draws joysticks 10 pixels apart. The hypotenuse, k, keeps getting longer and we use cosine and sine of the angle of press times
                    //k, the hypotenuse
                    xLeftSolidJoystick = (int) (k * Math.cos(angleL));      //New x-coordinate to draw the joystick
                    yLeftSolidJoystick = (int) (k * Math.sin(angleL));      //New y-coordinate to draw the joystick
                    g.drawJoystick(redJoystick, 850 + xLeftSolidJoystick, 1950 - yLeftSolidJoystick);   //Draw joystick
                }
                g.drawJoystick(redJoystick, xTouchBottomLeft - 375, yTouchBottomLeft - 375);    //Draw the joystick at the thumb press

            }
            //If thumb is outside of the gray circle
            else if (leftThumbOutOfCircle == 1 && stopSendingLeft == 0) {
                for (int k = 0; k <= leftHyp; k += 10) {    //For loop draws red joysticks 10 pixels apart from the origin to the thumb press
                    //Trigonometry which draws joysticks 10 pixels apart. The hypotenuse, k, keeps getting longer and we use cosine and sine of the angle of press times
                    //k, the hypotenuse
                    xLeftSolidJoystick = (int) (k * Math.cos(angleL));
                    yLeftSolidJoystick = (int) (k * Math.sin(angleL));
                    g.drawJoystick(redJoystick, 850 + xLeftSolidJoystick, 1950 - yLeftSolidJoystick);
                }
                //The thumb is out of the circle. Use the scaled values of x and y and show joystick at perimeter
                g.drawJoystick(redJoystick, 850 + scaledXL, 1950 - scaledYL);
            }
            //If thumb is withing the gray circle
            if (rightThumbOutOfCircle == 0 && stopSendingRight == 0) {
                for (int k = 0; k <= rightHyp; k += 10) {    //For loop draws red joysticks 10 pixels apart from the origin to the thumb press
                    xRightSolidJoystick = (int) (k * Math.cos(angleR));      //New x-coordinate to draw the joystick
                    yRightSolidJoystick = (int) (k * Math.sin(angleR));      //New y-coordinate to draw the joystick
                    g.drawJoystick(redJoystick, 3400 + xRightSolidJoystick, 1950 - yRightSolidJoystick);   //Draw joystick
                }
                g.drawJoystick(redJoystick, xTouchBottomRight - 375, yTouchBottomRight - 375);    //Draw the joystick at the thumb press

            }
            //If thumb is outside of the gray circle
            else if (rightThumbOutOfCircle == 1 && stopSendingRight == 0) {
            for (int k = 0; k <= rightHyp; k += 10) {    //For loop draws red joysticks 10 pixels apart from the origin to the thumb press
                xRightSolidJoystick = (int) (k * Math.cos(angleR));      //New x-coordinate to draw the joystick
                yRightSolidJoystick = (int) (k * Math.sin(angleR));      //New y-coordinate to draw the joystick
                g.drawJoystick(redJoystick, 3400 + xRightSolidJoystick, 1950 - yRightSolidJoystick);    //Draw joystick
            }
            //The thumb is out of the circle. Use the scaled values of x and y and show joystick at perimeter
            g.drawJoystick(redJoystick, 3400 + scaledXR, 1950 - scaledYR);
        }
        if(stopSendingLeftTrack == 1){      //Thumb is lifted, send 0's
            l = 0;
        }
        else {      //Thumb is pressed
            //If the y-coordinates of the thumb press are between 400-1000, draw a blue joystick
            if (yTrackLeft > 400 && yTrackLeft < 1000 && stopSendingLeftTrack == 0) {
                g.drawBlueJoystick(blueJoystick, 295, yTrackLeft - 250);
                l = 700 - yTrackLeft;   //Value l to be sent out in ConnectedThread is normalized so (0,0) is in the middle of the slider
            }
            //else if the y-coordinate of the thumb press is <= 400, draw a blue joystick maxed out at 300
            else if (yTrackLeft <= 400 && stopSendingLeftTrack == 0) {
                g.drawBlueJoystick(blueJoystick, 295, 150);  //y was 25
                l = 300;        //Value for left track sent out in ConnectedTread is maxed out at 300
            }
            //else if the y-coordinate of the thumb press is >= 1000, draw a blue joystick maxed out at -300
            else if (yTrackLeft >= 1000 && stopSendingLeftTrack == 0) {
                g.drawBlueJoystick(blueJoystick, 295, 750);
                l = -300;       //Value for left track sent out in ConnectedTread is maxed out at -300
            }
        }
        if(stopSendingRightTrack == 1){      //Thumb is lifted, send 0's
            r = 0;
        }
        else {      //Thumb is pressed
            //If the y-coordinates of the thumb press are between 400-1000, draw a blue joystick
            if (yTrackRight > 400 && yTrackRight < 1000 && stopSendingRightTrack == 0) {
                g.drawBlueJoystick(blueJoystick, 4110, yTrackRight - 250);
                r = 700 - yTrackRight;   //Value r to be sent out in ConnectedThread is normalized so (0,0) is in the middle of the slider
            }
            //else if the y-coordinate of the thumb press is <= 400, draw a blue joystick maxed out at 300
            else if (yTrackRight <= 400 && stopSendingRightTrack == 0) {
                g.drawBlueJoystick(blueJoystick, 4110, 150);
                r = 300;        //Value for right track sent out in ConnectedTread is maxed out at 300
            }
            //else if the y-coordinate of the thumb press is >= 1000, draw a blue joystick maxed out at -300
            else if (yTrackRight >= 1000 && stopSendingRightTrack == 0) {
                g.drawBlueJoystick(blueJoystick, 4110, 750);
                r = -300;        //Value for right track sent out in ConnectedTread is maxed out at -300
            }
        }
        if (stopSendingLeft == 1) {     //Bottom left joystick thumb is lifted
            //Draw the joysticks slowly returning to zero. leftUpHyp is the hypotenuse of when the thumb left the screen
            //leftUpAngle is the angle of when the thumb left the screen
            //Every frame the hypotenuse is 60 pixels shorter. We calculate the new x and y-coordinates to draw a red joystick
            xLeftScaled = (int) (leftUpHyp * Math.cos(leftUpAngle));
            yLeftScaled = (int) (leftUpHyp * Math.sin(leftUpAngle));
            //for loop draws red joysticks from the origin to leftUpHyp. leftUpHyp is decremented by 60 every loop
            for (int k = 0; k <= leftUpHyp; k += 10) {
                xLeftSolidJoystick = (int) (k * Math.cos(leftUpAngle));     //New scaled x-coordinate to draw a red joystick
                yLeftSolidJoystick = (int) (k * Math.sin(leftUpAngle));     //New scaled y-coordinate to draw a red joystick
                g.drawJoystick(redJoystick, 850 + xLeftSolidJoystick, 1950 - yLeftSolidJoystick);       //Draw a red joystick
            }
            g.drawJoystick(redJoystick, 850 + xLeftScaled, 1950 - yLeftScaled);     //Draw a red joystick
            leftUpHyp -= 60;        //Decrement the hypotenuse as the trail of joysticks shortens up
            if (leftUpHyp < 0) {
                leftUpHyp = 0;
            }
        }
        if (stopSendingRight == 1) {     //Bottom right joystick thumb is lifted
            //Draw the joysticks slowly returning to zero. rightUpHyp is the hypotenuse of when the thumb left the screen
            //rightUpAngle is the angle of when the thumb left the screen
            //Every frame the hypotenuse is 60 pixels shorter. We calculate the new x and y-coordinates to draw a red joystick
            xRightScaled = (int) (rightUpHyp * Math.cos(rightUpAngle));
            yRightScaled = (int) (rightUpHyp * Math.sin(rightUpAngle));
            //for loop draws red joysticks from the origin to rightUpHyp. rightUpHyp is decremented by 60 every loop
            for (int k = 0; k <= rightUpHyp; k += 10) {
                xRightSolidJoystick = (int) (k * Math.cos(rightUpAngle));     //New scaled x-coordinate to draw a red joystick
                yRightSolidJoystick = (int) (k * Math.sin(rightUpAngle));     //New scaled y-coordinate to draw a red joystick
                g.drawJoystick(redJoystick, 3400 + xRightSolidJoystick, 1950 - yRightSolidJoystick);
            }
            rightUpHyp -= 60;        //Decrement the hypotenuse as the trail of joysticks shortens up
            g.drawJoystick(redJoystick, 3400 + xRightScaled, 1950 - yRightScaled);
            if (rightUpHyp < 0) {
                rightUpHyp = 0;
            }
        }
        if (stopSendingLeftTrack == 1) {        //Thumb is lifted
            //Every frame yTrackLeftUp gets shorter as the joystick slowly returns to 0
            g.drawBlueJoystick(blueJoystick, 295, 425 - yTrackLeftUp);      //Draw blue joystick
            if (yTrackLeftUp >= 0) {        //yTrackLeftUp is positive, decrement yTrackLeftUp to get to 0
                yTrackLeftUp -= 50;
                if (yTrackLeftUp < 0) {
                    yTrackLeftUp = 0;
                }
            } else {                        //yTrackLeft is negative, increment yTrackLeftUp to get to 0
                yTrackLeftUp += 50;
                if (yTrackLeftUp > 0) {
                    yTrackLeftUp = 0;
                }
            }
        }
        if (stopSendingRightTrack == 1) {       //Thumb is lifted
            //Every frame yTrackRightUp gets shorter as the joystick slowly returns to 0
            g.drawBlueJoystick(blueJoystick, 4110, 425 - yTrackRightUp);      //Draw blue joystick
            if (yTrackRightUp >= 0) {        //yTrackRightUp is positive, decrement yTrackRightUp
                yTrackRightUp -= 50;
                if (yTrackRightUp < 0) {
                    yTrackRightUp = 0;
                }
            } else {                        //yTrackRightUp is negative, increment yTrackRightUp
                yTrackRightUp += 50;
                if (yTrackRightUp > 0) {
                    yTrackRightUp = 0;
                }
            }
        }
        delayString = String.valueOf(delay);        //Convert the delay integer to a string
        g.drawRect(2750, 500, 600, 275, 0);     //Draw a blank white box
        g.drawText(delayString, 2800, 720);         //Draw the delay text
        g.drawText("%", 3200, 720);          //Draw a %
        renderCount = 0;                                  //We've reached the end of 5 frames, renderCount = 0
    }

    renderCount++;

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
                    /*
                    numAvg = 10;
                    xR = xTouchRight - 3400;
                    yR = 1950 - yTouchRight;
                    if (((int)Math.sqrt(Math.abs((xR*xR + yR*yR)))) > 570) {
                        //Inverse tangent to find the angle
                        angleR = Math.atan2((double) yR, (double) xR);
                        //cos for x
                        scaledXR = (int) (570 * Math.cos(angleR));
                        //sin for y
                        scaledYR = (int) (570 * Math.sin(angleR));
                        //Save the previous values in case the user lifts a thumb
                        xPrevRight = 3400 + scaledXR;
                        yPrevRight = 1950 - scaledYR;
                        //Draw the joystick maxed out
                        g.drawJoystick(redJoystick, (3400 + scaledXR), (1950 - scaledYR));
                        //Do a numAvg moving average of the x and y coordinates of the thumb presses
                        //Shift all of the values in the temp arrays one value to the left
                        for (h = 1; h < numAvg; h++) {
                            tempCArr[h - 1] = tempCArr[h];
                            tempBArr[h - 1] = tempBArr[h];
                        }
                        //Pop the new x and y coordinates onto the stacks
                        tempCArr[numAvg - 1] = scaledXR;
                        tempBArr[numAvg - 1] = scaledYR;
                        //Once there are numAvg values in the stack rightCount = numAvg
                        if (rightCount < numAvg) {
                            rightCount++;
                        }
                        if (rightCount == numAvg) {
                            //Loop to total up the numAvg values in each array
                            for (j = 0; j < numAvg; j++) {
                                tempC += tempCArr[j];
                                tempB += tempBArr[j];
                            }
                            //The value to be sent out over Bluetooth is c. Take the average
                            c = (int) (tempC / numAvg);
                            //Make a dead zone along the y-axis. Otherwise both motors would always be spinning at the same time
                            if (c > -40 && c < 40) {
                                stopSendingCurl = 1;
                            } else {
                                stopSendingCurl = 0;
                            }
                            b = (int) (tempB / numAvg);
                            //Make a dead zone along the x-axis. Otherwise both motors would always be spinning at the same time
                            if (b > -40 && b < 40) {
                                stopSendingBoom = 1;
                            } else {
                                stopSendingBoom = 0;
                            }

                            tempC = 0;
                            tempB = 0;
                        }
                    } else if((((int)Math.sqrt(Math.abs((xR*xR + yR*yR))) <= 570))) {
                        //The thumb is within the circle. Draw the joystick at the thumb press
                        g.drawJoystick(redJoystick, xTouchRight, yTouchRight);

                        //Do a numAvg moving average of the x and y coordinates of the thumb presses
                        //Shift all of the values in the temp arrays one value to the left

                        for (k = 1; k < numAvg; k++) {
                            tempCArr[k - 1] = tempCArr[k];
                            tempBArr[k - 1] = tempBArr[k];
                        }




                        //Pop the new x and y coordinates onto the stacks
                        tempCArr[numAvg - 1] = xR;
                        tempBArr[numAvg - 1] = yR;
                        //Once there are numAvg values in the stack rightCount = numAvg
                        if (rightCount < numAvg) {
                            rightCount++;
                        }
                        if (rightCount == numAvg) {
                            //Loop to total up the numAvg values in each array
                            for (m = 0; m < numAvg; m++) {
                                tempC += tempCArr[m];
                                tempB += tempBArr[m];
                            }
                            //The value to be sent out over Bluetooth is c. Take the average
                            c = (int) (tempC / numAvg);
                            //Make a dead zone along the y-axis. Otherwise both motors would always be spinning at the same time
                            if (c > -40 && c < 40) {
                                stopSendingCurl = 1;
                            } else {
                                stopSendingCurl = 0;
                            }
                            b = (int) (tempB / numAvg);
                            //Make a dead zone along the x-axis. Otherwise both motors would always be spinning at the same time
                            if (b > -40 && b < 40) {
                                stopSendingBoom = 1;
                            } else {
                                stopSendingBoom = 0;
                            }

                            tempC = 0;
                            tempB = 0;
                        }

                    }
                    xL = xTouchLeft - 150;
                    yL = 275 - yTouchLeft;
                    if (((int)Math.sqrt(Math.abs((xL*xL + yL*yL)))) > 85) {
                        //Inverse tangent to find the angle
                        angleL = Math.atan2((double) yL, (double) xL);
                        //cos for x
                        scaledXL = (int) (85 * Math.cos(angleL));
                        //sin for y
                        scaledYL = (int) (85 * Math.sin(angleL));
                        //Save the previous values in case the user lifts a thumb
                        xPrevLeft = 140 + scaledXL;
                        yPrevLeft = 275 - scaledYL;
                        //Draw the joystick maxed out
                        g.drawCircle((140 + scaledXL), (275 - scaledYL), 45);
                        g.drawLine(140, 275, (140 + scaledXL), (275 - scaledYL), 0);
                        //Do a numAvg moving average of the x and y coordinates of the thumb presses
                        //Shift all of the values in the temp arrays one value to the left
                        for (k = 1; k < numAvg; k++) {
                            tempOArr[k - 1] = tempOArr[k];          //O for orbit
                            tempSArr[k - 1] = tempSArr[k];          //S for stick
                        }
                        //Pop the new x and y coordinates onto the stacks
                        tempOArr[numAvg - 1] = scaledXL;
                        tempSArr[numAvg - 1] = scaledYL;
                        //Once there are numAvg values in the stack rightCount = numAvg
                        if (leftCount < numAvg) {
                            leftCount++;
                        }
                        if (leftCount == numAvg) {
                            //Loop to total up the numAvg values in each array
                            for (m = 0; m < numAvg; m++) {
                                tempO += tempOArr[m];
                                tempS += tempSArr[m];
                            }
                            //o for orbit. Take the average
                            o = (int) (tempO / numAvg);
                            //Make a dead zone along the y-axis. Otherwise both motors would always be spinning at the same time
                            if (o > -40 && o < 40) {
                                stopSendingOrbit = 1;
                            } else {
                                stopSendingOrbit = 0;
                            }
                            //s for stick. Take the average
                            s = (int) (tempS / numAvg);
                            //Make a dead zone along the x-axis. Otherwise both motors would always be spinning at the same time
                            if (s > -40 && s < 40) {
                                stopSendingStick = 1;
                            } else {
                                stopSendingStick = 0;
                            }

                            tempO = 0;
                            tempS = 0;
                        }
                    } else if((((int)Math.sqrt(Math.abs((xL*xL + yL*yL))) <= 85))) {
                        //The thumb is within the circle. Draw the joystick at the thumb press
                        g.drawCircle((140 + xL), (275 - yL), 45);
                        g.drawLine(140, 275, (140 + xL), (275 - yL), 0);


                        //Do a numAvg moving average of the x and y coordinates of the thumb presses
                        //Shift all of the values in the temp arrays one value to the left
                        for (h = 1; h < numAvg; h++) {
                            tempOArr[h - 1] = tempOArr[h];
                            tempSArr[h - 1] = tempSArr[h];
                        }

                        //Pop the new x and y coordinates onto the stacks
                        tempOArr[numAvg - 1] = xL;
                        tempSArr[numAvg - 1] = yL;
                        //Once there are numAvg values in the stack rightCount = numAvg
                        if (leftCount < numAvg) {
                            leftCount++;
                        }


                        if (leftCount == numAvg) {
                            //Loop to total up the numAvg values in each array
                            for (j = 0; j < numAvg; j++) {
                                tempO += tempOArr[j];
                                tempS += tempSArr[j];
                            }
                            //o for orbit. Take the average
                            o = (int) (tempO / numAvg);
                            //Make a dead zone along the y-axis. Otherwise both motors would always be spinning at the same time
                            if (o > -40 && o < 40) {
                                stopSendingOrbit = 1;
                            } else {
                                stopSendingOrbit = 0;
                            }
                            //s for stick
                            s = (int) (tempS / numAvg);
                            //Make a dead zone along the x-axis. Otherwise both motors would always be spinning at the same time
                            if (s > -40 && s < 40) {
                                stopSendingStick = 1;
                            } else {
                                stopSendingStick = 0;
                            }

                            tempO = 0;
                            tempS = 0;
                        }


                    }
                    g.drawCircle(290, yTrackLeft, 45);
                    g.drawCircle(425, yTrackRight, 45);
                    l = 110 - yTrackLeft;
                    r = 110 - yTrackRight;
                }
              */