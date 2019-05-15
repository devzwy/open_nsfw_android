# open_nsfw_android
[![](https://jitpack.io/v/devzwy/open_nsfw_android.svg)](https://jitpack.io/#devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Base-TensorFlow-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-赵文贇-orange.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/QQ-3648415-brightgreen.svg)](https://github.com/devzwy/KUtils) [![](https://img.shields.io/badge/微信-admin_zwy-brightgreen.svg)](https://github.com/devzwy/open_nsfw_android) [![](https://img.shields.io/badge/Mail-dev_zwy@aliyun.com-green.svg)](https://github.com/devzwy/open_nsfw_android)

### 色情图片离线识别，基于TensorFlow实现。识别只需200ms,可断网测试，成功率100%，调用只要一行代码，从雅虎的开源项目open_nsfw_python移植，tflite（6M）为训练好的模型，可用于iOS平台

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

- Add the dependency

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