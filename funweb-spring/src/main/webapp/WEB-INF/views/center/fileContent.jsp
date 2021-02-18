<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<%-- head 컨텐트 영역 --%>
<jsp:include page="/WEB-INF/views/include/headContent.jsp" />

<link href="/css/subpage.css" rel="stylesheet" type="text/css"
	media="all">
<link href="/css/bootstrap.css" rel="stylesheet" type="text/css">
<link href="/css/bootstrap-theme.css" rel="stylesheet" type="text/css">
<link href="/css/custom2.css" rel="stylesheet" type="text/css">
<style>
li.media {
	background-color: lightgray;
}

span.reply-toggle {
	cursor: pointer;
}

span.reply-toggle:hover {
	text-decoration: underline;
}

/* 나타나고 있을때와 사라지고 있을때 0.5초*/
.v-enter-active, .v-leave-active {
	transition: 0.5s;
}
/* 나타나기 전의 상태과 사라진 후의 상태는 투명도 0, 오른쪽으로 50 이동*/
.v-enter, .v-leave-to {
	opacity: 0;
	transform: translateX(50px)
}
</style>
</head>
<body>
	<div id="wrap">
		<%-- header 영역 --%>
		<jsp:include page="/WEB-INF/views/include/topHeader.jsp" />

		<div class="clear"></div>
		<div id="sub_img_center"></div>

		<div class="clear"></div>
		<%-- nav 영역 --%>
		<jsp:include page="/WEB-INF/views/include/submenuBoard.jsp" />

		<article>

			<h1>자료실 게시판 상세보기</h1>

			<table id="notice">
				<tr>
					<th scope="col" class="tno">글번호</th>
					<td class="left" width="500">${ noticeVo.num }</td>
				</tr>
				<tr>
					<th scope="col" class="tread">조회수</th>
					<td class="left">${ noticeVo.readcount }</td>
				</tr>
				<tr>
					<th scope="col" class="twrite">작성자</th>
					<td class="left">${ noticeVo.id }</td>
				</tr>
				<tr>
					<th scope="col" class="tdate">작성일자</th>
					<td class="left"><fmt:formatDate value="${ noticeVo.regDate }"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</tr>
				<tr>
					<th scope="col" class="ttitle">글제목</th>
					<td class="left">${ noticeVo.subject }</td>
				</tr>
				<tr>
					<th scope="col" class="ttitle">글내용</th>
					<td class="left">${ noticeVo.content }</td>
				</tr>
				<tr>
					<th scope="col" class="ttitle">첨부파일</th>
					<td class="left">
						<c:if test="${ not empty attachList }">

							<c:forEach var="attach" items="${ attachList }">

								<c:choose>
									<c:when test="${ attach.image eq 'I' }">
										<p>
											<a href="/upload/${ attach.uploadpath }/${ attach.uuid }_${ attach.filename }">
												<img src="/upload/${ attach.uploadpath }/s_${ attach.uuid }_${ attach.filename }">
											</a>
										</p>
									</c:when>
									<c:otherwise>
										<p>
											<a href="/fileNotice/download?num=${ attach.num }">
												${ attach.filename } 
											</a>
										</p>
									</c:otherwise>
								</c:choose>

							</c:forEach>
						</c:if></td>
				</tr>
			</table>

			<div id="table_search">

				<c:if test="${ not empty id }">
					<%-- 로그인 했을때 --%>
					<c:if test="${ id eq noticeVo.id }">
						<%-- 로그인 아이디와 글작성자 아이디가 같을때 --%>
						<input type="button" value="글수정" class="btn" onclick="location.href = '/fileNotice/modify?num=${ noticeVo.num }&pageNum=${ pageNum }'">
						<input type="button" value="글삭제" class="btn" onclick="remove()">
					</c:if>
					<input type="button" value="답글쓰기" class="btn" onclick="location.href = '/fileNotice/replyWrite?reRef=${ noticeVo.reRef }&reLev=${ noticeVo.reLev }&reSeq=${ noticeVo.reSeq }&pageNum=${ pageNum }'">
				</c:if>

				<input type="button" value="목록보기" class="btn"
					onclick="location.href = '/fileNotice/list?pageNum=${ pageNum }'">
			</div>

			<div class="clear"></div>
			<div id="page_control"></div>

			<!-- 댓글 영역 -->
			<div id="app" class="panel panel-default">

				<div class="panel-heading">
					<div class="row">
						<div class="col-md-6">
							<button type="button" v-on:click="showListArea = !showListArea"
								class="btn btn-default">
								<span class="glyphicon glyphicon-comment"></span> 댓글 <span
									style="color: red;">{{ totalCount }}</span>
							</button>
							<button type="button" class="btn btn-default" v-on:click="getList">
								<span class="glyphicon glyphicon-refresh"></span> 새로고침
							</button>
						</div>
						<div class="col-md-6">
							<label for="commentCount" class="col-md-offset-3 col-md-5 control-label text-right">댓글 갯수: </label>
							<div class="col-md-4">
								<select id="commentCount" class="form-control pull-right" v-model="numOfRows" v-on:change="loadListWithFirstPage">
									<option v-for="num in [5, 10, 15, 20]">{{ num }}</option>
								</select>
							</div>
						</div>
					</div>
				
				


				</div>

				<template v-if="showListArea">
					<div class="panel-body">
						<!-- 댓글 리스트 영역 -->
						<ul class="media-list">

							<transition-group>
							<li class="media" v-for="(comment, index) in commentList"
								v-bind:key="comment.cno"
								v-bind:style="{ marginLeft: comment.reLev * 50 + 'px' }"><span
								class="pull-left" v-if="comment.reLev > 0">└</span> <a
								class="pull-left" href="#"> <img
									src="/images/center/pic.jpg">
							</a>

								<div class="media-body">
									<div class="media-heading">
										<strong class="text-primary">{{ comment.id }}</strong>
										<div v-if="comment.id == loginId" class="dropdown pull-right"
											style="display: inline-block;">
											<a data-toggle="dropdown" href="#">수정/삭제</a>
											<ul class="dropdown-menu">
												<li><a href="#"
													v-on:click="showModifyComment(index, $event)">수정</a></li>
												<li><a href="#"
													v-on:click="removeComment(index, $event)">삭제</a></li>
											</ul>
										</div>
									</div>
									<p>{{ comment.content }}</p>
									<div>
										<small class="text-muted">{{ getDate(comment.updateDate) }}</small>
										<span class="reply-toggle"
											v-on:click="toggleReplyWriteArea(index)">
											{{ comment.writeOk ? '답글접기' : '답글쓰기' }}
										</span>
									</div>
								</div> <!-- 답댓글 작성 영역 -->
								<div v-if="comment.writeOk" style="margin-left: 50px;">
									<textarea v-model="inputReplyComment" rows="5"
										class="form-control" placeholder="댓글을 입력하세요."></textarea>
									<br>

									<button type="button" class="btn btn-primary"
										v-bind:data-type="type" v-bind:data-reref="comment.reRef"
										v-bind:data-relev="comment.reLev"
										v-bind:data-reseq="comment.reSeq"
										v-on:click="addReplyOrUpdateComment(index, $event)">등록</button>

									<small class="text-muted" style="margin-left: 20px;">{{
										remain }} / 600</small>
								</div></li>
							</transition-group>
						</ul>
						
						<!-- 페이지 블록 영역 -->
						<ul v-if="totalCount > 0" class="pagination pull-right">
							<li v-bind:class="{ 'disabled': !isPrevOk }">
								<a href="#" v-on:click="setPageNo(prevBlockPage)">« 이전</a>
							</li>
							
							<li v-for="item in pageBlockList" v-bind:key="item.pageNum" v-bind:class="{ 'active': item.isCurrentPage }">
								<a href="#" v-on:click="setPageNo(item.pageNum)">{{ item.pageNum }}</a>
							</li>
							
							<li v-bind:class="{ 'disabled': !isNextOk }">
								<a href="#" v-on:click="setPageNo(nextBlockPage)">다음 »</a>
							</li>
						</ul>
					</div>

					<!-- 주댓글 작성 영역 -->
					<div class="panel-footer">
						<textarea rows="5" v-model="inputComment" class="form-control"
							placeholder="댓글을 입력하세요."></textarea>
						<br>
						<button type="button" v-on:click="addComment"
							class="btn btn-primary">등록</button>
						<small class="text-muted" style="margin-left: 20px;">{{ remain }} / 600</small>
					</div>
				</template>
			</div>

		</article>


		<div class="clear"></div>
		<%-- footer 영역 --%>
		<jsp:include page="/WEB-INF/views/include/bottomFooter.jsp" />
	</div>


	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/locale/ko.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js"></script>
	<script src="/script/jquery-3.5.1.js"></script>
	<script src="/script/bootstrap.js"></script>
	<script>
		// 게시글 삭제 함수
		function remove() {
			let isDelete = confirm('${ noticeVo.num }번 글을 정말 삭제하시겠습니까?');
			if (isDelete) {
				location.href = '/fileNotice/delete?num=${ noticeVo.num }&pageNum=${ pageNum }';
			}
		}
	</script>
	<script>
		// 게시글 번호
		const nno = ${ noticeVo.num };
	
		new Vue({
			el: '#app',
			data: {
				loginId: '${ sessionScope.id }', // 톰캣서버 세션의 로그인 아이디
				pageNo: 1,  // 사용자 요청 페이지 번호
				showListArea: true,  // 댓글리스트 영역 보이기 여부 스위치
				totalCount: 0,    // 전체 댓글 갯수
				commentList: [],  // 댓글 리스트 데이터
				inputComment: '', // 주댓글 입력내용
				inputReplyComment: '', // 답댓글 또는 댓글수정 입력내용
				type: '',  // 댓글수정(update) 또는 답댓글 추가(insert) 인지 구분

				// 페이지블록 관련 데이터 속성
				numOfRows: 10, // 한 페이지당 보여지는 댓글 갯수
				pageCount: 0,  // 댓글의 총 페이지 갯수
				pageBlock: 5,  // 페이지블록 내의 페이지 갯수
				startPage: 0,  // 페이지블록 내의 시작페이지
				endPage: 0,    // 페이지블록 내의 끝페이지
				pageBlockList: [] // 페이지블록 리스트 (시작페이지부터 끝페이지까지의 페이지번호가 요소)
			},
			// 산출속성은 반드시 값을 리턴해야 함!
			computed: {
				
				remain: function () { // 댓글입력 남은 글자수
					return 600 - this.inputComment.length;
				},
				
				isPrevOk: function () { // 이전 페이지블록 존재 여부
					return this.startPage > this.pageBlock;
				},
				
				isNextOk: function () { // 다음 페이지블록 존재여부
					return this.endPage < this.pageCount;
				},

				prevBlockPage: function () {
					return this.startPage - this.pageBlock;
				},

				nextBlockPage: function () {
					return this.startPage + this.pageBlock;
				}
			},
			watch: {
				inputComment: function () {
					if (this.inputComment.length > 600) {
						this.inputComment = this.inputComment.substring(0, 600);
					}
				}
			},
			methods: {
				toggleReplyWriteArea: function (index) {
					// 답댓글 입력내용 비우기
					this.inputReplyComment = '';

					for (let i=0; i<this.commentList.length; i++) {
						let comment = this.commentList[i];

						// 클릭한 위치는 토글처리
						if (i == index) {
							if (comment.writeOk) {
								comment.writeOk = false;
							} else {
								comment.writeOk = true;
								this.type = 'insert'; // 등록 버튼의 용도를 답댓글 추가로 표시 변경
							}
						} else { // 클릭한 위치가 아니면 모두 댓글쓰기영역 접기
							comment.writeOk = false;
						}
					} // for
				},
				
				getDate: function (str) {
					console.log(typeof str);
					console.log(str);

					let today = new Date(); // 현재시점 날짜시간
					let date = new Date(str); // new Date('2021-01-06T02:44:32.000+00:00');

					let gap = today.getTime() - date.getTime(); // 밀리초 차이값
					let oneDay = 1000 * 60 * 60 * 24; // 밀리초 하루

					let result;
					if (gap > oneDay) { // 년월일
						result = moment(str).format('YYYY-MM-DD');
					} else { // gap <= oneDay   시분초
						result = moment(str).format('a hh:mm:ss');
					}
					return result;
				},

				setPageNo: function (pageNum) {
					if (pageNum < 1 || pageNum > this.pageCount) {
						return;
					}
					this.pageNo = pageNum;
					this.getList();
				},
				
				// 댓글목록 가져오기
				getList: function () {
					// jsp의 el언어로 게시판 글번호를 출력해서 자바스크립트 변수에 저장
					let vm = this; // ViewModel의 약칭. Vue객체 자기자신을 vm 변수로 저장
					
					$.ajax({
						url: '/comment/pages/' + nno + '/' + this.pageNo + '/' + this.numOfRows,
						method: 'GET',
// 						dataType: 'json',
						success: function (response) {
							console.log(typeof response);
							console.log(response);
								
							for (let comment of response.commentList) {
								comment.writeOk = false; // 오브젝트마다 writeOk 필드 추가
								console.log(comment);
							} // for

							// 서버에서 응답받은 데이터를 리액티브 데이터에 저장
							// 리액티브 데이터가 변경되면 즉시 바인딩된 화면도 렌더링됨
							vm.commentList = response.commentList;

							// 전체 댓글 갯수
							vm.totalCount = response.totalCount;

							// 페이지블록 만들기 (전체댓글갯수를 기준으로 페이지블록 생성)
							vm.makePageBlock();
						},
						error: function () {
							alert('댓글 리스트 가져오기 오류 발생...')
						}
					});
				}, // getList

				// 페이지블록 만들기
				makePageBlock: function () {
					// 댓글의 총 페이지 갯수 구하기
					this.pageCount = Math.ceil( this.totalCount / this.numOfRows );

					// ((1-1) / 5) * 5 + 1  -> 1
					// ((2-1) / 5) * 5 + 1  -> 1
					// ((3-1) / 5) * 5 + 1  -> 1
					// ((4-1) / 5) * 5 + 1  -> 1
					// ((5-1) / 5) * 5 + 1  -> 1
					
					// ((6-1)) / 5 * 5 + 1  -> 6
					// ((7-1)) / 5 * 5 + 1  -> 6
					// ((8-1)) / 5 * 5 + 1  -> 6
					// ((9-1)) / 5 * 5 + 1  -> 6
					// ((10-1)) / 5 * 5 + 1 -> 6
					
					// 페이지블록 내의 시작페이지 구하기
					this.startPage = Math.floor((this.pageNo - 1) / this.pageBlock) * this.pageBlock + 1;

					// 페이지블록 내의 끝페이지 구하기
					this.endPage = this.startPage + this.pageBlock - 1;
					if (this.endPage > this.pageCount) {
						this.endPage = this.pageCount;
					}

					// 페이지번호 배열리스트 비우기
					this.pageBlockList.splice(0);

					for (let i=this.startPage; i<=this.endPage; i++) {
						let obj = {
								pageNum: i,
								isCurrentPage: (this.pageNo == i) ? true : false 
						};
						
						this.pageBlockList.push(obj);
					} // for
					
				},

				// 한페이지당 보여줄 댓글갯수 변경시 새로 댓글목록 가져오기
				loadListWithFirstPage: function () {
					this.pageNo = 1;
					this.getList();
				},
				
				// 주댓글 등록하기
				addComment: function () {
					let obj = {
							nno: nno,
							content: this.inputComment
					};
					console.log(obj);

					let str = JSON.stringify(obj); // 자바스크립트 객체를 JSON 문자열로 변환
					console.log(str);


					let vm = this;
					
					$.ajax({
						url: '/comment/new',
						data: str,
						method: 'POST',
						contentType: 'application/json; charset=UTF-8',
						success: function (response) {
							console.log(response);

							// 현재 페이지 다시 불러오기
							vm.getList();
							
							// 다른방식 구현
// 							let comment = response.comment;
// 							comment.writeOk = false; // writeOk 필드 추가
// 							vm.commentList.push(comment); // 리스트 맨 뒤에 댓글 추가
// 							vm.inputComment = '';  // 입력상자 비우기
// 							vm.totalCount++;  // 댓글갯수 증가
						},
						error: function (req, status, err) {
							console.log('code: ' + req.status + '\n message: ' + req.responseText + '\n error: ' + err);
							console.log(typeof req.responseText); // string

							let obj = JSON.parse(req.responseText); // <-> JSON.stringify()
							console.log(typeof obj) // object
							
							if (obj.isLogin == false) {
								alert('로그인 사용자만 댓글 작성이 가능합니다.');
								location.href = '/member/login';
							}
						}
					});
				}, // addComment

				// 답댓글 등록하기
				addReplyComment: function (event) {
					let btn = event.target;
					
// 					let reRef = $(btn).data('reref');
					let reRef = btn.dataset.reref;
					let reLev = btn.dataset.relev;
					let reSeq = btn.dataset.reseq;
					console.log(reRef + ', ' + reLev + ', ' + reSeq);

					
					let obj = {
							nno: nno,
							content: this.inputReplyComment,
							reRef: reRef,
							reLev: reLev,
							reSeq: reSeq
					};
					console.log(obj);

					let str = JSON.stringify(obj); // 자바스크립트 객체를 JSON 문자열로 변환
					console.log(str);

					let vm = this;
					
					$.ajax({
						url: '/comment/new/reply', // 답댓글은 /reply
						data: str,
						method: 'POST',
						contentType: 'application/json; charset=UTF-8',
						success: function (response) {
							console.log(response);

							vm.getList();
						},
						error: function () {
							alert('로그인 사용자만 댓글 작성이 가능합니다.');
							location.href = '/member/login';
						}
					});
					
				},

				// 댓글 한개 삭제하기
				removeComment: function (index, event) {
					// a 태그의 href 속성 기본기능 방지
					event.preventDefault(); // 스크롤바 이동 방지

					let isRemove = confirm('댓글을 정말 삭제하시겠습니까?');
					if (isRemove == false) {
						return;
					}
					
					let comment = this.commentList[index];
					let cno = comment.cno;
					console.log('삭제 cno = ' + cno);

					let vm = this;
					
					// 삭제 요청
					$.ajax({
						url: '/comment/' + cno,
						method: 'DELETE',
						success: function (response) {
							console.log(response);
							// 현재 페이지 다시 불러오기
							vm.getList();

							// 다른방식 구현
// 							vm.commentList.splice(index, 1); // 삭제한 댓글 아이템을 리스트에서 제거
// 							vm.totalCount--; // 댓글갯수 1 감소
						},
						error: function () {
							alert('로그인 사용자만 댓글 삭제가 가능합니다.');
							location.href = '/member/login';
						}
					});
				},

				// 댓글 한개 수정화면 보이기
				showModifyComment: function (index, event) {
					// a 태그의 href 속성 기본기능 방지
					event.preventDefault(); // 스크롤바 이동 방지

					// 선택한 댓글만 토글로 보이고, 나머지는 숨기기
					this.toggleReplyWriteArea(index);
					
					// 댓글내용을 수정댓글 입력상자에 배치
					let comment = this.commentList[index];
					this.inputReplyComment = comment.content;
					
					this.type = 'update'; // 등록 버튼의 용도를 수정으로 표시 변경
				}, // showModifyComment

				modifyComment: function (index) {
					let comment = this.commentList[index];

					let obj = {
							cno: comment.cno,
							content: this.inputReplyComment
					};

					// 오브젝트를 JSON 문자열로 변환
					let strJson = JSON.stringify(obj);

					let vm = this;
					
					$.ajax({
						url: '/comment/modify',
						method: 'PUT',
						data: strJson,
						contentType: 'application/json; charset=UTF-8',
						success: function (response) {
							// 현재페이지 다시 불러오기
							vm.getList();

							// 다른방식 구현
// 							let cmt = response.comment;
// 							cmt.writeOk = false;
// 							//vm.commentList[index] = cmt; //변경내용을 Vue객체가 알아차라지 못해서 화면랜더링이 일어나지 않음.
// 							vm.commentList.splice(index, 1, cmt); // 인덱스 위치부터 1개를 cmt 객체로 수정
						},
						error: function () {
							alert('로그인 사용자만 댓글 수정이 가능합니다.');
							location.href = '/member/login';
						}
					});
				},

				// 답댓글 추가 또는 현재댓글 수정하기 
				addReplyOrUpdateComment: function (index, event) {
					if (this.type == 'insert') { // 답댓글 추가
						this.addReplyComment(event);
					} else if (this.type == 'update') { // 현재댓글 수정
						this.modifyComment(index);
					}
				}
			},
			mounted: function () {
				this.getList();
			}
		});

	</script>
</body>
</html>



