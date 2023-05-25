package com.esark.framework;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.esark.roboticarm.GameScreen;
import com.esark.roboticarm.R;
import com.esark.roboticarm.RoboticArm;
import com.esark.roboticarm.ConnectedThread;
import com.esark.video.FloatingWindow;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public abstract class AndroidGame extends Activity implements Game {
    Bundle newBundy = new Bundle();
    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;

    private final String TAG = AndroidGame.class.getSimpleName();

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    // GUI Components
    private TextView mBluetoothStatus;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private ListView mDevicesListView;

    public BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    Button enablebt,disablebt,scanbt, mShowGraphBtn;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    private AlertDialog dialog;

    private int number1000 = 0;
    private int number100 = 0;
    private int number10 = 0;
    private int number1 = 0;
    private int numberHolder = 0;
    private int numberCount = 0;
    private char bluetoothVal3 = 0;
    private char bluetoothVal2 = 0;
    private char bluetoothVal1 = 0;
    private char bluetoothVal0 = 0;
    private int j = 0;
    private int k = 0;
    private int m = 0;
    private int n = 0;
    private int q = 0;
    private int p = 0;
    private int count = 0;
    public static final int bufferSize = 10;
    private int tLast = 0;
    public int breakFlag = 0;
    private final int loopCount = 30;
    int t = 0;
    public static int landscape = 0;
    public static char startChar = 0;
    public static int width = 0;
    public static int height = 0;

    public static int[] boomADC = new int[10];
    public static int[] stickADC = new int[10];
    public static int[] tipADC = new int[10];
    public static int[] clawADC = new int[10];

    public static int[] alpha = new int[10];
    public static int[] alphaFB = new int[10];
    public static int[] beta = new int[10];
    public static int[] betaFB = new int[10];
 //   public static int[] xDistance = new int[10];
    public static int xDistance = 0;

   // private LruCache<String, Bitmap> mMemoryCache;
    //public int count = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

                /*

    If you setup the cache in the Application class then it will never get destroyed
    until the app shuts down. You are guaranteed that the Application class will always
    be "alive" when ever one of your activities are.
         */

        setContentView(R.layout.activity_main);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();
        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);
        display.getSize(size);
        width = size.x;
        height = size.y;
        mBluetoothStatus = (TextView)findViewById(R.id.bluetooth_status);
        //New Client Oncreate Bluetooth Code
        enablebt=(Button)findViewById(R.id.button_enablebt);
        disablebt=(Button)findViewById(R.id.button_disablebt);
        scanbt=(Button)findViewById(R.id.button_scanbt);
        mShowGraphBtn = (Button)findViewById((R.id.display_btn));
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
        if (BTAdapter.isEnabled()){
            scanbt.setVisibility(View.VISIBLE);
        }

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        //Message from run() in ConnectedThread mHandler.obtain message
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        System.out.println(readMessage);
                        breakFlag = 0;
/*
                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 'a') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0 && (number1000 + number100 + number10 + number1) <= 360) {
                                        //stickBuffer += (number1000 + number100 + number10 + number1);
                                        alpha[q] = (number1000 + number100 + number10 + number1);
                                    }
                                    q++;
                                    if (q >= bufferSize)
                                        q = 0;
                                    break;
                                }
                            }
                        }
                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 'A') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0 && (number1000 + number100 + number10 + number1) <= 360) {
                                        //stickBuffer += (number1000 + number100 + number10 + number1);
                                        alphaFB[j] = (number1000 + number100 + number10 + number1);
                                    }
                                    j++;
                                    if (j >= bufferSize)
                                        j = 0;
                                    break;
                                }
                            }
                        }
                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 'b') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0 && (number1000 + number100 + number10 + number1) <= 360) {
                                        //stickBuffer += (number1000 + number100 + number10 + number1);
                                        beta[k] = (number1000 + number100 + number10 + number1);
                                    }
                                    k++;
                                    if (k >= bufferSize)
                                        k = 0;
                                    break;
                                }
                            }
                        }
                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 'B') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0 && (number1000 + number100 + number10 + number1) <= 360) {
                                        //stickBuffer += (number1000 + number100 + number10 + number1);
                                        betaFB[m] = (number1000 + number100 + number10 + number1);
                                    }
                                    m++;
                                    if (m >= bufferSize)
                                        m = 0;
                                    break;
                                }
                            }
                        }
                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 'x') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 40 && (number1000 + number100 + number10 + number1) <= 120) {
                                        //stickBuffer += (number1000 + number100 + number10 + number1);
                                        xDistance = (number1000 + number100 + number10 + number1);
                                    }
                                    p++;
                                    if (p >= bufferSize)
                                        p = 0;
                                    break;
                                }
                            }
                        }
                        */

                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 'b') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0 && (number1000 + number100 + number10 + number1) <= 360) {
                                        //stickBuffer += (number1000 + number100 + number10 + number1);
                                        boomADC[q] = (number1000 + number100 + number10 + number1);
                                    }
                                    q++;
                                    if (q >= bufferSize)
                                        q = 0;
                                    break;
                                }
                            }
                        }

                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 's') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0 && (number1000 + number100 + number10 + number1) <= 360) {
                                        //stickBuffer += (number1000 + number100 + number10 + number1);
                                        stickADC[j] = (number1000 + number100 + number10 + number1);
                                    }
                                    j++;
                                    if (j >= bufferSize)
                                        j = 0;
                                    break;
                                }
                            }
                        }

                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 't') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0) {
                                        tipADC[k] = (number1000 + number100 + number10 + number1);
                                    }
                                    k++;
                                    if (k >= bufferSize) {
                                        k = 0;
                                    }
                                    break;
                                }
                            }
                        }
                        for(int t = 0; t < loopCount; t++) {
                            breakFlag = 0;
                            if (readMessage.charAt(t) == 'c') {         //Next char is a number
                                for (n = 1; n < 5; n++) {
                                    //Log.d("ADebugTag", "readMessage: " + (int)(readMessage.charAt(t + n)));
                                    if ((int) (readMessage.charAt(t + n)) < 48 || (int) (readMessage.charAt(t + n)) > 57) {
                                        breakFlag = 1;
                                    }
                                }
                                if (breakFlag != 1) {
                                    t++;
                                    number1000 = (Character.getNumericValue(readMessage.charAt((t)))) * 1000;
                                    t++;
                                    number100 = (Character.getNumericValue(readMessage.charAt((t)))) * 100;
                                    t++;
                                    number10 = (Character.getNumericValue(readMessage.charAt((t)))) * 10;
                                    t++;
                                    number1 = Character.getNumericValue(readMessage.charAt((t)));
                                    if ((number1000 + number100 + number10 + number1) >= 0) {
                                        clawADC[m] = (number1000 + number100 + number10 + number1);
                                    }
                                    m++;
                                    if (m >= bufferSize) {
                                        m = 0;
                                    }
                                    break;
                                }
                            }
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + msg.obj);
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }

        mShowGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showGraph(); }
        });


        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if(isLandscape == true)
            landscape = 1;
        else if(isLandscape == false)
            landscape = 0;
        int frameBufferWidth = isLandscape ? 5000 : 3500;
        int frameBufferHeight = isLandscape ? 3500 : 5000;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Config.RGB_565);



        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        float scaleX = (float) frameBufferWidth
                / displaymetrics.widthPixels;
        float scaleY = (float) frameBufferHeight
                / displaymetrics.heightPixels;

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getStartScreen();

    }


    public void on(View v){
        if (!BTAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_SHORT).show();
        }
        scanbt.setVisibility(View.VISIBLE);
        lv.setVisibility(View.VISIBLE);
    }

    public void off(View v){
        BTAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_SHORT).show();
        scanbt.setVisibility(View.INVISIBLE);
        lv.setVisibility(View.GONE);
    }

    public void deviceList(View v){
        ArrayList deviceList = new ArrayList();
        pairedDevices = BTAdapter.getBondedDevices();

        if (pairedDevices.size() < 1) {
            Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice bt : pairedDevices) deviceList.add(bt.getName() + " " + bt.getAddress());
            Toast.makeText(getApplicationContext(), "Showing paired devices", Toast.LENGTH_SHORT).show();
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(myListClickListener);
        }
    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);
            new Thread() {
                @Override
                public void run() {
                    boolean fail = false;
                    final BluetoothDevice device = BTAdapter.getRemoteDevice(address);
                    BluetoothSocket tmp = null;
                    mmDevice = device;
                    try {
                        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
                        tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
                    } catch (IOException e) {
                        Log.e(TAG, "Socket's create() method failed", e);
                    }
                    mmSocket = tmp;
                    BTAdapter.cancelDiscovery();
                    try {
                        mmSocket.connect();

                    } catch (IOException connectException) {
                        Log.v(TAG, "Connection exception!");
                        try {
                            mmSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                            /*mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                            mmSocket.connect();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } */
                        } catch (IOException closeException) {

                        }
                    }
                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();
                    mConnectedThread = new ConnectedThread(mmSocket, mHandler);
                    mConnectedThread.start();
                    /*
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AndroidGame.this, CommsActivity.class);
                    intent.putExtra(EXTRA_ADDRESS, address);
                    startActivity(intent);

                     */
                    //  startActivity(intent);
                }
            }.start();
        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Graphics g = this.getGraphics();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscape = 1;
            Intent intent3 = new Intent(this.getApplicationContext(), RoboticArm.class);
            this.startActivity(intent3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            landscape = 0;
            Intent intent4 = new Intent(this.getApplicationContext(), RoboticArm.class);
            this.startActivity(intent4);
        }
    }

    private void showGraph(){
        if (checkOverlayDisplayPermission()) {
            setContentView(renderView);
            // FloatingWindowGFG service is started
            startService(new Intent(this, FloatingWindow.class));
            // The MainActivity closes here
         //   finish();
        } else {
            // If permission is not given,
            // it shows the AlertDialog box and
            // redirects to the Settings
            requestOverlayDisplayPermission();
        }
        //Intent intent = new Intent(this, VideoActivity.class);
        //startActivity(intent);
        // Intent intent = new Intent(this, MuscleVolt.class);
        //startActivity(intent);
    }

    private boolean isMyServiceRunning() {
        // The ACTIVITY_SERVICE is needed to retrieve a
        // ActivityManager for interacting with the global system
        // It has a constant String value "activity".
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        // A loop is needed to get Service information that
        // are currently running in the System.
        // So ActivityManager.RunningServiceInfo is used.
        // It helps to retrieve a
        // particular service information, here its this service.
        // getRunningServices() method returns a list of the
        // services that are currently running
        // and MAX_VALUE is 2147483647. So at most this many services
        // can be returned by this method.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // If this service is found as a running,
            // it will return true or else false.
            if (FloatingWindow.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestOverlayDisplayPermission() {
        // An AlertDialog is created
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // This dialog can be closed, just by taping
        // anywhere outside the dialog-box
        builder.setCancelable(true);

        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");

        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");

        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

                // This method will start the intent. It takes two parameter, one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK);
            }
        });
        dialog = builder.create();
        // The Dialog will
        // show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        // Android Version is lesser than Marshmallow or
        // the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled
            // it will return false or else true
            if (!Settings.canDrawOverlays(this)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        screen.resume();
        renderView.resume();
    }
    @Override
    public void onPause() {
        super.onPause();
        renderView.pause();
        screen.pause();
   //     System.gc();
        //screen.dispose();
        if (isFinishing())
            screen.dispose();
    }
    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Audio getAudio() {
        return audio;
    }
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0, getBaseContext());
        this.screen = screen;
    }
    public Screen getCurrentScreen() {
        return screen;
    }


}
