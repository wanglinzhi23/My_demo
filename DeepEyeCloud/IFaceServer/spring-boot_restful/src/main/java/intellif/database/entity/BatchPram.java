package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

public class BatchPram {
	private String insertDetailSql;
	private String insertInfoSql;
	private BatchPreparedStatementSetter detailSetter;
	private BatchPreparedStatementSetter infoSetter;
	public String getInsertDetailSql() {
		return insertDetailSql;
	}
	public void setInsertDetailSql(String insertDetailSql) {
		this.insertDetailSql = insertDetailSql;
	}
	public String getInsertInfoSql() {
		return insertInfoSql;
	}
	public void setInsertInfoSql(String insertInfoSql) {
		this.insertInfoSql = insertInfoSql;
	}
	public BatchPreparedStatementSetter getDetailSetter() {
		return detailSetter;
	}
	public void setDetailSetter(BatchPreparedStatementSetter detailSetter) {
		this.detailSetter = detailSetter;
	}
	public BatchPreparedStatementSetter getInfoSetter() {
		return infoSetter;
	}
	public void setInfoSetter(BatchPreparedStatementSetter infoSetter) {
		this.infoSetter = infoSetter;
	}
	
	
	public BatchPram(List<ImportBank> bankList,int bankType){
	     final List<ImportBank> list = bankList;
	    
		BatchPreparedStatementSetter detailSetter=new BatchPreparedStatementSetter (){
			public void setValues(PreparedStatement ps,int i) throws SQLException{
				ps.setLong(1,list.get(i).getId());
				ps.setTimestamp(2, new Timestamp(new Date().getTime()));
				ps.setTimestamp(3, new Timestamp(new Date().getTime()));
				ps.setLong(4,list.get(i).getId());
				ps.setString(5,list.get(i).getImageUrl());
			
			}

			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return list.size();
			}
			
		};
		BatchPreparedStatementSetter infoSetter=new BatchPreparedStatementSetter (){
			public void setValues(PreparedStatement ps,int i) throws SQLException{
				ps.setLong(1, list.get(i).getId());
				ps.setTimestamp(2, new Timestamp(new Date().getTime()));
				ps.setTimestamp(3, new Timestamp(new Date().getTime()));
				ps.setString(4, list.get(i).getImageUrl());
				ps.setString(5, list.get(i).getName());
			
			}

			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return list.size();
			}
			
		};
	    String detailSql = null;
	    String infoSql = null;
		if(bankType == 1){//cid
			 detailSql = "insert into "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_CID_DETAIL+
					 "(id,created,updated,face_feature,from_cid_id,from_image_id,image_data,indexed,version,zplxmc) "+
					 " VALUES (?,?,?,null,?,0,?,-1,0,null)";
		     infoSql = "INSERT INTO "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_CID_INFO+
		    		 "(id,created,updated,XS,SFZQFJG,MZ,SG,XJZDZ,RID,JGGJ,CSRQ,PHOTO,GXSJ,XB,DSMC,JGSSX,ROWKEY,DS,GMSFHM,SJHM,CJSJ,MZMC,xm) "+
		    		 " VALUES (?, ?, ?, null, null, null, null, null, null, null, null, ?, null, '1',  null, null, null, null, null, null, null, null,?)";
			
		}else if(bankType == 2){//juzhu
			 detailSql = "insert into "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_JUZHU_DETAIL+
					 "(id,created,updated,face_feature,from_cid_id,from_image_id,image_data,indexed,version,zplxmc) "+
					 " VALUES (?,?,?,null,?,0,?,'-1',0,null)";
		     infoSql = "INSERT INTO "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_JUZHU_INFO+
		    		 "(id,created,updated,XS,SFZQFJG,MZ,SG,XJZDZ,RID,JGGJ,CSRQ,PHOTO,GXSJ,XB,DSMC,JGSSX,ROWKEY,DS,GMSFHM,SJHM,CJSJ,MZMC,xm) "+
		    		 " VALUES (?, ?, ?, null, null, null, null, null, null, null, null, ?, null, '1',  null, null, null, null, null, null, null, null,?)";
		}else if(bankType == 3){//在逃
			 detailSql = "insert into "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_OTHER_DETAIL+
					 "(id,created,updated,face_feature,from_cid_id,from_image_id,image_data,indexed,version,zplxmc) "+
					 " VALUES (?,?,?,null,?,0,?,'-1',0,5)";
		     infoSql = "INSERT INTO "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_OTHER_INFO+
		    		 "(id,created,updated,XS,SFZQFJG,MZ,SG,XJZDZ,RID,JGGJ,CSRQ,PHOTO,GXSJ,XB,DSMC,JGSSX,ROWKEY,DS,GMSFHM,SJHM,CJSJ,MZMC,xm,addr,owner,type) "+
		    		 " VALUES (?, ?, ?, null, null, null, null, null, null, null, null, ?, null, '1', null, null, null, null, null, null, null, null, ?,null,null,5)";
		}else if(bankType == 4){//警综
			 detailSql = "insert into "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_OTHER_DETAIL+
					 "(id,created,updated,face_feature,from_cid_id,from_image_id,image_data,indexed,version,zplxmc) "+
					 " VALUES (?,?,?,null,?,0,?,'-1',0,6)";
		     infoSql = "INSERT INTO "+GlobalConsts.INTELLIF_STATIC+"."+GlobalConsts.T_NAME_OTHER_INFO+
		    		 "(id,created,updated,XS,SFZQFJG,MZ,SG,XJZDZ,RID,JGGJ,CSRQ,PHOTO,GXSJ,XB,DSMC,JGSSX,ROWKEY,DS,GMSFHM,SJHM,CJSJ,MZMC,xm,addr,owner,type,datatype,datatypename) "+
		    		 " VALUES (?, ?, ?, null, null, null, null, null, null, null, null, ?, null, '1', null, null, null, null, null, null, null, null, ?,null,null,6,18,'其它')";
		}
		this.detailSetter = detailSetter;
		this.infoSetter = infoSetter;
		this.insertDetailSql = detailSql;
		this.insertInfoSql= infoSql;

	}
}
