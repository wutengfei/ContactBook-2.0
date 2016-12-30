package cn.cnu.contactbook.androidUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import cn.cnu.contactbook.R;
import cn.cnu.contactbook.controller.Controller;
import cn.cnu.contactbook.model.Contact;

import java.util.*;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView lv;
    private SearchView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initContacts();
    }

    private void initContacts() {
        Controller controller = new Controller(MainActivity.this);
        Contact[] contact = controller.getAllContact();
        if (contact == null) {//
             //动态获取读取联系人权限
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        1);
            }

            Contact[] contacts = controller.readContacts();
            for (int i = 0; i < contacts.length; i++)
                controller.add(contacts[i]);
        }
    }

    /**
     * 将适配器显示在onStart方法中是为了让每次显示此界面时都刷新列表
     */
    @Override
    public void onStart() {
        super.onStart();
        lv = (ListView) findViewById(R.id.listView);

        sv = (SearchView) findViewById(R.id.searchView);
        lv.setTextFilterEnabled(true);//设置lv可以被过虑
        // 设置该SearchView默认是否自动缩小为图标
        sv.setIconifiedByDefault(true);
        // 为该SearchView组件设置事件监听器
        sv.setOnQueryTextListener(this);
        // 设置该SearchView显示搜索按钮
        // sv.setSubmitButtonEnabled(true);
        // 设置该SearchView内默认显示的提示文本
        //sv.setQueryHint("");

        final List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        Controller controller = new Controller(MainActivity.this);
        Contact[] contacts = controller.getAllContact();

        if (contacts != null)
            for (int i = 0; i < contacts.length; i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("id", contacts[i].getId());
                item.put("name", contacts[i].getName());
                item.put("phone", contacts[i].getPhone());
                data.add(item);
            }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview, new String[]{"name"}, new int[]{R.id.name});


        if (lv != null) {
            lv.setAdapter(adapter);
            //listView点击事件
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override //设置点击事件要判断这个position是对应原来的list，还是搜索后的list，parent.getAdapter().getItem(position)。
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    System.out.println("--------点击的是-----" + parent.getAdapter().getItem(position).toString());
                    List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

                    data.add((HashMap<String, Object>) parent.getAdapter().getItem(position));
                    Set<String> keySet = data.get(0).keySet();//用Set的keySet方法取出key的集合
                    Iterator<String> it = keySet.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (key.equals("id")) {
                            int value = (int) data.get(0).get(key);//拿到key对应的value
                            Intent intent = new Intent(MainActivity.this, LookActivity.class);
                            intent.putExtra("id", value);//把id传递到下一个界面
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    public void add(View v) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // 实际应用中应该在该方法内执行实际查询
        // 此处仅使用Toast显示用户输入的查询内容
        Toast.makeText(this, "您的选择是:" + query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (TextUtils.isEmpty(newText)) {
            // 清除ListView的过滤
            lv.clearTextFilter();
        } else {
            // 使用用户输入的内容对ListView的列表项进行过滤
            lv.setFilterText(newText);
        }
        return true;
    }


    //监听返回键退出事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();

        }
        return false;
    }

    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
}
