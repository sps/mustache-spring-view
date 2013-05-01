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

import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractTemplateView;

import com.samskivert.mustache.Template;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 */
public class MustacheView extends AbstractTemplateView {

    private Template template;

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        response.setContentType(getContentType());
        final Writer writer = response.getWriter();
        template.execute(model, writer);
        writer.flush();
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Template getTemplate() {
        return template;
    }
}
