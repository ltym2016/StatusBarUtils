# StatusBarUtils
## Android 沉浸式状态栏适配
#### Android 4.4之前
在Android系统4.4以前，状态栏的背景色和字体颜色都是不能改变的。默认为黑色。

<img src="https://github.com/ltym2016/StatusBarUtils/blob/master/image/4.3.png" width="350"/><img src="https://github.com/ltym2016/StatusBarUtils/blob/master/image/4.3.1.png" width="350"/>

#### Android 4.4到5.0(不包括5.0)并且非miui和非Flyme系统
在Android系统4.4到5.0(不包括5.0)并且非miui和非Flyme系统，状态栏的背景和字体颜色由系统决定，这里不做兼容。正常有下面两种样式。

<img src="https://github.com/ltym2016/StatusBarUtils/blob/master/image/4.3.png" width="350"/><img src="https://github.com/ltym2016/StatusBarUtils/blob/master/image/4.4.png" width="350"/>

#### Android 5.x系列并且非miui和非Flyme系统
在Android5.系列的系统上，可以设置状态栏的颜色，但不可以改变状态栏字体和图标的颜色。所以这里统一设置为半透明效果，可以通过setStatusBarColor设置状态栏的背景色，但是部分华为机型不支持setStatusBarColor的方式，只能是黑色，至少保证状态栏图标可见。

<img src="https://github.com/ltym2016/StatusBarUtils/blob/400c0dcd783143b3daf06317d2bb3dfd0687a4b5/image/5.0.png" width="350"/><img src="https://github.com/ltym2016/StatusBarUtils/blob/400c0dcd783143b3daf06317d2bb3dfd0687a4b5/image/5.1.png" width="350"/>
部分华为机型不支持setStatusBarColor的方式，只能是黑色

<img src="https://github.com/ltym2016/StatusBarUtils/blob/master/image/4.3.png" width="350"/><img src="https://github.com/ltym2016/StatusBarUtils/blob/master/image/4.3.1.png" width="350"/>

这里如果想设置状态栏的颜色和标题栏颜色一样，可以在标题栏上方放一个View，动态的设置view的高度为状态栏的高度。

#### Android  4.4以上的MIUI和Flyme以及Android6.0以上系统
4.4以上的MIUI和Flyme系统以及Android6.0以上系统机型适配沉浸式状态栏，可以通过StatusBarUtils里面相关方法设置状态栏字体和图标的颜色切换

**miui 小米**

<img src="https://github.com/ltym2016/StatusBarUtils/blob/400c0dcd783143b3daf06317d2bb3dfd0687a4b5/image/miui.png" width="350"/><img src="https://github.com/ltym2016/StatusBarUtils/blob/400c0dcd783143b3daf06317d2bb3dfd0687a4b5/image/miui1.png" width="350"/>

**flyme 魅族**

<img src="https://github.com/ltym2016/StatusBarUtils/blob/400c0dcd783143b3daf06317d2bb3dfd0687a4b5/image/meizu.png" width="350"/><img src="https://github.com/ltym2016/StatusBarUtils/blob/400c0dcd783143b3daf06317d2bb3dfd0687a4b5/image/meizu1.png" width="350"/>

### StatusBarUtils如何使用
#### 步骤一
```
implementation 'com.samlu:statusbarutils:1.0.0'

```

#### 步骤二
- 直接在Activity中设置Window属性
```@Override
protected void onCreate(Bundle savedInstanceState) {
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
    super.onCreate(savedInstanceState);
}
```
- 或者在xml的style文件中设置：
```
<item name="android:windowTranslucentStatus">true</item> 
android:fitsSystemWindows="true" 
```
- 使用android:windowTranslucentStatus属性需要在res目录下新建values-v19文件夹，style文件要放在里面

#### 步骤三
- 在BaseActivity的onCreate里面调用StatusBarUtils.transparencyBar(this);开启沉浸式状态栏

#### 步骤四
- 在需要设置状态栏黑白字体的Activity里面调用如下方法
```
// 设置状态栏黑色字体图标 适配4.4以上版本的MIUI、Flyme和6.0以上版本其他Android
StatusBarUtils.StatusBarIconDark(Activity activity)
// 设置状态栏白色字体图标 适配4.4以上版本的MIUI、Flyme和6.0以上版本其他Android
StatusBarUtils.StatusBarIconLight(Activity activity)
```
