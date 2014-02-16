package ee.midaiganes.portlet.impl;

import java.io.Serializable;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import ee.midaiganes.portal.portletinstance.PortletNamespace;

public class StateAwareResponseImpl extends PortletResponseImpl implements StateAwareResponse {

	private PortletMode portletMode = PortletMode.VIEW;
	private WindowState windowState = WindowState.NORMAL;
	private final RenderParameterMap renderParameters;
	private final PortletRequest portletRequest;

	public StateAwareResponseImpl(HttpServletResponse response, PortletRequest portletRequest, PortletNamespace namespace) {
		super(response, namespace);
		this.portletRequest = portletRequest;
		renderParameters = new RenderParameterMap();
		RenderParameterUtil.setRenderparameterMap(portletRequest, namespace, renderParameters);
	}

	@Override
	public Map<String, String[]> getRenderParameterMap() {
		return renderParameters.getCopy();
	}

	@Override
	public PortletMode getPortletMode() {
		return portletMode;
	}

	@Override
	public WindowState getWindowState() {
		return windowState;
	}

	@Override
	public void removePublicRenderParameter(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEvent(QName name, Serializable value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWindowState(WindowState windowState) throws WindowStateException {
		if (!portletRequest.isWindowStateAllowed(windowState)) {
			throw new WindowStateException("Window state not allowed: " + windowState, windowState);
		}
		this.windowState = windowState;
	}

	@Override
	public void setPortletMode(PortletMode portletMode) throws PortletModeException {
		if (!portletRequest.isPortletModeAllowed(portletMode)) {
			throw new PortletModeException("Portlet mode not allowed: " + portletMode, portletMode);
		}
		this.portletMode = portletMode;
	}

	@Override
	public void setRenderParameters(Map<String, String[]> parameters) {
		renderParameters.putAll(parameters);
	}

	@Override
	public void setRenderParameter(String key, String value) {
		renderParameters.put(key, value);
	}

	@Override
	public void setRenderParameter(String key, String[] values) {
		renderParameters.put(key, values);
	}

	@Override
	public void setEvent(String name, Serializable value) {
		// TODO Auto-generated method stub

	}

}
