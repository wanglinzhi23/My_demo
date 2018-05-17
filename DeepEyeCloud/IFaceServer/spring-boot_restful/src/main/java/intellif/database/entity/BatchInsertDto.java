package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.util.CollectionUtils;


public class BatchInsertDto {
	private String insertSql;
	private BatchPreparedStatementSetter insertSetter;
	
	
	public String getInsertSql() {
		return insertSql;
	}


	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}


	public BatchPreparedStatementSetter getInsertSetter() {
		return insertSetter;
	}


	public void setInsertSetter(BatchPreparedStatementSetter insertSetter) {
		this.insertSetter = insertSetter;
	}


	public BatchInsertDto(List<Object> dataList){
	    if(!CollectionUtils.isEmpty(dataList)){
		 if (dataList.get(0) instanceof CameraAndBlackDetail) {
			 List<CameraAndBlackDetail> cList = new ArrayList<CameraAndBlackDetail>();
			for(Object o : dataList){
				CameraAndBlackDetail ca = (CameraAndBlackDetail) o;
				cList.add(ca);
			}
			 final List<CameraAndBlackDetail> list = cList;
	    
		BatchPreparedStatementSetter insertSetter=new BatchPreparedStatementSetter (){
			public void setValues(PreparedStatement ps,int i) throws SQLException{
				ps.setLong(1,list.get(i).getId());
				ps.setLong(2, list.get(i).getCameraId());
				ps.setLong(3, list.get(i).getBlackdetailId());
			
			}

			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return list.size();
			}
			
		};
		
	    String sql = "insert into "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_CAMERA_BLACKDETAIL+
					 "(id,camera_id,blackdetail_id) "+" VALUES (?,?,?)";		  		
		this.insertSetter = insertSetter;
		this.insertSql = sql;

		}  else if (dataList.get(0) instanceof AreaAndBlackDetail) {
            List<AreaAndBlackDetail> cList = new ArrayList<AreaAndBlackDetail>();
           for(Object o : dataList){
               AreaAndBlackDetail ca = (AreaAndBlackDetail) o;
               cList.add(ca);
           }
            final List<AreaAndBlackDetail> list = cList;
       
       BatchPreparedStatementSetter insertSetter=new BatchPreparedStatementSetter (){
           public void setValues(PreparedStatement ps,int i) throws SQLException{
               ps.setLong(1,list.get(i).getId());
               ps.setLong(2, list.get(i).getAreaId());
               ps.setLong(3, list.get(i).getBlackdetailId());
           
           }

           @Override
           public int getBatchSize() {
               // TODO Auto-generated method stub
               return list.size();
           }
           
       };
       
       String sql = "insert into "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_AREA_BLACKDETAIL+
                    "(id,area_id,blackdetail_id) "+" VALUES (?,?,?)";                
       this.insertSetter = insertSetter;
       this.insertSql = sql;
       }
	}
    }

}
