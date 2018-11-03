package project;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import ir.skydevelopers.app.project.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

@SuppressLint("Registered")
public class G extends Application{

    @SuppressLint("StaticFieldLeak")
    public static  Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        //Log.i("LOG", "onCreate: From G class");
    }
}
