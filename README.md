# open_nsfw_android
色情图片离线识别，基于TensorFlow实现。识别只需200ms,可断网测试，成功率100%，调用只要一行代码，从雅虎的开源项目open_nsfw_python移植，tflite（6M）为训练好的模型，可用于iOS平台

```
val nsfwBean = NsfwHelper.getInstance(this, true, 1).scanBitmapSyn(bitmap)
    nsfwBean?.sfw ...
    nsfwBean?.nsfw ...
```
Demo运行结果：  
![图片](https://github.com/devzwy/open_nsfw_android/blob/master/img/1.png)