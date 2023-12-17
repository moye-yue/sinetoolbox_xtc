package moye.sinetoolbox.xtc.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import moye.sinetoolbox.xtc.AppTools;
import moye.sinetoolbox.xtc.R;

public class ActivityAddActivity extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        EditText packagename_view = findViewById(R.id.activity_open_packagename);
        EditText activityname_view = findViewById(R.id.activity_open_activityname);
        EditText displayname_view = findViewById(R.id.activity_open_displayname);
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
        button.setOnClickListener(view -> {
            if(packagename_view.length() < 3) Toast.makeText(ActivityAddActivity.this,"包名太短",Toast.LENGTH_SHORT).show();
            else if(activityname_view.length() < 6) Toast.makeText(ActivityAddActivity.this,"活动名太短",Toast.LENGTH_SHORT).show();
            else{
                try {
                    JSONArray array = new JSONArray(sharedPreferences.getString("activity_list",""));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("package",packagename_view.getText());
                    jsonObject.put("uri",activityname_view.getText());
                    if(displayname_view.getText().length() < 1) jsonObject.put("name",activityname_view.getText());
                    else jsonObject.put("name",displayname_view.getText());
                    array.put(jsonObject);
                    editor.putString("activity_list",array.toString());
                    editor.commit();
                    Toast.makeText(this,"已添加",Toast.LENGTH_SHORT).show();
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppTools.open_activity(this, MainActivity.class);
    }
}
