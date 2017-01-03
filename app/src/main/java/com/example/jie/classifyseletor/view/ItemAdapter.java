package com.example.jie.classifyseletor.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jie.classifyseletor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类选择器的下面的item的adaper
 * Created by jie on 16/12/29.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private String TAG = "ItemAdapter";
    private List<ClassifySeletorItem> items;
    private Context context;
    private OnItemClickListener onItemClickListener;

    private List<ClassifySeletorItem> status = new ArrayList<>();//保存选中的item

    public ItemAdapter(Context context) {
        items = new ArrayList<>();
        this.context = context;
    }

    public void reset() {
        status.clear();
        notifyDataSetChanged();
    }

    //设置要显示的数据
    public void setData(List<ClassifySeletorItem> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
        status.clear();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customview_cs_item_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.textView.setText(items.get(position).getName());
        //如果有下一级，就显示箭头，否则就不显示箭头
        ClassifySeletorItem item=items.get(position);
        //根据点击的item，加载下一页的数据
        if (null==item.getFinal()) {
            //如果不知道节点是不是最后一级,调用构造，去判断
            item.setFinal(onItemClickListener.isFinal(item));
        }
        if (item.getFinal()) {
            holder.arrow.setVisibility(View.GONE);
        } else {
            holder.arrow.setVisibility(View.VISIBLE);
        }

        //设置选中状态
        if (status.contains(items.get(position))) {
            holder.imageView.setSelected(true);
        } else {
            holder.imageView.setSelected(false);

        }
        //设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemClickListener) {
                    onItemClickListener.click(holder, position, items.get(position));
                    //用来保存元素的状态
                    if (status.contains(items.get(position))) {
                        status.remove(items.get(position));
                    } else {
                        status.add(items.get(position));
                        Log.i(TAG, "onClick: add:" + items.get(position).getName());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public View itemView;
        public ImageView arrow;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.customview_cs_item_iv);
            arrow = (ImageView) itemView.findViewById(R.id.customview_cs_rv_title_iv);
            textView = (TextView) itemView.findViewById(R.id.customview_cs_item_tv);
        }
    }

    public List<ClassifySeletorItem> getStatus() {
        return status;
    }

    public void setStatus(List<ClassifySeletorItem> status) {
        this.status = status;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 点击事件
         * @param holder
         * @param position
         * @param item
         */
        void click(ItemViewHolder holder, int position, ClassifySeletorItem item);
        /**
         * 判断某个节点是不是最后一级了
         * @param item 节点
         * @return false：不是，true：是
         */
        Boolean isFinal(ClassifySeletorItem item);
    }
}
