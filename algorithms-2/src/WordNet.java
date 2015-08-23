import edu.princeton.cs.algs4.*;
import java.util.*;

//----------------------------------------------------------------------------------------
@SuppressWarnings("Convert2streamapi")
public class WordNet
{
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
    //------------------------------------------------------------------------------------
    private Digraph _dag;
    private HashMap<Integer, Synset> _synsets = new HashMap<>();
    private HashMap<String, List<Synset>> _nouns_map = new HashMap<>();
    //------------------------------------------------------------------------------------
	public WordNet(String synsets, String hypernyms)
	{
        In in_synsets = new In(synsets);
        String[] lines = in_synsets.readAllLines();
        _dag = new Digraph(lines.length);
        in_synsets.close();

        for (String line : lines)
        {
            // 232,Aegates_Isles Aegadean_Isles,islands west of Sicily (now known as the Egadi Islands) where ...
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            Synset synset = new Synset(id, parts[1]);
            _synsets.put(id, synset);
            synset.add_nouns_to_map(_nouns_map);
        }

        parse_hypernyms(_dag, hypernyms);

        print("Vertices: " + _dag.V() + "; edges: " + _dag.E());
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
        return _nouns_map.containsKey(word);
    }
    //------------------------------------------------------------------------------------
    public int distance(String nounA, String nounB)
    {
        ArrayList<Integer> a_synsets = get_synsets(nounA);
        ArrayList<Integer> b_synsets = get_synsets(nounB);
        return new SAP(_dag).length(a_synsets, b_synsets);
    }
    //------------------------------------------------------------------------------------
    private ArrayList<Integer> get_synsets(String noun)
    {
        List<Synset> synsets = _nouns_map.get(noun);
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
        int ancestor_v = new SAP(_dag).ancestor(a_synsets, b_synsets);
        return _synsets.get(ancestor_v).synset_string();
    }
    //------------------------------------------------------------------------------------
    public static void main(String[] args)
	{
        Stopwatch sw = new Stopwatch();
        WordNet w = new WordNet(to_path("synsets.txt"), to_path("hypernyms.txt"));
        print("Done in " + sw.elapsedTime() + " s");
        print(w.sap("tow_car", "jean"));
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
