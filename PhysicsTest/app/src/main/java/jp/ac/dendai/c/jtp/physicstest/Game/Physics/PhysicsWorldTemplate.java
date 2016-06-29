package jp.ac.dendai.c.jtp.physicstest.Game.Physics;

/**
 * Created by Goto on 2016/06/28.
 */
public class PhysicsWorldTemplate {
    public float gravityX,gravityY,gravityZ,k;
    public long timeStepMilliTime;
    public int maxObject;

    //一秒間のステップ数からステップの時間を算出
    public static long frameNumToTimeStep(int num){
        return 1000/num;
    }
}
