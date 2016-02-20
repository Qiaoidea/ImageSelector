package com.qiao.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ImageLoadUtil {
    /**
     * 图片缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 轮询的线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;
    /**
     * 线程池的线程数量，默认为1
     */
    private int mThreadCount = 1;

    private static ImageLoadUtil mInstance;

    /**
     * 队列的调度方式
     *
     * @author zhy
     */
    public enum Type {
        FIFO, LIFO
    }


    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static ImageLoadUtil getInstance() {

        if (mInstance == null) {
            synchronized (ImageLoadUtil.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoadUtil(3, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    private ImageLoadUtil(int threadCount, Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, Type type) {
        // loop thread
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            ;
        };

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mThreadCount = threadCount;
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;

    }

    /**
     * 加载图片
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        loadImage(path, imageView, null);
    }

    public void loadImage(final String path, final ImageView imageView, final ImageLoadListener imageLoadListener) {
        // set tag
        imageView.setTag(path);
        // UI线程
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ImageOption holder = (ImageOption) msg.obj;
                    ImageView imageView = holder.imageView;
                    Bitmap bm = holder.bitmap;
                    String path = holder.path;
                    ImageLoadListener imageLoadListener = holder.imageLoadListener;
                    if (null != bm && imageView.getTag().toString().equals(path)) {
                        imageView.setImageBitmap(bm);
                        if (null != imageLoadListener)
                            imageLoadListener.onLoadComplete(imageView, bm, true);
                    } else if (null != imageLoadListener) {
                        imageLoadListener.onLoadComplete(imageView, bm, false);
                    }
                }
            };
        }

        final ImageOption option = new ImageOption(imageView,path,imageLoadListener);

        Bitmap bm = getBitmapFromLruCache(option);
        if (bm != null) {
            option.bitmap = bm;
            sendToTarget(option);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {

                    Bitmap bm = decodeSampledBitmapFromResource(path,
                            option.width,option.height);
                    addBitmapToLruCache(option, bm);
                    option.bitmap = bm;
                    sendToTarget(option);
                    mPoolSemaphore.release();
                }
            });
        }
    }

    private void sendToTarget(ImageOption option){
        Message message = Message.obtain();
        message.obj = option;
        mHandler.sendMessage(message);
    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);

        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取消所有任务
     */
    public void cancelAllTasks() {
        mTasks.clear();
        mPoolThreadHander.removeMessages(0x110);
        mPoolSemaphore.release(mThreadCount);
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static ImageLoadUtil getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (ImageLoadUtil.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoadUtil(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    public Bitmap getBitmapFromLruCache(ImageOption option) {
        String key = new StringBuffer(Base64.encodeToString(option.path.getBytes(),Base64.URL_SAFE))
                .append("_").append(option.width).append("x").append(option.height).toString();
        return mLruCache.get(key);
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    public Bitmap getBitmapFromLruCache(ImageView imageView,String path) {
        ImageOption option = new ImageOption(imageView,path,null);
        String key = new StringBuffer(Base64.encodeToString(path.getBytes(),Base64.URL_SAFE))
                .append("_").append(option.width).append("x").append(option.height).toString();
        return mLruCache.get(key);
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param option
     * @param bitmap
     */
    private void addBitmapToLruCache(ImageOption option, Bitmap bitmap) {
        String key = new StringBuffer(Base64.encodeToString(option.path.getBytes(),Base64.URL_SAFE))
                .append("_").append(option.width).append("x").append(option.height).toString();
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
    }

    /**
     * 计算inSampleSize，用于压缩图片
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        return bitmap;
    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;

                Log.e("TAG", value + "");
            }
        } catch (Exception e) {
        }
        return value;
    }

    private class ImageOption {
        int width;
        int height;
        Bitmap bitmap;
        ImageView imageView;
        String path;
        ImageLoadListener imageLoadListener;

        public ImageOption(ImageView imageView, String path, ImageLoadListener imageLoadListener) {
            this.imageView = imageView;
            this.path = path;
            this.imageLoadListener = imageLoadListener;
            wrap();
        }

        /**
         * 根据ImageView获得适当的压缩的宽和高
         *
         * @return
         */
        private void wrap(){
            final DisplayMetrics displayMetrics = imageView.getContext()
                    .getResources().getDisplayMetrics();
            final LayoutParams params = imageView.getLayoutParams();

            int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
                    .getWidth(); // Get actual image width
            if (width <= 0)
                width = params.width; // Get layout width parameter
            if (width <= 0)
                width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
            // maxWidth
            // parameter
            if (width <= 0)
                width = displayMetrics.widthPixels;
            int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
                    .getHeight(); // Get actual image height
            if (height <= 0)
                height = params.height; // Get layout height parameter
            if (height <= 0)
                height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
            // maxHeight
            // parameter
            if (height <= 0)
                height = displayMetrics.heightPixels;
            this.width = width;
            this.height = height;
        }
    }

    public interface ImageLoadListener {
        void onLoadComplete(ImageView imageView, Bitmap bitmap, boolean isSuccess);
    }
}
