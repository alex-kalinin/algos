import edu.princeton.cs.algs4.*;
import java.util.HashSet;
//==============================================================================
// SAP
//==============================================================================
public class SAP
{
	private final Graph   _dualG;
	private final Digraph _reverseG;
//	private BreadthFirstDirectedPaths _ancestorBfs;
	//------------------------------------------------------------------------------------
	public SAP(Digraph g)
	{
		_dualG = new Graph(g.V());

		for (int v = 0; v < g.V(); v++) {
			for (int w : g.adj(v)) {
				_dualG.addEdge(v, w);
			}
		}

		_reverseG = g.reverse();
//		_ancestorBfs = new BreadthFirstDirectedPaths(g.reverse(), 0);
	}
	//------------------------------------------------------------------------------------
	public int length(int v, int w)
	{
		if (v >= _dualG.V() || w >= _dualG.V())
			return -1;

		BreadthFirstPaths bfs = new BreadthFirstPaths(_dualG, v);
		return bfs.pathTo(w) != null
				? bfs.distTo(w)
				: -1;
	}
	//------------------------------------------------------------------------------------
	public int ancestor(int v, int w)
	{
		if (v >= _dualG.V() || w >= _dualG.V())
			return -1;

		if (v == w) return v;

		BreadthFirstPaths bfs = new BreadthFirstPaths(_dualG, v);
		return get_ancestor(v, w, bfs);
	}
	//------------------------------------------------------------------------------------
	private HashSet<Integer> create_path(int v, BreadthFirstDirectedPaths ancestors_bfs) {
		Iterable<Integer> root_to_v = ancestors_bfs.pathTo(v);
		HashSet<Integer> v_root = new HashSet<>();

		for (int i : root_to_v) {
			v_root.add(i);
		}
		return v_root;
	}
	//------------------------------------------------------------------------------------
	private int get_ancestor(int v, int w, BreadthFirstPaths bfs)
	{
		if (v >= _dualG.V() || w >= _dualG.V())
			return -1;

		if (v == w) return v;

		int root_id = get_root(v);
		BreadthFirstDirectedPaths ancestors_bfs = new BreadthFirstDirectedPaths(_reverseG, root_id);
		Iterable<Integer> path = bfs.pathTo(w);

		int ancestor = -1;
		if (path != null)
		{
			HashSet<Integer> v_root = create_path(v, ancestors_bfs);
			HashSet<Integer> w_root = create_path(w, ancestors_bfs);

			for (int path_step : path) {
				if (v_root.contains(path_step) && w_root.contains(path_step)) {
					ancestor = path_step;
					break;
				}
			}
		}

		return ancestor;
	}
	private int get_root(int v)
	{
		return 0;
	}
	//------------------------------------------------------------------------------------
	private class MinVertex
	{
		public final int w;
		public final Iterable<Integer> min_path;
		public final int length;
		//--------------------------------------------------------------------------------
		public MinVertex(int min_length, int min_w, Iterable<Integer> min_path)
		{
			length = min_length;
			w = min_w;
			this.min_path = min_path;
		}
		//--------------------------------------------------------------------------------
		public int other()
		{
			int result = -1;
			for (int i : min_path) {
				result = i;
			}
			return result;
		}
	}
	//------------------------------------------------------------------------------------
	private MinVertex get_min_path(BreadthFirstPaths bfs, Iterable<Integer> w)
	{
		int min_length = -1;
		int min_w = - 1;
		Iterable<Integer> min_path = null;

		for (int iw : w) {
			int len = bfs.distTo(iw);
			if (min_length <= -1 || len < min_length) {
				min_length = len;
				min_w = iw;
				min_path = bfs.pathTo(iw);
			}
		}

		return new MinVertex(min_length, min_w, min_path);
	}
	//------------------------------------------------------------------------------------
	public int length(Iterable<Integer> v, Iterable<Integer> w)
	{
		BreadthFirstPaths bfs = new BreadthFirstPaths(_dualG, v);
		return get_min_path(bfs, w).length;
	}
	//------------------------------------------------------------------------------------
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
	{
		BreadthFirstPaths bfs = new BreadthFirstPaths(_dualG, v);
		MinVertex minVertex = get_min_path(bfs, w);
		int w_  = minVertex.w;
		int v_  = minVertex.other();
		return get_ancestor(v_, w_, bfs);
	}
	//------------------------------------------------------------------------------------
	public static void main(String[] args)
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
}
