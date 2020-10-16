# open_nsfw_android
[ ![Download](https://api.bintray.com/packages/devzwy/maven/nsfw/images/download.svg) ](https://bintray.com/devzwy/maven/nsfw/_latestVersion)  [![](https://img.shields.io/badge/Base-TensorFlow-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-赵文贇-orange.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/QQ-3648415-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) ![Plugin on redmine.org](https://img.shields.io/redmine/plugin/stars/redmine_xlsx_format_issue_exporter?color=1&logo=1)

[English Doc](https://github.com/devzwy/open_nsfw_android/blob/dev/README_EN.md)


### 色情图片离线识别，基于TensorFlow实现。识别只需20ms,可断网测试，成功率99%，调用只要一行代码，从雅虎的开源项目open_nsfw移植，该模型文件可用于iOS、java、C++等平台
### iOS请参考：[issues13](https://github.com/devzwy/open_nsfw_android/issues/13)
### Python参考：[NSFW-Python](https://github.com/devzwy/NSFW-Python)、[Python-TensorflowLite-Api](https://tensorflow.google.cn/api_docs/python/tf/lite)、[Python-Tensorflow-Api](https://tensorflow.google.cn/api_docs/python/tf)
### Java参考:[Tensorflow-Api](https://tensorflow.google.cn/api_docs/java/reference/org/tensorflow/package-summary)
### C++参考:[TensorflowLite-Api](https://tensorflow.google.cn/lite/api_docs/cc)
### JavaScript参考[JS相关文档](https://js.tensorflow.org/api/latest/)
>>> 其中Python、C++均有两种数据喂入的方式，可根据需求选择pb模型或tfLite文件，详细请参考上面的链接.Java的目前只能加载pb模型。其他的平台可自行[百度](https://www.baidu.com)
### 本项目移除测试图片，请下载Demo后自行配图测试  
#### 测试图片

![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/demopic.png)


### 开始使用（从1.3.9版本开始，依赖从JetPack移动到Maven仓库，可直接在项目中依赖，无需添加jetpack支持）

- 开启tflite文件支持

```
  android {
        aaptOptions {
            noCompress "tflite"
        }
  }
```
- 引入依赖(lastVersion更换为最新版本，最新版本为右边图片中的数字👉[ ![Download](https://api.bintray.com/packages/devzwy/maven/nsfw/images/download.svg) ](https://bintray.com/devzwy/maven/nsfw/_latestVersion))

```
    //可选 快速初始化扫描器，可免去初始化代码
    implementation 'com.zwy.nsfw:nsfw_initializer:lastVersion'
    //必须 扫描器核心文件
    implementation 'com.zwy.nsfw:nsfw:lastVersion'
    //必须 tensorflow 支持库
    implementation 'org.tensorflow:tensorflow-lite:2.1.0'
    implementation 'org.tensorflow:tensorflow-lite-gpu:2.1.0'
```

- 初始化

```
    //方式一,将模型文件放在Assets根目录下并命名为nsfw.tflite
    NSFWHelper.init(context = this@Application)

    //方式二,适用于产品对apk大小控制严格，无法将模型文件直接放在apk中，可在用户打开Apk后台静默下载后指定模型路径进行初始化
    NSFWHelper.init(modelPath = "模型文件存放路径")

    //方式三,将模型文件放在Assets根目录下并命名为nsfw.tflite,引用该库可免去初始化代码
    implementation 'com.zwy.nsfw:nsfw_initializer:1.3.7'

```
- 使用：

```
    //val mNSFWScoreBean:NSFWScoreBean =  File.getNSFWScore()
    //val mNSFWScoreBean:NSFWScoreBean =  Bitmap.getNSFWScore()
    //val mNSFWScoreBean:NSFWScoreBean = NSFWHelper.getNSFWScore(bitmap)

    mNSFWScoreBean.sfw   ... 非涉黄数值 数值越大约安全
    mNSFWScoreBean.nsfw   ... 涉黄数值  数值越大约危险
    mNSFWScoreBean.timeConsumingToLoadData  ... 装载数据耗时  单位ms
    mNSFWScoreBean.timeConsumingToScanData  ... 扫描图片耗时  单位ms
```

### 安卓手机直接[点我安装](http://d.6short.com/q9cv)

### 扫码下载

![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/2.png)
