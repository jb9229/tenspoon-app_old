package host.exp.exponent.react;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import host.exp.exponent.MainActivity;
import host.exp.exponent.lockscreen.LockScreenActivity;

/**
 * Created by test on 2017-05-27.
 */

public class MenuModule extends ReactContextBaseJavaModule {
    public MenuModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "NativeMenu";
    }

    @ReactMethod
    public void startLockScreenService() {
        Activity activity = getCurrentActivity();
        if(activity != null && activity instanceof MainActivity)
        {
            ((MainActivity) activity).startLockViewService();
        }else{
            Toast.makeText(getReactApplicationContext(), "Fail, Start Lock Screen Service", Toast.LENGTH_SHORT).show();
        }
    }


    @ReactMethod
    public void stopLockScreenService() {
        Activity activity = getCurrentActivity();
        if(activity != null && activity instanceof MainActivity)
        {
            ((MainActivity) activity).stopLockViewService();
        }else{
            Toast.makeText(getReactApplicationContext(), "Fail, Stop Lock Screen Service", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactMethod
    public void distoryAdview() {
        Activity activity = getCurrentActivity();

        Toast.makeText(getReactApplicationContext(), "call distoryAdview native from ReactNative("+activity+")", Toast.LENGTH_SHORT).show();

        if(activity != null && activity instanceof LockScreenActivity) {
            ((LockScreenActivity) activity).distoryAdview();
        }
    }

    @ReactMethod
    public void adAction() {
        Toast.makeText(getReactApplicationContext(), "call adAction native from ReactNative", Toast.LENGTH_SHORT).show();

        Activity activity = getCurrentActivity();
        if(activity != null && activity instanceof MainActivity) {
//            ((MainActivity) activity).openDrawerMenu();
        }
    }
}
