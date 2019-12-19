# open_nsfw_android
[![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Base-TensorFlow-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-赵文贇-orange.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/QQ-3648415-brightgreen.svg)](https://github.com/devzwy/KUtils) [![](https://img.shields.io/badge/微信-admin_zwy-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Mail-dev_zwy@aliyun.com-green.svg)](https://github.com/devzwy/open_nsfw_android) ![Mozilla Add-on](https://img.shields.io/amo/stars/dustman.svg?label=stars&logo=1&logoColor=1&style=popout)

### 色情图片离线识别，基于TensorFlow实现。识别只需200ms,可断网测试，成功率99%，调用只要一行代码，从雅虎的开源项目open_nsfw移植，tflite（6M）为训练好的模型（已量化），该模型文件可用于iOS、java、C++等平台，Python使用生成的tfLite文件检测图片的速度远远快于使用原模型. 
### iOS请参考：[issues13](https://github.com/devzwy/open_nsfw_android/issues/13)
### Python参考：[NSFW-Python](https://github.com/devzwy/NSFW-Python)、[Python-TensorflowLite-Api](https://tensorflow.google.cn/api_docs/python/tf/lite)、[Python-Tensorflow-Api](https://tensorflow.google.cn/api_docs/python/tf)
### Java参考:[Tensorflow-Api](https://tensorflow.google.cn/api_docs/java/reference/org/tensorflow/package-summary)
### C++参考:[TensorflowLite-Api](https://tensorflow.google.cn/lite/api_docs/cc)
### JavaScript参考[JS相关文档](https://js.tensorflow.org/api/latest/)
>>> 其中Python、C++均有两种数据喂入的方式，可根据需求选择pb模型或tfLite文件，详细请参考上面的链接.Java的目前只能加载pb模型。其他的平台可自行[百度](https://www.baidu.com)
### 本项目移除测试图片，请下载Demo后自行配图测试 

### 使用
- Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

- Add the dependency [![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android)

```
	dependencies {
	         //versionCode：上面小icon中最新版本号
	        implementation 'com.github.devzwy:open_nsfw_android:[versionCode]'
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


- 使用

```
  val nsfwHelper = NSFWHelper.init(NSFWConfig(assets))
  val nsfwBean = nsfwHelper?.scanBitmap(bitmap)!!
  nsfwBean.sfw
  nsfwBean.nsfw
  if(nsfwBean.nsfw>0.3){
    Log.e("NSFW","图片涉黄")
  }
```
- kotlin可直接使用File.getNsfwScore(mAssetManager: AssetManager): NsfwBean 或 Bitmap.getNsfwScore(mAssetManager: AssetManager): NsfwBean 直接获取鉴定结果（NSFWHelper 1.2.9版本开始支持），比如：

```  
  val bitmap = BitmapFactory.decodeFile(path)
  
  val nsfwScore = bitmap.getNsfwScore(assets)
  
  if(nsfwBean.nsfw>0.3){
  
      Log.e("NSFW","图片涉黄")
      
  }
```
或
```
      val file = File(lm.path)
      
      val nsfwScore = file.getNsfwScore(assets)
      
      if(nsfwBean.nsfw>0.3){
      
          Log.e("NSFW","图片涉黄")
          
      }
      
```

### 安卓手机直接[点我安装](https://fir.im/1rj9)

### 扫码下载

![图片](https://github.com/devzwy/open_nsfw_android/blob/master/img/2.png)

### Demo运行结果：  


![图片](https://github.com/devzwy/open_nsfw_android/blob/master/img/1.png)
