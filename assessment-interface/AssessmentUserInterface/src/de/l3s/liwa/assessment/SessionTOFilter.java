package de.l3s.liwa.assessment;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/** Filter for session timeouts */
public class SessionTOFilter implements Filter {

    public SessionTOFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(javax.servlet.ServletRequest servletRequest, 
                         javax.servlet.ServletResponse servletResponse, 
                         javax.servlet.FilterChain filterChain) 
        throws java.io.IOException, javax.servlet.ServletException {

        
        HttpServletRequest hreq = (HttpServletRequest)servletRequest;
        HttpServletResponse hres = (HttpServletResponse)servletResponse;
        HttpSession session = hreq.getSession();
        String url = hreq.getRequestURI();
        boolean isNew = session.isNew();
        boolean loginPage;
        loginPage = (url.endsWith("login.jsp") || url.endsWith("login.jsf")) ;
        if (!loginPage) {

            if (isSessionInvalid(hreq)) {
                System.out.println("Session expired, redirecting to login page");
                // Set a message so that login page can pick it up and display it 
                session.setAttribute("LOGIN_MESSAGE", "Please sign on on this page because you did not sign on or your session is timed out.");
                hres.sendRedirect("/liwa/");
                return;
            }
        }
        /* deliver request to next filter */
        if (servletRequest != null && servletResponse!=null && !isNew) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
            && !httpServletRequest.isRequestedSessionIdValid();
        return sessionInValid;
    }

    public void destroy() {
    }
}

