package moye.sinetoolbox.xtc.Activity.webview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.R;
import moye.sinetoolbox.xtc.dialog.RemoveDialog;
import moye.sinetoolbox.xtc.view.DragableLuncher;

public class WebviewActivity extends BaseActivity {
    String last_url = "";
    WebView webView;
    DragableLuncher launcher;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private JSONArray history_json;
    private LinearLayout history_btns;
    private JSONArray bookmark_json;
    private LinearLayout bookmark_btns;
    private ToggleButton add_bookmark_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        launcher = findViewById(R.id.webview_launcher);
        launcher.isOpen = false;
        launcher.mCurrentScreen = 0;

        sharedPreferences = getSharedPreferences("webview",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        history_btns = findViewById(R.id.webview_history_list);
        bookmark_btns = findViewById(R.id.webview_bookmark_list);

        try {
            history_json = new JSONArray(sharedPreferences.getString("history","[]"));
            for (int i = 0;i<history_json.length();i++) create_history_button(history_json.getJSONObject(i).getString("url"),history_json.getJSONObject(i).getString("title"),history_json.getJSONObject(i).getString("time"),i);
            bookmark_json = new JSONArray(sharedPreferences.getString("bookmark","[]"));
            for (int i = 0;i<bookmark_json.length();i++) create_bookmark_button(bookmark_json.getJSONObject(i).getString("url"),bookmark_json.getJSONObject(i).getString("title"),i);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Intent intent = getIntent();
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                TextView textView = findViewById(R.id.webview_title);
                textView.setText(title);
            }
        });
        webView.setDownloadListener((s, s1, s2, s3, l) -> Toast.makeText(WebviewActivity.this,"不支持下载",Toast.LENGTH_SHORT).show());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    EditText editText = findViewById(R.id.webview_url_edittext);
                    editText.setText(url);
                    if(!Objects.equals(url, last_url)){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("title",webView.getTitle());
                        jsonObject.put("url",url);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                        jsonObject.put("time",sdf.format(new Date(System.currentTimeMillis())));
                        history_json.put(jsonObject);
                        editor.putString("history",history_json.toString());
                        editor.commit();
                        create_history_button(url,webView.getTitle(),sdf.format(new Date(System.currentTimeMillis())),history_json.length());
                        last_url = url;
                    }
                }catch (Exception e){
                    Toast.makeText(WebviewActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {;
                try {
                    boolean flags = false;
                    for (int i = 0;i<bookmark_json.length();i++){
                        if(bookmark_json.getJSONObject(i).getString("url").equals(webView.getUrl())) {
                            flags = true;
                            break;
                        }
                    }
                    add_bookmark_btn.setChecked(flags);
                }catch (Exception e){
                    Toast.makeText(WebviewActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        last_url = intent.getStringExtra("url");
        webView.loadUrl(last_url);

        add_bookmark_btn = findViewById(R.id.webview_bookmark_add_btn);
        add_bookmark_btn.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                if(b){
                    boolean flags = false;
                    for (int i = 0;i<bookmark_json.length();i++){
                        if(bookmark_json.getJSONObject(i).getString("url").equals(webView.getUrl())) {
                            flags = true;
                            break;
                        }
                    }
                    if(!flags){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("title",webView.getTitle());
                        jsonObject.put("url",webView.getUrl());
                        bookmark_json.put(jsonObject);
                        editor.putString("bookmark", bookmark_json.toString());
                        editor.commit();
                        bookmark_btns.removeAllViews();
                        try {
                            for (int j = 0; j < bookmark_json.length(); j++) create_bookmark_button(bookmark_json.getJSONObject(j).getString("url"),bookmark_json.getJSONObject(j).getString("title"),j);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else{
                    for (int i = 0;i<bookmark_json.length();i++){
                        if(bookmark_json.getJSONObject(i).getString("url").equals(webView.getUrl())){
                            bookmark_json.remove(i);
                            editor.putString("bookmark", bookmark_json.toString());
                            editor.commit();
                            bookmark_btns.removeAllViews();
                            try {
                                for (int j = 0; j < bookmark_json.length(); j++) create_bookmark_button(bookmark_json.getJSONObject(j).getString("url"),bookmark_json.getJSONObject(j).getString("title"),j);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                    }
                }
            }catch (Exception e){
                Toast.makeText(WebviewActivity.this,"发生错误",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.activity_webview_apps).setOnClickListener(view -> launcher.setToScreen(1));
        findViewById(R.id.activity_webview_goto).setOnClickListener(view -> {
            launcher.setToScreen(0);
            webView.loadUrl(((EditText)findViewById(R.id.webview_url_edittext)).getText().toString());
        });
        findViewById(R.id.activity_webview_refresh).setOnClickListener(view -> {
            launcher.setToScreen(0);
            webView.reload();
        });
        findViewById(R.id.activity_webview_history).setOnClickListener(view -> launcher.setToScreen(2));
        findViewById(R.id.activity_webview_bookmark).setOnClickListener(view -> launcher.setToScreen(3));
        findViewById(R.id.activity_webview_back).setOnClickListener(view -> {
            if(webView.canGoBack()){
                webView.goBack();
                launcher.setToScreen(0);
            }else {
                Toast.makeText(this, "已经是第一页了", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.activity_webview_top).setOnClickListener(view -> {
            launcher.setToScreen(0);
            webView.setScrollY(0);
        });
        findViewById(R.id.activity_webview_web).setOnClickListener(view -> launcher.setToScreen(0));
        findViewById(R.id.activity_webview_history_back).setOnClickListener(view -> launcher.setToScreen(0));
        findViewById(R.id.activity_webview_history_clear).setOnClickListener(view -> {
            history_json = new JSONArray();
            editor.putString("history",history_json.toString());
            editor.commit();
            history_btns.removeAllViews();
        });
        findViewById(R.id.activity_webview_exit).setOnClickListener(view -> finish());
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    private void create_history_button(String history,String title,String time,int index){
        Button button = new Button(this);
        button.setPadding(7,7,7,7);
        button.setText(Html.fromHtml("<font color='#ffffff'>" + title + "</font><br/><font color='#999999'>" + history + "</font><br/><font color='#333333'>" + time + "</font>"));
        button.setAllCaps(false);
        button.setTextColor(getResources().getColor(R.color.font_title));
        button.setOnClickListener(view -> {
            webView.loadUrl(history);
            launcher.setToScreen(0);
        });
        button.setOnLongClickListener(view -> {
            Intent intent = new Intent(WebviewActivity.this, RemoveDialog.class);
            intent.putExtra("index",index);
            intent.putExtra("type",0);
            startActivityForResult(intent,1);
            return false;
        });
        button.setBackground(getResources().getDrawable(R.drawable.button_default));
        TextView textView = new TextView(this);
        textView.setHeight(10);
        history_btns.addView(textView);
        history_btns.addView(button);
    }
    private void create_bookmark_button(String url,String title,int index){
        Button button = new Button(this);
        button.setPadding(7,7,7,7);
        button.setText(Html.fromHtml("<font color='#ffffff'>" + title + "</font><br/><font color='#999999'>" + url + "</font>"));
        button.setAllCaps(false);
        button.setOnClickListener(view -> {
            webView.loadUrl(url);
            launcher.setToScreen(0);
        });
        button.setOnLongClickListener(view -> {
            Intent intent = new Intent(WebviewActivity.this, RemoveDialog.class);
            intent.putExtra("index",index);
            intent.putExtra("type",1);
            startActivityForResult(intent,1);
            return false;
        });
        button.setBackground(getResources().getDrawable(R.drawable.button_default));
        TextView textView = new TextView(this);
        textView.setHeight(10);
        bookmark_btns.addView(textView);
        bookmark_btns.addView(button);
    }
    @Override  //接受初入页面返回的参数
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            int c_type = data.getIntExtra("type",-1);
            if(c_type == 0){
                int index = data.getIntExtra("index",-1);
                if(index == -1) {
                    Toast.makeText(this,"返回值错误", Toast.LENGTH_SHORT).show();
                }else if(index != -2) {
                    history_json.remove(index);
                    Toast.makeText(this,"已删除", Toast.LENGTH_SHORT).show();
                    history_btns.removeAllViews();
                    try {
                        for (int i = 0; i < history_json.length(); i++) {
                            create_history_button(history_json.getJSONObject(i).getString("url"),history_json.getJSONObject(i).getString("title"),history_json.getJSONObject(i).getString("time"),i);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    editor.putString("history", history_json.toString());
                    editor.commit();
                }
            }else if(c_type == 1){
                int index = data.getIntExtra("index",-1);
                if(index == -1) {
                    Toast.makeText(this,"返回值错误", Toast.LENGTH_SHORT).show();
                }else if(index != -2) {
                    bookmark_json.remove(index);
                    Toast.makeText(this,"已删除", Toast.LENGTH_SHORT).show();
                    bookmark_btns.removeAllViews();
                    try {
                        for (int i = 0; i < bookmark_json.length(); i++) {
                            create_bookmark_button(bookmark_json.getJSONObject(i).getString("url"),bookmark_json.getJSONObject(i).getString("title"),i);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    editor.putString("bookmark", bookmark_json.toString());
                    editor.commit();
                }
            }
        }
    }
}
