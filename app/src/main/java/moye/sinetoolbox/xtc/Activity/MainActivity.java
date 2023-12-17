package moye.sinetoolbox.xtc.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import moye.sinetoolbox.xtc.Activity.about.DeviceInfoActivity;
import moye.sinetoolbox.xtc.AppTools;
import moye.sinetoolbox.xtc.Activity.about.AboutLogActivity;
import moye.sinetoolbox.xtc.Activity.root.RootDPIActivity;
import moye.sinetoolbox.xtc.Activity.root.RootRebootActivity;
import moye.sinetoolbox.xtc.Activity.root.RootRemountActivity;
import moye.sinetoolbox.xtc.Activity.root.RootShellActivity;
import moye.sinetoolbox.xtc.Activity.webview.WebviewBeforeActivity;
import moye.sinetoolbox.xtc.dialog.RemoveDialog;
import moye.sinetoolbox.xtc.view.DragableLuncher;
import moye.sinetoolbox.xtc.dialog.ErrorDialog;
import moye.sinetoolbox.xtc.R;

public class MainActivity extends BaseActivity {
    private DragableLuncher launcher;
    private ImageView launcher_status;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public boolean has_root = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launcher = findViewById(R.id.main_launcher);
        launcher.mCurrentScreen = 1;
        launcher_status = findViewById(R.id.main_launcher_status);

        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        update_status();
        open_activity_init();
        init_root_page();
        init_setting_page();

        findViewById(R.id.activity_main_title_cont).setOnClickListener(view -> finish());

        ImageView main_activity_page = findViewById(R.id.activity_main_bg);
        switch (sharedPreferences.getInt("background_image",-1)){
            case 0:
                main_activity_page.setImageResource(R.drawable.bg_1);
                break;
            case 1:
                main_activity_page.setImageResource(R.drawable.bg_2);
                break;
            case 2:
                main_activity_page.setImageResource(R.drawable.bg_3);
                break;
            case 3:
                main_activity_page.setImageResource(R.drawable.bg_4);
                break;
            case 4:
                main_activity_page.setImageResource(R.drawable.bg_5);
                break;
            case 5:
                main_activity_page.setImageResource(R.drawable.bg_6);
                break;
            case 6:
                main_activity_page.setImageResource(R.drawable.blur_bg);
                break;
            case 7:
                main_activity_page.setImageResource(R.drawable.splash);
                break;
        }
    }

    void update_status(){
        if(sharedPreferences.getBoolean("root_enable",true)){
            switch (launcher.getCurrentScreen()){
                case 0:
                    finish();
                case 1:
                    launcher_status.setImageResource(R.drawable.frame_1in4);
                    break;
                case 2:
                    launcher_status.setImageResource(R.drawable.frame_2in4);
                    break;
                case 3:
                    launcher_status.setImageResource(R.drawable.frame_3in4);
                    break;
                case 4:
                    launcher_status.setImageResource(R.drawable.frame_4in4);
                    break;
            }
        }else{
            switch (launcher.getCurrentScreen()){
                case 0:
                    finish();
                case 1:
                    launcher_status.setImageResource(R.drawable.frame_1in3);
                    break;
                case 2:
                    launcher_status.setImageResource(R.drawable.frame_2in3);
                    break;
                case 3:
                    launcher_status.setImageResource(R.drawable.frame_3in3);
                    break;
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                update_status();
            }
        },300);
    }


    /*
    =======
    打开活动
    =======
     */
    public void open_activity_init(){
        findViewById(R.id.activity_main_webview_btn).setOnClickListener(view -> AppTools.open_activity(this, WebviewBeforeActivity.class));
        if(Build.VERSION.SDK_INT < 21) findViewById(R.id.activity_main_ftp_btn).setOnClickListener(view -> Toast.makeText(this,"此功能不支持你的系统",Toast.LENGTH_SHORT).show());
        else findViewById(R.id.activity_main_ftp_btn).setOnClickListener(view -> AppTools.open_activity(this,FTPActivity.class));
        findViewById(R.id.activity_main_toast_btn).setOnClickListener(view -> AppTools.open_activity(this,ToastActivity.class));
        findViewById(R.id.activity_main_open_btn).setOnClickListener(view -> AppTools.open_activity(this, OpenActivity.class));
        reload_activitys();
    }
    private void reset_activity_list(){
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0;i<getResources().getStringArray(R.array.activity_packages).length;i++){
                JSONObject object = new JSONObject();
                object.put("package",getResources().getStringArray(R.array.activity_packages)[i]);
                object.put("uri",getResources().getStringArray(R.array.activity_uris)[i]);
                object.put("name",getResources().getStringArray(R.array.activity_names)[i]);
                jsonArray.put(object);
            }
            editor.putString("activity_list",jsonArray.toString());
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void reload_activitys(){
        try{
            if(sharedPreferences.getString("activity_list", "").equals("")) reset_activity_list();

            JSONArray activity_list = new JSONArray(sharedPreferences.getString("activity_list",""));
            LinearLayout btn_layout = findViewById(R.id.activity_main_activity_btn_list);
            LinearLayout setting_list_layout = findViewById(R.id.activity_main_setting_activity_list);
            btn_layout.removeAllViews();
            setting_list_layout.removeAllViews();
            for (int i = 0;i<activity_list.length();i++){
                TextView textView = new TextView(this);
                textView.setHeight(8);
                btn_layout.addView(textView);

                TextView textView2 = new TextView(this);
                textView2.setHeight(8);
                setting_list_layout.addView(textView2);

                Button button = new Button(this);
                button.setText(activity_list.getJSONObject(i).getString("name"));
                button.setBackground(getResources().getDrawable(R.drawable.button_default));
                button.setAllCaps(false);
                button.setHeight(96);
                button.setTextColor(getResources().getColor(R.color.font_title));
                int finalI = i;
                button.setOnClickListener(view -> {
                    try {
                        int result_code = AppTools.open_activity(MainActivity.this,activity_list.getJSONObject(finalI).getString("package"),activity_list.getJSONObject(finalI).getString("uri"));
                        if(result_code == 1) {
                            if(has_root){
                                new Thread(() -> {
                                    Looper.prepare();
                                    try {
                                        String exec_return = AppTools.root_exec("am start " + activity_list.getJSONObject(finalI).getString("package") + "/" + activity_list.getJSONObject(finalI).getString("uri"));
                                        if(exec_return.equals("Er00")) Toast.makeText(MainActivity.this,"没有ROOT权限",Toast.LENGTH_SHORT).show();
                                        else Toast.makeText(MainActivity.this,"正在使用ROOT打开",Toast.LENGTH_SHORT).show();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    Looper.loop();
                                }).start();
                            }else Toast.makeText(this,"该活动需要ROOT权限打开",Toast.LENGTH_SHORT).show();
                        }else if(result_code == 2) Toast.makeText(MainActivity.this,"该活动不存在",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
                button.setLeft(10);
                button.setRight(10);
                button.setTop(10);
                btn_layout.addView(button);


                Button button2 = new Button(this);
                button2.setText(activity_list.getJSONObject(i).getString("name"));
                Drawable drawable = getResources().getDrawable(R.drawable.btn_right);
                drawable.setBounds(0,0,32,32);
                button2.setCompoundDrawables(null,null,drawable,null);
                button2.setBackground(getResources().getDrawable(R.drawable.button_default));
                button2.setAllCaps(false);
                button2.setHeight(70);
                button2.setPadding(6,6,6,6);
                button2.setTextColor(getResources().getColor(R.color.font_title));
                button2.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, RemoveDialog.class);
                    intent.putExtra("index",finalI);
                    startActivityForResult(intent,3);
                });
                button2.setLeft(10);
                button2.setRight(10);
                button2.setTop(10);
                setting_list_layout.addView(button2);
            }
            TextView textView = new TextView(this);
            textView.setHeight(50);
            btn_layout.addView(textView);
            if(!AppTools.getStringMd5(getString(R.string.app_developer)).equals("ad7eacbf0b288d1051afbd76040d4685")) finish();
            if(!AppTools.getStringMd5(AppTools.getStringMd5(getString(R.string.app_developer))).equals("c71e743e40ffee0d2c9b2ac991ab0e7f")) finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /*
    =========
    ROOT工具箱
    =========
     */
    private void init_root_page(){
        ToggleButton selinux_button = findViewById(R.id.selinux_togglebutton);
        String selinux_status = AppTools.root_exec("getenforce");
        selinux_button.setChecked(false);
        if (Objects.equals(selinux_status,"Permissive\n")) selinux_button.setChecked(true);
        else if(Objects.equals(selinux_status,"Disabled\n")) selinux_button.setTextOff("SELinux：禁用模式");
        else if(Objects.equals(selinux_status,"Enforcing\n")) selinux_button.setTextOff("SELinux：强制模式");
        else if(!sharedPreferences.getBoolean("debug_fakeroot",false)) has_root = false;

        if(!has_root){
            findViewById(R.id.root_main).setVisibility(View.GONE);
            findViewById(R.id.root_main_noroot).setVisibility(View.VISIBLE);
            if(sharedPreferences.getBoolean("root_enable",true)) Toast.makeText(MainActivity.this, "未检测到ROOT，将无法使用ROOT相关功能", Toast.LENGTH_SHORT).show();
        }

        selinux_button.setOnCheckedChangeListener((compoundButton, b) -> _on_root_selinux_change(b));

        findViewById(R.id.activity_main_root_shell).setOnClickListener(view -> AppTools.open_activity(this,RootShellActivity.class));
        findViewById(R.id.activity_main_root_dpi).setOnClickListener(view -> AppTools.open_activity(this,RootDPIActivity.class));
        findViewById(R.id.activity_main_root_reboot).setOnClickListener(view -> AppTools.open_activity(this,RootRebootActivity.class));
        findViewById(R.id.activity_main_root_mount).setOnClickListener(view -> AppTools.open_activity(this,RootRemountActivity.class));
        findViewById(R.id.activity_main_root_wifiadb).setOnClickListener(view -> {
            try {
                if (AppTools.root_exec_without_return("setprop service.adb.tcp.port 5555\nstop adbd\nstart adbd")) {
                    TextView textView = findViewById(R.id.wifi_adb_url);
                    view.setEnabled(false);
                    textView.setText("WifiADB已于" + AppTools.getLocalIPAddress(this) + ":5555上开启");
                    Toast.makeText(this, "WifiADB已于 " + AppTools.getLocalIPAddress(this) + ":5555 上开启", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "执行ROOT指令出错", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Intent intent = new Intent(MainActivity.this,ErrorDialog.class);
                intent.putExtra("title","发生错误");
                intent.putExtra("content",e.toString());
                startActivity(intent);
            }
        });
        findViewById(R.id.activity_main_root_appupdate_enable).setOnClickListener(view -> AppTools.able_package(this,"com.xtc.appupdate",true));
        findViewById(R.id.activity_main_root_appupdate_disable).setOnClickListener(view -> AppTools.able_package(this,"com.xtc.appupdate",false));
        findViewById(R.id.activity_main_root_systemupdatei11_enable).setOnClickListener(view -> AppTools.able_package(this,"com.xtc.systemupdate_i11",true));
        findViewById(R.id.activity_main_root_systemupdatei11_disable).setOnClickListener(view -> AppTools.able_package(this,"com.xtc.systemupdate_i11",false));
        findViewById(R.id.activity_main_root_trun_on).setOnClickListener(view -> {
            if(AppTools.root_exec_without_return("content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:1")) Toast.makeText(this, "已尝试开启屏幕自动旋转", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "请确保拥有ROOT权限", Toast.LENGTH_SHORT).show();
        });findViewById(R.id.activity_main_root_trun_off).setOnClickListener(view -> {
            if(AppTools.root_exec_without_return("content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0")) Toast.makeText(this, "已尝试关闭屏幕自动旋转", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "请确保拥有ROOT权限", Toast.LENGTH_SHORT).show();
        });
        if(!sharedPreferences.getBoolean("root_enable",true)) launcher.removeView(findViewById(R.id.main_root_page));
    }
    public void _on_root_selinux_change(boolean b) {
        try {
            ToggleButton selinux_button = findViewById(R.id.selinux_togglebutton);
            if (b) {
                if (AppTools.root_exec_without_return("setenforce 0")) Toast.makeText(this, "SELinux已设置为宽容模式", Toast.LENGTH_SHORT).show();
                else {
                    selinux_button.setChecked(false);
                    Toast.makeText(this, "执行ROOT指令出错", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (AppTools.root_exec_without_return("setenforce 1")) Toast.makeText(this, "SELinux已设置为强制模式", Toast.LENGTH_SHORT).show();
                else {
                    selinux_button.setChecked(true);
                    Toast.makeText(this, "执行ROOT指令出错", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            Intent intent = new Intent(MainActivity.this,ErrorDialog.class);
            intent.putExtra("title","发生错误");
            intent.putExtra("content",e.toString());
            startActivity(intent);
        }
    }


    /*
    =======
    工具箱设置
    =======
     */
    private void init_setting_page(){
        if(sharedPreferences.getInt("background_image",-2) == -2) {
            editor.putInt("background_image",-1);
            editor.commit();
        }

        RadioGroup bg_btns = findViewById(R.id.activity_main_setting_bg_group);
        for(int i = -1;i<getResources().getStringArray(R.array.background_images).length;i++){
            RadioButton button = new RadioButton(this);
            if(i>-1) button.setText(getResources().getStringArray(R.array.background_images)[i]);
            else button.setText("默认背景");
            button.setBackground(getResources().getDrawable(R.drawable.button_default));
            button.setButtonDrawable(R.drawable.radio_button_image);
            button.setAllCaps(false);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(layoutParams);
            button.setPadding(6,6,6,6);
            int finalI = i;
            button.setOnClickListener(view -> {
                editor.putInt("background_image", finalI);
                editor.commit();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            });
            button.setLeft(10);
            button.setRight(10);
            button.setTop(10);
            bg_btns.addView(button);
            if(sharedPreferences.getInt("background_image",-1) == i) bg_btns.check(button.getId());

            TextView textView = new TextView(this);
            textView.setHeight(9);
            bg_btns.addView(textView);
        }

        ToggleButton root_btn = findViewById(R.id.activity_main_setting_root);
        root_btn.setChecked(sharedPreferences.getBoolean("root_enable",true));
        root_btn.setOnCheckedChangeListener((compoundButton, b) -> {
            editor.putBoolean("root_enable",b);
            editor.commit();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        });

        findViewById(R.id.activity_main_setting_reset_activity_add_btn).setOnClickListener(view -> {
            AppTools.open_activity(this,ActivityAddActivity.class);
            finish();
        });
        findViewById(R.id.activity_main_setting_reset_activity_list_btn).setOnClickListener(view -> {
            reset_activity_list();
            reload_activitys();
            Toast.makeText(this,"已重置列表",Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.activity_main_setting_refresh_activity_list_btn).setOnClickListener(view -> reload_activitys());
    }



    /*
    ========
    关于工具箱
    ========
     */
    private int version_text_click_count = 0;
    public void _on_version_text_click(View view) {
        if(version_text_click_count++ >= 4) AppTools.open_activity(this,DebugActivity.class);
    }
    public void _on_updatelog_click(View view) {
        AppTools.open_activity(this, AboutLogActivity.class);
    }

    public void _on_device_click(View view) {
        AppTools.open_activity(this, DeviceInfoActivity.class);
    }

    @Override
    public void onBackPressed() {}

    @Override  //接受初入页面返回的参数
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == 3) {
                int index = data.getIntExtra("index",-1);
                if(index == -1){
                    Toast.makeText(this,"返回值错误", Toast.LENGTH_SHORT).show();
                }else if (index != -2) {
                    JSONArray array = new JSONArray(sharedPreferences.getString("activity_list",""));
                    array.remove(index);
                    editor.putString("activity_list",array.toString());
                    editor.commit();
                    Toast.makeText(this,"已删除", Toast.LENGTH_SHORT).show();
                    reload_activitys();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}