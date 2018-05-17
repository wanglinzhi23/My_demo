package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = GlobalConsts.T_NAME_OAUTH_ACCESS_TOKEN,schema=GlobalConsts.INTELLIF_BASE)
public class OauthAccessTokenInfo {

  

    private String tokenId;
    @JsonIgnore
	@Lob
	@Column
	private byte[] token;
    @Id
    private String authentication_id;
    private String user_name;
    private String client_id;
    @JsonIgnore
   	@Lob
   	@Column
    private String authentication;
    private String refresh_token;
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public byte[] getToken() {
		return token;
	}
	public void setToken(byte[] token) {
		this.token = token;
	}
	public String getAuthentication_id() {
		return authentication_id;
	}
	public void setAuthentication_id(String authentication_id) {
		this.authentication_id = authentication_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getAuthentication() {
		return authentication;
	}
	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
    
 
    


}