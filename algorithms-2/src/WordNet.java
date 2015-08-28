import edu.princeton.cs.algs4.*;
import java.util.*;
//----------------------------------------------------------------------------------------
public class WordNet
{
    private final SAP _sap;
    private static class Synset
    {
        private final String _nouns;
        private int _id;
        //--------------------------------------------------------------------------------
        public Synset(int id, String nouns)
        {
            _id = id;
            _nouns = nouns;
        }
        //--------------------------------------------------------------------------------
        public void add_nouns_to_map(HashMap<String, List<Synset>> nouns_map)
        {
            for (String n : nouns()) {
                if (!nouns_map.containsKey(n)) {
                    nouns_map.put(n, new ArrayList<>());
                }
                List<Synset> prev = nouns_map.get(n);
                prev.add(this);
            }
        }
        //--------------------------------------------------------------------------------
        public String synset_string()
        {
            return _nouns;
        }
        //--------------------------------------------------------------------------------
        public String[] nouns()
        {
            return _nouns.split(" ");
        }
    }
    private HashMap<Integer, Synset> _synsets = new HashMap<>();
    private HashMap<String, List<Synset>> _nouns_map = new HashMap<>();
    //------------------------------------------------------------------------------------
	public WordNet(String synsets, String hypernyms)
	{
        In in_synsets = new In(synsets);
        String[] lines = in_synsets.readAllLines();
        in_synsets.close();

        Digraph dag = new Digraph(lines.length);

        for (String line : lines)
        {
            // 232,Aegates_Isles Aegadean_Isles,islands west of Sicily (now known as the Egadi Islands) where ...
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            Synset synset = new Synset(id, parts[1]);
            _synsets.put(id, synset);
            synset.add_nouns_to_map(_nouns_map);
        }

        parse_hypernyms(dag, hypernyms);

        if (!is_rooted_dag(dag)) {
            throw new IllegalArgumentException("Hypernyms is not a rooted DAG");
        }

        print("Vertices: " + dag.V() + "; edges: " + dag.E());
        print("Nouns: " + _nouns_map.size());

        _sap = new SAP(dag);
	}
    //------------------------------------------------------------------------------------
    private static boolean is_rooted_dag(Digraph dag)
    {
        //check for cycles
        edu.princeton.cs.algs4.DirectedCycle cycle = new edu.princeton.cs.algs4.DirectedCycle(dag);

        boolean rooted = !cycle.hasCycle();

        if (rooted)
        {
            int root = find_root(dag);

            Digraph reverseDag = dag.reverse();
            DepthFirstDirectedPaths dfs = new DepthFirstDirectedPaths(reverseDag, root);
            for (int node = 0; node < reverseDag.V(); node++)
            {
                if (!dfs.hasPathTo(node))
                {
                    rooted = false;
                    break;
                }
            }
        }

        return rooted;
    }
    //------------------------------------------------------------------------------------
    private static int find_root(Digraph dag)
    {
        int v = 0;
        Iterator<Integer> it;
        while ((it = dag.adj(v).iterator()).hasNext())
        {
            v = it.next();
        }
        return v;
    }
    //------------------------------------------------------------------------------------
    private static void parse_hypernyms(Digraph dag, String hypernyms)
    {
        In in = new In(hypernyms);
        while (in.hasNextLine())
        {
            String line = in.readLine();
            String[] parts = line.split(",");
            int synset_id = Integer.parseInt(parts[0]);

            for (int i = 1; i < parts.length; i++)
            {
                int hyper_id = Integer.parseInt(parts[i]);
                dag.addEdge(synset_id, hyper_id);
            }
        }
        in.close();
    }
    //------------------------------------------------------------------------------------
    public Iterable<String> nouns()
    {
        return _nouns_map.keySet();
    }
    //------------------------------------------------------------------------------------
    public boolean isNoun(String word)
    {
        if (word == null) throw new NullPointerException();

        return _nouns_map.containsKey(word);
    }
    //------------------------------------------------------------------------------------
    public int distance(String nounA, String nounB)
    {
        ArrayList<Integer> a_synsets = get_synsets(nounA);
        ArrayList<Integer> b_synsets = get_synsets(nounB);
        return _sap.length(a_synsets, b_synsets);
    }
    //------------------------------------------------------------------------------------
    private ArrayList<Integer> get_synsets(String noun)
    {
        if (noun == null)
            throw new NullPointerException();

        List<Synset> synsets = _nouns_map.get(noun);

        if (synsets == null)
            throw new IllegalArgumentException();

        ArrayList<Integer> result = new ArrayList<>();
        for (Synset s : synsets) {
            result.add(s._id);
        }
        return result;
    }
    //------------------------------------------------------------------------------------
    public String sap(String nounA, String nounB)
    {
        ArrayList<Integer> a_synsets = get_synsets(nounA);
        ArrayList<Integer> b_synsets = get_synsets(nounB);

        int ancestor_v = _sap.ancestor(a_synsets, b_synsets);
        return _synsets.get(ancestor_v).synset_string();
    }
    //------------------------------------------------------------------------------------
    public static void main(String[] args)
	{
//        Stopwatch sw = new Stopwatch();
//        WordNet w = new WordNet(to_path("synsets3.txt"), to_path("hypernyms3InvalidCycle.txt"));
//        print("Done in " + sw.elapsedTime() + " s");
//        print(w.sap("tow_car", "jean"));
        test_performance(args);
    }
    //------------------------------------------------------------------------------------
    private static void test_performance(String[] args)
    {
        Stopwatch sw = new Stopwatch();
        WordNet w = new WordNet(args[0], args[1]);
        print("Done in " + sw.elapsedTime() + " s");

        StdOut.println("Press ENTER to continue...");
        StdIn.readLine();
        sw = new Stopwatch();

        for (int i = 0; i < 1000; i++)
        {
            w.sap("tow_car", "jean");
        }

        print("Done in " + sw.elapsedTime() + " s");
    }
    //------------------------------------------------------------------------------------
    private static void print_path(Iterable<Integer> path)
    {
        for (int v : path)
        {
            System.out.print(" " + v);
        }
        System.out.println();
    }
    //------------------------------------------------------------------------------------
    private static String to_path(String name)
    {
        return "data/wordnet/" + name;
    }
    //------------------------------------------------------------------------------------
    private static void print(String msg)
    {
        System.out.println(msg);
    }
}
