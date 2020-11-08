package com.example.recyclerview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclerview.DBUtils.DBUtils;
import com.example.recyclerview.SQLiteHelper.SQLiteHelper;

import java.io.FileNotFoundException;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{

    public  final int PICTURE = 0;
    public  final int CAMERA = 1;

    ImageView note_back;
    TextView note_time;
    EditText content;
    //EditText title;
    ImageView delete;
    ImageView note_save;
    TextView noteName;
    private SQLiteHelper mSQLiteHelper;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        note_back= findViewById(R.id.note_back);    //返回键
        note_time=findViewById(R.id.tv_time);       //时间
        content= findViewById(R.id.note_content);   //内容
        //title = findViewById(R.id.note_title);      //标题
        delete=findViewById(R.id.delete);           //清空键
        note_save= findViewById(R.id.note_save);    //保存键
        noteName= findViewById(R.id.note_name);     //标题
        //设置点击事件
        note_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        note_save.setOnClickListener(this);
        initData();
    }

    private void insertPic1() {
        SpannableString ss = new SpannableString("pic");
        Drawable d = getResources().getDrawable(R.drawable.save);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.append(ss);
    }
    private void initData() {
        mSQLiteHelper=new SQLiteHelper(this);
        noteName.setText("添加记录");//修改标题
        Intent intent=getIntent();
        if(intent!=null){
            id=intent.getStringExtra("id");
            if(id!=null){
                //显示内容
                noteName.setText("修改记录");
                //title.setText(intent.getStringExtra("title"));
                content.setText(intent.getStringExtra("content"));
                note_time.setText(intent.getStringExtra("time"));
                note_time.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.note_back:    //返回键被点击
                finish();
                break;
            case R.id.delete:       //清空键被点击
                content.setText(" ");
                break;
            case R.id.insertImage:          //插入本地图片按钮被点击
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);    // 跳转系统图库
                //ACTION_GET_CONTENT 文件管理器
                getImage.addCategory(Intent.CATEGORY_OPENABLE);             //增加一个OPENABLE分类，功能：使取得的uri可被resolver解析
                getImage.setType("image/*");                                //设置类型
                startActivityForResult(getImage,PICTURE);                   //启动，回调码为PICTURE
                break;
            case R.id.note_save:    //保存键被点击

                String noteContent =content.getText().toString().trim();    //trim删除首尾空格
                //String noteTitle = title.getText().toString().trim();
                if(id!=null){
                    //修改记录的功能
                    if(noteContent.length()>0){
                        if (mSQLiteHelper.updateData(id,noteContent, DBUtils.getTime())){
                            showToast("修改成功");
                            setResult(2);
                            finish();
                        }else{
                            showToast("修改失败");
                        }
                    } else{
                        showToast("修改的记录内容不能为空");
                    }
                }else{
                    //添加记录的功能
                    if(noteContent.length()>0){
                        if (mSQLiteHelper.insertData(noteContent, DBUtils.getTime())){
                            showToast("保存成功");
                            setResult(2);
                            finish();
                        }else{
                            showToast("保存失败");
                        }
                    } else{
                        showToast("保存的记录内容不能为空");
                    }
                }
                break;
        }
    }

    //展示信息用
    private void showToast(String message) {
        Toast.makeText(RecordActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        ContentResolver resolver =getContentResolver();
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case PICTURE:               //当请求码为PICTURE
                    Uri originalUri=intent.getData();       //通过getData方法获取Uri
                    //定义bitmap
                    Bitmap bitmap=null;
                    Bitmap originalBitmap= null;
                    try {
                            /*
                            通过BitmapFactory的decodeStream方法 生成 Bitmap
                            decodeStream 后参数要求为stream
                            Uri通过openInputStream解析为uri流
                             */
                        originalBitmap= BitmapFactory.decodeStream(resolver.openInputStream(originalUri));    //Uri转化为Bitmap
                        bitmap=resizeImage(originalBitmap,300,300);                   //调整bitmap的尺寸
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(bitmap!=null) {
                        //insertIntoEditText(getBitmapMime(bitmap, originalUri));

                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(RecordActivity.this, bitmap);

                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        SpannableString spannableString = new SpannableString("[local]" + 1 + "[/local]");

                        SpannableString ss = new SpannableString("pic");
                        Drawable d = new BitmapDrawable(bitmap);
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        //ImageSpan imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                        ss.setSpan(imageSpan, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        content.append(ss);
                    }else {
                        Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    /**
     * 功能：调整bitmap图像的尺寸
     * @param originalBitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap resizeImage(Bitmap originalBitmap,  int newWidth, int newHeight) {
        //定义原图宽高
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        //计算宽高缩放率
        float scanleWidth = (float)newWidth/width;
        float scanleHeight = (float)newHeight/height;

        //创建操作图片用的matrix对象 Matrix
        Matrix matrix=new Matrix();

        // 缩放图片动作
        matrix.postScale(scanleWidth,scanleHeight);

        //旋转图片 动作
        //matrix.postRotate(45);

        // 创建新的图片Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap,0,0,width,height,matrix,true);
        return resizedBitmap;
    }

}
