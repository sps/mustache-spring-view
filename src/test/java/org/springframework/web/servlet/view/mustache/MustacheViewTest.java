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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class MustacheViewTest {

    @Mock
    private MustacheTemplate template;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter mockWriter;

    private MustacheView view;

    @Before
    public void setUp() throws Exception {

        view = new MustacheView();
        view.setTemplate(template);
    }

    @Test
    public void testRenderMergedTemplateModel() throws Exception {

        final Map<String, Object> model = Collections.<String, Object>emptyMap();

        doReturn(mockWriter).when(response).getWriter();

        view.renderMergedTemplateModel(model, null, response);

        verify(template).execute(model, mockWriter);
        verify(mockWriter).flush();

        assertEquals(template, view.getTemplate());
    }
    
    
	
}
