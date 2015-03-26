package com.qiao.bean;

/**
 * 相册
 * @author qiao
 * 2015-3-18
 */
public class Bucket {
	public int bucketId;
	public String bucketName;
	public String bucketUrl = null;
	public int count;

	public Bucket(int id, String name, String url) {
		bucketId = id;
		bucketName = ensureNotNull(name);
		bucketUrl = url;
		count = 0;
	}

	@Override
	public int hashCode() {
		return bucketId;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Bucket)) return false;
		Bucket entry = (Bucket) object;
		return bucketId == entry.bucketId;
	}

	public String ensureNotNull(String value) {
		return value == null ? "" : value;
	}
}

