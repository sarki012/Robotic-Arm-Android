package com.esark.roboticarm;



import static com.esark.roboticarm.GameScreen.clawClosed;
import static com.esark.roboticarm.GameScreen.clawOpen;
import static com.esark.roboticarm.GameScreen.down;
import static com.esark.roboticarm.GameScreen.in;
import static com.esark.roboticarm.GameScreen.left;
import static com.esark.roboticarm.GameScreen.out;
import static com.esark.roboticarm.GameScreen.record;
import static com.esark.roboticarm.GameScreen.repeat;
import static com.esark.roboticarm.GameScreen.right;
import static com.esark.roboticarm.GameScreen.tipDown;
import static com.esark.roboticarm.GameScreen.tipUp;
import static com.esark.roboticarm.GameScreen.up;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import android.os.SystemClock;
import android.util.Log;

import com.esark.framework.AndroidGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


//Connected Thread handles Bluetooth communication
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;

    public static int arrayFilled = 0;
    public static int count = 0;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.available();
                byte[] buffer = new byte[55];
                if (bytes != 0) {
                    SystemClock.sleep(10); //Was 10 pause and wait for rest of data. Adjust this depending on your sending speed. Originally 100
                    bytes = mmInStream.read(buffer, 0, 55); // record how many bytes we actually read
                    mHandler.obtainMessage(AndroidGame.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget(); // Send the obtained bytes to the UI activity

                }
            } catch (IOException e) {
                e.printStackTrace();

                break;
            }


            if(clawOpen == 1){
                write("n");
       //         SystemClock.sleep(10);
            }
            else if(clawClosed == 1){
                write("c");
    //            SystemClock.sleep(10);
            }
            else if(clawOpen == 0 && clawClosed == 0){
                write("%");
    //            SystemClock.sleep(10);
            }

            if(up == 1){
                write("u");
    //            SystemClock.sleep(10);
            }
            else if(down == 1){
                write("d");
    //            SystemClock.sleep(10);
            }
            else if(down == 0 && up == 0) {
                write("@");
      //          SystemClock.sleep(10);
            }
            if(left == 1){
                write("l");
       //         SystemClock.sleep(10);
            }
            else if(right == 1){
                write("r");
      //          SystemClock.sleep(10);
            }
            else if(right == 0 && left == 0){
                write("$");
     //           SystemClock.sleep(10);
            }
            if(out == 1 && in == 0){
                write("O");
       //         SystemClock.sleep(10);
            }
            else if(in == 1 && out == 0){
                write("I");
      //          SystemClock.sleep(10);
            }
            else if(in == 0 && out == 0){
                write("&");
      //          SystemClock.sleep(10);
            }
            if(tipDown == 1){
                write("t");             //Tip Down
   //             SystemClock.sleep(10);
            }
            else if(tipUp == 1){
                write("p");             //Tip Up
        //        SystemClock.sleep(10);
            }
            else if(tipDown == 0 && tipUp == 0){
                write("^");             //Stop tip motor
        //        SystemClock.sleep(10);
            }
            /*
            if(record == 1){
                write("rec");
             //  SystemClock.sleep(100);
            }
            if(repeat == 1){
                write("rep");
                //SystemClock.sleep(100);
            }

             */
        }
    }

    public void write(String input) {
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}