package project.game;

import ir.skydevelopers.app.project.R;
import project.G;

public class Options {
    public static final int TYPE_CPU = 0;
    public static final int TYPE_PLAYER = 1;

    public static int cols = 6;
    public static int rows = 6;

    public static String[] playerNames = new String[]{G.resources.getString(R.string.player_1), G.resources.getString(R.string.player_2)};
    public static int[] playerTypes = new int[]{TYPE_CPU, TYPE_PLAYER};
    //private static int[] playerTypes = new int[]{TYPE_PLAYER, TYPE_PLAYER};
}
