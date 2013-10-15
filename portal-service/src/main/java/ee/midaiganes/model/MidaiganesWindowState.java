package ee.midaiganes.model;

import javax.portlet.WindowState;

public class MidaiganesWindowState extends WindowState {

	public static final WindowState EXCLUSIVE = new WindowState("exclusive");

	private MidaiganesWindowState(String name) {
		super(name);
	}

}
