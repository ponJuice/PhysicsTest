package jp.ac.dendai.c.jtp.physicstest.Game.Physics;

import android.util.Log;

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
    private Vector2 bufferA,bufferB,bufferC;
    private int maxObject;
    private long timeStep;
    private Thread physicsThread;

    public Physics2DWorld(PhysicsWorldTemplate pwt,Object lock){
        this.gravity = new Vector2(pwt.gravityX/1000f,pwt.gravityY/1000f);
        this.maxObject = pwt.maxObject;
        this.timeStep = pwt.timeStepMilliTime;
        this.lock = lock;

        bufferA = new Vector2();
        bufferB = new Vector2();
        bufferC = new Vector2();

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
        Time.punctuate();
        //外力
        gravityProc();
        //衝突＆物理計算
        physicsProc();
        //位置の更新
        updatePosition();
    }

    //外力の計算
    private void gravityProc(){
        Constant.buffer2D.copy(gravity);
        Constant.buffer2D.scalarMult(Time.getDelta());
        synchronized (lock) {
            for (IPhysics2D p : physics) {
                p.addVelocity(Constant.buffer2D);
            }
        }
        Constant.buffer2D.zeroReset();
    }

    //衝突判定 物理計算
    private void physicsProc(){
        //総当たり
        for(IPhysics2D owner : physics){
            for(IPhysics2D target : physics){
                if(owner == target)
                    continue;
                if(owner.getCollider().isCollision(target.getCollider())){
                    //衝突!!
                    //貫通深度計算
                    float deep = owner.getCollider().getDeepMagnitude(target.getCollider());
                    //反発係数計算
                    //本来は二つの物体の衝突前と衝突後の相対速度で出すが、物理シミュレーションの場合は
                    // オブジェクト固有の反発係数を設定し、この二つの掛け算で近似した値を使う。
                    float e = owner.getE() * target.getE();
                    //速度計算
                    bufferA.copy(owner.getVelocity());
                    bufferB.copy(target.getVelocity());
                    bufferC.copy(target.getVelocity());

                    bufferA.scalarMult(owner.getMass());

                    bufferB.sub(owner.getVelocity());
                    bufferB.scalarMult(e*target.getMass());

                    bufferC.scalarMult(target.getMass());

                    bufferA.add(bufferB);
                    bufferA.add(bufferC);

                    bufferA.scalarDiv(owner.getMass() + target.getMass());
                    bufferB.copy(bufferA);
                    bufferB.normalize();
                    bufferB.scalarMult((deep/1000f)*(target.getMass()/owner.getMass()));

                    owner.addVelocityImpulse(bufferB);

                    owner.addVelocity(bufferA);
                }
            }
        }
    }

    //位置の更新
    private void updatePosition(){
        synchronized (lock) {
            for (IPhysics2D p : physics) {
                p.updatePosition(Time.getDelta());
                Log.d("updatePosition", "velocity is " + p.getVelocity().toString() + " time:"+Time.getTotalTimeMillis());
            }
        }
    }
}
