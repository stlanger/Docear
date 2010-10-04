package org.freeplane.plugin.script;

import java.util.ArrayList;
import java.util.HashSet;

/** A minimal implementation of a stack that may contain an element only once - not threadsafe.
 * The stack may contains null but note that null is used by {@link #pop()} to signal an empty stack. */
public class UniqueStack<T> {
	private ArrayList<T> stack = new ArrayList<T>(8);
	private HashSet<T> set = new HashSet<T>(8);

	/** creates an empty stack. */
	public UniqueStack() {
	}

	/** initializes the stack with a single element. */
	public UniqueStack(T t) {
		push(t);
	}

	/** returns true only if the element was actually added. */
	public boolean push(T t) {
		if (set.add(t)) {
			stack.add(t);
			return true;
		}
		return false;
	}

	/** returns the last element in the stack or null if it is empty. */
	public T pop() {
		return stack.isEmpty() ? null : stack.get(stack.size() - 1);
	}
	
	public T first() {
		return stack.isEmpty() ? null : stack.get(0);
	}
	
	public T last() {
		return stack.isEmpty() ? null : stack.get(stack.size() - 1);
	}

	public String toString() {
		return stack.toString();
	}
}