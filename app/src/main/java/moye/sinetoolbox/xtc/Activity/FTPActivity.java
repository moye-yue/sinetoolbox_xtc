package moye.sinetoolbox.xtc.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;
import java.util.List;

import moye.sinetoolbox.xtc.AppTools;
import moye.sinetoolbox.xtc.dialog.ErrorDialog;
import moye.sinetoolbox.xtc.R;

public class FTPActivity extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FtpServer mFtpServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);

        sharedPreferences = getSharedPreferences("ftp_server",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        ToggleButton ftp_button = findViewById(R.id.ftp_toggle);
        ftp_button.setOnCheckedChangeListener((compoundButton, b) -> _on_ftp_btn_change(b));


        EditText ftp_username = findViewById(R.id.ftp_username);
        EditText ftp_password = findViewById(R.id.ftp_password);
        ftp_username.setText(sharedPreferences.getString("username","user"));
        ftp_password.setText(sharedPreferences.getString("password",""));
    }

    private void _on_ftp_btn_change(boolean b) {
        TextView textView = findViewById(R.id.ftp_tip);
        if(b){
            EditText ftp_username = findViewById(R.id.ftp_username);
            EditText ftp_password = findViewById(R.id.ftp_password);
            ToggleButton ftp_button = findViewById(R.id.ftp_toggle);
            if(ftp_username.getText().toString().length() > 8){
                ftp_button.setChecked(false);
                Intent intent = new Intent(FTPActivity.this,ErrorDialog.class);
                intent.putExtra("title","超出范围");
                intent.putExtra("content","FTP用户名超出规定的长度");
                startActivity(intent);
            }else if(ftp_username.getText().toString().length() < 2){
                ftp_button.setChecked(false);
                Intent intent = new Intent(FTPActivity.this,ErrorDialog.class);
                intent.putExtra("title","不足范围");
                intent.putExtra("content","FTP用户名太短");
                startActivity(intent);
            }else if(ftp_password.getText().toString().length() > 14){
                ftp_button.setChecked(false);
                Intent intent = new Intent(FTPActivity.this,ErrorDialog.class);
                intent.putExtra("title","超出范围");
                intent.putExtra("content","FTP密码太长");
                startActivity(intent);
            }else{
                editor.putString("username",ftp_username.getText().toString());
                editor.putString("password",ftp_password.getText().toString());
                editor.commit();
                FtpServerFactory serverFactory = new FtpServerFactory();
                ListenerFactory factory = new ListenerFactory();
                serverFactory.addListener("default",factory.createListener());
                factory.setPort(2221);
                factory.setServerAddress("0.0.0.0");
                serverFactory.addListener("default",factory.createListener());
                BaseUser user = new BaseUser();
                user.setName(ftp_username.getText().toString());
                user.setPassword(ftp_password.getText().toString());
                user.setEnabled(true);
                user.setMaxIdleTime(3000);
                user.setHomeDirectory("/storage/emulated/0");
                List<Authority> authorityList = new ArrayList<>();
                authorityList.add(new WritePermission());
                user.setAuthorities(authorityList);
                try {
                    serverFactory.getUserManager().save(user);
                    if(mFtpServer != null){
                        mFtpServer.stop();
                    }
                    mFtpServer = serverFactory.createServer();
                    mFtpServer.start();
                    textView.setText("FTP服务已在 " + AppTools.getLocalIPAddress(this) + ":2221 上开启");

                    ftp_username.setEnabled(false);
                    ftp_password.setEnabled(false);
                }catch (Exception e){
                    Intent intent = new Intent(FTPActivity.this,ErrorDialog.class);
                    intent.putExtra("title","发生错误");
                    intent.putExtra("content",e.toString());
                    startActivity(intent);
                }
            }
        }else{
            if(mFtpServer != null){
                mFtpServer.stop();
                mFtpServer = null;
            }
            textView.setText("");
            ((EditText)findViewById(R.id.ftp_username)).setEnabled(true);
            ((EditText)findViewById(R.id.ftp_password)).setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFtpServer != null){
            mFtpServer.stop();
            mFtpServer = null;
            Toast.makeText(this, "FTP服务已关闭", Toast.LENGTH_SHORT).show();
        }
    }
}