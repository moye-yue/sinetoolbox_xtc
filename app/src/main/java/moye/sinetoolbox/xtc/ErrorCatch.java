package moye.sinetoolbox.xtc;

import android.content.Context;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import moye.sinetoolbox.xtc.dialog.ErrorDialog;

public class ErrorCatch implements Thread.UncaughtExceptionHandler {

    private static ErrorCatch instance;
    private Context context;

    public static ErrorCatch getInstance() {
        if (instance == null) {
            instance = new ErrorCatch();
        }
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        Intent intent = new Intent(context, ErrorDialog.class);
        intent.putExtra("title","抱歉，崩溃了");
        intent.putExtra("quit",true);

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        arg1.printStackTrace(printWriter);

        String ct = result.toString() + "\n请上报开发者";
        intent.putExtra("content",ct);
        context.startActivity(intent);

        arg1.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}