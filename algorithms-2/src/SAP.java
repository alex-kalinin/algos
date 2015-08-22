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
		Ancestor result = new Ancestor(-1, -1);

		// Build a two-direction graph to find the path v->w.
		Graph dual = get_graph(_g);
		BreadthFirstPaths bfs_dual_v = new BreadthFirstPaths(dual, multi_v);

		for (int w : multi_w)
		{
			Iterable<Integer> path_v_w = bfs_dual_v.pathTo(w);
			print_path(path_v_w);
			if (path_v_w != null)
			{
				// Get the starting vertex that "won" the shortest path to w.
				int v = path_v_w.iterator().next();

				// Build paths from both vertices.
				BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(_g, v);
				BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(_g, w);

				// Find the element on the path that is also on the both
				// v_path_to_root and v_path_to_root.
				// That's the potential shortest path ancestor.
				// Continue for all elements on the path v->w. Pick the shortest length.
				int length = -1;
				int ancestor = -1;

				for (int parent : path_v_w)
				{
					if (bfs_v.hasPathTo(parent) && bfs_w.hasPathTo(parent))
					{
						int cur_length = bfs_v.distTo(parent) + bfs_w.distTo(parent);
						if (length <= -1 || cur_length < length)
						{
							ancestor = parent;
							length = cur_length;
						}
					}
				}

				if (result.length <= -1 || result.length > length) {
					result = new Ancestor(ancestor, length);
				}
			}
		}

		return result;
	}
	//------------------------------------------------------------------------------------
	private static void print_path(Iterable<Integer> path_v_w)
	{
		for (int n : path_v_w) {
			System.out.print(n + " ");
		}
		System.out.println();
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
	private Graph get_graph(Digraph g)
	{
		Graph dualG = new Graph(g.V());

		for (int v = 0; v < g.V(); v++)
		{
			for (int w : g.adj(v))
			{
				dualG.addEdge(v, w);
			}
		}

		return dualG;
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
//		test_length(sap, 7, 8, 1);
//		test_length(sap, 7, 0, 4);
//		test_length(sap, 0, 9, 3);
	}
	//------------------------------------------------------------------------------------
	public static void main(String[] args)
	{
		if (args[0].equals("unit-test")) {
			unit_test();
		}
		else {
			test_loop(args);
		}
	}
}
