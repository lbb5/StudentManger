package com.ischoolbar.programmer.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.util.StringUtil;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.service.UserService;
import com.ischoolbar.programmer.utils.CpachaUtil;


@Controller
@RequestMapping("/system")
public class SystemController {
	@Autowired
	private UserService UserService;
	
	@RequestMapping(value = "/index",method = RequestMethod.GET)
	public ModelAndView index(ModelAndView model) {
		model.setViewName("system/index");
		return model;
	}
	
	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public ModelAndView login(ModelAndView model) {
		model.setViewName("system/login");
		return model;
	}
	
	
	@RequestMapping(value = "/login",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> login(
			@RequestParam(value = "username",required = true) String username,
			@RequestParam(value = "password",required = true) String password,
			@RequestParam(value = "vcode",required = true) String vcode,
			@RequestParam(value = "type",required = true) int type,
			HttpServletRequest request
			
			) {
		Map<String, String> ret=new HashMap();
		if(StringUtil.isEmpty(username)) {
			ret.put("type","error");
			ret.put("msg","用户名不能为空");
			return ret;
		}
		if(StringUtil.isEmpty(password)) {
			ret.put("type","error");
			ret.put("msg","密码不能为空");
			return ret;
		}
		if(StringUtil.isEmpty(vcode)) {
			ret.put("type","error");
			ret.put("msg","验证码不能为空");
			return ret;
		}
		
		String cpacha = (String) request.getSession().getAttribute("cpacha");
		if(StringUtil.isEmpty(cpacha)) {
			ret.put("type","error");
			ret.put("msg","长时间未操作，会话失效");
			return ret;
		}
		if(!cpacha.toUpperCase().equals(vcode.toUpperCase())) {
			ret.put("type","error");
			ret.put("msg","验证码错误");
			return ret;
		}
		if(type==1) {
			//管理员
			User user = UserService.findByUserName(username);
			if(user==null) {
				ret.put("type","error");
				ret.put("msg","用户名不存在");
				return ret;
			}
			if(!user.getPassword().toUpperCase().equals(password.toUpperCase())) {
				ret.put("type","error");
				ret.put("msg","密码错误");
				return ret;
			}
			request.getSession().setAttribute("user", user);
		}
		
		if(type==2) {
			//学生
		}
		
		ret.put("type","success");
		ret.put("msg","登陆成功！");
		
		return ret;
	}
	//获取验证码
		@RequestMapping(value = "/get_cpacha",method = RequestMethod.GET)
		public void getCpacha(HttpServletRequest request,HttpServletResponse response) {
			 //生成验证码生成器对象
			 CpachaUtil cpachaUtil = new CpachaUtil(4,98,33);
			 //生成验证码，传到session域当中，为接下来的表单验证做准备
			 String generatorVCode = cpachaUtil.generatorVCode();
			 request.getSession().setAttribute("cpacha", generatorVCode);
			 
			 //把随机生成的验证码，生成一张图片
			 BufferedImage generatorVCodeImage = cpachaUtil.generatorVCodeImage(generatorVCode, true);
			 
			 //把图片，用输出流，输入到页面上
			 try {
				ImageIO.write(generatorVCodeImage, "gif", response.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	 
}
