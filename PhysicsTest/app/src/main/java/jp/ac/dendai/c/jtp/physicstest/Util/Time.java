package jp.ac.dendai.c.jtp.physicstest.Util;

/**
 * Created by Goto on 2016/06/28.
 */
public class Time {
    private static long startTimeMillis,endTimeMillis,freezeBuffer;
    private static long totalTimeMillis;
    private static boolean isFreeze;
    static {
        startTimeMillis = 0;
        endTimeMillis = 0;
        freezeBuffer = 0;
        totalTimeMillis = 0;
        isFreeze = false;
    }
    //時間を区切る
    public static void punctuate(){
        if(isFreeze)
            return;
        startTimeMillis = endTimeMillis;
        endTimeMillis = System.currentTimeMillis();
        totalTimeMillis += getDelta();
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

    public static long getDelta(){
        if(startTimeMillis == 0)
            return 0;
        if(isFreeze)
            return freezeBuffer;
        return endTimeMillis - startTimeMillis;
    }

    public static long getTotalTimeMillis(){
        return totalTimeMillis;
    }
}
