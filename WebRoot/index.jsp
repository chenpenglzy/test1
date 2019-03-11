<%@page contentType="text/html;charset=utf-8" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

    <base href="<%=basePath%>">
    
    <title></title>
	 <script type="text/javascript">
		function test(action){  
	    window.location = action;  
	}  
</script>  
  </head>
  
  <body>
    
    <input type="button" value = "出库任务生成出库单" onclick="test('stbGen.action')"/> <br/><br/>  
    <input type="button" value = "出库单到结束拣货" onclick="test('stbFinishPicking.action')"/> <br/><br/>  
    <input type="button" value = "出库任务生成出库单（不封箱）" onclick="test('stbGenNoPacking.action')"/> <br/><br/>  
    <input type="button" value = "出库单录入中到已确认（不封箱）" onclick="test('stbFinishConfirm.action')"/>  <br/><br/>
    <%--
    <a href="stbGen.action"> <span>出库任务生成出库单</span> </a> <br/><br/>  
    <a href="stbFinishPicking.action"> <span>出库单到结束拣货</span> </a> <br/><br/>  
    <a href="stbGenNoPacking.action"> <span>出库任务生成出库单（不封箱）</span> </a> <br/><br/>  
    <a href="stbFinishConfirm.action"> <span>出库单到已确认（不封箱）</span> </a> <br/><br/>  
   --%>
  </body>
</html>
