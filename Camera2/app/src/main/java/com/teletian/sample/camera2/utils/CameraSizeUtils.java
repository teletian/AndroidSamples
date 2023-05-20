package com.teletian.sample.camera2.utils;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Display;

import java.util.Arrays;

public class CameraSizeUtils {

    public static <T> Size getPreviewOutputSize(
            Display display,
            CameraCharacteristics characteristics,
            Class<T> targetClass,
            Integer format
    ) {

        // Find which is smaller: screen or 1080p
        SmartSize screenSize = SmartSize.getDisplaySmartSize(display);
        boolean hdScreen = screenSize.getLongSize() >= SmartSize.SIZE_1080P.getLongSize()
                || screenSize.getShortSize() >= SmartSize.SIZE_1080P.getShortSize();
        SmartSize maxSize = hdScreen ? SmartSize.SIZE_1080P : screenSize;

        // If image format is provided, use it to determine supported sizes; else use target class
        StreamConfigurationMap config = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (format == null)
            assert (StreamConfigurationMap.isOutputSupportedFor(targetClass));
        else
            assert (config.isOutputSupportedFor(format));
        Size[] allSizes = format == null ?
                config.getOutputSizes(targetClass) : config.getOutputSizes(format);

        // Get available sizes and sort them by area from largest to smallest
        Arrays.sort(allSizes, (size1, size2) -> {
            int area1 = size1.getWidth() * size1.getHeight();
            int area2 = size2.getWidth() * size2.getHeight();
            return Integer.compare(area2, area1);
        });

        for (Size size : allSizes) {
            SmartSize smartSize = new SmartSize(size.getWidth(), size.getHeight());
            if (smartSize.getLongSize() <= maxSize.getLongSize()
                    && smartSize.getShortSize() <= maxSize.getShortSize()) {
                return size;
            }
        }

        return allSizes[allSizes.length - 1];
    }
}
