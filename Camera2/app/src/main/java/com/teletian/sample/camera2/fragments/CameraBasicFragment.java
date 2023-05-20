package com.teletian.sample.camera2.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teletian.sample.camera2.R;
import com.teletian.sample.camera2.customview.AutoFitSurfaceView;
import com.teletian.sample.camera2.utils.CameraSizeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CameraBasicFragment extends Fragment {

    private static final String TAG = CameraBasicFragment.class.getSimpleName();
    private static final int IMAGE_BUFFER_SIZE = 3;

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private String cameraId;
    private CameraCharacteristics cameraCharacteristics;
    private CameraCaptureSession cameraSession;
    private ImageReader imageReader;

    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private HandlerThread imageReaderThread;
    private Handler imageReaderHandler;
    private HandlerThread takePhotoThread;
    private Handler takePhotoHandler;

    private AutoFitSurfaceView surfaceView;
    private View overlay;
    private ImageButton captureButton;

    private Runnable takePhotoAnim = () -> {
        overlay.setBackground(new ColorDrawable(Color.argb(150, 255, 255, 255)));
        overlay.postDelayed(() -> overlay.setBackground(null), 50);
    };

    public static CameraBasicFragment newInstance() {
        return new CameraBasicFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraManager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                int lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    this.cameraId = cameraId;
                    this.cameraCharacteristics = cameraCharacteristics;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        cameraThread = new HandlerThread("CameraThread");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
        imageReaderThread = new HandlerThread("ImageReaderThread");
        imageReaderThread.start();
        imageReaderHandler = new Handler(imageReaderThread.getLooper());
        takePhotoThread = new HandlerThread("TakePhotoThread");
        takePhotoThread.start();
        takePhotoHandler = new Handler(takePhotoThread.getLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_camera_basic, container, false);
        surfaceView = view.findViewById(R.id.surface_view);
        overlay = view.findViewById(R.id.overlay);
        captureButton = view.findViewById(R.id.capture_button);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                Size previewSize = CameraSizeUtils.getPreviewOutputSize(
                        surfaceView.getDisplay(),
                        cameraCharacteristics,
                        SurfaceHolder.class,
                        null
                );
                surfaceView.setAspectRatio(
                        previewSize.getWidth(),
                        previewSize.getHeight()
                );

                // To ensure that size is set, initialize camera in the view's thread
                surfaceView.post(() -> openCamera());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });

        captureButton.setOnClickListener(v -> {
            v.setEnabled(false);

            takePhotoHandler.post(() -> {

                while (imageReader.acquireNextImage() != null) {
                }

                BlockingQueue<Image> imageQueue = new ArrayBlockingQueue<>(IMAGE_BUFFER_SIZE);
                imageReader.setOnImageAvailableListener(reader -> {
                    Image image = reader.acquireNextImage();
                    Log.d(TAG, "Image available in queue: " + image.getTimestamp());
                    imageQueue.add(image);
                }, imageReaderHandler);

                try {
                    CaptureRequest.Builder captureRequest =
                            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    captureRequest.addTarget(imageReader.getSurface());
                    cameraSession.capture(captureRequest.build(), new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(@NonNull CameraCaptureSession session,
                                                     @NonNull CaptureRequest request,
                                                     long timestamp, long frameNumber) {
                            super.onCaptureStarted(session, request, timestamp, frameNumber);
                            surfaceView.post(takePhotoAnim);
                        }

                        @Override
                        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                       @NonNull CaptureRequest request,
                                                       @NonNull TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);

                            long resultTimestamp = result.get(CaptureResult.SENSOR_TIMESTAMP);
                            Log.d(TAG, "Capture result received: " + resultTimestamp);

                            takePhotoHandler.post(() -> {
                                while (true) {
                                    try (Image image = imageQueue.take()) {

                                        if (image.getTimestamp() != resultTimestamp) {
                                            image.close();
                                            continue;
                                        }

                                        imageReader.setOnImageAvailableListener(null, null);

                                        while (imageQueue.size() > 0) {
                                            imageQueue.take().close();
                                        }

                                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                                        int size = buffer.remaining();
                                        byte[] bytes = new byte[size];
                                        buffer.get(bytes);

                                        SimpleDateFormat sdf = new SimpleDateFormat(
                                                "yyyy_MM_dd_HH_mm_ss_SSS", Locale.US);
                                        File file = new File(
                                                requireContext().getExternalCacheDir(),
                                                "IMG_" + sdf.format(new Date()) + ".jpg");
                                        try (FileOutputStream fos = new FileOutputStream(file)) {
                                            fos.write(bytes);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }, cameraHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            });
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    try {
                        createCaptureSession();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    requireActivity().finish();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    switch (error) {
                        case ERROR_CAMERA_DEVICE:
                            Log.e(TAG, "Fatal (device)");
                            break;
                        case ERROR_CAMERA_DISABLED:
                            Log.e(TAG, "Device policy");
                            break;
                        case ERROR_CAMERA_IN_USE:
                            Log.e(TAG, "Camera in use");
                            break;
                        case ERROR_CAMERA_SERVICE:
                            Log.e(TAG, "Fatal (service)");
                            break;
                        case ERROR_MAX_CAMERAS_IN_USE:
                            Log.e(TAG, "Maximum cameras in use");
                            break;
                    }
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCaptureSession() throws CameraAccessException {
        List<Surface> targets = new ArrayList<>();
        targets.add(surfaceView.getHolder().getSurface());

        Size[] sizes = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                .getOutputSizes(ImageFormat.JPEG);
        Arrays.sort(sizes, (size1, size2) -> {
            int area1 = size1.getWidth() * size1.getHeight();
            int area2 = size2.getWidth() * size2.getHeight();
            return Integer.compare(area2, area1);
        });
        Size size = sizes[0];
        imageReader = ImageReader.newInstance(
                size.getWidth(), size.getHeight(), ImageFormat.JPEG, IMAGE_BUFFER_SIZE);
        targets.add(imageReader.getSurface());

        cameraDevice.createCaptureSession(targets, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                cameraSession = session;
                try {
                    createCaptureRequest();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        }, cameraHandler);
    }

    private void createCaptureRequest() throws CameraAccessException {
        CaptureRequest.Builder captureRequest =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequest.addTarget(surfaceView.getHolder().getSurface());
        cameraSession.setRepeatingRequest(captureRequest.build(),
                new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureStarted(@NonNull CameraCaptureSession session,
                                                 @NonNull CaptureRequest request,
                                                 long timestamp, long frameNumber) {
                        super.onCaptureStarted(session, request, timestamp, frameNumber);
                        Log.i(TAG, "onCaptureStarted：" + frameNumber + " " + timestamp);
                    }

                    @Override
                    public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                                    @NonNull CaptureRequest request,
                                                    @NonNull CaptureResult partialResult) {
                        super.onCaptureProgressed(session, request, partialResult);
                        Log.i(TAG, "onCaptureProgressed：" + partialResult.toString());
                    }

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                   @NonNull CaptureRequest request,
                                                   @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        Log.i(TAG, "onCaptureCompleted：" + result.toString());
                    }

                    @Override
                    public void onCaptureFailed(@NonNull CameraCaptureSession session,
                                                @NonNull CaptureRequest request,
                                                @NonNull CaptureFailure failure) {
                        super.onCaptureFailed(session, request, failure);
                        Log.i(TAG, "onCaptureFailed：" + failure);
                    }

                    @Override
                    public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session,
                                                           int sequenceId, long frameNumber) {
                        super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                        Log.i(TAG, "onCaptureSequenceCompleted：" + frameNumber + " " + sequenceId);
                    }

                    @Override
                    public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session,
                                                         int sequenceId) {
                        super.onCaptureSequenceAborted(session, sequenceId);
                        Log.i(TAG, "onCaptureSequenceAborted：" + sequenceId);
                    }

                    @Override
                    public void onCaptureBufferLost(@NonNull CameraCaptureSession session,
                                                    @NonNull CaptureRequest request,
                                                    @NonNull Surface target,
                                                    long frameNumber) {
                        super.onCaptureBufferLost(session, request, target, frameNumber);
                        Log.i(TAG, "onCaptureBufferLost：" + frameNumber);
                    }
                }, cameraHandler);
    }

    @Override
    public void onStop() {
        super.onStop();
        cameraDevice.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraThread.quitSafely();
    }
}
