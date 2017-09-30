package QQClient;
import javax.swing.*;
import QQServer.PersonalData;

import java.awt.*;
import java.awt.event.*;
public class QQClient extends JFrame{//jrame
	JTextField userText;
	JTextField passwText;
	JFrame dialogueFrame;
	Dimension screenSize;
	UserClient user;
	TextArea txt;
	TextArea textSend;
	JList<String> friends;
    public QQClient(){
    	super("客户端");
    	screenSize =Toolkit.getDefaultToolkit().getScreenSize();
    	this.setSize(screenSize.width/4, screenSize.height/3);
    	this.setLocationRelativeTo(null);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLayout(new BorderLayout(0,0));
    	JPanel inputUserPassw = new JPanel();
    	inputUserPassw.setLayout(new GridLayout(2,2));
    	inputUserPassw.add(new JLabel("用户名："));
    	userText = new JTextField(5);
    	userText.setSize(5, 10);//
    	//JPanel p1 = new JPanel();
    	//p1.add(userText);
    	//p1.setLayout(new FlowLayout());
    	inputUserPassw.add(userText);
    	inputUserPassw.add(new JLabel("密码："));
    	passwText = new JTextField(5);
    	inputUserPassw.add(passwText);
    	add(inputUserPassw,BorderLayout.CENTER);
    	JButton login = new JButton("登陆");
    	JButton signIn = new JButton("注册");
    	JPanel buttonPanel = new JPanel();
    	buttonPanel.setLayout(new FlowLayout());
    	buttonPanel.add(login);
    	buttonPanel.add(signIn);
    	TwoButtonListener action;
    	login.addActionListener(action = new TwoButtonListener());//登陆触发
    	signIn.addActionListener(action);
    	login.setSize(10,20);
    	add(buttonPanel,BorderLayout.SOUTH);
    	setVisible(true);
    	
    }
    public void dialogueSet(){//会话框设置
    	dialogueFrame.setTitle("客户端   用户名："+user.getUserName());
    	dialogueFrame.setSize(screenSize.width/2, (int) (screenSize.height/1.5));
    	dialogueFrame.setLocationRelativeTo(null);
    	dialogueFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	dialogueFrame.setLayout(new BorderLayout(0,0));
    	JPanel p1 = new JPanel();//上面的搜索框和资料
    	p1.setLayout(new FlowLayout());
    	JButton friendInfor = new JButton("好友资料");
    	JButton personInfor = new JButton("个人资料");
    	JButton addFriend = new JButton("加好友");
    	addFriend.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e){
        		String friendName = JOptionPane.showInputDialog("输入一个存在的用户名");
        		if(friendName.equals(user.getUserName())){
        			JOptionPane.showMessageDialog(null, "不能加自己为好友");
        			return ;
        		}
                if(friendName.length()>0){
                	String re = user.addFriend(friendName);
                	if(re.equals("success")){
                		JOptionPane.showMessageDialog(null, "加好友成功");
                	}else if(re.equals("wrongName")){
                		JOptionPane.showMessageDialog(null, "无此用户名");
                	}else{
                		JOptionPane.showMessageDialog(null, "网络错误");
                	}
                    if(friends.getSelectedIndex()==-1){
                    	friends.setListData(user.getFriends());
                    }
                    else{
                    	String newff = friends.getSelectedValue();
                    	friends.setListData(user.getFriends());
                    	friends.setSelectedValue(newff, true);
                    }
                }
        	}
    	});
    	JButton sendData = new JButton("发送");
    	InforButtonListener inforButtonListen = new InforButtonListener();
    	friendInfor.addActionListener(inforButtonListen);
    	personInfor.addActionListener(inforButtonListen);
    	JTextField searchFriend = new JTextField(5);
    	p1.add(searchFriend);
    	p1.add(friendInfor);
    	p1.add(personInfor);
    	p1.add(addFriend);
    	p1.add(sendData);
    	sendData.addActionListener(new SendButtonListener());
    	JPanel p2 = new JPanel();
    	txt = new TextArea();
    	p2.setLayout(new GridLayout(2,1));
    	p2.add(new JScrollPane(txt));
    	textSend = new TextArea();
    	p2.add(textSend);
    	dialogueFrame.add(p1, BorderLayout.NORTH);
    	dialogueFrame.add(p2, BorderLayout.CENTER);
    	friends = new JList(user.getFriends());
    	friends.addMouseListener(new MouseListener(){
    		public void mouseReleased(MouseEvent e){
    			if(friends.getSelectedIndex()==-1){
        			JOptionPane.showMessageDialog(null, "请选择一个好友");
        			return ;
        		}
    			else{
    				refreshTxt(friends.getSelectedValue());
    			}
    		}
    		public void mousePressed(MouseEvent e){		
    		}
    		public void mouseClicked(MouseEvent e){
    		}
    		public void mouseEntered(MouseEvent e){	
    		}
    		public void mouseExited(MouseEvent e){	
    		}
    	});
    	dialogueFrame.add(new JScrollPane(friends), BorderLayout.WEST);
    	dialogueFrame.setVisible(true);
    	RefreshMessages task = new RefreshMessages();
		new Thread(task).start();
    }
    class RefreshMessages implements Runnable{
    	/*TextArea txt;
    	JList<String> friends;
    	public RefreshMessages(TextArea txt,JList<String> friends){
    		this.txt=txt;
    		this.friends=friends;
    	}*/
    	public void run(){
    		int i=0;
    		while(true){
    			i++;
    			if(friends.getSelectedIndex()==-1){
    			}
    			else{
    				refreshTxt(friends.getSelectedValue());
    			}
    			if(i>2){
    				if(friends.getSelectedIndex()==-1){
                    	friends.setListData(user.getFriends());
                    }
                    else{
                    	String newff = friends.getSelectedValue();
                    	friends.setListData(user.getFriends());
                    	friends.setSelectedValue(newff, true);
                    }
    				i=0;
    			}
    			try{
    				Thread.sleep(1500);
    			}catch(Exception e){
    				System.out.println("刷新txt线程错误");
    			}
    		}
    	}
    }
    public void refreshTxt(String friendName){
    	String str =user.getChatLog(friendName);
    	txt.setText(str);
    }
    class SendButtonListener implements ActionListener{//资料按钮
    	public void actionPerformed(ActionEvent e){
    		if(friends.getSelectedIndex()==-1){
    			JOptionPane.showMessageDialog(null, "请选择一个好友");
    			return ;
    		}
    		String str = "fromUserName:"+user.getUserName()+"toUserName:"+friends.getSelectedValue()+"content:"+textSend.getText();
    		user.sendMessage(str);
    		textSend.setText("");
    		refreshTxt(friends.getSelectedValue());
    	}
    }
    public void login(){//
    	user = new UserClient(userText.getText(),passwText.getText());
    	if(user.getStatus().equals("success")){
    		dialogueFrame = new JFrame();
    		dialogueSet();
    		setVisible(false);
    		System.out.println("login success");
    	}
    	else if (user.getStatus().equals("wrongUserName")){
    		JOptionPane.showMessageDialog(null, "用户名错误");
    	}
    	else if (user.getStatus().equals("wrongPassw")){
    		JOptionPane.showMessageDialog(null, "密码错误");
    	}else {
    		JOptionPane.showMessageDialog(null, "网络错误");
    	}
    }
    public void signIn(){//注册
    	if(userText.getText().length()<6||passwText.getText().length()<6){
    		JOptionPane.showMessageDialog(null, "用户名或密码太短");
    		return ;
    	}
    	String str = UserClient.signIn(userText.getText(),passwText.getText());
    	System.out.println(str);
    	if(str.equals("success")){
    		login();
    		JOptionPane.showMessageDialog(null, "注册成功");
    	}
    	else if (str.equals("wrongUserName")){
    		JOptionPane.showMessageDialog(null, "用户名重复");
    	}
    	else {
    		JOptionPane.showMessageDialog(null, "网络错误");
    	}
    }
    class InforButtonListener implements ActionListener{//资料按钮
    	public void actionPerformed(ActionEvent e){
    		if(((JButton)e.getSource()).getText().equals("个人资料")){
    			PersonalData userData = user.getPersonalData();
    			if(userData !=null){
    				showUserData(userData);
    			}
    			else{
    				JOptionPane.showMessageDialog(null, "得到data为null");
    			}
    		}
    		else if(((JButton)e.getSource()).getText().equals("好友资料")){
    			if(friends.getSelectedIndex()==-1){
        			JOptionPane.showMessageDialog(null, "请选择一个好友");
        			return ;
        		}else{
        			PersonalData friendData = user.getFriendData(friends.getSelectedValue());
        			if(friendData == null){
        				JOptionPane.showMessageDialog(null, "网络错误");
        			}else{
        				showFriendData(friendData);
        			}
        		}
    		}
    	}
    }
    class TwoButtonListener implements ActionListener{//登陆按钮
    	public void actionPerformed(ActionEvent e){
    		if(((JButton)e.getSource()).getText().equals("登陆"))
    			login();
    		else
    			signIn();
    	}
    }
	public static void main(String[] args){
		QQClient myClient = new QQClient();
	}
    public void showUserData(PersonalData data){
    	JFrame showmyData = showFriendData(data);
    	JButton fix=new JButton("修改");
    	Component[] coms=showmyData.getContentPane().getComponents();
    	fix.addActionListener(new ActionListener(){//修改资料
    		public void actionPerformed(ActionEvent e){
    			String nickName = ((JTextField)coms[1]).getText()=="null" ? null:((JTextField)coms[1]).getText();
    			String remark = ((JTextField)coms[3]).getText()=="null" ? null:((JTextField)coms[3]).getText();
    			String sex = ((JTextField)coms[5]).getText()=="null" ? null:((JTextField)coms[5]).getText();
    			int age =0;
    			if(((JTextField)coms[7]).getText()=="null"){
    		         age = 0;
    			}else{
    				age = Integer.parseInt(((JTextField)coms[7]).getText());
    			}
    			String telephone = ((JTextField)coms[9]).getText()=="null" ? null:((JTextField)coms[9]).getText();
    			String email = ((JTextField)coms[11]).getText()=="null" ? null:((JTextField)coms[11]).getText();
    			String address = ((JTextField)coms[13]).getText()=="null" ? null:((JTextField)coms[13]).getText();
    			PersonalData fixData = new PersonalData( nickName, remark, sex, age, telephone, email, address);
    			if(user.fixPersonalData(fixData).equals("success")){
    				JOptionPane.showMessageDialog(null, "修改成功");
    				showmyData.dispose();
    			}
    			else{
    				JOptionPane.showMessageDialog(null, "修改失败");
    			}
    		}
    	});
    	showmyData.add(fix);
    }
    public JFrame showFriendData(PersonalData data){
    	JFrame showFriendDataFrame = new JFrame();
    	JLabel jLabel;
    	JTextField text;
    	showFriendDataFrame.setSize(200, 200);
    	showFriendDataFrame.setLocationRelativeTo(null);
    	showFriendDataFrame.setLayout(new FlowLayout(FlowLayout.CENTER,10,5));
    	jLabel = new JLabel("nick name:");
    	jLabel.setSize(40, 10);
    	text = new JTextField();
    	if(data.nickName==null){
    		text.setText("null");
    	}
    	else{
    		text.setText(data.nickName);
    	}
    	text.setSize(100,20);
    	showFriendDataFrame.add(jLabel);
    	showFriendDataFrame.add(text);
    	jLabel = new JLabel("remark:");
    	jLabel.setSize(40, 10);
    	text = new JTextField();
    	if(data.remark==null){
    		text.setText("null");
    	}
    	else{
    		text.setText(data.remark);
    	}
    	text.setSize(100,20);
    	showFriendDataFrame.add(jLabel);
    	showFriendDataFrame.add(text);
    	jLabel = new JLabel("sex:");
    	jLabel.setSize(40, 10);
    	text = new JTextField();
    	if(data.sex==null){
    		text.setText("null");
    	}
    	else{
    		text.setText(data.sex+"");
    	}
    	showFriendDataFrame.add(jLabel);
    	showFriendDataFrame.add(text);
    	jLabel = new JLabel("age:");
    	jLabel.setSize(40, 10);
    	text = new JTextField();
    	if(data.age==0){
    		text.setText("null");
    	}
    	else{
    		text.setText(data.age+"");
    	}
    	showFriendDataFrame.add(jLabel);
    	showFriendDataFrame.add(text);
    	jLabel = new JLabel("telephone:");
    	jLabel.setSize(40, 10);
    	text = new JTextField();
    	if(data.telephone==null){
    		text.setText("null");
    	}
    	else{
    		text.setText(data.telephone+"");
    	}
    	showFriendDataFrame.add(jLabel);
    	showFriendDataFrame.add(text);
    	jLabel = new JLabel("email:");
    	jLabel.setSize(40, 10);
    	text = new JTextField();
    	if(data.email==null){
    		text.setText("null");
    	}
    	else{
    		text.setText(data.email+"");
    	}
    	showFriendDataFrame.add(jLabel);
    	showFriendDataFrame.add(text);
    	jLabel = new JLabel("address:");
    	jLabel.setSize(40, 10);
    	text = new JTextField();
    	if(data.address==null){
    		text.setText("null");
    	}
    	else{
    		text.setText(data.address+"");
    	}
    	showFriendDataFrame.add(jLabel);
    	showFriendDataFrame.add(text);
    	showFriendDataFrame.setVisible(true);
    	return showFriendDataFrame;
    }
}
