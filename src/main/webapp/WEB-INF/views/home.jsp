<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<style>
	div{
		
	}
	.container{
		width:500px;
		height: 500px;
		margin: auto;
		border:1px solid black;		
	}
	.message-area {
		width : 100%;
		height : 90%;
		border:1px solid black;
	}
	#chatLog{
		width: 100%;
		height:90%;
		overflow-y:scroll;
		border:1px solid black;
	}
	.input-area{
		width: 100%;
		height:10%;
		border:1px solid black;		
	}
	.msg-box {
   		max-width: 250px;
   		word-wrap: break-word;
   		border: 1px dotted black;
   		border-radius: 5px;
   		margin: 5px;
   		padding: 5px;
   		display: inline-block;
	}
</style>
<script src="https://code.jquery.com/jquery-3.6.1.js">
</script>
<meta charset="UTF-8" />
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Home</title>
<script>
	$(function(){
		
		 function updateScroll(){
		      var element = document.getElementsByClassName("message-area")[0];
		       element.scrollTop = element.scrollHeight;
		 }
		
		let ws = new WebSocket("ws://192.168.50.50/chat"); //WebSocket 인스턴스 생성
			
		ws.onmessage = function(e) {
			console.log(e.data);
			let data = JSON.parse(e.data);
			
			for (let i =0; i<data.length; i++) {
				
				console.log(data[i]);
				//let msgJSON = JSON.parse(data[i]);
				let outer = $("<div>");
				let line = $("<div>");
				line.addClass("msg-box");
				
				line.append(data[i].msg);				
				
				outer.append(line);
				$(".message-area").append(outer);
				updateScroll();
			}			
			
	         
		}
		
		$(".input-area").on("keydown", function(e) {
			if (e.keyCode==13) {
				let text = $(".input-area").text();
				$(".input-area").text("");
				ws.send(text);
				
				return false;
			}
		});
		
	});
</script>
</head>
<body>
	<div class="container">
		<div id="chatLog" class="message-area">
		</div>
		<div id="chatTyping" class="input-area" contenteditable="true">
		</div>
	</div>
</body>
</html>