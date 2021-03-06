package com.example.recyclerview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
    private ImageView imageView;            //add图片
    private EditText searchEdit;            //搜索框
    private ImageView searchView;           //搜索图标
    private ImageView empty;                //搜索框右边的清空图标
    private ImageView introduce;        //软件介绍界面
    private ImageView menuLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //创建bugly产品
        CrashReport.initCrashReport(getApplicationContext(), "629f8fa455", true);

        //以下代码为调整状态栏颜色与背景色相同
        Window window = getWindow();
        //After LOLLIPOP not translucent status bar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //Then call setStatusBarColor.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#f3efef"));
        }


        mRecyclerView =findViewById(R.id.recycler_view);
        imageView = findViewById(R.id.add);
        searchView = findViewById(R.id.search_image);
        searchEdit = findViewById(R.id.search_edit);
        introduce = findViewById(R.id.introduce_btn);
        menuLeft = findViewById(R.id.menu_left);

        empty = findViewById(R.id.clear);
        empty.setVisibility(View.GONE);     //初始化搜索框的X为隐藏

        //这句代码使得打开activity时不会自动弹出输入法
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //横向排列ItemView
        //linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        //数据反向展示
        //linearLayoutManager.setReverseLayout(true);

        mSQLiteHelper=new SQLiteHelper(this);       //初始化数据库辅助类

        mRecyclerView.setLayoutManager(linearLayoutManager);        //初始化布局管理器
        mAdapter = new MyRecyclerViewAdapter(this,mRecyclerView);
        showQueryData();
        //为添加按钮添加点击事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RecordActivity.class);
                startActivityForResult(intent,1);
            }
        });

        //软件介绍界面
        introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,IntroduceActivity.class);
                startActivity(intent);
            }
        });
        menuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "该功能暂时未开放！", Toast.LENGTH_SHORT).show();
            }
        });

        //当点击清空按钮
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdit.setText("");
            }
        });
        //设置 当输入框改变时自动搜索
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = searchEdit.getText().toString();
                if(temp.equals("")){
                    empty.setVisibility(View.GONE);
                }
                else {
                    empty.setVisibility(View.VISIBLE);
                }
                mAdapter.setDataSource(searchData(searchEdit.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }



        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEdit.getWindowToken(),0);
                String words =searchEdit.getText().toString().trim();
                if(!words.equals("")){
                    mAdapter.setDataSource(searchData(words));
                    mAdapter.notifyDataSetChanged();
                } else{
                    Toast.makeText(MainActivity.this, "搜索的内容不能空！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //点击事件和长按事件
        mAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view,int position) {
                //Toast.makeText(MainActivity.this, "click"+position, Toast.LENGTH_SHORT).show();
                //跳转到RecordActivity
                //并将数据传过去
                NotepadBean notepadBean=data.get(position);
                Intent intent=new Intent(MainActivity.this,RecordActivity.class);
                intent.putExtra("id",notepadBean.getId());
                intent.putExtra("content",notepadBean.getNotepadContent());
                intent.putExtra("time",notepadBean.getNotepadTime());
                MainActivity.this.startActivityForResult(intent,1);
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
                            case R.id.top:      //置顶按钮
                                Toast.makeText(MainActivity.this,"置顶",Toast.LENGTH_SHORT).show();
                                NotepadBean notepadBean = data.get(position);
                                String Id = notepadBean.getId();
                                mAdapter.setDataSource(setTop(Id));
                                //mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();

                                break;
                            case R.id.delete:   //删除按钮
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
        String Id= new String();
        data = mSQLiteHelper.query(Id);
        return data;
    }

    public List<NotepadBean> setTop(String Id){
        if(data!=null)
            data.clear();
        data = mSQLiteHelper.query(Id);
        return data;
    }
    public List<NotepadBean> searchData(String string) {
        if(data!=null)
            data.clear();
            data = mSQLiteHelper.searchQuery(string);
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
