package com.weibo.sdk.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboParameters;

public class Utility {
    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static byte[] decodes = new byte[256];  
	public static Bundle parseUrl(String url) {
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
			}
		}
		return params;
	}

	public static String encodeUrl(WeiboParameters parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++) {
			if (first){
			    first = false;
			}
			else{
			    sb.append("&");
			}
			String _key=parameters.getKey(loc);
			String _value=parameters.getValue(_key);
			if(_value==null){
			    Log.i("encodeUrl", "key:"+_key+" 's value is null");
			}
			else{
			    sb.append(URLEncoder.encode(parameters.getKey(loc)) + "="
                        + URLEncoder.encode(parameters.getValue(loc)));
			}
			
		}
		return sb.toString();
	}

	public static String encodeParameters(WeiboParameters httpParams) {
		if (null == httpParams || isBundleEmpty(httpParams)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++) {
			String key = httpParams.getKey(loc);
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
						.append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();

	}

	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	private static boolean isBundleEmpty(WeiboParameters bundle) {
		if (bundle == null || bundle.size() == 0) {
			return true;
		}
		return false;
	}
	/**
	 * 将data编码成Base62的字符串
	 * @param data 
	 * @return
	 */
	public static String encodeBase62(byte[] data) {  
	    StringBuffer sb = new StringBuffer(data.length * 2);  
	    int pos = 0, val = 0;  
	    for (int i = 0; i < data.length; i++) {  
	        val = (val << 8) | (data[i] & 0xFF);  
	        pos += 8;  
	        while (pos > 5) {  
	            char c = encodes[val >> (pos -= 6)];  
	            sb.append(  
	            /**/c == 'i' ? "ia" :  
	            /**/c == '+' ? "ib" :  
	            /**/c == '/' ? "ic" : c);  
	            val &= ((1 << pos) - 1);  
	        }  
	    }  
	    if (pos > 0) {  
	        char c = encodes[val << (6 - pos)];  
	        sb.append(  
	        /**/c == 'i' ? "ia" :  
	        /**/c == '+' ? "ib" :  
	        /**/c == '/' ? "ic" : c);  
	    }  
	    return sb.toString();  
	}  
	  /**
	   * 将字符串解码成byte数组
	   * @param data
	   * @return
	   */
	public static byte[] decodeBase62(String string) {  
	    if(string==null){
	        return null;
	    }
	    char[] data=string.toCharArray();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(string.toCharArray().length);  
	    int pos = 0, val = 0;  
	    for (int i = 0; i < data.length; i++) {  
	        char c = data[i];  
	        if (c == 'i') {  
	            c = data[++i];  
	            c =  
	            /**/c == 'a' ? 'i' :  
	            /**/c == 'b' ? '+' :  
	            /**/c == 'c' ? '/' : data[--i];  
	        }  
	        val = (val << 6) | decodes[c];  
	        pos += 6;  
	        while (pos > 7) {  
	            baos.write(val >> (pos -= 8));  
	            val &= ((1 << pos) - 1);  
	        }  
	    }  
	    return baos.toByteArray();  
	}  
	private static boolean deleteDependon(File file, int maxRetryCount)
	  {
	    int retryCount = 1;
	    maxRetryCount = (maxRetryCount < 1) ? 5 : maxRetryCount;
	    boolean isDeleted = false;

	    if (file != null) {
	      while ((!(isDeleted)) && (retryCount <= maxRetryCount) && (file.isFile()) && (file.exists()))
	        if (!((isDeleted = file.delete()))) {
//	          LogUtils.i(file.getAbsolutePath() + "删除失败，失败次数为:" + retryCount);
	          ++retryCount;
	        }


	    }

	    return isDeleted;
	  }
	
	
	private static void mkdirs(File dir_)
	  {
		  if(dir_==null){
			   return;
		  }
	    if ((!(dir_.exists())) && (!(dir_.mkdirs())) ) throw new RuntimeException("fail to make " + dir_.getAbsolutePath());
	  }
	 private static void createNewFile(File file_)
	  {
		  if(file_==null){
			   return;
		  }
	    if (!(__createNewFile(file_))) throw new RuntimeException(file_.getAbsolutePath() + " doesn't be created!");
	  }
	  private static void delete(File f)
	  {
	    if ((f != null) && (f.exists()) && (!(f.delete())) ) {
	    	throw new RuntimeException(f.getAbsolutePath() + " doesn't be deleted!");
	    }
	    	
	  }
	  private static boolean __createNewFile(File file_)
	  {
		  if(file_==null){
			   return false;
		  }
	    makesureParentExist(file_);
	    if (file_.exists())
	      delete(file_);
	    try
	    {
	      return file_.createNewFile();
	    }
	    catch (IOException e) {
	     e.printStackTrace();
	    }

	    return false;
	  }


	  private static boolean deleteDependon(String filepath, int maxRetryCount)
	  {
	    if (TextUtils.isEmpty(filepath)) return false;
	    return deleteDependon(new File(filepath), maxRetryCount);
	  }

	private static boolean deleteDependon(String filepath)
	  {
	    return deleteDependon(filepath, 0);
	  }


	  private static boolean doesExisted(File file)
	  {
	    return ((file != null) && (file.exists()));
	  }

	  private static boolean doesExisted(String filepath)
	  {
	    if (TextUtils.isEmpty(filepath)) return false;
	    return doesExisted(new File(filepath));
	  }
	  private static void makesureParentExist(File file_)
	  {
	   if(file_==null){
		   return;
	  }
	    File parent = file_.getParentFile();
	    if ((parent != null) && (!(parent.exists())))
	      mkdirs(parent);
	  }

//	  private static void makesureParentExist(String filepath)
//	  {
//	    if(filepath==null){
//	    	return;
//	    }
//	    makesureParentExist(new File(filepath));
//	  }
	  private static void makesureFileExist(File file)
	  {
	   if(file==null)
		   return;
	    if (!(file.exists())) {
	      makesureParentExist(file);
	      createNewFile(file);
	    }
	  }

	  private static void makesureFileExist(String filePath_)
	  {
		  if(filePath_==null)
			   return;
	    makesureFileExist(new File(filePath_));
	  }
	  //判断当前网络是否为wifi
	    public static boolean isWifi(Context mContext) {  
	 	   ConnectivityManager connectivityManager = 
	 		   (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
	 	   NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
	 	   if (activeNetInfo != null  && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){  
	     	        return true;  
	     	 }  
	     return false;  
	    }

	
	 /**
     * 上传图片的策略
     * @author SinaDev
     *
     */
    public static final class UploadImageUtils {
    	 private static void revitionImageSizeHD( String picfile, int size , int quality ) throws IOException {
             if (size <= 0) {
                 throw new IllegalArgumentException("size must be greater than 0!");
             }
             if (!doesExisted(picfile)) {
                 throw new FileNotFoundException(picfile == null ? "null" : picfile );
             }
             
             if (!BitmapHelper.verifyBitmap(picfile)) {
                 throw new IOException("");
             }
             
             int photoSizesOrg = 2 * size;
             FileInputStream input = new FileInputStream(picfile);
             final BitmapFactory.Options opts = new BitmapFactory.Options();
             opts.inJustDecodeBounds = true;
             BitmapFactory.decodeStream(input, null, opts);
            try {
 			input.close();
 		} catch (Exception e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
             
             int rate = 0;
             for (int i = 0;; i++) {
                 if ((opts.outWidth >> i <= photoSizesOrg && (opts.outHeight >> i <= photoSizesOrg))) {
                     rate = i;
                     break;
                 }
             }
             
             opts.inSampleSize = (int) Math.pow(2, rate);
             opts.inJustDecodeBounds = false;
             
             Bitmap temp = safeDecodeBimtapFile( picfile, opts );
             
             if (temp == null) {
                 throw new IOException("Bitmap decode error!");
             }
             
             deleteDependon(picfile);
             makesureFileExist(picfile);
             
             int org = temp.getWidth()>temp.getHeight()?temp.getWidth():temp.getHeight();
             float rateOutPut = size/(float)org;
             
             if(rateOutPut < 1){
             	Bitmap outputBitmap;
             	while(true) {
             		try {
             			outputBitmap = Bitmap.createBitmap(((int)(temp.getWidth()*rateOutPut)), ((int)(temp.getHeight()*rateOutPut)), Bitmap.Config.ARGB_8888);
 						break;
 					} catch (OutOfMemoryError e) {
 						System.gc();
 						rateOutPut = (float)(rateOutPut * 0.8); 
 					}
 				}
             	if(outputBitmap == null){
             		temp.recycle();
             	}
             	Canvas canvas = new Canvas(outputBitmap);
             	Matrix matrix = new Matrix();
             	matrix.setScale(rateOutPut, rateOutPut);
             	canvas.drawBitmap(temp, matrix, new Paint());
             	temp.recycle();
             	temp = outputBitmap;
             }
             final FileOutputStream output = new FileOutputStream(picfile);
             if (opts != null && opts.outMimeType != null
                     && opts.outMimeType.contains("png")) {
                 temp.compress(Bitmap.CompressFormat.PNG, quality, output);
             } else {
                 temp.compress(Bitmap.CompressFormat.JPEG, quality, output);
             }
             try {
 				output.close();
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
             
             temp.recycle();
         }
        private static void revitionImageSize( String picfile, int size ,int quality) throws IOException {
            if (size <= 0) {
                throw new IllegalArgumentException("size must be greater than 0!");
            }
            
            if (!doesExisted(picfile)) {
                throw new FileNotFoundException(picfile == null ? "null" : picfile );
            }
            
            if (!BitmapHelper.verifyBitmap(picfile)) {
                throw new IOException("");
            }
            
            FileInputStream input = new FileInputStream(picfile);
            final BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opts);
           try {
			input.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            
            int rate = 0;
            for (int i = 0;; i++) {
                if ((opts.outWidth >> i <= size) && (opts.outHeight >> i <= size)) {
                    rate = i;
                    break;
                }
            }
            
            opts.inSampleSize = (int) Math.pow(2, rate);
            opts.inJustDecodeBounds = false;
            
            Bitmap temp = safeDecodeBimtapFile( picfile, opts );
            
            if (temp == null) {
                throw new IOException("Bitmap decode error!");
            }
            
            deleteDependon(picfile);
            makesureFileExist(picfile);
            final FileOutputStream output = new FileOutputStream(picfile);
            if (opts != null && opts.outMimeType != null
                    && opts.outMimeType.contains("png")) {
                temp.compress(Bitmap.CompressFormat.PNG, quality, output);
            } else {
                temp.compress(Bitmap.CompressFormat.JPEG, quality, output);
            }
           try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
            temp.recycle();
        }
        public static boolean revitionPostImageSize( String picfile) {
            try {
            	if(Weibo.isWifi){
            		revitionImageSizeHD(picfile, 1600 , 75);
            	}
            	else{
            		revitionImageSize( picfile, 1024 , 75);
            	}
              
                return true;
            } catch (IOException e) {
               e.printStackTrace();
            }
            return false;
        }    
        
        
        /**
         * 如果加载时遇到OutOfMemoryError,则将图片加载尺寸缩小一半并重新加载
         * @param bmpFile
         * @param opts 注意：opts.inSampleSize 可能会被改变
         * @return
         */
        private static Bitmap safeDecodeBimtapFile( String bmpFile, BitmapFactory.Options opts ) {
            BitmapFactory.Options optsTmp = opts;
            if ( optsTmp == null ) {
                optsTmp = new BitmapFactory.Options();
                optsTmp.inSampleSize = 1;
            }
            
            Bitmap bmp = null;
            FileInputStream input = null;
            
            final int MAX_TRIAL = 5;
            for( int i = 0; i < MAX_TRIAL; ++i ) {
                try {
                    input = new FileInputStream( bmpFile );
                    bmp = BitmapFactory.decodeStream(input, null, opts);
                    try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                    break;
                }
                catch( OutOfMemoryError e ) {
                    e.printStackTrace();
                    optsTmp.inSampleSize *= 2;
                    try {
						input.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                }
                catch (FileNotFoundException e) {
                    break;
                }
            }
            
            return bmp;
        }
    }
	
}
