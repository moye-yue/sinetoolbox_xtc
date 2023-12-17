package moye.sinetoolbox.xtc;

import android.app.Application;

public class Sinetoolbox extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        ErrorCatch crashHandler = ErrorCatch.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
