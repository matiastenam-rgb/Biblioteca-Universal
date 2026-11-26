package web;

import data.Connect;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

@WebListener
public class AppListener implements ServletContextListener, HttpSessionListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Connect.create();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Connect.close();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {}

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {}
}
