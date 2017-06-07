package cn.contactbook.androidUI;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.contactbook.R;
import cn.contactbook.controller.Controller;
import cn.contactbook.model.Contact;
import cn.contactbook.service.MyService;


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
    private int sleepTime = 0;

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
       finish();
    }

    /**
     * 跳转到编辑联系人
     * 把姓名，id等内容传递到编辑联系人界面
     *
     * @param v
     */
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
            } else return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    //拨打电话
    public void call(View v) {
        //如果版本>=Android6.0并且检查自身权限没有被赋予时，请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.CALL");//调用系统拨打电话
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
            //启动服务，在service中监听电话状态并进行重播提醒
            Intent callIntent = new Intent(LookActivity.this, MyService.class);
            callIntent.putExtra("phone", phone);
            startService(callIntent);
        }
    }

    //拨打电话
    public void call2(View v) {
        if (phone2.equals("")) {
            Toast.makeText(this, "没有号码", Toast.LENGTH_SHORT).show();
        } else {
            //动态获取打电话权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");//调用系统拨打电话
                intent.setData(Uri.parse("tel:" + phone2));
                startActivity(intent);
                //启动服务，在service中监听电话状态并进行重播提醒
                Intent callIntent = new Intent(LookActivity.this, MyService.class);
                callIntent.putExtra("phone", phone2);
                startService(callIntent);
            }
        }
    }

    public void sendMessage(View v) {
        Uri smsToUri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);//调用系统发短信
        //intent.putExtra("发送内容是", " ");
        startActivity(intent);

    }

    //发送短信
    public void sendMessage2(View v) {
        if (phone2.equals("")) {
            System.out.println("phone2:========" + phone2);
            Toast.makeText(this, "没有号码", Toast.LENGTH_SHORT).show();
        } else {
            Uri smsToUri = Uri.parse("smsto:" + phone2);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);//调用系统发短信
            //intent.putExtra("发送内容是", " ");
            startActivity(intent);
        }
    }

    //处理权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            // 用户取消授权这个数组为空，如果你同时申请两个权限，那么grantResults的length就为2，分别记录你两个权限的申请结果
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意授权时。。。。。
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!this.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                        //用户已经完全拒绝，或手动关闭了权限开启此对话框缓解一下尴尬...
                        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                                .setMessage("不开启该权限将无法正常工作，请在设置中手动开启！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getAppDetailSettingIntent(LookActivity.this);
                                    }
                                })
                                .setNegativeButton("取消", null).create();
                        dialog.show();

                    } else {
                        //用户一直拒绝并一直不勾选“不再提醒”
                        //不执行该权限对应功能模块，也不用提示，因为下次需要权限还会弹出对话框
                    }
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //以下代码可以跳转到应用详情，可以通过应用详情跳转到权限界面(6.0系统测试可用)
    private void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

}
