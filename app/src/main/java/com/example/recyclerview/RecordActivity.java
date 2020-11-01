package com.example.recyclerview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclerview.DBUtils.DBUtils;
import com.example.recyclerview.SQLiteHelper.SQLiteHelper;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{

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
            case R.id.note_save:    //保存键被点击

                String noteContent =content.getText().toString().trim();    //trim删除首尾空格
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
}
