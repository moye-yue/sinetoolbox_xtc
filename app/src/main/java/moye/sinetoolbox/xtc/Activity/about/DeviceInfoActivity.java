package moye.sinetoolbox.xtc.Activity.about;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.UUID;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.R;

public class DeviceInfoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        LinearLayout layout = findViewById(R.id.device_view_list);
        TextView textView1 = new TextView(this);
        textView1.setTextColor(getResources().getColor(R.color.font_title));
        textView1.setText("安卓版本：" + Build.VERSION.RELEASE);
        layout.addView(textView1);

        TextView textView2 = new TextView(this);
        textView2.setTextColor(getResources().getColor(R.color.font_title));
        textView2.setText("SDK版本：" + Build.VERSION.SDK_INT);
        layout.addView(textView2);

        TextView textView3 = new TextView(this);
        textView3.setTextColor(getResources().getColor(R.color.font_title));
        textView3.setText("设备名称：" + Build.DEVICE);
        layout.addView(textView3);

        TextView textView4 = new TextView(this);
        textView4.setTextColor(getResources().getColor(R.color.font_title));
        textView4.setText("SOC ABI：" + Build.CPU_ABI + "," + Build.CPU_ABI2);
        layout.addView(textView4);

        TextView textView5 = new TextView(this);
        textView5.setTextColor(getResources().getColor(R.color.font_title));
        textView5.setText("ro.build.type：" + Build.TYPE);
        layout.addView(textView5);

        TextView textView6 = new TextView(this);
        textView6.setTextColor(getResources().getColor(R.color.font_title));
        textView6.setText("安卓ID：" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        layout.addView(textView6);

        TextView textView7 = new TextView(this);
        textView7.setTextColor(getResources().getColor(R.color.font_title));
        textView7.setText("输入法活动：" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS));
        layout.addView(textView7);

        TextView textView8 = new TextView(this);
        textView8.setTextColor(getResources().getColor(R.color.font_title));
        textView8.setText("设备唯一ID：" + UUID.randomUUID().toString());
        layout.addView(textView8);
    }
}
