<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	padding: 20px 10px;
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
		<h1>간단한 채팅 서비스</h1>
		<hr>
		<div v-if="showNickname">
			<input type="text" v-model="nickname" placeholder="닉네임을 입력해주세요" required autofocus>
			<input type="button" value="채팅방 참여하기" v-on:click="enter"><br><br>
		</div>
		<div v-if="showChatting">
			<div id="chatbox" v-html="chatboxContent"></div>
			<input type="text" v-model="message" v-on:keyup.enter="send" placeholder="채팅글을 입력하세요" autofocus>
			<input type="button" value="전송" v-on:click="send">
			<input type="button" value="채팅방 연결끊기" v-on:click="disconnect">
		</div>
	</article>
    
	<div class="clear"></div>
	<%-- footer 영역 --%>
	<jsp:include page="/WEB-INF/views/include/bottomFooter.jsp" />
</div>

<script src="https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js"></script>
<script>
	var webSocket;

	var app = new Vue({
		el: '#app',
		data: {
			nickname: '',
			message: '',
			chatboxContent: '',
			showNickname: true,
			showChatting: false
		},
		methods: {
			enter: function () {
				this.connect();
				this.addWinEvt();
			},
			connect: function () {
				webSocket = new WebSocket('ws://localhost:8082/simpleChat');
				webSocket.onopen = this.onOpen;
				webSocket.onmessage = this.onMessage; // 소켓서버로부터 데이터를 받을때 호출됨
				webSocket.onclose = this.onClose;
			},
			onOpen: function () {
				this.showNickname = false;
				this.showChatting = true;
				
				webSocket.send(this.nickname + '님이 입장하셨습니다.');
			},
			onMessage: function (event) {
				let data = event.data;
				this.chatboxContent += '<br>' + data;
				this.scrollDown();
			},
			onClose: function () {
				this.chatboxContent += '<br>' + '채팅방 연결을 끊었습니다.';
				this.scrollDown();
			},
			disconnect: function () {
				webSocket.send(this.nickname + '님이 퇴장하셨습니다.');
				webSocket.close();
			},
			send: function () {
				if (this.message == '') {
					return;
				}
				webSocket.send(this.nickname + ' : ' + this.message);
				this.message = '';
			},
			scrollDown: function () {
				let chatbox = document.getElementById('chatbox');
				chatbox.scrollTop = chatbox.scrollHeight;
			},
			addWinEvt: function() {
				window.addEventListener('beforeunload', function (event) {
					let dialogText = 'Dialog text here';
					// Chrome requires returnValue to be set
					event.returnValue = dialogText;
					return dialogText;
				});

				let vm = this;
				window.addEventListener('unload', function () {
					vm.disconnect();
				});
			}
		}
	});
</script>
</body>
</html>   





    