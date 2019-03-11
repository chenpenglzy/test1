package com.order.action;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class test2 {

	public static void main(String[] args) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date_start = sdf.parse("2018/12/1 11:00:00");
        Date date_end = sdf.parse("2018/12/2 13:30:00");
        //pscHours(date_start,date_end);
        
        int[] numbers = {44,55,32,56,89,22};
        int temp = 0;
        int size = numbers.length;
        for(int i = 0; i < size - 1; i++){
        	for(int j = 0 ; j < size -1 - i; j++){
        		if(numbers[j] > numbers[j+1]){
        			temp = numbers[j];
        			numbers[j] = numbers[j+1];
        			numbers[j+1] = temp;
        		}
        	}
        }
       System.out.println(numbers);
        
	}
	
	
	
	
	public static float pscHours(Date date_start,Date date_end) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar calendar_start = Calendar.getInstance();
        Calendar calendar_end = Calendar.getInstance();
        Calendar calendar_start2 = Calendar.getInstance();
        Calendar calendar_end2 = Calendar.getInstance();
        calendar_start.setTime(date_start);
        calendar_end.setTime(date_end);
        int day_start = calendar_start.get(Calendar.DAY_OF_YEAR);
        int day_end = calendar_end.get(Calendar.DAY_OF_YEAR);
        float differHours = differHours(calendar_end,calendar_start);
        int differDays = day_end - day_start;
        float result = 0;
       
        //隔1天及以上订单 
        if(differDays >= 1){
        	//1.第一天所用小时数
        		//1.1 18:00之前,结束时间当天18:00
        	calendar_end2.setTime(sdf.parse(dateFormatYmd(date_start) + " 18:00:00"));
        	if(differHours(calendar_end2,calendar_start) < 0 ){
        		//1.2 18:00之后,结束时间当天24:00
        		calendar_end2.setTime(sdf.parse(dateFormatYmd(date_start) + " 24:00:00"));
        	}
        	differHours = differHours(calendar_end2,calendar_start);
        		//1.3 开始时间在12:00之前减中午休息一小时
        	calendar_end2.setTime(sdf.parse(dateFormatYmd(date_start) + " 12:00:00"));
        	if(differHours(calendar_end2,calendar_start) > 0){
    			differHours -= 1;
        	}
        	//2.最后一天所用小时数
        	calendar_start2.setTime(sdf.parse(dateFormatYmd(date_end) + " 8:30:00"));
        		//2.1结束时间在8:30之后
        	if(differHours(calendar_end,calendar_end2) > 0 ){
        		differHours += differHours(calendar_end,calendar_start2);
        	}
        		//2.2 结束时间在13:00之后减中午休息一小时
        	calendar_start2.setTime(sdf.parse(dateFormatYmd(date_end) + " 13:00:00"));
        	if(differHours(calendar_end,calendar_start2) > 0){
    			differHours -= 1;
        	}
        	//3.中间整天所用小时数
        	if(differDays > 1){
        		for(int i = 0;i < differDays - 1;i++){
            		//2.2计算中间日期小时数
            		differHours += 8.5;//1个工作日8.5小时
            	}
        	}
        	System.out.println("隔1天及以上订单相差小时："+floatFromatFour(differHours));
        //当天订单
        }else{ 
        	
        	calendar_end2.setTime(sdf.parse(dateFormatYmd(date_start) + " 13:00:00"));
        	if(differHours(calendar_end2,calendar_start)  > 0 && differHours(calendar_end,calendar_end2) > 0){
        		differHours -= 1;
        	}
        	System.out.println("当天订单相差小时："+floatFromatFour(differHours));
        }
        result = floatFromatFour(differHours);
        return result;
	}
	
	
	public static String dateFormatYmd(Date date){
		return new SimpleDateFormat("yyyy/MM/dd").format(date);
	}
	
	public static float differHours(Calendar end,Calendar start){
		
		return ((float)(end.getTimeInMillis() - start.getTimeInMillis()) / (1000 * 3600));
	}
	
	public static float floatFromatFour(float f){
		DecimalFormat df = new DecimalFormat("#0.0000");
		return Float.parseFloat(df.format(f));
		
	}
	
}
