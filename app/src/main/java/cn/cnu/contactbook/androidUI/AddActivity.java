package cn.cnu.contactbook.androidUI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.cnu.contactbook.R;
import cn.cnu.contactbook.controller.Controller;
import cn.cnu.contactbook.model.Contact;
import cn.cnu.contactbook.utils.PhotoHelper;

public class AddActivity extends AppCompatActivity {
    private EditText et_name;
    private EditText et_phone;
    private EditText et_phone2;
    private EditText et_email;
    private EditText et_sex;
    private EditText et_company;
    private ImageView imageView;
    private String name = "";
    private String phone = "";
    private String phone2 = "";
    private String email = "";
    private String photo = "";
    private String sex = "";
    private String company = "";
    private String imgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_phone2 = (EditText) findViewById(R.id.et_phone2);
        et_email = (EditText) findViewById(R.id.et_email);
        et_sex = (EditText) findViewById(R.id.et_sex);
        et_company = (EditText) findViewById(R.id.et_company);
        imageView=(ImageView)findViewById(R.id.imageView);
    }

    public void sure(View v) {
        name = et_name.getText().toString().trim();
         phone = et_phone.getText().toString().trim();
        phone2 = et_phone2.getText().toString().trim();
        email = et_email.getText().toString().trim();
        sex = et_sex.getText().toString().trim();
        company = et_company.getText().toString().trim();
        photo=imgPath;

        if (name.equals("") || phone.equals(""))
            Toast.makeText(AddActivity.this, "请填写姓名和手机号", Toast.LENGTH_SHORT).show();
        else {
            Contact contact = new Contact(name, phone, phone2, email, photo,sex,company);
            Controller controller = new Controller(AddActivity.this);
            controller.add(contact);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    /**
     * 点击头像选择系统图库或调用相机来选择图片
     *
     * @param view
     */

    public void selectPhoto(View view) {
        //点击打开相册
        PhotoHelper.selectMyPhotoFormGallery(this, PhotoHelper.REQUEST_LOAD_PHOTO_PICKED);
    }

    /**
     * 在onActivityResult中实现裁剪功能,并把图片显示出来
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoHelper.REQUEST_LOAD_PHOTO_PICKED:
                    //imgPath 为裁剪后保存的图片的路径
                    imgPath = PhotoHelper.doCropPhoto(this, data.getData(), PhotoHelper.REQUEST_PHOTO_CROP, true);
                    break;
                case PhotoHelper.REQUEST_PHOTO_CROP:
                    //在这里显示或处理裁剪后的照片
                    Bitmap bitmap = getLoacalBitmap(imgPath); //从本地取图片
                    imageView.setImageBitmap(bitmap);    //设置Bitmap
                    break;
            }
        }
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

}
