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
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = GlobalConsts.T_NAME_ROLE,schema=GlobalConsts.INTELLIF_BASE)
public class RoleInfo extends InfoBase implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 1L;
    //    @ElementCollection(targetClass = String.class)
//    private Set<String> resourceIds = new HashSet<String>();
    private String resIds;//resource IDs.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty
    private String name;
//    @JsonIgnore
//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
//    private Set<UserInfo> users = new HashSet<UserInfo>();

    private String cnName;
    
    private String modules;

    @Override
    public String getAuthority() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Set<UserInfo> getUsers() {
//        return users;
//    }
//
//    public void setUsers(Set<UserInfo> users) {
//        this.users = users;
//    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    @Override
    public String toString() {
        return "RoleInfo,id:" + this.getId() + ",name:" + this.getName() + ",cnName:" + this.getCnName() + ",authority:" + this.getAuthority() + ",resIds:" + this.getResIds();
    }

    public String getResIds() {
        return resIds;
    }

    public void setResIds(String resIds) {
        this.resIds = resIds;
    }

	public String getModules() {
		return modules;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}
}