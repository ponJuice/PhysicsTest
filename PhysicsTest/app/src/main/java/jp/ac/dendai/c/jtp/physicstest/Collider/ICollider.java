package jp.ac.dendai.c.jtp.physicstest.Collider;

import android.graphics.Canvas;
import android.graphics.Paint;

import jp.ac.dendai.c.jtp.physicstest.Game.Physics.IPhysics2D;

/**
 * Created by Goto on 2016/06/28.
 */
public interface ICollider {
    public boolean isCollision(ICollider col);
    public IPhysics2D getPhysicsObject();
    public float getDeepMagnitude(ICollider col);
    public float getBoundaryCircle();
    public void debugOutlineDraw(Canvas canvas,Paint paint);
}
