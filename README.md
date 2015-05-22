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
    
##授权申请
登录https://www.gokuai.com/login 网址，点击后台管理tab，输入后台帐号密码，设置 -> 库开发授权 开启，然后返回 云库 -> 授权管理 ->（点击进行开发的库）-> 授权管理 -> 点击获取ClientID和ClientSecret，记下这个两个参数，在使用SDK的时候，会使用这两个参数

##项目引用
Android Studio 可直接引用master 中 androidsdk Module，Eclispe 、IntelliJ 或者Android Studio 需要使用aar方式引用，需要先在https://github.com/gokuai/yunku-sdk-android/releases/ 下载最新的zip包，步骤如下：

###Android Studio

File>Import Module>，找到对应androidsdk所在的文件夹,并导入到项目

*gradle 1.8+ 建议引用 aar*

File>New Module>More Modules>Import .JAR or .AAR Package ,引用androidsdk.aar文件

**build.gradle**		

	dependencies {
    	...
    	compile project(':androidsdk')
	}
	  

###Eclipse + ADT

1.New>Project>Android>Android Application Project>任意填写包名和名称［例如 androidsdk］,然后将Eclipse和Source Code文件夹下文件复制替换到项目中	
2.将Source Code/assets复制到App运行项目中	
3.导入项目appcompat


###IntelliJ IDEA

1.File>New >Module...>Empty Module任意填写包名和名称［例如 androidsdk］,然后将Eclipse和Source Code文件夹下文件复制替换到项目中	
2.将Source Code/assets复制到App运行项目中	
3.导入项目appcompat

##项目必需设置
**[YourActivity].class**
	
 使用控件的Activity需要继承MainViewBaseActivity

	public class YourActivity extends MainViewBaseActivity{
		……
	}

**[YourApplication].class**
	
需要创建一个自定义的application 继承GKApplication

	public class YourApplication extends GKApplication {

   		 //======================== 这部分需要预先设置==========================
   		 static {

       		 Config.ORG_CLIENT_ID = "[预先申请的CLIENT_ID]";
       		 Config.ORG_CLIENT_SECRET = "[预先申请的CLIENT_SECRET]";

        	 Config.ORG_ROOT_PATH = "";//访问文件的根目录
       		 Config.ORG_ROOT_TITLE = "MyTitle";//根目录
       		 Config.ORG_OPT_NAME = "Brandon";//操作人，例如文件上传、改名、删除等
    	}

    	//===================================================================



    	//在这里添加自己要执行的代码

	}


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
    
    OR
    
    <style name="YourTheme" parent="Theme.AppCompat.Light">
        <!-- Customize your theme here. -->
    </style>
    
    OR
    
    <style name="YourTheme" parent="Theme.AppCompat.Light.DarkActionBar"">
        <!-- Customize your theme here. -->
    </style>

##类的使用说明
###YKMainView类

#### 构造
new YKMainView(Context context)		
context需要为MainViewBaseActivity 继承的Activity的实例

####setOption（Option option）
设置开启的功能（文件重命名、文件删除、文件上传）


####initData（）
数据初始化，在所有参数设置完毕之后，最后进行初始化

###Option类
| 属性 | 说明 |
| --- | --- |
| canDel | 是否开启删除 | 
| canRename | 是否开启重命名 | 
| canUpload | 是否可上传 | 

###FileDataManager类

####registerHook(HookCallback callback)
注册hook,可以控制指定路径的文件创建、列表显示、文件上传、文件重命名、文件删除是否可以被允许执行

###HookCallback接口
	boolean hookInvoke(HookType type, String fullPath);
	
| 参数 | 类型 |说明 |
| --- | --- | --- |
| type | HookType |  hook回调的类型 |
| fullPath | string |  执行操作的路径 |


###HookType枚举
| 枚举类型 | 说明 |
| --- | --- |
| HOOK_TYPE_FILE_LIST | 文件列表显示 | 
| HOOK_TYPE_DOWNLOAD | 文件下载 | 
| HOOK_TYPE_UPLOAD | 文件上传 | 
| HOOK_TYPE_CREATE_DIR |创建文件夹 |
| HOOK_TYPE_RENAME | 重命名 |
| HOOK_TYPE_DELETE | 文件删除 |
    
##相关SDK
https://github.com/gokuai/yunku-sdk-java




