package host.exp.exponent.network;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by test on 2017-01-16.
 */

public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
    //Static Attribute
    public static final String REQUEST_URL                         =   "http://ec2-54-175-206-210.compute-1.amazonaws.com/";
//    public static final String REQUEST_URL                         =   "http://localhost:8080/";
    public final static String REQUESTMETHOD_GET    =   "GET";
    public final static String REQUESTMETHOD_POST   =   "POST";

    //Class Attribute
    HttpURLConnection httpURLConn       =   null;
    URL url                              =   null;
    String accessToken                  =   null;
    Map postDataParams                  =   new HashMap();
    private String requestMethod;


    //Constructor
    public AsyncHttpRequest(String getRequestURL) {
        this.requestMethod = REQUESTMETHOD_GET;

        setHttpConnection(getRequestURL);
    }

    public AsyncHttpRequest(String postRequestURL, Map postParams) {
        this.requestMethod = REQUESTMETHOD_POST;

        setHttpConnection(postRequestURL);

        addPostHeaderData(postParams);

        setPostParameter(postParams);
    }

    public AsyncHttpRequest(String postRequestURL, Map headerParams, Map postParams) {
        this.requestMethod = REQUESTMETHOD_POST;

        connHttpWithoutTokenCheck(postRequestURL);

        addPostHeaderData(headerParams);

        setPostParameter(postParams);
    }


    //Method
    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream             = null;
        InputStreamReader inputStreamReader = null;
        OutputStream outputStream           = null;
        BufferedReader bufferReader         = null;
        BufferedWriter bufferedWriter       = null;
        StringBuilder strBuilder            = null;

//        boolean networkCheckedResult    =   checkNetwork();

        //jinbeomjeong@google.com:1q1q

        try
        {
            if(requestMethod.equals(REQUESTMETHOD_POST))
            {   // 요청
                outputStream = httpURLConn.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(getPostDataString(postDataParams));

                bufferedWriter.flush();

                bufferedWriter.close();
                outputStream.close();
            }


            httpURLConn.connect();


            inputStream     =   httpURLConn.getInputStream();


            inputStreamReader           =   new InputStreamReader(inputStream, "UTF-8");
            bufferReader                =   new BufferedReader(inputStreamReader);
            strBuilder                  =   new StringBuilder();
            String str;

            while ((str = bufferReader.readLine()) != null)
            {
                strBuilder.append(str);
            }

            //Log.v("kkb",strBuilder.toString());
            return strBuilder.toString();


        } catch (IOException e)
        {
            e.printStackTrace();
            return null;

        }finally
        {
            if (strBuilder != null) {strBuilder = null;}

            if (url != null) {url = null;}

            if (bufferReader != null) { try {bufferReader.close();} catch (IOException e) {e.printStackTrace();}    bufferReader = null;}

            if (inputStreamReader != null) {try {inputStreamReader.close();} catch (IOException e) {e.printStackTrace();}   inputStreamReader = null;}

            if (inputStreamReader != null) {try {inputStreamReader.close();} catch (IOException e) {e.printStackTrace();}   inputStreamReader = null;}

            if (inputStream != null) {try {inputStream.close();} catch (IOException e) {e.printStackTrace();}   inputStream = null;}

            if (httpURLConn != null) {httpURLConn.disconnect();}
        }
    }

    private void setHttpConnection(String urlStr) {

        if(accessToken == null)
        {
            getAccessToken();

            int cnt = 0;
            while(cnt < 2 && (accessToken == null || accessToken.isEmpty()))
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {e.printStackTrace();}

                cnt++;
            }
        }

        String httpURL  =   REQUEST_URL + urlStr;

        try {
            url = new URL (httpURL);

            // URL을 이용하여 웹페이지 연결
            httpURLConn = (HttpURLConnection) url.openConnection();
            httpURLConn.setRequestMethod(requestMethod);
            httpURLConn.setReadTimeout(5000 /* milliseconds */);
            httpURLConn.setConnectTimeout(1000 /* milliseconds */);
            httpURLConn.setRequestProperty ("Authorization", "Bearer " + accessToken);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void connHttpWithoutTokenCheck(String urlStr) {

        String httpURL  =   REQUEST_URL + urlStr;

        try {
            url = new URL (httpURL);

            // URL을 이용하여 웹페이지 연결
            httpURLConn = (HttpURLConnection) url.openConnection();
            httpURLConn.setRequestMethod(requestMethod);
            httpURLConn.setReadTimeout(5000 /* milliseconds */);
            httpURLConn.setConnectTimeout(1000 /* milliseconds */);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getAccessToken() {

//        String authorization = "Basic " + new String(Base64.encode("rest-client:rest-secret".getBytes(), 1));

        String authorization = "Basic cmVzdC1jbGllbnQ6cmVzdC1zZWNyZXQ=";


        Map<String, String> headerData  =   new HashMap();

        headerData.put("Authorization", authorization);
        headerData.put("Content-Type", "application/x-www-form-urlencoded");


        Map<String, String> postData  =   new HashMap();

        postData.put("username", "jb9229@gmail.com");
        postData.put("password", "123456");
        postData.put("grant_type", "password");
        postData.put("scope", "read write trust");
        postData.put("client_id", "rest-client");
        postData.put("client_secret", "rest-secret");



        try
        {
            String result   =   new AsyncHttpRequest("/oauth/token", headerData, postData).execute().get();

            if(result == null || result.isEmpty()){System.out.println("***JSONException: Cant get 'Access Token', please check network connection"); return;}

            JSONObject jsonObject   =   new JSONObject(result);

            accessToken =   jsonObject.getString("access_token");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("***JSONException: Cant get 'Access Token', please check network connection");
        }

    }

    private void addPostHeaderData(Map<String, String> headerData) {

        Iterator<String> iter   =   headerData.keySet().iterator();

        while(iter.hasNext())
        {
            String key      = iter.next();

            String value    =   headerData.get(key);

            httpURLConn.setRequestProperty (key, value);
        }
    }

    private String getPostDataString(Map params) {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keySet().iterator();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = null;
            try {
                value = params.get(key);


                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return result.toString();
    }

    private void setPostParameter(Map postParams){

        try
        {
            Iterator<String> iter   =   postParams.keySet().iterator();
            while(iter.hasNext())
            {
                String key  =   iter.next();
                postDataParams.put(key, postParams.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private boolean checkNetwork(){
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//
//        boolean result;
//
//        if (networkInfo != null &&  networkInfo.isConnected()) {
//            result  =    true;
//        } else {
//            result  =    false;
//        }
//
//        return result;
//    }
}
