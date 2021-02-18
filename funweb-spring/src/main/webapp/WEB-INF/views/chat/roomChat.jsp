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
	padding: 25px 15px;
	border: 1px solid black;
	background-color: lightgray;
	overflow: auto;
}

div#chatbox span.me {
	background-color: yellow;
	border-radius: 10px;
	padding: 10px;
}
div#chatbox span.others {
	background-color: white;
	border-radius: 10px;
	padding: 10px;
}

div#chatbox div {
	margin-bottom: 25px;
}

div#chatbox div.me {
	text-align: right;
}
div#chatbox div.others {
	text-align: left;
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
		<h1>채팅방 서비스</h1>
		<h2>방제목 : ${ room.title }</h2>
		<hr>
		<div v-if="showNickname">
			<input type="text" v-model="nickname" placeholder="닉네임을 입력해주세요" required autofocus>
			<input type="button" value="채팅방 참여하기" v-on:click="enter"><br>
			방번호 UUID : <input type="text" value="${ room.roomId }" readonly><br>
			<br>
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
	const roomId = '${ room.roomId }';
	var mySessionId;
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
				webSocket = new WebSocket('ws://localhost:8082/chat');
				webSocket.onopen = this.onOpen;
				webSocket.onmessage = this.onMessage; // 소켓서버로부터 데이터를 받을때 호출됨
				webSocket.onclose = this.onClose;
			},
			onOpen: function () {
				this.showNickname = false;
				this.showChatting = true;
				
				let obj = {
						type: 'ENTER',
						roomId: roomId,
						writer: this.nickname
				};
				let str = JSON.stringify(obj);
				webSocket.send(str);
			},
			onMessage: function (event) {
				let data = event.data; // json 문자열을 받음
				let obj = JSON.parse(data);
				let str = '';
				
				if (obj.type == 'SESSION_ID') {
					mySessionId = obj.sessionId;
				} else if (obj.type == 'ENTER') {
					str = `<div>
					           <span>\${obj.writer}님이 입장하셨습니다.</span>
						   </div>`;
				} else if (obj.type == 'LEAVE') {
					str = `<div>
					           <span>\${obj.writer}님이 퇴장하셨습니다.</span>
					       </div>`;
				} else { // obj.type == 'CHAT'
					if (obj.sessionId == mySessionId) {
						str = '<div class="me"><span class="me">';
					} else {
						str = '<div class="others"><span class="others">';
					}
					str += `\${obj.writer} : \${obj.message}</span></div>`;
				}
				this.chatboxContent += str;
				this.scrollDown();
			},
			onClose: function () {
				this.chatboxContent += '<div>채팅방 연결을 끊었습니다.</div>';
				this.scrollDown();
			},
			disconnect: function () {
				let obj = {
						type: 'LEAVE',
						roomId: roomId,
						writer: this.nickname
				};
				let str = JSON.stringify(obj);
				webSocket.send(str);
				webSocket.close();
			},
			send: function () {
				if (this.message == '') {
					return;
				}

				let obj = {
						type: 'CHAT',
						//sendWho: 'ME',
						roomId: roomId,
						sessionId: mySessionId,
						writer: this.nickname,
						message: this.message
				};
				let str = JSON.stringify(obj);
				webSocket.send(str);
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





    