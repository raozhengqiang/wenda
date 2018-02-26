package com.rao;

public class ThreadTest {
	public static void test() {
		for (int i = 0; i < 10; i++) {
			new MyThread(i).start();;
		}
	}

	public static void main(String[] argv) {
		test();
	}
}

class MyThread extends Thread{
	
	private int tid;
	
	public MyThread(int tid) {
		this.tid=tid;
	}
	@Override
	public void run() {
		try {
			for (int i = 0; i <10; i++) {
				Thread.sleep(1000);
				System.out.println(String.format("%d:%d",tid,i));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}