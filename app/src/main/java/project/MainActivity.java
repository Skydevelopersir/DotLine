package project;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ir.skydevelopers.app.project.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnReset;
    GameView gameView;
    private long timePassMillies = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.resetGame();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initialize() {
        bindViews();
    }

    private void bindViews() {
        btnReset = findViewById(R.id.btn_reset);
        gameView = findViewById(R.id.gameview);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - timePassMillies) > 2000) {
            Snackbar.make(gameView, R.string.press_back_again_to_exit, 2000).show();
            timePassMillies = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
