/*
           7.18         在线人数统计 返回在线用户列表
 */
package intellif.database.entity;

import org.hibernate.validator.constraints.NotEmpty;

import intellif.database.entity.UserInfo;

import javax.persistence.*;

import java.io.Serializable;

@Entity

public class OnLineUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
   

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 用户姓名
    @NotEmpty
    private String name;


	// ip
    @NotEmpty
    private String ip;
    
    

    // 登录时间
    @NotEmpty
    private String time;
    

    // 职务
   
    private String post;
    

    // 账号类型
  
    private String accounttype;
    

    // 所属派出所ID
   
    private long policeStationId;
    
    //登陆账号
    //@JsonIgnore
    private String owner;

  
    
    public OnLineUserInfo() {
    }

    
    public OnLineUserInfo(UserInfo userInfo) {
       // super();
        this.id = userInfo.getId();
        this.name = userInfo.getName(); 
		this.post = userInfo.getPost();
		this.policeStationId = userInfo.getPoliceStationId();
		this.owner=userInfo.getLogin();
		
    }

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

 



   
    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public long getPoliceStationId() {
        return policeStationId;
    }

    public void setPoliceStationId(long policeStationId) {
        this.policeStationId = policeStationId;
    }

 

   /* @Override
    public String toString() {
        return "OnLineUserInfo{" +
                "id=" + id +'\''+",name='"+name+'\''+
                ", ip='" + ip + '\'' +",time='"+time+'\''+",accounttype='"+accounttype+'\''+
                ", post='" + post + '\'' +
                ", policeStationId=" + policeStationId + '\'' +
                '}';
    }
*/

	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String string) {
		this.time = string;
	}


	public String getAccounttype() {
		return accounttype;
	}


	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}

  
    
  
    
}
