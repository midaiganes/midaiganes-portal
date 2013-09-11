package ee.midaiganes.util;

public class Pair<A, B> {
	private final A frs;
	private final B scnd;

	public Pair(A frs, B scnd) {
		this.frs = frs;
		this.scnd = scnd;
	}

	public A getFirst() {
		return frs;
	}

	public B getSecond() {
		return scnd;
	}
}
