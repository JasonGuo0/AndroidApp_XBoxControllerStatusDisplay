package com.example.xboxcontroller;

import android.content.Context;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.os.Handler;

public interface InputManagerCompat {
    InputDevice getInputDevice(int id);
    int[] getInputDeviceIds();
    void registerInputDeviceListener(InputManagerCompat.InputDeviceListener listener,
                                     Handler handler);
    void unregisterInputDeviceListener(InputManagerCompat.InputDeviceListener listener);
    //3 methods that won't be used
    void onGenericMotionEvent(MotionEvent event);

    interface InputDeviceListener {
        void onInputDeviceAdded(int deviceId);
        void onInputDeviceRemoved(int deviceId);
        void onInputDeviceChanged(int deviceId);
    }
    class Factory {
        public static InputManagerCompat getInputManager(Context context) {
            return new InputManagerV16(context);
        }
    }
}
