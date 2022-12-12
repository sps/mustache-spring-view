package org.springframework.web.servlet.view.mustache.jmustache;

import com.samskivert.mustache.Mustache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.view.mustache.MustacheTemplateException;

import java.io.FileNotFoundException;
import java.io.Reader;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JMustacheTemplateFactoryTest {

    private JMustacheTemplateFactory mustacheTemplateFactory;

    @Mock
    private JMustacheTemplateLoader templateLoader;

    @Mock
    private Mustache.Compiler compiler;

    @Mock
    private Reader templateReader;

    @Before
    public void setUp() throws Exception {
        mustacheTemplateFactory = new JMustacheTemplateFactory();

        mustacheTemplateFactory.setEscapeHTML(false);
        mustacheTemplateFactory.setStandardsMode(false);
        mustacheTemplateFactory.setPrefix(null);
        mustacheTemplateFactory.setSuffix(null);


        mustacheTemplateFactory.setTemplateLoader(templateLoader);
        mustacheTemplateFactory.afterPropertiesSet();

        mustacheTemplateFactory.setCompiler(compiler);
        mustacheTemplateFactory.afterPropertiesSet();
    }

    @Test
    public final void testGetTemplate() throws Exception {

        final String templateURL = "foo";
        when(templateLoader.getTemplate(templateURL)).thenReturn(templateReader);
        assertNotNull(mustacheTemplateFactory.getTemplate(templateURL));
        verify(compiler).compile(templateReader);

    }

    @Test(expected = MustacheTemplateException.class)
    public final void testGetTemplateWithException() throws Exception {

        final String templateURL = "foo";
        when(templateLoader.getTemplate(templateURL)).thenThrow(new FileNotFoundException());
        mustacheTemplateFactory.getTemplate(templateURL);

    }


}