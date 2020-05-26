package com.zhaoch.base.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import cn.hutool.core.util.StrUtil;

/**
 * logback 日志脱敏 Converter
 *
 * @author Zhaochen
 * @since 2019/11/29 16:05
 */
public class DesensitizationConverter extends CompositeConverter<ILoggingEvent> {

    /**
     * 正则表达式
     */
    private static final String REG = "(\\d{4})\\d{3,12}(\\d{4})";

    @Override
    protected String transform(ILoggingEvent event, String in) {
        String formattedMessage = event.getFormattedMessage();
        if (StrUtil.isBlank(formattedMessage)) {
            return formattedMessage;
        }
        return formattedMessage.replaceAll(REG, "$1****$2");
    }

}

