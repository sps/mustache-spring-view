/*
 * Copyright 2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.web.servlet.i18n;

import com.google.common.base.Function;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Spring Interceptor to add a model attribute, so a Mustache template can
 * access the Spring MessageSource for localized messages.
 * 
 * e.g. {{#i18n}}labels.global.mustache{{/i18n}}
 * 
 * Lambda support in mustache is handled using guava Function's
 * See: http://mustache.github.com/mustache.5.html (Lambdas)
 */
public class MustacheMessageInterceptor extends HandlerInterceptorAdapter {

    /**
     * Default key to be used in message templates.
     * 
     * e.g. {{i18n}}internationalize.this.key.please{{/i18n}}
     */
    private static final String DEFAULT_MODEL_KEY = "i18n";
    
    /** No support for message args, namely {0}, {1}, etc. */
    private static final Object[] NO_MESSAGE_ARGS = null;

    private String messageKey = DEFAULT_MODEL_KEY;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public MustacheMessageInterceptor(MessageSource messageSource,
            LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {

        final Locale locale = localeResolver.resolveLocale(request);

        // The Lambda function to support {{#messageKey}}property{{/messageKey}}
        modelAndView.addObject(messageKey, new Function<String, String>() {
            public String apply(@Nullable String input) {
                return messageSource.getMessage(input, NO_MESSAGE_ARGS, locale);
            }
        });
    }

    /**
     * Define custom key to access i18n messages in your Mustache template.
     * 
     * @param messageKey
     *            the key used in the template. For example if the messageKey is
     *            'label' then in the template you would use:
     * 
     *            {{#label}}labels.global.mustache{{/label}}
     * 
     *            The default messageKey is 'i18n'
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
