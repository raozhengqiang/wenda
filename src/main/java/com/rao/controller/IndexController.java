package com.rao.controller;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class IndexController {
	@RequestMapping("/hello")
	@ResponseBody
	public String index() {
		return "hello";
	}
	
	@RequestMapping("/profile/{userId}")
	@ResponseBody
	public String profile(@PathVariable("userId") int userId,
			@RequestParam("type") int type,@RequestParam("key") int key) {
		return String .format("Profile Page of %d, t:%d k:%d", userId,type,key);
	}
	
	@RequestMapping(path="/vm",method={RequestMethod.GET})
	public String template(Model model) {
		model.addAttribute("value1", "rao1");
		List<String> colors=Arrays.asList(new String[]{"red","green","blue"});
		model.addAttribute("colors", colors);
		Map<String, String> map=new HashMap<>();
		for (int i = 0; i < 4; i++) {
			map.put(String.valueOf(i), String.valueOf(i*i));
			
		}
		model.addAttribute("map", map);
		return "home";
	}
	
	@RequestMapping(path= {"/request"},method= {RequestMethod.GET})
	@ResponseBody
	public String request(Model model,HttpServletResponse response,
							HttpServletRequest request,HttpSession httpSession,
							//读取cookie中的东西
							@CookieValue("JSESSIONID") String sessionId) {
		StringBuilder sb=new StringBuilder();
		sb.append("cookieValue:"+sessionId);
		Enumeration<String> headerNames=request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String name=headerNames.nextElement();
			sb.append(name+":"+request.getHeader(name)+"<br>");
		}
		if(request.getCookies()!=null) {
			for(Cookie cookie:request.getCookies()) {
				sb.append("Cookie:"+cookie.getName()+"value:"+cookie.getValue());
			}
		}
		sb.append(request.getMethod()+"<br>");
		sb.append(request.getQueryString()+"<br>");
		sb.append(request.getPathInfo()+"<br>");
		sb.append(request.getRequestURI()+"<br>");
		return sb.toString();
	}
	
	 @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
	    public RedirectView redirect(@PathVariable("code") int code,
	                                 HttpSession httpSession) {
	        httpSession.setAttribute("msg", "jump from redirect");
	        RedirectView red = new RedirectView("/", true);
	        if (code == 301) {
	            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
	        }
	        return  red;
	    }
	
}
