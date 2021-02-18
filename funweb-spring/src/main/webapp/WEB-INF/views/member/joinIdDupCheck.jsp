<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<%
// request 영역객체에 저장한 데이터 가져오기
//String id = (String) request.getAttribute("id");
int count = (int) request.getAttribute("count");
%>
<%--
* JSP의 EL언어로 영역객체의 데이터에 접근하는 표현이 가능함
 ${  }로 표현함
 
Scope 객체 탐색 순서 (↑)
application -> applicationScope
              ↑
session -> sessionScope
              ↑
request -> requestScope
              ↑
page -> pageScope
	
 --%>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h2>ID 중복확인</h2>
<hr>

<%--
  count == 1  아이디있음  "사용중인 ID 입니다."
  count == 0  아이디없음  "사용가능한 ID 입니다."
 --%>
<c:choose>
	<c:when test="${ requestScope.count eq 1 }">
		<p>아이디 중복, 이미 사용중인 ID 입니다.</p>
	</c:when>
	<c:otherwise>
		<p>사용가능한 ID 입니다.</p>
		<input type="button" value="ID 사용" id="btnUseId">
	</c:otherwise>
</c:choose>


<form action="/member/joinIdDupCheck" method="get" name="frm">
	<input type="text" name="id" value="${ requestScope.id }">
	<input type="submit" value="ID 중복확인">
</form>

<script src="/script/jquery-3.5.1.js"></script>
<script>
	$('#btnUseId').click(function () {
		// 검색한 ID 값 -> 창을 열게해준 부모페이지인 join.jsp의 id 입력상자에 넣기
		window.opener.document.frm.id.value = frm.id.value;
		window.close(); // 현재 창 닫기
	});
</script>
</body>
</html>



