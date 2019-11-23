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
import com.ischoolbar.programmer.entity.Grade;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.service.GradeService;

@Controller
@RequestMapping("/grade")
public class GradeController {
		@Autowired
		private GradeService gradeService;
	
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		model.setViewName("grade/grade_list");
		return model;
	}
	
	//展示页面
		@RequestMapping(value = "/get_list", method = RequestMethod.POST)
		@ResponseBody
		public Map<String, Object> getList(
				@RequestParam(value = "name",required = false,defaultValue = "") String name,
				Page page	
				) {
			Map<String, Object> ret = new HashMap<String, Object>();
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("name","%"+name+"%");
			queryMap.put("offset", page.getOffset());
			queryMap.put("pageSize", page.getRows());
			ret.put("rows", gradeService.findList(queryMap));
			ret.put("total", gradeService.getTotal(queryMap));
			
			return ret;
		}
		
		//添加
		@RequestMapping(value = "/add", method = RequestMethod.POST)
		@ResponseBody
		public Map<String, String> add(Grade grade) {

			Map<String, String> ret = new HashMap<String, String>();

		
			if (StringUtil.isEmpty(grade.getName())) {
				ret.put("type", "error");
				ret.put("msg", "年级不能为空");
				return ret;
			}
			
			if(gradeService.add(grade)<=0) {
				ret.put("type", "error");
				ret.put("msg", "年级添加失败");
				return ret;
			}
			
			ret.put("type", "success");
			ret.put("msg", "添加成功");
			return ret;
		}
		
		
		//修改
		@RequestMapping(value = "/edit", method = RequestMethod.POST)
		@ResponseBody
		public Map<String, String> edit(Grade grade) {
			Map<String, String> ret = new HashMap<String, String>();
			if (StringUtil.isEmpty(grade.getName())) {
				ret.put("type", "error");
				ret.put("msg", "年级不能为空");
				return ret;
			}
			if(gradeService.edit(grade)<=0) {
				ret.put("type", "error");
				ret.put("msg", "年级添加失败");
				return ret;
			}
			
			ret.put("type", "success");
			ret.put("msg", "修改成功");
			return ret;
		}
	
		
		//删除年级
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
			try {
				if(gradeService.delete(idString)<=0) {
					ret.put("type", "error");
					ret.put("msg", "删除失败！");
					return ret;
				}
			} catch (Exception e) {
				ret.put("type", "error");
				ret.put("msg", "该年级下存在班级，请勿冲动！");
				return ret;
			}
			ret.put("type", "success");
			ret.put("msg", "删除成功！");
			return ret;
		}
		
	
}
