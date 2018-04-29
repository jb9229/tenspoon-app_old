package host.exp.exponent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.ReactPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import host.exp.exponent.generated.DetachBuildConstants;
import host.exp.exponent.experience.DetachActivity;
import host.exp.exponent.lockscreen.LockScreenService;

public class MainActivity extends DetachActivity {

  @Override
  public String publishedUrl() {
    return "exp://exp.host/@jb9229/tenspoon";
  }

  @Override
  public String developmentUrl() {
    return DetachBuildConstants.DEVELOPMENT_URL;
  }

  @Override
  public List<String> sdkVersions() {
    return new ArrayList<>(Arrays.asList("26.0.0"));
  }

  @Override
  public List<ReactPackage> reactPackages() {
    return ((MainApplication) getApplication()).getPackages();
  }

  @Override
  public boolean isDebug() {
    return BuildConfig.DEBUG;
  }

  @Override
  public Bundle initialProps(Bundle expBundle) {
    // Add extra initialProps here
    return expBundle;
  }

  public void startLockViewService(){
    Intent intent = new Intent(MainActivity.this, LockScreenService.class);
    startService(intent);
  }


  public void stopLockViewService(){
    Intent intent = new Intent(MainActivity.this, LockScreenService.class);
    stopService(intent);

  }
}
