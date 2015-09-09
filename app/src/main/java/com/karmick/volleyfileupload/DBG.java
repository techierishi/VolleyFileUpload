package com.karmick.volleyfileupload;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Administrator on 14-May-15.
 */

public class DBG {

    public static boolean DEBUG = true;
    public static boolean WEB_DEBUG = true;

    public static void d(String tag, String value) {
        if (DEBUG)
            Log.d(tag, value);
    }

    public static void e(String tag, String value) {
        if (DEBUG)
            Log.e(tag, value);
    }

    public static void w(String tag, String value) {
        if (DEBUG)
            Log.w(tag, value);
    }

    public static void printStackTrace(Exception e) {
        if (DEBUG) {
            if (e != null)
                e.printStackTrace();
        }
    }


    public enum COLOR {
        RED,
        GREEN,
        BLUE
    }

    /**
     * File write debugger
     *
     * @param text
     * @param doAppend
     * @param color
     */
    public static void appendLog(String text, boolean doAppend, COLOR color) {
        if (DEBUG) {
            DBG.d("DBG", "" + text);

            File logFile = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/velopal.html");

            String ecolor = "";
            if (color == COLOR.BLUE) {
                ecolor = "#2073F0";
            } else if (color == COLOR.GREEN) {
                ecolor = "#009A57";
            } else {
                ecolor = "#D33E2A";
            }


            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                        doAppend));

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String v_time = sdf.format(cal.getTime());


                buf.append(" <p style=\"color:" + ecolor + ";\">[" + v_time + "] " + text
                        + "</p>");

                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void appendWebLog(final String tag, final String content) {

        if (WEB_DEBUG) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String response = "";
                    // Making HTTP request
                    try {

                        URL url = new URL("http://192.168.1.33/androidWebLog/post_debug.php");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);

                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Cache-Control", "no-cache");

                        //Post Data
                        List<NameValue> nameValuePair = new ArrayList<NameValue>();
                        nameValuePair.add(new NameValue("tag", "" + tag));
                        nameValuePair.add(new NameValue("content", "" + content));

                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getQuery(nameValuePair));

                        writer.flush();
                        writer.close();
                        os.close();

                        conn.connect();


                        try {
                            InputStream is = conn.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            is.close();

                            response = sb.toString();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Buffer Error", "Error converting result " + e.toString());
                        }


                        conn.disconnect();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return response;
                }

                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                }
            }.execute();
        }

    }


    private static String getQuery(List<NameValue> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValue pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static class NameValue {
        private String name, value;

        public NameValue(String _name, String _value) {
            name = _name;
            value = _value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
