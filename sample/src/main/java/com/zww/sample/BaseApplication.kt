package com.zww.sample

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.kongzue.dialog.util.DialogSettings
import com.zww.sample.model.networkModule
import com.zww.sample.model.repositoryModule
import com.zww.sample.model.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        Utils.init(this)

        //全局注入对象
        startKoin {
            androidContext(this@BaseApplication)
            androidLogger()
            modules(networkModule)
            modules(repositoryModule)
            modules(viewModelModules)
        }

        initDialogStyle()
    }


    private fun initDialogStyle() {

//        DialogSettings.isUseBlur = (boolean);                   //是否开启模糊效果，默认关闭
//        DialogSettings.modalDialog = (boolean);                 //是否开启模态窗口模式，一次显示多个对话框将以队列形式一个一个显示，默认关闭
        DialogSettings.style =
            DialogSettings.STYLE.STYLE_IOS //全局主题风格，提供三种可选风格，STYLE_MATERIAL, STYLE_KONGZUE, STYLE_IOS
        DialogSettings.theme = DialogSettings.THEME.LIGHT //全局对话框明暗风格，提供两种可选主题，LIGHT, DARK
        DialogSettings.tipTheme = DialogSettings.THEME.LIGHT //全局提示框明暗风格，提供两种可选主题，LIGHT, DARK
        //        DialogSettings.titleTextInfo = new TextInfo("");              //全局对话框标题文字样式
//        DialogSettings.menuTitleInfo = (TextInfo);              //全局菜单标题文字样式
//        DialogSettings.menuTextInfo = (TextInfo);               //全局菜单列表文字样式
//        DialogSettings.contentTextInfo = (TextInfo);            //全局正文文字样式
//        DialogSettings.buttonTextInfo = (TextInfo);             //全局默认按钮文字样式
//        DialogSettings.buttonPositiveTextInfo = (TextInfo);     //全局焦点按钮文字样式（一般指确定按钮）
//        DialogSettings.inputInfo = (InputInfo);                 //全局输入框文本样式
//        DialogSettings.backgroundColor = (ColorInt);            //全局对话框背景颜色，值0时不生效
        DialogSettings.cancelable =
            true //全局对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipDialog）以及等待框（TipDialog）
        DialogSettings.cancelableTipDialog = true //全局提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
        DialogSettings.DEBUGMODE = true //是否允许打印日志
        //        DialogSettings.blurAlpha = (int);                       //开启模糊后的透明度（0~255）
//        DialogSettings.systemDialogStyle = (styleResId);        //自定义系统对话框style，注意设置此功能会导致原对话框风格和动画失效
//        DialogSettings.dialogLifeCycleListener = (DialogLifeCycleListener);  //全局Dialog生命周期监听器
//        DialogSettings.defaultCancelButtonText = (String);      //设置 BottomMenu 和 ShareDialog 默认“取消”按钮的文字
//        DialogSettings.tipBackgroundResId = (drawableResId);    //设置 TipDialog 和 WaitDialog 的背景资源
//        DialogSettings.tipTextInfo = (InputInfo);               //设置 TipDialog 和 WaitDialog 文字样式
        DialogSettings.autoShowInputKeyboard = false //设置 InputDialog 是否自动弹出输入法
        //        DialogSettings.okButtonDrawable = (drawable);           //设置确定按钮背景资源
//        DialogSettings.cancelButtonDrawable = (drawable);       //设置取消按钮背景资源
//        DialogSettings.otherButtonDrawable = (drawable);        //设置其他按钮背景资源

        //检查 Renderscript 兼容性，若设备不支持，DialogSettings.isUseBlur 会自动关闭；
//        boolean renderscriptSupport = DialogSettings.checkRenderscriptSupport(this);
        DialogSettings.init() //初始化清空 BaseDialog 队列
    }
}