package QQClient;

import java.util.Vector;
import QQServer.ChatLog;
import QQServer.PersonalData;
public class Friend{
	String userName;
	int identifier;
	PersonalData friendData;
	ChatLog[] chatLogs;
	public Friend(String userName){
		this.userName = userName;
	}
}