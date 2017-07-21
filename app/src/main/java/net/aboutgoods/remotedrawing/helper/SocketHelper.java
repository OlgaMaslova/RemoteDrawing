package net.aboutgoods.remotedrawing.helper;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Path;

import net.aboutgoods.remotedrawing.DrawingActivity;
import net.aboutgoods.remotedrawing.DrawingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * The singleton Socket helper.
 */
public class SocketHelper {


    private static final String HOST = "http://192.168.1.60:3000";
    private static SocketHelper mInstance = null;
    private LinkedHashMap<String, String> mUserList;
    private Socket mSocket;
    private String mRoomName;
    private String mName;


    private SocketHelper() {

        try {
            mSocket = IO.socket(HOST);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();

    }

    /**
     * Get instance socket helper.
     *
     * @return the socket helper
     */

    public static SocketHelper getInstance(){
        if(mInstance == null) {

            mInstance = new SocketHelper();
        }
        return mInstance;
    }

    public void disconnect() {
        mSocket.off();
        mSocket.disconnect();
        mInstance = null;
    }


    /*
        * Logs in, gets socket.id
        * @param activity the activity
         */
    public void login (String pseudo) {
        JSONObject jsonPseudo = new JSONObject();
        try {
            jsonPseudo.put("pseudo", pseudo);
            mSocket.emit("login", jsonPseudo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                drawingView.getPathFromJson(jsonData, activity);
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

     * Gets the color, joins the room
     *
     * @param activity the activity
     * @param room the room
     *
     */
    public void joinRoom (final Activity activity, String room, String name) {

        mRoomName = room;
        mName = name;
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("room", mRoomName);
            jsonData.put("name", mName);
            mSocket.emit("joinRoom", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.on("me", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        JSONObject jsonData = (JSONObject) args[0];
                        if (activity instanceof DrawingActivity) {
                            ((DrawingActivity) activity).onLogin(jsonData);
                            return;
                        }
                        throw new StackOverflowError(activity.getLocalClassName() + " must implement DrawingActivity");
                    }
        });
    }
    /*
    * Leaves the room
    * @param activity
    *
     */
    public void leaveRoom (final Activity activity) {
        mSocket.emit("leave");
    }


    /**
     * Clear drawing surface.
     */
    public void clearDrawingSurface(DrawingView drawingView) {
        
        mSocket.emit("clear");
    }

    /**
     * Clear drawing surface in particular room
     */
    public void clearRoom (String room) {
        JSONObject jsonRoom = new JSONObject();
        try {
            jsonRoom.put("room", room);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("clearRoom", jsonRoom);
    }
    /* Get the complete drawing and send it to the server

     */



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




