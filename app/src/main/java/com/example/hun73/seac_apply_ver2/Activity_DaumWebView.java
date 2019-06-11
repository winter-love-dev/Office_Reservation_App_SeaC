package com.example.hun73.seac_apply_ver2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

public class Activity_DaumWebView extends AppCompatActivity
{
    private WebView daum_webView;
    private TextView daum_result, daum_webView_address_confirm;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__daum_web_view);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.daum_webview_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }

    public void init_webView()
    {
        // WebView 설정
        daum_webView = (WebView) findViewById(R.id.daum_webview);
        daum_result = findViewById(R.id.daum_result);

        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");

        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());

        // webview url load. php 파일 주소
//        daum_webView.loadUrl("http://34.73.32.3/daum_address.php"); // gcp
        daum_webView.loadUrl("http://115.68.231.84/daum_address.php"); // iwinv
    }

    private class AndroidBridge
    {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    daum_result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));

                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();

                    daum_webView_address_confirm = findViewById(R.id.daum_webView_address_confirm);

                    daum_webView_address_confirm.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(Activity_DaumWebView.this, Activity_hostAddWorkSpace.class);
//                            intent.putExtra("address", String.format("(%s) %s %s", arg1, arg2, arg3));
                            intent.putExtra("address", String.format("(%s) %s %s", arg1, arg2, arg3));
                            setResult(RESULT_OK, intent);
//                            startActivity(intent);
                            finish();
                        }
                    });
                }
            });
        }
    }
}
