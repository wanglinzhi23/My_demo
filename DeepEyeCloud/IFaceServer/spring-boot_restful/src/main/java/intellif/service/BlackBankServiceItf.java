package intellif.service;


import intellif.dto.BankDto;
import intellif.dto.BankInfoDto;
import intellif.dto.BlackDetailDto;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;

import java.util.List;

public interface BlackBankServiceItf<T> extends CommonServiceItf<T>{

	public List<BlackDetailDto> findByCombinedConditions(BlackDetailDto blackDetailDto);
	public List<BankInfoDto> findBankByCombinedConditions(Long stationId,BankDto bankDto);

}
