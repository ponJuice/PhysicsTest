package jp.ac.dendai.c.jtp.physicstest.Collider;

import jp.ac.dendai.c.jtp.physicstest.Game.Physics.IPhysics2D;

/**
 * Created by Goto on 2016/06/28.
 */
public abstract class Collider implements ICollider{
    protected IPhysics2D object;
    @Override
    public IPhysics2D getPhysicsObject(){
        return object;
    }
}
