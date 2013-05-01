/**
 * 
 */
package org.springframework.web.servlet.i18n;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Writer;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.mustache.MustacheViewResolver;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Lambda;
import com.samskivert.mustache.Template;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
public class MustacheMessageInterceptorTest {

    private MessageSource messageSource;
    private LocaleResolver localeResolver;
    private MustacheViewResolver viewResolver;

    private MustacheMessageInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private final Object handler = new Object();

    private static final String messageKey = "foo";

    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        localeResolver = mock(LocaleResolver.class);
        messageSource = mock(MessageSource.class);
        interceptor = new MustacheMessageInterceptor();
        interceptor.setLocaleResolver(localeResolver);
        interceptor.setMessageSource(messageSource);
        interceptor.setViewResolver(viewResolver);
        interceptor.setMessageKey(messageKey);
    }

    @Test
    public void test() throws Exception {

        // exercise the null model condition...
        interceptor.postHandle(request, response, handler, null);

        // ...and the non-null model
        final ModelAndView modelAndView = mock(ModelAndView.class);
        final ArgumentCaptor<Mustache.Lambda> captor = ArgumentCaptor.forClass(Mustache.Lambda.class);

        interceptor.postHandle(request, response, handler, modelAndView);

        verify(modelAndView).addObject(eq(messageKey), captor.capture());

        // exercise the in-lined Lambda
        final Lambda lambda = captor.getValue();
        assertNotNull(lambda);

        final Template.Fragment frag = mock(Template.Fragment.class);
        final Writer out = mock(Writer.class);
        final String fragResult = "bar";

        when(frag.execute()).thenReturn(fragResult);
        when(localeResolver.resolveLocale(request)).thenReturn(Locale.CANADA_FRENCH);

        lambda.execute(frag, out);

        verify(messageSource).getMessage(fragResult, null, Locale.CANADA_FRENCH);

    }
}
