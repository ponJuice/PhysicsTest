package jp.ac.dendai.c.jtp.physicstest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.Iterator;
import java.util.LinkedList;

import jp.ac.dendai.c.jtp.physicstest.Game.Physics.IPhysics2D;
import jp.ac.dendai.c.jtp.physicstest.Game.Physics.Physics2DWorld;
import jp.ac.dendai.c.jtp.physicstest.Util.Time;

/**
 * Created by Goto on 2016/06/29.
 */
public class Debugger {
    public boolean trajectory;  //物体の軌跡
    public int trajectoryRage;  //何フレームに一度更新するか
    public boolean objectInfo;  //物理世界の物体の情報を描画するか
    public boolean worldInfo;   //物理世界の情報を描画するか
    public boolean screenInfo;  //画面の情報を表示するか
    public boolean timeInfo;    //時間表示
    public LinkedList<Package> line;
    public Paint paint;
    private int frameCount;
    private int fpsFrameCount;
    private float fps;

    private long startTime,endTime;

    public Debugger(){
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setTextSize(50f);
        line = new LinkedList<>();
        startTime = 0;
        endTime = 0;
        frameCount = 0;
    }

    public void FPSCount(){
        endTime = System.currentTimeMillis();
        if(endTime - startTime >= 1000){
            fps = fpsFrameCount;
            fpsFrameCount = 0;
            startTime = endTime;
        }
        else{
            fpsFrameCount++;
        }
    }

    public float getFps(){
        return fps;
    }

    public void drawScreenInfo(Canvas canvas,float x,float y,float width,float height,int meterPerPixel){
        if(!screenInfo)
            return;
        canvas.drawText("--- Screen Info ---",x,y,paint);
        canvas.drawText("FPS : "+ getFps(),x,y+paint.getTextSize(),paint);
        canvas.drawText("Screen Size : ("+width+","+height+")",x,y+paint.getTextSize()*2,paint);
        canvas.drawText("Scale :"+ meterPerPixel + " [meters per pixel]",x,y + paint.getTextSize()*3,paint);
    }

    public void drawWorldInfo(Canvas canvas,Physics2DWorld p2w,float x,float y){
        if(!worldInfo)
            return ;
        y += paint.getTextSize();
        String[] info = p2w.getWorldInfoArray();
        canvas.drawText("--- World Info---",x,y,paint);
        for(int n = 0;n < info.length;n++) {
            canvas.drawText(info[n],x,y + paint.getTextSize()*(n+1),paint);
        }
    }

    public void drawTimeInfo(Canvas canvas,float x,float y){
        if(!timeInfo)
            return;
        canvas.drawText(String.format("Total time :%d",Time.getTotalTimeMillis()),x,y,paint);
    }

    private void DrawTrajectory(Canvas canvas,float width,float height,float scale){
        Iterator<Point> pointIterator;
        Point start,end;
        for(Package pa : line){
            pointIterator = pa.getLines().iterator();
            if(!pointIterator.hasNext())
                break;
            start = pointIterator.next();
            while(pointIterator.hasNext()){
                end = pointIterator.next();
                canvas.drawLine(width - start.x/scale,height - start.y/scale,width - end.x/scale,height - end.y/scale,paint);
                start = end;
            }
        }
    }

    public void drawTrajectory(Canvas canvas,float width,float height,float scale){
        if(!trajectory)
            return;
        if(frameCount < trajectoryRage) {
            //軌跡の再描画
            DrawTrajectory(canvas,width,height,scale);
            frameCount++;
            return;
        }

        //軌跡のポイント追加
        for(Package p : line){
            p.update();
        }

        //再描画
        DrawTrajectory(canvas,width,height,scale);

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
