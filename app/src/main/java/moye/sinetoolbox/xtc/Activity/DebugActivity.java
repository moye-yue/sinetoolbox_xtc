package moye.sinetoolbox.xtc.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

import moye.sinetoolbox.xtc.R;

public class DebugActivity extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        ToggleButton fakeroot_btn = findViewById(R.id.activity_debug_fakeroot);
        fakeroot_btn.setChecked(sharedPreferences.getBoolean("debug_fakeroot",false));
        fakeroot_btn.setOnCheckedChangeListener((compoundButton, b) -> {
            editor.putBoolean("debug_fakeroot",b);
            editor.commit();
            finish();
        });

        findViewById(R.id.activity_debug_clearactivities).setOnClickListener(view -> {
            editor.putString("activity_list","[]");
            editor.commit();
            Toast.makeText(this,"已清空",Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
