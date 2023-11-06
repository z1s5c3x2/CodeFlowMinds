<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

</head>
<body>
<!-- 그림판시작 -->
		
        <canvas id="canvas">
        
        </canvas>
        	<img class='imgtest'/>
        <div class="tools">
            <!-- 컬러칩 -->
            <div onclick="change_color(this)" class="color-field" style="background: red;"></div>
            <div onclick="change_color(this)" class="color-field" style="background: blue;"></div>
            <div onclick="change_color(this)" class="color-field" style="background: green;"></div>
            <div onclick="change_color(this)" class="color-field" style="background: yellow;"></div>

             <!-- 컬러피커  -->
            <input onInput="draw_color= this.value" type="color" class="color-picker" style="width:70px">
            <!-- 펜굵기 조절 -->
            <input type="range" min="1" max="100" class="pen-range"
                  onInput="draw_width=this.value">
            <!-- 되돌리기, 지우기, 다운로드 버튼  -->
            <button onclick="undo_last()"type="button" class="button">되돌리기</button>
            <button onclick="clear_canvas()" type="button" class="button">지우기</button>
            <button onclick="save()" type="button" class="button">다운로드</button>
        </div>	
		<button onclick="test()"> 받기 테스트</button>
		<button onclick="deltest()"> 지우기 테스트</button>

	<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script src="draw2.js" type="text/javascript"></script>
</body>
</html>