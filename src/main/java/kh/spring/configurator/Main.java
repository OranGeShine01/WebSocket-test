package kh.spring.configurator;

import com.google.common.collect.EvictingQueue;

public class Main {
	public static void main(String[] args) {
		
		EvictingQueue<String> queue = EvictingQueue.create(3);
		queue.add("Apple");
		queue.add("OranGe");
		queue.add("Mango");
		queue.add("Grape");
		System.out.println(queue);
	}
}
