package jp.ac.dendai.c.jtp.physicstest.Game;

import android.app.Activity;
import android.content.Context;

import java.util.Objects;

import jp.ac.dendai.c.jtp.physicstest.Collider.CircleCollider;
import jp.ac.dendai.c.jtp.physicstest.Collider.ICollider;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2D;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2DTemplate;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2DWorld;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.PhysicsWorldTemplate;

/**
 * Created by Goto on 2016/06/28.
 */
public class Game{
    private Context act;
    private volatile Physics2DWorld p2w;
    private Object lockObject;
    private float width,height;
    public Game(Context act,float width,float height,Object lockObject){
        this.act = act;
        this.width = width;
        this.height = height;
        this.lockObject = lockObject;

        //物理世界の設定
        PhysicsWorldTemplate pwt = new PhysicsWorldTemplate();
        pwt.gravityX = 0;
        pwt.gravityY = -0.098f;
        pwt.gravityZ = 0;
        pwt.maxObject = 100;
        pwt.timeStepMilliTime = PhysicsWorldTemplate.frameNumToTimeStep(60);
        p2w = new Physics2DWorld(pwt,lockObject);

        //物理オブジェクトの設定
        Physics2DTemplate p2t = new Physics2DTemplate();
        p2t.position.setX(width / 2f);
        p2t.position.setY(height);
        p2t.mass = 1;
        p2t.e = 1f;
        Physics2D p = new Physics2D(p2t,null);
        ICollider c = new CircleCollider(p,50);
        p.setCollider(c);

        //物理オブジェクトの追加
        p2w.addPhysicsObject(p);

        p2t.position.setX(width/2f);
        p2t.position.setY(0);
        p2t.mass = 10;
        p2t.e = 1f;
        p = new Physics2D(p2t,null);
        c = new CircleCollider(p,50);
        p.setCollider(c);
        //上向きの力を与える(初速度)
        Constant.buffer2D.zeroReset();
        Constant.buffer2D.setX(0);
        Constant.buffer2D.setY(0.5f);
        p.setVelocity(Constant.buffer2D);

        //物理オブジェクトの追加
        p2w.addPhysicsObject(p);
    }
    public void startSimulate(){
        p2w.startPipeline();
    }
    public Physics2DWorld getPhysics2DWorld(){
        return p2w;
    }
}
