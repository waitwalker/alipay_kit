package com.sistalk.alipay_kit;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** AlipayKitPlugin */
public class AlipayKitPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private MethodChannel channel;
  private Context applicationContext;
  private Activity activity;
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.sistalk.alipay_kit");
    channel.setMethodCallHandler(this);
    applicationContext = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if ("isInstalled".equals(call.method)) {
      boolean isInstalled = false;
      try {
        final PackageManager packageManager = applicationContext.getPackageManager();
        final PackageInfo info = packageManager.getPackageInfo("com.eg.android.AlipayGphone", PackageManager.GET_SIGNATURES);
        isInstalled = info != null;
      } catch (PackageManager.NameNotFoundException ignore) {
      }
      result.success(isInstalled);
    } else if ("setEnv".equals(call.method)) {
      int env = 0;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        env = (int) Optional.ofNullable(call.argument("env")).orElse(0);
      }
      switch (env) {
        case 1:
          EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
          break;
        case 0:
        default:
          EnvUtils.setEnv(EnvUtils.EnvEnum.ONLINE);
          break;
      }
      result.success(null);
    } else if ("pay".equals(call.method)) {
      final String orderInfo = call.argument("orderInfo");
      final boolean isShowLoading = Boolean.TRUE.equals(call.argument("isShowLoading"));
      invokeMethod("onPayResp", orderInfo, isShowLoading);
      result.success(null);
    } else if ("auth".equals(call.method)) {
      final String authInfo = call.argument("authInfo");
      final boolean isShowLoading = Boolean.TRUE.equals(call.argument("isShowLoading"));
      invokeMethod("onAuthResp", authInfo, isShowLoading);
      result.success(null);
    } else {
      result.notImplemented();
    }
  }


  private void invokeMethod(String method, String info, boolean isShowLoading) {
    final WeakReference<Activity> activityRef = new WeakReference<>(activity);
    final WeakReference<MethodChannel> channelRef = new WeakReference<>(channel);
    executorService.submit(new Runnable() {
      Map<String, String> result = null;

      @Override
      public void run() {
        final Activity activity = activityRef.get();

        if (activity != null && !activity.isFinishing()) {
          if (method.equals("onPayResp")) {
            PayTask task = new PayTask(activity);
            result = task.payV2(info, isShowLoading);
          } else if (method.equals("onAuthResp")) {
            AuthTask task = new AuthTask(activity);
            result = task.authV2(info, isShowLoading);
          }

        }

        mainHandler.post(() -> {
          if (result != null) {
            final Activity activity1 = activityRef.get();
            final MethodChannel channel = channelRef.get();
            if (activity1 != null && !activity1.isFinishing() && channel != null) {
              channel.invokeMethod(method, result);
            }
          }
        });
      }
    });
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    channel = null;
    applicationContext = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
