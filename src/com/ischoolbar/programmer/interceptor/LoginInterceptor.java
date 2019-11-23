package com.ischoolbar.programmer.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ischoolbar.programmer.entity.User;

import net.sf.json.JSONObject;

/*
 * 过滤器
 * */
public class LoginInterceptor  implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		
		StringBuffer url = request.getRequestURL();
		User user = (User) request.getSession().getAttribute("user");
		if(user==null) {
			System.out.println("未登录，或登陆失效  url=\"+url"+url);
			
			//拦截ajax请求
			if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				Map<String, String> ret = new HashMap<String, String>();
				ret.put("type", "error");
				ret.put("msg", "登陆状态已失效，请重新去登陆");
				response.getWriter().write(JSONObject.fromObject(ret).toString());
				response.sendRedirect(request.getContextPath()+"/system/login");
				return false;
			}
			
			//转到登陆页面，进行登陆
			response.sendRedirect(request.getContextPath()+"/system/login");
			return false;
		}
		
		return true;
	}

}
