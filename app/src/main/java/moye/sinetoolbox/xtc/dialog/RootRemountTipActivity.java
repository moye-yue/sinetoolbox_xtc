package moye.sinetoolbox.xtc.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.AppTools;
import moye.sinetoolbox.xtc.R;

public class RootRemountTipActivity extends BaseActivity {
    private int dd_type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_remount_tip);
        Intent intent = this.getIntent();
        dd_type = intent.getIntExtra("dd",0);
    }

    public void _on_tip_yes_click(View view) {
        if(Objects.equals(dd_type, 0)){
            Toast.makeText(this, "你是怎么打开这个页面的？", Toast.LENGTH_SHORT).show();
        }
        else if(Objects.equals(dd_type, 1)){
            if(AppTools.root_exec_without_return("mount -o rw,remount /system\n")){
                Toast.makeText(this, "已成功将系统分区挂载为可读写", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "挂载分区失败", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    public void _on_tip_no_click(View view){
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}