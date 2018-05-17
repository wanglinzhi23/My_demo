/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intellif.database.entity;

import intellif.consts.GlobalConsts;
import intellif.oauth.AccessLimitMethod;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GlobalConsts.T_NAME_USER_API_LIMIT,schema=GlobalConsts.INTELLIF_BASE)
public class UserApiLimitInfo extends InfoBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private Long userId;
    
    private Long apiId;
    
    @Enumerated(EnumType.STRING)
    private AccessLimitMethod limitMethod;

    private Long callCount; 

	private Long denyCount;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getApiId() {
		return apiId;
	}

	public void setApiId(Long apiId) {
		this.apiId = apiId;
	}


	public AccessLimitMethod getLimitMethod() {
		return limitMethod;
	}

	public void setLimitMethod(AccessLimitMethod limitMethod) {
		this.limitMethod = limitMethod;
	}

	public Long getCallCount() {
		return callCount;
	}

	public void setCallCount(Long callCount) {
		this.callCount = callCount;
	}
	
	public Long getDenyCount() {
		if (denyCount == null) {
			return 0L;
		}
		return denyCount;
	}

	public void setDenyCount(Long denyCount) {
		this.denyCount = denyCount;
	}
    
}