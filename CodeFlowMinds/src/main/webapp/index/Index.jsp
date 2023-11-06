<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
<title>Insert title here</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
<link rel="stylesheet" href="./Index.css">
</head>
<body>

	<%@include file="../header.jsp"%>
	<div class="indexWrap">
		<div class="indexTitle">
			<h3>방 목록</h3>
		</div>
		<div class="roomListBox"></div>
		<div class="memberBtnBox">
			<button onclick="login()">로그인</button>
			<button onclick="signUp()">가입</button>
		</div>
		<div class="roomBtnBox">
			<button onclick="onCreateRoom()">방 만들기</button>
			<button type="button" onclick="getRoomList()">새로고침 </button>
			<button type="button" onclick="getRank()" class="rankingBtn" data-bs-toggle="modal" data-bs-target="#rankModal">
				랭킹
			</button>
		</div>
		

		<div id="roomModal" class="modal">
			<div class="modal-Content">
				<span class="close" onclick="closeModal()">×</span>
				<h3>방 만들기</h3>
				<div class="inputBox">
					<span> <span class="settingInfo">최대 인원</span> <select
						class="maxMember">

							<option value=2>2</option>
							<option value=3>3</option>
							<option value=4>4</option>

					</select> <span class="settingInfo">최대 라운드</span> <select class="maxRound">
							<option value=5>5</option>
							<option value=6>6</option>
							<option value=7>7</option>
							<option value=8>8</option>
							<option value=9>9</option>
					</select>
					</span> 
					<input type="text" maxlength="8" class="roomPwd" placeholder="방 비밀번호 공백 시 공개">
					<button type="button" onclick="createRoom()">방 생성</button> 				
				</div>
			</div>
		</div>

		<!-- 부트스트랩 Modal -->
		<div class="modal fade" id="rankModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
		  <div class="modal-dialog modal-dialog-centered modal-lg">
		    <div class="modal-content rankModalContent">
		      <div class="rankingTitle">
		      	<div class="rankTitleNo">No</div>
		      	<div class="rankTitleUserName">USER</div>
		      	<div class="rankTitleUserWinCount">WIN</div>
		      </div> 
		      	<!-- 유저 출력 구간 -->
		      	<div class="userRanking">
		      		<!-- innerHTML -->
		      	</div>    
		    </div>
		  </div>
		</div>
	</div>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
	<script type="text/javascript" src="./Index.js"></script>
</body>
</html>