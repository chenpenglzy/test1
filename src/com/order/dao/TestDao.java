package com.order.dao;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(
		propagation = Propagation.REQUIRED,
		rollbackFor={Exception.class}
)
public interface TestDao {
	
	
	public int updateSql(String sql);
	
	public int queryforInt(String sql);
	
	public List queryforList(String sql);
	
	public Map queryForMap(String sql);
	
	public String stbFinishPicking();
	
	public String stbGen();
	
	public String stbGenNoPacking();
	
	public String stbFinishConfirm();

}
