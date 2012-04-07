[mustache.js](http://mustache.github.com/mustache.5.html) view for [spring3](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html)
---------------------------

This is a version of (https://github.com/sps/mustache-spring-view) that works with mustache.java.

mustache.java: (https://github.com/spullara/mustache.java)

maven dependency
-----------------

    <dependency>
        <groupId>com.github.ericdwhite.mustachejava-spring-webmvc</groupId>
        <artifactId>mustache-spring-view</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>


spring config
-------------

    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <property name="prefix" value="/WEB-INF/views/" />
        <property name="suffix" value=".html" />
        <property name="templateLoader">
            <bean class="org.springframework.web.servlet.view.mustache.MustacheTemplateLoader"" />
        </property>
    </bean>
    
    
example
-------------
WEB-INF/views/parent.html

    <html>
        <head>
            <title>Home</title>
        </head>
        <body>
            <h1>Hello world!</h1>
            <h2>{{ token }}</h2>
            
            <!-- Partial Support -->
            {{> footer }}
        </body>
    </html>

WEB-INF/views/footer.html
    
    <div id="#footer">
        <p>Copyright (C) 2012, Example Inc.</p>
    </div>

A Controller
-------------

    @Controller
    public class HelloWorldController {
    
    	@RequestMapping(value="/hello")
    	public String hello(Model m) {
    		m.addAttribute("token", new java.util.Date());
    		return "parent";
    	}
    }
