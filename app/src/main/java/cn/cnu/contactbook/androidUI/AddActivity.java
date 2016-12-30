package cn.cnu.contactbook.androidUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.cnu.contactbook.R;
import cn.cnu.contactbook.controller.Controller;
import cn.cnu.contactbook.model.Contact;

public class AddActivity extends AppCompatActivity {
    private EditText et_name;
    private EditText et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
    }

    public void sure(View v) {
        String name = et_name.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        if (name.equals("") || phone.equals(""))
            Toast.makeText(AddActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
        else {
            Contact contact = new Contact(name, phone);
            Controller controller = new Controller(AddActivity.this);
            controller.add(contact);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
