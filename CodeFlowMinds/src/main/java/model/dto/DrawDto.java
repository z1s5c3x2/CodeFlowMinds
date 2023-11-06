package model.dto;

import java.util.ArrayList;

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
public class DrawDto {
	
	private ArrayList<String> dragList;
	private String brushColor;
	private String brushWidth;
}
