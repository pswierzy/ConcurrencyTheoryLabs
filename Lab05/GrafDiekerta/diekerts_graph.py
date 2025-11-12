import os

current_dir = os.path.dirname(os.path.abspath(__file__))
filename = os.path.join(current_dir, "case3.txt")


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



# print("A = ", alphabet)
# print("w = ", word)
# print("transactions = ", transaction)


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

print("I = ", I)
print("D = ", D)


class Node:
    def __init__(self, no, value):
        self.no = no
        self.value = value
        self.next = set()
    
    def is_connected(self, no):
        if self.no >= no: return self.no == no
    
        for node in self.next:
            if node.is_connected(no):
                return True
        
        return False
    
    def add_next(self, node):
        self.next.add(node)

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

import networkx as nx
import matplotlib.pyplot as plt

G = nx.DiGraph()

# wierzchołki
for node in graph:
    G.add_node(node.no, label=node.value)

# krawędzie
for node in graph:
    for nxt in node.next:
        G.add_edge(node.no, nxt.no)

# rysowanie
pos = nx.spring_layout(G)
nx.draw(G, pos)
labels = {n: graph[n].value for n in range(len(graph))}
nx.draw_networkx_labels(G, pos, labels)
plt.show()