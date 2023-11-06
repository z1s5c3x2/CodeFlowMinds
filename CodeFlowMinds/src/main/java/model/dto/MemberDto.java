package model.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.websocket.Session;

@Getter 
@Setter
@ToString 
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
	
	private String mid;
	private String mpwd;
	private int winCount;
	private Session memSession;
	private boolean isTurn;
	private boolean isReady;
	
	
}
