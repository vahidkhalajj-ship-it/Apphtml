package ir.promptexa.webviewbook;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends AppCompatActivity {
    private static final String HOST = "appassets.androidplatform.net";
    private WebView webView;
    private View splash;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long startTime;
    private boolean splashHidden = false;
    private static final long MIN_SPLASH_MS = 1500;
    private static final long MAX_SPLASH_MS = 8000;

    private void hideSplash() {
        if (splashHidden || splash == null) return;
        splashHidden = true;
        splash.animate().alpha(0f).setDuration(350).withEndAction(() -> splash.setVisibility(View.GONE)).start();
    }

    private void hideSplashAfterMinimum() {
        long wait = Math.max(0, MIN_SPLASH_MS - (SystemClock.elapsedRealtime() - startTime));
        handler.postDelayed(this::hideSplash, wait);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Blocks screenshots, screen recording and recent-apps thumbnail
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        splash = findViewById(R.id.splash);
        startTime = SystemClock.elapsedRealtime();
        handler.postDelayed(this::hideSplash, MAX_SPLASH_MS);
        webView.setBackgroundColor(Color.parseColor("#f8f9fc"));

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .setDomain(HOST)
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setSupportZoom(true);
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);

        // No long-press selection/menu; prompts are copied with their Copy button
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);
        webView.setOnLongClickListener(v -> true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public void onPageFinished(WebView view, String url) { hideSplashAfterMinimum(); }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if (HOST.equals(u.getHost())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) {}
                return true;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack(); else finish();
            }
        });

        if (savedInstanceState == null) webView.loadUrl("https://" + HOST + "/assets/index.html");
        else webView.restoreState(savedInstanceState);
    }

    @Override protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); webView.saveState(out); }
    @Override protected void onDestroy() { handler.removeCallbacksAndMessages(null); if (webView != null) webView.destroy(); super.onDestroy(); }
}
