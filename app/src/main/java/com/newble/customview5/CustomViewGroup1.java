package com.newble.customview5;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Date: 2017/12/26 09:02
 * Description:自定义的一个ViewGroup,内部可以传入0到4个childView,分别依次显示在左上角,右上角,左下角,右下角
 */

public class CustomViewGroup1 extends ViewGroup {

    public CustomViewGroup1(Context context) {
        this(context, null);
    }

    public CustomViewGroup1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewGroup1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 为了让ViewGroup能够支持margin,直接使用系统的MarginLayoutParams
     *
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
//        return new MyLayoutParams(getContext(), attrs);
    }

//    @Override
//    protected LayoutParams generateDefaultLayoutParams() {
//        return new MyLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//    }

    /**
     * 计算所有ChildView的宽度和高度,然后根据ChildView的计算结果,设置自己的宽度和高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 获得此ViewGroup的上级容器为其推荐的宽和高,以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int cCount = getChildCount();

//        for (int i = 0; i < cCount; i++) {
//            View childView = getChildAt(i);
//            //childView.measure(widthMeasureSpec,heightMeasureSpec);
//            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
//        }

        /**测量一次所有的childView的宽和高***/
        measureChildren(widthMeasureSpec, heightMeasureSpec);


        int width = 0;
        int height = 0;


        int cWidth = 0;
        int cHeight = 0;

        MarginLayoutParams cParams = null;
        //用于计算左边两个childView的高度
        int lHeight = 0;
        //用于计算右边两个childView的高度,最终高度取两者之间大值
        int rHeight = 0;

        //用于计算上边两个childView的宽度
        int tWidth = 0;
        //用于计算下面两个childView的宽度,最终宽度取两者之间大值
        int bWidth = 0;

        //遍历ChildView,计算出容器是wrap_content时的宽和高
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            if (i == 0 || i == 1) {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 2 || i == 3) {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 0 || i == 2) {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

            if (i == 1 || i == 3) {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }
        }

        width = Math.max(tWidth, bWidth);
        height = Math.max(lHeight, rHeight);

        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则:直接设置为父容器计算的值
         */
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        /**
         * 遍历所有childView,根据其宽和高,以及margin进行布局
         */
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            int cl = 0, ct = 0, cr = 0, cb = 0;
            switch (i) {
                case 0:
                    cl = cParams.leftMargin;
                    ct = cParams.topMargin;
                    break;
                case 1:
                    cl = getWidth() - cWidth - cParams.rightMargin;
                    ct = cParams.topMargin;
                    break;
                case 2:
                    cl = cParams.leftMargin;
                    ct = getHeight() - cHeight - cParams.bottomMargin;
                    break;
                case 3:
                    cl = getWidth() - cWidth - cParams.rightMargin;
                    ct = getHeight() - cHeight - cParams.bottomMargin;
                    break;
            }
            cr = cl + cWidth;
            cb = ct + cHeight;
            childView.layout(cl, ct, cr, cb);
        }

    }

    public static class MyLayoutParams extends MarginLayoutParams {

        public MyLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public MyLayoutParams(int width, int height) {
            super(width, height);
        }

        public MyLayoutParams(LayoutParams lp) {
            super(lp);
        }
    }

}
