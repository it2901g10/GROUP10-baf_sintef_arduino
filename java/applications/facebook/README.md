
Facebook social service
-------------------------------------------------------------------------------

About:
A social service implementation for Facebook.

Building:
To build the Facebook social service you will need to have the following
installed in your system:

1. A JDK
2. Android SDK
3. Facebook SDK for Android
4. Apache Ant
5. Social library*

* It will be built automatically by Ant if correctly referenced in project.properties.
  Use the RELATIVE path (to this directory)*

If you have all of the following then:

1. Follow the instruction in the Facebook SDK to build their Facebook project.
See: https://developers.facebook.com/docs/mobile/android/build/
This step is required only once.
2. Change the location of the Facebook project in project.properties to point
the project built for step 1. Use the RELATIVE path (to this directory).
3. Change the location of the Android SDK in local.properties to point your
Android SDK.
4. Run either 'ant debug' or 'ant release' depending on which version you
want to build. Keep in mind that to you will need to sign an APK built with
'ant release'
