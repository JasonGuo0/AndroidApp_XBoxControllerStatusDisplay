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
import java.sql.Connection;
import java.util.HashMap;
import java.lang.Object;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class XBoxTextView extends AppCompatTextView implements InputDeviceListener {
    public Socket socket = null;
    public DataOutputStream out = null;
    private final InputManagerCompat mInputManager;
    //The following HashMap must be initialized before it can use put(Integer, String)
    private HashMap<Integer, String> keyMap = new HashMap<Integer, String>();
    private HashMap<Integer, Boolean> keyStatus = new HashMap<Integer, Boolean>();
    private float[] values = {0, 0, 0, 0, 0, 0};
    private float threshold = 0.0001f;

    public XBoxTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInputManager = InputManagerCompat.Factory.getInputManager(this.getContext());
        mInputManager.registerInputDeviceListener(this, null);
        setupKeyMap();
    }
    public class Pair<T, U> {
        private T first;
        private U second;
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

        keyStatus.put(KeyEvent.KEYCODE_DPAD_UP, false);
        keyStatus.put(KeyEvent.KEYCODE_DPAD_DOWN, false);
        keyStatus.put(KeyEvent.KEYCODE_DPAD_LEFT, false);
        keyStatus.put(KeyEvent.KEYCODE_DPAD_RIGHT, false);
        keyStatus.put(KeyEvent.KEYCODE_DPAD_UP_LEFT, false);
        keyStatus.put(KeyEvent.KEYCODE_DPAD_UP_RIGHT, false);
        keyStatus.put(KeyEvent.KEYCODE_DPAD_DOWN_LEFT, false);
        keyStatus.put(KeyEvent.KEYCODE_DPAD_DOWN_RIGHT, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_A, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_B, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_X, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_Y, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_L1, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_R1, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_SELECT, false);
        keyStatus.put(KeyEvent.KEYCODE_BUTTON_START, false);
    }
    //Handle the connection to server and sending info to server
    private class ConnectionThread extends Thread{
        private String address;
        private int portNum;
        //private boolean[] newFlag;
        public ConnectionThread(String addr, int port) {
            address = addr; portNum = port;
        }
        public void run() {
            Pair<Boolean, String> successful_string = canPing(address);
            setText(successful_string.second);
            if (successful_string.first) {
                setText("Attempting to connect to server" + address);
                try {
                    socket = new Socket(address, portNum);
                    setText("Successfully connected to server at "+address+" "+portNum);
                    out = new DataOutputStream(socket.getOutputStream());
                    out.write("First Communication Test".getBytes());
                }
                catch (IOException e) { setText("Exception: "+e); }
            }
        }
    }
    public void connect2Server(String addr, int port) {
        //boolean[] flag = {false};
        new ConnectionThread(addr, port).start();
    }
    private class SendInfoThread extends Thread {
        private String info;
        public SendInfoThread(String s) {
            info = s;
        }
        public void run() {
            if (out != null && socket != null) {
                try { out.write(info.getBytes()); }
                catch (IOException e){ setText("Exception while sending data"); }
            }
            else setText("DataOutputStream or socket is null, or both are null");
        }
    }
    private void sendInfo2Server(String s) {
        new SendInfoThread(s).start();
    }
    public Pair<Boolean, String> canPing(String addr) {
        Pair<Boolean, String> successful_string = new Pair<Boolean, String>();
        successful_string.first = false; successful_string.second = "Can't ping address "+addr;
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 "+addr);
            successful_string.first = p.waitFor() == 0;
            if (successful_string.first) successful_string.second = "Successfully pinged addredd " + addr;
            return successful_string;
        }
        catch (IOException | InterruptedException e) {
            successful_string.second += "\nException occurred" + e;
            return successful_string;
        }
    }
    //Override the handler of keyDown, keyUp and genericMotion
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int deviceId = event.getSource();
        if (deviceId != -1 && !keyStatus.get(keyCode)) {
            String s = "Device ID: "+deviceId+" key pressed: ";
            s += keyMap.get(keyCode);
            keyStatus.put(keyCode, true);
            setText(s);
            sendInfo2Server(s);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int deviceId = event.getSource();
        if (deviceId != -1 && keyStatus.get(keyCode)) {
            String s = "Device ID: " + deviceId + " key released: ";
            s += keyMap.get(keyCode);
            keyStatus.put(keyCode, false);
            setText(s);
            sendInfo2Server(s);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
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
        String s = "", s_2server = "GenericMotionEvent";
        float LJX = event.getAxisValue(MotionEvent.AXIS_X);
        float LJY = event.getAxisValue(MotionEvent.AXIS_Y);
        float RJX = event.getAxisValue(MotionEvent.AXIS_Z);
        float RJY = event.getAxisValue(MotionEvent.AXIS_RZ);
        float LTrig = event.getAxisValue(MotionEvent.AXIS_BRAKE);
        float RTrig = event.getAxisValue(MotionEvent.AXIS_GAS);
        //If the joysticks are very close to 0 on both dimensions, treat them as 0
        if (LJX < 0.02 && LJY < 0.02) LJX = LJY = 0;
        if (RJX < 0.02 && RJY < 0.02) RJX = RJY = 0;

        float[] newValues = {LJX, LJY, RJX, RJY, LTrig, RTrig};
        if ((valueAbsoluteDistance(newValues) == 0.0 && valueAbsoluteDistance(values) != 0)
            || valueRelativeChange(newValues) > 0.007) {
            s += "Device ID: " + event.getDeviceId() + "\n";
            s += "Source of Event: " + event.getSource() + "\n";
            s += "Action: " + event.getAction() + "\n";
            s += "L Joystick X: " + LJX + "\n" + "L Joystick Y: " + LJY + "\n";
            s += "R Joystick X: " + RJX + "\n" + "R Joystick Y: " + RJY + "\n";
            s += "Left Trigger: " + LTrig + "\n" + "Right Trigger: " + RTrig + "\n";

            s_2server += "LJX " + LJX + " LJY " + RJY;
            s_2server += " RJX " + RJX + " RJY " + RJY;
            s_2server += " LTrig " + LTrig + " RTrig " + RTrig;
            values = newValues;
            sendInfo2Server(s_2server);
            setText(s);
        }
        return super.onGenericMotionEvent(event);
    }
    private double valueAbsoluteDistance(float[] newValues) {
        return Math.sqrt(Math.pow(newValues[0], 2) + Math.pow(newValues[1], 2)+ Math.pow(newValues[2], 2)
                + Math.pow(newValues[3], 2)+ Math.pow(newValues[4], 2) + Math.pow(newValues[5], 2));
    }
    private double valueRelativeChange(float[] newValues) {
        return Math.sqrt(Math.pow(newValues[0]-values[0], 2) + Math.pow(newValues[1]-values[1], 2)
                + Math.pow(newValues[2]-values[2], 2) + Math.pow(newValues[3]-values[3], 2)
                + Math.pow(newValues[4]-values[4], 2) + Math.pow(newValues[5]-values[5], 2));
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
