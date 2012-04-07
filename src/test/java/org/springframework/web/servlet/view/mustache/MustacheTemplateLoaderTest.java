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
import static org.mockito.Matchers.anyString;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * @author Eric D. White <eric@ericwhite.ca>
 */
public class MustacheTemplateLoaderTest {

	private static final ClassPathResource TEST_TEMPLATE = new ClassPathResource(
			"/WEB-INF/views/test-template.html",
			MustacheTemplateLoaderTest.class);

	private static final ClassPathResource TEST_PARENT_TEMPLATE = new ClassPathResource(
			"/WEB-INF/views/test-parent.html", MustacheTemplateLoaderTest.class);

	private static final ClassPathResource TEST_PARTIAL_TEMPLATE = new ClassPathResource(
			"/WEB-INF/views/test-partial.html",
			MustacheTemplateLoaderTest.class);

	private ResourceLoader resourceLoader;
	private MustacheTemplateLoader loader;

	private Resource anyResource;
	private Resource invalidResource;
	private Resource parentResource;
	private Resource partialResource;

	@Before
	public void setUp() throws Exception {
		anyResource = Mockito.mock(Resource.class);
		invalidResource = Mockito.mock(Resource.class);
		parentResource = Mockito.mock(Resource.class);
		partialResource = Mockito.mock(Resource.class);

		resourceLoader = Mockito.mock(ResourceLoader.class);
		Mockito.doReturn(anyResource).when(resourceLoader)
				.getResource(anyString());
		Mockito.doReturn(invalidResource).when(resourceLoader)
				.getResource("/WEB-INF/views/invalid");
		Mockito.doReturn(parentResource).when(resourceLoader)
				.getResource("/WEB-INF/views/test-parent.html");
		Mockito.doReturn(partialResource).when(resourceLoader)
				.getResource("/WEB-INF/views/test-partial.html");

		loader = new MustacheTemplateLoader();
		loader.setPrefix("/WEB-INF/views/");
		loader.setResourceLoader(resourceLoader);
	}

	@Test(expected = RuntimeException.class)
	public void testResourceNotFound() throws Exception {
		Mockito.doReturn(Boolean.FALSE).when(anyResource).exists();
		loader.compile("");
	}

	@Test(expected = RuntimeException.class)
	public void testResourceFailedToLoad() throws Exception {
		Mockito.doReturn(Boolean.TRUE).when(invalidResource).exists();
		Mockito.doReturn(null)
				.when(invalidResource).getInputStream();		
		loader.compile("invalid");
	}

	@Test
	public void testResourceFound() throws Exception {
		Mockito.doReturn(Boolean.TRUE).when(anyResource).exists();
		Mockito.doReturn(TEST_TEMPLATE.getInputStream()).when(anyResource)
				.getInputStream();
		Mockito.doReturn(TEST_TEMPLATE.getFile()).when(anyResource).getFile();

		assertNotNull(loader.compile(""));
	}

	/**
	 * This verifies that partials don't need to be fully qualified with the
	 * prefix.
	 */
	@Test
	public void testPartial() throws Exception {
		Mockito.doReturn(Boolean.TRUE).when(parentResource).exists();
		Mockito.doReturn(Boolean.TRUE).when(partialResource).exists();

		Mockito.doReturn(TEST_PARENT_TEMPLATE.getInputStream())
				.when(parentResource).getInputStream();
		Mockito.doReturn(TEST_PARTIAL_TEMPLATE.getInputStream())
				.when(partialResource).getInputStream();

		Mockito.doReturn(TEST_PARENT_TEMPLATE.getFile()).when(parentResource)
				.getFile();
		assertNotNull(loader.compile("/WEB-INF/views/test-parent.html"));
	}
}
