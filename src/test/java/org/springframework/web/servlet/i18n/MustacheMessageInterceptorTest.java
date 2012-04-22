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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.base.Function;

@RunWith(JMock.class)
public class MustacheMessageInterceptorTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final MessageSource messageSource = context.mock(MessageSource.class);
    private final LocaleResolver localeResolver = context.mock(LocaleResolver.class);
    private final HttpServletResponse UNUSED_RESPONSE = null;

    private MustacheMessageInterceptor messageInterceptor = new MustacheMessageInterceptor(
            messageSource, localeResolver);

    @Test
    public void postHandlePlacesTheMessageInterceptorInTheModel() throws Exception {
        ModelAndView mav = doPostHandle();
        assertThat(mav.getModel().size(), equalTo(1));

        Map.Entry<String, Object> messageEntry = mav.getModel().entrySet().iterator().next();

        assertThat(messageEntry.getKey(), equalTo("i18n"));
        assertThat(messageEntry.getValue(), instanceOf(Function.class));
    }

    /**
     * This tests that function inserted into model would actually return the
     * correct value when called from Mustache.
     * 
     * @throws Exception
     */
    @Test
    public void mustacheTemplateResolvesMessage() throws Exception {
        final String i18nProperty = "labels.global.mustache";
        final String translatedMessage = "snor";

        // Setup the model as per Spring
        ModelAndView mav = doPostHandle();

        context.checking(new Expectations() {
            {
                Object[] NO_ARGS_ALLOWED = null;
                oneOf(messageSource).getMessage(i18nProperty, NO_ARGS_ALLOWED, nlLocale());
                will(returnValue(translatedMessage));
            }
        });

        // Create a simple mustache template
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        StringReader templateString = new StringReader("-{{#i18n}}labels.global.mustache{{/i18n}}-");
        Mustache template = mf.compile(templateString, "i18n-test");

        // Parse the template and resolve the internationalized parameters
        StringWriter output = new StringWriter();
        template.execute(output, mav.getModel());

        // This is covered by the messageSource expectation, but lets make it
        // explicit.
        assertThat(output.toString(), equalTo("-" + translatedMessage + "-"));
    }

    @Test
    public void handlesAlternateMessageKey() throws Exception {
        String i18nKey = "testkey";
        messageInterceptor.setMessageKey(i18nKey);
        ModelAndView mav = doPostHandle();
        Map.Entry<String, Object> messageEntry = mav.getModel().entrySet().iterator().next();
        assertThat(messageEntry.getKey(), equalTo(i18nKey));
    }

    /**
     * This method would be triggered by the Spring framework.
     * 
     * @return a modified model and view containing a Function handler for i18n
     *         messages.
     * @throws Exception
     */
    private ModelAndView doPostHandle() throws Exception {
        final HttpServletRequest request = context.mock(HttpServletRequest.class);

        context.checking(new Expectations() {
            {
                oneOf(localeResolver).resolveLocale(request);
                will(returnValue(nlLocale()));
            }
        });

        ModelAndView mav = new ModelAndView();
        messageInterceptor.postHandle(request, UNUSED_RESPONSE, null, mav);
        return mav;
    }

    private Locale nlLocale() {
        return new Locale("nl", "BE");
    }
}
