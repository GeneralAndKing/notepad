package com.np.notepad.util;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * 日志工具类
 *
 * @author fengxin
 */
public class LoggerUtils {

    /**
     * 打印log开关
     */
    private static boolean LOG = true;

    static {
        // 初始化logger
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                // (Optional) Whether to show thread info or not. Default true
                .showThreadInfo(false)
                // (Optional) How many method line to show. Default 2
                .methodCount(0)
                // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .tag("[NpBlock]")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    /**
     * Log.e 打印
     *
     * @param text 需要打印的内容
     */
    public static void e(String text) {
        if (LOG) {
            Logger.e(text);
        }
    }

    /**
     * Log.d 打印
     *
     * @param text 需要打印的内容
     */
    public static void d(String text) {
        if (LOG) {
            Logger.d(text);
        }
    }

    /**
     * 打印json格式
     *
     * @param text 需要打印的内容
     */
    public static void toJson(String text) {
        if (LOG) {
            Logger.json(text);
        }
    }

    /**
     * Log.i 打印
     *
     * @param text 需要打印的内容
     */
    public static void i(String text) {
        if (LOG) {
            Logger.i(text);
        }
    }

    private LoggerUtils(){}
}
