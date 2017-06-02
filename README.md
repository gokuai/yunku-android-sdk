/*
Title:够快云库Android SDK使用说明
Description:
Author: Brandon
Date: 2015/05/20
Robots: noindex,nofollow
*/
# 够快云库Android SDK使用说明

[![](https://jitpack.io/v/gokuai/yunku-sdk-android.svg)](https://jitpack.io/#gokuai/yunku-sdk-android)

版本：1.0.4

创建：2016-05-8

## Demo

<img src="/Screenshot/1.png" alt="文件列表" title="文件列表" width="35%" height="35%" />

<img src="/Screenshot/2.png" alt="重命名、删除" title="重命名、删除" width="35%" height="35%" />	

<img src="/Screenshot/3.png" alt="上传文件" title="上传文件" width="35%" height="35%" />

## 场景使用声明
此SDK包含界面交互，适用客户端快入内嵌使用，包含文件列表、文件下载、预览、上传、文件删除和重命名功能，如果是基于文件管理的接口开发，请查看 [yunku-sdk-java] [1]

## 兼容性声明

	minSdkVersion 14
    
## 授权申请
登录[云库][2]，点击后台管理tab，输入后台帐号密码，设置 -> 库开发授权 开启，然后返回 云库 -> (选择要申请开发的库) -> 授权管理 ->（点击进行开发的库）-> 授权管理 -> 获取`ClientID`和`ClientSecret`

## 项目引用

### Gradle

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```groovy
	dependencies {
	        compile 'com.github.gokuai:yunku-sdk-android:v_1.0.4'
	}
```

### Maven

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

```xml
	<dependency>
	    <groupId>com.github.gokuai</groupId>
	    <artifactId>yunku-sdk-android</artifactId>
	    <version>v_1.0.4</version>
	</dependency>
```

Android Studio 可直接引用master 中 androidsdk Module，Eclispe 、IntelliJ 或者Android Studio 需要使用aar方式引用，需要先在https://github.com/gokuai/yunku-sdk-android/releases/ 下载最新的zip包，步骤如下：

### Android Studio

File>Import Module>，找到对应androidsdk所在的文件夹,并导入到项目

*gradle 1.8+ , Android Studio 1.2+ 建议引用 aar*

File>New Module>More Modules>Import .JAR or .AAR Package ,引用androidsdk.aar文件

**build.gradle**		

	dependencies {
    	...
    	compile project(':androidsdk')
	}
	  

### Eclipse + ADT

* 1.New>Project>Android>Android Application Project>任意填写包名和名称［例如 androidsdk］,然后将Eclipse和Source Code文件夹下文件复制替换到项目中	
* 2.将Source Code/assets复制到App运行项目中	
* 3.导入项目appcompat


### IntelliJ IDEA

* 1.File>New >Module...>Empty Module任意填写包名和名称［例如 androidsdk］,然后将Eclipse和Source Code文件夹下文件复制替换到项目中	
* 2.将Source Code/assets复制到App运行项目中	
* 3.导入项目appcompat

## 项目必需设置
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

               <!--============================使用云库3.0API需要注册以下Activity==============================-->
               <!--文件上传选择-->
               <activity android:name="com.gokuai.yunkuandroidsdk.FileUploadActivity" />
       
               <!--Gknote 笔记工具-->
               <activity
                   android:name="com.gokuai.yunkuandroidsdk.GKNoteEditorActivity"
                   android:windowSoftInputMode="adjustResize" />
       
               <!--图片类型预览-->
               <activity
                   android:name="com.gokuai.yunkuandroidsdk.GalleryUrlActivity"
                   android:configChanges="orientation|screenSize"
                   android:launchMode="singleTop"
                   android:uiOptions="splitActionBarWhenNarrow" />
       
               <!--文件类型预览-->
               <activity
                   android:name="com.gokuai.yunkuandroidsdk.PreviewActivity"
                   />
       
               <!--============================使用云库2.0API需要注册以下Activity==============================-->
               <!--文件上传选择-->
               <activity android:name="com.gokuai.yunkuandroidsdk.compat.v2.FileUploadActivity" />
       
               <!--Gknote 笔记工具-->
               <activity
                   android:name="com.gokuai.yunkuandroidsdk.compat.v2.GKNoteEditorActivity"
                   android:windowSoftInputMode="adjustResize" />
       
               <!--图片类型预览-->
               <activity
                   android:name="com.gokuai.yunkuandroidsdk.compat.v2.GalleryUrlActivity"
                   android:configChanges="orientation|screenSize"
                   android:launchMode="singleTop"
                   android:uiOptions="splitActionBarWhenNarrow" />
       
               <!--文件类型预览-->
               <activity
                   android:name="com.gokuai.yunkuandroidsdk.compat.v2.PreviewActivity"
                   />
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

## 类的使用说明

## 兼容使用
* 使用云库2.0API: `package com.gokuai.yunkuandroidsdk.compat.v2`

* 使用云库3.0API: `package com.gokuai.yunkuandroidsdk`

### YKMainView类

#### 构造
new YKMainView(Context context)		
context需要为MainViewBaseActivity 继承的Activity的实例

#### setOption（Option option）
设置开启的功能（文件重命名、文件删除、文件上传）


#### initData（）
数据初始化，在所有参数设置完毕之后，最后进行初始化

### Option类
| 属性 | 说明 |
| --- | --- |
| canDel | 是否开启删除 | 
| canRename | 是否开启重命名 | 
| canUpload | 是否可上传 | 

### FileDataManager类

#### registerHook(HookCallback callback)
注册hook,可以控制指定路径的文件创建、列表显示、文件上传、文件重命名、文件删除是否可以被允许执行

### HookCallback接口
	boolean hookInvoke(HookType type, String fullPath);
	
| 参数 | 类型 |说明 |
| --- | --- | --- |
| type | HookType |  hook回调的类型 |
| fullPath | string |  执行操作的路径 |


### HookType枚举
| 枚举类型 | 说明 |
| --- | --- |
| HOOK _TYPE _ FILE _LIST | 文件列表显示 | 
| HOOK _TYPE _DOWNLOAD | 文件下载 | 
| HOOK _TYPE _UPLOAD | 文件上传 | 
| HOOK _TYPE _CREATE _DIR |创建文件夹 |
| HOOK _TYPE _RENAME | 重命名 |
| HOOK _TYPE _DELETE | 文件删除 |

### DocConvertManager类

#### 获取预览文档转化信息
转化对应库文件路径文档为pdf，返回下载地址
	
	getPreviewInfo(Context context, String fullPath, PreviewInfoListener listener)


| 参数 | 类型 |说明 |
| --- | --- | --- |
| context | Context |   |
| fullPath | string |  执行操作的路径 |
| listener | PreviewInfoListener |  监听转化系列情况的接口 |

#### 取消请求
可以在请求发出之后，取消请求

	cancel()
	
### PreviewInfoListener接口
#### 转化状态

	onStatus(String fullPath, int status)
	

| 参数 | 类型 |说明 |
| --- | --- | --- |
| fullPath | string |  执行操作的路径 |
| status | int |  转化状态 |

| status | 说明 |
| --- | --- |
| STATUS _CODE _ANALYZE _SERVER | 分析服务器地址 | 
| STATUS _CODE _START _TO _CONVERT _PDF | 开始转化为PDF | 
| STATUS_CODE_COMPLETE | 转化完毕 | 

#### 转化进度
返回文档转化进度

	onProgress(int percent)

| 参数 | 类型 |说明 |
| --- | --- | --- |
| percent | int |  转化进度返回 0－100 |

#### 返回错误信息

	onError(int errorCode, String fullPath, String message)
	
| 参数 | 类型 |说明 |
| --- | --- | --- |
| errorCode | int |  错误号 |
| fullPath | string |  执行操作的路径 |
| message | string |  错误描述 |

| error code | 说明 |
| --- | --- |
| ERROR _CODE _UNSUPPORTED| 101:文件类型不支持 | 
| ERROR _CODE _GET _FILE _INFO _ERROR | 102:获取文件信息错误 | 
| ERROR _CODE _FILE _CONVERT _ERROR | 103:文件转化失败 | 
| ERROR _CODE _INCOMPLETE | 104:转化未完成，需要等待转化文档 | 
	
#### 返回转化完毕的PDF文档下载地址

	onGetPDFUrl(String fullPath, String url)

| 参数 | 类型 |说明 |
| --- | --- | --- |
| fullPath | string |  执行操作的路径 |
| url | string |  pdf文件下载地址 |


    
## 相关SDK
https://github.com/gokuai/yunku-sdk-java

[1]: https://github.com/gokuai/yunku-sdk-java
[2]: https://www.gokuai.com/login


