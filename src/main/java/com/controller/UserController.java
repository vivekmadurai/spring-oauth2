package com.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.services.admin.directory.model.User;
import com.user.UserList;

@Controller
public class UserController 
{
	
	@RequestMapping(value = "/listuser", method = RequestMethod.GET, produces = "application/xml")
	public @ResponseBody UserList getUserList() {
		return new UserList();
	}
	
	@RequestMapping(value = "/syncuser", method = RequestMethod.GET)
	public String syncUsers() {
		UserFeed feed = new UserFeed();
		String url = feed.getRedirectUrl();
		return "redirect:"+url;
	}
	
	@RequestMapping(value = "/oauth2callback", method = RequestMethod.GET)
	public @ResponseBody String processUser(@RequestParam String code) {
		StringBuffer userBuff = new StringBuffer(); 
		UserFeed feed = new UserFeed();
		List<User> allUsers;
		try {
			allUsers = feed.getUserList(code);
			if (allUsers.size() > 0) {
				userBuff.append("Email Id,   Full Name");
				userBuff.append("</br>");
				for (User user : allUsers) {
					userBuff.append(user.getPrimaryEmail());
					userBuff.append(",   ");
					userBuff.append(user.getName().getFullName());
					userBuff.append("</br>");
			    }
			} else {
				userBuff.append("Please provide a valid domain admin user");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userBuff.toString();
	}
}
