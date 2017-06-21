package com.escframework.core.web.request;

import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestParamModelArguemtMethodeResolver implements HandlerMethodArgumentResolver
{

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception
	{
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		RequestParamsModel requestParamsModel = parameter.getMethodAnnotation(RequestParamsModel.class);
		Class<?> clazz = requestParamsModel.clazz();
		Object targetParams = clazz.getConstructor().newInstance();
		// 用spring的方法比apache的方法性能更好
		BeanUtils.copyProperties(targetParams, parameterMap);
		return targetParams;
	}

	public boolean supportsParameter(MethodParameter methodParameter)
	{
		RequestParamsModel requestParamsModel = methodParameter.getMethodAnnotation(RequestParamsModel.class);
		if (null == requestParamsModel)
		{
			return false;
		}

		return true;
	}

}
