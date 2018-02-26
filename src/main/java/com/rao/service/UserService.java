package com.rao.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.rao.dao.LoginTicketDAO;
import com.rao.dao.UserDao;
import com.rao.model.LoginTicket;
import com.rao.model.User;
import com.rao.utils.WendaUtil;
//注册和登陆之后都会跳转到主页面
@Service
public class UserService {
	@Autowired
	UserDao userDao;
	
	@Autowired
	private LoginTicketDAO loginTicketDao;
	//用户注册
	public Map<String,Object> register(String username,String password){
		Map<String, Object> map=new HashMap<String, Object>();
		if(StringUtils.isEmpty(username)) {
			map.put("msg", "用户名不能为空");
			return map;
		}
		if(StringUtils.isEmpty(password)) {
			map.put("msg", "密码不能为空");
			return map;
		}
		//观察用户名是否存在
		User user=userDao.selectByName(username);
		if(user!=null) {
			map.put("msg", "用户名已注册");
			return map;
		}
		user=new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().substring(0,5));
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
		user.setPassword(WendaUtil.MD5(password+user.getSalt()));
		//System.out.println(user);
		userDao.addUser(user);
		//System.out.println(user.toString());
		//System.out.println(user.getId());
		//登陆
		String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
		return map;
	}
	//用户登录，与注册相反
	public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isEmpty(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }
        //选取用户名，用来获取用户
        User user = userDao.selectByName(username);

        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        if (!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码不正确");
            return map;
        }
        
        String ticket=addLoginTicket(user.getId());
        map.put("ticket", ticket);
        
        return map;
    }
	//添加一个ticket
	public String addLoginTicket(int userId) {
		LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();
		
	}
	
	public void logout(String ticket) {
		loginTicketDao.updateStatus(ticket, 1);
	}
	
	public User getUser(int id) {
		return userDao.selectById(id);
	}
	
	public User selectByName(String name) {
		return userDao.selectByName(name);
	}
	
}
