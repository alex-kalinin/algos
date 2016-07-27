# This is manually copied from https://gist.github.com/karpathy/d4dee566867f8291f086
# with minor style improvements

import numpy as np

# Globals initialized in main()
import sys

vocab_size, Wxh, Whh, Why, bh, by = None, None, None, None, None, None

#-----------------------------------------------------------------------------------------
def loss_function(inputs, targets, prev_hidden):
    global vocab_size, Wxh, Whh, Why, bh, by

    assert isinstance(inputs, list)
    assert isinstance(targets, list)
    assert isinstance(inputs[0], int)
    assert isinstance(targets[0], int)

    assert isinstance(prev_hidden, np.ndarray)

    xs, hs, ys, ps = {}, {}, {}, {}

    hs[-1] = np.copy(prev_hidden)
    loss = 0

    # forward pass
    for t in range(len(inputs)):
        xs[t] = np.zeros((vocab_size, 1))
        xs[t][inputs[t]] = 1
        hs[t] = np.tanh(np.dot(Wxh, xs[t]) + np.dot(Whh, hs[t - 1]) + bh)  # hidden state
        ys[t] = np.dot(Why, hs[t]) + by
        ps[t] = np.exp(ys[t]) / np.sum(np.exp(ys[t]))
        loss += -np.log(ps[t][targets[t], 0])

    # backward pass: compute gradients going backwards
    dWxh, dWhh, dWhy = np.zeros_like(Wxh), np.zeros_like(Whh), np.zeros_like(Why)
    dbh, dby = np.zeros_like(bh), np.zeros_like(by)
    dh_next = np.zeros_like(hs[0])

    for t in reversed(range(len(inputs))):
        dy = np.copy(ps[t])
        dy[targets[t]] -= 1  # backprop into y
        dWhy += np.dot(dy, hs[t].T)
        dby += dy
        dh = np.dot(Why.T, dy) + dh_next  # backprop into h
        dhraw = (1 - hs[t] * hs[t]) * dh  # backprop through tanh nonlinearity
        dbh += dhraw
        dWxh += np.dot(dhraw, xs[t].T)
        dWhh += np.dot(dhraw, hs[t - 1].T)
        dh_next = np.dot(Whh.T, dhraw)

    for dparam in [dWxh, dWhh, dWhy, dbh, dby]:
        np.clip(dparam, -5, 5, out=dparam)  # clip to mitigate exploding gradients

    return loss, dWxh, dWhh, dWhy, dbh, dby, hs[len(inputs) - 1]
#-----------------------------------------------------------------------------------------
def sample(h, seed_ix, n):
    global vocab_size, Wxh, Whh, Why
    """
    sample a sequence of integers from the model
    h is memory state, seed_ix is seed letter for first time step
    """
    x = np.zeros((vocab_size, 1))
    x[seed_ix] = 1
    ixes = []
    for t in range(n):
        h = np.tanh(np.dot(Wxh, x) + np.dot(Whh, h) + bh)
        y = np.dot(Why, h) + by
        p = np.exp(y) / np.sum(np.exp(y))
        ix = np.random.choice(range(vocab_size), p=p.ravel())
        x = np.zeros((vocab_size, 1))
        x[ix] = 1
        ixes.append(ix)
    return ixes
#-----------------------------------------------------------------------------------------
def main():
    global vocab_size, Wxh, Whh, bh, by, Why

    file_name = sys.argv[1]

    data = open(file_name, 'r').read()
    chars = list(set(data))
    data_size, vocab_size = len(data), len(chars)
    print('characters: {}, unique: {}'.format(data_size, vocab_size))
    char_to_index = { ch : i for i, ch in enumerate(chars) }
    index_to_char = { i : ch for i, ch in enumerate(chars) }

    hidden_size = 100   # size of hidden layer of neurons
    seq_length = 25     # number of steps to unroll the RNN for
    learning_rate = 1e-1

    # model parameters
    Wxh = np.random.randn(hidden_size, vocab_size) * 0.01   # input to hidden
    Whh = np.random.randn(hidden_size, hidden_size) * 0.01  # hidden to hidden
    Why = np.random.randn(vocab_size, hidden_size) * 0.01   # hidden to output
    bh = np.zeros((hidden_size, 1))     # hidden bias
    by = np.zeros((vocab_size, 1))      # output bias

    n, p = 0, 0
    mWxh, mWhh, mWhy = np.zeros_like(Wxh), np.zeros_like(Whh), np.zeros_like(Why)
    mbh, mby = np.zeros_like(bh), np.zeros_like(by)     # memory variables for Adagrad
    smooth_loss = -np.log(1.0 / vocab_size) * seq_length    # loss at iteration 0

    hprev = None

    while True:
        # prepare inputs (we're sweeping from left to right in steps seq_length long)
        if p + seq_length + 1 >= len(data) or n == 0:
            hprev = np.zeros((hidden_size, 1))  # reset RNN memory
            p = 0   # go from start of data
        inputs = [char_to_index[ch] for ch in data[p:p + seq_length]]
        targets = [char_to_index[ch] for ch in data[p + 1:p + seq_length + 1]]

        # sample from the model now and then
        if n % 100 == 0:
            sample_ix = sample(hprev, inputs[0], 200)
            txt = ''.join(index_to_char[ix] for ix in sample_ix)
            print('----\n %s \n----' % (txt, ))

        # forward seq_length characters through the net and fetch gradient
        loss, dWxh, dWhh, dWhy, dbh, dby, hprev = loss_function(inputs, targets, hprev)
        smooth_loss = smooth_loss * 0.999 + loss * 0.001
        if n % 100 == 0:
            print('iter %d, loss: %f' % (n, smooth_loss))   # print progress

        # perform parameter update with Adagrad
        for param, dparam, mem in zip([Wxh, Whh, Why, bh, by],
                                      [dWxh, dWhh, dWhy, dbh, dby],
                                      [mWxh, mWhh, mWhy, mbh, mby]):
            mem += dparam * dparam
            param += -learning_rate * dparam / np.sqrt(mem + 1e-8)  # adagrad update

        p += seq_length     # move data pointer
        n += 1              # iteration counter

#-----------------------------------------------------------------------------------------
if __name__ == '__main__':
    main()
