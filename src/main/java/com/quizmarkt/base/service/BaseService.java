package com.quizmarkt.base.service;

import com.quizmarkt.base.data.context.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author anercan
 */
public class BaseService {

    protected static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected Integer getAppId() {
        return UserContextHolder.getAppId();
    }

    protected String getUserId() {
        return UserContextHolder.getUserId();
    }

    protected boolean isPremiumUser() {
        return false;
    }

}