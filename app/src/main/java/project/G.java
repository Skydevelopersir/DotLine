package project;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import ir.skydevelopers.app.project.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

@SuppressLint("Registered")
public class G extends Application{

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static Resources resources;
    public static DisplayMetrics displayMetrics;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        resources = context.getResources();
        displayMetrics = resources.getDisplayMetrics();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        //Log.i("LOG", "onCreate: From G class");
    }
}
