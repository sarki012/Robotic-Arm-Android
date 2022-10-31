package com.esark.videostreamtest2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.net.Uri;
import android.view.Window;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;
public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
      //  setContentView(R.layout.activity_main);
        //VideoView vidView = (VideoView)findViewById(R.id.myVideo);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        String vidAddress = "http://10.0.0.203:8081";
      //  Uri vidUri = Uri.parse(vidAddress);
        mWebView.loadUrl(vidAddress);
        //String vidAddress = "https://10.0.0.203:8081";
      //  vidView.setVideoURI(vidUri);
        //vidView.start();
    }
}