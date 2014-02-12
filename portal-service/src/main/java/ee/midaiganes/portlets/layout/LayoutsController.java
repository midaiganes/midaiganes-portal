package ee.midaiganes.portlets.layout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.beans.BeanRepositoryUtil;
import ee.midaiganes.portal.layout.Layout;
import ee.midaiganes.portal.layout.LayoutRepository;
import ee.midaiganes.portal.layout.LayoutTitle;
import ee.midaiganes.portal.pagelayout.PageLayoutName;
import ee.midaiganes.portal.pagelayout.PageLayoutRepository;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.LanguageRepository;
import ee.midaiganes.services.exceptions.IllegalFriendlyUrlException;
import ee.midaiganes.services.exceptions.IllegalPageLayoutException;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

public class LayoutsController extends BasePortlet {
    private static final Logger log = LoggerFactory.getLogger(LayoutsController.class);

    private final LayoutRepository layoutRepository;
    private final LanguageRepository languageRepository;
    private final PageLayoutRepository pageLayoutRepository;

    public LayoutsController() {
        this.layoutRepository = BeanRepositoryUtil.getBean(LayoutRepository.class);
        this.languageRepository = BeanRepositoryUtil.getBean(LanguageRepository.class);
        this.pageLayoutRepository = BeanRepositoryUtil.getBean(PageLayoutRepository.class);
    }

    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        if ("edit-layout".equals(action) && id != null) {
            editLayoutView(id, request, response);
        } else {
            addLayoutView(request, response);
        }
    }

    private void addLayoutView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        request.setAttribute("layouts", LayoutItem.getLayoutItems(layoutRepository.getLayouts(RequestUtil.getPageDisplay(request).getLayoutSet().getId())));
        super.include("layouts/add-page", request, response);
    }

    private void editLayoutView(String id, RenderRequest request, RenderResponse response) throws PortletException, IOException {
        if (StringUtil.isNumber(id)) {
            Layout layout = layoutRepository.getLayout(Long.parseLong(id));
            LayoutModel layoutModel = new LayoutModel();
            layoutModel.setDefaultLayoutTitleLanguageId(languageRepository.getLanguageId(layout.getDefaultLayoutTitleLanguageId()));
            Long parentId = layout.getParentId();
            layoutModel.setParentId(Long.toString(parentId == null ? 0 : parentId.longValue()));
            layoutModel.setUrl(layout.getFriendlyUrl());
            for (String languageId : languageRepository.getSupportedLanguageIds()) {
                layoutModel.getLayoutTitles().put(languageId, StringPool.EMPTY);
            }
            for (LayoutTitle lt : layout.getLayoutTitles()) {
                layoutModel.getLayoutTitles().put(languageRepository.getLanguageId(lt.getLanguageId()), lt.getTitle());
            }
            request.setAttribute("editLayoutModel", layoutModel);
            request.setAttribute("layout", layout);
            request.setAttribute("layouts", layoutRepository.getLayouts(RequestUtil.getPageDisplay(request).getLayoutSet().getId()));

            super.include("layouts/edit-layout", request, response);
        } else {
            addLayoutView(request, response);
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) {
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        if ("delete".equals(action) && id != null) {
            deleteLayoutAction(id);
        } else if ("move-up".equals(action) && id != null) {
            moveUpAction(id);
        } else if ("move-down".equals(action) && id != null) {
            moveDownAction(id);
        } else if ("edit-layout".equals(action) && id != null) {
            try {
                editLayoutAction(id, this.getAddLayoutModel(request));
            } catch (IllegalFriendlyUrlException | IllegalPageLayoutException e) {
                log.warn(e.getMessage(), e);
            }
        } else {
            try {
                addLayoutAction(this.getAddLayoutModel(request), request);
            } catch (IllegalFriendlyUrlException | IllegalPageLayoutException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private void addLayoutAction(LayoutModel layout, PortletRequest request) throws IllegalFriendlyUrlException, IllegalPageLayoutException {
        if (layoutRepository.isFriendlyUrlValid(layout.getUrl())) {
            Long parentId = getParentId(layout.getParentId());
            long layoutSetId = RequestUtil.getPageDisplay(request).getLayoutSet().getId();
            PageLayoutName defaultPageLayoutName = pageLayoutRepository.getDefaultPageLayout().getPageLayoutName();
            // TODO
            long languageId = languageRepository.getId(languageRepository.getSupportedLanguageIds().get(0)).longValue();
            layoutRepository.addLayout(layoutSetId, layout.getUrl(), null, defaultPageLayoutName, parentId, languageId);
        } else {
            log.warn("invalid friendly url '{}'", layout.getUrl());
        }
    }

    private void deleteLayoutAction(String id) {
        if (StringUtil.isNumber(id)) {
            layoutRepository.deleteLayout(Long.parseLong(id));
        }
    }

    private void editLayoutAction(String id, LayoutModel layoutModel) throws IllegalFriendlyUrlException, IllegalPageLayoutException {
        if (StringUtil.isNumber(id)) {
            Layout layout = layoutRepository.getLayout(Long.parseLong(id));
            if (layout != null) {
                updateLayout(layoutModel, layout);
            } else {
                log.warn("Invalid layout id '{}'", id);
            }
        }
    }

    private void moveUpAction(String id) {
        if (StringUtil.isNumber(id)) {
            if (!layoutRepository.moveLayoutUp(Long.parseLong(id))) {
                log.warn("Layout not moved up: '{}'", id);
            }
        } else {
            log.warn("Can't move layout up: invalid id '{}'", id);
        }
    }

    private void moveDownAction(String id) {
        if (StringUtil.isNumber(id)) {
            if (!layoutRepository.moveLayoutDown(Long.parseLong(id))) {
                log.warn("Layout not moved down: '{}'", id);
            }
        } else {
            log.warn("Can't move layout down: invalid id '{}'", id);
        }
    }

    private void updateLayout(LayoutModel layoutModel, Layout layout) throws IllegalFriendlyUrlException, IllegalPageLayoutException {
        PageLayoutName pageLayoutName = new PageLayoutName(layout.getPageLayoutId());
        Long parentId = StringUtil.isEmpty(layoutModel.getParentId()) ? null : Long.valueOf(layoutModel.getParentId());
        long defaultLayoutTitleLanguageId = languageRepository.getId(layoutModel.getDefaultLayoutTitleLanguageId()).longValue();
        layoutRepository.updateLayout(layoutModel.getUrl(), pageLayoutName, parentId, defaultLayoutTitleLanguageId, layout.getId());
        updateLayoutTitles(layoutModel, layout);
    }

    private void updateLayoutTitles(LayoutModel layoutModel, Layout layout) {
        for (String languageId : languageRepository.getSupportedLanguageIds()) {
            Long l = languageRepository.getId(languageId);
            String layoutTitle = layoutModel.getLayoutTitles().get(languageId);
            if (!StringUtil.isEmpty(layoutTitle)) {
                addOrUpdateLayoutTitle(layout, languageId, layoutTitle);
            } else if (layout.getLayoutTitle(l.longValue()) != null) {
                deleteLayoutTitle(layout.getId(), languageId);
            }
        }
    }

    private void deleteLayoutTitle(long layoutId, String languageId) {
        Long lid = languageRepository.getId(languageId);
        if (lid != null) {
            layoutRepository.deleteLayoutTitle(layoutId, lid.longValue());
        } else {
            log.warn("Can't delete LayoutTitle ({}). Invalid language id '{}'", Long.valueOf(layoutId), languageId);
        }
    }

    private void addOrUpdateLayoutTitle(Layout layout, String languageId, String layoutTitle) {
        Long lid = languageRepository.getId(languageId);
        if (lid != null) {
            addOrUpdateLayoutTitle(layout, layoutTitle, lid.longValue());
        } else {
            log.warn("Can't add/update LayoutTitle. Invalid language id '{}'", languageId);
        }
    }

    private void addOrUpdateLayoutTitle(Layout layout, String val, long lid) {
        if (layout.getLayoutTitle(lid) == null) {
            layoutRepository.addLayoutTitle(layout.getId(), lid, val);
        } else {
            layoutRepository.updateLayoutTitle(layout.getId(), lid, val);
        }
    }

    private LayoutModel getAddLayoutModel(PortletRequest request) {
        LayoutModel model = new LayoutModel();
        model.setDefaultLayoutTitleLanguageId(request.getParameter("defaultLayoutTitleLanguageId"));
        model.setParentId(request.getParameter("parentId"));
        model.setUrl(request.getParameter("url"));
        Map<String, String> layoutTitles = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            if (key.length() > 14 && key.startsWith("layoutTitles[") && key.endsWith("]")) {
                String[] values = entry.getValue();
                layoutTitles.put(key.substring(12, key.length() - 2), values != null && values.length > 0 ? values[1] : null);
            }
        }
        log.debug("Layout titles are: '{}'", layoutTitles);
        model.setLayoutTitles(layoutTitles);
        return model;
    }

    private Long getParentId(String parentId) {
        if (StringUtil.isEmpty(parentId)) {
            return null;
        }
        return Long.valueOf(parentId);
    }
}
