package jp.ac.dendai.c.jtp.physicstest.Collider;

import android.graphics.Canvas;
import android.graphics.Paint;

import jp.ac.dendai.c.jtp.physicstest.Game.Constant;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.IPhysics2D;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2D;

/**
 * Created by Goto on 2016/06/28.
 */
public class CircleCollider extends Collider{
    private float radius;
    public CircleCollider(Physics2D object,float radius){
        this.object = object;
        this.radius = radius;
    }
    @Override
    public boolean isCollision(ICollider col) {
        //二つのオブジェクトの距離ベクトルを計算
        Constant.buffer2D.copy(object.getPosition());
        Constant.buffer2D.sub(col.getPhysicsObject().getPosition());
        //それぞれのオブジェクトの半径の合計を計算
        float sumRadius = radius + col.getBoundaryCircle();
        //もし距離ベクトルの長さが半径の合計よりも小さければ衝突、でなければ衝突しない
        return (Constant.buffer2D.getSqrMagnitude() <= (sumRadius * sumRadius));
    }

    @Override
    public float getBoundaryCircle() {
        return radius;
    }

    @Override
    public void debugOutlineDraw(Canvas canvas,Paint paint) {
        canvas.drawCircle(object.getPosition().getX(),object.getPosition().getY(),radius,paint);
    }
}
