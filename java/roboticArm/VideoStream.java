package com.esark.roboticArm;

import static com.esark.framework.AndroidGame.mWebView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class VideoStream extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_stream);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        String vidAddress = "http://10.0.0.203:8081";
        //  Uri vidUri = Uri.parse(vidAddress);
        mWebView.loadUrl(vidAddress);
    }
}

