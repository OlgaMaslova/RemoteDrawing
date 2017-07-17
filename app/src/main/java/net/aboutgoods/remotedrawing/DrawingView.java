package net.aboutgoods.remotedrawing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
<<<<<<< HEAD
import android.widget.Toast;


=======
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

import net.aboutgoods.remotedrawing.helper.PaintHelper;
import net.aboutgoods.remotedrawing.helper.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Drawing view.
 */
public class DrawingView extends View {

    private static final String TAG = "DrawingView";

    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Context mContext;
    private Paint mLinePaint;
<<<<<<< HEAD
    private Paint paintNewColor;
    private Path mCirclePath;
    private String mBackgroundColor = "#424242";
    private float mX, mY;
    private String mMessage = "";
=======
    private Path mCirclePath;
    private String mBackgroundColor = "#424242";
    private float mX, mY;
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

    /**
     * Instantiates a new Drawing view.
     *
     * @param activity the activity
     * @param paint    the paint
     */
    public DrawingView(final Activity activity, Paint paint) {
        super(activity);
<<<<<<< HEAD
        this.mContext = activity;
=======
        this.mContext =activity;
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
        this.mPath = new Path();
        this.mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        this.mLinePaint = paint;
        this.mCirclePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.parseColor(mBackgroundColor));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, PaintHelper.getBluePrintPaint());
        canvas.drawPath(mCirclePath, PaintHelper.getCirclePaint());
<<<<<<< HEAD
        // Changes brush of Blue Print for Eraser
        if (!mMessage.isEmpty()){
            canvas.drawPath(mPath, PaintHelper.getEraserBluePrintPaint());
        }
=======
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            SocketHelper.getInstance().sendCoordinate(mX, mY, x, y);
<<<<<<< HEAD
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
=======
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
            mCirclePath.reset();
            mCirclePath.addCircle(mX, mY, 60, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCirclePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mLinePaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    /**
     * Clear canvas.
     *
     * @param activity the activity
     */
    public void clear(Activity activity) {

        if (mCanvas == null) return;

        mCanvas.drawColor(Color.parseColor(mBackgroundColor));
        activity.runOnUiThread(new Runnable() {
<<<<<<< HEAD
            @Override
            public void run() {
                invalidate();
            }
        });
=======
                @Override
                public void run() {
                    invalidate();
                }
            });
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

    }

    /**
     * Draw line from json.
     *
     * @param json     the json
     * @param activity the activity
     */
    public void drawFromJson(JSONObject json, Activity activity) {
        try {

            if (mCanvas == null) return;

            JSONObject jsonCoordinates = json.getJSONObject("coordinates");
            String userId = json.getString("drawer");

            if (userId != null && !userId.isEmpty()) {

                Paint paint = SocketHelper.getInstance().getPaintColorFromUserId(userId);
<<<<<<< HEAD

                if (paint == null)
                    return;

=======
                if (paint == null) return;
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

                JSONObject jsonOldCoordinate = jsonCoordinates.getJSONObject("old");
                JSONObject jsonNewCoordinate = jsonCoordinates.getJSONObject("new");

                float oldX = (float) jsonOldCoordinate.getDouble("x");
                float oldY = (float) jsonOldCoordinate.getDouble("y");

                float newX = (float) jsonNewCoordinate.getDouble("x");
                float newY = (float) jsonNewCoordinate.getDouble("y");

                Path path = new Path();
                path.moveTo(oldX, oldY);
                path.lineTo(newX, newY);
<<<<<<< HEAD

                mCanvas.drawPath(path, paint);

                path.reset();
=======
                mCanvas.drawPath(path, paint);
                path.reset();

>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }
        } catch (JSONException e) {
            Log.e(TAG, "drawFromJson: " + e.getMessage());
        }
    }
<<<<<<< HEAD


    /**
     * Set a new  color
     */
    public void setPaintNewColor(JSONObject json, final Activity activity) {
         try {
            String newColor = json.getString("color");
            this.paintNewColor = PaintHelper.createPaintFromRGB(newColor);
            mMessage = json.getString("message");
            if (!mMessage.isEmpty()) {
                this.paintNewColor.setStrokeWidth(50);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CharSequence text = "You are Eraser!!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(activity, text, duration);
                        toast.show();
                    }
                });
            }
         } catch (JSONException e) {
            Log.e(TAG, "drawFromJson: " + e.getMessage());
         }
         this.mLinePaint = this.paintNewColor;
    }

    public String getBackgroundColor (Activity activity){
       return mBackgroundColor;
    }
}








=======
}
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
