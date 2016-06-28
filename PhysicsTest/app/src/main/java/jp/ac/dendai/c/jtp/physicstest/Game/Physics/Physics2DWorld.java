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
public class Physics2DWorld implements Runnable{
    private Object lock;
    private volatile LinkedList<IPhysics2D> physics;
    private Vector2 gravity;
    private int maxObject;
    private boolean isAllive;
    private long timeStep;
    private Thread physicsThread;

    public Physics2DWorld(PhysicsWorldTemplate pwt,Object lock){
        this.gravity = new Vector2(pwt.gravityX,pwt.gravityY);
        this.maxObject = pwt.maxObject;
        this.timeStep = pwt.timeStepMilliTime;
        this.lock = lock;

        physics = new LinkedList<IPhysics2D>();
        isAllive = false;

        physicsThread = new Thread(this);

    }

    public LinkedList<IPhysics2D> getPhysicsObjects(){
        return physics;
    }

    public void startWorld(){
        isAllive = true;
        physicsThread.run();
    }

    public void endWorld(){
        isAllive = false;
        physicsThread.interrupt();
        physicsThread = null;
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

    //パイプライン開始
    private void startPipeline(){
        //外力
        gravityProc();

        //位置の更新
        updatePosition();
    }

    //外力の計算
    private void gravityProc(){
        Constant.buffer2D.copy(gravity);
        Constant.buffer2D.scalarMult(((float)Time.getDelta())/1000f);
        synchronized (lock) {
            for (IPhysics2D p : physics) {
                p.addVelocity(Constant.buffer2D);
            }
        }
        Constant.buffer2D.zeroReset();
    }

    //衝突判定

    //拘束の解除

    //位置の更新
    private void updatePosition(){
        synchronized (lock) {
            for (IPhysics2D p : physics) {
                p.updatePosition(Time.getDelta() / 1000f);
                Log.d("updatePosition", "position is " + p.getPosition().toString());
            }
        }
    }

    @Override
    public void run() {
        while(isAllive){
            if(Time.isFreeze())
                continue;
            Time.punctuate();
            //スリープ時間の算出
            long time = timeStep -Time.getDelta();
            Log.d("TimeDelta", " is " + Time.getDelta());
            //時間が余っていたらスリープ
            if(time >= 0) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    break;
                }
            }
            //パイプライン処理
            //startPipeline();
        }
    }
}
