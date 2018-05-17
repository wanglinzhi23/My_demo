package intellif.fk.dto;


public class FkLoginDto {

    private static final long serialVersionUID = -1588902803798110245L;

   
    private String user;
  
    private String password;
    
    private String key;
   
    private String application_id;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

 
  

}
