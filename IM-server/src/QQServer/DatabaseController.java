package QQServer;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;

import javax.swing.JOptionPane;

import javax.swing.*;
public class DatabaseController {
	private Connection connection;
	private Statement statement;
	private DatabaseMetaData dbMetaData;
	public DatabaseController(String s1,String s2,String s3){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("driver loaded");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"+s1,s2,s3);
			System.out.println("database connection connected");
			statement = connection.createStatement();
			dbMetaData = connection.getMetaData();
		}catch(ClassNotFoundException e){
			System.out.println("class not found");
		}catch(SQLException e){
			System.out.println("Connect to database failed.");
		}
	}
	public String getPasswordFromUserName(String s1){  //查找方法
		String s2 = "select password from users where username = '"+s1+"'";
		try{
			ResultSet resultset = statement.executeQuery(s2);
			if(resultset.next())
				return s1=resultset.getString(1);
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}
		return null;
	}
	public String getFriendsFromUserName(String s1){  //查找方法
		String s2 = "select friendname from friends where username = '"+s1+"'";
		try{
			ResultSet resultset = statement.executeQuery(s2);
			int i =0;
			String str = "||";
			while(resultset.next()){
				i++;
				str += resultset.getString(1)+"||";
			}
			str= i+str;
			return str;
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}
		return null;
	}
	public PersonalData getDataFromUserName(String s1){
		String s2 = "select nickname,remark,sex,age,telephone,email,address from users where username = '"+s1+"'";
		try{
			ResultSet resultset = statement.executeQuery(s2);
			if(resultset.next())
				return new PersonalData(resultset.getString(1),resultset.getString(2),resultset.getString(3),resultset.getInt(4),resultset.getString(5),resultset.getString(6),resultset.getString(7));
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}
		return null;
	}
	public ChatLog[] getChatLogFromName(String userName,String friendName){//查询数据库聊天记录
		String s2 = "select senderid,receiverid,date,content from messagetable where (senderid = '"+userName+"' and receiverid ='"+friendName+"') or (senderid  = '"+friendName+"' and "
				+ "receiverid ='"+userName+"')";
		try{
			String from,to,chatTime,chatContent;
			ChatLog tmp;
			Vector<ChatLog> chatlogVs = new Vector<ChatLog>();
			ResultSet resultset = statement.executeQuery(s2);
			int i=0;
			while(resultset.next()){
				from = resultset.getString(1);
				to = resultset.getString(2);
				chatTime = resultset.getString(3);
				chatContent = resultset.getString(4);
				tmp = new ChatLog(from,to,chatTime,chatContent);
				chatlogVs.add(tmp);
				i++;
			}
			ChatLog[] chatlogs = new ChatLog[i];
			for(i=0;i<chatlogVs.size();i++){
				chatlogs[i]=(ChatLog)chatlogVs.get(i);
			}
			return chatlogs;
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}
		return null;
	}
	public String insertNewMessage(String from,String to,String content){  //查找方法
		try{
			ResultSet resultset = statement.executeQuery("select * from messagetable");
			//ResultSetMetaData rs = resultset.getMetaData();
			resultset.last();
			int i = resultset.getRow()+1;
			String s2 = "insert into messagetable(messageid,senderid,receiverid,date,content) values ("+i+",'"+from+"','"+to+"','"+new Date()+"','"+new String(content.getBytes(), "UTF-8")+"')";
			statement.executeUpdate(s2);
			return "success";
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}catch(UnsupportedEncodingException e){
			JOptionPane.showMessageDialog(null, "编码失败");
		}
		return null;
	}
	public String addFriend(String userName,String friendName){  //加好友
		try{
			ResultSet resultset = statement.executeQuery("select * from friends");
			resultset.last();
			int i = resultset.getRow()+1;
			if(isUserExist(friendName)){
				String s2 = "insert into friends(idfriends,username,friendname,date) values ("+i+",'"+userName+"','"+friendName+"','"+new Date()+"')";
				statement.executeUpdate(s2);
				i++;
				s2 = "insert into friends(idfriends,username,friendname,date) values ("+i+",'"+friendName+"','"+userName+"','"+new Date()+"')";
				statement.executeUpdate(s2);
				return "success";
			}
			else{
				return "wrongName";
			}
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}
		return "fail";
	}
	public String insertNewUser(String userName,String passw){  //查找方法
		if(isUserExist(userName)){
			return "wrongUserName";
		}
		try{
			ResultSet resultset = statement.executeQuery("select * from users");
			//ResultSetMetaData rs = resultset.getMetaData();
			resultset.last();
			int i = resultset.getRow()+1;
			String s2 = "insert into users(userid,username,password) values ("+i+",'"+userName+"','"+passw+"')";
			statement.executeUpdate(s2);
			return "success";
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}
		return null;
	}
	public boolean isUserExist(String s1){  //userName是否重复
		String s2 = "select username from users where username = '"+s1+"'";
		try{
			ResultSet resultset = statement.executeQuery(s2);
			if(resultset.next())
				return resultset.getString(1).equals(s1);
			else
				return false;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	public String UpdataUserData(PersonalData data,String userName){
		String s2 = "update users set nickname = '"+data.nickName+"', remark = '"+data.remark+"', sex = '"+data.sex+"', age = "+data.age+",telephone = '"
				+ data.telephone+"', email = '"+data.email+"', address = '"+data.address+"' where username ='"+userName+"'" ;
		try{
			statement.executeUpdate(s2);
			return "success";
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, ""+e.getMessage());
		}
		return "fail";
	}
	public void insertToTable(String s1){  //插入方法
		try{
			statement.executeUpdate("s1");
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("insert failed.");
		}
		System.out.println("insert success.");
	}
	public void deleteToTable(String s1){  //删除方法
		try{
			statement.executeUpdate("s1");
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("delete failed.");
		}
		System.out.println("delete success.");
	}
	public void updateToTable(String s1){  //更新方法
		try{
			statement.executeUpdate("s1");
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("update failed.");
		}
		System.out.println("update success.");
	}
	public void colseConnection(){  //关闭连接
		try{
			connection.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("connection colse failed.");
		}
		System.out.println("colse success.");
	}
	public void getMetaData(){  //得到元数据
		try{
			System.out.println("database URL:"+dbMetaData.getURL());
			System.out.println("database username:"+dbMetaData.getUserName());
			System.out.println("database productname:"+dbMetaData.getDatabaseProductName());
			System.out.println("database productversion:"+dbMetaData.getDatabaseProductVersion());
			System.out.println("max numbers of connection:"+dbMetaData.getMaxConnections());
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("get MetaData failed.");
		}
	}
	public void getTableName(){  //得到所有的table name
		try{
			ResultSet table = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
			System.out.println("User tables:");
			while(table.next()){
				System.out.print(table.getString("TABLE_NAME")+" ");
			}
			System.out.println();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("get table failed.");
		}
	}
	public void getTable(String s1){  //得到某张表格
		try{
			ResultSet resultSet = statement.executeQuery("select * from "+s1);
			ResultSetMetaData rs = resultSet.getMetaData();
			for(int i = 1;i <= rs.getColumnCount();i++){
				System.out.printf("%-12s\t",rs.getColumnName(i));
			}
			System.out.println();
			while(resultSet.next()){
				for(int i = 1;i <= rs.getColumnCount();i++)
					System.out.printf("%-12s\t",resultSet.getObject(i));
				System.out.println();
			}
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("get table failed.");
		}
	}
}
class PersonalData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String nickName;
	String remark;
	String sex;
	int age;
	String telephone;
	String email;
	String address;
	public PersonalData(String nickName,String remark,String sex,int age,String telephone,String email,String address){
		this.nickName = nickName;
		this.remark = remark;
		this.sex = sex;
		this.age = age;
		this.telephone = telephone;
		this.email = email;
		this.address = address;
	}
}
class Friend{
	String name;
	int identifier;
	PersonalData friendData;
	ChatLog[] chatLogs;
	public Friend(){
		
	}
}
class ChatLog implements Serializable{
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
	public ChatLog(String from,String to, String date,String chatContent){
		this.from = from;
		this.to = to;
		this.chatContent = chatContent;
		chatTime = date;
	}
}
