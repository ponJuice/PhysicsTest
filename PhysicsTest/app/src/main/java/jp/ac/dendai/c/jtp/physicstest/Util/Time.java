package jp.ac.dendai.c.jtp.physicstest.Util;

/**
 * Created by Goto on 2016/06/28.
 */
public class Time {
    private static long startTimeMillis,endTimeMillis,freezeBuffer;
    private static long totalStartTimeMillis;
    private static boolean isFreeze;
    static {
        startTimeMillis = 0;
        endTimeMillis = 0;
        freezeBuffer = 0;
        totalStartTimeMillis = System.currentTimeMillis();
        isFreeze = false;
    }
    //トータル時間のスタートリセット
    public static void resetTotalStartTime(){
        totalStartTimeMillis = System.currentTimeMillis();
    }
    //時間を区切る
    public static void punctuate(){
        if(isFreeze)
            return;
        startTimeMillis = endTimeMillis;
        endTimeMillis = System.currentTimeMillis();
    }
    //時間カウントを一時停止
    public static void freeze(){
        freezeBuffer = System.currentTimeMillis();
        isFreeze = true;
    }
    //時間カウントを再開
    public static void unFreeze(){
        endTimeMillis += System.currentTimeMillis() - freezeBuffer;
    }

    public static boolean isFreeze(){
        return isFreeze;
    }

    public static long getDeltaMillis(){
        if(startTimeMillis == 0)
            return 0;
        if(isFreeze)
            return freezeBuffer;
        return endTimeMillis - startTimeMillis;
    }

    public static float getDelta(){
        return getDeltaMillis() / 1000f;
    }

    public static long getTotalTimeMillis(){
        return System.currentTimeMillis() - totalStartTimeMillis;
    }
}
