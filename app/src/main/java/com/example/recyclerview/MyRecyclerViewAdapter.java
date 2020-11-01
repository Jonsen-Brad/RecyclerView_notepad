package com.example.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.recyclerview.Bean.NotepadBean;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private RecyclerView mRv;
    private List<NotepadBean> dataSource;
    private Context mContext;



    public MyRecyclerViewAdapter(Context context,RecyclerView recyclerView){
        this.mContext = context;
        this.dataSource = new ArrayList<>();
        this.mRv=recyclerView;
    }

    public void setDataSource(List<NotepadBean> dataSource){
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }
    /**
     * 创建并返回ViewHolder
     * @param parent
     * @param position
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_layout,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    //定义点击事件接口
    public interface OnItemClickListener{
        void OnItemClick(View view,int position);
    }
    public interface OnItemLongClickListener{
        void OnItemLongClick(View view,int position);
    }

    //定义变量
    OnItemClickListener listener;
    OnItemLongClickListener longClickListener;
//写一个公共方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    /**
     * ViewHolder 绑定数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        //holder.mImageView.setImageResource(getIcon(position));
        holder.mTextView.setText(dataSource.get(position).getNotepadContent());

        //点击事件接口回调
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.OnItemClick(holder.itemView,position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longClickListener != null){
                    longClickListener.OnItemLongClick(holder.itemView,position);
                }
                return true;
            }
        });

        /**
         * 只在瀑布流中设置随机高度
         */
        if (mRv.getLayoutManager().getClass() == StaggeredGridLayoutManager.class) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getRandomHeight());
            holder.mTextView.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.mTextView.setLayoutParams(params);
        }
    }
    /**
     * 返回数据数量
     * @return
     */
    @Override
    public int getItemCount() {
        return dataSource.size();
    }

//    private int getIcon(int position){
//////        switch (position % 5){
//////            case 0:
//////                return R.mipmap.a;
//////            case 1:
//////                return R.mipmap.b;
//////            case 2:
//////                return R.mipmap.c;
//////            case 3:
//////                return R.mipmap.d;
//////            case 4:
//////                return R.mipmap.e;
//////
//////        }
//////        return 0;
//////    }

    private int getRandomHeight(){
        return (int)(Math.random()*1000);
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTitleView;
        TextView mTextView;
        TextView mTimeView;

        public MyViewHolder (@NonNull View itemView){
            super(itemView);

            mTitleView = itemView.findViewById(R.id.titleView);
            mTextView = itemView.findViewById(R.id.contentView);
            mTimeView = itemView.findViewById(R.id.timeView);
        }
    }
}
