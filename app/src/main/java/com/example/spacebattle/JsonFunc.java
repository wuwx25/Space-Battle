package com.example.spacebattle;

import org.json.JSONObject;

/**
 * Created by 吴伟鑫 on 2020/7/7.
 */

public class JsonFunc {
    public static String sprite2JSON(Sprite sprite) {
        JSONObject obj = new JSONObject();
        if(obj==null) return "";
        try {
            obj.put("type", "spriteinfo");
            obj.put("spName", sprite.spName);
            obj.put("x", sprite.x);
            obj.put("y", sprite.y);
            obj.put("dir",sprite.dir);
            obj.put("hit",sprite.hit);
            obj.put("active",sprite.active);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj.toString();
    }
    public static String bullet2JSON(Bullet bullet) {
        JSONObject obj = new JSONObject();
        if(obj==null) return "";
        try {
            obj.put("type", "bulletinfo");
            obj.put("spName",bullet.spName);
            obj.put("x",bullet.x);
            obj.put("y",bullet.y);
            obj.put("dir",bullet.dir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj.toString();
    }

    public static JSONObject parserJSON2Object(String str) {
        JSONObject obj = null;
        try {
            //System.out.println(str);
            obj = new JSONObject(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }

}

