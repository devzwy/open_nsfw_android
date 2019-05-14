# open_nsfw_android
色情图片离线识别，200ms,可断网测试，成功率100%，原库为雅虎的python开源项目open_nsfw  

```
val nsfwBean = NsfwHelper.getInstance(this, true, 1).scanBitmapSyn(bitmap)
    nsfwBean?.sfw ...
    nsfwBean?.nsfw ...
```
Demo运行结果：  
![图片](https://github.com/devzwy/open_nsfw_android/blob/master/img/1.png)