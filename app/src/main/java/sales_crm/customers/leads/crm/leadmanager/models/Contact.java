package sales_crm.customers.leads.crm.leadmanager.models;

import java.io.Serializable;

public class Contact implements Serializable {

    @Override
    public boolean equals(Object obj) {
        return this.uid.equals(((Contact) obj).getUid()) && this.name.equals(((Contact) obj).getName());
    }

    String name;
    String email;
    String phone;
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    String address;
}
