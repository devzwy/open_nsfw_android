
package com.zwy.nsfw;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;
import com.zwy.nsfw.api.NsfwBean;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A classifier specialized to label images using TensorFlow Lite.
 */
public abstract class Classifier {

    public static final String TAG = "open_nsfw_android";

    /**
     * Dimensions of inputs.
     */
    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 3;

    /**
     * Preallocated buffers for storing image data in.
     */
    private int[] intValues = new int[224 * 224];

    /**
     * Options for configuring the Interpreter.
     */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /**
     * The loaded TensorFlow Lite model.
     */
    private MappedByteBuffer tfliteModel;

    /** Labels corresponding to the output of the vision model. */
//  private List<String> labels;

    /**
     * Optional GPU delegate for accleration.
     */
    private GpuDelegate gpuDelegate = null;

    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    protected Interpreter tflite;

    /**
     * A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs.
     */
    protected ByteBuffer imgData = null;

    /**
     * Creates a classifier with the provided configuration.
     *
     * @param activity   The current Activity.
     * @param numThreads The number of threads to use for classification.
     * @return A classifier with the desired configuration.
     */
    public static Classifier create(Activity activity, Boolean isAddGpuDelegate, int numThreads)
            throws IOException {
        return new ClassifierFloatMobileNet(activity, isAddGpuDelegate, numThreads);
    }

    /**
     * An immutable result returned by a Classifier describing what was recognized.
     */

    /**
     * Initializes a {@code Classifier}.
     */
    protected Classifier(Activity activity, Boolean isGPU, int numThreads) throws IOException {
        tfliteModel = loadModelFile(activity);
        if (isGPU) {
            gpuDelegate = new GpuDelegate();
            tfliteOptions.addDelegate(gpuDelegate);
        }
        tfliteOptions.setNumThreads(numThreads);
        tflite = new Interpreter(tfliteModel, tfliteOptions);
        imgData =
                ByteBuffer.allocateDirect(
                        DIM_BATCH_SIZE
                                * getImageSizeX()
                                * getImageSizeY()
                                * DIM_PIXEL_SIZE
                                * getNumBytesPerChannel());
        imgData.order(ByteOrder.LITTLE_ENDIAN);
        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCv Initialization Success.");
        }else {
            Log.e(TAG, "OpenCv Initialization Error.");
        }
        Log.d(TAG, "Tensorflow Lite Image Classifier Initialization Success.");
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Writes Image data into a {@code ByteBuffer}.
     */
    private void convertBitmapToByteBuffer(Bitmap bitmap_) {
        if (imgData == null || bitmap_ == null) {
            return;
        }
        imgData.rewind();
        Matrix m = new Matrix();
        m.setScale(-1, 1);//水平翻转
        Bitmap reversePic = Bitmap.createBitmap(bitmap_, 0, 0, bitmap_.getWidth(), bitmap_.getHeight(), m, true);
        //把每个像素的颜色值转为int 存入intValues
        reversePic.getPixels(intValues, 0, 224, 16, 16, 224, 224);
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 16; i < 240; ++i) {
            for (int j = 16; j < 240; ++j) {
                final int color = intValues[pixel++];
                int r1 = Color.red(color);
                int g1 = Color.green(color);
                int b1 = Color.blue(color);

                int rr1 = r1 - 123;
                int gg1 = g1 - 117;
                int bb1 = b1 - 104;

                imgData.putFloat(bb1);
                imgData.putFloat(gg1);
                imgData.putFloat(rr1);
            }
        }
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to put values into ByteBuffer: " + (endTime - startTime) + "ms");
    }

    public NsfwBean run(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat, false);
        //线性采样
        Mat mat1 = new Mat();
        Imgproc.resize(mat, mat1, new Size(256, 256), 0, 0, Imgproc.INTER_CUBIC);
        Bitmap bitmap_256 = Bitmap.createBitmap(256, 256, Bitmap.Config.RGB_565);
        Utils.matToBitmap(mat1, bitmap_256);
        convertBitmapToByteBuffer(bitmap_256);
        long startTime = SystemClock.uptimeMillis();
        float[][] labelProbArray = new float[1][2];
        tflite.run(imgData, labelProbArray);
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to run model inference: " + (endTime - startTime) + "ms");
        return new NsfwBean(labelProbArray[0][0], labelProbArray[0][1]);
    }


    /**
     * Closes the interpreter and model to release resources.
     */
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
        if (gpuDelegate != null) {
            gpuDelegate.close();
            gpuDelegate = null;
        }
        tfliteModel = null;
    }

    /**
     * Get the image size along the x axis.
     *
     * @return
     */
    public abstract int getImageSizeX();

    /**
     * Get the image size along the y axis.
     *
     * @return
     */
    public abstract int getImageSizeY();

    /**
     * Get the name of the model file stored in Assets.
     *
     * @return
     */
    protected abstract String getModelPath();


    /**
     * Get the number of bytes that is used to store a single color channel value.
     *
     * @return
     */
    protected abstract int getNumBytesPerChannel();

}
