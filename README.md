# open_nsfw_android
[![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Base-TensorFlow-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-赵文贇-orange.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/QQ-3648415-brightgreen.svg)](https://github.com/devzwy/KUtils) [![](https://img.shields.io/badge/微信-admin_zwy-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Mail-dev_zwy@aliyun.com-green.svg)](https://github.com/devzwy/open_nsfw_android) ![Mozilla Add-on](https://img.shields.io/amo/stars/dustman.svg?label=stars&logo=1&logoColor=1&style=popout)

### 色情图片离线识别，基于TensorFlow实现。识别只需200ms,可断网测试，成功率99%，调用只要一行代码，从雅虎的开源项目open_nsfw_python移植，tflite（6M）为训练好的模型，该模型文件可用于iOS、java、C++等平台

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

- Code like this

```
   val nsfwBean = NsfwHelper.getInstance(this, true, 1).scanBitmapSyn(bitmap)
   nsfwBean?.sfw ...
   nsfwBean?.nsfw ...
```
### Demo运行结果：

![图片](https://github.com/devzwy/open_nsfw_android/blob/master/img/1.png)




0.9254902   0.7764706      0.7254902                            0.92156863   0.77254903    0.72156864

    [16:16+224,16:16+224,:]       [16:240,16:240,:]


0.90588236-R  0.75686276-G     0.68235296-B       ------0.01960784    0.01960784    0.04313724
0.90196079    0.75294119       0.6784314

    [:,:,:: -1]
0.68235296-B 0.75686276-G  0.90588236-R


*255 转换为int

174 193 231

-[104, 117, 123]

70  76  108


opencv假设图像是RGB三分量组成的图像，那么图像的

第一通道是R，

第二通道是G，

第三通道是B

Img[:,:,2]代表R通道，也就是红色分量图像；

Img[:,:,1]代表G通道，也就是绿色分量图像；

Img[:,:,0]代表B通道，也就是蓝色分量图像。