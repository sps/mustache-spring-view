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

import static java.util.Collections.reverse;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.startsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimLeadingCharacter;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.TemplateLoader;


/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 */
public class MustacheViewResolver extends AbstractTemplateViewResolver implements ViewResolver,
        InitializingBean, ResourceLoaderAware {

	private String innerPartialAlias = "inner";
	private String layoutPartialAlias = "layout";
	private String layoutTemplateName = "layout";
	
	private ResourceLoader resourceLoader;
	private MustacheTemplateLoader templateLoader;
    private Compiler compiler;

    private boolean standardsMode = false;
    private boolean escapeHTML = true;
	
    public MustacheViewResolver() {
        setViewClass(MustacheView.class);
        setPrefix("/WEB-INF/views/");
        setSuffix(".mustache");
    }

    @Override
    protected Class<?> requiredViewClass() {
        return MustacheView.class;
    }
    
    @Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
    
	public void setTemplateLoader(MustacheTemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}
    
    @Override
    public void afterPropertiesSet() throws Exception {
    	if (templateLoader == null) {
	    	templateLoader = new MustacheTemplateLoader();
	    	templateLoader.setPrefix(getPrefix());
	    	templateLoader.setSuffix(getSuffix());
	    	templateLoader.setResourceLoader(resourceLoader);
    	}
        compiler = Mustache.compiler()
                .escapeHTML(escapeHTML)
                .standardsMode(standardsMode)
                .withLoader(templateLoader);
    }
    
    

    /**
     * Whether or not standards mode is enabled.
     * 
     * disabled by default.
     */
    public void setStandardsMode(boolean standardsMode) {
        this.standardsMode = standardsMode;
    }

    /**
     * Whether or not HTML entities are escaped by default.
     * 
     * default is true.
     */
    public void setEscapeHTML(boolean escapeHTML) {
        this.escapeHTML = escapeHTML;
    }    

    
    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {

    	final MustacheView view = (MustacheView) super.buildView(viewName);
    	
    	URI uri = new URI(viewName);
    	String newViewName = uri.getPath();
    	Map<String,String> aliases = parseQueryString(uri.getQuery());
    	view.setPartialAliases(aliases);
    	
    	/*
    	 * Normalize the path.
    	 */
    	if (endsWithIgnoreCase(getPrefix(), "/") && startsWithIgnoreCase(newViewName, "/")) {
    		newViewName = trimLeadingCharacter(newViewName, '/');
    	}
    	TemplateLoader templateLoader = this.templateLoader.withPartialAliases(aliases);
    	Compiler compiler = this.compiler.withLoader(templateLoader);
    	/*
    	 * Reset the view url
    	 */
    	view.setUrl(getPrefix() + newViewName + getSuffix());
    	
        aliases.put(getInnerPartialAlias(), newViewName);
       /*
        * Go find the parent layout template if one is not defined.
        * We walk up the tree to find the parent.
        */
        if ( ! hasText(aliases.get(getLayoutPartialAlias())) ) {
        	String parentTemplate = findParentLayoutTemplate(newViewName, templateLoader);
        	if (hasText(parentTemplate))
        		aliases.put(getLayoutPartialAlias(), parentTemplate);
        }

        String templateName = null;
        
        boolean found = hasText(templateName = aliases.get(getLayoutPartialAlias())) 
        	|| hasText(templateName = aliases.get(getInnerPartialAlias()));
        
        if (found)
        	view.setTemplate(compiler.compile(templateLoader.getTemplate(templateName)));
        else
        	throw new IllegalStateException("Body is missing");
        
        return view;
    }

    /**
     * Finds the parent layout template that will surround the inner layout template.
     * By default it will look in the same directory as the inner template for a template called
     * 'layout'.
     * @param newViewName not null.
     * @param templateLoader not null.
     * @return null if not found.
     * @throws Exception
     */
	protected String findParentLayoutTemplate(String newViewName, TemplateLoader templateLoader) throws Exception {
		String parentTemplate = null;
		String[] paths = StringUtils.split(newViewName, "/");
		if (paths == null) return null;
		List<String> ps = new ArrayList<String>();
		String cur = "";
		for (String p : paths) {
			if (hasText(p)) {
				cur = cur + "/" + p;
				ps.add(p);
			}
		}
		reverse(ps);
		if (! ps.isEmpty()) {
			ps.remove(0);
			for (String p : ps) {
				try {
					String n = p + "/" + getLayoutTemplateName();
					templateLoader.getTemplate(n);
					parentTemplate = n;
					break;
				} catch (FileNotFoundException e1) {
					//Ignore as there is no layout template.
				}
			}
		}
		return parentTemplate;
	}
    
    /**
     * Strategy to parse the query string for template aliases.
     * This is hardly robust for all URI query strings but works for now.
     * @param query the query string <strong>not</strong> including the leading '?'.
     * @return not null.
     */
	protected Map<String, String> parseQueryString(String query) {
		Map<String, String> params = new HashMap<String, String>();
		if (! hasText(query) ) return params;
		try {
			for (String param : query.split("&")) {
				String pair[] = param.split("=");
				String key = URLDecoder.decode(pair[0], "UTF-8");
				String value = "";
				if (pair.length > 1) {
					value = URLDecoder.decode(pair[1], "UTF-8");
				}
				params.put(key, value);
			}
			return params;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String getInnerPartialAlias() {
		return innerPartialAlias;
	}	
	public void setInnerPartialAlias(String innerPartialAlias) {
		this.innerPartialAlias = innerPartialAlias;
	}

	public String getLayoutPartialAlias() {
		return layoutPartialAlias;
	}
	public void setLayoutPartialAlias(String layoutPartialAlias) {
		this.layoutPartialAlias = layoutPartialAlias;
	}
	
	public String getLayoutTemplateName() {
		return layoutTemplateName;
	}

	public void setLayoutTemplateName(String layoutTemplateName) {
		this.layoutTemplateName = layoutTemplateName;
	}
	        

}
