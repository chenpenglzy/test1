package com.order.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.order.dao.TestDao;

public class Test {

	/*private TestDao testDao;

	protected static final Log log = LogFactory.getLog(Test.class);

	public TestDao getTestDao() {
		return testDao;
	}

	public void setTestDao(TestDao testDao) {
		this.testDao = testDao;
	}

	// 出库单到结束拣货
	public void stbFinishPicking() {

		try {
			log.info("出库单结束拣货开始......");
			log.info(testDao.stbFinishPicking());
			log.info("出库单结束拣货结束......");
		} catch (Exception e) {
			log.info("出库任务到结束拣货失败:" + e.getMessage());
		}

	}

	// 出库任务生成出库单
	public void stbGen() {
		try {
			log.info("出库任务生成出库单开始......");
			log.info(testDao.stbGen());
			log.info("出库任务生成出库单结束......");
		} catch (Exception e) {
			log.info("出库任务生成出库单推送失败:" + e.getMessage());
		}
	}

	// 出库任务生成出库单(不需要装箱)
	public void stbGenNoPacking() {
		try {
			log.info("出库任务生成出库单开始(不需要装箱)......");
			log.info(testDao.stbGenNoPacking());
			log.info("出库任务生成出库单结束(不需要装箱)......");
		} catch (Exception e) {
			log.info("出库任务生成出库单失败(不需要装箱):" + e.getMessage());
		}
	}

	// 出库单录入中到已确认(不需要装箱)
	public void stbFinishConfirm() {

		try {
			log.info("出库单到已确认开始......");
			log.info(testDao.stbFinishConfirm());
			log.info("出库单到已确认结束......");
		} catch (Exception e) {
			log.info("出库单到已确认结束失败:" + e.getMessage());
		}

	}*/

	public String load(String url, String query) throws Exception {
		URL restURL = new URL(url);
		/*
		 * 此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类
		 * 的子类HttpURLConnection
		 */
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
		// 请求方式
		conn.setRequestMethod("POST");
		// 设置是否从httpUrlConnection读入，默认情况下是true;
		// httpUrlConnection.setDoInput(true);
		conn.setDoOutput(true);
		// allowUserInteraction 如果为 true，则在允许用户交互（例如弹出一个验证对话框）的上下文中对此 URL 进行检查。
		conn.setAllowUserInteraction(false);

		PrintStream ps = new PrintStream(conn.getOutputStream());
		ps.print(query);

		ps.close();

		BufferedReader bReader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));

		String line, resultStr = "";

		while (null != (line = bReader.readLine())) {
			resultStr += line;
		}
		JSONObject jsStr = JSONObject.fromObject(resultStr);
		JSONArray jsArr = (JSONArray) jsStr.get("data");
        for(int i=0;i<jsArr.size();i++){
        	JSONObject job = jsArr.getJSONObject(i);
        	System.out.println(job.get("opentime").toString().substring(0, 10)+"="+job.get("opencode"));
        }
		bReader.close();
		
		

		return resultStr;

	}

	public static void main(String[] args) {
		/*try {
			Test restUtil = new Test();
	
	        String resultString = restUtil.load(
	                "http://f.apiplus.net/ssq-20.json", "floor=first&year=2017&month=9&isLeader=N");
	
	        } catch (Exception e) {
	
	        // TODO: handle exception
	
	        System.out.print(e.getMessage());
	
	        }*/
		
			// TODO Auto-generated method stub
	 
		Integer a = 129;
		 Integer b = 129;
		 if(a.intValue() == b.intValue()){
		   System.out.print("a等于b");  
		 }else{
		   System.out.print("a不等于b");  
		 }
		 
		 String user = null;
			Integer user_id= Integer.parseInt(user.toString());
			 System.out.print(user_id);  
		}
	

	

}
