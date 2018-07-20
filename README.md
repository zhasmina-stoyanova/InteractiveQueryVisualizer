# Interactive Query Visualizer
An application that can be used to visualize data on mobile devices based on preferences.

## Version of the software:
- Android Studio 3.1.3
- the OS on the testing Android phone is Jelly Bean

## Starting the application
To be able to start the application the following conditions must be met:
1. Android device with min API 15: Android 4.0.3 (IceCreamSandwich).
2. Installed USB Drivers for the connected device (depends on the device).
3. Developer options for the device available with USB debugging turned on.
4. Allow installation of apps from unknown sources on the device Settings -> Security - > Unknown sources.
4. A working web service with InteractiveQueryVisualizerWS.war deployed on a server. 
   The aaddress on which the phone is listening is http://192.168.42.16:8080.
5. Runing the application by choosing the connected device from Select Deployment Target window in Android Studio.
