package jp.ac.dendai.c.jtp.physicstest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Objects;

import jp.ac.dendai.c.jtp.physicstest.Collider.ICollider;
import jp.ac.dendai.c.jtp.physicstest.Game.Game;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.IPhysics2D;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2DWorld;

/**
 * Created by Goto on 2016/06/28.
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback ,Runnable{
    private Paint paint;
    private SurfaceHolder holder;
    private Thread thread = null;
    private boolean isAttached = true;
    private final Object lock;
    private Context context;
    private Game game;

    private float width,height;

    public CustomSurfaceView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);

        lock = new Object();

        paint = new Paint();
        //色を黒に設定
        paint.setColor(Color.argb(255,0,0,0));
        //アンチエイリアスなし
        paint.setAntiAlias(false);
        //塗りつぶしなし（枠線表示）
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        //ゲームの初期化
        game = new Game(context,width,height,lock);
        //描画用スレッド作成＆開始
        thread = new Thread(this);
        thread.start();
        Log.d("surfaceChanged", "changed");

        game.simulateStart();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isAttached = false;
        thread.interrupt();
        thread = null;
    }

    @Override
    public void run() {
        while(isAttached){
            //synchronized (lock) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.argb(255, 255, 255, 255));
                /*for(IPhysics2D p : game.getPhysics2DWorld().getPhysicsObjects()) {
                    ICollider c = p.getCollider();
                    Log.d("CustomSurfaceView", "("+p.getPosition().getX()+","+p.getPosition().getY()+") radius "+c.getBoundaryCircle());
                    canvas.drawCircle(p.getPosition().getX(),p.getPosition().getY(),p.getCollider().getBoundaryCircle(),paint);
                }*/
            canvas.drawCircle(0,0,100,paint);
                holder.unlockCanvasAndPost(canvas);
            //}

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
