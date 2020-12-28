# AndroidApp_XBoxControllerStatusDisplay
This project is an example of android application that display input events from an XBox controller.
The controller is connected via Bluetooth. Displayed events include: pressing buttons and the direction pad, moving left and right joysticks, and pulling left and right shoulder triggers.
Currently working on socket connection between the android app and a python server within LAN, so that the inputs from the controller can be transmitted to the server.

The key layout is given in the official android developer guide at https://developer.android.com/training/game-controllers/controller-input
The KeyEvents mapped are:
            
            KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_BUTTON_X, KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_BUTTON_L1, KeyEvent.KEYCODE_BUTTON_R1,
            KeyEvent.KEYCODE_BUTTON_SELECT, KeyEvent.KEYCODE_BUTTON_START

The motion of joystick and triggers are both reported as MotionEvent, handled in onGenericMotionEvent(MotionEvent event). The two are rather difficult to distinguish.
The joystick and trigger positions are accessed using "float MotionEvent.getAxisValue(int axis)", with axis:

            AXIS_X (X position of the left joystick), AXIS_Y (Y position of the left joystick)
            AXIS_Z (X position of the right joystick), AXIS_RZ (Y position of the right joystick)
            AXIS_BRAKE (Position of the left trigger), AXIS_GAS (Position of the right trigger)
            //These values are normalized 0~1

NOTE:

In the official guide, you can use 
            
            InputDevice.MotionRange range = device.getMotionRange(MotionEvent.AXIS_X, event.getSource())
to get the threshold for non-zero joystick positions. However, if you apply this to AXIS_BRAKE and AXIS_GAS, null will be return (Shoulder triggers don't have thresholds) and skipping the code reading the trigger position.

NOTE:

In the official guide, the processJoystickInput method
            
            private void processJoystickInput(MotionEvent event, int historyPos) {
                        InputDevice inputDevice = event.getDevice();
                        float x = getCenteredAxis(event, inputDevice,
                        MotionEvent.AXIS_X, historyPos);
                        if (x == 0) x = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos);
                        if (x == 0) x = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos);
                        
                        float y = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos);
                        if (y == 0) y = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
                        if (y == 0) y = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos);
	            //Steps necessary using joystick positions 
            }
will checks a series of condtionals to check the x/y returned by getCenteredAxis using different axis parameters and  skip the rest if x/y becomes non-zero. But the problem is the joystick position might give a finite value even when you are not touching (due to mechanical imperfection), and if you simply add 
            
            if (x == 0) x = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_BRAKE, historyPos)

to the end of the conditionals, it might be skipped, preventing you from actually reading the left trigger position.
