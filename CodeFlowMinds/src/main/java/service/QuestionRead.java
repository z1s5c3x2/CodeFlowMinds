package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class QuestionRead {
	private static QuestionRead instance = new QuestionRead();
	public static QuestionRead getInstance() {return instance;}
	private QuestionRead () {}
	private final String filePath = this.getClass().getResource("/txtFile/").getPath();
	public static ArrayList<String> categoryList = new ArrayList<>();
	public String getQuestion(int category){
		try {
			//현재 작업 경로 가져오기
			BufferedReader br = new BufferedReader(new FileReader(filePath+categoryList.get(category-1)+".txt"));;

			String _line =  br.readLine();
			String [] ql = _line.split(",");
			
			br.close();
			return ql[(int)(Math.random()*ql.length-1)];
			
			
		} catch (Exception e) {
			System.out.println("문제 읽기 에러" + e);
		}
		 return null;
	}
}
