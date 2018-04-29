package host.exp.exponent.lockscreen;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.react.views.view.ReactViewGroup;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import host.exp.exponent.MainActivity;
import host.exp.exponent.R;
import host.exp.exponent.network.AsyncHttpRequest;
import host.exp.exponent.react.TSReactPackage;
import host.exp.exponent.tools.DownLoadImageTask;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
//import com.google.android.gms.common.api.GoogleApiClient;

public class LockScreenActivity extends Activity{
    //Test
    public static final int TEMP_ACCOUNTID =   2;

    //Static Final Attribute
    private static final int SPOON_UP_EXPORSE   =   1;
    private static final int SPOON_UP_LINK      =   2;
    private static final String URL_SPOON_UP    =   "/api/v1/spoon/rice/add/{accountId}/{rice}";
    private static final String URL_SPOON_GET   =   "/api/v1/spoon/{accountId}";
    private static final String URL_ADTS_GET    =   "/api/v1/adts/{accountId}";
    private static final String ADMOB_AD_UNIT_ID   = "ca-app-pub-9415708670922576/7209392876";
    private static final String ADMOB_APP_ID        =   "ca-app-pub-9415708670922576~1619490004";
    private static final int PBS_TYPE_ADTS         =   1;
//    private static final String ADMOB_AD_UNIT_ID   = "ca-app-pub-9415708670922576/7285785625";
//    private static final String ADMOB_APP_ID        = "ca-app-pub-9415708670922576~1619490004";
//    private static final String ADMOB_AD_UNIT_ID   = "ca-app-pub-3940256099942544/2247696110";
//    private static final String ADMOB_APP_ID        = "ca-app-pub-3940256099942544~3347511713";

    //
    private ActionBarDrawerToggle mDrawerToggle;
    private Button mRefresh;
    private Button installBut;
    private TextView adLinkText;
    private TextView dayText;
    private TextView hourText;
    private SeekBar adUnlockSeekbar;
    private CheckBox mRequestAppInstallAds;
    private CheckBox mRequestContentAds;

    //Side View Attribute
    TextView sideRiceProgTxView;
    ProgressBar sideRiceProgress;
    TextView sideContentsTextView;
    TextView sideTitleTextView;
    WebView sideVideoWebView;
    Button  sideDonaResultButt;
    Thread tTask;

    //AdMob Attribute
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        //Load Publish
        boolean isAdTSLoaded    =   false;
        JSONObject adTSResponse  =   loadAdTS();
        if(adTSResponse == null)
        {
            setContentView(R.layout.activity_banner_admob);

            loadAdMob();
        }else
        {
            setContentView(R.layout.ad_ts_default);

            try {
                loadAdTSView(adTSResponse);

            } catch (JSONException e) {Toast.makeText(getApplicationContext(), R.string.ERRORMSG_UNEXPECT, Toast.LENGTH_SHORT);}

            isAdTSLoaded    =   true;
        }



        //Lock Control View Background
        DrawerLayout lockControlLayout    =     addLockControlView();

        checkLockControlBackground(isAdTSLoaded);


        dayText     =   (TextView) findViewById(R.id.clock_date);
        hourText    =   (TextView) findViewById(R.id.clock_hour);

        tTask = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    Date rightNow = new Date();

                    SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm a");
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM.dd(E)");

                    final String dateString = dateFormatter.format(rightNow);
                    final String hourString = hourFormatter.format(rightNow);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dayText.setText(dateString);
                            hourText.setText(hourString);
                        }
                    });

                    try {

                        Thread.sleep(2000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        tTask.start();



        ///Add Unlock React View
        ReactRootView mReactRootView          =   new ReactRootView(this);
        ReactInstanceManager mReactInstanceManager  =    ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModulePath("index")
                .addPackage(new MainReactPackage())
                .addPackage(new TSReactPackage())
                .setUseDeveloperSupport(com.facebook.react.BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

        mReactInstanceManager.onHostResume(this, null);

        mReactRootView.startReactApplication(mReactInstanceManager, "Tenspoon", null);

        final FrameLayout contentFrame = (FrameLayout) findViewById(R.id.native_content_frame);
        contentFrame.addView(mReactRootView);


        mReactRootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                    case MotionEvent.ACTION_MOVE :
                    case MotionEvent.ACTION_UP   :
                        // 이미지 뷰의 위치를 옮기기
                        contentFrame.setBackgroundResource(R.drawable.unlock_bg_touch);
                }

                return true;
            }
        });



        //Add Side View Content
        mDrawerToggle = new ActionBarDrawerToggle(this, lockControlLayout,
                null, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                setSlideviewData();
            }
        };

        // Set the drawer toggle as the DrawerListener
        lockControlLayout.addDrawerListener(mDrawerToggle);

        ListView sideListview = (ListView) findViewById(R.id.left_drawer);

        ArrayList<String> items = new ArrayList<>();
        items.add("item1");
        items.add("item2");
        items.add("item3");
        items.add("item4");
        items.add("item5");
        items.add("item6");

        CustomAdapter adapter = new CustomAdapter(this, 0, items);
        sideListview.setAdapter(adapter);
//        int childCnt    =   ((ReactRootView)MainActivity.getVisibleActivity().getRootView()).getChildCount();
//
//        for(int i=0; i<childCnt; i++)
//        {
//            System.out.println(((ReactRootView)MainActivity.getVisibleActivity().getRootView()).getChildAt(i));
//        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private DrawerLayout addLockControlView() {
        //Add Lock Control Content View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout lockControlLayout = (DrawerLayout) inflater.inflate(R.layout.lock_screen_control, null);
        RelativeLayout.LayoutParams paramRelative = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);


        addContentView(lockControlLayout, paramRelative);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return lockControlLayout;
    }

    private void checkLockControlBackground(boolean isSuccLoadTSAD) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.lockControl_bottom_area);


        if(layout == null){return;}


        if(isSuccLoadTSAD)
        {
            layout.setBackgroundResource(0);
        }else
        {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_lockscreen_bottom) );
            } else {
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_lockscreen_bottom));
            }
        }

    }

    private JSONObject loadAdTS() {
        //Get Publish from API
        try {
            String requestParam = URL_ADTS_GET.replace("{accountId}", "" + TEMP_ACCOUNTID);

            String response = new AsyncHttpRequest(requestParam).execute().get();


            //Validation
            if (response == null)
            {
                Toast.makeText(getApplicationContext(), R.string.ERRORMSG_ADTS_GET, Toast.LENGTH_SHORT);
                return null;
            }

            JSONObject jsonObject = new JSONObject(response);

            JSONObject pbsTypeObj = jsonObject.getJSONObject("pbsType");

            int bpsType = pbsTypeObj.getInt("adType");

            if(bpsType != PBS_TYPE_ADTS){ return null;}

            return jsonObject;
        }catch (Exception e){Toast.makeText(getApplicationContext(), R.string.ERRORMSG_UNEXPECT, Toast.LENGTH_SHORT);}



        return null;
    }

    private void loadAdTSView(JSONObject adTSRspObj) throws JSONException {
        //Get json Publish
        try {
            //GET AdTS
            String imgUrl = adTSRspObj.getString("imgUrl");


            ImageView imgView = (ImageView) findViewById(R.id.adts_df_bgimg);


            new DownLoadImageTask(imgView).execute(imgUrl);

        }catch (Exception e){Toast.makeText(getApplicationContext(), R.string.ERRORMSG_UNEXPECT, Toast.LENGTH_SHORT);}
    }

    private void loadAdMob() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, ADMOB_APP_ID);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                Toast.makeText(this, "Can't Back Action.", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshAdMob(true, false);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     * @param requestAppInstallAds indicates whether app install ads should be requested
     * @param requestContentAds    indicates whether content ads should be requested
     */
    private void refreshAdMob(boolean requestAppInstallAds, boolean requestContentAds) {
//        if (!requestAppInstallAds && !requestContentAds) {
//            Toast.makeText(this, "At least one ad format must be checked to request an ad.",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        AdLoader.Builder builder = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID);
//
//        if (requestAppInstallAds) {
//            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
//                @Override
//                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
//                    FrameLayout frameLayout =
//                            (FrameLayout) findViewById(R.id.fl_adplaceholder);
//                    NativeAppInstallAdView adView = (NativeAppInstallAdView) getLayoutInflater()
//                            .inflate(R.layout.ad_app_install, null);
//                    populateAppInstallAdView(ad, adView);
//                    frameLayout.removeAllViews();
//                    frameLayout.addView(adView);
//                }
//            });
//        }
//
//        if (requestContentAds) {
//            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
//                @Override
//                public void onContentAdLoaded(NativeContentAd ad) {
//                    FrameLayout frameLayout =
//                            (FrameLayout) findViewById(R.id.fl_adplaceholder);
//                    NativeContentAdView adView = (NativeContentAdView) getLayoutInflater()
//                            .inflate(R.layout.ad_content, null);
//                    populateContentAdView(ad, adView);
//                    frameLayout.removeAllViews();
//                    frameLayout.addView(adView);
//                }
//            });
//        }
//
//        AdLoader adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(LockScreenActivity.this, "Failed to load native ad: "
//                        + errorCode, Toast.LENGTH_SHORT).show();
//            }
//        }).build();
//
//        adLoader.loadAd(new AdRequest.Builder().build());


        //Banner
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        if(mAdView != null){mAdView.loadAd(adRequest);}
    }

    private void adAction() {
//        adLinkText.performClick();
        installBut.performClick();
    }

    private void increaseExposeCnt() {
        boolean netState   =   checkNetwork();

        if(netState)
        {
            int accountId   =   2;

            String requestURL   =   URL_SPOON_UP.replace("{accountId}",""+accountId);

            requestURL          =   requestURL.replace("{rice}",""+SPOON_UP_EXPORSE);

            new AsyncHttpRequest(requestURL).execute();
        }
    }

    private void increaseLinkCnt() {
        boolean netState   =   checkNetwork();

        if(netState)
        {
            int accountId   =   2;

            String requestURL   =   URL_SPOON_UP.replace("{accountId}",""+accountId);

            requestURL          =   requestURL.replace("{rice}",""+SPOON_UP_LINK);


            new AsyncHttpRequest(requestURL).execute();
        }
    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // 네트워크가 연결되어 있을 때 -> HttpURLConnection
        if (networkInfo != null &&  networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "네트워크 연결상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void distoryAdview() {
        increaseExposeCnt();

//        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.adView);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable(){
//                    @Override
//                    public void run() {
//                        frameLayout.removeAllViews();
//                        tTask.interrupt();
//                    }
//                });
//            }
//        }).start();

//        mAdView.removeAllViews();
        this.finishAndRemoveTask();
    }

    //Set Side View
    private void setSlideviewData() {
        //Step 1. Spoon list 요청(현재는 한개의 Spoon만 리스트)
        //Step 2. Spoon data View 설정
        JSONArray jsonArr =   null;
        JSONObject jsonSpoon = null;
        JSONObject jsonBowl = null;


        String responsedSideViewData    =   null;
        try
        {
            String requestParam     =   URL_SPOON_GET.replace("{accountId}",""+ TEMP_ACCOUNTID);

            responsedSideViewData   =   new AsyncHttpRequest(requestParam).execute().get();


            //Validation
            if(responsedSideViewData == null){sideTitleTextView.setText("Fail Connection.."); return;}

            jsonArr     =   new JSONArray(responsedSideViewData);

            jsonSpoon   =   jsonArr.getJSONObject(0);
            jsonBowl    =   jsonSpoon.getJSONObject("bowl");

            String videoUrl    =   jsonBowl.getString("imgPath");

            int riceTol     =   jsonBowl.getInt("riceTol");
            int riceAim     =   jsonBowl.getInt("riceAim");

//            String videoUrl =   jsonBowl.getString("videoUrl");

//            String videoUrl =   "https://player.vimeo.com/video/209839145";
//            String data_html = "<!DOCTYPE HTML> <html xmlns='http://www.w3.org/1999/xhtml' xmlns:og='http://opengraphprotocol.org/schema/' " +
//                    "xmlns:fb='http://www.facebook.com/2008/fbml'> " +
//                    "<head><meta http-equiv='Content-Security-Policy' " +
//                    "content='default-src * gap:; script-src * 'unsafe-inline' 'unsafe-eval'; connect-src *; img-src * data: blob: " +
//                    "android-webview-video-poster:; style-src * 'unsafe-inline';></head> " +
//                    "<body style='margin:0 0 0 0; padding:0 0 0 0;'> " +
//                    "<iframe width='225' height='160' src='https://player.vimeo.com/video/209839145' frameborder='0'></iframe> </body> </html> ";
//
//            String cspMetaTag   =   "<meta http-equiv=\"Content-Security-Policy\" content=\"default-src * 'self' cdvfile://*; script-src * 'unsafe-inline' 'unsafe-eval'; connect-src *; img-src * data: blob: android-webview-video-poster:; style-src * 'unsafe-inline';\">";
//
//            String data_html = "<html> <head> "+ cspMetaTag +
//                    " </head> <body>" +
//                    "<iframe src=\"https://player.vimeo.com/video/209839145\" width=\"225\" height=\"160\" frameborder=\"0\" ></iframe>" +
//                    "</body></html>";
//
//            videoUrl    =   "<iframe src='https://player.vimeo.com/video/209839145' width='225' height='160' frameborder='0' webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";

            sideTitleTextView.setText(jsonBowl.getString("title"));

            sideRiceProgTxView.setText(String.format("%,d",riceTol)+"/"+String.format("%,d",riceAim)+"(원)");

            sideRiceProgress.setMax(riceAim);
            sideRiceProgress.setProgress(riceTol);

            sideContentsTextView.setText(jsonBowl.getString("contents"));

            sideVideoWebView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
            sideVideoWebView.setWebChromeClient(new WebChromeClient());
            sideVideoWebView.getSettings().setJavaScriptEnabled(true);
            sideVideoWebView.getSettings().setAppCacheEnabled(true);
            sideVideoWebView.getSettings().setBuiltInZoomControls(true);
            sideVideoWebView.getSettings().setSaveFormData(true);
//            sideVideoWebView.loadUrl(videoUrl);
            sideVideoWebView.loadData(videoUrl, "text/html",  "UTF-8");

        }
        catch (InterruptedException e) {e.printStackTrace();}
        catch (ExecutionException e) {e.printStackTrace();}
        catch (JSONException e) {Toast.makeText(getApplicationContext(), R.string.ERRORMSG_SIDEVIEW_OPEN, Toast.LENGTH_SHORT);System.out.println("***JSONException: Cant get convert side view contents, please check side view data -> "+responsedSideViewData);}
    }

    private void setSideVideoView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            sideVideoWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }



    //Ref Class
    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View sideLayout = vi.inflate(R.layout.ad_side_view, null);


                if ("item1".equals(items.get(position))) {
                    sideTitleTextView = (TextView) sideLayout.findViewById(R.id.side_total_title);
                    v = sideTitleTextView;

                } else if ("item2".equals(items.get(position))) {
                    sideVideoWebView = (WebView) sideLayout.findViewById(R.id.side_video_webView);
                    v = sideVideoWebView;

                    setSideVideoView();

                } else if ("item3".equals(items.get(position))) {
                    sideRiceProgTxView = (TextView) sideLayout.findViewById(R.id.side_total_text);
                    v = sideRiceProgTxView;

                } else if ("item4".equals(items.get(position))) {
                    sideRiceProgress = (ProgressBar) sideLayout.findViewById(R.id.side_total_progress);

                    v = sideRiceProgress;
                } else if ("item5".equals(items.get(position))) {
                    sideContentsTextView = (TextView) sideLayout.findViewById(R.id.side_donation_detail);
                    v = sideContentsTextView;

                    sideContentsTextView.setMovementMethod(new ScrollingMovementMethod());
                } else if ("item6".equals(items.get(position))) {
                    sideDonaResultButt = (Button) sideLayout.findViewById(R.id.preDodaResultButton);
                    v = sideDonaResultButt;
                }
            }

            // ImageView 인스턴스


//            TextView textView = (TextView)v.findViewById(R.id.textView);
//            textView.setText(items.get(position));
//
//            final String text = items.get(position);
//            Button button = (Button)v.findViewById(R.id.button);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(LockScreenActivity.this, text, Toast.LENGTH_SHORT).show();
//                }
//            });

            return v;
        }
    }
}
