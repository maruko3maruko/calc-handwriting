/*!
 *  @date 2020/01/17
 *  @file MainActivity.java
 *  @author SELVAS AI
 *
 *  Copyright 2020. SELVAS AI Inc. All Rights Reserved.
 */

package com.selvasai.handwriting.math;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.selvy.spmath.DHWR;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {
      String URL = "https://google.com";
    private WebView webView;
    private WritingRecognizer mWritingRecognizer;
    private WritingView mWritingView;
    private WebView mWebView;
    private TextView mVersion;
    private TextView mCandidates;
    private final String MATHJAX_HUB_QUEUE_URL = "javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);";
    private final String MATHJAX_MATH_URL_PREFIX = "javascript:document.getElementById('math').innerHTML=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webViews);
        webView.setWebViewClient(new WebViewClient()); // WebViewを設定する
        webView.getSettings().setJavaScriptEnabled(true); // JavaScriptを有効にする
        webView.loadUrl(URL);

        copyResourceToStorage();
        initialize();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_BACK){ // 戻るボタンがタップされた時
            if(webView != null && webView.canGoBack()){ // WebViewがNULLでなく、閲覧履歴があるなら
                webView.goBack(); // 一つ前のウェブページを表示する
            }
            return true;
        }else{
            return super.onKeyDown(keyCode, e);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();// バックグラウンドからフォアグランドに戻った時など
        if(webView != null){ // WebViewが空でなければ
            String url = webView.getUrl(); // 現在のウェブページを
            webView.loadUrl(url); // 再表示する
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWritingRecognizer.destroy();
    }

    private void initialize() {
        mWritingRecognizer = new WritingRecognizer(getApplicationContext());
        mWritingView = (WritingView) findViewById(R.id.canvas);
        mWritingView.setRecognizer(mWritingRecognizer);
        Button clearButton = (Button) findViewById(R.id.clear);
        if (clearButton != null) {
            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        handleClear();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        Button recognizeButton = (Button) findViewById(R.id.recognize);
        if (recognizeButton != null) {
            recognizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        handleRecognize();
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        RadioGroup languageGroup = (RadioGroup) findViewById(R.id.languageGroup);
        if (languageGroup != null) {
            languageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    handleLanguageChanged(id);
                }
            });
        }

        mVersion = (TextView) findViewById(R.id.version);
        mVersion.setText(mWritingRecognizer.getVersion());
        mCandidates = (TextView) findViewById(R.id.candidates);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.loadDataWithBaseURL("http://bar/", "<script type='text/x-mathjax-config'>"
                + "MathJax.Hub.Config({ "
                + "showMathMenu: false, "
                + "jax: ['input/TeX','output/HTML-CSS'], "
                + "extensions: ['tex2jax.js','toMathML.js'], "
                + "TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
                + "'noErrors.js','noUndefined.js'] }, });</script>"
                + "<script type='text/javascript' src='file:///android_asset/MathJax/MathJax.js'></script>"
                + "<span id='math'></span>","text/html","utf-8","");

    }

    private void copyResourceToStorage() {
        String path = "hdb";
        String[] fileList = null;
        try {
            fileList = getAssets().list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileList == null) {
            return;
        }

        for (String file : fileList) {
            try {
                String filePath = (path + "/" + file);
                try {
                    FileInputStream fis = openFileInput(file);
                    fis.close();
                } catch (FileNotFoundException e) {
                    InputStream is = getAssets().open(filePath);
                    final int bufferSize = is.available();
                    byte[] buffer = new byte[bufferSize];
                    int readSize = is.read(buffer);
                    is.close();
                    if (readSize > 0) {
                        FileOutputStream fos = openFileOutput(file, Activity.MODE_PRIVATE);
                        fos.write(buffer);
                        fos.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//消す
    private void handleClear() throws IOException, ExecutionException, InterruptedException {
        mCandidates.setVisibility(View.GONE);
        mCandidates.setText("");
        mWritingView.clear();
        mWritingRecognizer.clearInk();

        mWebView.setVisibility(View.GONE);
        loadUrl(MATHJAX_MATH_URL_PREFIX + "''");
        loadUrl(MATHJAX_HUB_QUEUE_URL);
 ;

    }

    //OK
    private void handleRecognize() throws IOException, ExecutionException, InterruptedException {
        webView = (WebView) findViewById(R.id.webViews);
        webView.setWebViewClient(new WebViewClient()); // WebViewを設定する
        webView.getSettings().setJavaScriptEnabled(true); // JavaScriptを有効にする
        webView.loadUrl(mWritingRecognizer.recognize().get(1));


        mWritingView.clear();
        mCandidates.setText(mWritingRecognizer.recognize().get(0));

        mCandidates.setVisibility(View.VISIBLE);
        mWritingRecognizer.clearInk();

        mWebView.setVisibility(View.VISIBLE);

        String[] candidates = mCandidates.getText().toString().split("\n");
        loadUrl(MATHJAX_MATH_URL_PREFIX + "'\\\\[" + doubleEscapeTeX(candidates[0]) + "\\\\]';");
        loadUrl(MATHJAX_HUB_QUEUE_URL);

//        webView = (WebView) findViewById(R.id.webViews);
//        webView.setWebViewClient(new WebViewClient()); // WebViewを設定する
//        webView.getSettings().setJavaScriptEnabled(true); // JavaScriptを有効にする
//        webView.loadUrl(mWritingRecognizer.recognize().get(1));

//
     }

    private void handleLanguageChanged(int id) {
        int language = DHWR.DLANG_MATH_MIDDLE_EXPANSION;
        int option = DHWR.DTYPE_MATH_EX;
        switch (id) {
            case R.id.mathematical:
                language = DHWR.DLANG_MATH_MIDDLE_EXPANSION;
                option = DHWR.DTYPE_MATH_EX;
                break;
//            case R.id.chemical:
//                language = DHWR.DLANG_MATH_CHEMICAL;
//                option = DHWR.DTYPE_MATH_CF;
//                break;
        }
        mWritingRecognizer.setLanguage(language, option);
    }

     private void loadUrl(String url) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             mWebView.evaluateJavascript(url, null);
         } else {
             mWebView.loadUrl(url);
         }
     }

    private String doubleEscapeTeX(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') {
                sb.append("\\");
            }
            if (s.charAt(i) != '\n') {
                sb.append(s.charAt(i));
            }
            if (s.charAt(i) == '\\') {
                sb.append("\\");
            }
        }
        return sb.toString();
    }
}
