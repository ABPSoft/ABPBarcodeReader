package com.aminbahrami.abpbarcodereader.Library;


import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by ABP on 7/15/2021 - 21:11
 */

public class CameraHandlerThread extends HandlerThread
{
	private static final String LOG_TAG="CameraHandlerThread";
	
	private BarcodeScannerView mScannerView;
	
	public CameraHandlerThread(BarcodeScannerView scannerView)
	{
		super("CameraHandlerThread");
		mScannerView=scannerView;
		start();
	}
	
	public void startCamera(final int cameraId)
	{
		Handler localHandler=new Handler(getLooper());
		localHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				final Camera camera=CameraUtils.getCameraInstance(cameraId);
				Handler mainHandler=new Handler(Looper.getMainLooper());
				mainHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						mScannerView.setupCameraPreview(CameraWrapper.getWrapper(camera,cameraId));
					}
				});
			}
		});
	}
}