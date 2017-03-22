package com.haloai.hud.carrecorderlibrary.views;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView {
    private static CameraView mCameraView;

    public static CameraView getSingleInstance(Context context) {
        if (mCameraView == null) {
            synchronized (CameraView.class) {
                if (mCameraView == null) {
                    mCameraView = new CameraView(context);
                }
            }
        }
        return mCameraView;
    }

    private SurfaceHolder holder = null;
    private Camera mCamera = null;

    public CameraView(final Context context) {
        super(context);

        holder = getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            public void surfaceCreated(SurfaceHolder holder) {
                mCamera = Camera.open();
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    mCamera.release();
                    mCamera = null;
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(800, 480);
                parameters.setFocusMode("auto");
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
        });
    }
}

