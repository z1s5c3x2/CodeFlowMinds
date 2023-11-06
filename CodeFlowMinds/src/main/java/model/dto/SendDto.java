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
public class SendDto<T> {
	private String serviceType;
	private int roomNum;
	private String type;
	private T data;
}
