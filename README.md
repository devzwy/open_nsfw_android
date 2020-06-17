# open_nsfw_android
[![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Base-TensorFlow-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-赵文贇-orange.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/QQ-3648415-brightgreen.svg)](https://github.com/devzwy/KUtils)  [![](https://img.shields.io/badge/Mail-dev_zwy@aliyun.com-green.svg)](https://github.com/devzwy/open_nsfw_android) ![Mozilla Add-on](https://img.shields.io/amo/stars/dustman.svg?label=stars&logo=1&logoColor=1&style=popout)

### 色情图片离线识别，基于TensorFlow实现。识别只需20ms,可断网测试，成功率99%，调用只要一行代码，从雅虎的开源项目open_nsfw移植，该模型文件可用于iOS、java、C++等平台
### iOS请参考：[issues13](https://github.com/devzwy/open_nsfw_android/issues/13)
### Python参考：[NSFW-Python](https://github.com/devzwy/NSFW-Python)、[Python-TensorflowLite-Api](https://tensorflow.google.cn/api_docs/python/tf/lite)、[Python-Tensorflow-Api](https://tensorflow.google.cn/api_docs/python/tf)
### Java参考:[Tensorflow-Api](https://tensorflow.google.cn/api_docs/java/reference/org/tensorflow/package-summary)
### C++参考:[TensorflowLite-Api](https://tensorflow.google.cn/lite/api_docs/cc)
### JavaScript参考[JS相关文档](https://js.tensorflow.org/api/latest/)
>>> 其中Python、C++均有两种数据喂入的方式，可根据需求选择pb模型或tfLite文件，详细请参考上面的链接.Java的目前只能加载pb模型。其他的平台可自行[百度](https://www.baidu.com)
### 本项目移除测试图片，请下载Demo后自行配图测试  
#### 即将优化Demo使用MVVM模式，可用作开发脚手架使用  
`Kotlin+okhttp3+rxjava2+retrofit2+koin+glide+greendao+databinding+Livedata`  
  
![MVVM](https://github.com/devzwy/open_nsfw_android/blob/dev/img/4.jpg)


#### 1.3.5版本优化说明：  
模型大小改动较大，原量化模型虽小(6M)，但对GPU加速支持不友好，新模型大约23M，完美支持GPU加速并优化识别精度，加速效果明显。建议全部升级该版本。__新版本的GPU加速默认开启状态__，SDK默认会检测设备是否支持，不支持时会自动取消加速(老版本会奔溃)    
#### 1.3.4版本特殊说明：  
由于模型支持GPU加速后模型文件变大，部分用户反馈Apk体积增大，考虑到这方面，在1.3.4版本中增加初始化方式，可通过传入模型文件的路径进行初始化，该模型在`/OpenNSFW/src/main/assets/nsfw.tflite`处，可自行下载并存放适当位置，比如放在后台，app端自行下载后初始化NSFW后使用。初始化方式：
```
        Classifier.Build()
//            .context(this) //1.3.4版本可不用调用该代码。其他版本必须调用，否则会有异常抛出
//            .isOpenGPU(true)//默认不开启GPU加速，默认为true
//            .numThreads(100) //分配的线程数 根据手机配置设置，默认1
            .nsfwModuleFilePath("/data/user/0/com.zwy.demo/files/nsfw.tflite") //1.3.4版本必须配置模型存放路径，否则会有异常抛出
            .build()
```  


### 开始使用
- 添加远程仓库支持
```
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
```

- 配置依赖(如果需要自行配置模型路径可适用1.3.4版本，否则请使用最新版本，版本号看右边的icon中的数字) [![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android) （编译过程报错时请自行使用梯子）

```
	dependencies {
	         //versionCode：上面小icon中最新版本号
	        implementation 'com.github.devzwy:open_nsfw_android:[versionCode]'
	}

```

- 以下配置1.3.4版本可跳过
__除1.3.4版本外，其他任何版本均需要添加如下代码，否则会有`java.io.FileNotFoundException: This file can not be opened as a file descriptor; it is probably compressed`异常抛出，因为assets下模型文件较大__
```
  android {
        aaptOptions {
            noCompress "tflite"
        }
  }
```  


- 建议在Application中全局初始化

```
        Classifier.Build()
            .context(this) //1.3.4版本可不用调用该代码。其他版本必须调用，否则会有异常抛出
//            .isOpenGPU(true)//默认不开启GPU加速，默认为true
//            .numThreads(100) //分配的线程数 根据手机配置设置，默认1
//            .nsfwModuleFilePath("/data/user/0/com.zwy.demo/files/nsfw.tflite") //1.3.4版本必须配置模型存放路径，否则会有异常抛出
            .build()
```
- 使用：

```  
         //方式一：
        val nsfwBean = Classifier.Build().context(this).build().run(bitmap)
        //方式二
        val nsfwBean = bitmap.getNsfwScore()
        //方式三
        val nsfwBean = file.getNsfwScore()

        nsfwBean.sfw   ... 非涉黄数值 数值越大约安全
        nsfwBean.nsfw   ... 涉黄数值  数值越大约危险
```
### 安卓手机直接[点我安装](http://d.6short.com/mwfv)

### 扫码下载

![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/2.png)

### Demo运行结果：  

![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/1.png)
