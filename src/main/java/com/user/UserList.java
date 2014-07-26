package com.user;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Users")
public class UserList {

	@XmlElement(name = "User", required = true)
	public static List<User> userList = new ArrayList<User>();
}
