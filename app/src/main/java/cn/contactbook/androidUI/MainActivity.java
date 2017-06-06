package cn.contactbook.androidUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import cn.contactbook.R;
import cn.contactbook.controller.Controller;
import cn.contactbook.model.Contact;

import java.util.*;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView lv;
    private SearchView sv;
    private static final int item1 = Menu.FIRST;
    Contact[] contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    /**
     * 将适配器显示在onStart方法中是为了让每次显示此界面时都刷新列表
     */
    @Override
    public void onStart() {
        super.onStart();
        lv = (ListView) findViewById(R.id.listView);

        sv = (SearchView) findViewById(R.id.searchView);
        //lv.setTextFilterEnabled(true);//设置lv可以被过虑
        // 设置该SearchView默认是否自动缩小为图标
        sv.setIconifiedByDefault(true);
        // 为该SearchView组件设置事件监听器
        sv.setOnQueryTextListener(this);
        // 设置该SearchView显示搜索按钮
        // sv.setSubmitButtonEnabled(true);
        // 设置该SearchView内默认显示的提示文本
        //sv.setQueryHint("");

        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        Controller controller = new Controller(MainActivity.this);
        contacts = controller.getAllContact();

        if (contacts != null)
            for (int i = 0; i < contacts.length; i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("id", contacts[i].getId());
                item.put("name", contacts[i].getName());
                item.put("phone", contacts[i].getPhone());
                item.put("phone2", contacts[i].getPhone2());
                item.put("email", contacts[i].getEmail());
                item.put("photo", contacts[i].getPhoto());
                item.put("sex", contacts[i].getSex());
                item.put("company", contacts[i].getCompany());

                data.add(item);
            }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview,
                new String[]{"photo", "name"}, new int[]{R.id.imageView, R.id.name});
        //把头像填充到适配器中
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });

        /**
         * 给ListView绑定适配器，并设置点击事件
         */
        if (lv != null) {
            lv.setAdapter(adapter);
            //listView点击事件
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                //设置点击事件要判断这个position是对应原来的list，还是搜索后的list，parent.getAdapter().getItem(position)。
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "该软件需要悬浮窗权限，请授予！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            } else {
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        }


    }

    /**
     * 用户返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, AddActivity.class);
                    startActivity(intent);
                }
            }

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // 实际应用中应该在该方法内执行实际查询
        // 此处仅使用Toast显示用户输入的查询内容
        // Toast.makeText(this, "您的选择是:" + query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<HashMap<String, Object>> obj = searchItem(newText);
        updateLayout(obj);
        return false;

//        if (TextUtils.isEmpty(newText)) {
//            // 清除ListView的过滤
//            lv.clearTextFilter();
//        } else {
//            // 使用用户输入的内容对ListView的列表项进行过滤
//            lv.setFilterText(newText);
//        }
//        return true;
    }

    /**
     * 搜索主要逻辑。数据库中contact的姓名和输入框中输入的文字一致就存放到新ArrayList
     *
     * @param name 输入框中输入的文字
     * @return dataList  搜索结果存放的ArrayList
     */
    public ArrayList<HashMap<String, Object>> searchItem(String name) {
        ArrayList dataList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < contacts.length; i++) {
            int index = contacts[i].getName().indexOf(name);//搜索框内输入的内容在ListView各条目中的位置 ，内容不匹配就返回-1
            System.out.println("index-" + index);
            // 存在匹配的数据
            if (index != -1) {

                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("id", contacts[i].getId());
                item.put("name", contacts[i].getName());
                item.put("phone", contacts[i].getPhone());
                item.put("phone2", contacts[i].getPhone2());
                item.put("email", contacts[i].getEmail());
                item.put("photo", contacts[i].getPhoto());
                item.put("sex", contacts[i].getSex());
                item.put("company", contacts[i].getCompany());

                dataList.add(item);

            }
        }
        return dataList;
    }

    /**
     * 更新适配器
     * @param obj
     */
    public void updateLayout(ArrayList<HashMap<String, Object>> obj) {
        lv.setAdapter(new SimpleAdapter(this, obj, R.layout.listview,
                new String[]{"photo", "name"}, new int[]{R.id.imageView, R.id.name}));
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

    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, item1, 0, "设置");

        return true;
    }

    //菜单列表
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case item1:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingActivity.class);
                MainActivity.this.startActivity(intent);
                break;
        }
        return true;
    }
}
