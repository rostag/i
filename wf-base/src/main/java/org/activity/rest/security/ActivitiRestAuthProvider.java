package org.activity.rest.security;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngines;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by diver on 6/26/15.
 */
@Component
public class ActivitiRestAuthProvider implements AuthenticationProvider {

	private final Logger log = LoggerFactory
			.getLogger(ActivitiRestAuthProvider.class);
    
	private static final String GENERAL_ROLE = "ROLE_USER";

	@Value("${general.auth.login}")
	private String generalUsername;
	@Value("${general.auth.password}")
	private String generalPassword;

	private IdentityService identityService;

	public void setGeneralPassword(String generalPassword) {
		this.generalPassword = generalPassword;
	}

	public void setGeneralUsername(String generalUsername) {
		this.generalUsername = generalUsername;
	}

	public void setIdentityService(IdentityService identityService) {
		this.identityService = identityService;
	}

	private IdentityService getIdentityService() {
		return identityService == null ? ProcessEngines.getDefaultProcessEngine().getIdentityService() : identityService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		validateAuthenticationInformation(authentication);
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		if (username.equals(generalUsername) && password.equals(generalPassword)) {
                        log.info("generalUsername!!!1test: tech_mvd,getIdentityService="+getIdentityService().getUserInfo("tech_mvd", "tech_mvd"));
                        Authentication oAuthentication=createBasicAuthUsernameAndPasswordToken("tech_mvd", "tech_mvd");
                        log.info("generalUsername!!!2test: tech_mvd,oAuthentication!=mull:"+(oAuthentication!=null));
                        if(oAuthentication!=null){
                            log.info("generalUsername!!!3test: tech_mvd"
                                    + ",oAuthentication.getName()="+oAuthentication.getName()
                                    + ",oAuthentication.getDetails="+oAuthentication.getDetails()
                            );
                        }
			return createBasicAuthUsernameAndPasswordToken(username, password);
		} else {
			if (getIdentityService().checkPassword(username, password)) {
                                log.info("username="+username+",getIdentityService="+getIdentityService().getUserInfo(username, password));
				return createBasicAuthUsernameAndPasswordToken(username, password);
			} else {
				return null;
			}
		}
	}

	private Authentication createBasicAuthUsernameAndPasswordToken(String username, String password) {
		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(GENERAL_ROLE));
		return new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
	}

	private void validateAuthenticationInformation(Authentication authentication) throws AuthenticationException {
		boolean isAuthInfoInvalid = authentication == null;
		isAuthInfoInvalid = isAuthInfoInvalid || StringUtils.isBlank(authentication.getName());
		isAuthInfoInvalid = isAuthInfoInvalid || authentication.getCredentials() == null;
		isAuthInfoInvalid = isAuthInfoInvalid || StringUtils.isBlank(authentication.getCredentials().toString());
		if (isAuthInfoInvalid) {
			throw new BadCredentialsException("User or password not valid");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return aClass.equals(UsernamePasswordAuthenticationToken.class);
	}
}
