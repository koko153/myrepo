<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%-- head 컨텐트 영역 --%>
<jsp:include page="/WEB-INF/views/include/headContent.jsp" />

<link href="/css/subpage.css" rel="stylesheet" type="text/css"  media="all">
</head>

<body>
<div id="wrap">
	<%-- header 영역 --%>
	<jsp:include page="/WEB-INF/views/include/topHeader.jsp" />
	
	<div class="clear"></div>
	<div id="sub_img_member"></div>
	
	<div class="clear"></div>
	<nav id="sub_menu">  
        <ul>
            <li> <a href="#">Join us</a></li>
            <li> <a href="#">Privacy policy</a></li>
    	</ul>
	</nav>
	
	<article>
		<h1>로그인</h1>
		<form action="/member/login" method="post" id="join">
			<fieldset>
				<legend>Login Info</legend>
				
				<label>User ID</label>
				<input type="text" name="id"><br>
				
				<label>Password</label>
				<input type="password" name="passwd"><br>
				
				<label>로그인 상태 유지</label>
				<input type="checkbox" name="keepLogin" value="true">
				체크하면 10분 동안 로그인 상태가 유지됩니다.
				<br>
			</fieldset>
			
			<div class="clear"></div>
			<div id="buttons">
				<input type="submit" value="로그인" class="submit">
				<input type="reset" value="초기화" class="cancel">
			</div>
		</form>
	</article>
	
	<div class="clear"></div>
	<%-- footer 영역 --%>
	<jsp:include page="/WEB-INF/views/include/bottomFooter.jsp" />
</div>

</body>
</html>   

    