package service;

import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;

import controller.ServerSocket;
import model.dto.MemberDto;
import model.dto.SendDto;

public class GameException {
	public void playerStatusUpdate(String _type,int rno) {

		Vector<MemberDto> res = new Vector<>();
		for(MemberDto mdt : ServerSocket.roomList.get(rno).getMemberVector()){
			res.add(MemberDto.builder()
					.mid(mdt.getMid())
					.isTurn(mdt.isTurn())
					.winCount(mdt.getWinCount())
					.build());
		}
		SendDto<Vector<MemberDto>> resSend = SendDto.<Vector<MemberDto>>builder()
				.data(res)
				.roomNum(rno)
				.type(_type)
				.build();
		String jsonDto;
		try {
			jsonDto = new ObjectMapper().writeValueAsString(resSend);
		} catch (Exception e) {
			return;
		}
		
		ServerSocket.roomList.get(rno).getMemberVector().forEach(m->{
			try {
				m.getMemSession().getBasicRemote().sendText(jsonDto);
			} catch (Exception e2) {
				// TODO: handle exception
				System.out.println("멤버 정보 전송 에러"+e2);
				return;
			}
		});
	}
	
	public boolean turnException(int rno,String mid){
		try {
			boolean chk = false;
			for(MemberDto mem : ServerSocket.roomList.get(rno).getMemberVector()){
				if(mem.getMid().equals(mid)){
					chk = mem.isTurn();
				}
			}
			return chk;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("턴 예외 에러"+e);
		}
		
		return false;
	}
}
