import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.zwy.opennsfw.core.Classifier
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat

private val TAG = "OpenNSFWLogTag"

var mClassifier: Classifier? = null

fun String.d() = Log.d(TAG, this)

fun String.e() = Log.e(TAG, this)

class NsfwBean(val sfw: Float, val nsfw: Float) {
    override fun toString(): String {
        val floatFormat = DecimalFormat("0.0000")
        floatFormat.roundingMode = RoundingMode.HALF_UP
//        return "适宜度(非色情程度):${floatFormat.format(sfw * 100)}%,不适宜度(涉黄程度)：${floatFormat.format(nsfw * 100)}%"
        return "sfw:$sfw,nsfw：$nsfw%"
    }
}

fun Bitmap.getNsfwScore(): NsfwBean {
    checkNull()
    return mClassifier!!.run(this)
}

fun File.getNsfwScore(): NsfwBean {
    checkNull()
    val bitmap = try {
        BitmapFactory.decodeFile(this.path)
    } catch (e: Exception) {
        val m = "File2Bitmap过程失败,请确认文件是否存在或是否为图片"
        m.e()
        throw RuntimeException(m)
    }
    return bitmap.getNsfwScore()
}

private fun checkNull() {
    if (mClassifier == null) throw RuntimeException("Classifier未初始化，请使用Classifier.Build().context(context)初始化后再试")
}

