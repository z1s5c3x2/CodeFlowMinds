
//URL(window.location).searchParams.get("boardId")
// 캔버스 설정
const canvas = document.getElementById("canvas");
canvas.width = 800;
canvas.height =600;

let context = canvas.getContext("2d");
let start_background_color ="whitesmoke";
context.fillStyle = start_background_color;
context.fillRect(0,0,canvas.width, canvas.height);

// 펜설정, 컬러 굵기 
let draw_color ="black";
let draw_width = "2";
let is_drawing = false;

// 이전으로 돌리기 // 빈배열을 만든다.
let restore_array =[];
let index = -1;
let sendimg = '';
// 컬러변경
function change_color(element){
    draw_color = element.style.background;
}

//canvas.addEventListener("touchstart",start, false);
//canvas.addEventListener("touchmove",draw, false);
canvas.addEventListener("mousedown",start, false);
canvas.addEventListener("mousemove",draw, false);

//canvas.addEventListener("touchend",stop, false);
canvas.addEventListener("mouseup",stop, false);
//canvas.addEventListener("mouseout",stop, false);
//---------------------------------------------------//
// 이미지 그리는 부분
let sendinfo = {
		draglist : []
	}
function start(event){
	sendinfo = {
		draglist : []
	}
    is_drawing = true;
    context.beginPath();
    context.moveTo(event.clientX - canvas.offsetLeft,
                   event.clientY - canvas.offsetTop);
    event.preventDefault();
    sendinfo.draglist.push([event.clientX,event.clientY])

    // 이전것 저장해두기 // 이벤트가 마우스아웃이 아닐때 마우스가 안에 있을때 위치값 저장.
    
    if(event.type != 'mouseout'){
    restore_array.push(context.getImageData(0,0,canvas.width, canvas.height));
    index += 1;
    }
    
}
let testarray = []
function getdraw(_arr)
{
	console.log( _arr );
	
	context.beginPath();
	context.moveTo(_arr[0][0] - canvas.offsetLeft,
                   _arr[0][1] - canvas.offsetTop);
	testarray.push(context.getImageData(0,0,canvas.width, canvas.height))
	for(i=1;i<_arr.length;i++)
	{
		context.lineTo(_arr[i][0] - canvas.offsetLeft,
        				_arr[i][1] - canvas.offsetTop);
        context.strokeStyle = draw_color;
        context.lineWidth =draw_width;
        context.lineCap ="round";
        context.lineJoin ="round";
        context.stroke();
	}            
                   
}
function test()
{
	for(i = 0; i <testarray.length;i++)
	{
		context.putImageData(testarray[i],0,0);	
	}
	
}
function deltest()
{
	if(testarray.length <= 1)
	{
		clear_canvas();
		return
	}
	testarray.pop()
	context.putImageData(testarray[testarray.length-1],0,0);
	
}
function draw(event){
	
    if(is_drawing)
    {
		console.log(draw_width)
		sendinfo.draglist.push([event.clientX,event.clientY])
        context.lineTo(event.clientX - canvas.offsetLeft,
        				event.clientY - canvas.offsetTop);
        context.strokeStyle = draw_color;
        context.lineWidth =draw_width;
        context.lineCap ="round";
        context.lineJoin ="round";
        context.stroke();
    }
}

function stop(event){
    if (is_drawing){
        context.stroke();
        context.closePath();
        is_drawing =false;
    }
    event.preventDefault();
    sendserver()

}


	let clientSocket = new WebSocket('ws://localhost:80/CodeFlowMinds/ServerSocke');
	clientSocket.onopen = (e) => { console.log('알림용 서버소켓에 들어옴'); }
	clientSocket.onclose = (e) => { console.log('알림용 서버소켓에 나감'); }
	clientSocket.onerror = (e) => { console.log('알림용 서버소켓 오류'); }
	clientSocket.onmessage = (e) => { onmessage(e) }
	

function onmessage( e ){
	console.log( e );
	console.log( e.data );
	console.log( JSON.parse(e.data) );
	
	let getDragList = []
	
	let res = JSON.parse(e.data).draglist;
	
	console.log( res );
	
			for(i=0;i<res.length;i++)
			{
				
				getDragList.push([parseInt(res[i][0]),parseInt(res[i][1])])
				
			}
			getdraw(getDragList)
			
}
	
function sendserver()
{
	clientSocket.send( JSON.stringify(sendinfo) );
	/*
	$.ajax({
		url:"../DrawController",
		method:'post',
		traditional:true,
		data: sendinfo,
		
		success: res =>{
			
			for(i=0;i<res.length;i++)
			{
				getDragList.push([parseInt(res[i].split(',')[0]),parseInt(res[i].split(',')[1])])
			}
			getdraw(getDragList)
		},
		erroe :e=>{
			console.log(e)
		}
		
		
	})
	*/
	
}
// 지우기
function clear_canvas(){
    context.fillStyle = start_background_color;
    context.clearRect(0,0,canvas.width, canvas.height);
    context.fillRect(0,0,canvas.width, canvas.height);

    restore_array=[];
    index =-1;
}

// 뒤로가기
function undo_last(){
    if(index <=0){
        clear_canvas();
    }else{
        index -= 1;
        restore_array.pop();
        context.putImageData(restore_array[index],0,0);
    }
}
function testview()
{
	console.log(context)
}
// 저장하기
function save(){
    canvas.toBlob((blob)=>{

        const a = document.createElement('a');
        document.body.append(a);
        a.download ='export{timestamp}.png';
        a.href= URL.createObjectURL(blob);
        a.click();
        a.remove();
    });
}