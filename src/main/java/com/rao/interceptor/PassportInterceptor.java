package com.rao.interceptor;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.rao.dao.LoginTicketDAO;
import com.rao.dao.UserDao;
import com.rao.model.HostHolder;
import com.rao.model.LoginTicket;
import com.rao.model.User;

@Component
public class PassportInterceptor implements HandlerInterceptor{
	@Autowired
	private LoginTicketDAO loginTicketDao;
	
	@Autowired
    private UserDao userDAO;
	
	@Autowired
	private HostHolder hostHolder;
	
	//先执行
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception exception)
			throws Exception {
		hostHolder.clear();
		
	}

	//在渲染页面之前直接可以把user放入spring上下文中
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, ModelAndView modelAndView)
			throws Exception {
		//若存在视图渲染以及用户存在，则将用户信息放入
		if(modelAndView!=null&& hostHolder.getUser() != null){
			//渲染浏览器
			System.out.println(hostHolder.getUser().getName());
			modelAndView.addObject("user", hostHolder.getUser());
		}
		
	}
	//处在所有http请求的最前面
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
		String ticket=null;
		if(httpServletRequest.getCookies()!=null) {
			//找到用户请求中的ticket并将相关信息取出来
			for (Cookie cookie: httpServletRequest.getCookies()) {
				if(cookie.getName().equals("ticket")) {
					ticket=cookie.getValue();
					break;
				}
			}
		}
		
		if(ticket!=null) {
			//通过ticket将ticket的那个字段找出来
			LoginTicket loginTicket=loginTicketDao.selectByTicket(ticket);
			//查看是否在当前时间点之前，或者ticket状态是不是为0
			if(loginTicket==null || loginTicket.getExpired().before(new Date())|| loginTicket.getStatus()!=0) {
				return true;
			}
			//若ticket有效，则将用户的信息取出来，放置到上下文中
			User user=userDAO.selectById(loginTicket.getUserId());
			System.out.println(user.getName());
			hostHolder.setUser(user);
		}
		return true;
	}

}
