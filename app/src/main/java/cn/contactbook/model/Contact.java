package cn.contactbook.model;

/**
 * Created by dell on 2016/10/10.
 */
public class Contact {
    private String name;
    private String phone;
    private String phone2;
    private String email;
    private String photo;
    private String company;
    private String sex;
    private int id;

    public Contact() {
    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Contact(int id, String name, String phone, String phone2, String email, String photo) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.phone2 = phone2;
        this.email = email;
        this.photo = photo;
    }

    public Contact(String name, String phone, String phone2, String email, String photo,  String sex,String company) {
        this.name = name;
        this.phone = phone;
        this.phone2 = phone2;
        this.email = email;
        this.photo = photo;
        this.sex = sex;
        this.company = company;

    }

    public Contact(String name, String phone, String phone2, String email, String photo) {
        this.name = name;
        this.phone = phone;
        this.phone2 = phone2;
        this.email = email;
        this.photo = photo;
    }

    public Contact(String name, String phone, String phone2, String email) {
        this.name = name;
        this.phone = phone;
        this.phone2 = phone2;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
