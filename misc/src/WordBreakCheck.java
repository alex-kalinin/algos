import com.google.common.collect.Sets;
import java.util.Set;
//==============================================================================
// WordBreakCheck
//==============================================================================
public class WordBreakCheck
{
    private static Set<String> dict = Sets.newHashSet(
            "mobile","samsung","sam","sung","man","mango",
            "icecream","and","go","i","like","ice","cream");
    //--------------------------------------------------------------------------
    private static boolean execute(String input) {
        // wb[i] True if input[0:i) can be segmented into words.
        boolean[] wb = new boolean[input.length() + 1];
        boolean result = false;

        for (int prefix_size = 1; prefix_size <= input.length(); prefix_size++) {
            String candidate = input.substring(0, prefix_size);

            // If wb[i] is false, but the dict contains the candidate, which
            // is input[0:i), then set it to true.
            if (!wb[prefix_size] && dict.contains(candidate))
                wb[prefix_size] = true;

            // If wb[i] is true, check the remainder of the input, otherwise
            // proceed to the next prefix.
            if (wb[prefix_size]) {
                // Exit condition
                if (prefix_size >= input.length()) {
                    result = true;
                    break;
                }
                else {
                    result = check_remainder(input, wb, prefix_size);
                }
            }
        }

        return result;
    }
    //--------------------------------------------------------------------------
    private static boolean check_remainder(String input, boolean[] wb, int prefix_size) {
        boolean result = false;

        for (int end_index = prefix_size + 1; end_index <= input.length(); end_index++) {
            String remainder_candidate = input.substring(prefix_size, end_index);

            // Now the tricky part:
            // if wb[i] is true, AND the remainder_candidate is in the set, then
            // we can set wb[end_index] to true as well. That's because the entire
            // string from [0 to end_index) is comprised of two parts:
            // [0, i) - which CAN be broken into words since wb[i] is true, and
            // [i, end) - which we just proved a word in our set.
            // Therefore, the entire string [0, end) can be broken into words as well,
            // so we can set wb[end_index]  to true.

            if (!wb[end_index] && dict.contains(remainder_candidate))
                wb[end_index] = true;

            if (end_index >= input.length() && wb[end_index]) {
                result = true;
                break;
            }
        }

        return result;
    }
    //--------------------------------------------------------------------------
    private static void print(boolean result) {
        System.out.println(result ? "Yes!" : "Nope");
    }
    //--------------------------------------------------------------------------
    public static void main(String[] args) {
        print(execute("ilikesamsung"));
        print(execute("iiiiiiii"));
        print(execute(""));
        print(execute("ilikelikeimangoiii"));
        print(execute("samsungandmango"));
        print(execute("samsungandmangok"));
    }
}
