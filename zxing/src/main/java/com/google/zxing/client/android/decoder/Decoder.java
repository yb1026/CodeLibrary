package com.google.zxing.client.android.decoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.android.QRActivity;
import com.google.zxing.client.android.QRMessageIds;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

/**
 * Created by yb1026 on 2016/12/6.
 */
public class Decoder {


    private static final int MAX_PICTURE_PIXEL = 256;
    private static byte[] yuvs;
    private final QRActivity activity;
    private final QRCodeReader reader;
    private final Hashtable<DecodeHintType, Object> hints;
    private byte[] mRotatedData;
    private Bitmap scanBitmap;

    public static interface DecoderCallBack {

        void startDecode();

        void completeDecoer(Result rawResult);

    }

    public Decoder(QRActivity activity) {

        Collection<BarcodeFormat> decodeFormats = new ArrayList<BarcodeFormat>();
        hints = new Hashtable<DecodeHintType, Object>(3);

        // The prefs can't change while the thread is running, so pick them up
        // once here.
        if (decodeFormats == null || decodeFormats.isEmpty()) {

            decodeFormats = new ArrayList<BarcodeFormat>();

            //条码与二维码控制
            //decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            //decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

        reader = new QRCodeReader();
        this.activity = activity;

    }


    public void destroy() {
        if (this.scanBitmap != null && !this.scanBitmap.isRecycled()) {
            this.scanBitmap.recycle();
        }
    }


    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decode to
     * the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    public void decodeQRCode(byte[] data, int width, int height) {

        if (activity.decoding) {
            return;
        }

        if (null == mRotatedData) {
            mRotatedData = new byte[width * height];
        } else {
            if (mRotatedData.length < width * height) {
                mRotatedData = new byte[width * height];
            }
        }
        Arrays.fill(mRotatedData, (byte) 0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + y * width >= data.length) {
                    break;
                }
                mRotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        Result rawResult = decodeImage(mRotatedData, width, height);

        if (rawResult != null) {
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            Message message = Message.obtain(activity.handler,
                    QRMessageIds.decode_succeeded, rawResult);
            Bundle bundle = new Bundle();

            message.setData(bundle);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(activity.handler,
                    QRMessageIds.decode_failed);
            message.sendToTarget();
        }
    }


    public void decodeQrImagePath(String path, DecoderCallBack decoderCallBack) {

        if (activity.decoding) {
            return;
        }

        decoderCallBack.startDecode();


        new DecodeImageThread(path, decoderCallBack).start();
    }


    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     */
    private Result decodeImage(byte[] data, int width, int height) {
        // 处理
        Result result = null;
        try {
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            /**
             * HybridBinarizer算法使用了更高级的算法，但使用GlobalHistogramBinarizer识别效率确实比HybridBinarizer要高一些。
             *
             * GlobalHistogram算法：（http://kuangjianwei.blog.163.com/blog/static/190088953201361015055110/）
             *
             * 二值化的关键就是定义出黑白的界限，我们的图像已经转化为了灰度图像，每个点都是由一个灰度值来表示，就需要定义出一个灰度值，大于这个值就为白（0），低于这个值就为黑（1）。
             * 在GlobalHistogramBinarizer中，是从图像中均匀取5行（覆盖整个图像高度），每行取中间五分之四作为样本；以灰度值为X轴，每个灰度值的像素个数为Y轴建立一个直方图，
             * 从直方图中取点数最多的一个灰度值，然后再去给其他的灰度值进行分数计算，按照点数乘以与最多点数灰度值的距离的平方来进行打分，选分数最高的一个灰度值。接下来在这两个灰度值中间选取一个区分界限，
             * 取的原则是尽量靠近中间并且要点数越少越好。界限有了以后就容易了，与整幅图像的每个点进行比较，如果灰度值比界限小的就是黑，在新的矩阵中将该点置1，其余的就是白，为0。
             */
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));

            result = reader.decode(bitmap, hints);

        } catch (Exception e) {
            //MyLog.d(this.getClass(), "decode err");
        } finally {
            reader.reset();
        }


        return result;
    }


    private class DecodeImageThread extends Thread {

        private DecoderCallBack decoderCallBack;
        private byte[] mData;
        private int mWidth;
        private int mHeight;

        private String mImgPath;

        private DecodeImageThread(String path, DecoderCallBack decoderCallBack) {
            this.mImgPath = path;
            this.decoderCallBack = decoderCallBack;
        }

        @Override
        public void run() {


            scanBitmap = decodeSampledBitmapFromFile(mImgPath, MAX_PICTURE_PIXEL, MAX_PICTURE_PIXEL);
            this.mData = getYUV420sp(scanBitmap.getWidth(), scanBitmap.getHeight(), scanBitmap);
            this.mWidth = scanBitmap.getWidth();
            this.mHeight = scanBitmap.getHeight();

            Result rawResult = decodeImage(mData, mWidth, mHeight);

            final Result result = rawResult;
            new Handler(activity.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    decoderCallBack.completeDecoer(result);
                }
            }, 1000l);
        }
    }


    /**
     * 将图片根据压缩比压缩成固定宽高的Bitmap，实际解析的图片大小可能和#reqWidth、#reqHeight不一样。
     *
     * @param imgPath   图片地址
     * @param reqWidth  需要压缩到的宽度
     * @param reqHeight 需要压缩到的高度
     * @return Bitmap
     */
    private Bitmap decodeSampledBitmapFromFile(String imgPath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }


    /**
     * 根据给定的宽度和高度动态计算图片压缩比率
     *
     * @param options   Bitmap配置文件
     * @param reqWidth  需要压缩到的宽度
     * @param reqHeight 需要压缩到的高度
     * @return 压缩比
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * YUV420sp
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    private byte[] getYUV420sp(int inputWidth, int inputHeight, Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        /**
         * 需要转换成偶数的像素点，否则编码YUV420的时候有可能导致分配的空间大小不够而溢出。
         */
        int requiredWidth = inputWidth % 2 == 0 ? inputWidth : inputWidth + 1;
        int requiredHeight = inputHeight % 2 == 0 ? inputHeight : inputHeight + 1;

        int byteLength = requiredWidth * requiredHeight * 3 / 2;
        if (yuvs == null || yuvs.length < byteLength) {
            yuvs = new byte[byteLength];
        } else {
            Arrays.fill(yuvs, (byte) 0);
        }

        encodeYUV420SP(yuvs, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuvs;
    }

    /**
     * RGB转YUV420sp
     *
     * @param yuv420sp inputWidth * inputHeight * 3 / 2
     * @param argb     inputWidth * inputHeight
     * @param width
     * @param height
     */
    private void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        // 帧图片的像素大小
        final int frameSize = width * height;
        // ---YUV数据---
        int Y, U, V;
        // Y的index从0开始
        int yIndex = 0;
        // UV的index从frameSize开始
        int uvIndex = frameSize;

        // ---颜色数据---
        // int a, R, G, B;
        int R, G, B;
        //
        int argbIndex = 0;
        //

        // ---循环所有像素点，RGB转YUV---
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                // a is not used obviously
                // a = (argb[argbIndex] & 0xff000000) >> 24;
                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                //
                argbIndex++;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                //
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the sampling is every other
                // pixel AND every other scanline.
                // ---Y---
                yuv420sp[yIndex++] = (byte) Y;
                // ---UV---
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    //
                    yuv420sp[uvIndex++] = (byte) V;
                    //
                    yuv420sp[uvIndex++] = (byte) U;
                }
            }
        }
    }

}
