package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.database.dao.OauthResourceDao;
import intellif.database.dao.RoleDao;
import intellif.database.dao.RoleResourceDao;
import intellif.database.dao.UserDao;
import intellif.database.entity.OauthResource;
import intellif.database.entity.UserInfo;
import intellif.service.ResourceServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.RoleResourceDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Zheng Xiaodong on 2017/4/20.
 */
@Transactional
@Service
public class ResourceServiceImpl implements ResourceServiceItf {
  

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private OauthResourceDao oauthResourceDao;
    @Autowired
    private RoleResourceDao roleResourceDao;
  

    @Override
    public List<RoleResourceDto> queryResourcesByCurrentUser(String roleName, Boolean display) {
        List<RoleResourceDto> resources = roleResourceDao.queryResourcesByRoleName(roleName, display);
        UserInfo userInfo = CurUserInfoUtil.getUserInfo();
        List<OauthResource> userResources = this.queryUserResources(userInfo.getId());
        List<Long> userResIds = new ArrayList<>();
        for (OauthResource r : userResources) {
            userResIds.add(r.getId());
        }
        Set<Long> compIds = compatibleResIds(userResIds);
        List<RoleResourceDto> filteredResources = new ArrayList<>();

        boolean isInUser;
        for (RoleResourceDto r : resources) {
            isInUser = false;
            for (OauthResource ur : userResources) {
                if (r.getResourceId().equals(ur.getId())) {
                    isInUser = true;
                    break;
                }
            }
            for (Long i : compIds) {
                if (i.equals(r.getResourceId())) {
                    isInUser = true;
                    break;
                }
            }
            if (isInUser) {
                filteredResources.add(r);
            }
        }
        return filteredResources;
    }

    @Override
    public List<OauthResource> queryUserResources(Long userId) {
        UserInfo userInfo = (UserInfo) this.userDao.findById(userId);
        String[] roleIds = new String[] {};
        if (userInfo.getRoleIds() != null) {
            roleIds = userInfo.getRoleIds().split(",");
        }
        List<RoleInfo> roleInfoList = new ArrayList<>();
        List<OauthResource> oauthResourceList = new ArrayList<>();
        // Todo: 优化查询方式
        for (int i = 0; i < roleIds.length; i++) {
            long roleId = Long.valueOf(roleIds[i]);
            RoleInfo roleInfo = (RoleInfo) this.roleDao.findById(roleId);
            roleInfoList.add(roleInfo);
            String[] resIds = roleInfo.getResIds().split(",");
            for (int j = 0; j < resIds.length; j++) {
                Long resId = Long.valueOf(resIds[j]);
                OauthResource oauthResource = (OauthResource) this.oauthResourceDao.findById(resId);
                oauthResourceList.add(oauthResource);
            }
        }
        return oauthResourceList;
    }


    // Todo: Configurable
    @Override
    public Set<Long> compatibleResIds(List<Long> resIds) {
        Set<Long> result = new HashSet<>();
        if (resIds == null)
            return Collections.emptySet();
        for (Long r : resIds) {
            if (r.longValue() == 100) {
                result.add(101L);
            } else if (r.longValue() == 200) {
                result.add(201L);
                result.add(202L);
            } else if (r.longValue() == 201) {
                result.add(202L);
            }
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public String queryResourceNames(String resIds) {
        if (StringUtils.isEmpty(resIds))
            return "";
        String resNamesString = "";
        String filterSql = " id in (" + resIds + ") and id != 800 order by t.id";
        List<String> names =  oauthResourceDao.findFieldByFilter("cn_name", filterSql);
        if (CollectionUtils.isNotEmpty(names)) {
            for (int i = 0; i < names.size(); i++) {
                String name = names.get(i);
                resNamesString += name;
                if (i < names.size() - 1) {
                    resNamesString += ",";
                }
            }
        }
        return resNamesString;
    }

  

}
