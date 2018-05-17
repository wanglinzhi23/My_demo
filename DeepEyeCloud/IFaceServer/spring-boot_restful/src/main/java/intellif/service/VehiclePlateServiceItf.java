package intellif.service;

import intellif.dto.VehiclePlateQueryDto;

public interface VehiclePlateServiceItf {
	String gatherNumberByTime(VehiclePlateQueryDto vehiclePlateQueryDto);
	String gatherCountByCrossing_Id(VehiclePlateQueryDto vehiclePlateQueryDto);
	String getTotalCountByEveMinute(VehiclePlateQueryDto vehiclePlateQueryDto);
}
