//package org.example.configs;
//
//import org.example.configs.interceptors.OwnerInterceptor;
//import org.example.service.UserService;
//import org.example.service.impl.ServiceManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer
//{
//	HandlerInterceptor ownerInterceptor;
//
//	UserService userService;
//
//	ServiceManager serviceManager;
//
//	public WebMvcConfig(UserService userService, ServiceManager serviceManager)
//	{
//		this.userService = userService;
//		this.serviceManager = serviceManager;
//
//		ownerInterceptor = getOwnerInterceptor();
//	}
//
//	@Override
//	public void addInterceptors(InterceptorRegistry registry)
//	{
//		registry.addInterceptor(ownerInterceptor).addPathPatterns("/api/**");
//	}
//
//	public HandlerInterceptor getOwnerInterceptor() {
//		return new OwnerInterceptor(serviceManager, userService);
//	}
//}
