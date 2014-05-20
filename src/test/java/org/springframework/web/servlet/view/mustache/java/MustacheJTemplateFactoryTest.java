/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.web.servlet.view.mustache.java;

import com.github.mustachejava.MustacheException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class MustacheJTemplateFactoryTest {

    private static final ClassPathResource TEST_TEMPLATE = new ClassPathResource("/test-template.html", MustacheJTemplateFactoryTest.class);

    @Mock
    private Resource resource;

    @Mock
    private ResourceLoader resourceLoader;

    private ArgumentCaptor<String> templateNameCaptor;

    private MustacheJTemplateFactory templateFactory;


    @Before
    public void setUp() throws Exception {

        templateNameCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(resource).when(resourceLoader).getResource(templateNameCaptor.capture());

        templateFactory = new MustacheJTemplateFactory();
        templateFactory.setResourceLoader(resourceLoader);
        templateFactory.setPrefix("");
    }


    @Test(expected = MustacheException.class)
    public void testResourceNotFound() throws Exception {
        doReturn(Boolean.FALSE).when(resource).exists();
        templateFactory.getTemplate("template.html");
        assertEquals(templateNameCaptor.getValue(), "template.html");
    }


    @Test(expected = MustacheException.class)
    public void testResourceIOException() throws Exception {
        doReturn(Boolean.TRUE).when(resource).exists();
        doThrow(new IOException()).when(resource).getInputStream();
        templateFactory.getTemplate("template.html");
    }

    @Test
    public void testResourceFound() throws Exception {

        templateFactory.setPrefix("prefix/");

        doReturn(Boolean.TRUE).when(resource).exists();

        doReturn(TEST_TEMPLATE.getInputStream())
                .when(resource)
                .getInputStream();

        doReturn(TEST_TEMPLATE.getFile()).when(resource).getFile();

        assertNotNull(templateFactory.getTemplate("test_template"));
        assertEquals(templateNameCaptor.getValue(), "prefix/test_template");
    }
}