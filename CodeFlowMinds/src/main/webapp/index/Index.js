let clientSocket = new WebSocket('ws://localhost:80/CodeFlowMinds/ServerSocket/' + -1);
	clientSocket.onopen = (e) => { console.log('알림용 서버소켓에 들어옴'); }
	clientSocket.onclose = (e) => { console.log('알림용 서버소켓에 나감'); }
	clientSocket.onerror = (e) => { console.log('알림용 서버소켓 오류'); }
	clientSocket.onmessage = (e) => { onmessage(e) }
let loginState = false;

isLogin();

// 로그인 여부에 따른 화면 출력
function isLogin(){
	
	$.ajax({
		url: "../MemberController",
		method: "get",
		data: {type:'isLogin'},
		success: r => {
			let html = ``;

			if (r) {
				html = `<button onclick="logout()">로그아웃</button>`
				loginState=true;
			}
			else{
				html = `<button onclick="login()">로그인</button>
					<button onclick="signUp()">가입</button>`
				loginState=false;	
			}

			document.querySelector('.memberBtnBox').innerHTML = html;
		},
		error: e => { console.log(e)}					 
	})			
}
function login() {
	let data = { type:'login', mid: prompt('아이디 입력'), mpwd: prompt('비밀번호 입력') };
	$.ajax({
		url: "../MemberController",
		method: "get",
		data: data,
		success: r => {
			
			if (r != "fail") {
				alert("로그인 성공")
				loginState = true;
				window.sessionStorage.setItem('loginId',r);
				// 로그인 여부에 따른 화면 출력
				isLogin();
			}
			else{
				alert('아이디 또는 비밀번호 확인')				
			}			
		},
		error: e => { console.log(e)}					 
	})		
}
function logout(){
	$.ajax({
		url:"../MemberController",
		data:{},
		method:"delete",
		success:r=>{
			if(r){
				alert('로그아웃 되었습니다.');loginState = false;isLogin();
				window.sessionStorage.setItem("loginId",null);	
			 }
			else
				alert('로그아웃 실패')
		},
		error:e=>{console.log(e)}
	})
}
function signUp() {
	let mid = prompt('회원가입 할 아이디 입력')
	if (mid == null || mid.length < 1) { return }
	let mpwd = prompt('회원가입 비밀번호 입력')
	if (mpwd == null || mpwd.length < 1) { return }

	if (prompt('비밀번호 확인') != mpwd) {
		return
	}

	$.ajax({
		url: "../MemberController",
		method: "post",
		data: { mid: mid, mpwd: mpwd },
		success: r => {
			alert(r ? "가입 성공" : '아이디 또는 비밀번호 중복')
		},
		error: e => { console.log(e) }
	})


}
// 방입장
function selectRoom(rno) {
	
	if (!loginState)
		return alert('로그인 해주세요.')
		
	let rpwd = prompt('방 비밀번호를 입력해주세요')
	
	$.ajax({
		url : "/CodeFlowMinds/RoomController",
		method : "put",
		data : {type:'checkRpwd' , rno : rno , rpwd : rpwd},
		success : r => { console.log(r)
			
			if( r == 'true' )	
				location.href = '/CodeFlowMinds/room/Room.jsp?roomNumber='+rno;		
			
			else if( r == 'false')
				alert('잘못된 비밀번호입니다.')
			
			else if (r == 'overSize')
				alert('이미 가득찬 방입니다.')
			
		},
		error : e => {console.log(e)}
	})
	
	
	
}
getRoomList()
function getRoomList() {
	
	$.ajax({
		url: "../RoomController",
		method: "get", //roomController로 수정
		data: { type: "getList" },
		success: r => {
			let roombox = document.querySelector('.roomListBox')
			roombox.innerHTML = ''
			console.log(r)
			//받아오기
			r.forEach(g => {
				roombox.innerHTML += `
				 	<div class="roomItem">
					<h4> ${g.roomNum} 번방</h4>
					<div class='roomNowInfo'>${g.nowPlayer}/${g.limitPlayer}</div>
					<button onclick="selectRoom(${g.roomNum})"> 입장 </butt on>
					</div>`
			})
		},
		error: e => {
			console.log(e)
		}
	})
}
function createRoom() {


	let dataInfo = {
		limitPlayer: document.querySelector('.maxMember').value,
		allRound: document.querySelector('.maxRound').value,
		pwd: document.querySelector('.roomPwd').value ,
		mid : window.sessionStorage.getItem("loginId") ,		
	}

	$.ajax({
		url: "../RoomController",
		method: "post", 
		data: dataInfo,
		success: r => {
				
				location.href = '/CodeFlowMinds/room/Room.jsp?roomNumber='+r

		},
		error: e => { console.log(e) }
	})
	
}

// 모달 열기
function onCreateRoom() {
	if (!loginState)
		return alert('로그인 해주세요.')
	document.getElementById('roomModal').style.display = 'block';
}

// 모달 닫기
function closeModal() {
	document.getElementById('roomModal').style.display = 'none';
}

// 모달 바깥을 클릭하면 모달 닫기
window.onclick = function(event) {
	var modal = document.getElementById('roomModal');
	if (event.target == modal) {
		modal.style.display = 'none';
	}
}
// 랭킹 가져오기
function getRank(){
	
	$.ajax({
		url: "../RoomController",
		method: "get", //roomController로 수정
		data: { type: "getRanking" },
		success: r => {
			console.log(r)
			let html = ``;
			
			r.forEach( (a , i) =>{
				html += `<div class="userRank">
		      			<div class="rankNo">${i+1}.</div>
			      		<div class="rankUserName">${a.mid}</div>
			     	 	<div class="rankUserWinCount">${a.winCount}</div>
		      		</div>`				
			})
			document.querySelector('.userRanking').innerHTML = html;
		},
		error: e => {
			console.log(e)
		}
	})					
}
