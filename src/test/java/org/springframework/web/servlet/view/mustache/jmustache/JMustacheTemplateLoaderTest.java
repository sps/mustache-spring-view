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
package org.springframework.web.servlet.view.mustache.jmustache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
public class JMustacheTemplateLoaderTest {

    private static final ClassPathResource TEST_TEMPLATE = new ClassPathResource("/test-template.html",
            JMustacheTemplateLoaderTest.class);

    private Resource resource;
    private ResourceLoader resourceLoader;
    private JMustacheTemplateLoader loader;
    private ArgumentCaptor<String> templateNameCaptor;

    @Before
    public void setUp() throws Exception {
        templateNameCaptor = ArgumentCaptor.forClass(String.class);
        resource = Mockito.mock(Resource.class);
        resourceLoader = Mockito.mock(ResourceLoader.class);
        Mockito.doReturn(resource).when(resourceLoader).getResource(templateNameCaptor.capture());

        loader = new JMustacheTemplateLoader();
        loader.setResourceLoader(resourceLoader);
    }

    @Test(expected = FileNotFoundException.class)
    public void testResourceNotFound() throws Exception {
        Mockito.doReturn(Boolean.FALSE).when(resource).exists();
        loader.getTemplate("template.html");
        assertEquals(templateNameCaptor.getValue(), "template.html");
    }

    @Test
    public void testResourceFound() throws Exception {

        loader.setPrefix("prefix/");
        loader.setSuffix(".suffix");

        Mockito.doReturn(Boolean.TRUE).when(resource).exists();

        Mockito.doReturn(TEST_TEMPLATE.getInputStream())
                .when(resource)
                .getInputStream();

        Mockito.doReturn(TEST_TEMPLATE.getFile()).when(resource).getFile();

        assertNotNull(loader.getTemplate("test_template"));
        assertEquals(templateNameCaptor.getValue(), "prefix/test_template.suffix");
    }

}
