/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageHelper {

    public Message createMessageSuccess(MessageSource messageSource,
                                        String code,
                                        Object[] args) {
        MessageResolver resolver =
            new MessageBuilder().code(code).args(args).info().build();

        return resolver.resolveMessage(messageSource,
                                       LocaleContextHolder.getLocale());
    }

}
