package com.cdo.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K,V>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3406101835051432861L;
	private static final float   DEFAULT_LOAD_FACTOR = 0.8f; //HashMap  Default The load factor
	
    protected final int _maxEntries;
    public LRUCache(int maxEntries){
    	this(16,maxEntries);
    }
    public LRUCache(int initialEntries, int maxEntries)
    {
        super(initialEntries, DEFAULT_LOAD_FACTOR, true);
        _maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest)
    {
        return size() > _maxEntries;
    }

}
