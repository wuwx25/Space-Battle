package com.example.spacebattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import static com.example.spacebattle.Global.v2Rx;
import static com.example.spacebattle.Global.v2Ry;
import static com.example.spacebattle.Global.virtualH;
import static com.example.spacebattle.Global.virtualW;
import static com.example.spacebattle.MainActivity.myView;

/**
 * Created by 吴伟鑫 on 2020/7/6.
 */

public class Sprite {
    final float HEIGHT = 120;   // 精灵高度（虚拟尺寸）。final变量(Java常量)只能赋一次值
    final float WIDTH = 120;    // 精灵宽度（虚拟尺寸）
    final int FONT_SIZE = 24;   // 字体大小（虚拟尺寸）
    final long FRAME_DURATION = 1 * Global.LOOP_TIME;  // 每帧持续时间（ms）
    float step = 10;           // 每个LOOP_TIME的移动步幅（用于控制速度）取值1~ Any(虚拟单位)
    String spName;              // 精灵名称
    float x;                    // 精灵x轴定位
    float y;                    // 精灵y轴定位
    float dir;                  // 移动方向，单位：degree
    boolean me = false;          // 区分自己的精灵还是其它用户的精灵
    boolean active = true;     // 是否活动（可见）。重用（reuse）方式可用
    boolean ai=false;
    boolean hit=false;
    int curFrameIndex;       // 当前帧号，取值0~7
    long frameDuration = 0;   // 当前帧已显示时间
    final float AI_MOVE_GAP = 30* Global.LOOP_TIME;    // AI：自动转向的时间间隔(ms)
    float aiMoveDelay = 0;      // 自动转向的累计时间（到AI_MOVE_GAP时移动方向）
    final float AI_SHOT_GAP = 8 * Global.LOOP_TIME;     // AI：自动发射的时间间隔(ms)
    float aiShotDelay = 0;
    final float DEAD_TIME=100* Global.LOOP_TIME;
    float deadTime=0;

    int[] frames = { R.drawable.sprite1,R.drawable.sprite2,R.drawable.sprite3,
            R.drawable.sprite4,R.drawable.sprite5,R.drawable.sprite6,
            R.drawable.sprite7,R.drawable.sprite8
    };
    Paint paint1;   // 精灵图像画笔
    Paint paint2;   // 精灵文字画笔
    Context context;
    Sprite(Context context){
        this.context = context;
        paint1 = new Paint();
        paint2 = new Paint();
        paint2.setColor(Color.argb(60,50,50,50));
        paint2.setTextSize(Global.v2Rx(FONT_SIZE));
        curFrameIndex = 0;
    }
    // 绘制精灵：先计算下一帧的索引，计算精灵的位置，再绘制精灵
    void draw(Canvas canvas, long loopTime){
        if(canvas==null) return;
        if(active){
            if(hit) drop(loopTime);
            else {
                if(myView.networkMode){
                    if(me) pos(loopTime);
                }
                else pos(loopTime);
            }
            if(!me&&myView.networkMode) deadCalc(loopTime);
            nextFrame(loopTime);
            canvas.drawText(spName,v2Rx(x),v2Ry(y),paint2);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),frames[curFrameIndex]);
            bitmap=rotateSprite(bitmap,dir);
            Rect src=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            Rect des=new Rect((int)v2Rx(x),(int)v2Ry(y),(int)v2Rx(x+WIDTH),(int)v2Ry(y+HEIGHT));

            paint1=new Paint();
            ColorMatrix colorMatrix1 = new ColorMatrix(new float[]{
                    0.5F, 0, 0, 0, 0,
                    0, 0.7F, 0, 0, 0,
                    0, 0, 0.2F, 0, 0,
                    0, 0, 0, 1, 0
            });
            ColorMatrix colorMatrix2 = new ColorMatrix(new float[]{
                    0.8F, 0, 0, 0, 0,
                    0, 0.1F, 0, 0, 0,
                    0, 0, 0.2F, 0, 0,
                    0, 0, 0, 1, 0
            });
            if(me) paint1.setColorFilter(new ColorMatrixColorFilter(colorMatrix1));
            if(hit) paint1.setColorFilter(new ColorMatrixColorFilter(colorMatrix2));
            canvas.drawBitmap(bitmap,src,des,paint1);
        }
    }
    // 设置精灵图像画笔和文字画笔
    void setImagePaint(){
    }
    // 通过累计frameDuration计算下一个帧的索引
    private void nextFrame(long loopTime){
        frameDuration+=loopTime;
        if(frameDuration>=FRAME_DURATION){
            curFrameIndex+=1;
            if(curFrameIndex==8)
                curFrameIndex=0;
            frameDuration=0;
        }
    }

    private void pos(long loopTime){
        float step1 = this.step * loopTime/Global.LOOP_TIME;
        x = x + step1 * (float)Math.cos(dir * Math.PI /180);
        y = y + step1 * (float)Math.sin(dir * Math.PI /180);
        if(x<0) x=0;
        if(x+WIDTH>virtualW) x=virtualW-WIDTH;
        if(y<0) y=0;
        if(y+HEIGHT>virtualH) y=virtualH-HEIGHT;
    }

    private void drop(long loopTime){
        float step1 = 4*this.step * loopTime/Global.LOOP_TIME;
        y=y+step1;
        if(y>virtualH) active=false;
    }

    public void changeDirection(float n_x,float n_y){
        dir=getDirection(x,y,n_x,n_y);
    }

    // 给出两个点(left,top)和（newLeft,newTop），计算出方向（degree）
    float getDirection(float left,float top, float newLeft, float newTop){
        float dx = newLeft - left;
        float dy = newTop - top;
        double dist = Math.sqrt(dx*dx+dy*dy);
        float theta=0;
        if((int)dist!=0) {
            theta = (float) Math.asin(Math.abs(dy) / dist) * 180.0f / (float)Math.PI;
        }
        else {
            theta = 0;
        }
        if(dx<=0 && dy>=0){
            theta= 180-theta;
        }
        if(dx<=0 && dy<=0){
            theta= 180+theta;
        }
        if(dx>=0 && dy<=0){
            theta= 360-theta;
        }
        return theta;
    }
    // 根据方向（度数）旋转精灵
    Bitmap rotateSprite(Bitmap bitmap, float degree){
        float deg = degree;
        Matrix matrix = new Matrix();

        if(degree>=0 && degree<=90) {
        }
        if(degree>=90 && degree<=180) {
            matrix.postTranslate(-bitmap.getWidth(),0);
            matrix.postScale(-1, 1);    //镜像水平翻转. 垂直(1, -1)
            deg = degree-180;
        }
        if(degree>=180 && degree<=270) {
            matrix.postTranslate(-bitmap.getWidth(),0);
            matrix.postScale(-1, 1);    //镜像水平翻转. 垂直(1, -1)
            deg = degree-180;
        }
        if(degree>=270 && degree<=360) {
            deg = degree-360;
        }
        Bitmap tempBmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBmp);
        matrix.postRotate(deg,bitmap.getWidth()/2,bitmap.getHeight()/2);    //旋转-deg度
        canvas.drawBitmap(bitmap,matrix,null);
        return tempBmp;
    }

    // 射击（产生新子弹）,速度是精灵移动速度的3倍
    void shot(Bullets bullets){
        bullets.add(x+WIDTH/2,y+HEIGHT/2,dir,spName);
    }

    //计算自动转向和发射子弹的累计时间
    void aiCalc(Sprite objSprite, long loopTime){
        if(objSprite!=null){
            aiMoveDelay+=loopTime;
            aiShotDelay+=loopTime;
            if(aiShotDelay>=AI_SHOT_GAP)
                aiShotDelay=0;
            if(aiMoveDelay>=AI_MOVE_GAP){
                dir=getAiDirect(x,y,objSprite.x,objSprite.y,objSprite.dir,objSprite.step);
                aiMoveDelay=0;
            }
        }

    }
    //根据目标的当前位置、方向和速度进行计算从位置（myleft,mytop）的方向。
    private float getAiDirect(float myleft,float mytop,float objleft,float objtop,
                              float objDirect,float objSpeed){
        float aidir=0;
        double dis= (float) Math.sqrt((myleft-objleft)*(myleft-objleft)+(mytop-objtop)*(mytop-objtop));
        dis=dis/(2*Math.sqrt(2));
        objleft+= dis * (float)Math.cos(objDirect * Math.PI /180);
        objtop+= dis*(float)Math.sin(objDirect * Math.PI /180);
        aidir=getDirection(myleft,mytop,objleft,objtop);
        return aidir;
    }

    // 计算累积更新时间
    void deadCalc(long loopTime){
        deadTime += loopTime;
        if(deadTime > DEAD_TIME){
            active = false;
        }
    }
}

