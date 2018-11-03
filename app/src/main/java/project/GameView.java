package project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import project.game.Diff;
import project.game.Home;
import project.game.Line;
import project.game.Position;

public class GameView extends View {

    private Paint homePaint;
    private Paint dotPaint;
    private Paint touchPaint;
    private Paint textPaint;
    private Paint linePaint;

    private String BG_Color = "#222222";
    private String player1Color = "#4444ff";
    private String player2Color = "#ff4444";


    private int cols = 6;
    private int rows = 6;
    private int offsetX;
    private int offsetY;
    private int space = 150;
    private int radius = 15;

    private float touchX;
    private float touchY;

    private boolean debugMode = false;
    private boolean isPlayer1 = true;

    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Home> homes = new ArrayList<>();

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //initializeing GameView
        initPaint();
        gameBoxSize();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        drawGameBox(canvas);
        drawLines(canvas);
        drawDots(canvas);
        drawHome(canvas);
        debugMode(canvas);

    }

    private void drawLines(Canvas canvas) {
        for (Line line : lines) {
            drawLine(canvas, line);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //get touch postions in current view. for get raw position you can use getRawX() getRawY().
        touchX = event.getX();
        touchY = event.getY();


        detectLine();

        //Rest GameView For Refresh IT
        invalidate();

        return super.onTouchEvent(event);
    }

    private void detectLine() {

        ArrayList<Diff> diffs = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Position position = computePosition(i, j);
                float diff = computeDiff(touchX, touchY, position.x, position.y);
                diffs.add(new Diff(i, j, diff));
            }
        }

        Collections.sort(diffs, new Comparator<Diff>() {
            @Override
            public int compare(Diff o1, Diff o2) {
                return o1.diff.compareTo(o2.diff);
            }
        });

        Diff min1 = diffs.get(0);
        Diff min2 = diffs.get(1);

        Diff firstPoint;
        Diff secondPoint;

        Home home1 = null;
        Home home2 = null;

        if (min1.i == min2.i) {
            //vertical
            if (min1.j < min2.j) {
                firstPoint = min1;
                secondPoint = min2;
            } else {
                firstPoint = min2;
                secondPoint = min1;
            }

            home1 = new Home(firstPoint.i, firstPoint.j);

            if (firstPoint.i > 0) {
                home2 = new Home(firstPoint.i - 1, firstPoint.j);
            }
        } else {
            //horizontal
            if (min1.i < min2.i) {
                firstPoint = min1;
                secondPoint = min2;
            } else {
                firstPoint = min2;
                secondPoint = min1;
            }

            home1 = new Home(firstPoint.i, firstPoint.j);

            if (firstPoint.j > 0) {
                home2 = new Home(firstPoint.i, firstPoint.j - 1);
            }
        }

        if (firstPoint.diff > space / 1.75) {
            //return;
        }

        lines.add(new Line(firstPoint.i, firstPoint.j, secondPoint.i, secondPoint.j, isPlayer1 ? 1 : 2));

        if (home1 != null) {
            checkHome(home1);
        }

        if (home2 != null) {
            checkHome(home2);
        }


    }

    private void checkHome(Home home) {
        int i = home.i;
        int j = home.j;

        boolean leftConnected = false;
        boolean rightConnected = false;
        boolean topConnected = false;
        boolean bottomConnected = false;

        for (Line line : lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j + 1) {
                leftConnected = true;
            }

            if (line.i1 == i + 1 && line.j1 == j && line.i2 == i + 1 && line.j2 == j + 1) {
                rightConnected = true;
            }

            if (line.i1 == i && line.j1 == j + 1 && line.i2 == i + 1 && line.j2 == j + 1) {
                topConnected = true;
            }

            if (line.i1 == i && line.j1 == j && line.i2 == i + 1 && line.j2 == j) {
                bottomConnected = true;
            }
        }

        boolean isFullConnected = leftConnected && rightConnected && topConnected && bottomConnected;
        if (isFullConnected) {
            home.playerIndex = isPlayer1 ? 1 : 2;
            homes.add(home);
        }
    }

    private void debugMode(Canvas canvas) {
        if (debugMode == true) {

            //draw dotNaming Postions in under per dot
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    String name = " " + i + "," + j;
                    Position position = computePosition(i, j);
                    canvas.drawText(name, position.x, (position.y) + 50, textPaint);
                }
            }

            //create dot for showing touch position
            canvas.drawCircle(touchX, touchY, 10, touchPaint);

        }
    }

    private Position computePosition(int i, int j) {
        int x = offsetX + (i * space);
        int y = offsetY + (((rows - 1) - j) * space);
        return new Position(x, y);
    }

    private float computeDiff(float x1, float y1, float x2, float y2) {
        return ((float) Math.sqrt((Math.pow(x1 - x2, 2)) + (Math.pow(y1 - y2, 2))));
    }

    private void drawLine(Canvas canvas, Line line) {
        Position p1 = computePosition(line.i1, line.j1);
        Position p2 = computePosition(line.i2, line.j2);
        if (line.player == 1) {
            linePaint.setColor(Color.parseColor(player1Color));
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);
            isPlayer1 = false;
        } else if (line.player == 2) {
            linePaint.setColor(Color.parseColor(player2Color));
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);
            isPlayer1 = true;
        }

    }

    private void drawDots(Canvas canvas) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Position position = computePosition(i, j);
                canvas.drawCircle(position.x, position.y, radius, dotPaint);
            }
        }
    }

    private void drawHome(Canvas canvas) {
        for (Home home : homes) {
            if (home.playerIndex == 1) {
                homePaint.setColor(Color.parseColor(player1Color));
            } else {
                homePaint.setColor(Color.parseColor(player2Color));
            }

            Position homePosition = computePosition(home.i, home.j);
            canvas.drawCircle(homePosition.x + space / 2, homePosition.y - space / 2, 30, homePaint);
        }
    }

    private void drawGameBox(Canvas canvas) {
        //set background color
        canvas.drawColor(Color.parseColor(BG_Color));
    }

    private void initPaint() {

        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        homePaint = new Paint();
        homePaint.setColor(Color.WHITE);
        homePaint.setStyle(Paint.Style.FILL);
        homePaint.setAntiAlias(true);

        touchPaint = new Paint();
        touchPaint.setColor(Color.RED);
        touchPaint.setStyle(Paint.Style.FILL);
        touchPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(10);
        linePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

    }

    private void gameBoxSize() {
        //calculate game box size
        int boxWith = (cols - 1) * space;
        int boxHeight = (rows - 1) * space;

        //set game box in center screen
        DisplayMetrics display = getResources().getDisplayMetrics();
        int screenWith = display.widthPixels;
        int screenHeight = display.heightPixels;
        offsetX = (screenWith - boxWith) / 2;
        offsetY = (screenHeight - boxHeight) / 2;
    }

}
