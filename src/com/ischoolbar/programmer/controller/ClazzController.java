package com.ischoolbar.programmer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.util.StringUtil;
import com.ischoolbar.programmer.entity.Clazz;
import com.ischoolbar.programmer.entity.Grade;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.service.ClazzService;
import com.ischoolbar.programmer.service.GradeService;

import net.sf.json.JSONArray;
/*
 * 班级管理控制器
 * */
@Controller
@RequestMapping("/clazz")
public class ClazzController {
		@Autowired
		private GradeService gradeService;
		@Autowired
		private ClazzService clazzService;
	/*
	 * 班级列表页
	 * */
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		model.setViewName("clazz/clazz_list");
		List<Grade> findAll = gradeService.findAll();
		System.out.println(findAll);
		model.addObject("gradeList", findAll);
		model.addObject("gradeListJson", JSONArray.fromObject(findAll));
		return model;
	}
	
	//展示页面
		@RequestMapping(value = "/get_list", method = RequestMethod.POST)
		@ResponseBody
		public Map<String, Object> getList(
				@RequestParam(value = "name",required = false,defaultValue = "") String name,
				@RequestParam(value = "gradeId",required = false) Long gradeId,
				Page page	
				) {
			Map<String, Object> ret = new HashMap<String, Object>();
			Map<String, Object> queryMap = new HashMap<String, Object>();
			if(gradeId !=null) {
				queryMap.put("gradeId",gradeId);
			}
			queryMap.put("name","%"+name+"%");
			queryMap.put("offset", page.getOffset());
			queryMap.put("pageSize", page.getRows());
			ret.put("rows", clazzService.findList(queryMap));
			ret.put("total",clazzService.getTotal(queryMap));
			
			return ret;
		}
		
		//添加
		@RequestMapping(value = "/add", method = RequestMethod.POST)
		@ResponseBody
		public Map<String, String> add(Clazz clazz) {

			Map<String, String> ret = new HashMap<String, String>();

		
			if (StringUtil.isEmpty(clazz.getName())) {
				ret.put("type", "error");
				ret.put("msg", "班级名称不能为空");
				return ret;
			}
			if (clazz.getGradeId()==null) {
				ret.put("type", "error");
				ret.put("msg", "请选择所属年级");
				return ret;
			}
			
			if(clazzService.add(clazz)<=0) {
				ret.put("type", "error");
				ret.put("msg", "班级添加失败");
				return ret;
			}
			
			ret.put("type", "success");
			ret.put("msg", "班级添加成功");
			return ret;
		}
		
		
		//修改
		@RequestMapping(value = "/edit", method = RequestMethod.POST)
		@ResponseBody
		public Map<String, String> edit(Clazz clazz) {
			Map<String, String> ret = new HashMap<String, String>();
			if (StringUtil.isEmpty(clazz.getName())) {
				ret.put("type", "error");
				ret.put("msg", "班级不能为空");
				return ret;
			}
			if(clazzService.edit(clazz)<=0) {
				ret.put("type", "error");
				ret.put("msg", "修改失败！");
				return ret;
			}
			
			ret.put("type", "success");
			ret.put("msg", "修改成功");
			return ret;
		}
	
		
		//删除班级
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
				if(clazzService.delete(idString)<=0) {
					ret.put("type", "error");
					ret.put("msg", "删除失败！");
					return ret;
				}
			} catch (Exception e) {
				ret.put("type", "error");
				ret.put("msg", "改班级下存在学生信息，请勿冲动！");
				e.printStackTrace();
			}
			ret.put("type", "success");
			ret.put("msg", "删除成功！");
			return ret;
		}
		
	
}
