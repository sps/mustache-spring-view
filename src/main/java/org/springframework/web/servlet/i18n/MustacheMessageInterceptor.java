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

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.mustache.MustacheViewResolver;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

/**
 * Spring Interceptor to add a model attribute, so a Mustache template can
 * access the Spring MessageSource for localized messages.
 * 
 * e.g. {{#i18n}}labels.global.mustache{{/i18n}}
 */
public class MustacheMessageInterceptor extends HandlerInterceptorAdapter implements MessageSourceAware {

	/**
	 * Default key to be used in message templates.
	 * 
	 * e.g. {{i18n}}internationalize.this.key.please{{/i18n}}
	 */
	private static final String DEFAULT_MODEL_KEY = "i18n";

	/** No support for message args, namely {0}, {1}, etc. */
	private static final Object[] NO_MESSAGE_ARGS = null;

	private String messageKey = DEFAULT_MODEL_KEY;

	private MessageSource messageSource;
	private LocaleResolver localeResolver;

	@Autowired
	private MustacheViewResolver viewResolver;

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			modelAndView.addObject(messageKey, new Mustache.Lambda() {
				public void execute(Template.Fragment frag, Writer out) throws IOException {
					final Locale locale = localeResolver.resolveLocale(request);
					final String key = frag.execute();
					final String text = messageSource.getMessage(key, NO_MESSAGE_ARGS, locale);
					out.write(text);
				}
			});
		}

		super.postHandle(request, response, handler, modelAndView);
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

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	public void setViewResolver(MustacheViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

}
