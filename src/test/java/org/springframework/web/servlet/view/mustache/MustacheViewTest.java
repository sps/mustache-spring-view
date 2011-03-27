/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.view.mustache;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.samskivert.mustache.Template;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 */
public class MustacheViewTest {

    private Template template;
    private HttpServletResponse response;
    private PrintWriter mockWriter;
    private MustacheView view;

    @Before
    public void setUp() throws Exception {

        template = Mockito.mock(Template.class);
        response = Mockito.mock(HttpServletResponse.class);
        mockWriter = Mockito.mock(PrintWriter.class);

        view = new MustacheView();
        view.setTemplate(template);
    }

    @Test
    public void testRenderMergedTemplateModel() throws Exception {
        
        final Map<String, Object> model = Collections.<String, Object> emptyMap();
        
        Mockito.doReturn(mockWriter).when(response).getWriter();

        view.renderMergedTemplateModel(model, null, response);

        Mockito.verify(template).execute(model, mockWriter);
        Mockito.verify(mockWriter).flush();

        assertEquals(template, view.getTemplate());
    }
}
