package com.alexkalinin.basic;
//==============================================================================
// LinkedList
//==============================================================================
public class LinkedList<T>
{
	private static class LinkedNode<T>
	{
		public LinkedNode<T> forward;
		public LinkedNode<T> back;
		T value;
	}
	//------------------------------------------------------------------------------------
	private LinkedNode<T> _header;
	//------------------------------------------------------------------------------------
	public LinkedList()
	{
		_header = new LinkedNode<>();
		_header.forward = _header.back = _header;
	}
	//------------------------------------------------------------------------------------
	public boolean is_empty()
	{
		return _header.forward == _header;
	}
	//------------------------------------------------------------------------------------
	public void add_tail(T val)
	{
		LinkedNode<T> node = new LinkedNode<>();
		node.value = val;

		LinkedNode<T> prev_last_node = _header.back;
		prev_last_node.forward = node;

		node.back = prev_last_node;
		node.forward = _header;

		_header.back = node;
	}
	//------------------------------------------------------------------------------------
	public void add_head(T val)
	{
		LinkedNode<T> node = new LinkedNode<>();
		node.value = val;

		LinkedNode<T> prev_first_node = _header.forward;
		prev_first_node.back = node;

		node.forward = prev_first_node;
		node.back = _header;

		_header.forward = node;
	}
	//------------------------------------------------------------------------------------
	public T remove_head()
	{
		if (is_empty()) return null;

		LinkedNode<T> head = _header.forward;

		LinkedNode<T> new_head = head.forward;
		_header.forward = new_head;
		new_head.back = _header;

		return head.value;
	}
	//------------------------------------------------------------------------------------
	public T remove_tail()
	{
		if (is_empty()) return null;

		LinkedNode<T> tail = _header.back;

		LinkedNode<T> new_tail = tail.back;
		_header.back = new_tail;
		new_tail.forward = _header;

		return tail.value;
	}
	//------------------------------------------------------------------------------------
	public static void main(String[] args)
	{
		LinkedList<String> list = new LinkedList<>();
		assert list.is_empty();

		list.add_head("A");
		assert !list.is_empty();

		list.add_tail("B");
		list.add_tail("C");
		list.add_head("1");
		assert !list.is_empty();

		test_remove_tail(list, "C");
		test_remove_tail(list, "B");

		test_remove_head(list, "1");

		assert !list.is_empty();

		test_remove_head(list, "A");

		assert list.is_empty();

		list.remove_head();
		list.remove_head();
		String s = list.remove_head();
		assert s == null;

		list.add_head("A");
		assert !list.is_empty();
		test_remove_head(list, "A");
	}
	//------------------------------------------------------------------------------------
	private static void test_remove_tail(LinkedList<String> list, String expected)
	{
		String s = list.remove_tail();
		assert s.equals(expected);
	}
	//------------------------------------------------------------------------------------
	private static void test_remove_head(LinkedList<String> list, String expected)
	{
		String s = list.remove_head();
		assert s.equals(expected);
	}
}
