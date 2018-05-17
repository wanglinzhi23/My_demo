package intellif.fk.service;

import java.util.List;

import intellif.fk.dto.FindFkPersonDto;
import intellif.fk.dto.FkLocalInstitutionDto;
import intellif.fk.dto.FkPersonDto;
import intellif.fk.dto.FkPersonResultDto;
import intellif.fk.dto.FkSubInstitutionDto;
import intellif.fk.vo.FkInstitutionCode;


public interface FkInstitutionCodeService {

    //根据机构代码表获取反恐平台所有 分局信息
    public List<FkSubInstitutionDto> findFkSubStation();
    
    //根据机构代码表获取反恐平台所有派出所信息
    public List<FkLocalInstitutionDto> findFkLocalStation();
    
    //根据 分局机构代码 获取其下面所有的 派出所
    public List<FkLocalInstitutionDto> findFkLocalStationBySubStation(String subStationJGDM);
 
    
}
