package intellif.zoneauthorize.plugin.impl;

import org.springframework.stereotype.Service;

import intellif.database.entity.DistrictInfo;
import intellif.zoneauthorize.plugin.ZoneAuthorizePluginItf;
import intellif.database.entity.UserDistrict;

@Service
public class DistrictPluginImpl extends AbstracZonePluginImpl<DistrictInfo> implements ZoneAuthorizePluginItf<DistrictInfo> {

    @Override
    public Class<DistrictInfo> zoneClass() {
        return DistrictInfo.class;
    }

    @Override
    public Class<?> userToZoneClass() {
        return UserDistrict.class;
    }

    @Override
    public String foreignKey() {
        return "district_id";
    }
}
