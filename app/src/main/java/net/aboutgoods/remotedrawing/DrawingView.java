package net.aboutgoods.remotedrawing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Toast;
import net.aboutgoods.remotedrawing.helper.PaintHelper;
import net.aboutgoods.remotedrawing.helper.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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
    private Paint paintNewColor;
    private Path mCirclePath;
    private String mBackgroundColor = "#424242";
    private float mX, mY;
    private boolean mEraser = false;
    private Toast mToast;
    private ArrayList<Path> mPaths = new ArrayList<>();
    private String mPseudo;
    private String mPreviousPseudo = null;



    /**
     * Instantiates a new Drawing view.
     *
     * @param activity the activity
     * @param paint    the paint
     */
    public DrawingView(final Activity activity, Paint paint) {
        super(activity);
        this.mContext = activity;
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

        // Changes brush of Blue Print for Eraser
        if (mEraser){
            canvas.drawPath(mPath, PaintHelper.getEraserBluePrintPaint());
        }

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
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
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
          @Override
                public void run() {
                    invalidate();
                }
            });

    }

    /**
     * Draw line from json.
     *
     * @param json     the json
     * @param activity the activity
     */
    public void getPathFromJson(JSONObject json, Activity activity) {
        try {
            if (mCanvas == null) return;

             JSONObject jsonCoordinates = json.getJSONObject("coordinates");
             mPseudo = json.getString("drawer");
             String colorOfDrawer = json.getString("color");
             Paint paint = PaintHelper.createPaintFromRGB(colorOfDrawer);
             Path path = new Path();

             JSONObject jsonOldCoordinate = jsonCoordinates.getJSONObject("old");
             JSONObject jsonNewCoordinate = jsonCoordinates.getJSONObject("new");

             float oldX = (float) jsonOldCoordinate.getDouble("x");
             float oldY = (float) jsonOldCoordinate.getDouble("y");
             float newX = (float) jsonNewCoordinate.getDouble("x");
             float newY = (float) jsonNewCoordinate.getDouble("y");

             path.moveTo(oldX, oldY);
             path.quadTo(oldX, oldY, (newX+ oldX)/2, (newY + oldY)/2);
             path.lineTo(newX, newY);
             mPaths.add(path);

             mCanvas.drawPath(path, paint);

             activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String toastText =  mPseudo.concat(" is drawing");
                    int duration = Toast.LENGTH_SHORT;
                    mToast = Toast.makeText(mContext, toastText, duration);
                    mToast.setGravity(Gravity.BOTTOM, 50, 50);
                    if (!mPseudo.equals(mPreviousPseudo)) {
                        mToast.show();
                    }
                    mPreviousPseudo = mPseudo;
                    invalidate();
                }
             });

        } catch (JSONException e) {
            Log.e(TAG, "getPathFromJson: " + e.getMessage());
        }

    }

    /**
     * Set a new  color
     */
    public void setPaintNewColor(JSONObject json, final Activity activity) {
         if (mEraser) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mToast.cancel();
                }
            });
         }
         try {
            String newColor = json.getString("color");
            this.paintNewColor = PaintHelper.createPaintFromRGB(newColor);
            mEraser = json.getBoolean("eraser");
            if (mEraser) {
                this.paintNewColor.setStrokeWidth(50);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CharSequence text = "You are Eraser!!";
                        int duration = Toast.LENGTH_SHORT;
                        mToast = Toast.makeText(mContext, text, duration);
                        mToast.show();
                        invalidate();
                    }
                });
            }
         } catch (JSONException e) {
            Log.e(TAG, "setPaintNewColor: " + e.getMessage());
         }

         this.mLinePaint = this.paintNewColor;

    }

    public void setNewBackgroundColor (JSONObject json, final Activity activity) {
        try {
            String newBackgroundColor = json.getString("color");

            mCanvas.drawColor(Color.parseColor(newBackgroundColor));

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "setPaintNewColor: " + e.getMessage());
        }
    }

    public String getBackgroundColor (){
       return mBackgroundColor;
    }

/*
Show all drawings of the user in this Room
 */
    public void showDrawing (Activity activity) {
        if (mPaths.isEmpty()) {
            mCanvas.drawColor(Color.parseColor(mBackgroundColor));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (final Path p: mPaths) {
                    mCanvas.drawPath(p, mLinePaint);
                }
                invalidate();
            }
        });
    }



}
