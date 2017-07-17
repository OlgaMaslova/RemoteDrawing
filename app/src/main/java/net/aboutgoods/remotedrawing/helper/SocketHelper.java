package net.aboutgoods.remotedrawing.helper;

import android.app.Activity;
import android.graphics.Paint;
<<<<<<< HEAD
=======

>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
import net.aboutgoods.remotedrawing.DrawingActivity;
import net.aboutgoods.remotedrawing.DrawingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * The singleton Socket helper.
 */
public class SocketHelper {

<<<<<<< HEAD
    private static final String HOST = "http://192.168.1.60:3000";
    private static SocketHelper mInstance = null;
    private LinkedHashMap<String, String> mUserList;
    private Socket mSocket;
    private String mRoomName;

=======
    private static final String HOST = "http://192.168.1.95:3000";
    private static SocketHelper mInstance = null;
    private LinkedHashMap<String, String> mUserList;
    private Socket mSocket;
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510

    private SocketHelper() {

        try {
            mSocket = IO.socket(HOST);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
<<<<<<< HEAD
        mSocket.connect();
=======

        mSocket.connect();

        mSocket.on("userList", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray jsonArray = (JSONArray) args[0];
                mUserList = new LinkedHashMap<String, String>();
                for(int i=0; i<jsonArray.length(); i++) {
                    try {
                        JSONObject jsonUser = jsonArray.getJSONObject(i);
                        mUserList.put(jsonUser.getString("id"), jsonUser.getString("color"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
    }

    /**
     * Get instance socket helper.
     *
     * @return the socket helper
     */
<<<<<<< HEAD
    public static SocketHelper getInstance() {
        if (mInstance == null) {
=======
    public static SocketHelper getInstance(){
        if(mInstance == null) {
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
            mInstance = new SocketHelper();
        }
        return mInstance;
    }

    public void disconnect() {
        mSocket.off();
        mSocket.disconnect();
        mInstance = null;
    }

    /**
     * Send coordinate.
     *
     * @param oldX the old x
     * @param oldY the old y
     * @param newX the new x
     * @param newY the new y
     */
    public void sendCoordinate(float oldX, float oldY, float newX, float newY) {
        JSONObject jsonCoordinate = new JSONObject();

        try {
            JSONObject jsonOldCoordinate = new JSONObject();
            jsonOldCoordinate.put("x", oldX);
            jsonOldCoordinate.put("y", oldY);

            JSONObject jsonNewCoordinate = new JSONObject();
            jsonNewCoordinate.put("x", newX);
            jsonNewCoordinate.put("y", newY);

            jsonCoordinate.put("old", jsonOldCoordinate);
            jsonCoordinate.put("new", jsonNewCoordinate);

            mSocket.emit("drawing", jsonCoordinate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draw on view.
     *
     * @param activity    the activity
     * @param drawingView the drawing view
     */
    public void drawOn(final Activity activity, final DrawingView drawingView) {

        mSocket.on("receiveDrawing", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonData = (JSONObject) args[0];
                drawingView.drawFromJson(jsonData, activity);
            }
        });

        mSocket.on("clear", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                drawingView.clear(activity);
            }
        });
    }

    /**
<<<<<<< HEAD
     * Gets the color, joins the room
     *
     * @param activity the activity
     * @param room the room
     *
     */
    public void joinRoom (final Activity activity, String room) {

        mRoomName = room;
        JSONObject jsonRoom = new JSONObject();
        try {
            jsonRoom.put("room", mRoomName);
            mSocket.emit("joinRoom", jsonRoom);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.on("me", new Emitter.Listener() {
=======
     * Login.
     *
     * @param activity the activity
     */
    public void login(final Activity activity) {

        Emitter.Listener listener = new Emitter.Listener() {
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
            @Override
            public void call(Object... args) {

                JSONObject jsonData = (JSONObject) args[0];

<<<<<<< HEAD
                if (activity instanceof DrawingActivity) {
                    ((DrawingActivity) activity).onLogin(jsonData);
                    return;
                }
                throw new StackOverflowError(activity.getLocalClassName() + " must implement DrawingActivity");
                }
        });

        mSocket.on("userList", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray jsonArray = (JSONArray) args[0];
                mUserList = new LinkedHashMap<String, String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonUser = jsonArray.getJSONObject(i);
                        mUserList.put(jsonUser.getString("id"), jsonUser.getString("color"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    /*
    * Leaves the room
    * @param activity
    *
     */
    public void leave (final Activity activity) {
        mSocket.emit("leave");
    }

    /*
    * Logs in, gets socket.id
    * @param activity the activity
    * @param drawingView the drawing view
     */
    public void login (final Activity activity) {
=======
                if(activity instanceof DrawingActivity) {
                    ((DrawingActivity) activity).onLogin(jsonData);
                } else {
                    throw new StackOverflowError(activity.getLocalClassName() + " must implement DrawingActivity");
                }
            }
        };

        mSocket.on("me", listener);
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
        mSocket.emit("login", "");
    }

    /**
     * Clear drawing surface.
     */
    public void clearDrawingSurface() {
        mSocket.emit("clear");
    }

    /**
     * Gets paint color from user id.
     *
     * @param userId the user id
     * @return the paint color from user id
     */
    public Paint getPaintColorFromUserId(String userId) {

        if (mUserList == null) return null;

        String color = mUserList.get(userId);

<<<<<<< HEAD

=======
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
        if (color == null) return null;

        return PaintHelper.createPaintFromRGB(color);
    }
<<<<<<< HEAD

    /**
     * Gets new color from the server
     * @param activity the activity
     * @param drawingView the drawing view
     */


    public void getNewColor(final Activity activity, final DrawingView drawingView) {

        String backgroundColor = drawingView.getBackgroundColor(activity);
        JSONObject jsonBackground = new JSONObject();

        try {
            jsonBackground.put("color", backgroundColor);
            mSocket.emit("colorChanging", jsonBackground);
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.on("newColor", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonUserColor = (JSONObject) args[0];
                drawingView.setPaintNewColor(jsonUserColor, activity);
                }
        });

    }
}




=======
}
>>>>>>> 73479522a4da535a1f12065714569d7c133e8510
