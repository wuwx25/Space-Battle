package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by 吴伟鑫 on 2020/7/6.
 */

public class Buttons {
    Button[] arrButtons;
    String[] texts = {"开始","开火","自动","关闭"};
    Buttons(Context context){
        arrButtons = new Button[4];
        for(int i=0; i< arrButtons.length;i++) {
            arrButtons[i] = new Button(context);
            arrButtons[i].text = texts[i];
        }
    }
    // 定位所有按钮
    void pos() {
        arrButtons[0].centerX=220;
        arrButtons[1].centerX=440;
        arrButtons[2].centerX=660;
        arrButtons[3].centerX=880;
        for(int i=0;i<4;i++){
            arrButtons[i].centerY=1820;
        }
    }
    // 绘制所有按钮
    void draw(Canvas canvas) {
        for(int i=0;i<4;i++){
            arrButtons[i].draw(canvas);
        }
    }
    // 判断哪个按钮被点击
    String getPressedButton(float x,float y) {
        for(int i=0;i<4;i++){
            if(arrButtons[i].getPressed(x,y))
                return texts[i];
        }
        return "null";
    }
}



