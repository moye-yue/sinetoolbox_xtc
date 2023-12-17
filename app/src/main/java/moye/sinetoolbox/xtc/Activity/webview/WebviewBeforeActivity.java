package moye.sinetoolbox.xtc.Activity.webview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.R;

public class WebviewBeforeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_before);

        findViewById(R.id.activity_webview_before_bing).setOnClickListener(view -> ((EditText)findViewById(R.id.webview_url)).setText("https://bing.com"));
        findViewById(R.id.activity_webview_before_baidu).setOnClickListener(view -> ((EditText)findViewById(R.id.webview_url)).setText("https://baidu.com"));
        findViewById(R.id.activity_webview_before_go).setOnClickListener(view -> {
            Intent intent = new Intent(WebviewBeforeActivity.this,WebviewActivity.class);
            intent.putExtra("url",((EditText)findViewById(R.id.webview_url)).getText().toString());
            startActivity(intent);
        });
    }
}
