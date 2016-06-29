package jp.ac.dendai.c.jtp.physicstest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.Iterator;
import java.util.LinkedList;

import jp.ac.dendai.c.jtp.physicstest.Game.Physics.IPhysics2D;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2DWorld;

/**
 * Created by Goto on 2016/06/29.
 */
public class Debugger {
    public boolean trajectory;  //物体の軌跡
    public int trajectoryRage;  //何フレームに一度更新するか
    public boolean objectInfo;  //物理世界の物体の情報を描画するか
    public boolean worldInfo;   //物理世界の情報を描画するか
    public boolean timeInfo;    //時間表示
    public LinkedList<Package> line;
    public Paint paint;
    private int frameCount;

    public Debugger(){
        paint = new Paint();
        paint.setStrokeWidth(5f);
        line = new LinkedList<>();
    }

    private void DrawTrajectory(Canvas canvas,float width,float height){
        Iterator<Point> pointIterator;
        Point start,end;
        for(Package pa : line){
            pointIterator = pa.getLines().iterator();
            if(!pointIterator.hasNext())
                break;
            start = pointIterator.next();
            while(pointIterator.hasNext()){
                end = pointIterator.next();
                canvas.drawLine(width - start.x,height - start.y,width - end.x,height - end.y,paint);
                start = end;
            }
        }
    }

    public void drawTrajectory(Canvas canvas,float width,float height){
        if(!trajectory)
            return;
        if(frameCount < trajectoryRage) {
            //軌跡の再描画
            DrawTrajectory(canvas,width,height);
            frameCount++;
            return;
        }

        //軌跡のポイント追加
        for(Package p : line){
            p.update();
        }

        //再描画
        DrawTrajectory(canvas,width,height);

        frameCount = 0;
    }

    public void addTarget(IPhysics2D target){
        line.add(new Package(target));
    }

    class Package{
        private IPhysics2D target;
        private LinkedList<Point> lines;
        public Package(IPhysics2D target){
            this.target = target;
            lines = new LinkedList<>();
            addPoint(target.getPosition().getX(),target.getPosition().getY());
        }
        public void addPoint(float x,float y){
            lines.add(new Point(x,y));
        }
        public LinkedList<Point> getLines(){
            return lines;
        }
        public void update(){
            lines.add(new Point(target.getPosition().getX(),target.getPosition().getY()));
        }
    }
    class Point{
        public float x,y;
        public Point(float x,float y){
            this.x = x;
            this.y = y;
        }
    }
}
