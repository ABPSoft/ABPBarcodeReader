package com.aminbahrami.abpbarcodereader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;

/**
 * Created by ABP on 11/23/2016 - 6:23 PM
 * Document
 * https://github.com/dm77/barcodescanner
 */
public class ABPBarcodeReader
{
	public interface IOnDetectBarcode
	{
		public void onDetect(BarcodeResult result);
	}
	
	public class BarcodeResult
	{
		private String text;
		private BarcodeFormat format;
		
		BarcodeResult(String text,BarcodeFormat format)
		{
			this.text=text;
			this.format=format;
		}
		
		public String getText()
		{
			return text;
		}
		
		public void setText(String text)
		{
			this.text=text;
		}
		
		public BarcodeFormat getFormat()
		{
			return format;
		}
		
		public void setFormat(BarcodeFormat format)
		{
			this.format=format;
		}
	}
	
	private int cameraId=-1;
	private boolean autoFocus=true;
	private boolean flash=false;
	
	private Context context=null;
	
	private ViewGroup view=null;
	
	private List<BarcodeFormat> formats=ZXingScannerView.ALL_FORMATS;
	
	private ZXingScannerView mScannerView;
	
	private IOnDetectBarcode onDetectBarcode=null;
	
	private ZXingScannerView.ResultHandler resultHandler=null;
	
	private Handler handler=new Handler();
	
	public ABPBarcodeReader(Context context)
	{
		this.context=context;
		
		resultHandler=new ZXingScannerView.ResultHandler()
		{
			@Override
			public void handleResult(Result rawResult)
			{
				BarcodeResult barcodeResult=new BarcodeResult(rawResult.getText(),rawResult.getBarcodeFormat());
				
				if(onDetectBarcode!=null)
				{
					onDetectBarcode.onDetect(barcodeResult);
				}
			}
		};
	}
	
	public void startCamera()
	{
		mScannerView=new ZXingScannerView(this.context);
		mScannerView.setFlash(this.flash);
		mScannerView.setAutoFocus(this.autoFocus);
		mScannerView.setFormats(formats);
		
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				view.addView(mScannerView);
			}
		});
		
		//this parameter will make your HUAWEI phone works great!
		mScannerView.setAspectTolerance(0.5f);
		
		mScannerView.setResultHandler(resultHandler);
		
		mScannerView.startCamera(this.cameraId);
	}
	
	public void stopCamera()
	{
		mScannerView.stopCamera();
	}
	
	public void resumeCameraPreview()
	{
		//ممکن بود توی گوشی های قدیمی خطا بده
		//پس تنظیم کردم اگر خطا داد، 2 ثانیه صبر کن بعد اجرا کن
		
		try
		{
			mScannerView.resumeCameraPreview(resultHandler);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			Handler handler=new Handler();
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mScannerView.resumeCameraPreview(resultHandler);
				}
			},2000);
		}
	}
	
	public void setOnDetectBarcode(IOnDetectBarcode onDetectBarcode)
	{
		this.onDetectBarcode=onDetectBarcode;
	}
	
	public int getCameraId()
	{
		return cameraId;
	}
	
	public void setCameraId(int cameraId)
	{
		this.cameraId=cameraId;
	}
	
	public boolean isAutoFocus()
	{
		return autoFocus;
	}
	
	public void setAutoFocus(boolean autoFocus)
	{
		this.autoFocus=autoFocus;
	}
	
	public boolean isFlash()
	{
		return flash;
	}
	
	public void setFlash(boolean flash)
	{
		this.flash=flash;
	}
	
	public void setFormats(List<BarcodeFormat> formats)
	{
		this.formats=formats;
	}
	
	public List<BarcodeFormat> getAllFormats()
	{
		return ZXingScannerView.ALL_FORMATS;
	}
	
	public void setView(ViewGroup view)
	{
		this.view=view;
	}
	
	private Bitmap encodeAsBitmap(String str,int width,int height) throws WriterException
	{
		BitMatrix result;
		
		try
		{
			result=new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE,width,height,null);
		}
		catch(IllegalArgumentException iae)
		{
			return null;
		}
		
		int w=result.getWidth();
		int h=result.getHeight();
		int[] pixels=new int[w*h];
		for(int y=0;y<h;y++)
		{
			int offset=y*w;
			for(int x=0;x<w;x++)
			{
				pixels[offset+x]=result.get(x,y)?Color.BLACK:Color.WHITE;
			}
		}
		Bitmap bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels,0,width,0,0,w,h);
		return bitmap;
	}
	
	public Bitmap generateQr(String content,int size)
	{
		try
		{
			return encodeAsBitmap(content,size,size);
		}
		catch(WriterException e)
		{
			return null;
		}
	}
}
