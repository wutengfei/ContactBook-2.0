package cn.cnu.contactbook.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.sql.SQLOutput;

import static android.content.Context.MODE_PRIVATE;

public class PhoneStateReceive extends BroadcastReceiver {

    private Context mContext;
    private int mCurrentState = TelephonyManager.CALL_STATE_IDLE ;
    private int mOldState = TelephonyManager.CALL_STATE_IDLE ;
    SharedPreferences sharedPref = mContext.getSharedPreferences("IDENTIFY", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPref.edit();
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            mOldState = sharedPref.getInt("callState", 0);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    mCurrentState = TelephonyManager.CALL_STATE_IDLE;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    mCurrentState = TelephonyManager.CALL_STATE_OFFHOOK;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    mCurrentState = TelephonyManager.CALL_STATE_RINGING;
                    break;
            }

            if(mOldState == TelephonyManager.CALL_STATE_IDLE && mCurrentState == TelephonyManager.CALL_STATE_OFFHOOK ) {
                Log.i("callState", "onCallStateChanged: 接通");
                System.out.println("------------接通-----------------");
                editor.putInt("callState", mCurrentState);
            } else if (mOldState == TelephonyManager.CALL_STATE_OFFHOOK && mCurrentState == TelephonyManager.CALL_STATE_IDLE) {
                Log.i("callState", "onCallStateChanged: 挂断");
                System.out.println("------------挂断-----------------");

                editor.putInt("callState", mCurrentState);
            }
        }
    }

}