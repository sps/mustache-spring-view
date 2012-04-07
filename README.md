[mustache.java](https://github.com/spullara/mustache.java) view for [spring3](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html)
---------------------------
What is mustache: [mustache.js](http://mustache.github.com/mustache.5.html)

This is a version of [mustache-spring-view](https://github.com/sps/mustache-spring-view) that
 works with [mustache.java](https://github.com/spullara/mustache.java).

Getting Started
-----------------
See: http://blog.springsource.com/2011/01/04/green-beans-getting-started-with-spring-mvc/

And the following sections

Maven dependency
-----------------

    <dependencies>
        ...
        <dependency>
            <groupId>com.github.ericdwhite</groupId>
	        <artifactId>mustache.java-spring-webmvc</artifactId>
            <version>1.0-RELEASE</version>
        </dependency>
        ...
    </dependencies>
    
    <repositories>
    	...
  		<repository>
    		<id>Sonatype Snapshots</id>
    		<url>https://oss.sonatype.org/content/repositories/snapshots/</url>
  		</repository>
  		...
	</repositories>

Spring configuration
-------------

    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <property name="prefix" value="/WEB-INF/views/" />
        <property name="suffix" value=".html" />
        <property name="templateLoader">
            <bean class="org.springframework.web.servlet.view.mustache.MustacheTemplateLoader"" />
        </property>
    </bean>
    
Example
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
