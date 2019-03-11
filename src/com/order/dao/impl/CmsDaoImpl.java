package com.order.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.order.dao.CmsDao;

public class CmsDaoImpl implements CmsDao {

	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List queryforList(String sql) {
		return this.jdbcTemplate.queryForList(sql);
	}

	public int querforInt(String sql) {
		return this.jdbcTemplate.queryForInt(sql);
	}

	public void updateSql(String sql) {
		 this.jdbcTemplate.update(sql);
		
	}
	

}
