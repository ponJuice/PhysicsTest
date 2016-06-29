package jp.ac.dendai.c.jtp.physicstest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import jp.ac.dendai.c.jtp.physicstest.Game.Collider.ICollider;
import jp.ac.dendai.c.jtp.physicstest.Game.Game;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.IPhysics2D;
import jp.ac.dendai.c.jtp.physicstest.Util.Time;

/**
 * Created by Goto on 2016/06/28.
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private Paint paint;
    private SurfaceHolder holder;
    private Thread thread = null;
    private Thread moveThread = null;
    private boolean isAttached = true;
    private final Object lock;
    private Context context;
    private Game game;
    private Debugger debug;

    private float width,height;

    public CustomSurfaceView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);


        lock = new Object();

        paint = new Paint();
        //色を黒に設定
        paint.setColor(Color.argb(255, 0, 0, 0));
        //アンチエイリアスなし
        paint.setAntiAlias(false);
        //塗りつぶしなし（枠線表示）
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        //文字サイズ指定
        paint.setTextSize(48);

        //デバッガ作成
        debug = new Debugger();
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

        //デバッガにオブジェクトを登録
        for(IPhysics2D p : game.getPhysics2DWorld().getPhysicsObjects()) {
            debug.addTarget(p);
        }
        //デバッガの設定
        debug.trajectory = true;
        debug.trajectoryRage = 10;

        //描画用スレッド作成＆開始
        thread = new Thread(new DrawThread());
        thread.start();
        //シミュレート用スレッド作成＆開始
        moveThread = new Thread(new SimulateThread());
        moveThread.start();
        Log.d("surfaceChanged", "changed");
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isAttached = false;
        thread.interrupt();
        thread = null;
    }

    class DrawThread extends Thread{
        @Override
        public void run() {
            //スレッド終了判定
            while(isAttached){
                synchronized (lock) {
                    Canvas canvas = holder.lockCanvas();
                    if(canvas==null)
                        continue;
                    //背景色初期化
                    canvas.drawColor(Color.argb(255, 255, 255, 255));
                    //オブジェクトの軌跡の描画
                    debug.drawTrajectory(canvas,width,height);
                    //すべての物理世界のオブジェクトを描画
                    for(IPhysics2D p : game.getPhysics2DWorld().getPhysicsObjects()) {
                        ICollider c = p.getCollider();
                        //オブジェクトの描画
                        canvas.drawCircle(width - p.getPosition().getX(),height - p.getPosition().getY(),p.getCollider().getBoundaryCircle(),paint);
                        //オブジェクトの速度、質量を表示
                        canvas.drawText(p.getVelocity().toString() + " mass:"+p.getMass(),width - p.getPosition().getX(),height - p.getPosition().getY(),paint);
                    }
                    holder.unlockCanvasAndPost(canvas);
                }

                try {
                    //適当な時間スリープ
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    class SimulateThread extends  Thread{
        @Override
        public void run() {
            while(isAttached){
                if(Time.isFreeze())
                    continue;
                //時間を区切る
                Time.punctuate();
                //スリープ時間の算出
                long time = game.getPhysics2DWorld().getTimeStep() - Time.getDelta();
                //時間が余っていたらスリープ
                if(time > 0) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                //パイプライン処理
                game.startSimulate();
            }
        }
    }
}
