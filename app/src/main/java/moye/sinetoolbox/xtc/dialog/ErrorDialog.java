package moye.sinetoolbox.xtc.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import moye.sinetoolbox.xtc.R;

public class ErrorDialog extends Activity {
    Boolean quit_on_ok = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_dialog);
        Intent intent = this.getIntent();
        TextView title = findViewById(R.id.err_dialog_title);
        title.setText(intent.getStringExtra("title"));
        TextView content = findViewById(R.id.err_dialog_content);
        content.setText(intent.getStringExtra("content"));
        quit_on_ok = intent.getBooleanExtra("quit",false);
    }

    public void _on_ok_click(View view) {
        if (quit_on_ok) System.exit(-2);
        else finish();
    }

    @Override
    public void onBackPressed() {

    }
}