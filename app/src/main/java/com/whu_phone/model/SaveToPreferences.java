package com.whu_phone.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by wuhui on 2017/2/17.
 */

public class SaveToPreferences<T> {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SaveToPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();
    }

    public void saveObject(T object,String objectName) throws Exception {
        if (object instanceof Serializable) {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(baos);
            oos.writeObject(object);
            String s=new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));
            editor.putString(objectName,s);
            editor.commit();
        } else {
            throw new Exception("object must implements serializable");
        }
    }

    public T getObject(String objectName) {
        String s=preferences.getString(objectName,"");
        if (s=="") {
            return null;
        }
        ByteArrayInputStream bais=new ByteArrayInputStream(Base64.decode(s.getBytes(),Base64.DEFAULT));
        T object=null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            object= (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
}
