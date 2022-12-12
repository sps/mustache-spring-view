package org.springframework.web.servlet.view.mustache.java;

import com.github.mustachejava.Mustache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.Writer;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MustacheJTemplateTest {

    @Mock
    private Mustache mockTemplate;

    @Mock
    private Object context;

    @Mock
    private Object parentContext;

    @Mock
    private Writer out;

    private MustacheJTemplate template;

    @Before
    public void setUp() throws Exception {
        template = new MustacheJTemplate(mockTemplate);
    }

    @Test
    public void testExecute() throws Exception {
        template.execute(context, out);
        verify(mockTemplate).execute(out, context);
    }

    @Test
    public void testExecuteParentContext() throws Exception {
      template.execute(context, parentContext, out);
      verify(mockTemplate).execute(out, new Object[]{context, parentContext});
    }
}