package com.rao.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.rao.model.HostHolder;
//如用户没有登录，此拦截器起作用强制转到登陆页面
@Component
public class LoginRequredInterceptor implements HandlerInterceptor{
	
	@Autowired
	HostHolder hostHolder;
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
		//判断cookie中有没有ticket信息
		if(hostHolder.getUser()==null) {
			//先跳转到登录页面，再跳转到请求页面
			httpServletResponse.sendRedirect("/reglogin?next="+httpServletRequest.getRequestURI());
			return false;
		}
		return true;
	}

}
