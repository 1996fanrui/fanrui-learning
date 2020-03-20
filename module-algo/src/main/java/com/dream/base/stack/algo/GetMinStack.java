package com.dream.base.stack.algo;

import java.util.Stack;

/**
 * @author fanrui
 * @time 2020-02-06 12:19:30
 * 实现一个特殊的栈，在实现栈的基本功能的基础上，再实现返 回栈中最小元素
 */
public class GetMinStack {

	/**
	 * 如果插入元素 <= min 栈的堆顶元素，则将当前元素插入到 min 栈，否则不插入数据到 min 栈
	 * 弹出数据时，如果 data 栈弹出的数据比 min 栈顶数据大，则min 栈不用动
	 */
	public static class MyStack1 {
		private Stack<Integer> stackData;
		private Stack<Integer> stackMin;

		public MyStack1() {
			this.stackData = new Stack<Integer>();
			this.stackMin = new Stack<Integer>();
		}

		public void push(int newNum) {
			this.stackData.push(newNum);
			if (this.stackMin.isEmpty()
					|| newNum <= stackMin.peek()) {
				this.stackMin.push(newNum);
			}
		}

		public int pop() {
			if (this.stackData.isEmpty()) {
				throw new RuntimeException("Your stack is empty.");
			}
			int value = this.stackData.pop();
			if (value == this.getmin()) {
				this.stackMin.pop();
			}
			return value;
		}


        public int top() {
            if(stackData.isEmpty()){
                return -1;
            }
            return stackData.peek();
        }

		public int getmin() {
			if (this.stackMin.isEmpty()) {
				throw new RuntimeException("Your stack is empty.");
			}
			return this.stackMin.peek();
		}
	}

	/**
	 *
	 */
	public static class MyStack2 {
		private Stack<Integer> stackData;
		private Stack<Integer> stackMin;

		public MyStack2() {
			this.stackData = new Stack<Integer>();
			this.stackMin = new Stack<Integer>();
		}

		/**
		 * min 栈插入元素的规则：
		 * 如果插入元素 <= min 栈的堆顶元素，则将当前元素插入到 min 栈
		 * 如果插入元素 > min 栈的堆顶元素，则将 min 栈的堆顶元素插入到 min 栈
		 * @param newNum
		 */
		public void push(int newNum) {
			if (this.stackMin.isEmpty()) {
				this.stackMin.push(newNum);
			} else if (newNum < this.getmin()) {
				this.stackMin.push(newNum);
			} else {
				int newMin = this.stackMin.peek();
				this.stackMin.push(newMin);
			}
			this.stackData.push(newNum);
		}

		/**
		 * data 栈弹出元素时，min 栈也要弹出，两个栈中元素个数永远保持一致
		 * @return
		 */
		public int pop() {
			if (this.stackData.isEmpty()) {
				throw new RuntimeException("Your stack is empty.");
			}
			this.stackMin.pop();
			return this.stackData.pop();
		}

		/**
		 * min 栈的栈顶永远存放着当前最小的元素，需要时，直接 get 即可
		 * @return
		 */
		public int getmin() {
			if (this.stackMin.isEmpty()) {
				throw new RuntimeException("Your stack is empty.");
			}
			return this.stackMin.peek();
		}
	}

	public static void main(String[] args) {
		MyStack1 stack1 = new MyStack1();
		stack1.push(3);
		System.out.println(stack1.getmin());
		stack1.push(4);
		System.out.println(stack1.getmin());
		stack1.push(1);
		System.out.println(stack1.getmin());
		System.out.println(stack1.pop());
		System.out.println(stack1.getmin());

		System.out.println("=============");

		MyStack1 stack2 = new MyStack1();
		stack2.push(3);
		System.out.println(stack2.getmin());
		stack2.push(4);
		System.out.println(stack2.getmin());
		stack2.push(1);
		System.out.println(stack2.getmin());
		System.out.println(stack2.pop());
		System.out.println(stack2.getmin());
	}

}
