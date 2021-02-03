package com.example.spacebattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by 吴伟鑫 on 2020/7/6.
 */

public class GameObjects {
    private Context context;
    private SurfaceHolder holder;
    Buttons buttons;
    Sprites sprites=null;
    Sprite mySprite;  // 本玩家精灵
    String myName="";    // 本玩家精灵的名字
    Bullets bullets=null;
    boolean networkMode = true;

    public String serverIpAddr="192.168.0.4";
    public int port = 50000;
    public TcpSocket tcpSocket;
    RecvData recvData=null;

    GameObjects(Context context){
        this.context = context;
        tcpSocket = new TcpSocket();
        buttons = new Buttons(context);
        buttons.pos();
        sprites=new Sprites(context,"");
        bullets=new Bullets(context,"");
    }
    void draw(Canvas canvas,long loopTime){
        Iterator<String> it = sprites.hmSprites.keySet().iterator();
        while(it.hasNext()){
            Sprite sprite= sprites.hmSprites.get(it.next());
            if(sprite.active==false||sprite.hit) continue;
            if(sprite.ai) handleAI(sprite.spName,loopTime);
            Bullet bullet=bullets.getHitBullet(sprite);
            if(bullet!=null){
                bullet.active=false;
                sprite.hit=true;
                sprite.dir=270;
            }
        }
        drawBackground(canvas);
        buttons.draw(canvas);
        sprites.draw(canvas,loopTime);
        bullets.draw(canvas,loopTime);

        if(networkMode&&!myName.equals("")){
            if(mySprite.active){
                if (!tcpSocket.isConnected()){
                    tcpSocket.connect(serverIpAddr, port);
                }
                if(recvData==null){
                    recvData = new RecvData(tcpSocket.socket, this);
                    Thread thread = new Thread(recvData);
                    thread.start();
                }
                String data = JsonFunc.sprite2JSON(mySprite);
                tcpSocket.sendString(data);
            }
        }
    }
    void drawBackground(Canvas canvas){
        if(canvas==null) return;
        Paint p = new Paint(); //创建画笔
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.cloud);
        Matrix matrix = new Matrix();
        matrix.setScale(1,(float)1.1,0,0);
        canvas.drawBitmap(bitmap,matrix,p);
    }
    void drawMysprite(String spName){
        myName=spName;
        sprites.myName=myName;
        bullets.myName=myName;
        sprites.add(sprites.myName,0,20,0,10,true,false);
        mySprite=sprites.hmSprites.get(sprites.myName);
        System.out.println(mySprite.spName);

    }
    String getPressedButton(float x,float y){
        return buttons.getPressedButton(x,y);
    }
    // aiSpriteName的Sprite找一个其它Sprite作为目标
    //  利用它的aiCalc()方法进行ai计算
    void handleAI(String aiSpriteName,long loopTime){
        Sprite sprite=sprites.hmSprites.get(aiSpriteName);
        if(sprite!=null){
            if(sprite.active&&sprite.hit==false){
                Sprite other=sprites.findOtherSprite(sprite.spName);
                if(other==null) return;
                sprite.aiCalc(other,loopTime);
                if(sprite.aiShotDelay==0)
                    sprite.shot(bullets);
            }
        }
    }
    void handleRecvData(String data) {
        JSONObject obj = JsonFunc.parserJSON2Object(data);
        if(obj==null) return;
        String type = obj.optString("type");
        if (type.equals("spriteinfo")) {
            if(obj.optString("spName").equals(myName)){
                return;
            }
            Sprite sprite=sprites.findSprite(obj.optString("spName"));
            if(sprite!=null){
                sprite.x=(float)obj.optDouble("x");
                sprite.y=(float)obj.optDouble("y");
                sprite.dir=(float)obj.optDouble("dir");
                sprite.active=obj.optBoolean("active");
                sprite.hit=obj.optBoolean("hit");
                sprite.deadTime=0;
            }
            else{
                sprite=new Sprite(context);
                sprite.me=false;
                sprite.spName=obj.optString("spName");
                sprite.x=(float)obj.optDouble("x");
                sprite.y=(float)obj.optDouble("y");
                sprite.dir=(float)obj.optDouble("dir");
                sprite.active=obj.optBoolean("active");
                sprite.hit=obj.optBoolean("hit");
                sprites.hmSprites.put(sprite.spName,sprite);
            }
        }
        else if(type.equals("bulletinfo")){
            if(obj.optString("spName").equals(myName)) return;
            else{
                bullets.add((float)obj.optDouble("x"),(float)obj.optDouble("y"),(float)obj.optDouble("dir"),obj.optString("spName"));
            }
        }
        else{
            System.out.println("收到数据的类型错误！");
        }
    }
}

