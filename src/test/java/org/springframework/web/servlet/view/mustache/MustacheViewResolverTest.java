/*
 * Copyright 2012 the original author or authors.
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

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import com.github.mustachejava.Mustache;

/**
 * @author Eric D. White <eric@ericwhite.ca>
 */
@RunWith(JMock.class)
public class MustacheViewResolverTest {
	
	private Mockery context = new Mockery(){{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private final Mustache mustache = context.mock(Mustache.class);
	
	/**
	 * A basic test where no prefix is used.
	 */
	@Test
	public void resolvesViewWithoutPrefix() throws Exception {
		final String viewPath = "top-level.mustache";
		final MustacheTemplateLoader templateLoader = context.mock(MustacheTemplateLoader.class);

		context.checking(new Expectations(){{
			oneOf(templateLoader).setPrefix(with(any(String.class)));
			oneOf(templateLoader).compile(viewPath);will(returnValue(mustache));
		}});
		
		MustacheViewResolver r = new MustacheViewResolver();
		r.setTemplateLoader(templateLoader);
		r.afterPropertiesSet();

		AbstractUrlBasedView view = r.buildView(viewPath);
		assertNotNull(view);
	}
	
	/**
	 * Ensure the prefix is passed on to the template loader
	 * and that the template loader is called with a fully 
	 * resolved view path.
	 */
	@Test
	public void resolvesViewWithPrefix() throws Exception {
		final String viewPath = "WEB-INF/views/";
		final String viewName = "hello.mustache";
		
		final MustacheTemplateLoader templateLoader = context.mock(MustacheTemplateLoader.class);

		context.checking(new Expectations(){{
			oneOf(templateLoader).setPrefix(with(any(String.class)));
			oneOf(templateLoader).compile(viewPath + viewName);will(returnValue(mustache));
		}});

		MustacheViewResolver r = new MustacheViewResolver();
		r.setTemplateLoader(templateLoader);
		r.setPrefix(viewPath);
		r.afterPropertiesSet();

		AbstractUrlBasedView view = r.buildView(viewName);
		assertNotNull(view);
	}
}
