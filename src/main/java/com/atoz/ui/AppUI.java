package com.atoz.ui;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.UI;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.atoz.dao.UserDAOImpl;
import com.atoz.model.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

@PreserveOnRefresh
@Theme("chameleon")
public class AppUI extends UI {

  private ApplicationContext applicationContext;

  @Override
  protected void init(VaadinRequest request) {
    WrappedSession session = request.getWrappedSession();
    HttpSession httpSession = ((WrappedHttpSession) session).getHttpSession();
    ServletContext servletContext = httpSession.getServletContext();
    applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

    Navigator navigator = new Navigator(this, this);

    navigator.addView("login", LoginView.class);
    navigator.addView("user", UserView.class);

    navigator.navigateTo("login");

    setNavigator(navigator);
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }
}
