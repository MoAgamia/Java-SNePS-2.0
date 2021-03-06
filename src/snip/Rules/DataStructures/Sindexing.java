package snip.Rules.DataStructures;

import java.util.TreeMap;

public class Sindexing {
	TreeMap<Integer, RuleUseInfoSet> map;

	/**
	 * Create new Empty Sindexing table
	 */
	public Sindexing() {
		map = new TreeMap<Integer, RuleUseInfoSet>();
	}

	/**
	 * Insert the rule use info rui in this table in the index x, if there was a
	 * rule use info in the index x then it should be replaced by the merge of
	 * rui and the existed one
	 * 
	 * @param x
	 *            int
	 * @param rui
	 *            RuleUseInfo
	 * @return the final RuleUseInfo
	 */
	private RuleUseInfoSet insertInIndex(int x, RuleUseInfo rui) {
		// TODO
		return null;
	}

	/**
	 * Get the index of a RuleUseInfo in the table by the id's of its
	 * substitutions
	 * 
	 * @param x
	 *            int[]
	 * @return int
	 */
	private int getIndex(int[] x) {
		int p = 16777619;
		int hash = (int) 2166136261L;
		for (int i = 0; i < x.length; ++i) {
			hash += (hash ^ x[i]) * p;
		}
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		return hash;
	}

	/**
	 * Insert rui in the table
	 * 
	 * @param rui
	 *            RuleUseInfo
	 * @param ids
	 *            int []
	 * @return the final RuleUseInfo
	 */
	public RuleUseInfoSet insert(RuleUseInfo rui, int[] ids) {
		int index = getIndex(ids);
		return insertInIndex(index, rui);
	}
}