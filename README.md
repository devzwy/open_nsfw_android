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

## 注意 已从jCenter仓库迁移到Maven,注：新版本需要手动下载[nsfw.tflite](https://github.com/devzwy/open_nsfw_android/blob/dev/app/src/main/assets/nsfw.tflite)模型初始化使用

### 开始使用

- __[下载模型文件](https://github.com/devzwy/open_nsfw_android/blob/dev/app/src/main/assets/nsfw.tflite),并放入assets目录下__
![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/assets.png)

- __开启tflite文件读取支持__(解决模型放在assets目录下无法读取的问题。如果模型不放在assets目录下可跳过该步骤)

```
android {

    ...

    aaptOptions {
        noCompress "tflite"
    }
}
```
- __引入依赖__

```
    dependencies {
        ...
        implementation 'io.github.devzwy:nsfw:1.5.1'
    }

```

- __初始化__（建议在Application中进行）

```
    class KtApp : Application() {
        override fun onCreate() {
            super.onCreate()

            //开启日志输出，可选
            NSFWHelper.openDebugLog()

            //扫描前必须初始化
            NSFWHelper.initHelper(
                context = this)

            //初始化api原型
            /* NSFW初始化函数 内部日志默认关闭，调试环境可使用openDebugLog()开启日志*/
            fun initHelper(
                context: Context, //建议传入application,传入activity可能会有内存泄漏
                modelPath: String? = null,//模型文件路径，为空时将默认从Assets下读取
                isOpenGPU: Boolean = true, //是否开启GPU扫描加速，部分机型兼容不友好的可关闭。默认开启
                numThreads: Int = 4 //扫描数据时内部分配的线程 默认4
            )

        }
    }
```

- __支持的api列表__ 带返回值的为同步，传入函数的为异步：

>> [NSFWHelper.getNSFWScore(file: File): NSFWScoreBean]()
>> [getNSFWScore(file: File, onResult: ((NSFWScoreBean) -> Unit))]()
>> [getNSFWScore(filePath: String): NSFWScoreBean]()
>> [getNSFWScore(filePath: String, onResult: ((NSFWScoreBean) -> Unit))]()
>> [getNSFWScore(bitmap: Bitmap): NSFWScoreBean]()
>> [getNSFWScore(bitmap: Bitmap, onResult: ((NSFWScoreBean) -> Unit))]()

- __识别结果说明__
```
    mNSFWScoreBean.sfw   ... 非涉黄数值 数值越大约安全
    mNSFWScoreBean.nsfw   ... 涉黄数值  数值越大约危险
    mNSFWScoreBean.timeConsumingToLoadData  ... 装载数据耗时  单位ms
    mNSFWScoreBean.timeConsumingToScanData  ... 扫描图片耗时  单位ms
```

- __调用参考__

```
    //异步方式
    NSFWHelper.getNSFWScore(item.bitmap) {
        this.text =
            "nsfw:${it.nsfwScore}\nsfw:${it.sfwScore}\n扫描耗时：${it.timeConsumingToScanData} ms"
        if (it.nsfwScore > 0.7) {
            this.setBackgroundColor(Color.parseColor("#C1FF0000"))
        } else if (it.nsfwScore > 0.5) {
            this.setBackgroundColor(Color.parseColor("#C1FF9800"))
        } else {
            this.setBackgroundColor(Color.parseColor("#803700B3"))
        }
    }

    //同步方式
    NSFWHelper.getNSFWScore(item.bitmap).let {
        this.text =
            "nsfw:${it.nsfwScore}\nsfw:${it.sfwScore}\n扫描耗时：${it.timeConsumingToScanData} ms"
        if (it.nsfwScore > 0.7) {
            this.setBackgroundColor(Color.parseColor("#C1FF0000"))
        } else if (it.nsfwScore > 0.5) {
            this.setBackgroundColor(Color.parseColor("#C1FF9800"))
        } else {
            this.setBackgroundColor(Color.parseColor("#803700B3"))
        }
    }

```

### 安卓手机直接[点我安装](http://d.6short.com/q9cv)

### 扫码下载

![图片](https://github.com/devzwy/open_nsfw_android/blob/dev/img/2.png)
