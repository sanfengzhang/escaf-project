package com.escframework.esauth;

import com.escframework.esauth.ipauth.IpWhileLitsAuthService;
import com.escframework.esauth.tokenauth.AuthTokenService;
import com.escframework.esauth.utils.PropsConfigUtils;

public class AuthServiceFactory
{

	private static final String IP_AUTH_SERVICE = "ipAuthService";

	// private static final String NAME_AND_PWD_SERVICE = "passwordAuthService";

	private static final String TOKEN_AUTH_SERVICE = "tokenAuthService";

	public static AuthService getAuthService()
	{

		PropsConfigUtils propsConfigUtils = PropsConfigUtils.getInstance();
		String authServiceName = propsConfigUtils.getValue("authServiceName");
		authServiceName = authServiceName != null ? authServiceName : IP_AUTH_SERVICE;
		AuthService authService = null;
		if (IP_AUTH_SERVICE.equals(authServiceName))
		{
			authService = new IpWhileLitsAuthService();
		} else if (TOKEN_AUTH_SERVICE.equals(authServiceName))
		{
			authService = new AuthTokenService();
		} else
		{
			throw new AuthException("unsupport authService,authServiceName=" + authServiceName);
		}

		return authService;
	}

}
