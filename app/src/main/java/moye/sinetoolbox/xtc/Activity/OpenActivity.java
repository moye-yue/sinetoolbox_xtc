package moye.sinetoolbox.xtc.Activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import moye.sinetoolbox.xtc.AppTools;
import moye.sinetoolbox.xtc.R;

public class OpenActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        EditText packagename_view = findViewById(R.id.activity_open_packagename);
        EditText activityname_view = findViewById(R.id.activity_open_activityname);
        packagename_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activityname_view.setText(packagename_view.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Button button = findViewById(R.id.activity_open_openbtn);
        if(!getSharedPreferences("setting",MODE_PRIVATE).getBoolean("root_enable",true)) findViewById(R.id.activity_open_rootuse).setVisibility(View.GONE);
        button.setOnClickListener(view -> {
            if(packagename_view.length() < 3) Toast.makeText(OpenActivity.this,"包名太短",Toast.LENGTH_SHORT).show();
            else if(activityname_view.length() < 6) Toast.makeText(OpenActivity.this,"活动名太短",Toast.LENGTH_SHORT).show();
            else{
                ToggleButton use_btn_view = findViewById(R.id.activity_open_rootuse);
                if(use_btn_view.isChecked()) {
                    new Thread(() -> {
                        Looper.prepare();
                        String exec_return = AppTools.root_exec("am start " + packagename_view.getText() + "/" + activityname_view.getText());
                        if(exec_return.equals("Er00")) Toast.makeText(OpenActivity.this,"没有ROOT权限",Toast.LENGTH_SHORT).show();
                        else if(exec_return.equals("does not exist")) Toast.makeText(OpenActivity.this,"活动不存在",Toast.LENGTH_SHORT).show();
                        else Toast.makeText(OpenActivity.this,"正在使用ROOT打开",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }).start();
                }else{
                    try {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName(packagename_view.getText().toString(), activityname_view.getText().toString()));
                        startActivity(intent);
                    }catch (SecurityException e){
                        Toast.makeText(OpenActivity.this,"此活动需要ROOT打开",Toast.LENGTH_SHORT).show();
                    }catch (ActivityNotFoundException e){
                        Toast.makeText(OpenActivity.this,"活动不存在",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(OpenActivity.this,"发生错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
