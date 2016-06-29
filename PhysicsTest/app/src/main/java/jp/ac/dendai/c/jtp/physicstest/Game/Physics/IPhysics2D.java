package jp.ac.dendai.c.jtp.physicstest.Game.Physics;

import android.graphics.Canvas;
import android.graphics.Paint;

import jp.ac.dendai.c.jtp.physicstest.Game.Collider.ICollider;
import jp.ac.dendai.c.jtp.physicstest.Math.Vector2;

/**
 * Created by Goto on 2016/06/28.
 */
public interface IPhysics2D {
    //力を加える(撃力)
    public void addForceImpulse(Vector2 newton);
    //力を加える(撃力)
    public void addVelocityImpulse(Vector2 velocity);
    //位置を変える
    public void setPosition(Vector2 position);
    //位置を取得する
    public Vector2 getPosition();
    //速度を加える
    public void addVelocity(Vector2 velocity);
    //速度を設定する
    public void setVelocity(Vector2 velocity);
    //速度を取得
    public Vector2 getVelocity();
    //質量を設定する
    public void setMass(float mass);
    //質量を取得する
    public float getMass();
    //反発係数を設定する
    public void setE(float e);
    //反発係数を取得する
    public float getE();
    //位置を速度から更新する
    public void updatePosition(float deltaTime);
    //コライダーを取得する
    public ICollider getCollider();
    //コライダーを設定する
    public void setCollider(ICollider collider);
    //デバッグ用　コライダーのアウトラインを描画する
    public void debugDrawColliderOutline(Canvas canvas,Paint paint);
}
