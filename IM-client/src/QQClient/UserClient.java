package QQClient;
import java.io.*;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;
import QQServer.PersonalData;
import javax.swing.JOptionPane;
import QQServer.ChatLog;

public class UserClient {
	private String userName;
	String password;
	private String status;
	int identifier;
	int friendNum;
	InputStream inPure;
	OutputStream outPure;
	DataInputStream in;
	DataOutputStream out;
	ObjectInputStream inObject;
	ObjectOutputStream outObject;
	Vector<Friend> friends;
	PersonalData userData;
	Socket socket;
	public UserClient(String userName,String passw){
		this.userName = userName;
		try{
			socket = new Socket("127.0.0.1",8000);
			in = new DataInputStream(inPure=socket.getInputStream());
			out = new DataOutputStream(outPure=socket.getOutputStream());
			inObject = new ObjectInputStream(inPure);
			outObject = new ObjectOutputStream(outPure);
			out.writeUTF("login"+"userName:"+userName+"password:"+passw);
			out.flush();
			//outObject.flush();
			String str;
			System.out.println("发送登陆命令");
			str=in.readUTF();
			System.out.println("得到回复");
			if(str.equals("wrongUserName")){
				status="wrongUserName";
			}
			else if(str.equals("wrongPassw")){
				status="wrongPassw";
			}
			else{
				status="success";
				this.password = passw;
			}
		}
		catch(UnknownHostException e){
			System.out.println("wrong host");
		}
		catch(Exception e){
			e.printStackTrace();
			status="networkError";
		}
	    finally{
	    }
	}
	public static String signIn(String userName,String passw){//注册账户
		try{
			Socket socket = new Socket("127.0.0.1",8000);
			InputStream inPure;
			OutputStream outPure;
			ObjectInputStream inObject;
			ObjectOutputStream outObject;
			DataInputStream in = new DataInputStream(inPure=socket.getInputStream());
			DataOutputStream out = new DataOutputStream(outPure=socket.getOutputStream());
			inObject = new ObjectInputStream(inPure);
			outObject = new ObjectOutputStream(outPure);
			System.out.println("发送注册命令前");
			out.writeUTF("signIn"+"userName:"+userName+"password:"+passw);
			out.flush();
			String str;
			System.out.println("发送注册命令");
			str=in.readUTF();
			System.out.println("得到回复");
			in.close();
			out.close();
			socket.close();
			System.out.println(str);
			if(str == null){
				return "未知错误";
			}
			else if(str.equals("wrongUserName")){
				return "wrongUserName";
			}
			else if(str.equals("unknown")){
				return "insert wrong";
			}
			else{
				return "success";
			}
		}
		catch(UnknownHostException e){
			System.out.println("wrong host");
		}
		catch(Exception e){
			e.printStackTrace();
			return "networkError";
		}
	    finally{
	    }
		return null;
	}
	public PersonalData getPersonalData(){
			try{
				out.writeUTF("getPersonData"+"userName:"+userName);
				out.flush();
				
				System.out.println("等待接收对象");
				Object o = inObject.readObject();
				System.out.println("接收完毕");
				userData=(PersonalData) o;
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return userData;
	}
	public PersonalData getFriendData(String friendName){
		PersonalData friendData = null;
		try{
			out.writeUTF("getPersonData"+"userName:"+friendName);
			out.flush();
			
			System.out.println("等待接收对象");
			Object o = inObject.readObject();
			System.out.println("接收完毕");
			friendData=(PersonalData) o;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return friendData;
}
	public String fixPersonalData(PersonalData data){
			try{
				out.writeUTF("fixPersonData"+"userName:"+userName);
				out.flush();
				outObject.writeObject(data);
				outObject.flush();
				if(in.readUTF().equals("success")){
					return "success";
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return "fail";
	}
	public String getChatLog(String friendName){//得到聊天记录
		try{
			out.writeUTF("getChatLog"+"userName:"+userName+"friendName:"+friendName);
			out.flush();
			Friend ff=new Friend(friendName);
			for(int i=0;i<friends.size();i++){
				if(friendName.equals(((Friend)friends.elementAt(i)).userName)){
					ff=(Friend)friends.elementAt(i);
				}
			}
			String str="";
			ChatLog[] chatlogs=ff.chatLogs=(ChatLog[]) inObject.readObject();
			if(ff.chatLogs==null){
				return "";
			}
			else{
				for(int i=0;i<ff.chatLogs.length;i++){
					if((chatlogs[i]).from.equals(friendName)){
						str+=chatlogs[i].chatTime+"\n"+chatlogs[i].chatContent+"\n";
					}
					else{
						str+="\t\t\t"+chatlogs[i].chatTime+"\n\t\t\t"+chatlogs[i].chatContent+"\n";
					}
				}
				return str;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "fail";
	}
	public String addFriend(String friendName){//加好友
		try{
			out.writeUTF("addFriend"+"userName:"+userName+"friendName:"+friendName);
			out.flush();
			String result ="fail";
			result = in.readUTF();
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "fail";
	}
	public String sendMessage(String message){
		try{
			out.writeUTF("sendMessage"+message);
			out.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "fail";
}
	public String getStatus(){
		return status;
	}
	public String getUserName(){
		return userName;
	}
	public String[] getFriends(){
		//if(friends == null){
			friends = new Vector<Friend>();
			try{
				out.writeUTF("getFriends"+"userName:"+userName);
				out.flush();
				String re = in.readUTF();
				if(re.equals("wrong")){
					return new String[]{"null"};
				}
				int i,i1,j1;
				i1=j1=0;
				i1 = re.indexOf("||"); 
				i=Integer.parseInt(re.substring(0, i1));
				for(int h=0;h<i;h++){
					j1=re.indexOf("||", i1+1);
					System.out.println(i1+"/"+j1+"/");
					friends.addElement(new Friend(re.substring(i1+2, j1)));
					i1=j1;
				}
			}
			catch(Exception e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "get friendwrong");
			}
		//}
		String[] friendsName = new String[friends.size()];
		for(int i=0;i<friends.size();i++){
			friendsName[i]=((Friend)friends.elementAt(i)).userName;
		}
		return friendsName;
	}
}

