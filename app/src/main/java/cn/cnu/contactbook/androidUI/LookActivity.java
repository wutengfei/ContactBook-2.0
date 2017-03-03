package cn.cnu.contactbook.androidUI;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;

import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.cnu.contactbook.R;
import cn.cnu.contactbook.controller.Controller;
import cn.cnu.contactbook.model.Contact;


public class LookActivity extends AppCompatActivity {
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_phone2;
    private TextView tv_email;
    private TextView tv_sex;
    private TextView tv_company;
    private String name = "";
    private String phone = "";
    private String phone2 = "";
    private String email = "";
    private String photo = "";
    private String sex = "";
    private String company = "";
    private static int id;
    private Controller controller;
    private ImageView imageView;
    private String imgPath = "";
    private int sleepTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);
        tv_name = (TextView) findViewById(R.id.name);
        tv_phone = (TextView) findViewById(R.id.phone);
        tv_phone2 = (TextView) findViewById(R.id.phone2);
        tv_email = (TextView) findViewById(R.id.email);
        tv_sex = (TextView) findViewById(R.id.sex);
        tv_company = (TextView) findViewById(R.id.company);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        controller = new Controller(this);
        Contact[] contact = controller.getContact(id);
        phone = contact[0].getPhone();
        name = contact[0].getName();
        phone2 = contact[0].getPhone2();
        email = contact[0].getEmail();
        photo = contact[0].getPhoto();
        sex = contact[0].getSex();
        company = contact[0].getCompany();
        tv_phone.setText(phone);
        tv_name.setText(name);
        tv_phone2.setText(phone2);
        tv_email.setText(email);
        tv_sex.setText(sex);
        tv_company.setText(company);
        Bitmap bitmap = getLoacalBitmap(photo); //根据路径从本地取图片
        imageView.setImageBitmap(bitmap);    //设置Bitmap
    }

    public void delete(View v) {
        buildDialog();
    }

    //删除时弹出的提示对话框
    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LookActivity.this,
                android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("将要删除联系人");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                controller.delete(id);
                Toast.makeText(LookActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setPositiveButton("取消", null);
        builder.show();
    }

    public void back(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void edit(View v) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("phone2", phone2);
        intent.putExtra("email", email);
        intent.putExtra("photo", photo);
        intent.putExtra("sex", sex);
        intent.putExtra("company", company);
        startActivity(intent);
    }



    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            if (!url.equals("")) {
                FileInputStream fis = new FileInputStream(url);
                return BitmapFactory.decodeStream(fis);
            }
           else return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    //拨打电话
    public void call(View v) {
        //动态获取打电话权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //这里就是向系统请求权限了,这里我还做了一个判断. sdk是M(M = 23 android L)才做这个请求,否则就不做.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 111);
            }
            return;
        }

        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");//调用系统拨打电话
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
        //监听电话接通状态
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
    //拨打电话
    public void call2(View v) {
        //动态获取打电话权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //这里就是向系统请求权限了,这里我还做了一个判断. sdk是M(M = 23 android L)才做这个请求,否则就不做.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 111);
            }
            return;
        }

        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");//调用系统拨打电话
        intent.setData(Uri.parse("tel:" + phone2));
        startActivity(intent);
        //监听电话接通状态
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void sendMessage(View v) {
        Uri smsToUri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);//调用系统发短信
        //intent.putExtra("发送内容是", " ");
        startActivity(intent);

    }
    public void sendMessage2(View v) {
        Uri smsToUri = Uri.parse("smsto:" + phone2);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);//调用系统发短信
        //intent.putExtra("发送内容是", " ");
        startActivity(intent);

    }
    private class MyPhoneStateListener extends PhoneStateListener {
        private int callCount = 0;
        Thread thread;
        SharedPreferences sp = getSharedPreferences("recallTime", MODE_PRIVATE);
        int sleepTime=sp.getInt("recallTime",10);
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
                                    Thread.sleep(sleepTime*1000);// 线程暂停多少毫秒
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);// 发送消息
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
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
            AlertDialog.Builder builder = new AlertDialog.Builder(LookActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setTitle("是否重新拨打");
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.CALL");//调用系统拨打电话
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            });
            builder.setPositiveButton("取消", null);
            builder.show();
        }
    }

}
