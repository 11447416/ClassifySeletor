package com.example.jie.classifyseletor.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.BoolRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by jie on 16/12/29.
 */

public class SlideContainer extends FrameLayout implements ItemAdapter.OnItemClickListener {
    private String TAG = "SlideContainer";

    private float startX = 0, startY = 0;//开始点击的坐标，用来处理滑动冲突
    private int currentPage = -1;//当前的页码
    private float parallax = 0.2f;//视差因子
    private FrameLayout frameLayout1, frameLayout2, frameLayoutTop;//用frameLayout套 一下，主要是为了以后增加蒙层
    private RecyclerView recyclerView1, recyclerView2;
    private boolean needExchange = false; //是否需要交换页面
    private SlideContainListener slideContainListener;//获取数据的接口，把数据加载这个委托出去

    private Map<String,ClassifySeletorItem> path=new HashMap<>();

    public SlideContainer(Context context) {
        super(context);
        throw new UnsupportedOperationException("不支持java代码实例化，T_T");
    }
    public void subPage(int position){
        Log.i(TAG, "subPage: "+position);
        currentPage=position;
    }

    public SlideContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        frameLayout1 = new FrameLayout(context);
        recyclerView1 = new RecyclerView(context);
        frameLayout1.addView(recyclerView1);
        frameLayout2 = new FrameLayout(context);
        recyclerView2 = new RecyclerView(context);
        frameLayout2.addView(recyclerView2);
        addView(frameLayout1);
        addView(frameLayout2);
        //设置下面的rvitem垂直
        recyclerView1.setLayoutManager(new LinearLayoutManager(context));
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));
        frameLayoutTop = frameLayout2;
        frameLayout1.setBackgroundColor(Color.WHITE);
        frameLayout2.setBackgroundColor(Color.WHITE);

        ItemAdapter itemAdapter = new ItemAdapter(context);
        itemAdapter.setOnItemClickListener(this);
        recyclerView1.setAdapter(itemAdapter);
        ItemAdapter itemAdapter2 = new ItemAdapter(context);
        itemAdapter2.setOnItemClickListener(this);
        recyclerView2.setAdapter(itemAdapter2);
    }

    @Override
    public void click(ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
        //根据点击的item，加载下一页的数据
        if (null==item.getFinal()) {
            //如果不知道节点是不是最后一级,调用构造，去判断
            item.setFinal(slideContainListener.isFinal(item));
        }
        if (!item.getFinal()) {
            //不是最后一级，就加载新的一页的数据
            getData(position, item);
        } else {
            holder.imageView.setSelected(!holder.imageView.isSelected());
            //把没有子内容的元素的点击时间委托出去
            slideContainListener.click(holder.imageView.isSelected(), holder, position, item);
        }
    }

    @Override
    public Boolean isFinal(ClassifySeletorItem item) {
        return slideContainListener.isFinal(item);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //处理滑动冲突
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - startX) - Math.abs(event.getY() - startY) > 10) {//10是 为了防止有的人肾虚，手抖，设置10个像素容差，避免点击不灵
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentPage <= 0) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //当向右滑动，并且前面有页面的时候
                if ((event.getX() - startX) > 0) {
                    if (frameLayout1 == frameLayoutTop) {
                        //按照比例手指滑动，移动当前页面
                        frameLayout1.setTranslationX((event.getX() - startX));
                        //造成视差
                        frameLayout2.setTranslationX(-(frameLayout2.getMeasuredWidth() * (parallax) - (event.getX() - startX) * parallax));
                    } else {
                        //按照比例手指滑动，移动当前页面
                        frameLayout2.setTranslationX((event.getX() - startX));
                        //造成视差
                        frameLayout1.setTranslationX(-(frameLayout1.getMeasuredWidth() * (parallax) - (event.getX() - startX) * parallax));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //滑动超过页面宽度1/5就弹出来显示，否则弹回去
                if (event.getX() - startX > frameLayout1.getMeasuredWidth() / 5) {
                    //出去
                    if (frameLayout1 == frameLayoutTop) {
                        animate(frameLayout1.getMeasuredWidth(), frameLayout1);
                        animate(0, frameLayout2);
                    } else {
                        animate(frameLayout2.getMeasuredWidth(), frameLayout2);
                        animate(0, frameLayout1);
                    }
                    needExchange = true;
                } else {
                    //恢复
                    if (frameLayout1 == frameLayoutTop) {
                        animate(0, frameLayout1);
                        animate(-frameLayout2.getMeasuredWidth() * (parallax), frameLayout2);
                    } else {
                        animate(0, frameLayout2);
                        animate(-frameLayout2.getMeasuredWidth() * (parallax), frameLayout1);
                    }
                }
                break;
        }
        return true;
    }

    //前进的动画和页面交换
    private void nextPage() {
        final View view = getChildAt(0);
        removeView(view);
        addView(view);
        if (frameLayout1 == frameLayoutTop) {
            frameLayoutTop = frameLayout2;
        } else {
            frameLayoutTop = frameLayout1;
        }
        //前进动画
        ValueAnimator animator = ValueAnimator.ofFloat(view.getMeasuredWidth(), 0).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setTranslationX((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    //回退或者是恢复的动画
    private void animate(float end, final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(view.getTranslationX(), end).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setTranslationX((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //如果需要交换页面，需要在动画结束以后
                if (needExchange) {
                    needExchange = false;
                    currentPage--;
                    if (null != slideContainListener) {
                        //返回的时候，重新加载上一页的数据
                        Log.i(TAG, "onAnimationEnd: get:"+currentPage+","+path.get((currentPage)+""));
                        List<ClassifySeletorItem> getDataListenerData=slideContainListener.pageBack(currentPage,path.get((currentPage)+""));
                        ItemAdapter itemAdapter;
                        if (null != getDataListenerData) {
                            //判断当前显示的是哪个recyclerView,加载新的数据到另一个view
                            if (frameLayoutTop == frameLayout1) {
                                itemAdapter = (ItemAdapter) recyclerView2.getAdapter();
                                Log.i(TAG, "getData: 设置数据11");
                            } else {
                                Log.i(TAG, "getData: 设置数据22");
                                itemAdapter = (ItemAdapter) recyclerView1.getAdapter();
                            }
                            itemAdapter.setData(getDataListenerData);
                        }
                    }
                    Log.i(TAG, "onAnimationEnd: "+getChildAt(1));
                    //出去
                    if (frameLayout1 == frameLayoutTop) {
                        removeView(frameLayout1);
                        addView(frameLayout1, 0);
                        frameLayoutTop = frameLayout2;
                    } else {
                        removeView(frameLayout2);
                        addView(frameLayout2, 0);
                        frameLayoutTop = frameLayout1;
                    }
                    Log.i(TAG, "onAnimationEnd: "+getChildAt(1));

                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
    }

    //根据点击的item，获取下一页的新数据
    public void getData(int itemPosition, ClassifySeletorItem item) {
        if (null == slideContainListener) return;
        List<ClassifySeletorItem> getDataListenerData;
        ItemAdapter itemAdapter;
        if (currentPage == -1) {
            //获取首页的数据
            getDataListenerData = slideContainListener.getData(itemPosition, item, 0);//
            if (null == getDataListenerData) return;
            itemAdapter = (ItemAdapter) recyclerView2.getAdapter();
            path.put("0",item);
            Log.i(TAG, "getData: put:0->"+item);
        } else {
            currentPage++;
            path.put(""+currentPage,item);
            Log.i(TAG, "getData: put:"+currentPage+"->"+item);
            //加载下一页的数据
            getDataListenerData = slideContainListener.getData(itemPosition, item, currentPage);
            if (null == getDataListenerData) return;
            //判断当前显示的是哪个recyclerView,加载新的数据到另一个view
            if (frameLayoutTop == frameLayout1) {
                itemAdapter = (ItemAdapter) recyclerView2.getAdapter();
                Log.i(TAG, "getData: 设置数据11");
            } else {
                Log.i(TAG, "getData: 设置数据22");
                itemAdapter = (ItemAdapter) recyclerView1.getAdapter();
            }
        }
        itemAdapter.setData(getDataListenerData);
        if (currentPage != -1) {
            //说明不是初始化，就是点击，要下一页
            nextPage();//页面动画
        } else {
            //加载首页数据，所以不跳转
            currentPage++;
        }

    }

    public SlideContainListener getSlideContainListener() {
        return slideContainListener;
    }

    public void setSlideContainListener(SlideContainListener slideContainListener) {
        this.slideContainListener = slideContainListener;
        //获取第一页的初始化数据
        getData(0, null);
    }

    /**
     * 获取最后选中的状态
     * @return
     */
    public List<ClassifySeletorItem> getSelectItems(){
        return ((ItemAdapter)((RecyclerView)frameLayoutTop.getChildAt(0)).getAdapter()).getStatus();
    }

    /**
     * 全部取消
     */
    public void reset(){
        ((ItemAdapter)((RecyclerView)frameLayoutTop.getChildAt(0)).getAdapter()).reset();
    }


    public interface SlideContainListener {
        /**
         * 获取当前需要显示的数据,注意，初始化加载第一页的数据的时候，item为null
         *
         * @param itemPosition
         * @param item
         * @return 返回下一页要现实的数据
         */
        List<ClassifySeletorItem> getData(int itemPosition, ClassifySeletorItem item, int currentPage);

        /**
         * 当页面回退的时候
         *
         * @param currentPage 返回以后，页面的深度
         */
        List<ClassifySeletorItem> pageBack(int currentPage,ClassifySeletorItem item);

        /**
         * 当没有子元素的时候，点击的回调
         *
         * @param holder
         * @param position
         * @param item
         */
        void click(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item);

        /**
         * 判断某个节点是不是最后一级了
         * @param item 节点
         * @return false：不是，true：是
         */
        Boolean isFinal(ClassifySeletorItem item);
    }
}
