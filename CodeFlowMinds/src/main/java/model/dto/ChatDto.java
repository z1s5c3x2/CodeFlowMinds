package model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class ChatDto {
	private String mid;
	private String text;
	private String date;
}
