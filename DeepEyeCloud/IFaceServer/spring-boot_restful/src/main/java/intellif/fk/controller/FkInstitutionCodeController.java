package intellif.fk.controller;

import intellif.consts.GlobalConsts;
import intellif.fk.dto.FkLocalInstitutionDto;
import intellif.fk.dto.FkSubInstitutionDto;
import intellif.fk.service.FkInstitutionCodeService;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_FK_INSTITUTION_CODE)
public class FkInstitutionCodeController {

    private static Logger LOG = LogManager.getLogger(FkInstitutionCodeController.class);

    @Autowired
    private FkInstitutionCodeService fkInstitutionCodeService;
    
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list of all the fk institutions.")
    public List<FkSubInstitutionDto> getFkPerson() {
             
        List<FkSubInstitutionDto> subList = fkInstitutionCodeService.findFkSubStation();
        for(int i=0;i<subList.size();i++){
            List<FkLocalInstitutionDto> localList = fkInstitutionCodeService.findFkLocalStationBySubStation(subList.get(i).getSubStationCode());
            subList.get(i).setLocalStationList(localList);
        }
        
       return subList;
    }

}