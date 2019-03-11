package com.order.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.OrderedMap;
import org.springframework.jdbc.core.JdbcTemplate;

import com.order.dao.CmsDao;
import com.order.dao.TestDao;

public class TestDaoImpl implements TestDao {
	private JdbcTemplate jdbcTemplate;
	private CmsDao cmsDao;
	
	public CmsDao getCmsDao() {
		return cmsDao;
	}

	public void setCmsDao(CmsDao cmsDao) {
		this.cmsDao = cmsDao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int updateSql(String sql){
		return this.jdbcTemplate.update(sql);
	}

	public int querforInt(String sql) {
		return this.jdbcTemplate.queryForInt(sql);
	}

	public List queryforList(String sql) {
		return this.jdbcTemplate.queryForList(sql);
	}

	public int queryforInt(String sql) {
		return this.jdbcTemplate.queryForInt(sql);
	}
	
	public Map queryForMap(String sql){
		return this.jdbcTemplate.queryForMap(sql);
	}
	
	//出库任务生成出库单
	public String stbGen(){
		Date date = new Date();
		String sql = "";
		String pscNum = "00187379";//测试购销合同编号
		String stbNum = findNumDtl("STB_NUM",1);//库存单编号
		Map pscMap = this.queryForMap("SELECT * FROM PSC T WHERE T.PSC_NUM = '"+pscNum+"'");
		int apiId = 21;//操作用户API ID
		int vendeeId = ObjectToInteger(pscMap.get("VENDEE_ID"));//采购商ID
		int vdeWarehId = ObjectToInteger(pscMap.get("VDE_WAREH_ID"));//采购商仓库ID
		int vdrWarehId = ObjectToInteger(pscMap.get("VDR_WAREH_ID"));//发货仓库ID
		String remarks = ObjectToString(pscMap.get("REMARKS"));//备注
		String delivAddr = ObjectToString(pscMap.get("DELIV_ADDR"));//送货地址
		Map ttlMkvMap = this.queryForMap("SELECT SUM(T.QTY * T3.RT_UNIT_PRICE) TTL_MKV FROM PSC_DTL T "+
						" JOIN PRODUCT T2 ON T.PROD_ID = T2.PROD_ID JOIN UNIT_PROD_CLS T3 ON T2.PROD_CLS_ID = T3.PROD_CLS_ID"+
						" WHERE T.PSC_NUM = '"+pscNum+"'"); //总市值需要重新算，订货平台下单的销售合同总市值为0,结束拣货会重新算总市值
		
		//①出库任务操作：生成
		sql = "INSERT INTO GDN " +
				  "(CNSN_REQD, RCK_REQD, END_WAREH_ID, PICK_REQD, DELIV_PSTD, VEH_REQD, "+
				  "RCV_FSCL_UNIT_ID, GDN_NUM, PROGRESS, DELIV_MODE, UNIT_ID, DELIV_MTHD, DELIV_ADDR, "+
				  "FSCL_DELIV_MODE, RCV_UNIT_ID, END_UNIT_ID, RCV_WAREH_ID) VALUES " +
				  "('F' , 'F' , NULL , 'F' , NULL , "+"'F' , " +
				   "NULL,'"+stbNum+"' , 'PG' , 'SELL' , 1 , NULL , '"+delivAddr+"' , " +
				   "'SELL' , "+vendeeId+" , NULL , "+vdeWarehId+")";
		this.updateSql(sql);
		
		
		
		
		sql = "INSERT INTO STB " +
				"(CNTR_NUM, TTL_EXPD_QTY, BOX_SCHD, TTL_PACK, INST_STL, COST_CHG, IS_REV,"+ 
				 "SUSPENDED, BOX_REQD, POST_CTRL, FSCL_UNIT_ID, FSCL_DATE, TTL_RWD, TTL_MKV, LOC_ADOPTED, DR_TYPE, "+
				 "FSCL_DATE_APTD, STB_NUM, SRC_DOC_TYPE, TTL_EXPD_BOX, DOC_DATE, LDG_STB_NUM, SRC_DOC_NUM, REMARKS, "+
				 "OPR_ID, TTL_TAX, WAREH_ID, TTL_BOX, BXI_ENABLED, TRAN_UNIT_ID, LGC_WAREH_ID, LOC_ID, CLN_AREA_ID, "+
				 "TTL_VAL, TTL_COST, UNIT_ID, COST_GRP_ID, TTL_QTY, CANCELLED, OP_TIME, REVERSED, SRC_DOC_UNIT_ID, "+
				 "BRDG_MODE, EFFECTIVE) VALUES "+
				 "(NULL,0,'F',NULL,'F','F','F'," +
				 "'F','T','U',NULL,NULL,NULL,0,'F','D'," +
				 "'F','"+stbNum+"','SLC',0,"+formatDate(date)+",NULL,'"+pscNum+"','"+remarks+"',"+
				 apiId+",0,"+vdrWarehId+",0,'F',NULL,NULL,NULL,NULL," +
				 "0,NULL,1,NULL,0,'F',"+formatTime(date)+",'F',1," +
				 "'D','F') ";
		this.updateSql(sql);
		
		sql = "UPDATE PSC SET PROGRESS = 'DG' , TASKS_IN_DELIV = 1  " +
				"WHERE PSC_NUM = '"+pscNum+"'  AND PROGRESS = 'CK'  AND CANCELLED = 'F'";
		this.updateSql(sql);
		
		
		sql = "UPDATE STB SET TTL_EXPD_QTY ="+pscMap.get("TTL_QTY")+" , TTL_QTY = "+pscMap.get("TTL_QTY")+" ," +
				"TTL_VAL = "+pscMap.get("TTL_VAL")+" , TTL_TAX = "+pscMap.get("TTL_TAX")+" , " +
				"TTL_MKV = "+ttlMkvMap.get("TTL_MKV")+
		" WHERE STB_NUM = '"+stbNum+"'  AND UNIT_ID = 1";
		this.updateSql(sql);
		

		
		sql = "INSERT INTO STB_DTL" +
				"(TAX, UNIT_PRICE, REMARKS, UNIT_COST, LINE_NUM, UNIT_ID, APP_PRICE, " +
				"MK_UNIT_PRICE, STB_NUM, VAL, EXPD_QTY, ROW_NUM, PROD_ID, MKV, DISC_RATE, QTY, COST, RWD," +
				"TAX_RATE, FNL_PRICE)" +
				"(SELECT T.TAX, T.UNIT_PRICE, T.REMARKS, NULL, T.LINE_NUM, 1, NULL,"+
				"T3.RT_UNIT_PRICE, '"+stbNum+"', T.VAL, T.QTY, T.ROW_NUM, T.PROD_ID, T.QTY * T3.RT_UNIT_PRICE, T.DISC_RATE, T.QTY, NULL, NULL,  "+
				"T.TAX_RATE, T.FNL_PRICE " +
					"FROM PSC_DTL T JOIN PRODUCT T2 ON T.PROD_ID = T2.PROD_ID " +
					"JOIN UNIT_PROD_CLS T3 ON T2.PROD_CLS_ID = T3.PROD_CLS_ID " +
					"WHERE T.PSC_NUM = '"+pscNum+"')";
		
		
		this.updateSql(sql);
		
		sql =  "DELETE FROM WAREH_DELIV_TASK WHERE UNIT_ID = 1  AND TASK_DOC_TYPE = 'SLC'  " +
				" AND TASK_DOC_UNIT_ID = 1  AND TASK_DOC_NUM = '"+pscNum+"'";
		
		
		 
		this.updateSql(sql);
		return null;
	}

	public String stbFinishPicking() {
		Date date = new Date();
		String sql = "";
		String pscNum = "00187379";//测试购销合同编号
		String stbNum = "00240757";//测试库存单编号
		Map pscMap = this.queryForMap("SELECT * FROM PSC T WHERE T.PSC_NUM = '"+pscNum+"'");
		int apiId = 21;//操作用户API ID
		int vdrWarehId = ObjectToInteger(pscMap.get("VDR_WAREH_ID"));//发货仓库ID
				 
        //②出库单操作：确认
		sql = "DELETE FROM STB_DTL WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  AND QTY = 0";
		this.updateSql(sql);
				
		sql = "UPDATE STB SET TTL_BOX = 0 , TTL_EXPD_BOX = 0  WHERE STB_NUM = '"+stbNum+"'  AND UNIT_ID = 1";
		this.updateSql(sql);
		
		sql = "UPDATE STB SET TTL_QTY = "+pscMap.get("TTL_QTY")+" , TTL_EXPD_QTY = "+pscMap.get("TTL_QTY")+"  WHERE STB_NUM = '"+stbNum+"'  AND UNIT_ID = 1";
		this.updateSql(sql);
		
		sql = "UPDATE GDN SET PROGRESS = 'CN'  WHERE UNIT_ID = 1  AND GDN_NUM = '"+stbNum+"'  AND PROGRESS = 'PG'";
		this.updateSql(sql);
		
		sql = "DELETE FROM STB_BOX WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  AND BOX = 0 AND NOT EXISTS"+ 
				"(SELECT 1 FROM (STB_DTL A INNER JOIN BOX_DTL B ON (A.PROD_ID = B.PROD_ID)) WHERE A.UNIT_ID = "+
				"STB_BOX.UNIT_ID AND A.STB_NUM = STB_BOX.STB_NUM AND B.BOX_CODE = STB_BOX.BOX_CODE)";
		this.updateSql(sql);
		
		//③出库单操作：开始拣货
		sql = "UPDATE GDN SET PROGRESS = 'FI' , FTCH_ST_TIME = "+formatTime(date)+" WHERE UNIT_ID = 1  AND "+
				" GDN_NUM = '"+stbNum+"'  AND PROGRESS = 'CN'  AND EXISTS(SELECT 1 FROM STB WHERE UNIT_ID = GDN.UNIT_ID  "+
				" AND STB_NUM = GDN.GDN_NUM AND SUSPENDED = 'F' AND CANCELLED = 'F' " +
				" AND (GDN.PICK_REQD = 'T' OR GDN.RCK_REQD = 'T' OR (BOX_REQD = 'T' AND BOX_SCHD = 'F' OR CLN_AREA_ID IS NOT NULL)))";
		this.updateSql(sql);
		
		//备注:结束拣货操作中的部分代码
		sql = "UPDATE STB_DTL SET QTY = 0, VAL = 0, TAX = 0, MKV = 0 WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'";
		this.updateSql(sql);
		
		//④出库扫描、出库装箱操作：保存、封箱
		  //4.1循环装箱单
		  //测试boxList
		sql = " SELECT T2.TTL_QTY,T2.BOX_CODE FROM BXN T1 JOIN BOX T2 ON T1.BOX_CODE = T2.BOX_CODE  WHERE T1.UNIT_ID = 1 AND T1.REL_DOC_NUM = '00240738'";
		List boxList = this.queryforList(sql);//测试内容
		int ttlBox = boxList.size(); 
		for(int i = 0;i < boxList.size(); i++){
			String bxnNum = findNumDtl("BXN_NUM",0);
			String boxCode = findNumDtl("BOX_CODE",0);
			OrderedMap boxMap = (OrderedMap) boxList.get(i); 
			int boxTtlQty = ObjectToInteger(boxMap.get("TTL_QTY")); 
			
			sql = "INSERT INTO BXN " +
					"(REMARKS, PROGRESS, REL_DOC_TYPE, UNIT_ID, LOC_ADOPTED, REL_DOC_NUM, OPR_ID,"+ 
					"PCK_TIME, PCKR_ID, BOX_CODE, REL_DOC_UNIT_ID, CANCELLED, WAREH_ID, OP_TIME, " +
					"BXN_NUM, BOX_KIND,LOC_ID, DOC_DATE, BOX_RSN, EFFECTIVE) VALUES "+
					"(NULL ,'PK', 'GDN' , 1 , 'F' , '"+stbNum+"' , "+apiId+" , "+
					" "+formatTime(date)+", 1 , '"+boxCode+"', 1, 'F' , "+vdrWarehId+" , "+formatTime(date)+" ," +
					"'"+bxnNum+"' , 'BB' , 0 , "+formatDate(date)+" ,'D' , 'T')" ;
			this.updateSql(sql);
			
			sql = "INSERT INTO BOX " +
						"(REMARKS, PROGRESS, UID_ENABLED, SAT_ID, OPR_ID, BOX_CODE, TTL_QTY, BXI_ENABLED,"+ 
						"EGN_STR, BOX_STD_ID, PROD_ID, PROD_CLS_ID, COLOR_ID, EDITION, EFFECTIVE," +
						"SRC_DOC_TYPE,SRC_DOC_UNIT_ID,SRC_DOC_NUM) VALUES " +
						"(NULL , 'PK' , 'F' , NULL , NULL , '"+boxCode+"' , "+boxTtlQty+" , 'F' " +
						", NULL , NULL , NULL , NULL, NULL , NULL , 'T'," +
						"'BXN',1,'"+bxnNum+"')";
			this.updateSql(sql);
			
			String boxCodeSearch = boxMap.get("BOX_CODE").toString(); //测试内容
			sql = "SELECT PROD_ID,QTY FROM BOX_DTL T WHERE T.BOX_CODE = '"+ boxCodeSearch+"'";//测试内容
			List boxDtlList = (this.queryforList(sql));//测试内容
			//4.2循环装箱单明细
			for(int j = 0;j < boxDtlList.size(); j++){
				OrderedMap boxDtlMap = (OrderedMap) boxDtlList.get(j);
				int prodId = ObjectToInteger(boxDtlMap.get("PROD_ID")); 
				int prodQty = ObjectToInteger(boxDtlMap.get("QTY")); 
				sql = "INSERT INTO BOX_DTL " +
						"(ROW_NUM, PROD_ID, LINE_NUM, QTY, BOX_CODE) VALUES " +
						"("+(j+1)+" , "+prodId+" , "+(j+1)+" ,"+prodQty+"  , '"+boxCode+"' )";
				this.updateSql(sql);
				
				//备注:结束拣货操作中的部分代码
				sql = "UPDATE STB_DTL SET QTY = QTY + "+prodQty+" , VAL = VAL + ROUND(("+prodQty+"  * FNL_PRICE), 2), " +
						"TAX = TAX + ROUND(("+prodQty+"  * FNL_PRICE * TAX_RATE / (1 + TAX_RATE)), 2), " +
						"MKV = MKV + ROUND(("+prodQty+"  * MK_UNIT_PRICE), 2) " +
						"WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  AND PROD_ID = "+prodId;
				this.updateSql(sql);
			}
			
			//4.3封箱
			sql = "DELETE FROM BOX_DTL WHERE BOX_CODE = '"+boxCode+"'  AND QTY = 0";
			this.updateSql(sql);
			
			//备注:结束拣货操作中的部分代码
			sql = "INSERT INTO STB_BOX " +
					"(UNIT_ID, STB_NUM, BOX_CODE, LINE_NUM, EXPD_BOX, BOX) VALUES " +
					"(1 , '"+stbNum+"', '"+boxCode+"' , "+(i+1)+" , 0, 1 )";
			this.updateSql(sql);
			
		}
		
		//⑤出库单操作：结束拣货
		sql = "UPDATE GDN SET PROGRESS = 'FD' , FTCHR_ID = "+apiId+" , FTCH_FIN_TIME = "+formatTime(date)+
			  " WHERE UNIT_ID = 1  AND GDN_NUM = '"+stbNum+"'  AND PROGRESS = 'FI'  " +
			  "AND EXISTS(SELECT 1 FROM STB WHERE UNIT_ID = GDN.UNIT_ID AND STB_NUM = GDN.GDN_NUM AND SUSPENDED = 'F' AND CANCELLED = 'F')";
		this.updateSql(sql);
		
		
		//sql = "UPDATE STB_DTL SET QTY = :1 , VAL = ROUND((:2  * FNL_PRICE), 2), TAX = ROUND((:3  * FNL_PRICE * TAX_RATE / (1 + TAX_RATE)), 2), " +
		//		"MKV = ROUND((:4  * MK_UNIT_PRICE), 2), COST = ROUND((:5  *UNIT_COST), 2) " +
		//		"WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  AND PROD_ID = 97809";
		//sql = "MERGE INTO STB_DTL T USING " +
		//	  "(SELECT T.REL_DOC_NUM AS STB_NUM,T.UNIT_ID,T3.PROD_ID,SUM(T3.QTY) QTY FROM BXN T JOIN BOX T2 ON T.BOX_CODE = T2.BOX_CODE JOIN BOX_DTL T3 ON T2.BOX_CODE = T3.BOX_CODE " +
		//	  "WHERE T.UNIT_ID = 1 AND T.REL_DOC_NUM = '"+stbNum+"' GROUP BY T3.PROD_ID,T.UNIT_ID,T.REL_DOC_NUM) B " +
		//	   	"ON (T.PROD_ID = B.PROD_ID AND T.UNIT_ID = B.UNIT_ID AND T.STB_NUM = B.STB_NUM) " +
		//	  "WHEN MATCHED THEN UPDATE " +
		//	  	"SET T.QTY = B.QTY, VAL = ROUND((B.QTY  * T.FNL_PRICE), 2), " +
		//	  	"T.TAX = ROUND((B.QTY  * T.FNL_PRICE * TAX_RATE / (1 + T.TAX_RATE)), 2), " +
		//	  	"T.MKV = ROUND((B.QTY  * T.MK_UNIT_PRICE), 2) ";
		//this.updateSql(sql);
		
		//sql = "UPDATE STB SET TTL_QTY = :1 , TTL_VAL = :2 , TTL_TAX = :3 ,TTL_MKV = :4 , TTL_COST = :5 "+ "" +
		//		"WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'";
		sql = "MERGE INTO STB T USING "+
			  "(SELECT UNIT_ID,STB_NUM,SUM(QTY) AS TTL_QTY, SUM(VAL) AS TTL_VAL, SUM(TAX) AS TTL_TAX, SUM(MKV) AS TTL_MKV "+
			   "FROM STB_DTL WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  GROUP BY UNIT_ID,STB_NUM) T2 "+
			   "ON (T.UNIT_ID = T2.UNIT_ID AND T.STB_NUM = T2.STB_NUM) "+
			   "WHEN MATCHED THEN UPDATE SET " +
			   "T.TTL_QTY = T2.TTL_QTY,T.TTL_VAL = T2.TTL_VAL,T.TTL_TAX = T2.TTL_TAX, "+
			   "T.TTL_MKV = T2.TTL_MKV ";
		this.updateSql(sql);
		
		sql = "UPDATE STB SET TTL_BOX = "+ttlBox+"  WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'";
		this.updateSql(sql);
		return null;
	}
	
	
	    //出库任务生成出库单(不需要装箱)
		public String stbGenNoPacking(){
			Date date = new Date();
			String sql = "";
			String pscNum = "00187383";//测试购销合同编号
			String stbNum = findNumDtl("STB_NUM",1);//库存单编号
			Map pscMap = this.queryForMap("SELECT * FROM PSC T WHERE T.PSC_NUM = '"+pscNum+"'");
			int apiId = 21;//操作用户API ID
			int vendeeId = ObjectToInteger(pscMap.get("VENDEE_ID"));//采购商ID
			int vdeWarehId = ObjectToInteger(pscMap.get("VDE_WAREH_ID"));//采购商仓库ID
			int vdrWarehId = ObjectToInteger(pscMap.get("VDR_WAREH_ID"));//发货仓库ID
			String remarks = ObjectToString(pscMap.get("REMARKS"));//备注
			String delivAddr = ObjectToString(pscMap.get("DELIV_ADDR"));//送货地址
			Map ttlMkvMap = this.queryForMap("SELECT SUM(T.QTY * T3.RT_UNIT_PRICE) TTL_MKV FROM PSC_DTL T "+
							" JOIN PRODUCT T2 ON T.PROD_ID = T2.PROD_ID JOIN UNIT_PROD_CLS T3 ON T2.PROD_CLS_ID = T3.PROD_CLS_ID"+
							" WHERE T.PSC_NUM = '"+pscNum+"'"); //总市值需要重新算，订货平台下单的销售合同总市值为0,已确认会重新算总市值
			
			//①出库任务操作：生成
			sql = "INSERT INTO GDN " +
					  "(CNSN_REQD, RCK_REQD, END_WAREH_ID, PICK_REQD, DELIV_PSTD, VEH_REQD, "+
					  "RCV_FSCL_UNIT_ID, GDN_NUM, PROGRESS, DELIV_MODE, UNIT_ID, DELIV_MTHD, DELIV_ADDR, "+
					  "FSCL_DELIV_MODE, RCV_UNIT_ID, END_UNIT_ID, RCV_WAREH_ID) VALUES " +
					  "('F' , 'F' , NULL , 'F' , NULL , "+"'F' , " +
					   "NULL,'"+stbNum+"' , 'PG' , 'SELL' , 1 , NULL , '"+delivAddr+"' , " +
					   "'SELL' , "+vendeeId+" , NULL , "+vdeWarehId+")";
			this.updateSql(sql);
			
			
			sql = "INSERT INTO STB " +
					"(CNTR_NUM, TTL_EXPD_QTY, BOX_SCHD, TTL_PACK, INST_STL, COST_CHG, IS_REV,"+ 
					 "SUSPENDED, BOX_REQD, POST_CTRL, FSCL_UNIT_ID, FSCL_DATE, TTL_RWD, TTL_MKV, LOC_ADOPTED, DR_TYPE, "+
					 "FSCL_DATE_APTD, STB_NUM, SRC_DOC_TYPE, TTL_EXPD_BOX, DOC_DATE, LDG_STB_NUM, SRC_DOC_NUM, REMARKS, "+
					 "OPR_ID, TTL_TAX, WAREH_ID, TTL_BOX, BXI_ENABLED, TRAN_UNIT_ID, LGC_WAREH_ID, LOC_ID, CLN_AREA_ID, "+
					 "TTL_VAL, TTL_COST, UNIT_ID, COST_GRP_ID, TTL_QTY, CANCELLED, OP_TIME, REVERSED, SRC_DOC_UNIT_ID, "+
					 "BRDG_MODE, EFFECTIVE) VALUES "+
					 "(NULL,0,'F',NULL,'F','F','F'," +
					 "'F','F','U',NULL,NULL,NULL,0,'F','D'," +
					 "'F','"+stbNum+"','SLC',0,"+formatDate(date)+",NULL,'"+pscNum+"','"+remarks+"',"+
					 apiId+",0,"+vdrWarehId+",0,'F',NULL,NULL,NULL,NULL," +
					 "0,NULL,1,NULL,0,'F',"+formatTime(date)+",'F',1," +
					 "'D','F') ";
			this.updateSql(sql);
			
			sql = "UPDATE PSC SET PROGRESS = 'DG' , TASKS_IN_DELIV = 1  " +
					"WHERE PSC_NUM = '"+pscNum+"'  AND PROGRESS = 'CK'  AND CANCELLED = 'F'";
			this.updateSql(sql);
			
			
			sql = "UPDATE STB SET TTL_EXPD_QTY ="+pscMap.get("TTL_QTY")+" , TTL_QTY = "+pscMap.get("TTL_QTY")+" ," +
					"TTL_VAL = "+pscMap.get("TTL_VAL")+" , TTL_TAX = "+pscMap.get("TTL_TAX")+" , " +
					"TTL_MKV = "+ttlMkvMap.get("TTL_MKV")+
			" WHERE STB_NUM = '"+stbNum+"'  AND UNIT_ID = 1";
			this.updateSql(sql);
			
			sql = "INSERT INTO STB_DTL" +
					"(TAX, UNIT_PRICE, REMARKS, UNIT_COST, LINE_NUM, UNIT_ID, APP_PRICE, " +
					"MK_UNIT_PRICE, STB_NUM, VAL, EXPD_QTY, ROW_NUM, PROD_ID, MKV, DISC_RATE, QTY, COST, RWD," +
					"TAX_RATE, FNL_PRICE)" +
					"(SELECT T.TAX, T.UNIT_PRICE, T.REMARKS, NULL, T.LINE_NUM, 1, NULL,"+
					"T3.RT_UNIT_PRICE, '"+stbNum+"', T.VAL, T.QTY, T.ROW_NUM, T.PROD_ID, T.QTY * T3.RT_UNIT_PRICE, T.DISC_RATE, T.QTY, NULL, NULL,  "+
					"T.TAX_RATE, T.FNL_PRICE " +
						"FROM PSC_DTL T JOIN PRODUCT T2 ON T.PROD_ID = T2.PROD_ID " +
						"JOIN UNIT_PROD_CLS T3 ON T2.PROD_CLS_ID = T3.PROD_CLS_ID " +
						"WHERE T.PSC_NUM = '"+pscNum+"')";
			this.updateSql(sql);
			
			sql =  "DELETE FROM WAREH_DELIV_TASK WHERE UNIT_ID = 1  AND TASK_DOC_TYPE = 'SLC'  " +
					" AND TASK_DOC_UNIT_ID = 1  AND TASK_DOC_NUM = '"+pscNum+"'";
			
			this.updateSql(sql);
			return null;
		}
		
    //出库单录入中到已确认(不需要装箱)	
	public String stbFinishConfirm(){
		Date date = new Date();
		String sql = "";
		String pscNum = "00187383";//测试购销合同编号
		String stbNum = "00240769";//测试库存单编号
		sql = "SELECT PROD_ID,(QTY-3) AS QTY FROM PSC_DTL T WHERE T.PSC_NUM = '"+ pscNum+"'";//测试内容
		List dtlList = (this.queryforList(sql));//测试内容
		for(int i=0;i<dtlList.size();i++){
			OrderedMap dtlMap = (OrderedMap) dtlList.get(i);
			int prodId = ObjectToInteger(dtlMap.get("PROD_ID")); 
			int prodQty = ObjectToInteger(dtlMap.get("QTY")); 
			
			sql = "UPDATE STB_DTL SET QTY = "+prodQty+" , VAL = ROUND(("+prodQty+"  * FNL_PRICE), 2), " +
					" TAX = ROUND(("+prodQty+"  * FNL_PRICE * TAX_RATE / (1 + TAX_RATE)), 2), " +
					" MKV = ROUND(("+prodQty+"  * MK_UNIT_PRICE), 2), " +
					" LINE_NUM = "+ (i+1) + ",ROW_NUM ="+(i+1)+
					" WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  AND PROD_ID = "+prodId;
			this.updateSql(sql);
		}
		
		sql = "DELETE FROM STB_DTL WHERE UNIT_ID = 1 AND STB_NUM = '"+stbNum+"'  AND QTY = 0";
		this.updateSql(sql);
		
		sql = "UPDATE GDN SET PROGRESS = 'CN'  WHERE UNIT_ID = 1  AND GDN_NUM = '"+stbNum+"'  AND PROGRESS = 'PG'";
		this.updateSql(sql);
		
		
		sql = "MERGE INTO STB T USING "+
				  "(SELECT UNIT_ID,STB_NUM,SUM(QTY) AS TTL_QTY, SUM(VAL) AS TTL_VAL, SUM(TAX) AS TTL_TAX, SUM(MKV) AS TTL_MKV "+
				   "FROM STB_DTL WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  GROUP BY UNIT_ID,STB_NUM) T2 "+
				   "ON (T.UNIT_ID = T2.UNIT_ID AND T.STB_NUM = T2.STB_NUM) "+
				   "WHEN MATCHED THEN UPDATE SET " +
				   "T.TTL_QTY = T2.TTL_QTY,T.TTL_VAL = T2.TTL_VAL,T.TTL_TAX = T2.TTL_TAX, "+
				   "T.TTL_MKV = T2.TTL_MKV ";
		this.updateSql(sql);
		
		sql = "UPDATE STB SET TTL_BOX = 0,TTL_EXPD_BOX = 0,LOC_ID = 0,OP_TIME = "+formatTime(date)+" WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'";
		this.updateSql(sql);
		
		sql = "DELETE FROM STB_BXI WHERE UNIT_ID = 1  AND STB_NUM = '"+stbNum+"'  AND BOX = 0 " +
				"AND NOT EXISTS (SELECT 1 FROM ((" +
					"STB_DTL A INNER JOIN PRODUCT B ON (A.PROD_ID = B.PROD_ID)) " +
					"INNER JOIN EGN_TBL_DTL C ON (B.SPEC_ID = C.SPEC_ID)) " +
						"WHERE A.UNIT_ID = STB_BXI.UNIT_ID AND A.STB_NUM = STB_BXI.STB_NUM " +
						"AND C.EGN_STR = STB_BXI.EGN_STR AND B.PROD_CLS_ID = STB_BXI.PROD_CLS_ID " +
						"AND B.COLOR_ID = STB_BXI.COLOR_ID AND B.EDITION = STB_BXI.EDITION)";
		this.updateSql(sql);
		
		return null;
	}
	
	
	//获取最新的编号
	public String findNumDtl(String numType,int unitId){
		this.updateSql("UPDATE SYS_REF_NUM_DTL SET LAST_NUM = LAST_NUM + 1  WHERE REF_NUM_ID = '"+numType+"'  AND UNIT_ID = "+unitId);
        int lastNum = this.querforInt("SELECT LAST_NUM FROM SYS_REF_NUM_DTL WHERE REF_NUM_ID = '"+numType+"'  AND UNIT_ID = "+unitId);
        String currentNum = String.valueOf(lastNum);	
        int strLen = currentNum.length();
        StringBuffer sb = null;
         while (strLen < 8) {
               sb = new StringBuffer();
               sb.append("0").append(currentNum);
               currentNum = sb.toString();
               strLen = currentNum.length();
         }
		return currentNum;
	}
	
	public String formatDate(Date date)
	{
		return "TO_DATE('"+new SimpleDateFormat("YYYY/MM/DD").format(date)+"','YYYY/MM/DD')";
	}
	
	public String formatTime(Date date){
		return "TO_DATE('"+new SimpleDateFormat("YYYY/MM/dd HH:mm:ss").format(date)+"','YYYY/MM/DD HH24:MI:SS')";
	}
	
	public int ObjectToInteger(Object obj){
		return Integer.parseInt(obj.toString());
	}
	
	public String ObjectToString(Object obj){
		return obj == null ? "" : obj.toString();
	}
	
	
}
