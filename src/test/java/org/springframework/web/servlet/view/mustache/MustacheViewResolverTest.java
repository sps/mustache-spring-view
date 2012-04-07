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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.mustachejava.Mustache;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * @author Eric D. White <eric@ericwhite.ca>
 */
public class MustacheViewResolverTest {

	private Mustache mustache;
    private MustacheViewResolver viewResolver;
    private MustacheTemplateLoader templateLoader;
    private String viewName;

    @Before
    public void setUp() throws Exception {
    	mustache = Mockito.mock(Mustache.class);
        templateLoader = Mockito.mock(MustacheTemplateLoader.class);
        viewName = "viewname";

        viewResolver = new MustacheViewResolver();
        viewResolver.setTemplateLoader(templateLoader);
        viewResolver.afterPropertiesSet();
    }

    @Test
    public void testBuildView() throws Exception {
        Mockito.doReturn(mustache).when(templateLoader).compile(viewName);
        assertNotNull(viewResolver.buildView(viewName));
    }
}
