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

import static org.junit.Assert.assertNotNull;

import java.io.StringReader;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 */
public class MustacheViewResolverTest {

    private MustacheViewResolver viewResolver;
    private MustacheTemplateLoader templateLoader;
    private String viewName;

    @Before
    public void setUp() throws Exception {
        templateLoader = Mockito.mock(MustacheTemplateLoader.class);
        viewName = "viewname";

        viewResolver = new MustacheViewResolver();
        viewResolver.setTemplateLoader(templateLoader);
        viewResolver.setStandardsMode(false);
        viewResolver.setEscapeHTML(true);
        viewResolver.afterPropertiesSet();

    }

    @Test
    public void testBuildView() throws Exception {
        Mockito.doReturn(new StringReader("")).when(templateLoader).getTemplate(viewName);
        assertNotNull(viewResolver.buildView(viewName));
    }

    @Test
    public void testWithCustomCompiler() throws Exception {
        final Template template = Mockito.mock(Template.class);
        final Mustache.Compiler customCompiler = Mockito.mock(Mustache.Compiler.class);

        viewResolver = new MustacheViewResolver();
        viewResolver.setTemplateLoader(templateLoader);
        viewResolver.setStandardsMode(false);
        viewResolver.setEscapeHTML(true);
        viewResolver.setCompiler(customCompiler);
        viewResolver.afterPropertiesSet();

        Mockito.doReturn(new StringReader("")).when(templateLoader).getTemplate(viewName);
        Mockito.when(customCompiler.compile(Mockito.any(StringReader.class))).thenReturn(template);

        viewResolver.buildView(viewName);

        Mockito.verify(customCompiler).compile(Mockito.any(StringReader.class));
    }
}
