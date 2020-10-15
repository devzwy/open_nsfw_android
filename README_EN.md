# open_nsfw_android
[![](https://img.shields.io/badge/JCenter-1.3.7-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android)  [![](https://img.shields.io/badge/Base-TensorFlow-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-赵文贇-orange.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/QQ-3648415-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) ![Mozilla Add-on](https://img.shields.io/amo/stars/dustman.svg?label=stars&logo=1&logoColor=1&style=popout)
<a href='https://bintray.com/devzwy/maven/nsfw?source=watch' alt='Get automatic notifications about new "nsfw" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>
[中文文档](https://github.com/devzwy/open_nsfw_android/blob/dev/README.md)


### The off-line identification of pornographic images is based on tensorflow. Identification only takes 20ms, can be disconnected test, the success rate is 99%, call as long as a line of code, open from the open source project of Yahoo_ Nsfw transplantation, the model file can be used in IOS, Java, C + + and other platforms
### iOS like this：[issues13](https://github.com/devzwy/open_nsfw_android/issues/13)
### Python like this：[NSFW-Python](https://github.com/devzwy/NSFW-Python)、[Python-TensorflowLite-Api](https://tensorflow.google.cn/api_docs/python/tf/lite)、[Python-Tensorflow-Api](https://tensorflow.google.cn/api_docs/python/tf)
### Java like this:[Tensorflow-Api](https://tensorflow.google.cn/api_docs/java/reference/org/tensorflow/package-summary)
### C++  like this:[TensorflowLite-Api](https://tensorflow.google.cn/lite/api_docs/cc)
### JavaScript like this[js doc](https://js.tensorflow.org/api/latest/)
>>> Python and C + + have two ways to feed data. You can select PB model or tflite file according to your needs. Please refer to the above link for details. Java can only load PB model at present. Other platforms can [Google]（ https://www.google.cn )
### This project removes the test picture, please download demo and test by yourself
#### picture



### start use

- Open tflite support

```
  android {
        aaptOptions {
            noCompress "tflite"
        }
  }
```
- Dependencies

```
    //Optional Quick initialization of scanner, can avoid initialization code
    implementation 'com.zwy.nsfw:nsfw_initializer:1.3.7'
    //must Scanner core file
    implementation 'com.zwy.nsfw:nsfw:1.3.7'
```

- Initialization

```
    //Method 1: put the model file in the assets root directory and name it nsfw.tflite
    NSFWHelper.init(context = this@Application)

    //Method 2 is applicable to the product that strictly controls the size of the APK and cannot directly put the model file in the APK. It can be initialized by specifying the model path after the user opens the APK background and downloads it silently
    NSFWHelper.init(modelPath = "modelPath")

    //Method 3: put the model file in the assets root directory and name it nsfw.tflite To refer to the library can avoid initializing the code
    implementation 'com.zwy.nsfw:nsfw_initializer:1.3.7'

```
- GetNSFWScore：

```
    //val mNSFWScoreBean:NSFWScoreBean =  File.getNSFWScore()
    //val mNSFWScoreBean:NSFWScoreBean =  Bitmap.getNSFWScore()
    //val mNSFWScoreBean:NSFWScoreBean = NSFWHelper.getNSFWScore(bitmap)

    mNSFWScoreBean.sfw   ... The more approximate the non yellow value is, the safer it is
    mNSFWScoreBean.nsfw   ... The more the Yellow value is, the more dangerous it will be
    mNSFWScoreBean.timeConsumingToLoadData  ... Load data in milliseconds
    mNSFWScoreBean.timeConsumingToScanData  ... Scan picture in milliseconds
```

### Android mobile phone direct[Click me to install](http://d.6short.com/q9cv)

### Scan code to download

![pic](https://github.com/devzwy/open_nsfw_android/blob/dev/img/2.png)