package moye.sinetoolbox.xtc.Activity.root;

import android.content.Intent;
import android.os.Bundle;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.R;
import moye.sinetoolbox.xtc.dialog.RootRemountTipActivity;

public class RootRemountActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remount);

        findViewById(R.id.activity_root_remount_system).setOnClickListener(view -> {
            Intent intent = new Intent(RootRemountActivity.this, RootRemountTipActivity.class);
            intent.putExtra("dd",1);
            startActivity(intent);
        });
    }
}