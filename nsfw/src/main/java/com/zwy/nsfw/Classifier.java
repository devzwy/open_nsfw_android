
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
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.Math.max;

public class Classifier {

    public static final String TAG = "open_nsfw_android";

    /**
     * tensor input img size
     */
    private final int INPUT_WIDTH = 224;
    private final int INPUT_HEIGHT = 224;

    /**
     * BytesPerChannel
     */
    private final int BYTES_PER_CHANNEL_NUM = 4;
    /**
     * Dimensions of inputs.
     */
    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 3;

    /**
     * Preallocated buffers for storing image data in.
     */
    private int[] intValues = new int[INPUT_WIDTH * INPUT_HEIGHT];

    /**
     * The loaded TensorFlow Lite model.
     */
    private MappedByteBuffer tfliteModel;

    /**
     * Optional GPU delegate for accleration.
     */
    private GpuDelegate gpuDelegate = null;

    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    private Interpreter tflite;

    /**
     * A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs.
     */
    private ByteBuffer imgData;

    /**
     * Creates a classifier with the provided configuration.
     *
     * @param activity         The current Activity.
     * @param isAddGpuDelegate Add gpu delegate
     * @param numThreads       The number of threads to use for classification.
     * @return A classifier with the desired configuration.
     */
    public static Classifier create(Activity activity, Boolean isAddGpuDelegate, int numThreads)
            throws IOException {
        return new Classifier(activity, isAddGpuDelegate, numThreads);
    }

    private Classifier(Activity activity, Boolean isGPU, int numThreads) throws IOException {
        tfliteModel = loadModelFile(activity);
        Interpreter.Options tfliteOptions = new Interpreter.Options();
        if (isGPU) {
            gpuDelegate = new GpuDelegate();
            tfliteOptions.addDelegate(gpuDelegate);
        }
        tfliteOptions.setNumThreads(numThreads);
        tflite = new Interpreter(tfliteModel, tfliteOptions);
        imgData =
                ByteBuffer.allocateDirect(
                        DIM_BATCH_SIZE
                                * INPUT_WIDTH
                                * INPUT_HEIGHT
                                * DIM_PIXEL_SIZE
                                * BYTES_PER_CHANNEL_NUM);

        imgData.order(ByteOrder.LITTLE_ENDIAN);
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCv Initialization Success.");
        } else {
            Log.e(TAG, "OpenCv Initialization Error.");
        }
        Log.d(TAG, "Tensorflow Lite Image Classifier Initialization Success.");
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("nsfw.tflite");
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
        int W = bitmap_.getWidth();
        int H = bitmap_.getHeight();

        int w_off = max((W - INPUT_WIDTH) / 2, 0);
        int h_off = max((H - INPUT_HEIGHT) / 2, 0);

        //把每个像素的颜色值转为int 存入intValues
        reversePic.getPixels(intValues, 0, INPUT_WIDTH, h_off, w_off, INPUT_WIDTH, INPUT_HEIGHT);
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = h_off; i < h_off + INPUT_HEIGHT; ++i) {
            for (int j = w_off; j < w_off + INPUT_WIDTH; ++j) {
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
        //add alpha
        Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, false), mat, true);

        Mat mat1 = new Mat();
        //将原bitmap双线性采样为256*256大小
        Imgproc.resize(mat, mat1, new Size(256, 256), 0, 0, Imgproc.INTER_LINEAR);
        //add alpha
        Bitmap bitmap_256 = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        //convert
        Utils.matToBitmap(mat1, bitmap_256);
        //Writes image data into byteBuffer
        convertBitmapToByteBuffer(bitmap_256);
        long startTime = SystemClock.uptimeMillis();
        // out
        float[][] outArray = new float[1][2];
        Tensor aa = tflite.getInputTensor(tflite.getInputIndex("input"));
        Log.d(TAG, "dataType : " + aa.dataType());
        Log.d(TAG, "numBytes : " + aa.numBytes());
        Log.d(TAG, "numDimensions : " + aa.numDimensions());
        Log.d(TAG, "numElements : " + aa.numElements());
        Log.d(TAG, "shape : " + aa.shape().length);
        Log.d(TAG, "lastImgData : " + imgData);
        tflite.run(imgData, outArray);
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to run model inference: " + (endTime - startTime) + "ms");
        return new NsfwBean(outArray[0][0], outArray[0][1]);
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

}
