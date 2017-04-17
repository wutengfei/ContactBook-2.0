package cn.contactbook.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.*;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;


public class MyService extends Service {

    private String phone = "";

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        phone = intent.getStringExtra("phone");
        System.out.println("phone=" + phone);
        //监听电话接通状态
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 监听电话状态类，挂断重拨的逻辑
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        private int callCount = 0;
        Thread thread;
        SharedPreferences sp = getSharedPreferences("recallTime", MODE_PRIVATE);
        int sleepTime = sp.getInt("recallTime", 100000000);

        /**
         * CALL_STATE_IDLE 无任何状态时
         * CALL_STATE_OFFHOOK 接起电话时(正在拨通中或接通)
         * CALL_STATE_RINGING 电话进来时（通话时有来电打入）
         */
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://无任何状态时

                    final Handler handler = new Handler() {
                        public void handleMessage(Message msg) {
                            // 要做的事情
                            super.handleMessage(msg);
                            if (callCount >= 1) {//第二次及以后不再弹出提示对话框
                                callCount++;
                                thread = null;
                            } else {//第一次挂断时弹出提示对话框
                                callCount++;

                                newDialog();
                            }
                        }
                    };

                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(sleepTime * 1000);// 线程暂停多少毫秒
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);// 发送消息
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread.start();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接起电话时
                    break;
                case TelephonyManager.CALL_STATE_RINGING://电话进来时
                    break;
                default:
                    break;
            }
        }

        private void newDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyService.this);
            builder.setTitle("是否重新拨打");
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent();


                    intent.setAction("android.intent.action.CALL");//调用系统拨打电话
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            });
            builder.setPositiveButton("取消", null);
            Dialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();


        }

    }

}
