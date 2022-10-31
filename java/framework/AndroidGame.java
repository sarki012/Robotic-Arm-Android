package com.esark.framework;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import androidx.window.WindowMetrics;

import com.esark.roboticArm.R;
import com.esark.roboticArm.RoboticArm;
import com.esark.roboticArm.ConnectedThread;
import com.esark.roboticArm.VideoStream;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public abstract class AndroidGame extends AppCompatActivity implements Game {
    Bundle newBundy = new Bundle();
    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    Context context = null;
    private final String TAG = AndroidGame.class.getSimpleName();
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    Button enablebt,disablebt,scanbt, mShowGraphBtn;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    public final static String EXTRA_ADDRESS = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
    int n = 0;
    int t = 0;
    public static int landscape = 0;
    public static char startChar = 0;
    public static int width = 0;
    public static int height = 0;
    public BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    private Handler mHandler; // Our main handler that will receive callback notifications
    public static WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    //    mWebView = (WebView) findViewById(R.id.activity_main_webview);
      //  String vidAddress = "http://10.0.0.203:8081";
        //mWebView.loadUrl(vidAddress);
        /*
        Intent i = new Intent(this, VideoStream.class);
        //    PackageManager manager = getPackageManager();
        //  i = manager.getLaunchIntentForPackage("com.google.android.apps.maps");

        i.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        //Rect mBounds = new Rect(0, 0, getScreenWidth(this), getScreenHeight(this));
        Rect mBounds = new Rect(1200, 0, 2100, 700);
        ActivityOptions mOptions = getActivityOptions(AndroidGame.this);
        mOptions = mOptions.setLaunchBounds(mBounds);

        startActivity(i, mOptions.toBundle());
        */

        /*
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        VideoStream fragment = new VideoStream();
        fragmentTransaction.add(R.id.fragment, fragment);
        fragmentTransaction.commit();

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.add(R.id.fragment, new VideoStream());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
*/
       //Intent i = new Intent(AndroidGame.this,VideoStream.class);
        //startActivity(i);
        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();
        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);
        display.getSize(size);
        width = size.x;
        height = size.y;

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
        //Message from run() in ConnectedThread mHandler.obtain message
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        System.out.println(readMessage);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

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

        mShowGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showGraph(); }
        });
    }

    public static ActivityOptions getActivityOptions(Context context) {
        ActivityOptions options = ActivityOptions.makeBasic();
        int freeform_stackId = 5;
        try {
            Method method = ActivityOptions.class.getMethod("setLaunchWindowingMode", int.class);
            method.invoke(options, freeform_stackId);
        } catch (Exception e) { /* Gracefully fail */
        }

        return options;
    }
    /*
    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    public static int getScreenHeight(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }
    */


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
        setContentView(renderView);
        // Intent intent = new Intent(this, MuscleVolt.class);
        //startActivity(intent);
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
