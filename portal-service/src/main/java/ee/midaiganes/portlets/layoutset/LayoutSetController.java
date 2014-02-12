package ee.midaiganes.portlets.layoutset;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.model.ThemeName;
import ee.midaiganes.portal.layoutset.LayoutSet;
import ee.midaiganes.portal.layoutset.LayoutSetRepository;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.ThemeRepository;
import ee.midaiganes.util.StringUtil;

public class LayoutSetController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(LayoutSetController.class);

    private final LayoutSetRepository layoutSetRepository;
    private final ThemeRepository themeRepository;

    public LayoutSetController() {
        this.layoutSetRepository = BeanRepositoryUtil.getBean(LayoutSetRepository.class);
        this.themeRepository = BeanRepositoryUtil.getBean(ThemeRepository.class);
    }

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String id = request.getParameter("id");
        request.setAttribute("addLayoutSetModel", new LayoutSetModel());
        if (id != null && "edit-layout-set".equals(request.getParameter("action"))) {
            editLayoutSetView(id, request, response);
        } else {
            addLayoutSetView(request, response);
        }
    }

    private void addLayoutSetView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        request.setAttribute("layoutSets", layoutSetRepository.getLayoutSets());
        request.setAttribute("themes", themeRepository.getThemes());
        super.include("layout-set/add-layout-set", request, response);
    }

    private void editLayoutSetView(String id, RenderRequest request, RenderResponse response) throws PortletException, IOException {
        try {
            LayoutSet layoutSet = layoutSetRepository.getLayoutSet(Long.parseLong(id));
            LayoutSetModel layoutSetModel = new LayoutSetModel();
            ThemeName themeName = layoutSet.getThemeName();
            layoutSetModel.setFullThemeName(themeName != null ? themeName.getFullName() : null);
            layoutSetModel.setHost(layoutSet.getVirtualHost());
            layoutSetModel.setId(Long.toString(layoutSet.getId()));
            request.setAttribute("editLayoutSetModel", layoutSetModel);
            request.setAttribute("themes", themeRepository.getThemes());
            super.include("layout-set/edit-layout-set", request, response);
        } catch (NumberFormatException e) {
            log.debug(e.getMessage(), e);
            addLayoutSetView(request, response);
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter("action");
        LayoutSetModel layoutSetModel = new LayoutSetModel();
        layoutSetModel.setFullThemeName(request.getParameter("fullThemeName"));
        layoutSetModel.setHost(request.getParameter("host"));
        layoutSetModel.setId(request.getParameter("id"));
        if ("add-layout-set".equals(action)) {
            addLayoutSetAction(layoutSetModel);
        } else if ("edit-layout-set".equals(action) && layoutSetModel.getId() != null) {
            editLayoutSetAction(layoutSetModel);
        }
    }

    private void addLayoutSetAction(LayoutSetModel layoutSetModel) {
        if (!StringUtil.isEmpty(layoutSetModel.getHost())) {
            String fullThemeName = layoutSetModel.getFullThemeName();
            ThemeName themeName = StringUtil.isEmpty(fullThemeName) ? null : new ThemeName(fullThemeName);
            layoutSetRepository.addLayoutSet(layoutSetModel.getHost(), themeName);
        }
    }

    private void editLayoutSetAction(LayoutSetModel layoutSetModel) {
        if (!StringUtil.isEmpty(layoutSetModel.getHost()) && StringUtil.isNumber(layoutSetModel.getId())) {
            String fullThemeName = layoutSetModel.getFullThemeName();
            ThemeName themeName = StringUtil.isEmpty(fullThemeName) ? null : new ThemeName(fullThemeName);
            layoutSetRepository.updateLayoutSet(Long.parseLong(layoutSetModel.getId()), layoutSetModel.getHost(), themeName);
        }
    }
}
