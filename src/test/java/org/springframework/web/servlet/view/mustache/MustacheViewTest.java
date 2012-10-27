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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mustachejava.Mustache;

/**
 * @author Eric D. White <eric@ericwhite.ca>
 */
@RunWith(JMock.class)
public class MustacheViewTest {

	private final Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void rendersAModelUsingItsTemplate() throws Exception {
		final Map<String, Object> model = Collections.<String, Object> emptyMap();

		HttpServletRequest UNUSED_REQUEST = null;
		final HttpServletResponse response = context.mock(HttpServletResponse.class);
		final PrintWriter writer = context.mock(PrintWriter.class);
		final Mustache template = context.mock(Mustache.class);

		context.checking(new Expectations() {
			{
				oneOf(response).setContentType(with(any(String.class)));
				oneOf(response).setCharacterEncoding(with(any(String.class)));
				oneOf(response).getWriter();
				will(returnValue(writer));
				oneOf(template).execute(writer, model);
				oneOf(writer).flush();
			}
		});

		MustacheView view = new MustacheView();
		view.setTemplate(template);
		view.renderMergedTemplateModel(model, UNUSED_REQUEST, response);

		assertThat(view.getTemplate(), equalTo(template));
	}
}
