import Flutter
import UIKit
import AudioToolbox
import AVFoundation

private let isDevice = TARGET_OS_SIMULATOR == 0
    
public class SwiftVibratePlugin: NSObject, FlutterPlugin {
    private var playSound = false
    private var successPlayer: AVAudioPlayer?
    private var failedPlayer: AVAudioPlayer?

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "vibrate", binaryMessenger: registrar.messenger())
    let instance = SwiftVibratePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      switch (call.method) {
          case "canVibrate":
              if isDevice {
                result(true)
              } else {
                result(false)
              }
        case "canPlaySound":
        if call.arguments != nil {
            playSound = call.arguments as! Bool;
        }
          case "vibrate":
            AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            // Feedback
          case "impact":
            if #available(iOS 10.0, *) {
              let impact = UIImpactFeedbackGenerator()
              impact.prepare()
              impact.impactOccurred()
            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
          case "selection":
            if #available(iOS 10.0, *) {
              let selection = UISelectionFeedbackGenerator()
              selection.prepare()
              selection.selectionChanged()
            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
          case "success":
//            if #available(iOS 10.0, *) {
//              let notification = UINotificationFeedbackGenerator()
//              notification.prepare()
//              notification.notificationOccurred(.success)
//            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
        if (playSound) {
            successPlay();
        }
//            }
          case "warning":
            if #available(iOS 10.0, *) {
              let notification = UINotificationFeedbackGenerator()
              notification.prepare()
              notification.notificationOccurred(.warning)
            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
          case "error":
//            if #available(iOS 10.0, *) {
//              let notification = UINotificationFeedbackGenerator()
//              notification.prepare()
//              notification.notificationOccurred(.error)
//            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
        if (playSound) {
            failedPlay()
        }
//            }
          case "heavy":
            if #available(iOS 10.0, *) {
              let generator = UIImpactFeedbackGenerator(style: .heavy)
              generator.prepare()
              generator.impactOccurred()
            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
         case "medium":
            if #available(iOS 10.0, *) {
              let generator = UIImpactFeedbackGenerator(style: .medium)
              generator.prepare()
              generator.impactOccurred()
            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
         case "light":
            if #available(iOS 10.0, *) {
              let generator = UIImpactFeedbackGenerator(style: .light)
              generator.prepare()
              generator.impactOccurred()
            } else {
              // Fallback on earlier versions
              AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
          default:
              result(FlutterMethodNotImplemented)
      }
    }
    func successPlay() {
        if let path = Bundle.main.path(forResource: "success", ofType: "mp3") {
        let url = URL.init(fileURLWithPath: path)
            if self.successPlayer == nil {
                self.successPlayer = try? AVAudioPlayer.init(contentsOf: url)
            }
            if let play = self.failedPlayer?.play() {
                if play {
                    self.failedPlayer?.pause();
                }
            }
        self.successPlayer?.play();
        }
    }
    
    func failedPlay() {
        if let path = Bundle.main.path(forResource: "failed", ofType: "wav") {
        let url = URL.init(fileURLWithPath: path);
            if self.failedPlayer == nil {
                self.failedPlayer = try? AVAudioPlayer.init(contentsOf: url)
            }
            if let play = self.successPlayer?.play() {
                if play {
                    self.successPlayer?.pause();
                }
            }
        self.failedPlayer?.play();
        }
    }
    
    deinit {
        self.successPlayer = nil
        self.failedPlayer = nil
    }
}
