package project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ir.skydevelopers.app.project.R;
import project.game.Box;
import project.game.Debug;
import project.game.Diff;
import project.game.Line;
import project.game.Options;
import project.game.Point;
import project.game.Position;
import project.game.State;
import project.game.Theme;


public class GameView extends View {

    private Paint paintBox;
    private Paint paintDot;
    private Paint paintTouch;
    private Paint paintText;
    private Paint paintLine;

    private int boxWidth;
    private int boxHeight;

    private int screenWidth;
    private int screenWidthHalf;
    private int screenHeight;

    private int offsetX;
    private int offsetY;

    private float touchX;
    private float touchY;

    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_TOP = 2;
    private static final int EDGE_BOTTOM = 3;

    public static ArrayList<Line> lines = new ArrayList<>();
    public static ArrayList<Box> boxes = new ArrayList<>();


    public GameView(Context context) {
        super(context);
        initialize();
    }


    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }


    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }


    private void initialize() {
        if (isInEditMode()) {
            return;
        }

        initializePaints();
        initializeMetrics();
    }


    private void initializePaints() {
        paintDot = new Paint();
        paintDot.setColor(Color.WHITE);
        paintDot.setStyle(Paint.Style.FILL);
        paintDot.setAntiAlias(true);

        paintBox = new Paint();
        paintBox.setColor(Color.WHITE);
        paintBox.setStyle(Paint.Style.FILL);
        paintBox.setAntiAlias(true);

        paintTouch = new Paint();
        paintTouch.setColor(Color.RED);
        paintTouch.setStyle(Paint.Style.FILL);
        paintTouch.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#4444ff"));
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setStrokeWidth(10);
        paintLine.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setAntiAlias(true);
        paintText.setTextSize(30);
        paintText.setTextAlign(Paint.Align.CENTER);
    }


    private void initializeMetrics() {
        boxWidth = (Options.cols - 1) * Theme.space;
        boxHeight = (Options.rows - 1) * Theme.space;

        screenWidth = G.displayMetrics.widthPixels;
        screenHeight = G.displayMetrics.heightPixels;

        screenWidthHalf = screenWidth / 2;

        offsetX = (screenWidth - boxWidth) / 2;
        offsetY = (screenHeight - boxHeight) / 2;
    }


    public void resetGame() {
        State.playerScores[0] = 0;
        State.playerScores[1] = 0;

        Debug.isDebugMode = false;
        State.isPlayer1 = true;
        State.isGameOver = false;

        lines.clear();
        boxes.clear();

        if (isCpuTurn()) {
            playNext();
        }

        refresh();
    }



    private void refresh() {
        if (boxes.size() == (Options.cols - 1) * (Options.rows - 1)) {
            State.isGameOver = true;
        }

        invalidate();
    }


    private boolean isGameFinished() {
        return boxes.size() == (Options.cols - 1) * (Options.rows - 1);
    }


    private float computeDiff(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    private int getPlayerColor(int playerIndex) {
        return Theme.playerColors[playerIndex - 1];
    }


    private int getPlayerIndex() {
        return State.isPlayer1 ? 1 : 2;
    }


    private int getPlayerScore(int playerIndex) {
        return State.playerScores[playerIndex - 1];
    }


    private int getPlayerType(int playerIndex) {
        return Options.playerTypes[playerIndex - 1];
    }


    private void increasePlayerScore(int playerIndex) {
        State.playerScores[playerIndex - 1]++;
    }


    private String getPlayerName(int playerIndex) {
        return Options.playerNames[playerIndex - 1];
    }


    private boolean isCpuTurn() {
        return getPlayerType(getPlayerIndex()) == Options.TYPE_CPU;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            return;
        }

        drawBackground(canvas);
        drawConnectedLines(canvas);
        drawBoxes(canvas);
        drawDots(canvas);
        drawScores(canvas);
        drawDebugTouchPosition(canvas);
        drawDebugNaming(canvas);

        if (State.isGameOver) {
            drawFinishMessage(canvas);
        }
    }


    private Position getPointPoisition(int i, int j) {
        int x = offsetX + (i * Theme.space);
        int y = offsetY + ((Options.rows - 1 - j) * Theme.space);

        return new Position(x, y);
    }


    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Theme.backgroundColor);
    }


    private void drawConnectedLines(Canvas canvas) {
        for (Line line : lines) {
            drawLine(canvas, line);
        }
    }


    private void drawLine(Canvas canvas, Line line) {
        Position p1 = getPointPoisition(line.i1, line.j1);
        Position p2 = getPointPoisition(line.i2, line.j2);
        paintLine.setColor(getPlayerColor(line.playerIndex));
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paintLine);
    }


    private void drawBoxes(Canvas canvas) {
        for (Box box : boxes) {
            paintBox.setColor(getPlayerColor(box.playerIndex));
            Position boxPos = getPointPoisition(box.i, box.j);
            canvas.drawCircle(boxPos.x + Theme.space / 2, boxPos.y - Theme.space / 2, 30, paintBox);
        }
    }


    private void drawDots(Canvas canvas) {
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position point = getPointPoisition(i, j);
                canvas.drawCircle(point.x, point.y, Theme.radius, paintDot);
            }
        }
    }


    private void drawPlayerScore(Canvas canvas, int playerIndex, int x, int y) {
        paintBox.setColor(getPlayerColor(playerIndex));
        canvas.drawCircle(x, y, 40, paintBox);
        canvas.drawText("" + getPlayerScore(playerIndex), x, y + 10, paintText);
        canvas.drawText(getPlayerName(playerIndex), x, y + 80, paintText);
    }


    private void drawScores(Canvas canvas) {
        drawPlayerScore(canvas, 1, screenWidthHalf - 100, 100);
        drawPlayerScore(canvas, 2, screenWidthHalf + 100, 100);
    }


    private void drawFinishMessage(Canvas canvas) {

        if(State.isGameOver){
            canvas.drawText(getGameFinishMessage(), screenWidthHalf, getHeight() - 100, paintText);
            Snackbar.make(MainActivity.gameView,getGameFinishMessage(), 2000).show();
        }
    }


    private void drawDebugTouchPosition(Canvas canvas) {
        if (!Debug.isDebugMode || !Debug.drawTouch) {
            return;
        }

        canvas.drawCircle(touchX, touchY, 10, paintTouch);
    }


    private void drawDebugNaming(Canvas canvas) {
        if (!Debug.isDebugMode || !Debug.drawDotNames) {
            return;
        }

        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                String name = "" + i + "," + j;
                Position point = getPointPoisition(i, j);
                canvas.drawText(name, point.x, point.y + 50, paintText);
            }
        }
    }


    private String getGameFinishMessage() {
        String message = "";
        if (getPlayerScore(1) == getPlayerScore(2)) {
            message = G.context.getString(R.string.game_is_drawn);
        } else if (getPlayerScore(1) > getPlayerScore(2)) {
            message = " " + getPlayerName(1) + " " + G.context.getString(R.string.won_the_game);
        } else {
            message = " " + getPlayerName(2) + " " + G.context.getString(R.string.won_the_game);
        }

        return message;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (State.isGameOver) {
            return true;
        }

        if (isCpuTurn()) {
            return true;
        }

        touchX = event.getX();
        touchY = event.getY();

        ArrayList<Diff> diffs = getDiffsByOrder();

        Diff diff1 = diffs.get(0);
        Diff diff2 = diffs.get(1);

        connectLine(diff1.point, diff2.point);
        refresh();



        return super.onTouchEvent(event);
    }


    private ArrayList<Diff> getDiffsByOrder() {
        ArrayList<Diff> diffs = new ArrayList<>();

        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position position = getPointPoisition(i, j);
                float diff = computeDiff(touchX, touchY, position.x, position.y);
                diffs.add(new Diff(new Point(i, j), diff));
            }
        }

        Collections.sort(diffs, new Comparator<Diff>() {
                    @Override
                    public int compare(Diff o1, Diff o2) {
                        return o1.diff.compareTo(o2.diff);
                    }
                }
        );

        return diffs;
    }


    private boolean connectLine(Point point1, Point point2) {
        Point firstPoint;
        Point secondPoint;

        Box box1;
        Box box2 = null;

        if (point1.i == point2.i) {
            // vertical
            if (point1.j < point2.j) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }

            box1 = new Box(firstPoint.i, firstPoint.j);

            if (firstPoint.i > 0) {
                box2 = new Box(firstPoint.i - 1, firstPoint.j);
            }
        } else {
            // horizontal
            if (point1.i < point2.i) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }

            box1 = new Box(firstPoint.i, firstPoint.j);

            if (firstPoint.j > 0) {
                box2 = new Box(firstPoint.i, firstPoint.j - 1);
            }
        }

        // if this line is already connected
        for (Line line : lines) {
            if (line.i1 == firstPoint.i && line.j1 == firstPoint.j && line.i2 == secondPoint.i && line.j2 == secondPoint.j) {
                return false;
            }
        }

        // add line to list of connected lines
        Line line = new Line(firstPoint.i, firstPoint.j, secondPoint.i, secondPoint.j, getPlayerIndex());
        lines.add(line);

        // check if player get award
        boolean wonBox1 = checkBox(box1);
        boolean wonBox2 = false;

        if (box2 != null) {
            wonBox2 = checkBox(box2);
        }

        boolean mustSwitchSide = !wonBox1 && !wonBox2;

        // if switching side required
        if (mustSwitchSide) {
            switchSide();
            return true;
        }

        playNext();
        return true;
    }


    private void switchSide() {
        State.isPlayer1 = !State.isPlayer1;
        playNext();
    }


    private void playNext() {
        if (isCpuTurn()) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ai();
                    refresh();
                }
            }, 500);
        }
    }


    private int getRandom(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }


    private void ai() {
        if (isGameFinished()) {
            return;
        }

        while (true) {
            int i = getRandom(0, Options.cols - 2);
            int j = getRandom(0, Options.rows - 2);

            int side = getRandom(0, 3);
            boolean connected = false;
            switch (side) {
                case EDGE_LEFT:
                    connected = connectLeft(i, j);
                    break;
                case EDGE_RIGHT:
                    connected = connectRight(i, j);
                    break;
                case EDGE_TOP:
                    connected = connectTop(i, j);
                    break;
                case EDGE_BOTTOM:
                    connected = connectBottom(i, j);
                    break;
            }

            if (connected) {
                break;
            }
        }
    }


    private boolean connectLeft(int i, int j) {
        return connectLine(new Point(i, j), new Point(i, j + 1));
    }


    private boolean connectRight(int i, int j) {
        return connectLine(new Point(i + 1, j), new Point(i + 1, j + 1));
    }


    private boolean connectTop(int i, int j) {
        return connectLine(new Point(i, j + 1), new Point(i + 1, j + 1));
    }


    private boolean connectBottom(int i, int j) {
        //return connectLine(new Point(i, j), new Point(i + 1, j));
        return connectLine(new Point(i, j), new Point(i + 1, j));
    }


    private boolean hasLeft(int i, int j) {
        for (Line line : lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasRight(int i, int j) {
        for (Line line : lines) {
            if (line.i1 == i + 1 && line.j1 == j && line.i2 == i + 1 && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasTop(int i, int j) {
        for (Line line : lines) {
            if (line.i1 == i && line.j1 == j + 1 && line.i2 == i + 1 && line.j2 == j + 1) {
                return true;
            }
        }

        return false;
    }


    private boolean hasBottom(int i, int j) {
        for (Line line : lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i + 1 && line.j2 == j) {
                return true;
            }
        }

        return false;
    }


    private boolean checkBox(Box box) {
        int i = box.i;
        int j = box.j;

        boolean hasLeft = false;
        boolean hasRight = false;
        boolean hasTop = false;
        boolean hasBottom = false;

        for (Line line : lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j + 1) {
                hasLeft = true;
            }

            if (line.i1 == i + 1 && line.j1 == j && line.i2 == i + 1 && line.j2 == j + 1) {
                hasRight = true;
            }

            if (line.i1 == i && line.j1 == j + 1 && line.i2 == i + 1 && line.j2 == j + 1) {
                hasTop = true;
            }

            if (line.i1 == i && line.j1 == j && line.i2 == i + 1 && line.j2 == j) {
                hasBottom = true;
            }
        }

        boolean isFullConnected = hasLeft && hasRight && hasTop && hasBottom;
        if (isFullConnected) {
            box.playerIndex = getPlayerIndex();
            boxes.add(box);

            increasePlayerScore(box.playerIndex);
            return true;
        }

        return false;
    }

}
