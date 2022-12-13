package kh.spring.endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.common.collect.EvictingQueue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kh.spring.configurator.WSConfigurator;

@ServerEndpoint(value="/chat", configurator = WSConfigurator.class)
public class ChatEndPoint {
	
	// 접속한 사용자 Session을 모아두는 컬렉션
	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
	// 동시성 오류 방지
	private static EvictingQueue<JsonObject> lately = EvictingQueue.create(30);
	private Gson g = new Gson();
		
	// 접속자의 HttpSession 객체를 저장할 멤버필드
	private HttpSession hsession;
	
	// WebSocket이 처음 연결 되었을 때 실행할 함수.
	@OnOpen
	public void onConnection(Session client, EndpointConfig config) {
		System.out.println("웹 소켓 연결 확인");
		clients.add(client);
		
		this.hsession = (HttpSession) config.getUserProperties().get("hSession");
		
		String latelyMessages = g.toJson(lately);
		System.out.println(latelyMessages);
		
		try {
			client.getBasicRemote().sendText(latelyMessages);
		}catch(Exception e) {}
	}
	
	@OnMessage
	public void onMessage(String msg) {
		
		
		msg = msg.replace("<", "&lt;"); // script효과차단
		
		JsonObject data = new JsonObject();
		data.addProperty("ip", (String) this.hsession.getAttribute("IP"));
		data.addProperty("sender", (String) this.hsession.getAttribute("loginID"));
		data.addProperty("msg", msg);
		
		lately.add(data); // 가장 최근 30개의 메세지를 보관
		
		JsonArray arr = new JsonArray();
		arr.add(data);
		
		// 동시성 오류 방지
		synchronized(clients) {		
			for(Session client : clients) {
				try {
					client.getBasicRemote().sendText(arr.toString());
				} catch (IOException e) {}
			}
		}
	}
	
	@OnClose
	public void onClose(Session client) {
		clients.remove(client);
	}
	
	@OnError
	public void onError(Session client, Throwable t) {
		clients.remove(client);
	}
	
}
