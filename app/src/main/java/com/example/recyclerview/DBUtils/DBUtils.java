package com.example.recyclerview.DBUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBUtils {
    public static final String DATABASE_NAME="notepad_New";//数据库名
    public static final String DATABASE_TABLE="Record";//表名
    public static final int DATABASE_VERSION=1;//数据库版本

    //数据库表中的列名
    public static String NOTEPAD_ID="id";
    //public static String NOTEPAD_TITLE="title";
    public static final String NOTEPAD_CONTENT="content";
    public static final String NOTEPAD_TIME="noteTime";

    /**
     * 获取当前时间
     * @return
     */
    public static final String getTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}