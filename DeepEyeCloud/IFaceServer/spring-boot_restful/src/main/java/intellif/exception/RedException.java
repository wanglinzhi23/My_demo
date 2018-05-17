package intellif.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="红名单禁止搜索")  // 404
public class RedException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 4311113980598846255L;
    
    private int type = 1;     //区分拖动搜索时，faceId是从静态库，抓拍库等
    private int searchType; //0 上传搜索  1 拖动搜索
    private String sId;//搜索图片ID
    private long rId;
    private String rUrl;
  public RedException(String rUrl,long sId,long rId,int searchType,int type){
      this.rUrl = rUrl;
      this.searchType = searchType;
      this.sId = String.valueOf(sId);
      this.rId = rId;
      this.type = type;
  }
        public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

  

    public String getsId() {
        return sId;
    }
    public void setsId(String sId) {
        this.sId = sId;
    }
    public long getrId() {
        return rId;
    }

    public void setrId(long rId) {
        this.rId = rId;
    }
    public String getrUrl() {
        return rUrl;
    }
    public void setrUrl(String rUrl) {
        this.rUrl = rUrl;
    }

        

}