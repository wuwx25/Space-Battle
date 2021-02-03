package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import static com.example.spacebattle.Global.v2Rx;
import static com.example.spacebattle.Global.v2Ry;

/**
 * Created by 吴伟鑫 on 2020/7/6.
 */
public class Button {
    private Context context;
    final float RADIUS = 80;  // 半径 （虚拟单位）
    final int FONT_SIZE = 48; // 字体大小（虚拟单位）
    String text;               // 按钮文本
    public float centerX;
    public float centerY;
    Paint paint1;
    Paint paint2;
    Button(Context context){
        this.context = context;
        paint1 = new Paint();
        paint1.setColor(Color.argb(128,100,160,100));
        paint2 = new Paint();
        paint2.setColor(Color.argb(128,50,50,50));
        paint2.setTextSize(Global.v2Rx(FONT_SIZE)); //px
    }
    void draw(Canvas canvas){
        if(canvas==null)return;
        canvas.drawCircle(v2Rx(centerX), v2Ry(centerY), RADIUS, paint1);
        paint2.setColor(Color.argb(128,50,50,50));
        paint2.setTextSize(Global.v2Rx(FONT_SIZE));
        canvas.drawText(text,v2Rx(centerX-50), v2Ry(centerY+15),paint2);
    }
    boolean getPressed(float x, float y){
        return Math.sqrt((x-centerX)*(x-centerX)
                +(y-centerY)*(y-centerY))<RADIUS;
    }
}

