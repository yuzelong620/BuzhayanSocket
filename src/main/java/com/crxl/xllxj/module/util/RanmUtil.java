package com.crxl.xllxj.module.util;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RanmUtil<E> {
	ArrayList<RandomItem<E>> list = new ArrayList<>();
	int sumOdds;

	public void add(int weight, E e) {
		sumOdds += weight;
		list.add(new RandomItem<E>(sumOdds, e));
	}

	public E random() {
		int random = ThreadLocalRandom.current().nextInt(sumOdds);
		for (RandomItem<E> item : list) {
			if (item.getOdds() > random) {
				return item.getValue();
			}
		}
		return null;// 永远都不会执行的代码
	}
}
