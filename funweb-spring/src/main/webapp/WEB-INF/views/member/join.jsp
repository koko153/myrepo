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
		
	<h1>회원 가입</h1>
	<form id="join" action="/member/join" method="post" name="frm">
	<fieldset>
		<legend>Basic Info</legend>
		
		<label>User ID</label>
		<input type="text" name="id" class="id" required> 
		<input type="button" value="ID 중복확인" class="dup" id="btnDupChk">
		<span id="msgIdDup"></span><br>
		
		<label>Password</label> 
		<input type="password" name="passwd" class="pass pass1" required><br>
		
		<label>Retype Password</label> 
		<input type="password" class="pass pass2" required>
		<span id="msgPass"></span>
		<br>
		
		<label>Name</label> 
		<input type="text" name="name" class="nick" required><br>
		
		<label>E-Mail</label> 
		<input type="email" name="email" class="email" required><br>
		
		<label>Retype E-mail</label> 
		<input type="email" class="email" required><br>
	</fieldset>
	
	<fieldset>
		<legend>Optional</legend>
		
		<label>Address</label> 
		<input type="text" name="address" class="address"><br>
		
		<label>Phone Number</label> 
		<input type="tel" name="tel" class="phone"><br>
		
		<label>Age</label> 
		<input type="number" name="age" min="0" max="200" class="mobile"><br>
		
		<label>Gender</label> 
		<input type="radio" name="gender" value="남"> 남성
		<input type="radio" name="gender" value="여"> 여성
		<br>
	</fieldset>

	<div class="clear"></div>
	<div id="buttons">
		<input type="submit" value="회원가입" class="submit"> 
		<input type="reset" value="초기화" class="cancel">
	</div>
	</form> 
	
	</article>
	
	<div class="clear"></div>
	<%-- footer 영역 --%>
	<jsp:include page="/WEB-INF/views/include/bottomFooter.jsp" />
</div>

<script src="/script/jquery-3.5.1.js"></script>
<script>
	$('input[name="id"]').keyup(function () {
		let id = $(this).val();
	
		if (id.length <= 2) { // 아이디 두글자 까지는 중복체크 안함
			return;
		}
	
		// 아이디 세글자 부터는 Ajax로 아이디 중복체크하기
		$.ajax({
			url: '/member/ajax/joinIdDupChk',
			data: { id: id },
			//method: 'GET',
			success: function (response) {
				console.log(typeof response);
				console.log(response);
	
				if (response.isIdDup) {
					$('span#msgIdDup').html('이미 사용중인 아이디 입니다.').css('color', 'red');
				} else {
					$('span#msgIdDup').html('사용 가능한 아이디 입니다.').css('color', 'green');
				}
			}
		});
	});


	$('#btnDupChk').click(function () {
		
		let id = $('input[name="id"]').val();
		
		// id가 공백이면 '아이디 입력하세요' 포커스주기
		if (id == '') { // id.length == 0
			alert('아이디를 입력하세요');
			$('input[name="id"]').focus();
			return;
		}

		// id중복체크 창열기  joinIdDupCheck.jsp
		window.open('/member/joinIdDupCheck?id=' + id, 'idDupCheck', 'width=500,height=400');
	});

	// .pass2 요소에 포커스가 해제되면
	$('.pass2').focusout(function () {
		let pass1 = $('.pass1').val();
		let pass2 = $(this).val();

		if (pass1 == pass2) {
			$('#msgPass').html('패스워드 일치함').css('color', 'green');
		} else {
			$('#msgPass').html('패스워드 불일치').css('color', 'red');
		}
	});

</script>
</body>
</html>   

    