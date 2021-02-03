package com.example.spacebattle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;

import static android.media.MediaPlayer.create;
import static com.example.spacebattle.Global.r2Vx;
import static com.example.spacebattle.Global.r2Vy;

public class MainActivity extends AppCompatActivity {
    static float stopX,stopY;
    public static MyView myView=null;
    MediaPlayer mp;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder dialogBuilder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView=new MyView((this));
        setContentView(myView);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*Intent intent1 = new Intent();
        intent1.putExtra("music", "summer");
        intent1.setAction("com.example.activity.musicService");
        sendBroadcast(intent1);   //发送广播*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopX = event.getX();
                stopY = event.getY()-248;
                //System.out.println("StopX: "+stopX+"StopY: "+stopY);
                //String s=myView.buttons.getPressedButton(r2Vx(stopX),r2Vy(stopY));
                String s=myView.gameObjects.buttons.getPressedButton(r2Vx(stopX),r2Vy(stopY));
                if(!s.equals("null")) {
                    System.out.println("按下了["+s+"]按钮");
                    if (s.equals("关闭"))
                        MainActivity.this.finish();
                    if(s.equals("开始")){
                        customView();
                    }
                    if(s.equals("开火")){
                        Sprite sprite=myView.gameObjects.mySprite;
                        if(sprite!=null){
                            if(sprite.active&&sprite.hit==false){
                                Bullets bls=myView.gameObjects.bullets;
                                myView.gameObjects.mySprite.shot(bls);
                                try {
                                    mp = create(this, R.raw.bullet);
                                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mp.setLooping(false);
                                    mp.start();
                                    mp.setVolume(14.0f, 14.0f); //声音调不了

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if(s.equals("自动")){
                        if(myView.gameObjects.mySprite!=null){
                            if(myView.gameObjects.mySprite.ai) myView.gameObjects.mySprite.ai=false;
                            else myView.gameObjects.mySprite.ai=true;
                        }
                    }
                }
                else{
                    Sprite sprite=myView.gameObjects.mySprite;
                    if(sprite!=null){
                        if(sprite.hit==false &&sprite.active&&!sprite.ai)
                            sprite.changeDirection(r2Vx(stopX),r2Vy(stopY));
                    }
                }
                break;
        }
        return true;
    }

    public void customView() {
        TableLayout studentForm = (TableLayout) getLayoutInflater()
                .inflate(R.layout.start, null);
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialog = dialogBuilder
                // 设置图标
                .setIcon(R.drawable.sysu)
                // 设置对话框标题
                .setTitle("输入用户")
                // 设置对话框显示的View对象
                .setView(studentForm)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setPositiveButton("联网", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myView.gameObjects.tcpSocket.close();
                        if(myView.gameObjects.recvData!=null){
                            myView.gameObjects.recvData.isRunning=false;
                            myView.gameObjects.recvData=null;
                        }
                        myView.gameObjects.sprites.clear();
                        myView.gameObjects.bullets.clear();
                        EditText editText1=(EditText)alertDialog.findViewById(R.id.name);
                        EditText editText2=(EditText)alertDialog.findViewById(R.id.ip);
                        myView.networkMode=true;
                        myView.gameObjects.networkMode=true;
                        myView.gameObjects.tcpSocket.connect(editText2.getText().toString(), myView.gameObjects.port);
                        myView.gameObjects.drawMysprite(editText1.getText().toString());
                    }
                })
                .setNeutralButton("单机版", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myView.gameObjects.tcpSocket.close();
                        if(myView.gameObjects.recvData!=null){
                            myView.gameObjects.recvData.isRunning=false;
                            myView.gameObjects.recvData=null;
                        }
                        myView.gameObjects.sprites.clear();
                        myView.gameObjects.bullets.clear();
                        EditText editText1=(EditText)alertDialog.findViewById(R.id.name);
                        myView.gameObjects.drawMysprite(editText1.getText().toString());
                        myView.networkMode=false;
                        myView.gameObjects.networkMode=false;
                        myView.gameObjects.sprites.add("other",900,1800,0,10,true,true);
                    }
                })
                .create();             // 创建AlertDialog对象
        alertDialog.show();
        EditText editText=(EditText)alertDialog.findViewById(R.id.name);
        editText.setText(get_random_string());
    }
    String get_random_string(){
        int max=10000,min=1;
        int ran2 = (int) (Math.random()*(max-min)+min);
        return String.valueOf(ran2);
    }
}
