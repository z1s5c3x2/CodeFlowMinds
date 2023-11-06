// 캔버스 설정
const canvas = document.getElementById("canvas");
canvas.width = 800;
canvas.height = 600;

let context = canvas.getContext("2d");
let start_background_color = "whitesmoke";
context.fillStyle = start_background_color;
context.fillRect(0, 0, canvas.width, canvas.height);

// 펜설정, 컬러 굵기 
let draw_color = "black";
let draw_width = "2";
let is_drawing = false;

// 이전으로 돌리기 // 빈배열을 만든다.
let restore_array = [];
let index = -1;
let sendimg = '';
// 컬러변경
function change_color(element) {
	draw_color = element.style.background;	
}

canvas.addEventListener("mousedown",start, false);
canvas.addEventListener("mousemove",draw, false);
canvas.addEventListener("mouseup",stop, false);

//---------------------------------------------------//

let roomNumer = new URL(document.location).searchParams.get('roomNumber')
let clientSocket = new WebSocket('ws://localhost:80/CodeFlowMinds/ServerSocket/'+sessionStorage.getItem('loginId'));
clientSocket.onopen = (e) => { 
				clientSocket.send (JSON.stringify({
					serviceType : 'Room',
					roomNum : roomNumer ,
					type : 'selectRoom',
					data: sessionStorage.getItem('loginId')				
				}))
				
 }
clientSocket.onclose = (e) => { }
function onclose(){console.log('알림용 서버소켓에 나감'); }
clientSocket.onerror = (e) => {
	console.log('알림용 서버소켓 오류');
	console.log(e)
}
clientSocket.onmessage = (e) => {

	//서버소켓에서 받은 메세지를 사용하기 쉽게 초기화
	console.log(e);
	console.log(e.data)
	let receiveMsg = JSON.parse(e.data)
	
	if (receiveMsg.type == "answer") {
		//유저 정답 알림
		document.querySelector('.chatBox').innerHTML += `<div class="chat">
					<span> ${receiveMsg.data.mid}님이 정답을 맞췄습니다 !!</span>
					<span> ${receiveMsg.data.date}</span>
				</div>`
		//정답 공개
		document.querySelector('.chatBox').innerHTML += `<div class="chat answer">
					<span class="answer"> 정답 : ${receiveMsg.data.text}</span>
				</div>`
		timer = 3
		scrollUpdate()
		
		
	}
	else if (receiveMsg.type == "chat") {
		document.querySelector('.chatBox').innerHTML += `<div class="chat">
					<span class="chatWall"> ${receiveMsg.data.mid} : ${receiveMsg.data.text}</span>
					<span> ${receiveMsg.data.date}</span>
					
				</div>`
				scrollUpdate()
	} 
	else if (receiveMsg.type == 'startRound')
	{

		console.log(receiveMsg)

		startRound(receiveMsg.isTurn)
		document.querySelector('.nowRound').innerHTML = receiveMsg.nowRound
		document.querySelector('.allRound').innerHTML = receiveMsg.allRound
	}
	else if(receiveMsg.type == 'selectQuestion')
	{
		selectQuestion()
	}
	else if (receiveMsg.type == 'endRound')
		endRound();
	else if(receiveMsg.type == 'randerBtn'){

		randerBtn();
	}
	else if (receiveMsg.type == "getQuestion") {
		getClear();
		document.querySelector('.question').innerHTML = receiveMsg.data
		timer = 10;		
		settimer();			// 타이머 실행	
		console.log(isturn)
	}
	else if(receiveMsg.type == "sendDraw"){
		makeDragList(receiveMsg)	
	}else if(receiveMsg.type == "getClear"){
		getClear();
	}else if(receiveMsg.type == "getUndoLast"){
		getUndoLast();
	}else if(receiveMsg.type=="PlayerStatus")
	{
		let uibox = document.querySelector('.userInfoBox')
		let uihtml = `<div class="userInfoTitle">
					<span class="titleCount">맞춘 횟수</span>
					<span class="titleName">이름</span>
				</div>`
		receiveMsg.data.sort((a, b) => b.winCount - a.winCount)
		receiveMsg.data.forEach(m=>{
			uihtml += `	<div class="userInfo${m.turn ? " turn":""}">
					<span>${m.winCount}</span>
					<span>${m.mid}</span>
				</div>	`
			
		})
		uibox.innerHTML = uihtml;
		
	}else if(receiveMsg.type == "resultPrint")
	{
		
		let html = ``
		let rnk = 1
		receiveMsg.data.sort((a, b) => b.winCount - a.winCount)
		receiveMsg.data.forEach(m=>{
			html +=`<div class="userRank${m.mid == sessionStorage.getItem('loginId') ? " me":""}">
		      			<div class="rankNo">${rnk++}</div>
			      		<div class="rankUserName">${m.mid}</div>
			     	 	<div class="rankUserWinCount">${m.winCount}</div>
		      		</div>`
		})
		
		document.querySelector('.userRanking').innerHTML =html
		resultOpen()
	}
	console.log(receiveMsg.type);
}
//채팅 기능 세팅

function selectQuestion() {
	/*let _ch = prompt('1.과일 2.과자 3.물건 4.게임')*/
	categoryOpen()
}
function chooseNum()
{
	clientSocket.send(JSON.stringify({
		serviceType : 'Player',
		roomNum: roomNumer,
		type: 'selectQuestion',
		data: document.querySelector('.selectNum').value

	}))
	document.querySelector('.categoryModal').style.display = 'none'
}
var input = document.querySelector(".doChat");
//dochat 인풋태그에서 엔터키 키업하면 아래의 함수를 실행
input.addEventListener("keyup", function(event) {
	
	if (event.keyCode === 13) {
		if (isturn) {
			alert('자기 차례엔 채팅을 할 수 없습니다.')
			document.querySelector('.doChat').value = ''
			return
		}
		event.preventDefault();
		let sendinfo = {
			serviceType : 'Player',
			roomNum: roomNumer,
			type: "chat",
			data: {
				mid: sessionStorage.getItem('loginId'),
				text: document.querySelector('.doChat').value,
				date: new Date().toLocaleTimeString()
			}
		}
		clientSocket.send(JSON.stringify(sendinfo))
		document.querySelector('.doChat').value = ''
		// 채팅을 소켓에 아이디 : 메세지 형식으로 보내서 같은 방에 있는 세션에 그대로 전달

	}

});
// 턴 상태
let isturn = false;

function ready() {
	let sendinfo = { roomNum: roomNumer, type: "ready", data: sessionStorage.getItem('loginId') }
	clientSocket.send(JSON.stringify(sendinfo))
}

//--------------------------------- 그리기 -----------------------------------//
// 전송 객체
let sendDraw = {
		dragList : [],
		brushColor : '' ,
		brushWidth : ''
	}
// 그리기 객체 받는 함수
function makeDragList(receiveMsg){
	let getDragList = []
	console.log(receiveMsg.data)
	let drawObject = receiveMsg.data
	draw_color = drawObject.brushColor;
	draw_width = drawObject.brushWidth;
	let arr = drawObject.dragList;	
	 
	
			for(i=0;i<arr.length;i++)							
				getDragList.push([parseInt(arr[i][0]),parseInt(arr[i][1])])				
			
	getDraw(getDragList);
}
// 클라이언트들 그리는 함수
function getDraw( arr ){
	
	context.beginPath();
	context.moveTo(arr[0][0] - canvas.offsetLeft,
                   arr[0][1] - canvas.offsetTop);
	
	for(i=1;i<arr.length;i++)
	{
		context.lineTo(arr[i][0] - canvas.offsetLeft,
        				arr[i][1] - canvas.offsetTop);
        context.strokeStyle = draw_color;
        context.lineWidth =draw_width;
        context.lineCap ="round";
        context.lineJoin ="round";
        context.stroke();
	}
	context.closePath();  
	restore_array.push(context.getImageData(0,0,canvas.width, canvas.height)) 
	index += 1;   
}
// 클라이언트 캔바스 전체 지우기
function getClear(){
	context.fillStyle = start_background_color;
	context.clearRect(0, 0, canvas.width, canvas.height);
	context.fillRect(0, 0, canvas.width, canvas.height);

	restore_array = [];
	index = -1;
}
// 클라이언트 캔바스 뒤로가기
function getUndoLast(){
	if (index <= 0) {
		getClear();
	} else {
		index -= 1;
		restore_array.pop();
		context.putImageData(restore_array[index], 0, 0);
	}
}
function start(event) {
	
	is_drawing = true;
	context.beginPath();
	context.moveTo(event.clientX - canvas.offsetLeft,
		event.clientY - canvas.offsetTop);
	event.preventDefault();
	// 시작지점 저장
	sendDraw.dragList.push([event.clientX,event.clientY]);

	// 이전것 저장해두기 // 이벤트가 마우스아웃이 아닐때 마우스가 안에 있을때 위치값 저장.

}

function draw(event) {

	if (is_drawing && isturn) {
		sendDraw.dragList.push([event.clientX,event.clientY])
		context.lineTo(event.clientX - canvas.offsetLeft,
			event.clientY - canvas.offsetTop);
		context.strokeStyle = draw_color;
		context.lineWidth = draw_width;
		context.lineCap = "round";
		context.lineJoin = "round";
		context.stroke();
	}
}
function stop(event) {
	if (is_drawing && isturn) {
		context.stroke();
		context.closePath();
		is_drawing = false;
	}
	event.preventDefault();
			
	sendDraw.brushColor = draw_color; sendDraw.brushWidth = draw_width;
	clientSocket.send( JSON.stringify({serviceType : 'Player', roomNum : roomNumer , type : 'sendDraw' , data:sendDraw}) );
	sendDraw.dragList = [];
}
// 지우기
function clear_canvas() {
	
	clientSocket.send( JSON.stringify({serviceType : 'Player',roomNum : roomNumer , type : 'getClear' , data : ''}) )
}
// 뒤로가기
function undo_last() {

	clientSocket.send( JSON.stringify({serviceType : 'Player',roomNum : roomNumer , type : 'getUndoLast' , data : ''}) )
}

// 방장 확인 후 시작버튼 띄우기
function isAdmin() {

	$.ajax({
		url: "../RoomController",
		data: { type: 'isAdmin', rno: roomNumer, id: sessionStorage.getItem('loginId') },
		method: "get",
		async: false,
		success: r => {
			let btnBox = document.querySelector('.btnBox')

			let html = `<button onclick="ready()" type="button">준비</button>
						<button onclick="exit()"type="button">나가기</button>`
			if (r) 
				 html = `<button onclick="startR()" type="button">시작</button>`+ html					
			 
			btnBox.innerHTML = html;
		},
		error : e => {console.log(e)}
	});

}

//타이머-------------------------------------------------------------------------//
let timer = 0; 
let timerInter; // setInterval() 함수를 가지고 있는 변수 [ setInterval 종료 시 필요 ]

// 시작버튼 누를 시 startRound() 실행하게 하는 메세지 전송 
function startR(){
	console.log("실행")
	$.ajax({
		url: "../RoomController",
		data : {type: 'isAlreadyStart',rno : roomNumer},
		method : "get",	
		async : false,	
		success : r => {
			console.log(r)
			if(r){
				startSend();
			}else{
				console.log('이미 게임 진행 중입니다.')
			}									
		},
		error : e => {console.log(e)}
	});
	
}
function startSend(){
	clientSocket.send(
		JSON.stringify({
			serviceType : 'Room',
			 roomNum : roomNumer ,
			 type : 'startRound',
			 data : 'null'}));	
}
// 라운드 시작
function startRound(_turn){
	
	isturn = _turn;
	if(isturn)
	{
		document.querySelector('.doChat').disabled = true
		document.querySelector('.doChat').placeholder ="본인 차례에는 채팅기능 이용 불가";
	}else
	{
		document.querySelector('.doChat').disabled = false
		document.querySelector('.doChat').placeholder ="입력 메세지";
	}
}

// 타이머 함수
function settimer(){

	timerInter = setInterval( () => {
			
		let m = parseInt( timer / 60 ); // 분 계산 [ 몫 ]
		let s = parseInt (timer % 60 ); // 초 계산 [ 나머지 ]
			// 2. 두자리수 맞춤 3 -> 03
		m = m < 10 ? "0"+m : m; 
		s = s < 10 ? "0"+s : s; 
		
		document.querySelector('.time').innerHTML = `${m}:${s}`;
		timer--;  
		
		if( timer < 0 ){	
			clearInterval( timerInter )
			
			if(isturn){console.log("턴 실행")
				startSend();
			}
				
		  }	
	} , 1000); // 1초에 한번씩 실행되는 함수
}
//------------------------------------------------------------//
// 결과 데이터베이스 업데이트
function endRound(){

	clearInterval( timerInter )
	$.ajax({
		url : "../RoomController",
		data : {type:"updateRecord" ,mid: sessionStorage.getItem('loginId'), rno : roomNumer },
		method : "put",
		success : r => {
			if(r)
				console.log('업데이트 성공')
			else
				console.log('업데이트 실패')
		}
	})
	// 화면 출력
}
randerBtn();
function randerBtn(){
	// 동기화 위해서 1초 지연
	setTimeout(function(){isAdmin()}, 700);
}
function scrollUpdate()
{
	let chatcon = document.querySelector('.chatBox')

	chatcon.scrollTop = chatcon.scrollHeight
}
// 새로고침 막기
function notReload(event) { 
    if((event.ctrlKey == true && (event.keyCode == 78 || event.keyCode == 82)) || (event.keyCode == 116) ) { 
        event.keyCode = 0; 
        event.cancelBubble = true; 
        event.returnValue = false; 
    } 
} 
document.onkeydown = notReload;
// 나가기 버튼
function exit(){
	location.href = '/CodeFlowMinds/index/Index.jsp'
	
}

// 결과 집계창 열기
function resultOpen(){
	let resultModal = document.querySelector('.resultModal');
	if(resultModal.style.display != 'block')
	resultModal.style.display = 'block';
	else resultModal.style.display = 'none';
}

// 카테고리 선택창 열기
function categoryOpen(){
	let categoryModal = document.querySelector('.categoryModal');
	if(categoryModal.style.display != 'block')
	categoryModal.style.display = 'block';
	else categoryModal.style.display = 'none';
}