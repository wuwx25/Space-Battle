package com.example.spacebattle;

/**
 * Created by 吴伟鑫 on 2020/7/6.
 */

public class Global {
    static int realW;               // 实际屏幕宽度
    static int realH;               // 实际屏幕高度
    static int virtualW = 1080;     // 虚拟屏幕宽度
    static int virtualH = 1920;     // 虚拟屏幕高度
    static long LOOP_TIME = 50;     // 一帧时长（ms）
    static float v2Rx(float virtualSize){        // virtualToReal  X方向
        return virtualSize * realW /virtualW;
    }
    static float v2Ry(float virtualSize){        // virtualToReal  Y方向
        return virtualSize * realH /virtualH;
    }
    static float r2Vx(float realSize){        // realToVirtual  X方向
        return realSize * virtualW / realW;
    }
    static float r2Vy(float realSize){        // realToVirtual  Y方向
        return realSize * virtualH / realH;
    }
}


