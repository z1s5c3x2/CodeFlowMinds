package service;

import java.io.IOException;
import java.util.Vector;

import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import controller.ServerSocket;
import model.dto.ChatDto;
import model.dto.DrawDto;
import model.dto.MemberDto;
import model.dto.RoomDto;
import model.dto.SendDto;

public class PlayerService extends GameException{
	private static PlayerService instance = new PlayerService();
	public static PlayerService getInstance() {return instance;}
	private PlayerService () {}
	
	private SendDto<String> sendDto;
	private Session session;
	ObjectMapper mapper = new ObjectMapper();
	String mid;
	
	public void getMsg(String msg , Session _session,String type,String _mid){
		try {
			this.sendDto = mapper.readValue(msg, SendDto.class);
			this.session = _session;
			this.mid = _mid;
		} catch (Exception e) {
			System.out.println("변환 에러");
			return;
		}
		if(type.equals("chat"))
			chat(msg);	
		else if(type.equals("selectQuestion"))
			seleteQuestion();
		else if( type.equals("sendDraw")) 
			sendDraw(msg);
		else if( type.equals("randerBtn") || type.equals("getClear") || type.equals("getUndoLast")) 
			canvasControl();
			
	}
	private void canvasControl(){
		Vector<RoomDto> list = ServerSocket.roomList;
		try {
			String json = mapper.writeValueAsString(sendDto);
			for(int i = 0; i < list.size(); i++) {

				if(list.get(i) == null) break;
				
				if(list.get(i).getRoomNum() == sendDto.getRoomNum()) {
					list.get(i).getMemberVector().forEach(r -> {
						try {r.getMemSession().getBasicRemote().sendText(json);
						} catch (Exception e) {e.printStackTrace();}
					});
				}			
			}
		} catch (Exception e2) {
			System.out.println("threeBranches 에러"+ e2);
		}	
	}
	private void sendDraw(String msg){
		//턴 예외처리
		if(!turnException(sendDto.getRoomNum(),mid)) return;
			
		
		//DrawDto용 send dto 초기화 
		try {
			SendDto<DrawDto> drawDto = mapper.readValue(msg, SendDto.class);
			String jsonDto = mapper.writeValueAsString(drawDto);
			
			Vector<RoomDto> list = ServerSocket.roomList;
			for(int i = 0; i < list.size(); i++) {
				
				if(list.get(i) == null) break;
				
				if(list.get(i).getRoomNum() == drawDto.getRoomNum()) {
					list.get(i).getMemberVector().forEach(r -> {
						try {r.getMemSession().getBasicRemote().sendText(jsonDto);
						} catch (Exception e) {e.printStackTrace();}
					});
				}			
			}		
		} catch (Exception e2) {
			System.out.println("sendDraw에러" +e2);
		}
		
	}
	private void chat(String msg){
		//채팅용 send dto 초기화 
		SendDto<ChatDto> _dto = new SendDto<ChatDto>();
		try {
			_dto = mapper.readValue(msg, SendDto.class);
		} catch (Exception e2) {
			System.out.println("채팅 Dto 매퍼 에러");
			return;
		}
		
		//sendDto 안의 data를 ChatDto로 초기화
		_dto.setData( mapper.convertValue(_dto.getData(), ChatDto.class));
		
		// chatdto 안에 있는 data에는 메세지가 아이디 : 메세지 형식으로 담겨있어 
		// 만약 정답이면 같은 방 안에 있는 유저에게 정답 알림
		if(_dto.getData().getText().equals(ServerSocket.roomList.get(sendDto.getRoomNum()).getRoomQuestion())){
			_dto.setType("answer");
			
			for(MemberDto mdt : ServerSocket.roomList.get(sendDto.getRoomNum()).getMemberVector()){
				
				if(mdt.getMid().equals(_dto.getData().getMid())){
					
					mdt.setWinCount(mdt.getWinCount()+1);
					break;
					
				}
			}
			
			
		}
		for(MemberDto mdt : ServerSocket.roomList.get(sendDto.getRoomNum()).getMemberVector()){
			try {
				mdt.getMemSession().getBasicRemote().sendText(mapper.writeValueAsString(_dto));
				playerStatusUpdate("PlayerStatus",sendDto.getRoomNum());
			} catch (Exception e2) {
				System.out.println("채팅 보내기 오류 " + e2);
			}
		}
	}
	private void seleteQuestion()
	{
		if(!turnException(sendDto.getRoomNum(),mid)) return;
			
		
		String ques = QuestionRead.getInstance().getQuestion(Integer.parseInt(sendDto.getData()));
		ServerSocket.roomList.get(sendDto.getRoomNum()).setRoomQuestion(ques);
		
		ServerSocket.roomList.get(sendDto.getRoomNum()).getMemberVector().forEach(mem->{
			try {
				mem.getMemSession().getBasicRemote().sendText(
						mapper.writeValueAsString(
								SendDto.<String>builder().type("getQuestion")
								.data( mem.isTurn() ? ques:ques.length() + " 글자")
								.build()
								)
						);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});
	}
	
}
