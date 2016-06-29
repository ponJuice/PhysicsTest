package jp.ac.dendai.c.jtp.physicstest.Game.Physics;

import jp.ac.dendai.c.jtp.physicstest.Math.Vector2;

/**
 * Created by Goto on 2016/06/28.
 */
public class Physics2DTemplate {
    public float mass;            //質量
    public Vector2 velocity;      //速度
    public Vector2 position;       //位置
    public float e;                //跳ね返り係数

    public Physics2DTemplate(){
        velocity = new Vector2();
        position  = new Vector2();
    }
}
