/*
Title:够快云库Android SDK使用说明
Description:
Author: Brandon
Date: 2015/05/20
Robots: noindex,nofollow
*/
# 够快云库Android SDK使用说明

版本：1.0.0

创建：2015-05-20
##兼容性声明

	minSdkVersion 14
    targetSdkVersion 22

##项目引用及设置
###Android Studio

File>Import Module,引用androidsdk项目

*gradle 1.8+ 建议*

File>New Module>More Modules>Import .JAR or .AAR Package ,引用androidsdk.aar文件

**build.gradle**		

	dependencies {
    	...
    	compile project(':androidsdk')
	}
	  

###Eclipse + ADT

###IntelliJ IDEA



**AndroidManifest.xml**

	权限注册：
	……
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	……
	
	Application设置：
	……
	<application
        android:name=".YourApplication"
        android:icon="@mipmap/ic_launcher"
        android:theme="@style/YourTheme" >
        <activity
            android:name=".YourActivity"
            android:label="@string/app_name" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!--============================需要注册以下Activity==============================-->
        <!--文件上传选择-->
        <activity android:name="com.gokuai.yunkuandroidsdk.FileUploadActivity" />

        <!--Gknote 笔记工具-->
        <activity
            android:name="com.gokuai.yunkuandroidsdk.GKNoteEditorActivity"
            android:windowSoftInputMode="adjustResize" />
        <!--==================================结束======================================-->
    </application>
    ……
**styles.xml**
	
	需要设置Theme.AppCompat的style主题，	
	
    <style name="YourTheme" parent="Theme.AppCompat>
        <!-- Customize your theme here. -->
    </style>
    
    or
    
    <style name="YourTheme" parent="Theme.AppCompat.Light">
        <!-- Customize your theme here. -->
    </style>
    
    or
    
    <style name="YourTheme" parent="Theme.AppCompat.Light.DarkActionBar"">
        <!-- Customize your theme here. -->
    </style>


##初始化
