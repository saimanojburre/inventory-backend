package com.inventory.system.item.dto;

import java.util.List;

public class BulkItemResponse {

	private int saved;
	private List<String> duplicates;

	public int getSaved() {
		return saved;
	}

	public void setSaved(int saved) {
		this.saved = saved;
	}

	public List<String> getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(List<String> duplicates) {
		this.duplicates = duplicates;
	}
}