package moye.sinetoolbox.xtc.Activity.root;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.R;
import moye.sinetoolbox.xtc.dialog.RootRebootDialogActivity;

public class RootRebootActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_reboot);

        findViewById(R.id.activity_root_reboot_rec).setOnClickListener(view -> reboot_dialog(1));
        findViewById(R.id.activity_root_reboot_edl).setOnClickListener(view -> reboot_dialog(2));
        findViewById(R.id.activity_root_reboot_bl).setOnClickListener(view -> reboot_dialog(3));
        if(Build.VERSION.SDK_INT < 21) ((LinearLayout)findViewById(R.id.activity_root_btn_cont)).removeView(findViewById(R.id.activity_root_reboot_edl));
    }

    private void reboot_dialog(int code) {
        Intent intent = new Intent(RootRebootActivity.this, RootRebootDialogActivity.class);
        intent.putExtra("reboot",code);
        startActivity(intent);
    }
}