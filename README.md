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
### [pb模型下载](https://github.com/devzwy/NSFW-Python/tree/master/model)
### 本项目移除测试图片，请下载Demo后自行配图测试  
#### 即将优化Demo使用MVVM模式，可用作开发脚手架使用  
`Kotlin+okhttp3+rxjava2+retrofit2+koin+glide+greendao+databinding+Livedata`  
  
![MVVM](https://github.com/devzwy/open_nsfw_android/blob/dev/img/4.jpg)


#### 1.3.2版本优化说明：  
模型大小改动较大，原量化模型虽小(6M)，但对GPU加速支持不友好，新模型大约26M，完美支持GPU加速并优化识别精度，加速效果明显。建议全部升级该版本。__新版本的GPU加速默认开启状态__，SDK默认会检测设备是否支持，不支持时会自动取消加速(老版本会奔溃)  

### 使用方式一 （建议）
- 添加远程仓库支持
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

- 配置依赖 [![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android) （编译过程报错时请自行使用梯子）

```
	dependencies {
	         //versionCode：上面小icon中最新版本号
	        implementation 'com.github.devzwy:open_nsfw_android:[versionCode]'
	}

```

### 使用方式二，源码依赖方式
- 克隆源码
```
	git clone 'https://github.com/devzwy/open_nsfw_android.git'
```

- 项目中依赖lib

```
	dependencies {
	        implementation project(path: ':OpenNSFW')
	}

```
- 扫描时报如下错误
```
java.lang.NullPointerException: Attempt to invoke virtual method 'com.zwy.nsfw.api.NsfwBean com.zwy.nsfw.Classifier.run(android.graphics.Bitmap)' on a null object reference
```
__请添加__
```
  android {
        ...
        aaptOptions {
            noCompress "tflite"
        }
  }
```  


- 使用(请使用最新版本1.3.2)
- 建议在Application中全局初始化

```
        Classifier.Build()
            .context(this) //必须调用 否则会有异常抛出
//            .isOpenGPU(true)//是否开启GPU加速 ,默认true
//            .numThreads(10) //分配的线程数 根据手机配置设置，默认1
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
### 安卓手机直接[点我安装](http://d.7short.com/1rj9)

### 扫码下载

![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/2.png)

### Demo运行结果：  

![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/1.png)
