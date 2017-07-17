# CropImageView

CropImageView的原型来自[**Cropimage_demo**](https://github.com/gumingwei/Cropimage_demo)，是android上的的一个图片裁剪控件。

原作者的博客[Android 自定义控件——图片剪裁](http://blog.csdn.net/u013045971/article/details/41960433)，如果读者想要有更详细的了解，请转至原作者博客。

之所以做这个控件是因为前段时间写了一个截图应用需要用到裁剪功能，现在把裁剪的控件单独拿出来写一个demo。但是说实话我并没有完全理解源码，希望有透彻了解的读者还是去看原作者的博客吧。

代码不多，用到的两个类加一起500行左右。

## 效果展示
gif效果展示：

![](Cropimage.gif)


## 特点

其实是相较于原版的特点。有两个主要的改进：

1. 裁剪框不会超出控件边界
   无论是整体移动裁剪框还是移动四角，提升用户体验。
2. 控件自适应图片大小
   图片超过屏幕尺寸则缩放图片，否则缩放控件适应图片大小，这样可以避免裁剪框剪切到图片以外的内容。

## 具体实现

### 控件适应图片

因为我们需要这个控件居中显示，而且控件必须和加载的图片一样大（否则裁剪框超出图片的话会截到黑边），所以在绘制这个控件的时候要测量图片大小根据大小调整控件大小。

重写onMeasure()方法

```java
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mBitmap != null) {
            if ((mBitmap.getHeight() > heightSize) && (mBitmap.getHeight() > mBitmap.getWidth())) {
                widthSize = heightSize * mBitmap.getWidth() / mBitmap.getHeight();
            } else if ((mBitmap.getWidth() > widthSize) && (mBitmap.getWidth() > mBitmap.getHeight())) {
                heightSize = widthSize * mBitmap.getHeight() / mBitmap.getWidth();
            } else {
                heightSize = mBitmap.getHeight();
                widthSize = mBitmap.getWidth();
            }
        }
        setMeasuredDimension(widthSize, heightSize);
    }
```

### 调整边界

调整边界我其实有三个思路来调整，但写下来的三种实现方法都不完美。

介绍三种思路之前先解释一下“拖动时调整边界”和“绘图时边界”，“拖动时调整边界”就是在`onTouchEvent()`内调整边界，“绘图时调整边界”是在`onTouchEvent()`不做限制，在onDraw()的时候再调整。

#### 拖动时调整边界 一

当手指坐标达到布局边界超出图片坐标（即超过承载着图片的CropImageView的范围）后，停止裁剪框边界坐标变化响应。

```java
public boolean onTouchEvent(MotionEvent event) {                
  ...
	case MotionEvent.ACTION_MOVE:
  ...
  		if (event.getX() > getWidth() || event.getX() < 0 || event.getY() > getHeight() || event.getY() < 0) {
			break;
		}
```

但是这个有个小问题，实际操作中发现会产生一个移动四个角过快的时候不容易移到到边界的现象，慢慢操作还是可以移动到CropImageView的边界的，但操作不友好。

#### 拖动时调整边界 二

以上边界为例，当拖动上边两个角超出上边界的时候，设定裁剪框的上边界为CropImageView的上边界。其他三边一次类推。注 裁剪框的右边界坐标为CropImageView的getWidth()。

```java
if (mDrawableFloat.top < 0) {
	mDrawableFloat.top = 0;
    break;
}
```

但是请思考，如果用户是在裁剪框内拖动这个框呢？

这时候你不能光判断裁剪框的边界在哪，你还得思考用户滑动的方向（dx，dy是滑动便宜量）。

```JAVA
		case EDGE_MOVE_IN:
			// 因为手指一直在移动，应该实时判断是否超出裁剪框（手指移动到图片范围外）
			isTouchInSquare = mDrawableFloat.contains((int) event.getX(), (int) event.getY());
			if (isTouchInSquare) {
				if (mDrawableFloat.left == 0 && dx < 0
						||mDrawableFloat.top == 0 && dy < 0
						||mDrawableFloat.right == getWidth() && dx > 0
						||mDrawableFloat.bottom == getHeight() && dy > 0) {
					break;
				}
				mDrawableFloat.offset(dx, dy);
			}
			break;
```

实测效果 拖动裁剪框的时候有些不跟手，可能跟代码运行效率有关？依然操作不友好。

#### 绘图时调整边界

这个就不在`onTouchEvent()`搞事情了，你随便拖动，拖出去算我输。但是拖动的过程也伴随着实时绘制的过程，在onDraw()里面调整也是调整裁剪框的边界了。

这里要区分是整体拖动还是四角拖动，看到代码吧

```java
	protected void configureBounds() {
        if () {
            ...
        } else if (getTouch((int) mX_1, (int) mY_1) == EDGE_MOVE_IN) {
            if (mDrawableFloat.left < 0) {
                mDrawableFloat.right = mDrawableFloat.width();
                mDrawableFloat.left = 0;
            }
            if (mDrawableFloat.top < 0) {
                mDrawableFloat.bottom = mDrawableFloat.height();
                mDrawableFloat.top = 0;
            }
            if (mDrawableFloat.right > getWidth()) {
                mDrawableFloat.left = getWidth() - mDrawableFloat.width();
                mDrawableFloat.right = getWidth();
            }
            if (mDrawableFloat.bottom > getHeight()) {
                mDrawableFloat.top = getHeight() - mDrawableFloat.height();
                mDrawableFloat.bottom = getHeight();
            }
            mDrawableFloat.set(mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.right, mDrawableFloat.bottom);
        } else {
            if (mDrawableFloat.left < 0) {
                mDrawableFloat.left = 0;
            }
            if (mDrawableFloat.top < 0) {
                mDrawableFloat.top = 0;
            }
            if (mDrawableFloat.right > getWidth()) {
                mDrawableFloat.right = getWidth();
                mDrawableFloat.left = getWidth() - mDrawableFloat.width();
            }
            if (mDrawableFloat.bottom > getHeight()) {
                mDrawableFloat.bottom = getHeight();
                mDrawableFloat.top = getHeight() - mDrawableFloat.height();
            }
            mDrawableFloat.set(mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.right, mDrawableFloat.bottom);
        }

        mDrawable.setBounds(mDrawableDst);
        mFloatDrawable.setBounds(mDrawableFloat);
    }
```



## 使用方法

使用超简单

### 布局文件中添加控件

```xml
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@color/translucent">

        <com.hcz017.cropimage.CropImageView
            android:id="@+id/cropimage"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_gravity="center"
            android:visibility="gone"/>
    </FrameLayout>
```

PS: 因为我们在java代码中设置控件大小随加载的图片变化，所以这里的layout_width和layout_width属性值意义不大。

### 提供的接口

裁剪控件嘛，两个主要方法，设置图片进去，返回裁减后的图片（bitmap）。

```java
mCropImageView.setDrawable(bitmap, 200, 300);
mCropImageView.getCropImage();
```

### **github地址：**[https://github.com/hcz017/Cropimage_demo2](https://github.com/hcz017/Cropimage_demo2)

感谢看到这里的大家~