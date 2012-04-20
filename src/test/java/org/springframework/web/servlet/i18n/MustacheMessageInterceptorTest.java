package org.springframework.web.servlet.i18n;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

public class MustacheMessageInterceptorTest {

  private MessageSource messageSource = Mockito.mock(MessageSource.class);
  private LocaleResolver localeResolver = Mockito.mock(LocaleResolver.class);

  private MustacheMessageInterceptor messageInterceptor = new MustacheMessageInterceptor(messageSource, localeResolver);

  @Test
  @SuppressWarnings("unchecked")
  public void testPostHandle() throws Exception {
    ModelAndView mav = doPostHandle();
    Assert.assertEquals(1, mav.getModel().size());
    Map.Entry<String, Object> messageEntry = mav.getModel().entrySet().iterator().next();
    Assert.assertEquals("message", messageEntry.getKey());
    Assert.assertTrue(messageEntry.getValue() instanceof Function);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPostHandle_ExecuteFunction() throws Exception {
    String testMessageCode = "labels.global.mustache";
    String translatedMessage = "snor";
    Mockito.when(messageSource.getMessage(testMessageCode, null, createLocale())).thenReturn(translatedMessage);

    ModelAndView mav = doPostHandle();
    Assert.assertEquals(1, mav.getModel().size());
    Map.Entry<String, Object> messageEntry = mav.getModel().entrySet().iterator().next();
    String message = (String) ((Function) messageEntry.getValue()).apply(testMessageCode);
    Assert.assertEquals(translatedMessage, message);
  }

  @Test
  public void testPostHandle_AlternativeModelKey() throws Exception {
    String messagesModelKey = "testkey";
    messageInterceptor.setMessageKey(messagesModelKey);
    ModelAndView mav = doPostHandle();
    Map.Entry<String, Object> messageEntry = mav.getModel().entrySet().iterator().next();
    Assert.assertEquals(messagesModelKey, messageEntry.getKey());
  }

  private ModelAndView doPostHandle() throws Exception {
    HttpServletRequest request = new MockHttpServletRequest();
    Mockito.when(localeResolver.resolveLocale(request)).thenReturn(createLocale());
    ModelAndView mav = new ModelAndView();

    messageInterceptor.postHandle(request, new MockHttpServletResponse(), null, mav);
    return mav;
  }

  private Locale createLocale() {
    return new Locale("nl", "BE");
  }

}
