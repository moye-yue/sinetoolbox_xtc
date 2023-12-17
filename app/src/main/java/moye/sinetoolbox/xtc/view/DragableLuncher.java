package moye.sinetoolbox.xtc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


// ===========
// 来自CSDN
// ===========
public class DragableLuncher extends ViewGroup {

    private Scroller mScroller;// 负责得到滚动属性的对象
    private VelocityTracker mVelocityTracker;// 负责触摸的功能类

    private int mScrollX = 0;// 滚动的起始X坐标
    private float mLastMotionX;// 滚动结束X坐标
    private static final int defaultScreen = 0;

    public int mCurrentScreen = 0;// 默认显示第几屏

    private static final int SNAP_VELOCITY = 1000;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    private int mTouchState = TOUCH_STATE_REST;

    private int mTouchSlop = 0;//用户滑动的距离最小值

    public DragableLuncher(Context context) {
        super(context);
        mScroller = new Scroller(context);
        //获取触发移动事件的最短距离，系统内定？
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        this.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT));
    }

    public DragableLuncher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        //获取触发移动事件的最短距离，系统内定？
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        this.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT));
    }

    /*    touch事件拦截器，返回true继续执行onTouchEvent回调函数
     *     即mTouchState ？= TOUCH_STATE_REST
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //获取触发事件的类型，主要有：ACTION_DOWN、ACTION_MOVE、ACTION_UP
        final int action = ev.getAction();
        //当动作正在滑动 且 屏幕在滚动中
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        //获取触发点的X坐标
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //mLastMotionX相当于初始时按下的坐标点
                mLastMotionX = x;
                //一种特殊情况，界面按初速度滚动时，触屏
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取滑动距离
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                //X滑动距离大于mTouchSlop开始滚动，小于则放弃
                boolean xMoved = xDiff > mTouchSlop;
                if (xMoved) {
                    mTouchState = TOUCH_STATE_SCROLLING;//进入滑动状态
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;//改成闲置状态
                break;
        }

        //判断进入滚动状态方可通过拦截器，否则不通过，通过后自动调用进一步的onTouchEvent
        return mTouchState != TOUCH_STATE_REST;
    }

    //isOpen用以控制是否开启滚动效果，可在isOpenTouchAnima中设置
    public boolean isOpen = true;    // 设置是否打开触摸滑动

    public boolean isOpenTouchAnima(boolean isOpen) {
        this.isOpen = isOpen;
        return isOpen;
    }

    //响应滑动时间

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isOpen) {
            //确保速率探测器不为空
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            //将event事件添加到探测器中，即绑定两者关系
            mVelocityTracker.addMovement(event);

            final int action = event.getAction();
            final float x = event.getX();

            //处理各种touch事件
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    //当页面正在滚动时按钮，则暂停滚动效果
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    //只有ACTION_DOWN条件下的坐标才是初始坐标
                    mLastMotionX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //移动距离，注：此处的移动带有方向，因此不取绝对值，负值向右，正值向左
                    final int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    //deltaX<0表示向右
                    //mScrollX表示当前View滚动，左边框的X坐标，即：mScrollX>0为向右
                    if (deltaX < 0) {
                        if (mScrollX > 0) {
                            scrollBy(Math.max(-mScrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        //取得可滚动的最大距离
                        final int availableToScroll =
                                getChildAt(getChildCount() - 1).getRight()
                                        - mScrollX - getWidth();
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //计算当前速率
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY
                            && mCurrentScreen > 0) {
                        // 滑动到左边的界面
                        snapToScreen(mCurrentScreen - 1);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentScreen < getChildCount() - 1) {
                        // 滑动到右边的界面
                        snapToScreen(mCurrentScreen + 1);
                    } else {
                        //滑动到判定的界面
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    mTouchState = TOUCH_STATE_REST;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mTouchState = TOUCH_STATE_REST;
            }
            //
            mScrollX = this.getScrollX();
        } else {
            return false;
        }
        return true;
    }

    //滑动到判定的界面
    private void snapToDestination() {
        final int screenWidth = getWidth();
        final int whichScreen = (mScrollX + (screenWidth / 2)) / screenWidth;
        snapToScreen(whichScreen);
    }

    /**
     * 带动画效果显示界面
     * 跳转到指定页面，id = whichScreen
     */
    public void snapToScreen(int whichScreen) {
        mCurrentScreen = whichScreen;
        final int newX = whichScreen * getWidth();
        final int delta = newX - mScrollX;
        mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) * 2);
        invalidate();
    }

    /**
     * 不带动画效果显示界面
     */
    public void setToScreen(int whichScreen) {
        mCurrentScreen = whichScreen;
        final int newX = whichScreen * getWidth();
        mScroller.startScroll(newX, 0, 0, 0, 10);
        invalidate();
    }

    //获得当前屏幕是第几屏
    public int getCurrentScreen() {
        return mCurrentScreen;
    }
    //当主界面布局改变时调用


    /*
     * 在此方法内逐个设置页面在parent内显示的position
     * 从左往右一张一张贴过去
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            //View不是隐藏状态都进行显示
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();

                //设置View在parent内的显示范围
                //前两个参数：左上顶点的坐标
                //后两个参数：右下顶点的坐标
                child.layout(childLeft, 0, childLeft + childWidth,
                        child.getMeasuredHeight());

                childLeft += childWidth;
            }
        }
    }

    //取得测量得到的高宽
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);//提取出宽度

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);//提取宽度的模式
        if (widthMode != MeasureSpec.EXACTLY) {
            /*测量规范模式
             * MeasureSpec.AT_MOS
             * ——The child can be as large as it wants up to the specified size.
             * MeasureSpec.EXACTLY
             * ——The parent has determined an exact size for the child.
             * MeasureSpec.UNSPECIFIED
             * ——The parent has not imposed any constraint on the child.
             * */
            throw new IllegalStateException("error mode.");
        }


        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);//提取高度的模式
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("error mode.");
        }

        // 子元素将被分配给同样的高和宽
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }

        //滚动到指定的屏幕
        scrollTo(mCurrentScreen * width, 0);
    }

    //计算滚动的坐标
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mScrollX = mScroller.getCurrX();
            scrollTo(mScrollX, 0);
            postInvalidate();
        }
    }
}