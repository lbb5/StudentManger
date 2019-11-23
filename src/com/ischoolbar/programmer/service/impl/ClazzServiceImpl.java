package com.ischoolbar.programmer.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ischoolbar.programmer.dao.ClazzDao;
import com.ischoolbar.programmer.entity.Clazz;
import com.ischoolbar.programmer.service.ClazzService;

@Service
public class ClazzServiceImpl implements ClazzService {
	@Autowired
	private ClazzDao clazzDao;
	@Override
	public int add(Clazz clazz) {
		return clazzDao.add(clazz);
	}

	@Override
	public int edit(Clazz clazz) {
		return clazzDao.edit(clazz);
	}

	@Override
	public int delete(String ids) {
		return clazzDao.delete(ids);
	}

	@Override
	public List<Clazz> findList(Map<String, Object> queryMap) {
		return clazzDao.findList(queryMap);
	}

	@Override
	public List<Clazz> findAll() {
		return clazzDao.findAll();
	}

	@Override
	public int getTotal(Map<String, Object> queryMap) {
		return clazzDao.getTotal(queryMap);
	}

}
