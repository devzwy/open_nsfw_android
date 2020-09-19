# open_nsfw_android
[![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Base-TensorFlow-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-jason-orange.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/QQ-3648415-brightgreen.svg)](https://github.com/devzwy/KUtils)  [![](https://img.shields.io/badge/Mail-dev_zwy@aliyun.com-green.svg)](https://github.com/devzwy/open_nsfw_android) ![Mozilla Add-on](https://img.shields.io/amo/stars/dustman.svg?label=stars&logo=1&logoColor=1&style=popout)

[中文文档](https://github.com/devzwy/open_nsfw_android/blob/dev/README.md)


### The off-line identification of pornographic images is based on tensorflow. Identification only takes 20ms, can be disconnected test, the success rate is 99%, call as long as a line of code, open from the open source project of Yahoo_ Nsfw transplantation, the model file can be used in IOS, Java, C + + and other platforms
### iOS like this：[issues13](https://github.com/devzwy/open_nsfw_android/issues/13)
### Python like this：[NSFW-Python](https://github.com/devzwy/NSFW-Python)、[Python-TensorflowLite-Api](https://tensorflow.google.cn/api_docs/python/tf/lite)、[Python-Tensorflow-Api](https://tensorflow.google.cn/api_docs/python/tf)
### Java like this:[Tensorflow-Api](https://tensorflow.google.cn/api_docs/java/reference/org/tensorflow/package-summary)
### C++  like this:[TensorflowLite-Api](https://tensorflow.google.cn/lite/api_docs/cc)
### JavaScript like this[js doc](https://js.tensorflow.org/api/latest/)
>>> Python and C + + have two ways to feed data. You can select PB model or tflite file according to your needs. Please refer to the above link for details. Java can only load PB model at present. Other platforms can [Google]（ https://www.google.cn )
### This project removes the test picture, please download demo and test by yourself
#### Demo uses MVVM mode, which can be used to develop scaffolding
`Kotlin+okhttp3+rxjava2+retrofit2+koin+glide+greendao+databinding+Livedata`  
  
![MVVM](https://github.com/devzwy/open_nsfw_android/blob/dev/img/4.jpg)


#### 1.3.5 optimization description：
Although the original quantization model is small (6m), it is not friendly to GPU acceleration. The new model is about 23m, which perfectly supports GPU acceleration and optimizes the recognition accuracy. The acceleration effect is obvious. It is recommended to upgrade all of this version__ The new version of GPU acceleration is on by default__ By default, the SDK will detect whether the device supports it. If it does not, it will automatically cancel the acceleration (the old version will crash)
#### 1.3.4 Special description of this version：
Since the model supports GPU acceleration, the model file becomes larger, and some users feedback that the APK volume increases. In this regard, the initialization method is added in version 1.3.4, which can be initialized through the path of the model file. The model can be initialized in '/ opennsfw / SRC / main / assets/ nsfw.tflite `You can download and store it in an appropriate location by yourself, such as in the background, and the app will download it by itself Use after nsfw. Initialization mode:
```
        Classifier.Build()
//            .context(this) //Version 1.3.4 does not need to call this code. Other versions must be called, otherwise an exception will be thrown
//            .isOpenGPU(true)//GPU acceleration
//            .numThreads(100) //The number of threads allocated is set according to the phone configuration. The default value is 1
            .nsfwModuleFilePath("/data/user/0/com.zwy.demo/files/nsfw.tflite") //Version 1.3.4 must configure the model storage path, otherwise an exception will be thrown
            .build()
```  


### dependencies
- add jitpack repository
```
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
```

- Configuration dependency (if you need to configure the model path by yourself, version 1.3.4 is applicable; otherwise, please use the latest version. For version number, see the number in the icon on the right) [![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android)

```
	dependencies {
	         //versionCode：The latest version number in the small icon above
	        implementation 'com.github.devzwy:open_nsfw_android:[versionCode]'
	}

```

- The following configuration version 1.3.4 can be skipped
__Except for version 1.3.4, the following code needs to be added to any other version, otherwise there will be`java.io.FileNotFoundException: This file can not be opened as a file descriptor; it is probably compressed`Exception thrown, because the model file under assets is large__
```
  android {
        aaptOptions {
            noCompress "tflite"
        }
  }
```  


- Global initialization in application

```
        Classifier.Build()
            .context(this) //Version 1.3.4 does not need to call this code. Other versions must be called, otherwise an exception will be thrown
//            .isOpenGPU(true)//GPU acceleration
//            .numThreads(100) //The number of threads allocated is set according to the phone configuration. The default value is 1
//            .nsfwModuleFilePath("/data/user/0/com.zwy.demo/files/nsfw.tflite") //Version 1.3.4 must configure the model storage path, otherwise an exception will be thrown
            .build()
```
- use：

```  
         //like this：
        val nsfwBean = Classifier.Build().context(this).build().run(bitmap)
        //or like this
        val nsfwBean = bitmap.getNsfwScore()
        //or like this
        val nsfwBean = file.getNsfwScore()

        nsfwBean.sfw   ... The more approximate the value, the safer it is
        nsfwBean.nsfw   ... The more approximate the value, the more dangerous it is
```
### android install[click me to installing](http://d.6short.com/q9cv)

### Scan qrcode to download

![pic](https://github.com/devzwy/open_nsfw_android/blob/dev/img/2.png)

### Demo run results：

![pic2](https://github.com/devzwy/open_nsfw_android/blob/dev/img/1.png)
