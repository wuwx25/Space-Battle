package com.example.spacebattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.example.spacebattle.Global.v2Rx;
import static com.example.spacebattle.Global.v2Ry;

/**
 * Created by 吴伟鑫 on 2020/7/7.
 */

public class Bullet {
    final float RADIUS = 5;   // 子弹半径
    static long seqnoX = 0;   // 当前子弹的最大序号
    String spName;            // 精灵名称（它发出的子弹）
    long  seqno;              // 子弹序号，和精灵名称一起可以唯一确定一颗子弹
    float x;                  // 子弹x轴定位（left）
    float y;                  // 子弹y轴定位（top）
    float dir;                // 移动方向，单位：degree
    float step = 30;          // 移动步幅 取值1~ Any
    boolean me = false;       // 是否是本玩家发出的子弹
    boolean active = true;    // 是否活动（击中精灵或离开画面则变为非活动）
    Paint paint1;
    Context context;
    Bullet(Context context,String spName){
        this.context = context;
        this.spName=spName;
        this.seqno=seqnoX;
        seqnoX+=1;
        paint1 = new Paint();
        paint1.setColor(Color.RED);
    }
    void draw(Canvas canvas,long loopTime){
        if(canvas==null)return;
        pos(loopTime);
        paint1.setColor(Color.RED);
        canvas.drawCircle(v2Rx(x), v2Ry(y), v2Rx(RADIUS), paint1);
    }
    private void pos(long loopTime){
        float step1 = this.step * loopTime/Global.LOOP_TIME;
        x = x + step1 * (float)Math.cos(dir * Math.PI /180);
        y = y + step1 * (float)Math.sin(dir * Math.PI /180);
        if(x>Global.virtualW||y>Global.virtualH||x<0||y<0) active=false;
    }
    public boolean hitSprite(Sprite sprite){
        float dx=sprite.x + (float)0.5*sprite.WIDTH;
        float dy=sprite.y + (float)0.5*sprite.HEIGHT;
        return Math.sqrt((x-dx)*(x-dx)+(y-dy)*(y-dy))<50;
    }
}
