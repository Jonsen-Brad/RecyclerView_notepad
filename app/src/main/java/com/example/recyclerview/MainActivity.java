package com.example.recyclerview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.recyclerview.Bean.NotepadBean;
import com.example.recyclerview.SQLiteHelper.SQLiteHelper;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    //定义全局变量
    private RecyclerView mRecyclerView;     //recyclerView
    private SQLiteHelper mSQLiteHelper;     //声明自定义的数据库操作类
    private MyRecyclerViewAdapter mAdapter; //适配器
    private List<NotepadBean> data;         //list
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashReport.initCrashReport(getApplicationContext(), "629f8fa455", true);

        mRecyclerView =findViewById(R.id.recycler_view);
        imageView = findViewById(R.id.add);
        //线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //横向排列ItemView
        //linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        //数据反向展示
        //linearLayoutManager.setReverseLayout(true);
        mSQLiteHelper=new SQLiteHelper(this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(this,mRecyclerView);
        showQueryData();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RecordActivity.class);
                startActivityForResult(intent,1);
            }
        });
        //点击事件和长按事件
        mAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view,int position) {
                Toast.makeText(MainActivity.this, "click"+position, Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setLongClickListener(new MyRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, final int position) {
                //Toast.makeText(MainActivity.this, "longClick"+position, Toast.LENGTH_SHORT).show();
                //实例化popupMenu对象
                PopupMenu menu = new PopupMenu(MainActivity.this,view);
                //加载菜单资源
                menu.getMenuInflater().inflate(R.menu.menue,menu.getMenu());
                //设置监听器
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.top:
                                Toast.makeText(MainActivity.this,"置顶",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.delete:
                                //Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
                                //用代码方式画出dialog对话框
                                AlertDialog dialog;
                                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("是否确定删除此记录?")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                NotepadBean notepadBean=data.get(position);
                                                if(mSQLiteHelper.deleteData(notepadBean.getId())){
                                                    data.remove(position);
                                                    mAdapter.notifyDataSetChanged();
                                                    Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                dialog=builder.create();
                                dialog.show();
                                break;
                        }
                        return false;
                    }
                });
                //最重要的一步，必须
                menu.show();
            }
        });

    }

    public List<NotepadBean> getData() {
        if(data!=null)
            data.clear();
        data = mSQLiteHelper.query();
        return data;
    }

    private void showQueryData(){
        mAdapter.setDataSource(getData());
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==1&&resultCode==2){
            showQueryData();
        }
    }

    /**
     *
     * @param v
     */
    public void onChangeLayoutClick(View v){
       //从线性布局切换为网格布局
       if(mRecyclerView.getLayoutManager().getClass() == LinearLayoutManager.class) {
           //切换为网格布局
           GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
           mRecyclerView.setLayoutManager(gridLayoutManager);
       } else if (mRecyclerView.getLayoutManager().getClass() == GridLayoutManager.class){
           //瀑布流布局
           StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
           mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
       } else {
           //线性布局
           LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
           mRecyclerView.setLayoutManager((linearLayoutManager));
       }
        Toast.makeText(MainActivity.this,"已切换",Toast.LENGTH_SHORT).show();
    }

}
