package service;

import java.io.IOException;
import java.util.Vector;

import javax.websocket.Session;

import com.fasterxml.jackson.databind.ObjectMapper;

import controller.ServerSocket;
import model.dto.MemberDto;
import model.dto.RoomDto;
import model.dto.SendDto;

public class RoomService extends GameException {
	private static RoomService instance = new RoomService();
	public static RoomService getInstance() {return instance;}
	private RoomService () {}
	
	private SendDto<String> sendDto;
	private Session session;
	ObjectMapper mapper = new ObjectMapper();
	
	public void getMsg(String msg , Session _session,String type){
		
		try {
			this.sendDto = mapper.readValue(msg, SendDto.class);
			this.session = _session;
		} catch (Exception e) {
			System.out.println("변환 에러");
			return;
		}
		
		
		if(type.equals("createRoom")) 
			createRoom();
		else if(type.equals("selectRoom")) 		
			selectRoom();
		else if(type.equals("startRound")) 
			startRound();
		
		
	}

	private void createRoom() {
		for (int i = 0; i < ServerSocket.roomList.size(); i++) {
			if (ServerSocket.roomList.get(i) == null)
				break;
			if (ServerSocket.roomList.get(i).getRoomNum() == sendDto.getRoomNum())
				ServerSocket.roomList.get(i).getMemberVector()
						.add(MemberDto.builder().mid(sendDto.getData()).memSession(session).build());
		}
	}

	private void selectRoom() {
		try {
			for (int i = 0; i < ServerSocket.roomList.size(); i++) {
				if (ServerSocket.roomList.get(i) == null)
					break;
				if (ServerSocket.roomList.get(i).getRoomNum() == sendDto.getRoomNum())
					ServerSocket.roomList.get(i).getMemberVector()
							.add(MemberDto.builder().mid(sendDto.getData()).memSession(session).build());
			
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		playerStatusUpdate("PlayerStatus",sendDto.getRoomNum());
	}

	private void startRound() {
		// 전송할 방번호와 번호에 해당하는 RoomDto
		int roomNum = sendDto.getRoomNum();
		RoomDto roomDto = ServerSocket.roomList.get(roomNum);
		int nowPlayer = roomDto.getNowPlayer();

		boolean nextOrEnd = false;

		if (roomDto.getNowRound() > roomDto.getAllRound())
			nextOrEnd = true;

		// 해당 방 세션들에게 'startRound'와 isTurn 보내기
		roomDto.getMemberVector().forEach(r -> {
			// 만약 모든 라운드가 끝난다면 endRound타입 전송
			if (roomDto.getNowRound() > roomDto.getAllRound()) {
				try {
					r.getMemSession().getBasicRemote().sendText("{\"type\":\"endRound\"}");
				} catch (IOException e) {
					e.printStackTrace();
				}
				// dto 초기화하기, 방 상태 바꾸기
			} else {
				//
				if (r == roomDto.getMemberVector().get(nowPlayer) ) {
					r.setTurn(true);
					try {
						r.getMemSession().getBasicRemote()
								.sendText("{\"type\":\"selectQuestion\" , \"isTurn\":" + r.isTurn() + "}");
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else
					r.setTurn(false);

				try {

					r.getMemSession().getBasicRemote()
							.sendText("{\"type\":\"startRound\" , \"isTurn\":" + r.isTurn() + ", \"nowRound\" : "
									+ roomDto.getNowRound() + ", \"allRound\" : " + roomDto.getAllRound() + "}");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}); // forEach 끝

		// 만약 모든 라운드가 끝나면 if 아니면 else
		if (nextOrEnd) {
			playerStatusUpdate("resultPrint",sendDto.getRoomNum());
			roomDto.setNowStatus(true);
			roomDto.setNowRound(0);
			roomDto.setNowPlayer(0);
		} else {
			// RoomDto의 방 현재상태 false로 바꾸기, 현재 차례 +1, 현재 라운드 +1
			roomDto.setNowStatus(false);
			roomDto.setNowPlayer(
					roomDto.getNowPlayer() >= roomDto.getMemberVector().size() - 1 ? 0 : roomDto.getNowPlayer() + 1);
			roomDto.setNowRound(roomDto.getNowRound() + 1);
		}
		playerStatusUpdate("PlayerStatus",sendDto.getRoomNum());
	}
	
}
