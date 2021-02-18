<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<%-- head 컨텐트 영역 --%>
<jsp:include page="/WEB-INF/views/include/headContent.jsp" />

<link href="/css/subpage.css" rel="stylesheet" type="text/css"  media="all">

<style type="text/css">
div#chatbox {
	width: 400px;
	height: 300px;
	border: 1px solid black;
	background-color: lightgray;
	overflow: auto;
}
</style>
</head>
<body>
<div id="wrap">
	<%-- header 영역 --%>
	<jsp:include page="/WEB-INF/views/include/topHeader.jsp" />
  
	<div class="clear"></div>
	<div id="sub_img"></div>
  
	<div class="clear"></div>
	<%-- nav 영역 --%>
	<jsp:include page="/WEB-INF/views/include/submenuChat.jsp" />
	
	<article id="app">
		<h1>새로운 채팅방 만들기</h1>
		<hr>
		
		<form action="/chat/new" method="post">
			<input type="text" name="title" placeholder="채팅방 제목을 입력해주세요" required autofocus>
			<br>
			<input type="submit" value="채팅방 생성하기">
			<input type="button" value="목록으로 돌아가기" onclick="/chat/list">
		</form>
	</article>
    
	<div class="clear"></div>
	<%-- footer 영역 --%>
	<jsp:include page="/WEB-INF/views/include/bottomFooter.jsp" />
</div>

<script src="https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js"></script>
<script>

</script>
</body>
</html>   





    