<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
// 세션값 가져오기
String id = (String) session.getAttribute("id");
// 세션값 있으면  ..님 반가워요~  [로그아웃]으로 바뀜. [회원가입]은 없어짐.
// 세션값 없으면  [로그인]  [회원가입]
%>
<header>
	<div id="login">
		<%
		if (id != null) {
			%>
			<%=id %>님 반가워요~
			<a href="/member/logout">로그아웃</a>
			<%
		} else { // id == null
			%>
			<a href="/member/login">로그인</a>
			 | <a href="/member/join">회원가입</a>
			<%
		}
		%>

	</div>
	<div class="clear"></div>
	<div id="logo">
		<a href="/">
		<img src="/images/logo.gif" width="265" height="62" alt="Fun Web">
		</a>
	</div>
	<nav id="top_menu">
		<ul>
			<li><a href="/">HOME</a></li>
			<li><a href="/company/welcome">COMPANY</a></li>
			<li><a href="/chat/simpleChat">CHAT</a></li>
			<li><a href="/notice/list">CUSTOMER CENTER</a></li>
			<li><a href="#">CONTACT US</a></li>
		</ul>
	</nav>
</header>



