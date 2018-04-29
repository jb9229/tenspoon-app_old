package host.exp.exponent.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by test on 2017-03-19.
 */

public class AsyncHttpIMGRequest extends AsyncTask<String, Void, Bitmap> {

    HttpURLConnection httpURLConn       =   null;
    URL url                              =   null;
    String urlStr                        =   null;



    public AsyncHttpIMGRequest(String urlStr) {
        this.urlStr = urlStr;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        InputStream inputStream         = null;
        Bitmap bitmap                   = null;

        String httpURL  =   AsyncHttpRequest.REQUEST_URL + urlStr;

        try
        {
            url = new URL (httpURL);

            httpURLConn = (HttpURLConnection) url.openConnection();
//            httpURLConn.setRequestMethod(requestMethod);
            httpURLConn.setReadTimeout(15000 /* milliseconds */);
            httpURLConn.setConnectTimeout(15000 /* milliseconds */);
//            httpURLConn.setRequestProperty ("Authorization", "Bearer " + accessToken);
            httpURLConn.connect();


            inputStream     =   httpURLConn.getInputStream();

            bitmap          =   BitmapFactory.decodeStream(inputStream);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;

        }finally
        {
            if (url != null) {url = null;}

            if (inputStream != null) {try {inputStream.close();} catch (IOException e) {e.printStackTrace();}   inputStream = null;}

            if (httpURLConn != null) {httpURLConn.disconnect();}
        }

        return bitmap;
    }
}
