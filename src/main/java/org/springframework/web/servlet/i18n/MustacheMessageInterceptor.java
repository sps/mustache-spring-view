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
 * Spring Interceptor to add a model attribute, so a Mustache template can access the Spring MessageSource
 * for localized messages.
 *
 * e.g. {{#message}}labels.global.mustache{{/message}}
 */
public class MustacheMessageInterceptor extends HandlerInterceptorAdapter {

  private static final String DEFAULT_MODEL_KEY = "message";

  private String messageKey = DEFAULT_MODEL_KEY;

  private final MessageSource messageSource;
  private final LocaleResolver localeResolver;

  public MustacheMessageInterceptor(MessageSource messageSource, LocaleResolver localeResolver) {
    this.messageSource = messageSource;
    this.localeResolver = localeResolver;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response,
                         Object handler, ModelAndView modelAndView) throws Exception {
    final Locale locale = localeResolver.resolveLocale(request);
    modelAndView.addObject(messageKey, new Function() {
      public Object apply(@Nullable Object input) {
        return messageSource.getMessage((String) input, null, locale);
      }
    });
  }

  /**
   * define custom key to access messages in your Mustache template.
   * @param messageKey the key used in the template e.g. label --> {{#label}}labels.global.mustache{{/label}}
   *   default is "message"
   */
  public void setMessageKey(String messageKey) {
    this.messageKey = messageKey;
  }
}
