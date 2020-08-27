package com.xx.netty.attribute;

import com.xx.netty.session.Session;
import io.netty.util.AttributeKey;

/**
 * @author yifanl
 */
public interface Attributes {
    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
}