package com.mao.slidemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * Created by 毛麒添 on 2016/12/19 0019.
 * 自定义控件继承ViewGroup
 * 侧滑面板界面
 */

public class MySilemenu extends ViewGroup {

    private float downX;//按下屏幕x的位置
    private float downY;//按下屏幕Y的位置
    private float moveX;//移动的位置

    //当前面板状态量
    public static final int MAIN_STATE=0;
    public static final int MENU_STATE=1;
    private int currentState=MAIN_STATE;//当前面板状态

    private Scroller scroller;//滚动器


    public MySilemenu(Context context) {
        super(context);
        init();
    }

    public MySilemenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySilemenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MySilemenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //初始化滚动器。数值模拟器
        scroller = new Scroller(getContext());
    }
    /**
     * 测量并设置所有子View的宽高
     * @param widthMeasureSpec 当前控件宽度测量规则
     * @param heightMeasureSpec 当前控件高度测量规则
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //自定中包裹的布局相当于在集合当中，获取每个布局相当于对集合中的元素进行操作
        //指定左面版宽高
        View leftMenu = getChildAt(0);
        leftMenu.measure(leftMenu.getLayoutParams().width,heightMeasureSpec);

        //指定主界面的宽高
        View layoutmain = getChildAt(1);
        layoutmain.measure(widthMeasureSpec,heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 摆放子控件的方法
     * @param changed 当前控件的尺寸大小，位置是否发生变化
     * @param l 当前控件的左边距
     * @param t 当前控件的顶边距
     * @param r 当前控件的右边距
     * @param b 当前控件的下边距
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View leftMenu = getChildAt(0);
        //左布局摆放在左边负布局宽度位置
        leftMenu.layout(-leftMenu.getMeasuredWidth(),0,0,b);
        //主界面布局摆放在默认位置
        getChildAt(1).layout(l,t,r,b);
    }

    /**
     * 触摸事件处理
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                //获取按下屏幕的位置
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //获取滑动的位置
                moveX = event.getX();
                //滑动距离
                int scrollX= (int) (downX-moveX);

                /**
                 *计算将要滚动到的位置，判断是否会大于布局的宽度而超出去，如果超出，就不执行scrollBy，
                 * 而是执行scrollTo,跳到指定位置
                 */
                int newScrollPosition= getScrollX()+scrollX;//计算将要滚动到的位置
                if(newScrollPosition < -getChildAt(0).getMeasuredWidth()){//限定左边界
                    //小于侧滑布局自身的宽度
                    scrollTo(-getChildAt(0).getMeasuredWidth(),0);

                }else if(newScrollPosition>0){//限定右边界
                    scrollTo(0,0);
                }else {
                    //让滑动生效
                    scrollBy(scrollX,0);
                }
                //将最新的位置替换原来的位置，避免叠加
                downX=moveX;
                break;
            case MotionEvent.ACTION_UP://手指抬起
                //松手后根据当前的位置和左布局的一半进行比较，
                int leftlayout= (int) (-getChildAt(0).getMeasuredWidth()/2.0f);
                if(getScrollX()<leftlayout){
                    //如果大于一半，展开侧滑布局,切换成菜单面板
                    currentState=MENU_STATE;
                    //根据状态执行关闭或者开启的动画
                    updateCurrentAnim();
                }else {
                   //如果小于一半，隐藏侧滑布局，切换成主页面
                    currentState=MAIN_STATE;
                    updateCurrentAnim();
                }

                break;
        }
        return true;//消费事件（如果完全自定义，则返回true,如果是继承已有的控件，则返回系统super.onTouchEvent(event)）
        //return super.onTouchEvent(event);
    }

    /**
     * 根据状态执行关闭或者开启的动画
     */
    private void updateCurrentAnim() {
        int startX=getScrollX();
        int dx=0;//差值变化量
        if(currentState==MENU_STATE){
            //开启侧滑面板
            /*scrollTo(-getChildAt(0).getMeasuredWidth(),0);*/
            //根据不同的状态获取不同的差值变化量
            dx=-getChildAt(0).getMeasuredWidth()-startX;
        }else {
            //回到主界面
           /* scrollTo(0,0);*/
            //根据不同的状态获取不同的差值变化量
            dx= 0 - startX;
        }

        int duration=Math.abs(dx);//绝对值
        // startX: 开始的x值
        // startY: 开始的y值
        // dx: 将要发生的水平变化量. 移动的x距离
        // dy: 将要发生的竖直变化量. 移动的y距离
        // duration : 数据模拟持续的时长
        // 1. 开始平滑的数据模拟
        scroller.startScroll(startX,0,dx,0,duration);

        invalidate();//重绘界面 drawChild()->computeScroll()
    }

    /**
     * 维持动画的继续
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            //动画还没有结束
            int x=scroller.getCurrX();
            scrollTo(x,0);

            invalidate();//继续重新绘制
        }
    }
    //显示侧滑面板
    public void open(){
        currentState=MENU_STATE;
        //根据状态执行关闭或者开启的动画
        updateCurrentAnim();

    }
    //关闭侧滑面板
    public void close(){
        currentState=MAIN_STATE;
        updateCurrentAnim();

    }

    //设置点击按钮显示或隐藏侧滑面板
    public void switchState(){
        if(currentState==MAIN_STATE){//如果关闭则打开
             open();
        }else {//如果打开则关闭
             close();
        }
    }

    //获取当前的状态
    public int getState(){
        return currentState;
    }

    /**
     * 触摸事件拦截,获取按下屏幕的坐标和滑动的坐标做差得到X,Y轴偏移量，
     * 当偏移量x>y,说明用户上想左右滑动，拦截此事件防止滚动，否则让事件可以传递下去，
     * 让侧滑面板可以滑动
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //获取点击的X ,Y坐标
                downX=ev.getX();
                downY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //X,Y轴偏移量
                float xoffset=Math.abs(ev.getX()-downX);
                float yoffset=Math.abs(ev.getY()-downY);
                if(xoffset>yoffset&&xoffset>5){//用户进行的操作是左右滑动
                    return true;//拦截此次触摸事件，界面的滚动
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
