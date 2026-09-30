-keep class ir.promptexa.webviewbook.MainActivity$AppBridge { *; }
-keepclassmembers class ir.promptexa.webviewbook.MainActivity$AppBridge {
    @android.webkit.JavascriptInterface <methods>;
}
