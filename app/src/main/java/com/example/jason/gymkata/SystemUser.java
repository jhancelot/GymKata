package com.example.jason.gymkata;

/**
 * Created by Jason on 2016-04-25.
 */
public class SystemUser {
    long _id;
    String _name;
    String _password;

    public SystemUser() {}

    public SystemUser(long id, String name, String pass) {
        set_id(id);
        set_name(name);
        set_password(pass);
    }
    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }
}
