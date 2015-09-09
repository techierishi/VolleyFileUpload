package com.karmick.volleyfileupload;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_PART_NAME = "file_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //simpleUpload();
        volleyUpload();
    }

    private void simpleUpload() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                filePlusDataUpload();

                return null;
            }
        }.execute();
    }

    private void volleyUpload() {


        try {
            String url = "http://192.168.1.33/upload/upload.php";

            HashMap<String, String> postData = new HashMap<>();
            postData.put("fname", "Rishi");
            postData.put("lname", "Jha");

            MultipartRequest obj = new MultipartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {

                }
            }, new File("/sdcard/Download/test.png"), postData);

            Volley.newRequestQueue(this).add(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void filePlusDataUpload() {
        String charset = "UTF-8";
        File uploadFile1 = new File("/sdcard/Download/test.png");
        File uploadFile2 = new File("/sdcard/Download/test.png");
        String requestURL = "http://192.168.1.33/upload/upload.php";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");

            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");

            multipart.addFilePart("file_name", uploadFile1);
            multipart.addFilePart("file_name", uploadFile2);

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}
