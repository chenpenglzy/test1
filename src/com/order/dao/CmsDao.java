package com.order.dao;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(
		propagation = Propagation.REQUIRED,
		rollbackFor={Exception.class}
)
public interface CmsDao {
	
	public List queryforList(String sql);
	
	public int querforInt(String sql);
	
	public void updateSql(String sql);
}
