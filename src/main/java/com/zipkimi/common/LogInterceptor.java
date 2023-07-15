package com.zipkimi.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        // 요청 전처리 로직 구현
        log.info("******************");
        log.info("URL         : " + request.getRequestURI());
        log.info("CONTENTTYPE : " + request.getContentType());
        log.info("METHOD      : " + request.getMethod());
        log.info("PARAMETER   : " + request.getParameterMap().toString());
        //TODO logging 추가

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler, ModelAndView modelAndView)
            throws Exception {
        // 요청 후처리 로직 구현
        log.info("******************");
        log.info("STATUS : " + response.getStatus());
        log.info("METHOD : " + response.getContentType());
    }

    @Override
    public void afterCompletion(HttpServletRequest request
            , HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 예외 처리 로직 구현
    }
}

