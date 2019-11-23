package com.ischoolbar.programmer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.util.StringUtil;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.service.UserService;

/*
 * 	用户（管理员）控制器
 * 
 * */
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService UserService;
	
	
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		model.setViewName("user/user_list");
		return model;
	}
	
	
	
	//展示页面
	@RequestMapping(value = "/get_list", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getList(
			@RequestParam(value = "username",required = false,defaultValue = "") String username,
			Page page	
			) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("username","%"+username+"%");
		queryMap.put("offset", page.getOffset());
		queryMap.put("pageSize", page.getRows());
		ret.put("rows", UserService.findList(queryMap));
		ret.put("total", UserService.getTotal(queryMap));
		
		return ret;
	}
	
	//删除管理员
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(
			@RequestParam(value ="ids[]",required = true) Long[] ids
			) {
		Map<String, String> ret = new HashMap<String, String>();
		if(ids==null) {
			ret.put("type", "error");
			ret.put("msg", "请选择要删除的数据！");
			return ret;
		}
		
		String idString ="";
		for (Long id : ids) {
			idString+=id+",";
		}
		idString=idString.substring(0,idString.length()-1);
		if(UserService.delete(idString)<=0) {
			ret.put("type", "error");
			ret.put("msg", "删除失败！");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "删除成功！");
		return ret;
	}
	
	

	//修改管理员信息
		@RequestMapping(value = "/edit", method = RequestMethod.POST)
		@ResponseBody
		public Map<String, String> edit(User user) {

			Map<String, String> ret = new HashMap<String, String>();

			if (user == null) {
				ret.put("type", "error");
				ret.put("msg", "数据绑定出错，请联系开发作者");
				return ret;
			}

			if (StringUtil.isEmpty(user.getUsername())) {
				ret.put("type", "error");
				ret.put("msg", "用户名不能为空");
				return ret;
			}
			if (StringUtil.isEmpty(user.getPassword())) {
				ret.put("type", "error");
				ret.put("msg", "密码不能为空");
				return ret;
			}
			
			User exitUser = UserService.findByUserName(user.getUsername());
			if(exitUser!=null) {
				if(user.getId() !=exitUser.getId()) {
					ret.put("type", "error");
					ret.put("msg", "该用户已存在！");
					return ret;
				}
			}
			
			if(UserService.edit(user)<=0) {
				ret.put("type", "error");
				ret.put("msg", "修改失败");
				return ret;
			}
			
			ret.put("type", "success");
			ret.put("msg", "修改成功");
			return ret;
		}
	
	
	//添加管理员
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(User user) {

		Map<String, String> ret = new HashMap<String, String>();

		if (user == null) {
			ret.put("type", "error");
			ret.put("msg", "数据绑定出错，请联系开发作者");
			return ret;
		}

		if (StringUtil.isEmpty(user.getUsername())) {
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空");
			return ret;
		}
		if (StringUtil.isEmpty(user.getPassword())) {
			ret.put("type", "error");
			ret.put("msg", "密码不能为空");
			return ret;
		}
		
		User exitUser = UserService.findByUserName(user.getUsername());
		if(exitUser!=null) {
			ret.put("type", "error");
			ret.put("msg", "用户已存在，请重新添加");
			return ret;
		}
		
		if(UserService.add(user)<=0) {
			ret.put("type", "error");
			ret.put("msg", "添加失败");
			return ret;
		}
		
		ret.put("type", "success");
		ret.put("msg", "登陆成功");
		return ret;
	}

}
