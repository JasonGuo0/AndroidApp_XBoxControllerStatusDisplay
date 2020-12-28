package com.example.xboxcontroller;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import com.example.xboxcontroller.InputManagerCompat.InputDeviceListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.annotation.Target;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.lang.Object;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class XBoxTextView extends AppCompatTextView implements InputDeviceListener {
    public Socket socket = null;
    public DataOutputStream out = null;
    private final InputManagerCompat mInputManager;
    //The following HashMap must be initialized before it can use put(Integer, String)
    private HashMap<Integer, String> keyMap = new HashMap<Integer, String>();

    public XBoxTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInputManager = InputManagerCompat.Factory.getInputManager(this.getContext());
        mInputManager.registerInputDeviceListener(this, null);
        setupKeyMap();
        //connect2Server();
    }
    public void connect2Server() {
        try {
            String address = "192.168.1.252";
            int port = 65432;
            setText("Attempting to connect to server" + address);
            socket = new Socket(address, port);
            setText("Successfully connected to server at "+address+" "+port);
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            setText("Exception: "+e);
        }
    }
    private void setupKeyMap() {
        keyMap.put(KeyEvent.KEYCODE_DPAD_UP, "Dpad up");
        keyMap.put(KeyEvent.KEYCODE_DPAD_DOWN, "Dpad down");
        keyMap.put(KeyEvent.KEYCODE_DPAD_LEFT, "Dpad left");
        keyMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, "Dpad right");
        keyMap.put(KeyEvent.KEYCODE_DPAD_UP_LEFT, "Dpad up left");
        keyMap.put(KeyEvent.KEYCODE_DPAD_UP_RIGHT, "Dpad up right");
        keyMap.put(KeyEvent.KEYCODE_DPAD_DOWN_LEFT, "Dpad down left");
        keyMap.put(KeyEvent.KEYCODE_DPAD_DOWN_RIGHT, "Dpad down right");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_A, "Button A");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_B, "Button B");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_X, "Button X");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_Y, "Button Y");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_L1, "Button L1");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_R1, "Button R1");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_SELECT, "Button Select");
        keyMap.put(KeyEvent.KEYCODE_BUTTON_START, "Button Start");
    }
    private void sendInfo2Server(String s) {
        if (out != null && socket != null) {
            byte[] byteStream = s.getBytes();
            try { out.write(byteStream); }
            catch (IOException e) { setText(s); }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int deviceId = event.getSource();
        if (deviceId != -1) {
            String s = "Device ID: "+deviceId+" key: ";
            s += keyMap.get(keyCode);
            sendInfo2Server(s);
            s += "\nSend to server";
            setText(s);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //@Override
    //public boolean onKeyUp(int keyCode, KeyEvent event) {
    //    int deviceId = event.getSource();
    //    if (deviceId != -1) {
    //        return true;
    //    }
    //    return super.onKeyUp(keyCode, event);
    //}
    private boolean isSourceFromDPad(int eventSource) {
        //Why not just compare eventSource and InputDevice.SOURCE_GAMEPAD?
        return (eventSource & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD;
    }
    private boolean isSourceFromJoyStick(int eventSource) {
        //Why not just compare eventSource and InputDevice.SOURCE_GAMEPAD?
        return (eventSource & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
    }
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        mInputManager.onGenericMotionEvent(event);
        int eventSource = event.getSource();
        String s = "", s_2server = "";
        float LJoystick_X = event.getAxisValue(MotionEvent.AXIS_X);
        float LJoystick_Y = event.getAxisValue(MotionEvent.AXIS_Y);
        float RJoystick_X = event.getAxisValue(MotionEvent.AXIS_Z);
        float RJoystick_Y = event.getAxisValue(MotionEvent.AXIS_RZ);
        float LTrigger = event.getAxisValue(MotionEvent.AXIS_BRAKE);
        float RTrigger = event.getAxisValue(MotionEvent.AXIS_GAS);
        //If the joysticks are very close to 0 on both dimensions, treat them as 0
        if (LJoystick_X < 0.05 && LJoystick_Y < 0.05) LJoystick_X = LJoystick_Y = 0;
        if (RJoystick_X < 0.05 && RJoystick_Y < 0.05) RJoystick_X = RJoystick_Y = 0;

        s += "Device ID: " + event.getDeviceId() + "\n";
        s += "Source of Event: " + event.getSource() + "\n";
        s += "Action: " + event.getAction() + "\n";
        s += "L Joystick X: " + LJoystick_X + "\n" + "L Joystick Y: " + LJoystick_Y + "\n";
        s += "R Joystick X: " + RJoystick_X + "\n" + "R Joystick Y: " + RJoystick_Y + "\n";
        s += "Left Trigger: " + LTrigger + "\n" + "Right Trigger: " + RTrigger + "\n";

        s_2server += LJoystick_X + ',' + LJoystick_Y + ',';
        s_2server += RJoystick_X + ',' + RJoystick_Y + ',';
        s_2server += LTrigger + ',' + RTrigger + ',';
        sendInfo2Server(s_2server);
        s += "\nSend to server";
        setText(s);
        return super.onGenericMotionEvent(event);
    }
    public class Pair<T, U> {
        public T first;
        public U second;
    }
    private void processJoystickInput(MotionEvent event, int historyPos) {
        //Respond to joystick position
        //Many game pads with two joysticks report the position of the second joystick using Z and RZ axes.
        InputDevice device = event.getDevice();
        Pair<Float, Boolean> value_notTrigger_x = getCenteredAxis(event, device, MotionEvent.AXIS_X, historyPos);
        if (value_notTrigger_x.first == 0) value_notTrigger_x = getCenteredAxis(event, device, MotionEvent.AXIS_HAT_X, historyPos);
        if (value_notTrigger_x.first == 0) value_notTrigger_x = getCenteredAxis(event, device, MotionEvent.AXIS_Z, historyPos);
        if (value_notTrigger_x.first == 0) value_notTrigger_x = getCenteredAxis(event, device, MotionEvent.AXIS_LTRIGGER, historyPos);

        //Get the position of the y dimension
        Pair<Float, Boolean> value_notTrigger_y = getCenteredAxis(event, device, MotionEvent.AXIS_Y, historyPos);
        if (value_notTrigger_y.first == 0) value_notTrigger_y = getCenteredAxis(event, device, MotionEvent.AXIS_HAT_Y, historyPos);
        if (value_notTrigger_y.first == 0) value_notTrigger_y = getCenteredAxis(event, device, MotionEvent.AXIS_RZ, historyPos);
        if (value_notTrigger_y.first == 0) value_notTrigger_y = getCenteredAxis(event, device, MotionEvent.AXIS_RTRIGGER, historyPos);
        String s;
        //if (device.getMotionRange(MotionEvent.AXIS_BRAKE, event.getSource()) != null) hasRange = true;
        //if (device.getMotionRange(MotionEvent.AXIS_GAS, event.getSource()) != null) hasRange = true;
        if (value_notTrigger_x.second) s = "NotTrigger X "+value_notTrigger_x.first+" Y "+value_notTrigger_y.first;
        else s = "IsTrigger "+value_notTrigger_x.first+" Y "+value_notTrigger_y.first;
        setText(s);
        byte[] byteStream = s.getBytes();
        if (out != null && socket != null)
            try {
                out.write(byteStream);
            }
            catch (IOException e) {
                setText(s);
            }
    }
    private Pair<Float, Boolean> getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        Pair<Float, Boolean> pair = new Pair();
        pair.first = 0f; pair.second = false;
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        //If the device doesn't have the axis, then it returns a null range.
        if (range != null) {
            final float flat = range.getFlat(); //Get the range where joystick is treated as at center
            final float value = historyPos < 0 ? event.getAxisValue(axis)
                    : event.getHistoricalAxisValue(axis, historyPos);
            //If the joystick is within the flat region, return 0
            if (value > flat || value < -flat) {
                pair.first = value;
                pair.second = true;
            }
        }
        else {
            final float value = historyPos < 0 ? event.getAxisValue(axis)
                    : event.getHistoricalAxisValue(axis, historyPos);
            pair.second = false;
            if (value != 0f) pair.first = value;
            else pair.first = -0.01f;
        }
        return pair;
    }
    @Override
    public void onInputDeviceAdded(int deviceId) {}
    @Override
    public void onInputDeviceRemoved(int deviced) {}
    @Override
    public void onInputDeviceChanged(int deviceId) {}
}
