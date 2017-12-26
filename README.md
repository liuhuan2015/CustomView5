# CustomView5
自定义ViewGroup学习<br>
按着hongyang的博客写了这个自定义ViewGroup的案例,实现效果:自定义的ViewGroup可以作为根布局使用,内部可以有四个以内的子控件,子控件会分别位于自定义
ViewGroup的左上,右上,左下,右下位置.<br>
思路:一,重写ViewGroup的onMeasure(...),在onMeasure中对子控件进行宽高测量,然后根据MeasureSpec的SpecMode得出自定义ViewGroup的最终宽高.<br>
代码:使用measureChildren(...)对子控件进行遍历测量,但是使用这种方式好像子控件设置margin会没有作用,网上说要用measureChildWithMargins(...),但是我不会用
,见相关文章http://www.jianshu.com/p/5fbb1ce3c7f0.

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
    
二,重写onLayout(...),对子控件的位置进行限定.

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
