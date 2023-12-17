package moye.sinetoolbox.xtc.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

public class BaseActivity extends Activity {
    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration origConfig = newBase.getResources().getConfiguration();
        origConfig.densityDpi = 320;
        Context confBase = newBase.createConfigurationContext(origConfig);
        super.attachBaseContext(confBase);
    }
}
