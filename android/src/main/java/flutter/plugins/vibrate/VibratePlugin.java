package flutter.plugins.vibrate;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
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

  private SoundPool mSoundPool;

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
        initSoundPool();
      }
      result.success(null);
    }// back
    else if(call.method.equals("impact")){
      sartVibrate(HapticFeedbackConstants.VIRTUAL_KEY);
      playSystemSound(0);
      result.success(null);
    }
    else if(call.method.equals("selection")){
      sartVibrate(HapticFeedbackConstants.KEYBOARD_TAP);
      playSystemSound(0);
      result.success(null);
    }
    else if(call.method.equals("success")){
      sartVibrate(50);
      playSystemSound(0);
      result.success(null);
    }
    else if(call.method.equals("warning")){
      sartVibrate(250);
      playSystemSound(R.raw.error_sound);
      result.success(null);
    }
    else if(call.method.equals("error")){
      sartVibrate(500);
      playSystemSound(R.raw.error_sound);
      result.success(null);
    }
    else if(call.method.equals("heavy")){
      sartVibrate(500);
      playSystemSound(0);
      result.success(null);
    }
    else if(call.method.equals("medium")){
      sartVibrate(40);
      playSystemSound(0);
      result.success(null);
    }
    else if(call.method.equals("light")){
      sartVibrate(10);
      playSystemSound(0);
      result.success(null);
    }
    else {
      result.notImplemented();
    }
  }

  private void initSoundPool() {
    if (Build.VERSION.SDK_INT >= 21) {
      SoundPool.Builder builder = new SoundPool.Builder();
      //传入最多播放音频数量,
      builder.setMaxStreams(1);
      //AudioAttributes是一个封装音频各种属性的方法
      AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
      //设置音频流的合适的属性
      attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
      //加载一个AudioAttributes
      builder.setAudioAttributes(attrBuilder.build());
      mSoundPool = builder.build();
    } else {
      /**
       * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
       * 第二个参数：int streamType：AudioManager中描述的音频流类型
       *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
       */
      mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

  }

  private void sartVibrate(int duration){
    if(_vibrator.hasVibrator()){
      _vibrator.vibrate(duration);
    }
  }

  private void playSystemSound(int resId){
    if(playSound&&mSoundPool!=null){
      if(resId==0){
        resId=R.raw.success_sound;
      }
      //可以通过四种途径来记载一个音频资源：
      //1.通过一个AssetFileDescriptor对象
      //int load(AssetFileDescriptor afd, int priority)
      //2.通过一个资源ID
      //int load(Context context, int resId, int priority)
      //3.通过指定的路径加载
      //int load(String path, int priority)
      //4.通过FileDescriptor加载
      //int load(FileDescriptor fd, long offset, long length, int priority)
      //声音ID 加载音频资源,这里用的是第二种，第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
      final int voiceId = mSoundPool.load(mContext,resId, 1);
      //异步需要等待加载完成，音频才能播放成功
      mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
          if (status == 0) {
            //第一个参数soundID
            //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
            //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
            //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
            //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
            //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
            soundPool.play(voiceId, 1, 1, 1, 0, 1);
          }
        }
      });
    }
  }
}
