package moye.sinetoolbox.xtc.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.R;

public class RemoveDialog extends BaseActivity {
    private int index = -1;
    private int c_type = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_remove);
        Intent intent = this.getIntent();
        index = intent.getIntExtra("index",-1);
        c_type = intent.getIntExtra("type",-1);
    }

    public void _on_remove_tip_yes_click(View view) {
        Intent intent = new Intent();
        intent.putExtra("index",index);
        intent.putExtra("type",c_type);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void _on_remove_tip_no_click(View view){
        Intent intent = new Intent();
        intent.putExtra("index",-2);
        intent.putExtra("type",c_type);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}