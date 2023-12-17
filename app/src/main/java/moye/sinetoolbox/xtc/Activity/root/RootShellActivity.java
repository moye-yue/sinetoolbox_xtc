package moye.sinetoolbox.xtc.Activity.root;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import moye.sinetoolbox.xtc.Activity.BaseActivity;
import moye.sinetoolbox.xtc.AppTools;
import moye.sinetoolbox.xtc.view.DragableLuncher;
import moye.sinetoolbox.xtc.R;
import moye.sinetoolbox.xtc.dialog.RemoveDialog;

public class RootShellActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private JSONArray history_json;
    private LinearLayout history_btns;
    private DragableLuncher launcher;
    private ImageView launcher_status;

    private boolean command_runing = false;

    private Thread command_thread = null;
    private int now_run_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_shell);

        launcher = findViewById(R.id.root_shell_launcher);
        launcher_status = findViewById(R.id.shell_status);
        history_btns = findViewById(R.id.root_shell_right_linear);

        sharedPreferences = getSharedPreferences("root_shell",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        try {
            history_json = new JSONArray(sharedPreferences.getString("history","[]"));
            for (int i = 0;i<history_json.length();i++) create_history_button(history_json.getString(i),i);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        findViewById(R.id.activity_root_shell_clear).setOnClickListener(view -> ((EditText)findViewById(R.id.shell_edittext)).setText(""));
        findViewById(R.id.activity_root_shell_enter).setOnClickListener(view -> ((EditText)findViewById(R.id.shell_edittext)).setText(((TextView) findViewById(R.id.shell_edittext)).getText() + "\n"));
        findViewById(R.id.activity_root_shell_run).setOnClickListener(view -> {
            if(!((EditText)findViewById(R.id.shell_edittext)).getText().toString().isEmpty())
                run_command(((EditText)findViewById(R.id.shell_edittext)).getText().toString(),true);
        });
        findViewById(R.id.activity_root_shell_stop).setOnClickListener(view -> {
            if (command_runing){
                now_run_id += 1;
                command_runing = false;
                ((TextView)findViewById(R.id.shell_statustext)).setText("已强制终止运行");
            }
        });
        findViewById(R.id.activity_root_shell_exit).setOnClickListener(view -> {
            if (!command_runing) finish();
            else Toast.makeText(this, "请先等待命令运行完成", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.activity_root_shell_history_clear).setOnClickListener(view -> {
            history_btns.removeAllViews();
            history_json = new JSONArray();
            editor.putString("history", history_json.toString());
            editor.commit();
        });
        update_status();
    }

    void update_status(){
        if(command_runing) {
            findViewById(R.id.activity_root_shell_stop).setEnabled(true);
            findViewById(R.id.activity_root_shell_run).setEnabled(false);
        } else {
            findViewById(R.id.activity_root_shell_stop).setEnabled(false);
            findViewById(R.id.activity_root_shell_run).setEnabled(true);
        }
        switch (launcher.getCurrentScreen()){
            case 0:
                launcher_status.setImageResource(R.drawable.frame_1in2);
                break;
            case 1:
                launcher_status.setImageResource(R.drawable.frame_2in2);
                break;
        }
        new Handler().postDelayed(() -> update_status(),300);
    }

    private void create_history_button(String history,int index){
        Button button = new Button(this);
        button.setPadding(7,7,7,7);
        button.setText(history);
        button.setAllCaps(false);
        button.setTextColor(getResources().getColor(R.color.font_title));
        button.setOnClickListener(view -> {
            ((EditText)findViewById(R.id.shell_edittext)).setText(history);
            run_command(history,false);
        });
        button.setOnLongClickListener(view -> {
            Intent intent = new Intent(RootShellActivity.this, RemoveDialog.class);
            intent.putExtra("index",index);
            startActivityForResult(intent,1);
            return false;
        });
        button.setBackground(getResources().getDrawable(R.drawable.button_default));
        TextView textView = new TextView(this);
        textView.setHeight(10);
        history_btns.addView(textView);
        history_btns.addView(button);
    }

    public void run_command(String command,boolean new_history) {
        launcher.snapToScreen(0);
        if (new_history) {
            history_json.put(command);
            create_history_button(command,history_json.length() + 1);
            editor.putString("history", history_json.toString());
            editor.commit();
        }
        ((TextView)findViewById(R.id.shell_statustext)).setText("");
        ((TextView)findViewById(R.id.shell_statustext)).setHint("指令运行中...\n\n");
        command_thread = new Thread(){
            @Override
            public void run(){
                super.run();
                command_runing = true;
                Process process = null;
                DataOutputStream os = null;
                try {
                    process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes(command + "\n");
                    os.writeBytes("exit\n");
                    os.flush();
                    process.waitFor();

                    BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String tmp;
                    while((tmp = successResult.readLine()) != null) {
                        String finalTmp = tmp;
                        RootShellActivity.this.runOnUiThread(() -> ((TextView)findViewById(R.id.shell_statustext)).setText(((TextView)findViewById(R.id.shell_statustext)).getText() + finalTmp + "\n"));
                    }
                    while((tmp = errorResult.readLine()) != null){
                        String finalTmp = tmp;
                        RootShellActivity.this.runOnUiThread(() -> ((TextView)findViewById(R.id.shell_statustext)).setText(((TextView)findViewById(R.id.shell_statustext)).getText() + finalTmp + "\n"));
                    }
                } catch (Exception e) {
                    RootShellActivity.this.runOnUiThread(() -> ((TextView)findViewById(R.id.shell_statustext)).setText("运行命令时发生错误，请确保软件拥有Root权限"));
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                        process.destroy();
                    } catch (Exception e) {
                    }
                }
                command_runing = false;
                RootShellActivity.this.runOnUiThread(() -> ((TextView)findViewById(R.id.shell_statustext)).setHint("这里将会显示命令的运行结果\n\n"));
            }
        };
        command_thread.start();
    }

    @Override  //接受初入页面返回的参数
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            int index = data.getIntExtra("index",-1);
            if(index == -1){
                Toast.makeText(this,"返回值错误", Toast.LENGTH_SHORT).show();
            }else if (index != -2) {
                history_json.remove(index);
                Toast.makeText(this,"已删除", Toast.LENGTH_SHORT).show();
                history_btns.removeAllViews();
                try {
                    for (int i = 0; i < history_json.length(); i++) {
                        create_history_button(history_json.getString(i),i);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                editor.putString("history", history_json.toString());
                editor.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {}
}