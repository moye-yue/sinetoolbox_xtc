package moye.sinetoolbox.xtc.Activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import moye.sinetoolbox.xtc.R;

public class ToastActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);

        findViewById(R.id.activity_toast_show).setOnClickListener(view -> Toast.makeText(getApplicationContext(),((EditText)findViewById(R.id.toast_content)).getText(),Toast.LENGTH_LONG).show());
    }
}
