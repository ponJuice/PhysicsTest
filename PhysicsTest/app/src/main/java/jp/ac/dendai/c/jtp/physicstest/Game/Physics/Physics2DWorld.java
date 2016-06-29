package jp.ac.dendai.c.jtp.physicstest.Game.Physics;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import jp.ac.dendai.c.jtp.physicstest.CustomSurfaceView;
import jp.ac.dendai.c.jtp.physicstest.Game.Constant;
import jp.ac.dendai.c.jtp.physicstest.Math.Vector2;
import jp.ac.dendai.c.jtp.physicstest.Util.Time;

/**
 * Created by Goto on 2016/06/28.
 */
public class Physics2DWorld{
    private Object lock;
    private volatile LinkedList<IPhysics2D> physics;
    private Vector2 gravity;
    private float k;                                    //めり込み解消用ばね定数
    private Vector2 bufferA,bufferB,bufferC,velocityBufferA,velocityBufferB;
    private int maxObject;
    private long timeStep;

    public Physics2DWorld(PhysicsWorldTemplate pwt,Object lock){
        this.gravity = new Vector2(pwt.gravityX/1000f,pwt.gravityY/1000f);
        this.maxObject = pwt.maxObject;
        this.timeStep = pwt.timeStepMilliTime;
        this.lock = lock;
        this.k = pwt.k;

        bufferA = new Vector2();
        bufferB = new Vector2();
        bufferC = new Vector2();
        velocityBufferA = new Vector2();
        velocityBufferB = new Vector2();

        physics = new LinkedList<IPhysics2D>();
    }

    public LinkedList<IPhysics2D> getPhysicsObjects(){
        return physics;
    }

    public void freeze(){
        Time.freeze();
    }

    public void unFreeze(){
        Time.unFreeze();
    }

    public boolean isFreeze(){
        return Time.isFreeze();
    }

    public void addPhysicsObject(IPhysics2D object){
        synchronized (lock) {
            physics.add(object);
        }
    }

    public void removePhysicsObject(IPhysics2D object){
        synchronized (lock) {
            physics.remove(object);
        }
    }

    public long getTimeStep(){
        return timeStep;
    }

    //パイプライン開始
    public void startPipeline(){
        synchronized (lock) {
            Time.punctuate();
            //外力
            gravityProc();
            //衝突＆物理計算
            physicsProc();
            //位置の更新
            updatePosition();
        }
    }

    //外力の計算
    private void gravityProc(){
        Constant.buffer2D.copy(gravity);
        Constant.buffer2D.scalarMult(Time.getDelta());
        for (IPhysics2D p : physics) {
            p.addVelocity(Constant.buffer2D);
        }
        Constant.buffer2D.zeroReset();
    }

    //衝突判定 物理計算
    private void physicsProc(){
        //総当たり
        IPhysics2D[] array = (IPhysics2D[])physics.toArray(new IPhysics2D[0]);
        for(int owner = 0;owner < array.length;owner++){
            for(int target = owner+1;target < array.length;target++){

                if(array[owner].getCollider().isCollision(array[target].getCollider())){
                    //衝突
                    //衝突情報をLogに出力
                    Log.d("Collision","info "+array[owner].getPosition().toString()
                    +" : "+array[target].getPosition().toString()
                    +" time : "+Time.getTotalTimeMillis() + "[mill sec]");

                    //貫通深度計算
                    float deep = array[owner].getCollider().getDeepMagnitude(array[target].getCollider());
                    //反発係数計算
                    //本来は二つの物体の衝突前と衝突後の相対速度で出すが、物理シミュレーションの場合は
                    // オブジェクト固有の反発係数を設定し、この二つの掛け算で近似した値を使う。
                    float e = array[owner].getE() * array[target].getE();

                    //owner側速度計算
                    bufferA.copy(array[owner].getVelocity());
                    bufferB.copy(array[target].getVelocity());
                    bufferC.copy(array[target].getVelocity());

                    bufferA.scalarMult(array[owner].getMass());

                    bufferB.sub(array[owner].getVelocity());
                    bufferB.scalarMult(e*array[target].getMass());

                    bufferC.scalarMult(array[target].getMass());

                    bufferA.add(bufferB);
                    bufferA.add(bufferC);

                    bufferA.scalarDiv(array[owner].getMass() + array[target].getMass());
                    velocityBufferA.copy(bufferA);

                    //target側速度計算
                    bufferA.copy(array[target].getVelocity());
                    bufferB.copy(array[owner].getVelocity());
                    bufferC.copy(array[owner].getVelocity());

                    bufferA.scalarMult(array[target].getMass());

                    bufferB.sub(array[target].getVelocity());
                    bufferB.scalarMult(e*array[owner].getMass());

                    bufferC.scalarMult(array[owner].getMass());

                    bufferA.add(bufferB);
                    bufferA.add(bufferC);

                    bufferA.scalarDiv(array[target].getMass() + array[owner].getMass());
                    velocityBufferB.copy(bufferA);

                    //速度更新
                    array[owner].setVelocity(velocityBufferA);
                    array[target].setVelocity(velocityBufferB);

                    //めり込み解消
                    //貫通深度を引っ張り距離としたばねを想定して貫通方向に力を加える
                    //貫通方向の単位ベクトルを取得
                    bufferA.copy(array[owner].getPosition());
                    bufferA.sub(array[target].getPosition());
                    bufferA.normalize();
                    bufferA.scalarMult(k * deep);
                    array[owner].addVelocityImpulse(bufferA);
                    bufferA.scalarMult(-1);
                    array[target].addVelocityImpulse(bufferA);
                }
            }
        }
    }

    //位置の更新
    private void updatePosition(){
        for (IPhysics2D p : physics) {
            p.updatePosition(Time.getDelta());
            //Log.d("updatePosition", "velocity is " + p.getVelocity().toString() + " time:"+Time.getTotalTimeMillis());
        }
    }
}
