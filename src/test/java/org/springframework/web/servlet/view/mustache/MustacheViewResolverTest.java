/*
 * Copyright 2011-2014 the original author or authors.
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
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class MustacheViewResolverTest {

    private MustacheViewResolver viewResolver;

    @Mock
    private MustacheTemplateFactory templateFactory;

    @Mock
    private MustacheTemplate template;

    private final String viewName = "viewName";

    @Before
    public void setUp() throws Exception {
        viewResolver = new MustacheViewResolver(templateFactory);
    }

    @Test
    public void testBuildView() throws Exception {

        doReturn(template).when(templateFactory).getTemplate(viewName);

        assertNotNull(viewResolver.buildView(viewName));

        verify(templateFactory).getTemplate(viewName);
    }

}