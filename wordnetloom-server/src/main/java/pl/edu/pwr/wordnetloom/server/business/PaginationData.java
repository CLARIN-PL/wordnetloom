package pl.edu.pwr.wordnetloom.server.business;

public class PaginationData {

	private final int start;
	private final int limit;

	public PaginationData(final int start, final int limit){
		this.start = start;
		this.limit = limit;
	}

	public int getStart() {
		return start;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		return "PaginationData{" +
				"start=" + start +
				", limit=" + limit +
				'}';
	}
}