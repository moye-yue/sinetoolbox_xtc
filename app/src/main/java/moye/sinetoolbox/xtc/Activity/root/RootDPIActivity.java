package moye.sinetoolbox.xtc.Activity.root;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.AppTools;
import moye.sinetoolbox.xtc.R;

public class RootDPIActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_dpi);

        findViewById(R.id.activity_root_dpi_reset).setOnClickListener(view -> change_dpi(0));
        findViewById(R.id.activity_root_dpi_100).setOnClickListener(view -> change_dpi(100));
        findViewById(R.id.activity_root_dpi_120).setOnClickListener(view -> change_dpi(120));
        findViewById(R.id.activity_root_dpi_140).setOnClickListener(view -> change_dpi(140));
        findViewById(R.id.activity_root_dpi_160).setOnClickListener(view -> change_dpi(160));
        findViewById(R.id.activity_root_dpi_200).setOnClickListener(view -> change_dpi(200));
        findViewById(R.id.activity_root_dpi_240).setOnClickListener(view -> change_dpi(240));
        findViewById(R.id.activity_root_dpi_260).setOnClickListener(view -> change_dpi(260));
        findViewById(R.id.activity_root_dpi_280).setOnClickListener(view -> change_dpi(280));
        findViewById(R.id.activity_root_dpi_300).setOnClickListener(view -> change_dpi(300));
        findViewById(R.id.activity_root_dpi_diy).setOnClickListener(view -> {
            EditText editText = findViewById(R.id.dpiInput);
            if(Integer.parseInt(editText.getText().toString()) > 500) Toast.makeText(this, "不建议使用大于500的DPI", Toast.LENGTH_SHORT).show();
            else if(Integer.parseInt(editText.getText().toString()) < 40) Toast.makeText(this, "不建议使用低于40的DPI", Toast.LENGTH_SHORT).show();
            else change_dpi(Integer.parseInt(editText.getText().toString()));
        });
    }

    private void change_dpi(int density){
        String dpi;
        if(density == 0) dpi = "reset";
        else dpi = String.valueOf(density);
        if (AppTools.root_exec_without_return("wm density " + dpi)) Toast.makeText(this, "DPI已复原", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "执行ROOT语句失败", Toast.LENGTH_SHORT).show();
    }
}