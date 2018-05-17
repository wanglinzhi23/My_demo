package intellif.fk.service;

import java.util.List;

import intellif.fk.dto.FindFkPersonDto;
import intellif.fk.dto.FkPersonDto;
import intellif.fk.dto.FkPersonResultDto;
import intellif.fk.vo.FkInstitutionCode;


public interface FkPersonDetailService {

    public List<FkPersonResultDto> findFkPerson(FindFkPersonDto findFkPersonDto);
   
}
