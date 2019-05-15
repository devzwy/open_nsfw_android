/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.zwy.nsfw;

import android.app.Activity;

import java.io.IOException;

/**
 * This TensorFlowLite classifier works with the float MobileNet model.
 */
public class ClassifierFloatMobileNet extends Classifier {


    public ClassifierFloatMobileNet(Activity activity, Boolean isAddGpuDelegate, int numThreads)
            throws IOException {
        super(activity, isAddGpuDelegate, numThreads);
    }

    @Override
    public int getImageSizeX() {
        return 224;
    }

    @Override
    public int getImageSizeY() {
        return 224;
    }

    @Override
    protected String getModelPath() {
        return "nsfw.tflite";
    }

    @Override
    protected int getNumBytesPerChannel() {
        return 4;
    }

}
