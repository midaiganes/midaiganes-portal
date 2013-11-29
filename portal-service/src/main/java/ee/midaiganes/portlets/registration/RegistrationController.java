package ee.midaiganes.portlets.registration;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.services.exceptions.DuplicateUsernameException;
import ee.midaiganes.util.StringUtil;

public class RegistrationController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        super.include("registration/view", request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        RegistrationData registrationData = new RegistrationData();
        registrationData.setUsername(request.getParameter("username"));
        registrationData.setPassword(request.getParameter("password"));
        registerUser(registrationData, request);
    }

    private void registerUser(RegistrationData registrationData, ActionRequest request) {
        if (!StringUtil.isEmpty(registrationData.getUsername()) && !StringUtil.isEmpty(registrationData.getPassword())) {
            try {
                long userid = BeanRepositoryUtil.getBean(UserRepository.class).addUser(registrationData.getUsername(), registrationData.getPassword());
                log.debug("user added: id = {}; name = {}", Long.valueOf(userid), registrationData.getUsername());
                request.setAttribute("success", Boolean.TRUE);
            } catch (DuplicateUsernameException e) {
                log.debug("duplicate username", e);
            }
        }
    }
}
