package QQServer;

import java.io.Serializable;
import java.util.Date;

public class ChatLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String from;
	public String to;
	public String chatTime;
	public String chatContent;
	public ChatLog(String from,String to, String chatContent){
		this.from = from;
		this.to = to;
		this.chatContent = chatContent;
		chatTime = (new Date())+"";
	}
	public ChatLog(String from,String to, String chatContent,String date){
		this.from = from;
		this.to = to;
		this.chatContent = chatContent;
		chatTime = date;
	}
}