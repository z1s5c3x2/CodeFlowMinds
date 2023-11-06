package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.dto.ChatDto;
import model.dto.DrawDto;
import model.dto.MemberDto;
import model.dto.RoomDto;
import model.dto.SendDto;
import service.PlayerService;
import service.QuestionRead;
import service.RoomService;


@ServerEndpoint("/ServerSocket/{mid}")
public class ServerSocket {
		
			
		
	public static Vector<RoomDto> roomList = new Vector<>(50);
	
	@OnOpen // 클라이언트 소켓이 들어왔을떄 매핑[연결]
	public void 서버입장( Session session ) throws Exception {
			System.out.println("서버 소켓 입장");
		}

	@OnClose // 클라이언트 소켓이 나갔을때 매핑[연결]
	public void 서버나가기( Session session ) throws Exception { 
		System.out.println("서버 소켓 나감 ");
		System.out.println(roomList.size());
		// 소켓 나가면 멤버벡터에서 세션 remove
		for(int i = 0; i < roomList.size(); i++) {
			boolean nowRoom = false;
			if(roomList.get(i) == null) {

				break;
			}
				
			
			//멤버벡터 사이즈만큼, 멤버벡터 m 인덱스의 세션이 나간 세션과 일치 시 m번째 dto remove
			for(int m = 0; m < roomList.get(i).getMemberVector().size(); m++) {

				if( roomList.get(i).getMemberVector().get(m).getMemSession() == session ) {

					roomList.get(i).getMemberVector().remove(m);
					nowRoom = true;
					
					PlayerService.getInstance().playerStatusUpdate("PlayerStatus", i);
					break;
				}					 				
			}
			
			// 세션이 나간후 멤버 벡터의 사이즈가 0이라면 방dto 초기화
			if( roomList.get(i).getMemberVector().size() == 0 ) {
				roomList.get(i).setNowStatus(true);
				roomList.get(i).setNowPlayer(0);
				roomList.get(i).setNowRound(1);
				continue;
			}
				
			// 세션이 나간 방에 randerBtn 전송
			if( nowRoom ) {
				for( int r = 0; r < roomList.get(i).getMemberVector().size(); r++) {
					roomList.get(i).getMemberVector().get(r).getMemSession()
							.getBasicRemote().sendText(
									new ObjectMapper().writeValueAsString(SendDto.<String>builder().type("randerBtn").build()));
				}
			}
			
		}// for 		
	}
	
	@OnError // 클라이언트 소켓이 에러가 발생했을떄 매핑[연결] !!! 필수로 정의할 인수
	public void 서버오류( Session session , Throwable e ) 
			
			throws Exception { System.out.println( e ); }

	public static SendDto<String> sendDto ;
	@OnMessage // 클라이언트 소켓이 메시지를 보냈을때  매핑[연결] !!! 필수로 정의할 인수
	public static void 서버메시지( Session session,@PathParam("mid") String mid , String msg ) throws Exception { 
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			sendDto = mapper.readValue(msg, SendDto.class);
		} catch (Exception e2) {
			System.out.println("변환 오류");
			e2.printStackTrace();
		}
		
		String type = sendDto.getType();
		if(sendDto.getServiceType().equals("Room"))
		{
			RoomService.getInstance().getMsg(msg, session, type);
		}else {
			PlayerService.getInstance().getMsg(msg, session, type,mid);
		}

	}
	// 멤버 상태 업데이트 (입장,퇴장,정답처리,턴처리 정보 클라이언트들에게 전송)
	
	//자기 턴일때만 가능한 msg 예외처리
	
}
//
// js아작트 -> 서블릿 get세션 loginDto( 방번호랑 아이디 ) -> 소켓에 보냄 무엇을? 세션 아이디랑 메세지 -> 소켓 hashMap에서 방번호 대조 value == list
//		 																						
//		 																						리스트 애들한테 아이디 + 메세지 뿌려줌
/*

	Session session : 요청한 클라이언트 소켓의 변수 
	Throwable e 	: 에러 발생시 에러이유가 저장된 변수 
	String msg 		: 요청받은 메시지 내용
	
	서버소켓 
		1. 	@ServerEndpoint("서버소켓경로/{매개변수명1}/{매개변수명2}")
		2.	@OnOpen			필수 매개변수 : Session session
		3. 	@OnClose		필수 매개변수 : Session session
		4.	@OnError		필수 매개변수 : Session session , Throwable e 
		5.	@OnMessage		필수 매개변수 : Session session , String s 
		6.	@PathParam("경로상의{매개변수명}") 	: 경로상의 변수 요청 

*/


/*
	Session session : 요청한 클라이언트 소켓의 변수 
	Throwable e 	: 에러 발생시 에러이유가 저장된 변수 
	String msg 		: 요청받은 메시지 내용
	
	서버소켓 
		1. 	@ServerEndpoint("서버소켓경로/{매개변수명1}/{매개변수명2}")
		2.	@OnOpen			필수 매개변수 : Session session
		3. 	@OnClose		필수 매개변수 : Session session
		4.	@OnError		필수 매개변수 : Session session , Throwable e 
		5.	@OnMessage		필수 매개변수 : Session session , String s 
		6.	@PathParam("경로상의{매개변수명}") 	: 경로상의 변수 요청 


*/


/*		String [] arparm = request.getParameterValues("draglist");
for(int i=0;i<arparm.length;i++)
{
System.out.println(arparm[i]);
}

ObjectMapper objectMapper = new ObjectMapper();
String jsonArray = objectMapper.writeValueAsString(arparm);

response.setContentType("application/json;charset=UTF-8");

System.out.println(jsonArray);
response.getWriter().print(jsonArray);*/

//유저가 준비 눌렀을 때 최대 인원과 준비한 사람의 수를 구해서 시작 
		/*if(type.equals("1ready"))
		{
			RoomDto rdt =roomList.get(sendDto.getRoomNum());
			int lp = rdt.getLimitPlayer();
			rdt.setReadyCount(0);
			rdt.getMemberVector().forEach(m->{
				// 준비 버튼 누른 클라이언트의 아이디를 가져와서 방 안에 멤버 벡터에서 같은 아이디를 찾아서 준비완료 상태로 만들어줌
				if(m.getMid().equals(sendDto.getData()))
				{
					m.setReady(true);
				}
				//방 안의 멤버들의 준비 상태를 확인하여 준비상태면 +1씩 증가시킴
				rdt.setReadyCount(m.isReady() ? rdt.getReadyCount()+1 : rdt.getReadyCount() );

			});
			// 방 안의 멤버수가 2명 이상이고(게임시작 가능수) 2명 이상인 인원이 모두 준비하면 방 안에 들어온 클라이언트들에게 게임시작 메세지 보내기
			if(!rdt.isNowStatus() && rdt.getMemberVector().size() > 1 &&rdt.getMemberVector().size() == rdt.getReadyCount())
			{
				//첫턴 true 설정, 방 상태 시작상태로 변경
				rdt.getMemberVector().get(0).setTurn(true);
				rdt.setNowStatus(true);
				
				rdt.getMemberVector().forEach(m ->{
					try {
						//맨 처음 유저에게 바로 정답 카테고리 띄워주기
						m.getMemSession().getBasicRemote().sendText(mapper.writeValueAsString(
								SendDto.<String>builder().type("Start").data( m.isTurn() ? "turn": "wait").roomNum(rdt.getRoomNum()).build()));
						
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("시작 전송 에러"+e);
					}
					
				});
				
			}
			
			
			
		}*/