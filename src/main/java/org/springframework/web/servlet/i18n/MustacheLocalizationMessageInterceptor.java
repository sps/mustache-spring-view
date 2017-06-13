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

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spring Interceptor to add a model attribute, so a Mustache template can access the Spring
 * MessageSource for localized messages.
 * <p/>
 * e.g. {{#i18n}}labels.global.mustache [arg1]...[argN]{{/i18n}}
 */
public abstract class MustacheLocalizationMessageInterceptor extends HandlerInterceptorAdapter implements
        MessageSourceAware {

    /**
     * Default key to be used in message templates.
     * <p/>
     * e.g. {{i18n}}internationalize.this.key.please{{/i18n}}
     */
    public static final String DEFAULT_MODEL_KEY = "i18n";

    private static final Pattern KEY_PATTERN = Pattern.compile("(.*?)[\\s\\[]");
    private static final Pattern ARGS_PATTERN = Pattern.compile("\\[(.*?)\\]");

    private String messageKey = DEFAULT_MODEL_KEY;

    private MessageSource messageSource;
    private LocaleResolver localeResolver;
    private boolean useDefaultMessage;
    private String defaultMessage;

    protected final void localize(final HttpServletRequest request, String frag, Writer out) throws IOException {
        final Locale locale = localeResolver.resolveLocale(request);
        final String key = extractKey(frag);
        final List<String> args = extractParameters(frag);
        final String text = this.getMessage(key, args, locale);
        out.write(text);
    }

    private String getMessage(String key, List<String> args, Locale locale) {
        if (useDefaultMessage) {
            return this.messageSource.getMessage(key, args.toArray(), this.defaultMessage, locale);
        }
        return this.messageSource.getMessage(key, args.toArray(), locale);
    }

    protected abstract Object createHelper(final HttpServletRequest request);

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
                           final Object handler,
                           final ModelAndView modelAndView) throws Exception {

        if (modelAndView != null) {
            modelAndView.addObject(messageKey, createHelper(request));
        }

        super.postHandle(request, response, handler, modelAndView);
    }


    /**
     * Split key from (optional) arguments.
     *
     * @param key
     * @return localization key
     */
    private String extractKey(String key) {
        Matcher matcher = KEY_PATTERN.matcher(key);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return key;
    }

    /**
     * Split args from input string.
     * <p/>
     * localization_key [param1] [param2] [param3]
     *
     * @param key
     * @return List of extracted parameters
     */
    private List<String> extractParameters(String key) {
        final Matcher matcher = ARGS_PATTERN.matcher(key);
        final List<String> args = new ArrayList<String>();
        while (matcher.find()) {
            args.add(matcher.group(1));
        }
        return args;
    }

    /**
     * Define custom key to access i18n messages in your Mustache template.
     *
     * @param messageKey the key used in the template. For example if the messageKey is 'label' then in the
     *                   template you would use:
     *                   <p/>
     *                   {{#label}}labels.global.mustache{{/label}}
     *                   <p/>
     *                   The default messageKey is 'i18n'
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public void setUseDefaultMessage(boolean useDefaultMessage) {
        this.useDefaultMessage = useDefaultMessage;
    }
}
