package net.aboutgoods.remotedrawing;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
<<<<<<< HEAD
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
=======
import android.widget.Button;
import android.widget.RelativeLayout;
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

import net.aboutgoods.remotedrawing.helper.PaintHelper;
import net.aboutgoods.remotedrawing.helper.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements DrawingActivity {

    private SocketHelper mSocketHelper = SocketHelper.getInstance();
    private DrawingView mDrawingView;
<<<<<<< HEAD
    private Button mButton;
    private EditText mEdit;
    private String mRoom;

=======
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
        setContentView(R.layout.welcomescreen);

        mButton = (Button) findViewById(R.id.button_submit);
        mEdit = (EditText) findViewById(R.id.room_input);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mRoom = mEdit.getText().toString();
                if (mRoom.isEmpty()) {
                    mRoom = "General";
                }
                SocketHelper.getInstance().login(MainActivity.this);
                SocketHelper.getInstance().joinRoom(MainActivity.this, mRoom);
            }
        });
    }


=======
        SocketHelper.getInstance().login(MainActivity.this);
    }

>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
    @Override
    public void onLogin(final JSONObject jsonData) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String myColor = jsonData.getString("color");
<<<<<<< HEAD
                    String myRoom = jsonData.getString("room");
                    Paint myPaint = PaintHelper.createPaintFromRGB(myColor);
                    setupView(myPaint, myRoom);
=======
                    Paint myPaint = PaintHelper.createPaintFromRGB(myColor);
                    setupView(myPaint);
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

<<<<<<< HEAD
    private void setupView(Paint paint, String room) {
        mRoom = room;

        setContentView(R.layout.drawinglayout);
        RelativeLayout drawingLayout = (RelativeLayout) findViewById(R.id.drawing);
        mDrawingView = new DrawingView(MainActivity.this, paint);

        TextView textRoom =(TextView) findViewById(R.id.YourRoomText);
        textRoom.setText(getResources().getString(R.string.Room)+" "+ room);
        drawingLayout.addView(mDrawingView);

        Button buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
=======
    private void setupView(Paint paint) {
        RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);

        mDrawingView = new DrawingView(MainActivity.this, paint);
        relativeLayout.addView(mDrawingView);

        Button button = new Button(MainActivity.this);
        button.setText(getString(R.string.clearAll));
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(Color.WHITE);
        button.setPadding(16, 16, 16, 16);
        button.setOnClickListener(new View.OnClickListener() {
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
            @Override
            public void onClick(View v) {
                mDrawingView.clear(MainActivity.this);
                mSocketHelper.clearDrawingSurface();
            }
        });

<<<<<<< HEAD
        Button buttonColor = (Button) findViewById(R.id.buttonColor);
        buttonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocketHelper.getNewColor(MainActivity.this, mDrawingView);
            }
        });

        Button buttonRoom = (Button) findViewById(R.id.buttonRoom);
        buttonRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editRoom = (EditText) findViewById(R.id.editRoom);
                String newRoom = editRoom.getText().toString();
                if (newRoom.isEmpty() ) {
                    newRoom = "General";
                }

                if (!mRoom.equals(newRoom)) {
                    mDrawingView.clear(MainActivity.this);
                    mSocketHelper.clearDrawingSurface();
                    SocketHelper.getInstance().leave(MainActivity.this);
                    SocketHelper.getInstance().joinRoom(MainActivity.this, newRoom);
                    return;
                }

                CharSequence text = "You are already in this Room!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(MainActivity.this, text, duration);
                toast.show();
            };
        });
=======
        relativeLayout.addView(button);
        setContentView(relativeLayout);
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

        mSocketHelper.drawOn(MainActivity.this, mDrawingView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocketHelper.disconnect();
    }
}