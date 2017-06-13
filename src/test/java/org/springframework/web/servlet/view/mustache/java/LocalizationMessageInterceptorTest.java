/**
 *
 */
package org.springframework.web.servlet.view.mustache.java;

import com.google.common.base.Function;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
public class LocalizationMessageInterceptorTest {
    private static final String DEFAULT_MESSAGE = "DEFAULT_MESSAGE";
    private MessageSource messageSource;
    private LocaleResolver localeResolver;

    private LocalizationMessageInterceptor interceptor;
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
        interceptor = new LocalizationMessageInterceptor();
        interceptor.setLocaleResolver(localeResolver);
        interceptor.setMessageSource(messageSource);
        interceptor.setMessageKey(messageKey);
    }

    @Test
    public void test() throws Exception {

        final ModelAndView modelAndView = mock(ModelAndView.class);
        final ArgumentCaptor<Function> captor = ArgumentCaptor.forClass(Function.class);

        interceptor.postHandle(request, response, handler, modelAndView);

        verify(modelAndView).addObject(eq(messageKey), captor.capture());

        // exercise the in-lined Lambda
        final Function<String, String> function = (Function<String, String>) captor.getValue();
        assertNotNull(function);


        final String fragResult = "bar";
        final String fragResultWithArgs = "bar  [foo] [baz][burp]";

        when(localeResolver.resolveLocale(request)).thenReturn(Locale.CANADA_FRENCH);

        function.apply(fragResult);

        verify(messageSource, times(1)).getMessage(fragResult, new Object[]{}, Locale.CANADA_FRENCH);

        function.apply(fragResultWithArgs);

        verify(messageSource, times(1)).getMessage(fragResult, new Object[]{"foo", "baz", "burp"}, Locale.CANADA_FRENCH);

        verifyNoMoreInteractions(messageSource);

    }

    @Test
    public void whenExecute_givenDefaultMessageAndLabelDoesntExist_thenOutputDefaultMessage() throws Exception {
        interceptor.setUseDefaultMessage(true);
        interceptor.setDefaultMessage(DEFAULT_MESSAGE);

        ModelAndView modelAndView = mock(ModelAndView.class);
        ArgumentCaptor<Mustache.Lambda> captor = ArgumentCaptor.forClass(Mustache.Lambda.class);

        interceptor.postHandle(request, response, handler, modelAndView);

        verify(modelAndView).addObject(eq(messageKey), captor.capture());

        // exercise the in-lined Lambda
        Function<String, String> function = (Function<String, String>) captor.getValue();
        assertNotNull(function);

        String fragResult = "bar";

        when(localeResolver.resolveLocale(request)).thenReturn(Locale.CANADA_FRENCH);
        when(messageSource.getMessage(fragResult, new Object[]{}, DEFAULT_MESSAGE, Locale.CANADA_FRENCH)).thenReturn(DEFAULT_MESSAGE);

        String out = function.apply(fragResult);

        assertEquals(DEFAULT_MESSAGE, out);
    }

    @Test(expected = NoSuchMessageException.class)
    public void whenExecute_givenDefaultMessageNullAndLabelDoesntExist_thenThrowException() throws Exception {
        ModelAndView modelAndView = mock(ModelAndView.class);
        ArgumentCaptor<Mustache.Lambda> captor = ArgumentCaptor.forClass(Mustache.Lambda.class);

        interceptor.postHandle(request, response, handler, modelAndView);

        verify(modelAndView).addObject(eq(messageKey), captor.capture());

        // exercise the in-lined Lambda
        Function<String, String> function = (Function<String, String>) captor.getValue();
        assertNotNull(function);

        String fragResult = "bar";

        when(localeResolver.resolveLocale(request)).thenReturn(Locale.CANADA_FRENCH);
        when(messageSource.getMessage(fragResult, new Object[]{}, Locale.CANADA_FRENCH)).thenThrow(NoSuchMessageException.class);

        function.apply(fragResult);
    }
}
