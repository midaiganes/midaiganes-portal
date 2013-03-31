package ee.midaiganes.model;

import java.util.List;

import javax.portlet.PortletRequest;

public abstract class PaginatorContext<T> {

	public abstract List<String> getHeaders();

	public abstract int getElementsCount();

	public abstract List<T> getElements(int start, int count);

	public List<T> getElements() {
		int itemsOnPage = getItemsOnPage();
		int currentPageNumber = getCurrentPageNumber();
		return getElements(currentPageNumber * itemsOnPage, itemsOnPage);
	}

	public int getNumberOfPages() {
		int elementsCount = getElementsCount();
		int itemsOnPage = getItemsOnPage();
		return (elementsCount / itemsOnPage) + (elementsCount % itemsOnPage > 0 ? 1 : 0);
	}

	public abstract int getCurrentPageNumber();

	public int getItemsOnPage() {
		return 20;
	}

	public static abstract class AbstractPortletPaginatorContext<T> extends PaginatorContext<T> {
		private final PortletRequest request;
		private static final String START = "start";
		private static final String ITEMS_ON_PAGE = "items-on-page";

		public AbstractPortletPaginatorContext(PortletRequest request) {
			this.request = request;
		}

		@Override
		public int getCurrentPageNumber() {
			return getCurrentPageNumber(request, getItemsOnPage(), getElementsCount());
		}

		@Override
		public int getItemsOnPage() {
			return getItemsOnPage(request);
		}

		protected static int getCurrentPageNumber(PortletRequest request, int itemsOnPage, int elementsCount) {
			try {
				int currentPageNumber = Math.max(Integer.parseInt(request.getParameter(START)), 0);
				return currentPageNumber * itemsOnPage > elementsCount ? 0 : currentPageNumber;
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		protected static int getItemsOnPage(PortletRequest request) {
			try {
				return Math.max(Integer.parseInt(request.getParameter(ITEMS_ON_PAGE)), 0);
			} catch (NumberFormatException e) {
				return 20;
			}
		}
	}

	public static class PortletPaginatorContext<T> extends AbstractPortletPaginatorContext<T> {
		private final int elementsCount;
		private final List<T> elements;
		private List<String> headers;

		public PortletPaginatorContext(int elementsCount, List<T> elements, List<String> headers, PortletRequest request) {
			super(request);
			this.elementsCount = elementsCount;
			this.elements = elements;
		}

		@Override
		public int getElementsCount() {
			return elementsCount;
		}

		@Override
		public List<T> getElements(int start, int count) {
			return elements;
		}

		@Override
		public List<String> getHeaders() {
			return headers;
		}

		public static int[] getStartAndCount(PortletRequest request, int elementsCount) {
			int itemsOnPage = getItemsOnPage(request);
			return new int[] { itemsOnPage * getCurrentPageNumber(request, itemsOnPage, elementsCount), itemsOnPage };
		}
	}
}
