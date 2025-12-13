import os
import networkx as nx
import matplotlib.pyplot as plt
import concurrent.futures
import math

def Aiks(n):
    Aiks = []
    for i in range(1, n+1):
        for j in range(1, n+1):
            if i!=j:
                Aiks.append(["A", i, j])
    return Aiks

def Bijks(n):
    Bijks = []
    for i in range(1, n+1):
        for j in range(1, n+2):
            for k in range(1, n+1):
                if i!=k:
                    Bijks.append(["B", i, j, k])
    return Bijks

def Cijks(n):
    Cijks = []
    for i in range(1, n+1):
        for j in range(1, n+2):
            for k in range(1, n+1):
                if i!=k:
                    Cijks.append(["C", i, j, k])
    return Cijks

def A_toString(list):
    return f"{list[0]}_{list[1]}_{list[2]}"

def BC_toString(list):
    return f"{list[0]}_{list[1]}_{list[2]}_{list[3]}"

def create_word(n):
    word = []
    for i in range(1, n+1):
        for k in range(1, n+1):
            if i != k:
                word.append(A_toString(["A", i, k]))
                
                for j in range(1, n + 2):
                    word.append(BC_toString(["B", i, j, k]))
                    word.append(BC_toString(["C", i, j, k]))
    return word

def create_transactions(alphabet):
    transactions = {}
    for key in alphabet:
        if key[0] == 'A':
            i = key[1]
            k = key[2]
            transactions[A_toString(key)] = (f"m{k}{i}", set([f"m{k}{i}", f"M{k}{i}", f"M{i}{i}"]))
        elif key[0] == 'B':
            i = key[1]
            j = key[2]
            k = key[3]
            transactions[BC_toString(key)] = (f"n{k}{i}{j}", set([f"n{k}{i}{j}", f"M{i}{j}", f"m{k}{i}"]))
        else:
            i = key[1]
            j = key[2]
            k = key[3]
            transactions[BC_toString(key)] = (f"M{k}{j}", set([f"M{k}{j}", f"n{k}{i}{j}"]))
    return transactions

def create_D(alphabet, transaction):
    D = set()

    for i in range(len(alphabet)):
        a = A_toString(alphabet[i]) if alphabet[i][0]=='A' else BC_toString(alphabet[i])
        for j in range(i, len(alphabet)):
            b = A_toString(alphabet[j]) if alphabet[j][0]=='A' else BC_toString(alphabet[j])
            (x, x_set) = transaction[a]
            (y, y_set) = transaction[b]

            if x in y_set or y in x_set:
                D.add((a, b))
                D.add((b, a))

    return D

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

def make_graph(word, D):

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

def make_graph_optimized(word, transactions):
    graph = []
    last_writer = {}
    last_readers = {}

    for i in range(len(word)):
        node = Node(i, word[i])
        graph.append(node)

        write_var, all_vars = transactions[word[i]]
        read_vars = [v for v in all_vars if v != write_var]
        potential_parents = set()

        for r in read_vars:
            if r in last_writer:
                potential_parents.add(last_writer[r])
            if r not in last_readers:
                last_readers[r] = []
            last_readers[r].append(node)
        
        if write_var in last_writer:
            potential_parents.add(last_writer[write_var])
        
        if write_var in last_readers:
            for reader in last_readers[write_var]:
                potential_parents.add(reader)
            last_readers[write_var] = []

        for parent in potential_parents:
            if parent.no != node.no:
                parent.add_next(node)
        
        last_writer[write_var] = node
    
    return graph

def plot_graph(graph, tactic='layers'):

    G = nx.DiGraph()

    for node in graph:
        G.add_node(node.no, label=node.value, layer=node.layer)

    for node in graph:
        for nxt in node.next:
            G.add_edge(node.no, nxt.no)

    if tactic=='layers':
        pos = nx.multipartite_layout(G, subset_key="layer")

        for key, coords in pos.items():
            pos[key] = (coords[1], -coords[0])
    else:   
        pos = nx.nx_pydot.graphviz_layout(G, prog='dot')
    
    layers = [nx.get_node_attributes(G, 'layer')[node] for node in G.nodes()]

    plt.figure(figsize=(12,12))
    nx.draw(G, pos, with_labels=False, node_size=3000, node_color=layers, cmap=plt.cm.autumn, arrowsize=25)

    labels = {node.no: f"{node.value}\nLayer: {node.layer}" for node in graph}
    nx.draw_networkx_labels(G, pos, labels)
    plt.show()

def create_FNF(graph):
    no_layers = 0
    for node in graph:
        no_layers = max(no_layers, node.layer)

    FNF = [[] for _ in range(no_layers+1)]

    for node in graph:
        FNF[node.layer].append(node.value)

    return FNF

def load_matrix(file):

    current_dir = os.path.dirname(os.path.abspath(__file__))
    path = os.path.join(current_dir, file)

    if not os.path.exists(path):
        raise FileNotFoundError(f"Nie znaleziono pliku: {path}")
    
    with open(path, 'r') as f:
        lines = f.readlines()
    
    lines = [line.strip() for line in lines]

    if not lines:
        raise ValueError("Plik jest pusty!")
    
    N = int(lines[0])
    matrix = []
    for line in lines[1:-1]:
        row = [float(x) for x in line.split()]
        matrix.append(row)
    
    last_line = lines[-1].split()
    for i in range(N):
        matrix[i].append(float(last_line[i]))

    return matrix

class GaussJordanContext:
    def __init__(self, matrix):
        self.matrix = matrix
        self.N = len(matrix)
        self.m = {}
        self.n = {}
        
    def print_matrix(self):
        for row in self.matrix:
            print([f"{i}" for i in row])
        print()

    def final_normalization(self):
        for i in range(self.N):
            self.matrix[i][-1] /= self.matrix[i][i]
            self.matrix[i][i] = 1

    def run_task(self, task):
        parts = task.split("_")
        task_type = parts[0]

        if task_type == 'A':
            # liczenie mnożnika: A_i_k -> m_k_i = M_k_i / M_i_i

            i, k = int(parts[1]), int(parts[2])
            
            self.m[(k, i)] = self.matrix[k-1][i-1] / self.matrix[i-1][i-1]

        elif task_type == 'B':
            # wartość do odjęcia: B_i_j_k -> n_k_i_j = M_i_j * m_k_i

            i, j, k = int(parts[1]), int(parts[2]), int(parts[3])

            self.n[(k, i, j)] = self.matrix[i-1][j-1] * self.m[(k, i)]
        
        elif task_type == 'C':
            # odjęcie: C_i_j_K -> M_k_j = M_k_j - n_k_i_j
            i, j, k = int(parts[1]), int(parts[2]), int(parts[3])

            self.matrix[k-1][j-1] -= self.n[(k, i, j)]

            del self.n[(k, i, j)]

def run_foata_scheduler(FNF, context):
    print("START ALGORYTMU RÓWNOLEGŁEGO...\n")

    with concurrent.futures.ThreadPoolExecutor(max_workers=20) as executor:
        for i, layer in enumerate(FNF):
            print(f"---- WARSTWA {i} ({len(layer)} zadań) ----")
            futures = [executor.submit(context.run_task, task) for task in layer]

            concurrent.futures.wait(futures)

            context.print_matrix()
    
    print("KONIEC OBLICZANIA!\n")

    print("Normalizacja wyników...\n")
    context.final_normalization()

def program(input_matrix):

    n = len(input_matrix)
    word = create_word(n)
    alphabet = Aiks(n) + Bijks(n) + Cijks(n)
    transaction = create_transactions(alphabet)

    print("Budowanie grafu...")
    graph = make_graph_optimized(word, transaction)

    print("Koniec budowy grafu")

    # plot_graph(graph)                     # - można odkomentować dla niskich n

    FNF = create_FNF(graph)

    ctx = GaussJordanContext(input_matrix)

    print("Macierz wejściowa:")
    ctx.print_matrix()

    run_foata_scheduler(FNF, ctx)

    print("Macierz wynikowa: ")
    ctx.print_matrix()
    
    return ctx.matrix

def checker():
    in_file = "sprawdzarka/Matrices/unsolved.txt"
    out_file = "sprawdzarka/Matrices/solved.txt"

    matrix = program(load_matrix(in_file))
    check_matrix = load_matrix(out_file)

    print("Wymagana macierz: ")
    
    for row in check_matrix:
        print([f"{i}" for i in row])
    print()

    epsilon = 0.00001
    
    all_ok = True
    for i in range(len(matrix)):
        for j in range(len(matrix[i])):
            
            if not math.isclose(matrix[i][j], check_matrix[i][j], abs_tol=epsilon):
                print(f"Różnica w wierszu {i}, kolumnie {j}:")
                print(f"   Wynik: {matrix[i][j]}")
                print(f"   Wzorzec: {check_matrix[i][j]}")
                all_ok = False
    assert(all_ok)

checker()