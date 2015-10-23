package com.dev.melosz.melodroid.utils;

import android.util.Log;

/**
 * Created by Marek on 10/11/2015.
 * Custom Logging Utility Class derived from Android's android.util.log. An alternative to standard
 * low-level logging, a second method is available with a method parameter so the method's name can
 * be logged, which can help to locate the log origin quicker.  In general, the low-level logging
 * will be used in Activities and Fragments due to their dynamic nature, while Logging with the
 * method parameter will be used for more static-based classes.
 */
public class LogUtil {

    // Priority constant for the println method; use Log.v.
    public static final int VERBOSE = 2;

    // Priority constant for the println method; use Log.d.
    public static final int DEBUG = 3;

    // Priority constant for the println method; use Log.i.
    public static final int INFO = 4;

    // Priority constant for the println method; use Log.w.
    public static final int WARN = 5;

    // Priority constant for the println method; use Log.e.
    public static final int ERROR = 6;

    // Priority constant for the println method.
    @SuppressWarnings("unused")
    public static final int ASSERT = 7;

    public LogUtil() {
    }

    /**
     * Send a {@link #VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void v(String tag, String msg) {
        println(VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message with the method.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param method The message where the log occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void v(String tag, String method, String msg) {
        println(VERBOSE, tag, method, msg);
    }

    /**
     * Send a {@link #DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void d(String tag, String msg) {
        println(DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message with the method.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param method The method where the log occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void d(String tag, String method, String msg) {
        println(DEBUG, tag, method, msg);
    }

    /**
     * Send an {@link #INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void i(String tag, String msg) {
        println(INFO, tag, msg);
    }

    /**
     * Send a {@link #INFO} log message with the method.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param method The method where the log occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void i(String tag, String method, String msg) {
        println(INFO, tag, method, msg);
    }

    /**
     * Send a {@link #WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void w(String tag, String msg) {
        println(WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message with the method.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param method The method where the log occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void w(String tag, String method, String msg) {
        println(WARN, tag, method, msg);
    }

    /**
     * Send an {@link #ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void e(String tag, String msg) {
        println(ERROR, tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message with the method.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param method The method where the log occurs.
     * @param msg The message you would like logged.
     */
    @SuppressWarnings("unused")
    public void e(String tag, String method, String msg) {
        println(ERROR, tag, method, msg);
    }

    /**
     * Low-level logging call by priority.
     * @param priority The priority/type of this log message
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public void println(int priority, String tag, String msg) {
        // Log depending on priority
        switch (priority){
            case VERBOSE:
                Log.v(tag, msg);
                break;
            case DEBUG:
                Log.d(tag, msg);
                break;
            case INFO:
                Log.i(tag, msg);
                break;
            case WARN:
                Log.w(tag, msg);
                break;
            case ERROR:
                Log.e(tag, msg);
                break;
            case ASSERT:
                Log.v(tag, msg);
                break;
            default:
                Log.i(tag, msg);
                break;
        }
    }
    /**
     * Low-level logging call by priority with method name.
     * @param priority The priority/type of this log message
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param method The method where the log occurs.
     * @param msg The message you would like logged.
     */
    public void println(int priority, String tag, String method, String msg) {
        // build the string with the appended Method name
        msg = method + ": " + msg;

        // Log depending on priority
        switch (priority){
            case VERBOSE:
                Log.v(tag, msg);
                break;
            case DEBUG:
                Log.d(tag, msg);
                break;
            case INFO:
                Log.i(tag, msg);
                break;
            case WARN:
                Log.w(tag, msg);
                break;
            case ERROR:
                Log.e(tag, msg);
                break;
            case ASSERT:
                Log.v(tag, msg);
                break;
            default:
                Log.i(tag, msg);
                break;
        }
    }
}
