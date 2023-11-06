package model.dto;

import java.util.Vector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class RoomDto {

	private int roomNum;
	@Default
	private int nowRound = 0;
	private int allRound;
	private int limitPlayer;
	@Default
	private int nowPlayer = 0;
	@Default
	private int readyCount = 0;
	// 방비밀번호
	private String pwd;
	//방 문제
	@Default
	private String roomQuestion = "test";
	// 방 현재 상태
	@Default
	private boolean nowStatus = true;
	// 벡터 필드 아이디랑 맞춘 회수
	private Vector< MemberDto > memberVector;
}


