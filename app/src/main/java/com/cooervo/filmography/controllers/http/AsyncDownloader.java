package com.cooervo.filmography.controllers.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Public class to be re used for downloading JSON response using library
 * OkHttp.
 * <p/>
 * We extend AsyncTask because:
 * An asynchronous task is defined by a computation that runs on a background
 * thread and whose result is published on the UI thread. An asynchronous task is defined
 * by 3 generic types, called Params, Progress and Result,
 * and 4 steps, called onPreExecute, doInBackground, onProgressUpdate and onPostExecute.
 */
public class AsyncDownloader extends AsyncTask<String, Integer, String> {

    public static final String TAG = AsyncDownloader.class.getSimpleName();

    private Context context;
    private Class classToLoad;
    private ProgressDialog dialog;

    private String url;

    public AsyncDownloader(Context ctx, Class c) {
        context = ctx;
        classToLoad = c;
    }

    /**
     * onPreExecute runs on the UI thread before doInBackground.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * doInBackground() runs in the background on a worker thread. This is where code that can block the GUI should go.
     *
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {

        String url = params[0];

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);

        Response response = null;

        String jsonData = null;

        try {
            response = call.execute();

            if (response.isSuccessful()) {
                jsonData = response.body().string();

            } else {
                jsonData = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData; //This is returned to onPostExecute()
    }

    /**
     * onProgressUpdate this is called in the UI thread when you call publishProgress.
     * It’s a good place to update progress dialogs and show the user that things are
     * still working.
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);
    }

    /**
     * onPostExecute runs on the UI thread and will be delivered
     * the result of doInBackground
     *
     * @param jsonData
     */
    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        dialog.dismiss();

        Intent i = new Intent(context, classToLoad);
        i.putExtra("jsonData", jsonData);
        context.startActivity(i);
    }
}