package QQServer;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.*;

public class QQServer extends JFrame{
	Dimension screenSize;
	TextArea txt;
	DatabaseController database;
	public QQServer(){//设置界面
		super("服务器");
		screenSize =Toolkit.getDefaultToolkit().getScreenSize();
    	this.setSize(screenSize.width/2, screenSize.height/3);
    	this.setLocationRelativeTo(null);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	txt = new TextArea();
    	add(new JScrollPane(txt));
    	setVisible(true);
    	database = new DatabaseController("newchatlog","chatdatabase","123456");
    	setLog("数据库已经连接");
    	try{
    		ServerSocket serverSocket = new ServerSocket(8000);
    		setLog("服务器启动，等待连接");
    		while(true){
    			Socket socket = serverSocket.accept();
    			setLog("连接成功");
    			HandleAClient task = new HandleAClient(socket);
    			new Thread(task).start();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	public void setLog(String str){//设置textlog的字
		txt.setText(txt.getText()+"\n"+str+"\t"+new Date());
	}
	public static void main(String[] args) {//启动界面
		QQServer myServer = new QQServer();

	}
	class HandleAClient implements Runnable{//服客户的线程
		private Socket socket;
		private InetAddress address;
		InputStream inPure;
		OutputStream outPure;
		DataInputStream in;
		DataOutputStream out;
		ObjectInputStream inObject;
		ObjectOutputStream outObject;
		String user;
		Boolean isUpdate;
		public HandleAClient(Socket socket){//建立流
			this.socket = socket;
			address = socket.getInetAddress();
			try{
				out = new DataOutputStream(outPure=socket.getOutputStream());
				in = new DataInputStream(inPure=socket.getInputStream());
				outObject = new ObjectOutputStream(outPure);
				inObject = new ObjectInputStream(inPure);
				isUpdate = false;
				//outObject = new ObjectOutputStream(socket.getOutputStream());
				//outObject.flush();
			}catch(Exception e){
				System.out.println("建立输入输出流失败");
			}
		}
		public void run(){
			String str;
			
			try{
				while(true){
					setLog("等待客户端命令");
					str=in.readUTF();//处理命令
					setLog(str);
					setLog("处理客户端命令");
					if(str.indexOf("login")>=0){
						login(str.substring(5));
					}
					else if(str.indexOf("signIn")>=0){
						signIn(str.substring(6));
					}
					else if(str.indexOf("getPersonData")>=0){
						sendData(str.substring(13));
					}
					else if(str.indexOf("fixPersonData")>=0){
						fixData(str.substring(13));
					}
					else if(str.indexOf("getFriends")>=0){
						getFriends(str.substring(10));
					}
					else if(str.indexOf("sendMessage")>=0){
						sendMessage(str.substring(11));
					}
					else if(str.indexOf("getChatLog")>=0){
						getChatLog(str.substring(10));
					}
					else if(str.indexOf("addFriend")>=0){
						addFriend(str.substring(9));
					}
				}
			}catch(Exception e){
				setLog("失去Client连接");
			}
		}
		public void addFriend(String str){//得到聊天记录
			setLog(str);
			int i,j;
			String userName,friendName;
			i = str.indexOf("userName:")+9;
			j = str.indexOf("friendName:");
			userName = str.substring(i,j);
			j+=11;
			friendName = str.substring(j);
			try{
				String re;
				out.writeUTF(re = database.addFriend(userName, friendName));
				//setLog();
				out.flush();
			}catch(Exception e){
				System.out.println("输入到客户失败");
			}
		}
		public void getChatLog(String str){//得到聊天记录
			setLog(str);
			int i,j;
			String userName,friendName;
			i = str.indexOf("userName:")+9;
			j = str.indexOf("friendName:");
			userName = str.substring(i,j);
			j+=11;
			friendName = str.substring(j);
			try{
				ChatLog[] chatLogs;
				outObject.writeObject(chatLogs = database.getChatLogFromName(userName, friendName));
				//setLog();
				outObject.flush();
			}catch(Exception e){
				System.out.println("输入到客户失败");
			}
		}
		public void sendMessage(String str){//注册处理
			setLog(str);
			int i,j;
			String from,to,content;
			i = str.indexOf("fromUserName:")+13;
			j = str.indexOf("toUserName:");
			from = str.substring(i, j);
			i=j+11;
			j= str.indexOf("content:");
			to = str.substring(i,j);
			content = str.substring(j+8);
			try{
				String str1 = database.insertNewMessage(from,to,content);
				setLog("存消息成功"+str);
			}catch(Exception e){
				System.out.println("存消息失败");
			}
			finally{
			}
		}
		public void getFriends(String str){//得到好友列表
			setLog(str);
			int i;
			String userName;
			i = str.indexOf("userName:")+9;
			userName = str.substring(i);
			try{
				String ss = database.getFriendsFromUserName(userName);
				setLog(ss);
				if(ss!=null){
					out.writeUTF(ss);
				}else{
					out.writeUTF("wrong");
				}
				out.flush();
			}catch(Exception e){
				System.out.println("输入到客户失败");
			}
		}
		public void sendData(String str){//查看个人资料
			setLog(str);
			int i;
			String userName;
			i = str.indexOf("userName:")+9;
			userName = str.substring(i);
			try{
				PersonalData ss;
				outObject.writeObject(ss = database.getDataFromUserName(userName));
				setLog(ss.nickName+ss.address);
				outObject.flush();
			}catch(Exception e){
				System.out.println("输入到客户失败");
			}
		}
		public void fixData(String str){//修改个人资料
			setLog(str);
			int i;
			String userName;
			i = str.indexOf("userName:")+9;
			userName = str.substring(i);
			try{
				PersonalData ss = (PersonalData)inObject.readObject();
				if(database.UpdataUserData(ss,userName).equals("success")){
					out.writeUTF("success");
					out.flush();
				}else{
					out.writeUTF("fail");
					out.flush();
				}
			}catch(Exception e){
				System.out.println("输入到客户失败");
			}
		}
		public void login(String str){//登陆处理
			setLog(str);
			int i,j;
			String userName,passw;
			i = str.indexOf("userName:")+9;
			j = str.indexOf("password:");
			userName = str.substring(i, j);
			j+=9;
			passw = str.substring(j);
			try{
				if(database.getPasswordFromUserName(userName) == null){
					out.writeUTF("wrongUserName");
				}
				else if(database.getPasswordFromUserName(userName).equals(passw)){
					out.writeUTF("success");
					setLog("用户："+userName+"登陆成功");
					this.user = userName;
				}
				else {
					out.writeUTF("wrongPassw");
				}
				out.flush();
			}catch(Exception e){
				System.out.println("输入到客户失败");
			}
		}
		public void signIn(String str){//注册处理
			setLog(str);
			int i,j;
			String userName,passw;
			i = str.indexOf("userName:")+9;
			j = str.indexOf("password:");
			userName = str.substring(i, j);
			j+=9;
			passw = str.substring(j);
			try{
				String str1 = database.insertNewUser(userName,passw);
				if(str1 == null){
					out.writeUTF("unknown");
				}
				else if(str1.equals("wrongUserName")){
					out.writeUTF("wrongUserName");
					setLog("用户："+userName+"注册失败");
				}
				else {
					out.writeUTF("success");
					setLog("用户："+userName+"注册成功");
				}
				out.flush();
			}catch(Exception e){
				System.out.println("输入到客户失败");
			}
			finally{
				try{
					out.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

}
