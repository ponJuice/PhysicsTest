package jp.ac.dendai.c.jtp.physicstest.Game;

import android.content.Context;

import jp.ac.dendai.c.jtp.physicstest.Game.Collider.CircleCollider;
import jp.ac.dendai.c.jtp.physicstest.Game.Collider.ICollider;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2D;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2DTemplate;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2DWorld;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.PhysicsWorldTemplate;

/**
 * Created by Goto on 2016/06/28.
 */
public class Game{
    private Context act;
    private volatile Physics2DWorld p2w;    //物理世界
    private Object lockObject;              //スレッドロック用オブジェクト
    private float width,height;            //画面の高さ、幅
    public Game(Context act,float width,float height,Object lockObject){
        this.act = act;
        this.width = width;
        this.height = height;
        this.lockObject = lockObject;

        //物理世界の設定
        PhysicsWorldTemplate pwt = new PhysicsWorldTemplate();                  //物理世界設定用オブジェクト作成
        pwt.gravityX = 0;
        pwt.gravityY = -0.098f;
        pwt.gravityZ = 0;
        pwt.maxObject = 100;
        pwt.k = 0.1f;
        pwt.timeStepMilliTime = PhysicsWorldTemplate.frameNumToTimeStep(60);    //物理計算の1ステップあたりの許容時間
        p2w = new Physics2DWorld(pwt,lockObject);                                   //物理世界作成

        //物理オブジェクトの設定
        Physics2DTemplate p2t = new Physics2DTemplate();                        //物理オブジェクト設定用オブジェクト作成
        p2t.position.setX(0);
        p2t.position.setY(0);
        p2t.mass = 10;
        p2t.e = 0.5f;
        Physics2D p = new Physics2D(p2t,null);                                  //物理オブジェクト作成
        ICollider c = new CircleCollider(p,10);                                //衝突判定用コライダー作成
        p.setCollider(c);                                                       //物理オブジェクトにコライダーを設定
        //弾     初速の設定
        Constant.buffer2D.zeroReset();
        Constant.buffer2D.setX(0.25f);
        Constant.buffer2D.setY(0.25f);
        p.setVelocity(Constant.buffer2D);                                       //速度設定

        //物理オブジェクトの追加
        p2w.addPhysicsObject(p);

        p2t.position.setX(1000);
        p2t.position.setY(1000);
        p2t.mass = 10;
        p2t.e = 1f;
        p = new Physics2D(p2t,null);
        c = new CircleCollider(p,10);
        p.setCollider(c);
        //モンキー
        Constant.buffer2D.zeroReset();
        Constant.buffer2D.setX(0);
        Constant.buffer2D.setY(0);
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
