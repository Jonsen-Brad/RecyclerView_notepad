package com.example.recyclerview.SQLiteHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import android.database.Cursor;

import com.example.recyclerview.Bean.NotepadBean;
import com.example.recyclerview.DBUtils.DBUtils;

import java.util.ArrayList;
import java.util.List;



public class SQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;

    public SQLiteHelper(Context context){
        /**
         * 参数列表
         * context
         * file name
         * factory
         * 版本
         */
        super(context, DBUtils.DATABASE_NAME,null,DBUtils.DATABASE_VERSION);
        sqLiteDatabase=this.getWritableDatabase();//以读写方式打开数据库，若磁盘满则出错
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 参数
         * DATABASE_TABLE: Note(表名)
         * NOTEPAD_ID: id（列名）
         * content（列名）
         * time （列名）
         */
        db.execSQL("CREATE TABLE "+DBUtils.DATABASE_TABLE+"("+DBUtils.NOTEPAD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+DBUtils.NOTEPAD_CONTENT+" text, "+DBUtils.NOTEPAD_TIME+" text)");
    }

    /**
     * 升级数据库
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    public boolean insertData(String userContent,String userTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT, userContent);
        contentValues.put(DBUtils.NOTEPAD_TIME, userTime);
        return sqLiteDatabase.insert(DBUtils.DATABASE_TABLE, null, contentValues) > 0;
    }

    //Delete data
    public boolean deleteData(String id){
        String sql=DBUtils.NOTEPAD_ID+"=?";
        String[] contentValuesArray=new String[]{String.valueOf(id)};
        //字符串连接，用参数转换
        /**
         * 参数：
         * 表名
         * sql语句
         * ？字符参数
         */
        return sqLiteDatabase.delete(DBUtils.DATABASE_TABLE,sql,contentValuesArray)>0;
    }

    //Update data
    public boolean updateData(String id,String content,String userYear){
        ContentValues contentValues=new ContentValues();//运用ArrayMap<>
        contentValues.put(DBUtils.NOTEPAD_CONTENT,content);
        contentValues.put(DBUtils.NOTEPAD_TIME,userYear);
        String sql=DBUtils.NOTEPAD_ID+"=?";
        String[] strings=new String[]{id};
        return sqLiteDatabase.update(DBUtils.DATABASE_TABLE,contentValues,sql,strings)>0;
    }

    /**
     * 将数据库中的数据传到List中
     * @return
     */
    public List<NotepadBean> query(){
        List<NotepadBean>list=new ArrayList<NotepadBean>();
        Cursor cursor=sqLiteDatabase.query(DBUtils.DATABASE_TABLE,null,null,
                null,null,null,DBUtils.NOTEPAD_ID+" desc");
        //最后为id降序排列

        if (cursor!=null){
            //循环将所有数据转入list
            while (cursor.moveToNext()){
                NotepadBean noteInfo=new NotepadBean();
                //获取所有数据
                String id=String.valueOf(cursor.getInt(cursor.getColumnIndex(DBUtils.NOTEPAD_ID)));
                String content=cursor.getString(cursor.getColumnIndex(DBUtils.NOTEPAD_CONTENT));
                String time=cursor.getString(cursor.getColumnIndex(DBUtils.NOTEPAD_TIME));

                noteInfo.setId(id);
                noteInfo.setNotepadContent(content);
                noteInfo.setNotepadTime(time);
                //将数据加入list
                list.add(noteInfo);
            }
            cursor.close();
        }
        return list;
    }
}
