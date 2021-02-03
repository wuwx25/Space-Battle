package com.example.spacebattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.example.spacebattle.MainActivity.myView;

/**
 * Created by 吴伟鑫 on 2020/7/7.
 */

public class Bullets {
    Context context;
    ConcurrentLinkedQueue<Bullet> lqBullets;
    String myName;
    Bullets(Context context, String myName){
        this.context = context;
        lqBullets = new ConcurrentLinkedQueue<Bullet>();
        this.myName = myName;
    }
    void add(float x,float y,float dir,String spName){
        Bullet bullet=new Bullet(context,spName);
        bullet.x=x;
        bullet.y=y;
        bullet.dir=dir;
        if(spName.equals(myName))
            bullet.me=true;
        else
            bullet.me=false;
        lqBullets.add(bullet);
        if(myView.networkMode&&spName.equals(myName)){
            GameObjects gbs=myView.gameObjects;
            if (!gbs.tcpSocket.isConnected()){
                gbs.tcpSocket.connect(gbs.serverIpAddr, gbs.port);
            }
            if(gbs.recvData==null){
                gbs.recvData = new RecvData(gbs.tcpSocket.socket, gbs);
                Thread thread = new Thread(gbs.recvData);
                thread.start();
            }
            String data = JsonFunc.bullet2JSON(bullet);
            gbs.tcpSocket.sendString(data);
        }
    }
    void draw(Canvas canvas,long loopTime){
        Iterator<Bullet> iterator = lqBullets.iterator();
        while (iterator.hasNext()){
            Bullet cur=iterator.next();
            if(cur.active)
                cur.draw(canvas,loopTime);
        }
    }

    // sprite判断是否被其它精灵发出的子弹击中，击中则返回该子弹，
//   否则，返回null
    Bullet getHitBullet(Sprite sprite){
        if(sprite.hit) return null;
        Iterator<Bullet> it = lqBullets.iterator();
        Bullet bullet;
        while(it.hasNext()){
            bullet=it.next();
            if(!bullet.spName.equals(sprite.spName) && bullet.active) {
                if (bullet.hitSprite(sprite)) {
                    return bullet;
                }
            }
        }
        return null;
    }

    void clear(){
        Iterator<Bullet> it = lqBullets.iterator();
        Bullet bullet;
        while(it.hasNext()){
            bullet=it.next();
            bullet.active=false;
        }
    }
}
