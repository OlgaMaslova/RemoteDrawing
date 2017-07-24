package net.aboutgoods.remotedrawing;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.aboutgoods.remotedrawing.helper.PaintHelper;
import net.aboutgoods.remotedrawing.helper.SocketHelper;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.id.button1;

public class MainActivity extends Activity implements DrawingActivity {

    private SocketHelper mSocketHelper = SocketHelper.getInstance();
    private DrawingView mDrawingView;
    private String mRoom;
    private String mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.welcomescreen);

        Button submit = (Button) findViewById(R.id.button_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.room_input);
                EditText editText1 = (EditText) findViewById(R.id.nicknameInput);
                mRoom = editText.getText().toString();
                mName = editText1.getText().toString();
                if (mRoom.isEmpty()) {
                    mRoom = "General";
                }
                if (mName.isEmpty()) {
                    CharSequence text = "Enter your nickname!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(MainActivity.this, text, duration);
                    toast.show();
                    return;
                }
                SocketHelper.getInstance().login(MainActivity.this, mName);

            }
        });

    }

    public  void checkName(final JSONObject jsonData) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    Boolean userExists = jsonData.getBoolean("userExists");
                    if (userExists) {

                        CharSequence text = "This nickname is already taken!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, text, duration);
                        toast.show();
                        

                    } else {
                        SocketHelper.getInstance().joinRoom(MainActivity.this, mRoom, mName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            });

    }

    @Override
    public void onLogin(final JSONObject jsonData) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    String myColor = jsonData.getString("color");
                    String myRoom = jsonData.getString("room");
                    Paint myPaint = PaintHelper.createPaintFromRGB(myColor);
                    setupView(myPaint, myRoom);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


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
            @Override
            public void onClick(View v) {
                mSocketHelper.clearRoom(MainActivity.this, mDrawingView, mRoom);
            }
        });

        Button buttonClearAll = (Button) findViewById(R.id.buttonClearAll);
        buttonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocketHelper.clearDrawingSurface();
                mDrawingView.clear(MainActivity.this);
            }
        });


        Button buttonColor = (Button) findViewById(R.id.buttonColor);
        buttonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocketHelper.getNewColor(MainActivity.this, mDrawingView);
            }
        });

        Button buttonBackground = (Button) findViewById(R.id.Background);
        buttonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocketHelper.newBackground(MainActivity.this, mDrawingView, mRoom);
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
                    SocketHelper.getInstance().leaveRoom(MainActivity.this);
                    SocketHelper.getInstance().joinRoom(MainActivity.this, newRoom, mName);
                    return;
                }

                CharSequence text = "You are already in this Room!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(MainActivity.this, text, duration);
                toast.show();
            };
        });

        Button buttonReceive = (Button) findViewById(R.id.buttonReceive);
        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mDrawingView.showDrawing(MainActivity.this);
            }
        });
        mSocketHelper.drawOn(MainActivity.this, mDrawingView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocketHelper.disconnect();
    }
}