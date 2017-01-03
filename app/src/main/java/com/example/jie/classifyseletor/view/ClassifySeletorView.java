package com.example.jie.classifyseletor.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jie.classifyseletor.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jie on 16/12/29.
 */

public class ClassifySeletorView extends LinearLayout {
    private String TAG = "ClassifySeletorView";
    private Context context;
    private RecyclerView rvTitle;
    private ClassifySeletorListener classifySeletorListener;
    private SlideContainer slideContainer;
    private TitleAdapter titleAdapter;
    private Button btnReset, btnOk;

    private ClassifySeletorItem firstHeadItem;
    public ClassifySeletorView(Context context) {
        super(context);
        this.context = context;
    }

    public ClassifySeletorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }



    //初始化布局
    private void init() {
        LayoutInflater.from(context).inflate(R.layout.customview_cs_main, this, true);
        rvTitle = (RecyclerView) findViewById(R.id.customview_cs_rv_title);
        btnOk = (Button) findViewById(R.id.customview_cs_main_ok);
        btnReset = (Button) findViewById(R.id.customview_cs_main_reset);
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                classifySeletorListener.clickOk(slideContainer.getSelectItems());
            }
        });
        //重置
        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                titleAdapter.setPage(0);
                slideContainer.setPage(0);
                slideContainer.reset();
                classifySeletorListener.clickReset();
            }
        });
        titleAdapter = new TitleAdapter(context, rvTitle);
        //委托一下，标题被点击
        titleAdapter.setOnItemClickListener(new TitleAdapter.OnItemClickListener() {
            @Override
            public void click(TitleAdapter.TitleViewHolder holder, int position, ClassifySeletorItem item) {
                Log.d(TAG, "click() called with: holder = [" + holder + "], position = [" + position + "], item = [" + item + "]");
                slideContainer.setPage(position);
            }
        });

        //设置水平标题
        rvTitle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvTitle.setAdapter(titleAdapter);

        slideContainer = (SlideContainer) findViewById(R.id.customview_cs_sc_multi);

        slideContainer.setSlideContainListener(new SlideContainer.SlideContainListener() {
            @Override
            public List<ClassifySeletorItem> getData(int itemPosition, ClassifySeletorItem item, int currentPage) {
                Log.d(TAG, "getData() called with: itemPosition = [" + itemPosition + "], item = [" + item + "], currentPage = [" + currentPage + "]");
                if(null==item){
                    //这里是加载第一个"全部分类"，然后加载第一页的数据
                    if(null==firstHeadItem){
                        firstHeadItem=classifySeletorListener.getFirstData();
                        titleAdapter.push(firstHeadItem);//让路径添加一个
                    }
                    if(null==firstHeadItem)return null;
                    Log.d(TAG, "getData()22 called with: itemPosition = [" + itemPosition + "], firstHeadItem = [" + firstHeadItem + "], currentPage = [" + currentPage + "]");
                    return classifySeletorListener.getData(currentPage, firstHeadItem);
                }else{
                    //不是点击的导航栏，才添加导航栏路径
                    if(-1!=itemPosition)titleAdapter.push(item);
                    //加载数据
                    return classifySeletorListener.getData(currentPage, item);
                }
            }

            @Override
            public List<ClassifySeletorItem> pageBack(int currentPage, ClassifySeletorItem item) {
                //返回的时候，还要加载数据
                Log.d(TAG, "pageBack() called with: currentPage = [" + currentPage + "], item = [" + item + "]");
                titleAdapter.pop();
                if(null==item){
                    return classifySeletorListener.getData(2,firstHeadItem);
                }else{
                    return classifySeletorListener.getData(currentPage+1, item);
                }
            }
            @Override
            public void click(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
                //item的点击事件委托出去
                classifySeletorListener.clickItem(isSelected, holder, position, item);
            }

            @Override
            public Boolean isFinal(ClassifySeletorItem item) {
                //判断某个节点，是不是最终的节点
                return classifySeletorListener.isFinal(item);
            }
        });
    }

    public ClassifySeletorListener getClassifySeletorListener() {
        return classifySeletorListener;
    }

    public void setClassifySeletorListener(ClassifySeletorListener classifySeletorListener) {
        this.classifySeletorListener = classifySeletorListener;
        init();
    }

    /**
     * 获取最后选中的状态
     *
     * @return
     */
    public List<ClassifySeletorItem> getSelectItems() {
        return slideContainer.getSelectItems();
    }

    public static abstract class ClassifySeletorListener {
        /**
         * 用来加载某一页的数据
         *
         * @param level level 0序
         * @param item  加载的父item，如果是跟，为null
         * @return 返回对应的自元素item
         */
        public abstract List<ClassifySeletorItem> getData(int level, ClassifySeletorItem item);

        /**
         * 获取最开头的初始化数据
         * @return
         */
        public abstract ClassifySeletorItem getFirstData();

        /**
         * 条目被点击了
         *
         * @param isSelected 是否选中
         * @param holder     承载体
         * @param position   位置
         * @param item       内容
         */
        public void clickItem(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
        }

        /**
         * 点击了重置按钮
         */
        public void clickReset() {
        }

        /**
         * 点击了ok按钮
         *
         * @param selectItem 被选中的条目集合
         */
        public void clickOk(List<ClassifySeletorItem> selectItem) {
        }
        /**
         * 判断某个节点是不是最后一级了
         * @param item 节点
         * @return false：不是，true：是
         */
        public abstract Boolean isFinal(ClassifySeletorItem item);
    }
}
