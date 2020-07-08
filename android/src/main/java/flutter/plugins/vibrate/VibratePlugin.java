package flutter.plugins.vibrate;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.HapticFeedbackConstants;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * VibratePlugin
 */
public class VibratePlugin implements MethodCallHandler {

  private Context mContext;

  private boolean playSound=false;

  private VibratePlugin(Registrar registrar){
    this._vibrator = (Vibrator)registrar.context().getSystemService(Context.VIBRATOR_SERVICE);
    mContext=registrar.context().getApplicationContext();
  }

  private Vibrator _vibrator;

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "vibrate");
    channel.setMethodCallHandler(new VibratePlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("vibrate")) {
      if(_vibrator.hasVibrator()){
        int duration = call.argument("duration");
        sartVibrate(duration);
      }
      result.success(null);
    }
    else if(call.method.equals("canVibrate")){
      result.success(_vibrator.hasVibrator());
    } //Feed
    else if(call.method.equals("canPlaySound")){
      if(call.arguments!=null && call.arguments instanceof Boolean){
        playSound= (boolean) call.arguments;
      }
      result.success(null);
    }// back
    else if(call.method.equals("impact")){
      sartVibrate(HapticFeedbackConstants.VIRTUAL_KEY);
      playSystemSound();
      result.success(null);
    }
    else if(call.method.equals("selection")){
      sartVibrate(HapticFeedbackConstants.KEYBOARD_TAP);
      playSystemSound();
      result.success(null);
    }
    else if(call.method.equals("success")){
      sartVibrate(50);
      playSystemSound();
      result.success(null);
    }
    else if(call.method.equals("warning")){
      sartVibrate(250);
      playSystemSound();
      result.success(null);
    }
    else if(call.method.equals("error")){
      sartVibrate(500);
      playSystemSound();
      result.success(null);
    }
    else if(call.method.equals("heavy")){
      sartVibrate(500);
      playSystemSound();
      result.success(null);
    }
    else if(call.method.equals("medium")){
      sartVibrate(40);
      playSystemSound();
      result.success(null);
    }
    else if(call.method.equals("light")){
      sartVibrate(10);
      playSystemSound();
      result.success(null);
    }
    else {
      result.notImplemented();
    }
  }

  private void sartVibrate(int duration){
    if(_vibrator.hasVibrator()){
      _vibrator.vibrate(duration);
    }
  }

  private void playSystemSound(){
    if(playSound){
      Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      Ringtone rt = RingtoneManager.getRingtone(mContext, uri);
      rt.play();
    }
  }
}
