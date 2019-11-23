package com.ischoolbar.programmer.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.util.StringUtil;
import com.ischoolbar.programmer.entity.Clazz;
import com.ischoolbar.programmer.entity.Grade;
import com.ischoolbar.programmer.entity.Student;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.service.ClazzService;
import com.ischoolbar.programmer.service.GradeService;
import com.ischoolbar.programmer.service.StudentService;

import net.sf.json.JSONArray;

/*
 * 班级管理控制器
 * */
@Controller
@RequestMapping("/student")
public class StudentController {
	@Autowired
	private StudentService studentService;
	@Autowired
	private ClazzService clazzService;

	/*
	 * 班级列表页
	 */
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		model.setViewName("student/student_list");
		List<Clazz> clazzList = clazzService.findAll();
		model.addObject("clazzList", clazzList);
		model.addObject("clazzListJson", JSONArray.fromObject(clazzList));
		return model;
	}

	// 展示页面
	@RequestMapping(value = "/get_list", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getList(@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "clazzId", required = false) Long clazzId, Page page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		if (clazzId != null) {
			queryMap.put("clazzId", clazzId);
		}
		queryMap.put("username", "%" + name + "%");
		queryMap.put("offset", page.getOffset());
		queryMap.put("pageSize", page.getRows());
		ret.put("rows", studentService.findList(queryMap));
		ret.put("total", studentService.getTotal(queryMap));

		return ret;
	}

	// 添加
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(Student student) {

		Map<String, String> ret = new HashMap<String, String>();

		if (StringUtil.isEmpty(student.getUsername())) {
			ret.put("type", "error");
			ret.put("msg", "学生姓名不能为空");
			return ret;
		}
		if (StringUtil.isEmpty(student.getPassword())) {
			ret.put("type", "error");
			ret.put("msg", "登录密码不能为空");
			return ret;
		}

		if (student.getClazzId() == null) {
			ret.put("type", "error");
			ret.put("msg", "请选择所属班级");
			return ret;
		}
		if(isExist(student.getUsername(), null)) {
			ret.put("type", "error");
			ret.put("msg", "该姓名已存在");
			return ret;
		}
		student.setSn("S" + new Date().getTime());

		if (studentService.add(student) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "学生添加失败");
			return ret;
		}

		ret.put("type", "success");
		ret.put("msg", "学生添加成功");
		return ret;
	}

	// 修改
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(Clazz clazz) {
		Map<String, String> ret = new HashMap<String, String>();
		if (StringUtil.isEmpty(clazz.getName())) {
			ret.put("type", "error");
			ret.put("msg", "班级不能为空");
			return ret;
		}
		if (clazzService.edit(clazz) <= 0) {
			ret.put("type", "error");
			ret.put("msg", "修改失败！");
			return ret;
		}

		ret.put("type", "success");
		ret.put("msg", "修改成功");
		return ret;
	}

	// 删除班级
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(@RequestParam(value = "ids[]", required = true) Long[] ids) {
		Map<String, String> ret = new HashMap<String, String>();
		if (ids == null) {
			ret.put("type", "error");
			ret.put("msg", "请选择要删除的数据！");
			return ret;
		}

		String idString = "";
		for (Long id : ids) {
			idString += id + ",";
		}
		idString = idString.substring(0, idString.length() - 1);
		try {
			if (clazzService.delete(idString) <= 0) {
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

	// 上传头像
	@RequestMapping(value = "/upload_photo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> uploadPhoto(MultipartFile photo, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, String> ret = new HashMap<String, String>();
		response.setCharacterEncoding("UTF-8");
		if (photo == null) {
			ret.put("type", "error");
			ret.put("msg", "请选择文件！");
			return ret;
		}
		if (photo.getSize() > 10485760) {
			ret.put("type", "error");
			ret.put("msg", "文件大小超过10M");
			return ret;
		}
		// 图片的后缀
		String suffix = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".") + 1,
				photo.getOriginalFilename().length());
		if (!"jpg,png,,gif,jpeg".contains(suffix.toLowerCase())) {
			ret.put("type", "error");
			ret.put("msg", "请上传格式为jpg,png,,gif,jpeg的图片");
			return ret;
		}

		String savapath = request.getServletContext().getRealPath("/") + "\\upload\\";
		File savapathFile = new File(savapath);
		if (!savapathFile.exists()) {
			savapathFile.mkdir();
		}
		String filename = new Date().getTime() + "." + suffix;
		photo.transferTo(new File(savapath + filename));

		ret.put("type", "success");
		ret.put("msg", "图片上传成功！");
		ret.put("src", request.getServletContext().getContextPath() + "/upload/" + filename);
		return ret;
	}
	
	private boolean isExist(String username,Long id) {
		Student student = studentService.findByUserName(username);
			if(student!=null) {
				if(id==null) {
					return true;
				}
				if(student.getId().longValue()!=id.longValue()) {
					return true;
				}
			}
		return false;
	}
}
