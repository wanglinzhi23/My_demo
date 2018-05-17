package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;

/**
 * Created by yangboz on 11/18/15.
 */
@Entity
@Table(name = GlobalConsts.T_NAME_CLIENT_DETAILS,schema=GlobalConsts.INTELLIF_BASE)
public class OauthClientDetails {
    //    client_id VARCHAR(255) PRIMARY KEY,
//    resource_ids VARCHAR(255),
//    client_secret VARCHAR(255),
//    scope VARCHAR(255),
//    authorized_grant_types VARCHAR(255),
//    web_server_redirect_uri VARCHAR(255),
//    authorities VARCHAR(255),
//    access_token_validity INTEGER,
//    refresh_token_validity INTEGER,
//    additional_information VARCHAR(4096),
//    autoapprove VARCHAR(255)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String clientId;
    private String resourceIds;
    private String clientSecret;
    private String scope;
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private String authorities;
    private int accessTokenValidity;
    private int refreshTokenValidity;
    @Column(name = "additional_information", columnDefinition = "VARCHAR(4096)")
    private String additionalInformation;
    private String autoapprove;

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public int getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public int getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAutoapprove() {
        return autoapprove;
    }

    public void setAutoapprove(String autoapprove) {
        this.autoapprove = autoapprove;
    }

    @Override
    public String toString() {
        return "OauthClientDetails,id:" + this.id + ",resourceIds:" + this.resourceIds
                + ",clientSecret:" + this.clientSecret
                + ",scope:" + this.scope
                + ",authorizedGrantTypes:" + this.authorizedGrantTypes
                + ",webServerRedirectUri:" + this.webServerRedirectUri
                + ",authorities:" + this.authorities
                + ",accessTokenValidity:" + this.accessTokenValidity
                + ",refreshTokenValidity:" + this.refreshTokenValidity
                + ",additionalInformation:" + this.additionalInformation
                + ",autoapprove:" + this.autoapprove;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
