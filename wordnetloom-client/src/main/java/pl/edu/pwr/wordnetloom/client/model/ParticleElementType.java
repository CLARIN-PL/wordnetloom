package pl.edu.pwr.wordnetloom.client.model;

public enum ParticleElementType {
	prefix("prefixes"), interfix("interfixes"), suffix("suffixes"), root(""), constituent("");

	private String dictionary;

	ParticleElementType(String dictionary) {
		this.dictionary = dictionary;
	}

	public String getDictionary() {
		return dictionary;
	}
}