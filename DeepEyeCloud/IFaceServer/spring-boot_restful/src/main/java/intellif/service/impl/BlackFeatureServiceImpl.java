package intellif.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.service.BlackFeatureServiceItf;
@Service
public class BlackFeatureServiceImpl implements BlackFeatureServiceItf{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public void deleteByFaceIds(List<Long> idList) {

      if(null != idList && !idList.isEmpty()){
          StringBuffer idsBuf = new StringBuffer();
          for(Long id : idList){
              idsBuf.append(",");
              idsBuf.append(String.valueOf(id));
          }
          String sql = idsBuf.toString().substring(1);
          String exeSql = "delete from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_BLACK_FEATURE+" where from_black_id in("+sql+")";
          jdbcTemplate.execute(exeSql);
      }
        
    }

}
