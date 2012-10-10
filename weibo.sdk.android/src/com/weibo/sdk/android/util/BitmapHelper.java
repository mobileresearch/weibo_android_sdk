package com.weibo.sdk.android.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.BitmapFactory;
import android.graphics.Rect;

public final class BitmapHelper {

    /**
     * make sure the color data size not more than 5M
     * 
     * @param rect
     * @return
     */
    public static boolean makesureSizeNotTooLarge(Rect rect) {
        final int FIVE_M = 5 * 1024 * 1024;
        if ( rect.width() * rect.height() * 2 > FIVE_M ) {
            // 不能超过5M
            return false;
        }
        return true;
    }
    
    public static int getSampleSizeOfNotTooLarge( Rect rect ) {
        final int FIVE_M = 5 * 1024 * 1024;
        double ratio = ( ( double ) rect.width() ) * rect.height() * 2 / FIVE_M;
        return ratio >= 1 ? (int)ratio : 1;
    }

    /**
     * 自适应屏幕大小 得到最大的smapleSize
     * 同时达到此目标： 自动旋转 以适应view的宽高后, 不影响界面显示效果
     * @param vWidth view width
     * @param vHeight view height
     * @param bWidth bitmap width
     * @param bHeight bitmap height
     * @return
     */
    public static int getSampleSizeAutoFitToScreen( int vWidth, int vHeight, int bWidth, int bHeight ) {
        if( vHeight == 0 || vWidth == 0 ) {
            return 1;
        }

        int ratio = Math.max( bWidth / vWidth, bHeight / vHeight );

        int ratioAfterRotate = Math.max( bHeight / vWidth, bWidth / vHeight );

        return Math.min( ratio, ratioAfterRotate );
    }
    
    /**
     * 检测是否可以解析成位图
     * 
     * @param datas
     * @return
     */
    public static boolean verifyBitmap(byte[] datas) {
        return verifyBitmap(new ByteArrayInputStream(datas));
    }

    /**
     * 检测是否可以解析成位图
     * 
     * @param input
     * @return
     */
    public static boolean verifyBitmap(InputStream input) {
        if (input == null) {
            return false;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        input = input instanceof BufferedInputStream ? input
                : new BufferedInputStream(input);
        BitmapFactory.decodeStream(input, null, options);
        try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return (options.outHeight > 0) && (options.outWidth > 0);
    }

    /**
     * 检测是否可以解析成位图
     * 
     * @param path
     * @return
     */
    public static boolean verifyBitmap(String path) {
        try {
            return verifyBitmap(new FileInputStream(path));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
