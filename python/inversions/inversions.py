import math
#-----------------------------------------------------------------------------------------
def sort_and_count(arr, start, length):
    result = 0
    if length > 1:
        left_len = math.floor(length / 2)
        left = sort_and_count(arr, start, left_len)
        right = sort_and_count(arr, start + left_len, length - left_len)
        split = merge_and_count_split(arr, start, length, left_len)
        result = left + right + split
    return result
#-----------------------------------------------------------------------------------------
# The function assumes that arr is split into two sub-arrays, arr[start:start + left_len]
# and arr[start + left_len : start + length], each of which is sorted.
# The function merges these two arrays in place in O(N) time (and extra space proportional to N)
# and calculates the number of split inversions between two sub-arrays.
#
def merge_and_count_split(arr, start, length, left_len):
    merged = []
    inversions = 0

    left = start
    left_end = start + left_len
    right = left_end
    right_end = start + length

    for i in range(length):
        if right >= right_end \
                or left < left_end and arr[left] < arr[right]:
            merged.append(arr[left])
            left += 1
        else:
            assert right < right_end
            # The number of inversions is the number of remaining
            # elements in the left sub-array
            inversions += left_end - left
            merged.append(arr[right])
            right += 1

    assert len(merged) == length
    arr[start:start + length] = merged
    return inversions
#-----------------------------------------------------------------------------------------
def main():
    with open('IntegerArray.txt', 'rt') as f:
        ints = [int(v) for v in f]
    inversions = sort_and_count(ints, 0, len(ints))
    print(inversions)
#-----------------------------------------------------------------------------------------
if __name__ == '__main__':
    main()
