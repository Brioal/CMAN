package com.brioal.cman.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.brioal.cman.db.DBHelper;
import com.brioal.cman.entity.DeviceEntity;
import com.brioal.cman.interfaces.OnListLoadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * DataLoader Util
 * Created by Brioal on 2016/9/11.
 */
public class DataLoader {
    private Context mContext;
    private DBHelper mDBHelper;
    private static DataLoader sLoader;

    public DataLoader(Context context) {
        mContext = context;
        mDBHelper = new DBHelper(mContext, "Device.db3", null, 1);
    }

    public static DataLoader newInstance(Context context) {
        if (sLoader == null) {
            sLoader = new DataLoader(context);
        }
        return sLoader;
    }

    //添加设备信息到本地
    public boolean addDeviceInfo(DeviceEntity entity) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        boolean succeed = false;
        try {
            db.execSQL("insert into DeviceInfo values (? , ? , ? , ? , ?)", new Object[]{
                    entity.getTime(),
                    entity.getTitle(),
                    entity.getSSID(),
                    entity.getMac(),
                    entity.getPass()
            });
            succeed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            succeed = false;
        }
        return succeed;
    }

    //根据SSID删除设备信息
    public boolean delDeviceInfo(String ssid) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        boolean succeed = false;
        try {
            db.execSQL("delete from DeviceInfo where mSSID = ?", new Object[]{
                    ssid
            });
            succeed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            succeed = false;
        }
        return succeed;
    }

    //获取本地的设备信息
    public void getDeviceInfoLocal(OnListLoadListener listener) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        List<DeviceEntity> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from DeviceInfo", null);
            while (cursor.moveToNext()) {
                DeviceEntity entity = new DeviceEntity(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                list.add(entity);
            }
            listener.succeed(list);
        } catch (Exception e) {
            e.printStackTrace();
            listener.failed("加载本地设备失败");
        } finally {
            if (cursor == null) {
                return;
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }
    }

    //根据SSID修改名称
    public void changeDeviceTitle(String ssid, String newTitle) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        try {
            db.execSQL("update DeviceInfo set mTitle =  ? where mSSID = ? ", new Object[]{
                    newTitle,
                    ssid
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //根据SSID修改密码
    public void changePass(String ssid, String newPass) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        try {
            db.execSQL("update DeviceInfo set mPass =  ? where mSSID = ? ", new Object[]{
                    newPass,
                    ssid
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取正确的密码
    public String getPass(String ssid, String pass) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select mPass from DeviceInfo where mSSID = ?", new String[]{
                    ssid
            });
            while (cursor.moveToNext()) {
                String rightPass = cursor.getString(0);
                return rightPass;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return pass;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return pass;
    }
}
