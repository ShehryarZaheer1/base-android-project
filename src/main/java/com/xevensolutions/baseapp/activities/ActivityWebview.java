package com.xevensolutions.baseapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ProgressBar;


import androidx.viewbinding.ViewBinding;

import com.xevensolutions.baseapp.R;
import com.xevensolutions.baseapp.databinding.ActivityWebviewBinding;

import im.delight.android.webview.AdvancedWebView;

public class ActivityWebview extends BaseActivity {

    String title;
    String url;
    AdvancedWebView mWebView;
    ProgressBar progressBar;

    private static final String KEY_TITLE = "TITLE";

    private static final String KEY_URL = "URL";

    public static void startActivity(Activity activity, String title, String url) {
        Intent intent = new Intent(activity, ActivityWebview.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        activity.startActivity(intent);
    }


    @Override
    public ViewBinding getViewBinding() {
        return ActivityWebviewBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);
        mWebView.setListener(this, new AdvancedWebView.Listener() {
            @Override
            public void onPageStarted(String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(String url) {
                if (mWebView.getTitle().equals(""))
                    mWebView.reload();
                else
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageError(int errorCode, String description, String failingUrl) {
                Log.i("Page error", errorCode + " " + description + " " + failingUrl);
            }

            @Override
            public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

            }

            @Override
            public void onExternalPageRequest(String url) {

            }
        });
        mWebView.setMixedContentAllowed(false);
        mWebView.getSettings().setDisplayZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.setCookiesEnabled(false);
        mWebView.setDesktopMode(true);
        mWebView.clearCache(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.loadUrl(url);
    }


    @Override
    public int getActivityName() {
        return -1;
    }


    @Override
    public void receiveExtras(Bundle arguments) {
        title = arguments.getString(KEY_TITLE);
        url = arguments.getString(KEY_URL);
    }


}