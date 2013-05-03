package ee.midaiganes.portlets.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ee.midaiganes.model.DefaultLayout;
import ee.midaiganes.model.Layout;

public class LayoutItem implements Serializable, Comparable<LayoutItem> {
	private static final long serialVersionUID = 1L;
	private final Layout layout;
	private final List<LayoutItem> childs = new ArrayList<>();

	public LayoutItem(Layout layout, List<Layout> layouts) {
		this.layout = layout;
		for (Layout l : layouts) {
			if (this.layout.getId() == l.getParentId()) {
				childs.add(new LayoutItem(l, layouts));
			}
		}
		Collections.sort(childs);
	}

	public static List<LayoutItem> getLayoutItems(List<Layout> layouts) {
		List<LayoutItem> list = new ArrayList<>();
		for (Layout layout : layouts) {
			if (layout.getParentId() == DefaultLayout.DEFAULT_LAYOUT_ID) {
				list.add(new LayoutItem(layout, layouts));
			}
		}
		Collections.sort(list);
		return list;
	}

	public Layout getLayout() {
		return layout;
	}

	public List<LayoutItem> getChilds() {
		return childs;
	}

	@Override
	public int compareTo(LayoutItem o) {
		return (int) (Math.abs(layout.getNr()) - Math.abs(o.layout.getNr()));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LayoutItem) {
			return layout.equals(((LayoutItem) o).layout) && childs.equals(((LayoutItem) o).childs);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return layout.hashCode();
	}
}
