<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="/CodeFlowMinds/room/Room.css" rel="stylesheet">
	
<title>Insert title here</title>
</head>
<body>

	<!-- 방 전체 wrap -->
	<div class="roomWrap">
	
		<!-- 왼쪽 전체  구간  -->
		<div class="problemBox">
			<!-- 상단 구역  -->
			<div class="topInfo">
				<div >라운드 <span class="nowRound">1</span>/<span class="allRound">?</span></div>
				<div class="question"> 문제 </div>
				<div>타이머 : <span class="time">03:00</span></div>
			</div>
			<!-- 캔바스 구간 -->	
			<div>
				<canvas id="canvas">
				
				</canvas>
				<div class="tools">
		            <!-- 컬러칩 -->
		            <div onclick="change_color(this)" class="color-field" style="background: red;"></div>
		            <div onclick="change_color(this)" class="color-field" style="background: blue;"></div>
		            <div onclick="change_color(this)" class="color-field" style="background: green;"></div>
		            <div onclick="change_color(this)" class="color-field" style="background: yellow;"></div>
		
		             <!-- 컬러피커  -->
		            <input onInput="draw_color= this.value" type="color" class="color-picker" style="width:70px">
		            <!-- 펜굵기 조절 -->
		            <input type="range" min="1" max="100" class="pen-range"
		                  onInput="draw_width=this.value">
		            <!-- 되돌리기, 지우기, 다운로드 버튼  -->
		            <button onclick="undo_last()"type="button" class="button">되돌리기</button>
		            <button onclick="clear_canvas()" type="button" class="button">지우기</button>		      
	       		 </div>
       		 </div>
		</div>
		<!-- 유저 전체 정보 구간 -->
		<div class="userInfoWrap">
			<!-- 유저정보 구역 -->
			<div class="userInfoBox">
				
				<!-- 여기서부터 innerHTML -->	
			</div>
			<!-- 채팅 구역 -->
			<div class="chatBox">
				

			</div>
			<div class="onChat">
				<input class="doChat" type="text" placeholder="입력 메세지">
			</div>
			<!-- 버튼 구역 -->
			<div class="btnBox">
				<button onclick="ready()" type="button">준비</button>
				<button onclick="exit()"type="button">나가기</button>
			</div>
		</div><!-- 유저 전체 정보 구간 끝 -->	
	
	</div><!-- 전체 wrap 끝  -->
	<div id="myModal" class="resultModal">
        <div class="modalContent">
             <div class="rankingTitle">
		      	<div class="rankTitleNo">No</div>
		      	<div class="rankTitleUserName">USER</div>
		      	<div class="rankTitleUserWinCount">WIN</div>
		      	<button type="button" onclick="resultOpen()">X</button>
		      </div> 
		      	<!-- 유저 출력 구간 -->
		      <div class="userRanking">
 		
		     </div>       
        </div>
    </div>
    <div id="myModal" class="categoryModal">
        <div class="modalContent">
        	<h1>카테고리를 선택해주세요.</h1>
        	<div class="categoryContent">      		
        		<div>1.스포츠</div>
        		<div>2.과일</div>
        		<div>3.사자성어</div>
        		<div>4.음식</div>
        		<div>5.랜덤</div>
        	</div> 
        	<div>
	        	<div>선택 번호</div>
	        	<input maxlength="1" type="text" class="selectNum"/>
	        	<button type="button" onclick="chooseNum()" class="chooseNum">선택</button>
        	</div>
        	       
        </div>
    </div>
    <button type="button" onclick="resultOpen()">열기</button>
    <button type="button" onclick="categoryOpen()">열기</button>
	<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script src="/CodeFlowMinds/room/Room.js" type="text/javascript"></script>
</body>
</html>