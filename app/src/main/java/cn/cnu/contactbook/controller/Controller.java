package cn.cnu.contactbook.controller;

import android.content.Context;
import cn.cnu.contactbook.model.Contact;
import cn.cnu.contactbook.model.DBAdapter;

/**
 * Created by dell on 2016/10/10.
 */
public class Controller {
    private static DBAdapter dbAdapter;
    Context context;

    public Controller(Context context) {
        this.context = context;
        dbAdapter = new DBAdapter(context);
        dbAdapter.open();
    }

    //添加
    public void add(Contact contact) {
        dbAdapter.insert(contact);

    }

    //查询所有联系人
    public Contact[] getAllContact() {
        return dbAdapter.getAll();
    }

    //查找联系人
    public Contact[] getContact(int id) {
        return dbAdapter.getContact(id);
    }

    //删除联系人
    public void delete(int id) {
        dbAdapter.delete(id);
        dbAdapter.close();
    }

    //更新联系人
    public void update(int id, Contact contact) {
        dbAdapter.update(id, contact);
        dbAdapter.close();
    }
    //初始化联系人
    public Contact[] readContacts(){
        return dbAdapter.readContacts();
    }
}
