package me.yimu.wexxar.utils;

import android.support.annotation.CheckResult;
import android.support.annotation.WorkerThread;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * Created by linwei on 2018/3/5.
 */

public class FileUtils {
    /***
     * 主要用来拷贝和保存文件,可以用于拷贝Asset,contentProvider和http返回的inputStream到指定文件
     * @param inputStream
     * @param targetFile
     */
    @WorkerThread
    public static final void copy(InputStream inputStream, File targetFile) {
        copy(Okio.source(inputStream), targetFile);
    }

    @WorkerThread
    public static final boolean copy(File originFile, File targetFile) {
        try {
            copy(Okio.source(originFile), targetFile);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public static final void copy(Source source, File targetFile) {
        BufferedSink sink = null;
        try {
            sink = Okio.buffer(Okio.sink(targetFile));
            sink.writeAll(source);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(sink);
            closeQuietly(source);
        }
    }

    /***
     * 读取文件的所有的内容到字符串,使用UTF-8编码
     * @param targetFile
     * @return
     */
    @WorkerThread
    @CheckResult
    public static final String readFileToString(File targetFile) {
        BufferedSource source = null;
        try {
            source = Okio.buffer(Okio.source(targetFile));
            return source.readUtf8();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(source);
        }

        return "";
    }

    @WorkerThread
    @CheckResult
    public static final byte[] readFileToBytes(File targetFile) {
        BufferedSource source = null;
        try {
            source = Okio.buffer(Okio.source(targetFile));
            return source.readByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(source);
        }

        return null;
    }

    /***
     * 将字符串写入文件
     * @param targetFile
     * @param string
     * @return
     */
    @WorkerThread
    public static final void writeStringToFile(File targetFile, String string) {
        BufferedSink sink = null;
        try {
            sink = Okio.buffer(Okio.sink(targetFile));
            sink.writeUtf8(string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(sink);
        }
    }

    /**
     * 删除文件或者文件夹
     *
     * @param file
     * @return
     */
    @WorkerThread
    public static boolean deleteFiles(File file) {
        if (file == null) {
            return true;
        }
        if (file.isDirectory()) {
            String[] children = file.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteFiles(new File(file, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    public static String streamToString(InputStream inputStream) {
        BufferedSource source = null;
        try {
            source = Okio.buffer(Okio.source(inputStream));
            return source.readUtf8();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(source);
        }
        return "";
    }

    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }
}
