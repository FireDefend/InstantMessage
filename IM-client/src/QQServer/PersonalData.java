package QQServer;

import java.io.Serializable;

public class PersonalData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String nickName;
	public String remark;
	public String sex;
	public int age;
	public String telephone;
	public String email;
	public String address;
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