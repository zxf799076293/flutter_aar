package com.linhuiba.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.HashMap;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.view.FlutterNativeView;

public class FlutterMainActivity extends FlutterActivity {
    //跳转该页面的时候可以传要跳转的页面,参数名固定为route
    private static final String ROUTE_PAGE = "route";
    String data = "{\n" +
            "\t\"animals\":{\n" +
            "\t\"dog\":[\n" +
            "\t\t{\"name\":\"Rufus\",\"breed\":\"labrador\",\"count\":1,\"twoFeet\":false},\n" +
            "\t\t{\"name\":\"Marty\",\"breed\":\"whippet\",\"count\":1,\"twoFeet\":false}\n" +
            "\t],\n" +
            "\t\"cat\":{\"name\":\"Matilda\"}\n" +
            "}\n" +
            "}";
    public static Intent makeIntent(Context context, String routePage) {
        if (routePage == null || routePage.equals("")) {
            routePage = "/";
        }
        Intent intent = new Intent(context, FlutterMainActivity.class);
        intent.setAction(Intent.ACTION_RUN);
        intent.putExtra(ROUTE_PAGE, routePage);
        return intent;
    }
//    @Override
//    public io.flutter.view.FlutterView createFlutterView(Context context) {
//        WindowManager.LayoutParams matchParent = new WindowManager.LayoutParams(-1, -1);
//        FlutterNativeView nativeView = this.createFlutterNativeView();
//        io.flutter.view.FlutterView flutterView = new io.flutter.view.FlutterView(FlutterMainActivity.this,  null, nativeView);
//        flutterView.setInitialRoute("");
//        flutterView.setLayoutParams(matchParent);
//        this.setContentView(flutterView);
//        return flutterView;
//    }
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //或者在这里初始化Flutter
        //FlutterMain.startInitialization(this);
        super.onCreate(savedInstanceState);
//        makeIntent(this,"firstpage");
        GeneratedPluginRegistrant.registerWith(this);
        native2Dart();
//        FlutterPluginEventTest.registerWith(this.registrarFor(FlutterPluginEventTest.CHANNEL));
//        HashMap<String,Object> map = new HashMap<>();
//        map.put("name","你好flutter，我是native");
//        new MethodChannel(getFlutterView(), "channel:ww").invokeMethod("getName", map, new MethodChannel.Result() {
//            @Override
//            public void success(Object o) {
//                // 这里就会输出 "Hello from Flutter"
//                Log.i("debug", o.toString());
//            }
//            @Override
//            public void error(String s, String s1, Object o) {
//            }
//            @Override
//            public void notImplemented() {
//            }
//        });
        new MethodChannel(getFlutterView(), "channel:Chenli").setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                // methodCall.method 对应 Flutter端invokeMethod方法的第一个参数
                if(methodCall.method.equals("data")) {
                    // 获取Flutter传递的参数
                    String msg = methodCall.<String>argument("msg");
                    // 回传给Flutter
                    Toast.makeText(FlutterMainActivity.this,msg,Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
    private void sendFlutterMsg() {
        new EventChannel(getFlutterView(), "samples.flutter.io/charging").setStreamHandler(
                new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object args, final EventSink events) {

                    }

                    @Override
                    public void onCancel(Object args) {

                    }
                }
        );
    }
    private void native2Dart() {
//        /**
//         * 数据流的通信（event streams） 不能使用
//         */
//        EventChannel eventChannel = new EventChannel(getFlutterView(), "samples.flutter.io/charging");
//        EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler() {
//
//            @Override
//            public void onListen(Object arguments, EventChannel.EventSink eventSink) {
//                Log.e("plateform_channel", "arguments: " + arguments.toString());
//                HashMap<String,Object> map = new HashMap<>();
//                map.put("name","你好flutter，我是native");
//                eventSink.success(data);
//            }
//
//            @Override
//            public void onCancel(Object arguments) {
//                Log.e("plateform_channel", "arguments: " + arguments.toString());
//            }
//        };
//        eventChannel.setStreamHandler(streamHandler);
        new EventChannel(getFlutterView(), "samples.flutter.io/charging").setStreamHandler(
                new EventChannel.StreamHandler() {
                    private BroadcastReceiver chargingStateChangeReceiver;
                    @Override
                    public void onListen(Object arguments, EventSink events) {
                        chargingStateChangeReceiver = createChargingStateChangeReceiver(events);
                        registerReceiver(
                                chargingStateChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        unregisterReceiver(chargingStateChangeReceiver);
                        chargingStateChangeReceiver = null;
                    }
                }
        );
    }
    private BroadcastReceiver createChargingStateChangeReceiver(EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
                    events.error("UNAVAILABLE", "Charging status unavailable", null);
                } else {
                    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL;
                    events.success(isCharging ? "charging" : "discharging");
                }
            }
        };
    }
}
