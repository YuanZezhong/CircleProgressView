package com.yuanyi.circleprogressview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yuanyi.circleprogressview.R;

import java.util.Arrays;

/**
 * Created by YuanZezhong on 2017/4/11.
 */
public class CircleProgressView extends View {
    public static final String TAG = "myview";
    public static final int CONTENT_TYPE_TEXT = 0;
    public static final int CONTENT_TYPE_BITMAP = 1;
    public static final int CONTENT_TYPE_DRAWABLE = 2;
    public static final int DEFAULT_TEXT_SIZE = 10;     // dp
    public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final int DEFAULT_BORDER_WIDTH = 0;    // dp
    public static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
    public static final int DEFAULT_CONTENT_BACKGROUND_COLOR = Color.TRANSPARENT;
    public static final int[] DEFAULT_PROGRESS_COLORS = {Color.TRANSPARENT, Color.TRANSPARENT};
    public static final int DEFAULT_MAX_PROGRESS = 100;

    private float mBorderWidth;
    private int mBorderColor;
    private int mContentBackgroundColor;
    private int mContentType;
    private String mContentText;
    private float mContentTextSize;
    private int mContentTextColor;
    private int[] mProgressColors;
    private int mMaxProgress;
    private int mProgress;
    private Paint mProgressPaint = new Paint();
    private Paint mContentPaint = new Paint();
    private Bitmap mContentBitmap;
    private Drawable mContentDrawable;

    public CircleProgressView(Context context) {
        super(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        mBorderWidth = typedArray.getDimension(R.styleable.CircleProgressView_border_width, dpToPx(DEFAULT_BORDER_WIDTH));
        Log.d(TAG, "borderWidth: " + mBorderWidth);
        mBorderColor = typedArray.getColor(R.styleable.CircleProgressView_border_color, DEFAULT_BORDER_COLOR);
        Log.d(TAG, "defaultBorderColor: " + Integer.toHexString(mBorderColor));
        mContentBackgroundColor = typedArray.getColor(R.styleable.CircleProgressView_content_background, DEFAULT_CONTENT_BACKGROUND_COLOR);
        Log.d(TAG, "contentBackgroundColor: " + Integer.toHexString(mContentBackgroundColor));
        mProgressColors = obtainProgressColors(context, typedArray);
        Log.d(TAG, "progressColors: " + Arrays.toString(mProgressColors));
        mMaxProgress = typedArray.getInt(R.styleable.CircleProgressView_max_progress, DEFAULT_MAX_PROGRESS);
        Log.d(TAG, "maxProgress: " + mMaxProgress);
        mProgress = typedArray.getInt(R.styleable.CircleProgressView_progress, 0);
        Log.d(TAG, "progress: " + mProgress);
        mContentType = typedArray.getInt(R.styleable.CircleProgressView_content_type, CONTENT_TYPE_TEXT);

        Log.d(TAG, "contentType: " + mContentType);
        mContentText = typedArray.getString(R.styleable.CircleProgressView_content_text);
        Log.d(TAG, "contentText: " + mContentText);
        mContentTextSize = typedArray.getDimension(R.styleable.CircleProgressView_content_text_size, DEFAULT_TEXT_SIZE);
        Log.d(TAG, "contentTextSize: " + mContentTextSize);
        mContentTextColor = typedArray.getColor(R.styleable.CircleProgressView_content_text_color, DEFAULT_TEXT_COLOR);
        Log.d(TAG, "contentTextColor: " + Integer.toHexString(mContentTextColor));

        int drawableId = typedArray.getResourceId(R.styleable.CircleProgressView_content_drawable, 0);
        if (drawableId > 0) {
            mContentDrawable = ContextCompat.getDrawable(context, drawableId);
        }

        int bitmapId = typedArray.getResourceId(R.styleable.CircleProgressView_content_bitmap, 0);
        if (bitmapId > 0) {
            mContentBitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId);
        }
        typedArray.recycle();
        initPaints();
    }

    private int[] obtainProgressColors(Context context, TypedArray attrArray) {
        int[] result = DEFAULT_PROGRESS_COLORS;
        int progressColorsId = attrArray.getResourceId(R.styleable.CircleProgressView_progress_colors, 0);
        if (progressColorsId > 0) {
            TypedArray colorArray = context.getResources().obtainTypedArray(progressColorsId);
            if (colorArray.length() > 0) {
                if (colorArray.length() == 1) {
                    int color = colorArray.getColor(0, Color.TRANSPARENT);
                    result = new int[]{color, color};
                } else {
                    result = new int[colorArray.length()];
                    for (int i = 0; i < colorArray.length(); i++) {
                        result[i] = colorArray.getColor(i, Color.TRANSPARENT);
                    }
                }
            }
            colorArray.recycle();
        }
        return result;
    }

    private void initPaints() {
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setDither(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        mContentPaint.setAntiAlias(true);
        mContentPaint.setDither(true);
        mContentPaint.setStyle(Paint.Style.FILL);
        mContentPaint.setTextAlign(Paint.Align.CENTER);
    }

    private Shader generateShader() {
        return new LinearGradient(0, 0, getWidth(), getHeight(),
                mProgressColors, null, Shader.TileMode.MIRROR);
    }

    private float computeAngle() {
        float result = 0f;
        if (mMaxProgress > 0) {
            if (mProgress > mMaxProgress) {
                mProgress = mMaxProgress;
            }
            float maxProgressF = (float) this.mMaxProgress;
            result = mProgress / maxProgressF * 360;
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // TODO: 2017/4/12 待修改
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        mProgressPaint.setColor(mBorderColor);
        mProgressPaint.setStrokeWidth(mBorderWidth);
        mProgressPaint.setShader(null);
        RectF progressRectF = new RectF(mBorderWidth/2, mBorderWidth/2, width - mBorderWidth/2, height - mBorderWidth/2);
        canvas.drawArc(progressRectF, 0, 360, false, mProgressPaint);
        mProgressPaint.setShader(generateShader());
        canvas.drawArc(progressRectF, -90, computeAngle(), false, mProgressPaint);
        if (mContentType == CONTENT_TYPE_TEXT) {
            drawText(canvas);
        } else if (mContentType == CONTENT_TYPE_BITMAP) {
            drawBitmap(canvas);
        } else if (mContentType == CONTENT_TYPE_DRAWABLE) {
            drawDrawable(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        mContentPaint.setColor(mContentTextColor);
        mContentPaint.setTextSize(mContentTextSize);
        Paint.FontMetrics fontMetrics = mContentPaint.getFontMetrics();
        float top = fontMetrics.top;    //为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;  //为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (getHeight()/2 - top/2 - bottom/2);   //基线中间点的y轴计算公式
        canvas.drawText(mContentText, getWidth() / 2, baseLineY, mContentPaint);
    }

    private void drawBitmap(Canvas canvas) {
        if (mContentBitmap != null) {
            int width = getWidth();
            int height = getHeight();
            float bitmapWidth = mContentBitmap.getWidth();
            float bitmapHeight = mContentBitmap.getHeight();
            RectF rectF = new RectF(width / 2 - bitmapWidth / 2, height / 2 - bitmapHeight / 2,
                    width / 2 + bitmapWidth / 2, height / 2 + bitmapHeight / 2);
            canvas.drawBitmap(mContentBitmap, null, rectF, mContentPaint);
        }
    }

    private void drawDrawable(Canvas canvas) {
        if (mContentDrawable != null) {
            int width = getWidth();
            int height = getHeight();
            int drawableWidth = mContentDrawable.getIntrinsicWidth();
            int drawableHeight = mContentDrawable.getIntrinsicHeight();
            Rect rect = new Rect(width / 2 - drawableWidth / 2, height / 2 - drawableHeight / 2,
                    width / 2 + drawableWidth / 2, height / 2 + drawableHeight / 2);
            mContentDrawable.setBounds(rect);
            mContentDrawable.draw(canvas);
        }
    }

    private int dpToPx(int dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int getContentType() {
        return mContentType;
    }

    public void setContentType(int contentType) {
        if (contentType != mContentType) {
            switch (contentType) {
                case CONTENT_TYPE_TEXT:
                case CONTENT_TYPE_BITMAP:
                case CONTENT_TYPE_DRAWABLE:
                    mContentType = contentType;
                    invalidate();
                    break;
                default:
                    // 无效
                    break;
            }
        }
    }

    public void setContentText(String text) {
        mContentText = text;
        if (mContentType == CONTENT_TYPE_TEXT) {
            invalidate();
        }
    }

    public String getContentText() {
        return mContentText;
    }

    public void setContentTextColor(int color) {
        mContentTextColor = color;
        if (mContentType == CONTENT_TYPE_TEXT) {
            invalidate();
        }
    }

    public int getContentTextColor() {
        return mContentTextColor;
    }

    public void setContentTextSize(float size) {
        mContentTextSize = size;
        if (mContentType == CONTENT_TYPE_TEXT) {
            invalidate();
        }
    }

    public float getContentTextSize() {
        return mContentTextSize;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        invalidate();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgressAndContentText(int progress, String text) {
        mProgress = progress;
        mContentText = text;
        invalidate();
    }

    public void setBorderWidth(float size) {
        mBorderWidth = size;
        invalidate();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
        invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setProgressColors(int[] colors) {
        if (colors == null || colors.length < 2) {
            // TODO: 2017/4/12 参数处理
        }
        mProgressColors = colors;
        invalidate();
    }

    public int[] getProgressColors() {
        return mProgressColors;
    }

    public void setContentDrawable(Drawable drawable) {
        mContentDrawable = drawable;
        if (mContentType == CONTENT_TYPE_DRAWABLE) {
            invalidate();
        }
    }

    public Drawable getContentDrawable() {
        return mContentDrawable;
    }

    public void setContentBitmap(Bitmap bitmap) {
        mContentBitmap = bitmap;
        if (mContentType == CONTENT_TYPE_BITMAP) {
            invalidate();
        }
    }

    public Bitmap getContentBitmap() {
        return mContentBitmap;
    }
}
