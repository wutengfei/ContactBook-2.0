package cn.cnu.contactbook.androidUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.cnu.contactbook.R;
import cn.cnu.contactbook.controller.Controller;
import cn.cnu.contactbook.model.Contact;

public class EditActivity extends AppCompatActivity {
    private String name;
    private String phone;
    private int id;
    private EditText et_name;
    private EditText et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();//接收上个活动传来的数据
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        id = intent.getIntExtra("id", 0);
        et_name = (EditText) findViewById(R.id.name);
        et_phone = (EditText) findViewById(R.id.phone);
        et_name.setText(name);
        et_phone.setText(phone);
    }

    //点击确定时更新数据
    public void sure(View v) {
        String phone_new = et_phone.getText().toString().trim();
        String name_new = et_name.getText().toString().trim();
        Contact contact = new Contact(name_new, phone_new);
        Controller controller = new Controller(this);
        controller.update(id, contact);
        finish();
    }
}
