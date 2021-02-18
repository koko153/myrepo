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
		<h1>채팅방 목록</h1>
		<hr>
		
		<table border="1">
			<thead>
				<tr>
					<th>방번호 아이디</th>
					<th>방 제목</th>
					<th>입장 버튼</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="room" items="${ roomList }">
				<tr>
					<td>${ room.roomId }</td>
					<td>${ room.title }</td>
					<td>
						<input type="button" value="채팅방 입장하기" onclick="location.href = '/chat/room/${ room.roomId }'">
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		
		<a href="/chat/new">새로운 채팅방 만들기</a>
		
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





    