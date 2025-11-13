import os
import networkx as nx
import matplotlib.pyplot as plt

current_dir = os.path.dirname(os.path.abspath(__file__))
filename = os.path.join(current_dir, "data/case3.txt")

def read_file(filename):
    with open(filename, "r") as f:
        lines = f.readlines()

    transaction = {}
    alphabet = set()
    word = ""

    for line in lines:
        line = line.strip()

        if line.startswith("A ="):
            letters = line.split("=")[1].strip()[1:-1].split(",")
            for l in letters:
                alphabet.add(l.strip())

        elif line.startswith("w ="):
            word = line.split("=")[1].strip()

    transaction_keys = set()

    for line in lines:
        if line.startswith("("):
            key = line[1]
            if key in alphabet:
                x = line.split(")")[1].split(":=")[0].strip()
                transaction_keys.add(x)

    for line in lines:
        if line.startswith("("):
            key = line[1]
            if key in alphabet:
                expr = line.split(")")[1].split(":=")
                using = set()

                for ch in line.split(")")[1]:
                    if ch in transaction_keys:
                        using.add(ch)
                
                transaction[key] = (expr[0].split()[0], using)

    return alphabet, word, transaction

alphabet, word, transaction = read_file(filename)

# print("A = ", alphabet)
# print("w = ", word)
# print("transactions = ", transaction)

def create_I_D(alphabet, transaction):
    I = set()
    D = set()

    for a in alphabet:
        for b in alphabet:
            (x, x_set) = transaction[a]
            (y, y_set) = transaction[b]

            if x in y_set or y in x_set:
                D.add((a, b))
            else:
                I.add((a, b))

    return I, D

I, D = create_I_D(alphabet, transaction)

# print("I = ", I)
# print("D = ", D)


# ================== GRAPH ================== 

class Node:
    def __init__(self, no, value):
        self.no = no
        self.value = value
        self.next = set()
        self.layer = 0
    
    def is_connected(self, no):
        if self.no >= no: return self.no == no
    
        for node in self.next:
            if node.is_connected(no):
                return True
        
        return False
    
    def add_next(self, node):
        self.next.add(node)
        node.update_layer(self.layer + 1)

    def update_layer(self, n):
        if n > self.layer:
            self.layer = n
            for node in self.next:
                node.update_layer(n+1)

def make_graph(word):

    graph = []
    for i in range(len(word)):
        node = Node(i, word[i])
        graph.append(node)

    for d in range(1, len(word)):
        for i in range(len(word)-d):
            fst_node: Node = graph[i]
            snd_node: Node = graph[i+d]

            if (fst_node.value, snd_node.value) in D:
                if not fst_node.is_connected(snd_node.no):
                    fst_node.add_next(snd_node)
    
    return graph

graph = make_graph(word)

def plot_graph(graph):

    G = nx.DiGraph()

    for node in graph:
        G.add_node(node.no, label=node.value)

    for node in graph:
        for nxt in node.next:
            G.add_edge(node.no, nxt.no)

    pos = nx.nx_pydot.graphviz_layout(G, prog='dot')

    plt.figure(figsize=(8,5))
    nx.draw(G, pos, with_labels=False, node_size=1000, node_color='lightblue')

    labels = {node.no: f"{node.value}\nLayer: {node.layer}" for node in graph}
    nx.draw_networkx_labels(G, pos, labels)
    plt.show()

plot_graph(graph)

# ================== FNF ==================

def printFNF(graph, word):
    no_layers = 0
    for node in graph:
        no_layers = max(no_layers, node.layer)

    FNF = [[] for _ in range(no_layers+1)]

    for node in graph:
        FNF[node.layer].append(node.value)

    print(f"FNF([{word}]) = ", end="")
    for layer in FNF:
        print("(", end="")
        for a in layer:
            print(a, end="")
        print(")", end="")
    print()

printFNF(graph, word)