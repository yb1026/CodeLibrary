package com.cultivator.codelibrary.widget.signature;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import com.cultivator.codelibrary.R;

import java.util.ArrayList;
import java.util.List;

public class SignatureView extends View {
	private static final float MIN_WIDTH = 0f;
	private static final float MAX_WIDTH = 7f;
	private static final float BASE_WIDTH_PIXELS = 800f;

	// View state
	private List<TimedPoint> mPoints;
	// private boolean mIsEmpty;
	private float mLastTouchX;
	private float mLastTouchY;
	private float mLastVelocity;
	private float mLastWidth;
	private RectF mDirtyRect;

	// Configurable parameters
	private float mMinWidth;
	private float mMaxWidth;
	private float mVelocityFilterWeight;
	private OnGestureListener mOnSignedListener;

	private Paint mPaint = new Paint();
	private Path mPath = new Path();
	static Bitmap mSignatureBitmap = null;
	Canvas mSignatureBitmapCanvas = null;

	public static interface OnGestureListener {
		void onGestureStarted(SignatureView signature, MotionEvent event);

		void onGesture(SignatureView signature, MotionEvent event);

		void onGestureEnded(SignatureView signature, MotionEvent event);

		void onGestureCancelled(SignatureView signature, MotionEvent event);
	}

	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SignatureView, 0, 0);
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metric);
		float multi = metric.widthPixels / BASE_WIDTH_PIXELS;
		// Configurable parameters
		try {
			mMinWidth = a.getFloat(R.styleable.SignatureView_minWidth,
					MIN_WIDTH * multi);
			mMaxWidth = a.getFloat(R.styleable.SignatureView_maxWidth,
					MAX_WIDTH * multi);
			mVelocityFilterWeight = a.getFloat(
					R.styleable.SignatureView_velocityFilterWeight, 0.9f);
			mPaint.setColor(a.getColor(R.styleable.SignatureView_penColor,
					Color.BLACK));
		} finally {
			a.recycle();
		}

		// Fixed parameters
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);

		// Dirty rectangle to update only the changed portion of the view
		mDirtyRect = new RectF();
		clear();
	}

	public void addPoint(TimedPoint newPoint) {
		mPoints.add(newPoint);
		if (mPoints.size() > 2) {
			// To reduce the initial lag make it work with 3 mPoints
			// by copying the first point to the beginning.
			if (mPoints.size() == 3)
				mPoints.add(0, mPoints.get(0));

			ControlTimedPoints tmp = calculateCurveControlPoints(
					mPoints.get(0), mPoints.get(1), mPoints.get(2));
			TimedPoint c2 = tmp.c2;
			tmp = calculateCurveControlPoints(mPoints.get(1), mPoints.get(2),
					mPoints.get(3));
			TimedPoint c3 = tmp.c1;
			Bezier curve = new Bezier(mPoints.get(1), c2, c3, mPoints.get(2));

			TimedPoint startPoint = curve.startPoint;
			TimedPoint endPoint = curve.endPoint;

			float velocity = endPoint.velocityFrom(startPoint);
			velocity = Float.isNaN(velocity) ? 0.0f : velocity;

			velocity = mVelocityFilterWeight * velocity
					+ (1 - mVelocityFilterWeight) * mLastVelocity;

			// The new width is a function of the velocity. Higher velocities
			// correspond to thinner strokes.
			// Log.e(this.getClass().getName(), "velocity="+velocity);
			float newWidth = strokeWidth(velocity);

			// The Bezier's width starts out as last curve's final width, and
			// gradually changes to the stroke width just calculated. The new
			// width calculation is based on the velocity between the Bezier's
			// start and end mPoints.
			// Log.e(this.getClass().getName(),
			// "LastWidth="+mLastWidth+"newWidth="+newWidth);
			addBezier(curve, mLastWidth, newWidth);

			mLastVelocity = velocity;
			mLastWidth = newWidth;

			// Remove the first element from the list,
			// so that we always have no more than 4 mPoints in mPoints array.
			mPoints.remove(0);
		}
	}

	private void addBezier(Bezier curve, float startWidth, float endWidth) {
		// ensureSignatureBitmap();
		float originalWidth = mPaint.getStrokeWidth();
		float widthDelta = endWidth - startWidth;

		float drawSteps = (float) Math.floor(curve.length());

		for (int i = 0; i < drawSteps; i++) {
			// Calculate the Bezier (x, y) coordinate for this step.
			float t = (i) / drawSteps;
			float tt = t * t;
			float ttt = tt * t;
			float u = 1 - t;
			float uu = u * u;
			float uuu = uu * u;

			float x = uuu * curve.startPoint.x;
			x += 3 * uu * t * curve.control1.x;
			x += 3 * u * tt * curve.control2.x;
			x += ttt * curve.endPoint.x;

			float y = uuu * curve.startPoint.y;
			y += 3 * uu * t * curve.control1.y;
			y += 3 * u * tt * curve.control2.y;
			y += ttt * curve.endPoint.y;

			// Set the incremental stroke width and draw.
			mPaint.setStrokeWidth(startWidth + ttt * widthDelta);
			mSignatureBitmapCanvas.drawPoint(x, y, mPaint);
			expandDirtyRect(x, y);
		}

		mPaint.setStrokeWidth(originalWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mSignatureBitmap != null) {
			canvas.drawBitmap(mSignatureBitmap, 0, 0, mPaint);
		}
	}

	public ControlTimedPoints calculateCurveControlPoints(TimedPoint s1,
			TimedPoint s2, TimedPoint s3) {
		float dx1 = s1.x - s2.x;
		float dy1 = s1.y - s2.y;
		float dx2 = s2.x - s3.x;
		float dy2 = s2.y - s3.y;

		TimedPoint m1 = new TimedPoint((s1.x + s2.x) / 2.0f,
				(s1.y + s2.y) / 2.0f);
		TimedPoint m2 = new TimedPoint((s2.x + s3.x) / 2.0f,
				(s2.y + s3.y) / 2.0f);

		float l1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
		float l2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

		float dxm = (m1.x - m2.x);
		float dym = (m1.y - m2.y);
		float k = l2 / (l1 + l2);
		TimedPoint cm = new TimedPoint(m2.x + dxm * k, m2.y + dym * k);

		float tx = s2.x - cm.x;
		float ty = s2.y - cm.y;

		return new ControlTimedPoints(new TimedPoint(m1.x + tx, m1.y + ty),
				new TimedPoint(m2.x + tx, m2.y + ty));
	}

	public float strokeWidth(float velocity) {
		return Math.max(mMaxWidth / (velocity + 1), mMinWidth);
	}

	public void clear() {
		mPoints = new ArrayList<TimedPoint>();
		mLastVelocity = 0f;
		mLastWidth = (mMinWidth + mMaxWidth) / 2;
		mPath.reset();

		
		if(mSignatureBitmapCanvas!=null){
			mSignatureBitmapCanvas.drawColor(Color.WHITE);
		}
		
		// mSignatureBitmap = null;
		invalidate();
	}

	public void recycle(boolean gc) {
		// if (mSignatureBitmap!=null&&!mSignatureBitmap.isRecycled()) {
		// mSignatureBitmap.recycle();
		// mSignatureBitmap=null;
		// }
		if (gc) {
			System.gc();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			mPoints.clear();
			mPath.moveTo(eventX, eventY);
			mLastTouchX = eventX;
			mLastTouchY = eventY;
			addPoint(new TimedPoint(eventX, eventY));
			mOnSignedListener.onGestureStarted(this, event);
			// invalidate();
		case MotionEvent.ACTION_MOVE:
			resetDirtyRect(eventX, eventY);
			addPoint(new TimedPoint(eventX, eventY));
			mOnSignedListener.onGesture(this, event);
			break;

		case MotionEvent.ACTION_UP:
			resetDirtyRect(eventX, eventY);
			addPoint(new TimedPoint(eventX, eventY));
			getParent().requestDisallowInterceptTouchEvent(false);
			mOnSignedListener.onGestureEnded(this, event);
			break;
		default:
			return false;
		}
		invalidate((int) (mDirtyRect.left - mMaxWidth),
				(int) (mDirtyRect.top - mMaxWidth),
				(int) (mDirtyRect.right + mMaxWidth),
				(int) (mDirtyRect.bottom + mMaxWidth));
		return true;
	}

	/**
	 * Called when replaying history to ensure the dirty region includes all
	 * mPoints.
	 */
	private void expandDirtyRect(float historicalX, float historicalY) {
		if (historicalX < mDirtyRect.left) {
			mDirtyRect.left = historicalX;
		} else if (historicalX > mDirtyRect.right) {
			mDirtyRect.right = historicalX;
		}
		if (historicalY < mDirtyRect.top) {
			mDirtyRect.top = historicalY;
		} else if (historicalY > mDirtyRect.bottom) {
			mDirtyRect.bottom = historicalY;
		}
	}

	/**
	 * Resets the dirty region when the motion event occurs.
	 */
	private void resetDirtyRect(float eventX, float eventY) {

		// The mLastTouchX and mLastTouchY were set when the ACTION_DOWN
		// motion event occurred.
		mDirtyRect.left = Math.min(mLastTouchX, eventX);
		mDirtyRect.right = Math.max(mLastTouchX, eventX);
		mDirtyRect.top = Math.min(mLastTouchY, eventY);
		mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
	}

	public void setOnSignedListener(OnGestureListener listener) {
		mOnSignedListener = listener;
	}

	public Bitmap getSignatureBitmap() {
		return getTransparentSignatureBitmap();
		// Bitmap originalBitmap = getTransparentSignatureBitmap();
		// Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
		// originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		// Canvas canvas = new Canvas(whiteBgBitmap);
		// canvas.drawColor(Color.WHITE);
		// canvas.drawBitmap(originalBitmap, 0, 0, null);
		// return whiteBgBitmap;
	}

	public Bitmap getTransparentSignatureBitmap() {
		ensureSignatureBitmap();
		return mSignatureBitmap;
	}

	public void setSignatureBitmap(Bitmap signature) {
		clear();
		ensureSignatureBitmap();

		RectF tempSrc = new RectF();
		RectF tempDst = new RectF();

		int dwidth = signature.getWidth();
		int dheight = signature.getHeight();
		int vwidth = getWidth();
		int vheight = getHeight();

		// Generate the required transform.
		tempSrc.set(0, 0, dwidth, dheight);
		tempDst.set(0, 0, vwidth, vheight);

		Matrix drawMatrix = new Matrix();
		drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);

		Canvas canvas = new Canvas(mSignatureBitmap);
		canvas.drawBitmap(signature, drawMatrix, null);
		invalidate();
	}

	public void ensureSignatureBitmap() {
		if (mSignatureBitmap == null) {
			mSignatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.RGB_565);
		}
		if (mSignatureBitmapCanvas == null) {
			mSignatureBitmapCanvas = new Canvas(mSignatureBitmap);
			mSignatureBitmapCanvas.drawColor(Color.WHITE);
		}
		
	}
}