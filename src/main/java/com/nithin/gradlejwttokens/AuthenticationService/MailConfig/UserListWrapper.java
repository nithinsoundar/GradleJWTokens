package com.nithin.gradlejwttokens.AuthenticationService.MailConfig;

import com.nithin.gradlejwttokens.AuthenticationService.Model.User;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class UserListWrapper {

    private List<User> userList;

    public UserListWrapper() {
        userList = new ArrayList<>();
    }

    public UserListWrapper(List<User> userList) {
        this.userList = userList;
    }

    @XmlElement(name = "user")
    public List<User> getUserList() {
        return userList;
    }
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
