
# coding: utf-8

# In[1]:

import numpy as np
#-----------------------------------------------------------------------------------------
class RNN:
    def __init__(self):
        self.vocabulary = ['e', 'h', 'l', 'o']
        self.vocab_size = 4
        
        self.W_x_to_hidden = None
        self.W_hidden_to_hidden = None
        self.W_hidden_to_y = None

        self.bias_hidden = None
        self.bias_output = None

        self.h = np.array([[0.]])
    #-----------------------------------------------------------------------------------------
    def clone(self):
        ret = RNN()
        ret.vocabulary = self.vocabulary
        ret.vocab_size = self.vocab_size
        
        ret.W_x_to_hidden = self.W_x_to_hidden
        ret.W_hidden_to_hidden = self.W_hidden_to_hidden
        ret.W_hidden_to_y = self.W_hidden_to_y
    
        ret.bias_hidden = self.bias_hidden
        ret.bias_output = self.bias_output
    
        self.h = np.array([[0.]])
        return ret
    #-----------------------------------------------------------------------------------------
    def reset(self):
        self.h = np.array([[0.]])
        return self.h[0][0]
    #-----------------------------------------------------------------------------------------
    def load_weights(self):
        self.W_x_to_hidden = np.array([[ 3.6, -4.8,  0.35, -0.26]])
        self.W_hidden_to_hidden = np.array([[ 4.1]])
        self.W_hidden_to_y = np.array([[-12.], [ -0.67], [ -0.85], [ 13.]])
        self.bias_hidden = np.array([[ 0.41]])
        self.bias_output = np.array([[-0.20], [-2.9], [ 6.1], [-3.3]])

        self.h = np.array([[0.]])
    #-----------------------------------------------------------------------------------------
    def step(self, x_char):
        assert isinstance(x_char, str)
        x_index = self.vocabulary.index(x_char)
        x_one_hot = np.zeros((self.vocab_size, 1))
        x_one_hot[x_index] = 1

        cur_hidden_state = np.tanh(np.dot(self.W_x_to_hidden, x_one_hot) + np.dot(self.W_hidden_to_hidden, self.h) + self.bias_hidden)
        
        y_output = np.dot(self.W_hidden_to_y, cur_hidden_state) + self.bias_output

        y_prob_after_softmax = np.exp(y_output) / np.sum(np.exp(y_output))
        index = np.argmax(y_prob_after_softmax)
        y_char = self.vocabulary[index]
        
        self.h = cur_hidden_state  # important to not forget to update the hidden state
        
        return y_char, self.h[0][0]
    #-----------------------------------------------------------------------------------------
    def loss(self, x_train, y_train, h_prev):
        xs, hs, ps = {}, {}, {}
        hs[-1] = h_prev.copy()
        loss_value = 0
        cur_hidden_state = None

        # Forward propagation
        for t, x_char in enumerate(x_train):
            x_index = self.vocabulary.index(x_char)
            x_one_hot = np.zeros((self.vocab_size, 1))
            x_one_hot[x_index] = 1
        
            y_prob_target = np.zeros((self.vocab_size, 1))
            y_index = self.vocabulary.index(y_train[t])
            y_prob_target[y_index] = 1
        
            cur_hidden_state = np.tanh(np.dot(self.W_x_to_hidden, x_one_hot) + np.dot(self.W_hidden_to_hidden, hs[t - 1]) + self.bias_hidden)
        
            y_output = np.dot(self.W_hidden_to_y, cur_hidden_state) + self.bias_output
        
            y_prob_after_softmax = np.exp(y_output) / np.sum(np.exp(y_output))

            xs[t] = x_one_hot
            hs[t] = cur_hidden_state
            ps[t] = y_prob_after_softmax

            loss_value += -np.log(y_prob_after_softmax[y_index, 0])
        
        dWhy = np.zeros_like(self.W_hidden_to_y)
        dWxh = np.zeros_like(self.W_x_to_hidden)
        dWhh = np.zeros_like(self.W_hidden_to_hidden)
        
        dbh = np.zeros_like(self.bias_hidden)
        dby = np.zeros_like(self.bias_output)
        dh_next = np.zeros_like(cur_hidden_state)

        for t, x_char in reversed(list(enumerate(x_train))):
            x_index = self.vocabulary.index(x_char)
            x_one_hot = np.zeros((self.vocab_size, 1))
            x_one_hot[x_index] = 1
            
            y_prob_target = np.zeros((self.vocab_size, 1))
            y_index = self.vocabulary.index(y_train[t])
            y_prob_target[y_index] = 1
        
            y_prob_after_softmax = ps[t]
            cur_hidden_state = hs[t]

            dy = np.copy(y_prob_after_softmax)
            dy -= y_prob_target
        
            dWhy += np.dot(dy, cur_hidden_state.T)
            dby += dy
        
            dh = np.dot(self.W_hidden_to_y.T, dy) + dh_next
        
            # tanh derivative and backprop
            dh_raw = (1 - cur_hidden_state ** 2) * dh
        
            dbh += dh_raw
            dWxh += np.dot(dh_raw, x_one_hot.T)
            dWhh += np.dot(dh_raw, hs[t - 1].T)
        
            dh_next = np.dot(self.W_hidden_to_hidden.T, dh_raw)
        
        for dp in [dWxh, dWhh, dWhy, dbh, dby]:
            np.clip(dp, -5, 5, out=dp)  # mitigate exploding gradients
        
        return loss_value, dWxh, dWhh, dWhy, dbh, dby, cur_hidden_state
    #-----------------------------------------------------------------------------------------
    def print_weights(self):
        print('self.W_x_to_hidden', self.W_x_to_hidden)
        print('self.W_hidden_to_hidden', self.W_hidden_to_hidden)
        print('self.W_hidden_to_y', self.W_hidden_to_y)

        print('self.bias_hidden', self.bias_hidden)
        print('self.bias_output', self.bias_output)
    #-----------------------------------------------------------------------------------------
    def train(self, show=False):
        self.W_x_to_hidden = np.random.randn(1, 4)
        self.W_hidden_to_hidden = np.array([[0.]])
        self.W_hidden_to_y = np.random.randn(4, 1)

        self.bias_hidden = np.random.randn(1, 1)
        self.bias_output = np.random.randn(4, 1)

        text = list('hello')
        inputs = text[:len(text) - 1]
        targets = text[1:]
        smooth_loss = -np.log(1.0 / self.vocab_size)
        
        learning_rate = 0.01
        loss = 0
        
        for n in range(40000):
            h_prev = np.array([[0]])
            loss, dWxh, dWhh, dWhy, dbh, dby, hprev = self.loss(inputs, targets, h_prev)
            smooth_loss = smooth_loss * 0.999 + loss * 0.001
            
            if n % 10000 == 0: 
                print('iter {}, loss: {}'.format(n, smooth_loss))
            
            for param, dparam in zip([
                    self.W_x_to_hidden, 
                    self.W_hidden_to_hidden, 
                    self.W_hidden_to_y, 
                    self.bias_hidden, 
                    self.bias_output],
                                          [dWxh, dWhh, dWhy, dbh, dby]):
                param += -learning_rate * dparam 
        print('loss: {}'.format(loss))
        
        if show:
            self.print_weights()


# In[2]:

def preload():
    er = RNN()
    er.load_weights()
    er.print_weights()

    hidden_state = er.h[0][0]
    print(('h', hidden_state))
    er.reset()
    next_state = er.step('h'); print(next_state)
    next_state = er.step(next_state[0]); print(next_state)
    next_state = er.step(next_state[0]); print(next_state)
    next_state = er.step(next_state[0]); print(next_state)

def train():
    n = RNN()
    n.train(True)
    er = n
    er.reset()
    print(('h', er.h))
    next_state = er.step('h'); print(next_state)
    next_state = er.step(next_state[0]); print(next_state)
    next_state = er.step(next_state[0]); print(next_state)
    next_state = er.step(next_state[0]); print(next_state)

# train()
preload()