package ee.midaiganes.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ee.midaiganes.util.GuiceUtil;

public abstract class HttpServlet implements Servlet {
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        service((HttpServletRequest) request, (HttpServletResponse) response);
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
        this.config = null;
    }

    protected abstract void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected void autowire() {
        GuiceUtil.getInjector(config.getServletContext()).injectMembers(this);
    }

    protected ServletContext getServletContext() {
        return getServletConfig().getServletContext();
    }
}
