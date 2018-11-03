


import android.content.Context;
import android.content.SharedPreferences;

public class First_Load
{

  private static final String FIRST_LOAD = "firstLoad";
  private static final String PREFS_NAME = "prefs";
  private static First_Load instance;
  private final SharedPreferences sharedPreferences;

  public First_Load(Context context)
  {
    sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  public static First_Load with(Context context)
  {

    if (instance == null)
    {
      instance = new First_Load(context);
    }
    return instance;
  }

  public void firstLoadIsDone()
  {

    sharedPreferences
      .edit()
      .putBoolean(FIRST_LOAD, false)
      .apply();
  }

  public boolean isFirstLoad()
  {
    return sharedPreferences.getBoolean(FIRST_LOAD, true);
  }

}
