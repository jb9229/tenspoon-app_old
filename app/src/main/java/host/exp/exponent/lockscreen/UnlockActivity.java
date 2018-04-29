package host.exp.exponent.lockscreen;


import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.BuildConfig;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import java.util.Arrays;
import java.util.List;

public class UnlockActivity extends ViewGroupManager<ViewGroup> {
    //static constants
    public static final String REACT_CLASS = "LockScreen";


    //
    private ThemedReactContext context = null;
    private FrameLayout frameLayout = null;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected FrameLayout createViewInstance(ThemedReactContext reactContext) {
        getUIManagerModule();
        context = reactContext;

        frameLayout = new FrameLayout(reactContext);

        return frameLayout;
    }

    public FrameLayout getFrameLayout() {
        return frameLayout;
    }

    public void setFrameLayout(FrameLayout frameLayout) {
        this.frameLayout = frameLayout;
    }
}
