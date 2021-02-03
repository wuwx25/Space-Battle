package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 吴伟鑫 on 2020/7/6.
 */

public class Sprites {
    Context context;
    ConcurrentHashMap<String,Sprite> hmSprites;    // 所有精灵(与HashMap用法相同，用于多线程环境)
    String myName;                               // 本精灵的名称
    Sprites(Context context, String myName){
        this.context = context;
        hmSprites = new ConcurrentHashMap<String,Sprite>();
        this.myName = myName;
    }
    void add(String spName, float x, float y, float dir, float step,
             boolean active,boolean ai){
        Sprite sp=new Sprite(context);
        if(spName.equals(myName)) sp.me=true;
        sp.ai=ai;
        sp.spName=spName;
        sp.x=x;
        sp.y=y;
        sp.dir=dir;
        sp.active=active;
        sp.step=step;
        System.out.println("xaxs"+spName);
        hmSprites.put(spName,sp);
    }
    void draw(Canvas canvas, long intl){
        Iterator<String> it = hmSprites.keySet().iterator();
        while(it.hasNext()){
            Sprite sprite= hmSprites.get(it.next());
            if(sprite.active) {
                sprite.draw(canvas,intl);
            }
        }
    }
    Sprite remove(String spName){
        return hmSprites.remove(spName);
    }

    // 查找名字不是spName的一个精灵
    Sprite findOtherSprite(String spName){
        Iterator<String> it = hmSprites.keySet().iterator();
        while(it.hasNext()){
            Sprite sprite= hmSprites.get(it.next());
            if(!sprite.spName.equals(spName)&&!sprite.hit&&sprite.active) {
                return sprite;
            }
        }
        return null;
    }

    void clear(){
        Iterator<String> it = hmSprites.keySet().iterator();
        while(it.hasNext()){
            Sprite sprite= hmSprites.get(it.next());
            sprite.active=false;
        }
    }
    // 查找名字是spName的一个精灵
    Sprite findSprite(String spName){
        Iterator<String> it = hmSprites.keySet().iterator();
        while(it.hasNext()){
            Sprite sprite= hmSprites.get(it.next());
            if(sprite.spName.equals(spName)) {
                return sprite;
            }
        }
        return null;
    }

}
