package wordnet;
import edu.princeton.cs.algs4.*;
//==============================================================================
// Outcast
//==============================================================================
public class Outcast
{
	private WordNet _wordnet;
    //--------------------------------------------------------------------------
	public Outcast(WordNet wordnet)
	{
		_wordnet = wordnet;
	}
    //--------------------------------------------------------------------------
	public String outcast(String[] nouns)
	{
		int max_distance_sum = -1;
		String max_noun = null;

		for (int i = 0; i < nouns.length; i++)
		{
			String noun_a = nouns[i];
			int distance_sum = 0;
			for (int j = 0; j < nouns.length; j++)
			{
				if (i != j) {
					String noun_b = nouns[j];
					distance_sum += _wordnet.distance(noun_a, noun_b);
				}
			}

			if (distance_sum > max_distance_sum) {
				max_distance_sum = distance_sum;
				max_noun = noun_a;
			}
		}

		return max_noun;
	}
    //--------------------------------------------------------------------------
	public static void main(String[] args)
	{
		WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }
	}
}

