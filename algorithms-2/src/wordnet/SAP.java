package wordnet;
import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
//==============================================================================
// SAP
//==============================================================================
public class SAP
{
	private final Digraph _g;
	//------------------------------------------------------------------------------------
	public SAP(Digraph g)
	{
		_g = new Digraph(g);
	}
	//------------------------------------------------------------------------------------
	private static class Ancestor
	{
		public final int ancestor;
		public final int length;

		private Ancestor(int ancestor, int length)
		{
			this.ancestor = ancestor;
			this.length = length;
		}
	}
	//------------------------------------------------------------------------------------
	private Ancestor walk(Iterable<Integer> multi_v, Iterable<Integer> multi_w)
	{
		WordNetFastBFS bfs_v = new WordNetFastBFS(_g, multi_v);
		WordNetFastBFS bfs_w = new WordNetFastBFS(_g, multi_w);

		int length = -1;
		int ancestor = -1;

		for (int node : bfs_v.reachable())
		{
			if (bfs_w.hasPathTo(node))
			{
				int cur_length = bfs_v.distTo(node) + bfs_w.distTo(node);

				if (length <= -1 || cur_length < length)
				{
					ancestor = node;
					length = cur_length;
				}
			}
		}

		return new Ancestor(ancestor, length);
	}
	//------------------------------------------------------------------------------------
	private static ArrayList<Integer> init_array(int v) {
		ArrayList<Integer> result = new ArrayList<>();
		result.add(v);
		return result;
	}
	//------------------------------------------------------------------------------------
	public int length(int v, int w)
	{
		if (v == w) return 0;
		return walk(init_array(v), init_array(w)).length;
	}
	//------------------------------------------------------------------------------------
	public int ancestor(int v, int w)
	{
		return walk(init_array(v), init_array(w)).ancestor;
	}
	//------------------------------------------------------------------------------------
	public int length(Iterable<Integer> v, Iterable<Integer> w)
	{
		return walk(v, w).length;
	}
	//------------------------------------------------------------------------------------
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
	{
		return walk(v, w).ancestor;
	}
	//------------------------------------------------------------------------------------
	private static void test_length(SAP sap, int v, int w, int expected)
	{
		int length = sap.length(v, w);
		if (length != expected) {
			throw new Error("Length doesn't match; expected: " + expected + ", actual: " + length);
		}
	}
	//------------------------------------------------------------------------------------
	private static void test_loop(String[] args)
	{
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);

		while (!StdIn.isEmpty())
		{
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}
	}
	//------------------------------------------------------------------------------------
	private static void unit_test()
	{
		In in = new In("./data/wordnet/digraph4.txt");
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		test_length(sap, 2, 0, 6);
	}
	//------------------------------------------------------------------------------------
	private static void test_on_graph_A()
	{
		Digraph d = new Digraph(9);
		d.addEdge(0, 1);
		d.addEdge(1, 2);
		d.addEdge(2, 5);
		d.addEdge(1, 3);
		d.addEdge(3, 4);
		d.addEdge(4, 5);
		d.addEdge(5, 6);
		d.addEdge(7, 6);
		d.addEdge(6, 8);

		SAP sap = new SAP(d);
		test_length(sap, 0, 7, 5);
	}
	//------------------------------------------------------------------------------------
	public static void main(String[] args)
	{
		if (args[0].equals("unit-test")) {
			unit_test();
			test_on_graph_A();
		}
		else {
			test_loop(args);
		}
	}
}
