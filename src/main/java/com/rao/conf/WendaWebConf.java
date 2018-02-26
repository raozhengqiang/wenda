package com.rao.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.rao.interceptor.LoginRequredInterceptor;
import com.rao.interceptor.PassportInterceptor;
//将构造的拦截器添加
@Component
public class WendaWebConf extends WebMvcConfigurerAdapter{
	
	@Autowired
	PassportInterceptor passportInterceptor;
	
	@Autowired
	LoginRequredInterceptor loginRequredInterceptor;
	//在工程启动时，自动注册一个拦截器
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(passportInterceptor);
		registry.addInterceptor(loginRequredInterceptor).addPathPatterns("/user/*");//表示访问
		super.addInterceptors(registry);
	}
}
